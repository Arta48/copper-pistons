package com.arta.copperpistons;

import com.arta.copperpistons.client.CopperPistonHeadRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

public class CopperPistonsClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(BlockEntityTypes.PISTON, context -> new CopperPistonHeadRenderer());
    }
}