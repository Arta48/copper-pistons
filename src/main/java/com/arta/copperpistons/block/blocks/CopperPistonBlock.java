package com.arta.copperpistons.block.blocks;

import com.arta.copperpistons.block.ModBlocks;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class CopperPistonBlock extends PistonBaseBlock {

    public CopperPistonBlock(boolean isSticky, BlockBehaviour.Properties properties) {
        super(isSticky, properties);
    }

    @Override
    public boolean getNeighborSignal(@NonNull SignalGetter level, @NonNull BlockPos pos, @NonNull Direction pushDirection) {
        for (Direction direction : Direction.values()) {
            if (direction != pushDirection && level.hasSignal(pos.relative(direction), direction)) {
                return true;
            }
        }
        return level.hasSignal(pos, Direction.DOWN);
    }

    @Override
    public boolean moveBlocks(@NonNull Level level, BlockPos pistonPos, @NonNull Direction direction, boolean extending) {
        BlockPos armPos = pistonPos.relative(direction);

        if (!extending && level.getBlockState(armPos).is(ModBlocks.COPPER_PISTON_HEAD)) {
            level.setBlock(armPos, Blocks.AIR.defaultBlockState(), 276);
        }

        PistonStructureResolver resolver = new PistonStructureResolver(level, pistonPos, direction, extending);
        if (!resolver.resolve()) {
            return false;
        } else {
            Map<BlockPos, BlockState> deleteAfterMove = Maps.newHashMap();
            List<BlockPos> toPush = resolver.getToPush();
            List<BlockState> toPushShapes = Lists.newArrayList();

            for (BlockPos pos : toPush) {
                BlockState state = level.getBlockState(pos);
                toPushShapes.add(state);
                deleteAfterMove.put(pos, state);
            }

            List<BlockPos> toDestroy = resolver.getToDestroy();
            BlockState[] toUpdate = new BlockState[toPush.size() + toDestroy.size()];
            Direction pushDirection = extending ? direction : direction.getOpposite();
            int updateIndex = 0;

            for (int i = toDestroy.size() - 1; i >= 0; --i) {
                BlockPos pos = toDestroy.get(i);
                BlockState state = level.getBlockState(pos);
                BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
                dropResources(state, level, pos, blockEntity);
                if (!state.is(BlockTags.FIRE) && level.isClientSide()) {
                    level.levelEvent(2001, pos, getId(state));
                }

                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 18);
                level.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(state));
                toUpdate[updateIndex++] = state;
            }

            for (int i = toPush.size() - 1; i >= 0; --i) {
                BlockPos pos = toPush.get(i);
                BlockState blockState = level.getBlockState(pos);
                pos = pos.relative(pushDirection);
                deleteAfterMove.remove(pos);
                BlockState actualState = Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, direction);
                level.setBlock(pos, actualState, 324);
                level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(pos, actualState, toPushShapes.get(i), direction, extending, false));
                toUpdate[updateIndex++] = blockState;
            }

            if (extending) {
                PistonType type = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
                BlockState headState = ModBlocks.COPPER_PISTON_HEAD.defaultBlockState()
                        .setValue(PistonHeadBlock.FACING, direction)
                        .setValue(PistonHeadBlock.TYPE, type);

                BlockState movingPistonState = Blocks.MOVING_PISTON.defaultBlockState()
                        .setValue(MovingPistonBlock.FACING, direction)
                        .setValue(MovingPistonBlock.TYPE, type);

                deleteAfterMove.remove(armPos);
                level.setBlock(armPos, movingPistonState, 324);
                level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(armPos, movingPistonState, headState, direction, true, true));
            }

            BlockState air = Blocks.AIR.defaultBlockState();
            for (BlockPos pos : deleteAfterMove.keySet()) {
                level.setBlock(pos, air, 82);
            }

            for (Map.Entry<BlockPos, BlockState> entry : deleteAfterMove.entrySet()) {
                BlockPos pos = entry.getKey();
                BlockState oldState = entry.getValue();
                oldState.updateIndirectNeighbourShapes(level, pos, 2);
                air.updateNeighbourShapes(level, pos, 2);
                air.updateIndirectNeighbourShapes(level, pos, 2);
            }

            Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, resolver.getPushDirection(), null);
            updateIndex = 0;

            for (int i = toDestroy.size() - 1; i >= 0; --i) {
                BlockState state = toUpdate[updateIndex++];
                BlockPos pos = toDestroy.get(i);
                if (level instanceof ServerLevel serverLevel) {
                    state.affectNeighborsAfterRemoval(serverLevel, pos, false);
                }
                state.updateIndirectNeighbourShapes(level, pos, 2);
                level.updateNeighborsAt(pos, state.getBlock(), orientation);
            }

            for (int i = toPush.size() - 1; i >= 0; --i) {
                level.updateNeighborsAt(toPush.get(i), toUpdate[updateIndex++].getBlock(), orientation);
            }

            if (extending) {
                level.updateNeighborsAt(armPos, ModBlocks.COPPER_PISTON_HEAD, orientation);
            }

            return true;
        }
    }
}