package com.arta.copperpistons.block;

import com.arta.copperpistons.CopperPistonsMod;
import com.arta.copperpistons.block.blocks.CopperPistonBlock;
import com.arta.copperpistons.block.blocks.CopperPistonHeadBlock;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class ModBlocks {

    public static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(CopperPistonsMod.MOD_ID, name));
    }

    public static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(CopperPistonsMod.MOD_ID, name));
    }

    public static final ResourceKey<Block> COPPER_PISTON_KEY = blockKey("copper_piston");
    public static final Block COPPER_PISTON = register(
            COPPER_PISTON_KEY,
            itemKey("copper_piston"),
            props -> new CopperPistonBlock(false, props),
            BlockBehaviour.Properties.ofFullCopy(Blocks.PISTON)
                    .mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.COPPER)
    );

    public static final ResourceKey<Block> HONEY_COPPER_PISTON_KEY = blockKey("honey_copper_piston");
    public static final Block HONEY_COPPER_PISTON = register(
            HONEY_COPPER_PISTON_KEY,
            itemKey("honey_copper_piston"),
            props -> new CopperPistonBlock(true, props),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STICKY_PISTON)
                    .mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.COPPER)
    );

    public static final ResourceKey<Block> COPPER_PISTON_HEAD_KEY = blockKey("copper_piston_head");
    public static final Block COPPER_PISTON_HEAD = Registry.register(
            BuiltInRegistries.BLOCK,
            COPPER_PISTON_HEAD_KEY,
            new CopperPistonHeadBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.PISTON_HEAD)
                            .setId(COPPER_PISTON_HEAD_KEY)
            )
    );

    private static Block register(
            ResourceKey<Block> blockKey,
            ResourceKey<Item> itemKey,
            Function<BlockBehaviour.Properties, Block> blockFactory,
            BlockBehaviour.Properties properties
    ) {
        Block block = blockFactory.apply(properties.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

        return block;
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS)
                .register(creativeTab -> {
                    creativeTab.accept(COPPER_PISTON.asItem());
                    creativeTab.accept(HONEY_COPPER_PISTON.asItem());
                });
    }
}