package com.arta.copperpistons.block.blocks;

import com.arta.copperpistons.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

public class CopperPistonHeadBlock extends PistonHeadBlock {

    public CopperPistonHeadBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFittingBase(BlockState armState, BlockState potentialBase) {
        Block expectedBase = armState.getValue(TYPE) == PistonType.DEFAULT
                ? ModBlocks.COPPER_PISTON
                : ModBlocks.HONEY_COPPER_PISTON;

        return potentialBase.is(expectedBase)
                && potentialBase.getValue(PistonBaseBlock.EXTENDED)
                && potentialBase.getValue(FACING) == armState.getValue(FACING);
    }
}