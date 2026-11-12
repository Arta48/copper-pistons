package com.arta.copperpistons.client;

import com.arta.copperpistons.block.blocks.CopperPistonBlock;
import com.arta.copperpistons.block.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.client.renderer.blockentity.state.PistonHeadRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CopperPistonHeadRenderer extends PistonHeadRenderer {

    public CopperPistonHeadRenderer() {
        super();
    }

    @Override
    public void extractRenderState(
            final @NonNull PistonMovingBlockEntity blockEntity,
            final @NonNull PistonHeadRenderState state,
            final float partialTicks,
            final @NonNull Vec3 cameraPosition,
            final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        BlockState blockState = blockEntity.getMovedState();
        boolean isCopperHead = blockState.is(ModBlocks.COPPER_PISTON_HEAD);
        boolean isCopperBase = blockState.getBlock() instanceof CopperPistonBlock;

        if (blockEntity.getLevel() instanceof ClientLevel level && !blockState.isAir()) {
            BlockPos pos = blockEntity.getBlockPos().relative(blockEntity.getMovementDirection().getOpposite());
            Holder<Biome> biome = level.getBiome(pos);

            if (isCopperHead && blockEntity.getProgress(partialTicks) <= 4.0F) {
                blockState = blockState.setValue(PistonHeadBlock.SHORT, blockEntity.getProgress(partialTicks) <= 0.5F);
                state.block = PistonHeadRenderer.createMovingBlock(pos, blockState, biome, level);

            } else if (isCopperBase && blockEntity.isSourcePiston() && !blockEntity.isExtending()) {
                PistonType type = blockState.is(ModBlocks.HONEY_COPPER_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
                BlockState pistonHeadState = ModBlocks.COPPER_PISTON_HEAD.defaultBlockState()
                        .setValue(PistonHeadBlock.TYPE, type)
                        .setValue(PistonHeadBlock.FACING, blockState.getValue(PistonBaseBlock.FACING));

                pistonHeadState = pistonHeadState.setValue(PistonHeadBlock.SHORT, blockEntity.getProgress(partialTicks) >= 0.5F);

                state.block = PistonHeadRenderer.createMovingBlock(pos, pistonHeadState, biome, level);
                BlockPos basePos = pos.relative(blockEntity.getMovementDirection());
                BlockState extendedBase = blockState.setValue(PistonBaseBlock.EXTENDED, true);
                state.base = PistonHeadRenderer.createMovingBlock(basePos, extendedBase, biome, level);
            }
        }
    }
}