package com.arta.copperpistons;

import com.arta.copperpistons.block.ModBlocks;
import net.fabricmc.api.ModInitializer;

public class CopperPistonsMod implements ModInitializer {
    public static final String MOD_ID = "copperpistons";

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
    }
}