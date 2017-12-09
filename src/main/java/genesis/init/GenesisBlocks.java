/*
 * This file is part of Genesis Mod, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017 Boethie
 * Copyright (c) 2017 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package genesis.init;

import genesis.GenesisMod;
import genesis.block.*;
import genesis.combo.variant.EnumFern;
import genesis.combo.variant.EnumOre;
import genesis.combo.variant.EnumRock;
import genesis.combo.variant.EnumTree;
import genesis.item.ItemGenesisLeaves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Mod.EventBusSubscriber(modid = GenesisMod.MOD_ID)
@GameRegistry.ObjectHolder(GenesisMod.MOD_ID)
public class GenesisBlocks {

    // humus
    public static final Block HUMUS = null;
    public static final Block HUMUS_FARMLAND = null;
    public static final Block HUMUS_PATH = null;

    // ferns
    public static final Block DRYOPTERIS = null;
    public static final Block PHLEBOPTERIS = null;
    public static final Block TODITES = null;

    // leaves
    public static final Block ARAUCARIOXYLON_LEAVES = null;
    public static final Block DRYOPHYLLUM_LEAVES = null;
    public static final Block FICUS_LEAVES = null;
    public static final Block GINKGO_LEAVES = null;
    public static final Block METASEQUOIA_LEAVES = null;

    // logs
    public static final Block ARAUCARIOXYLON_LOG = null;
    public static final Block DRYOPHYLLUM_LOG = null;
    public static final Block FICUS_LOG = null;
    public static final Block GINKGO_LOG = null;
    public static final Block METASEQUOIA_LOG = null;

    // saplings
    public static final Block ARAUCARIOXYLON_SAPLING = null;
    public static final Block DRYOPHYLLUM_SAPLING = null;
    public static final Block FICUS_SAPLING = null;
    public static final Block GINKGO_SAPLING = null;
    public static final Block METASEQUOIA_SAPLING = null;

    // rocks
    public static final Block GRANITE = null;
    public static final Block MOSSY_GRANITE = null;
    public static final Block KOMATIITE = null;
    public static final Block ORTHOGNEISS = null;
    public static final Block LIMESTONE = null;
    public static final Block PEGMATITE = null;
    public static final Block CARBONADO = null;

    // ores
    public static final Block ZIRCON_ORE = null;
    public static final Block GARNET_ORE = null;
    public static final Block MALACHITE_ORE = null;
    public static final Block PYRITE_ORE = null;

    // silt
    public static final Block SILT = null;
    public static final Block RED_SILT = null;
    public static final Block CRACKED_SILT = null;
    public static final Block RED_CRACKED_SILT = null;
    public static final Block SILTSTONE = null;
    public static final Block RED_SILTSTONE = null;

    // other
    public static final Block OOZE = null;

    private static final Set<Block> BLOCKS = new LinkedHashSet<>();
    private static final Set<Item> ITEMS = new LinkedHashSet<>();

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();

        BLOCKS.clear();

        // humus
        registerBlock(registry, new BlockHumus(), "humus");
        registerBlock(registry, new BlockHumusFarmland(), "humus_farmland");
        registerBlock(registry, new BlockHumusPath(), "humus_path");

        // ferns
        for (final EnumFern fernType : EnumFern.values()) {
            final String fernName = fernType.toString().toLowerCase(Locale.ENGLISH);
            registerBlock(registry, new BlockGenesisFern(), fernName);
        }

        // leaves, logs, saplings
        for (final EnumTree treeType : EnumTree.values()) {
            final String treeName = treeType.toString().toLowerCase(Locale.ENGLISH);
            registerBlock(registry, new BlockGenesisLeaves(treeType), treeName + "_leaves");
            registerBlock(registry, new BlockGenesisLog(), treeName + "_log");
            registerBlock(registry, new BlockGenesisSapling(treeType), treeName + "_sapling");
        }

        // rocks
        for (final EnumRock rockType : EnumRock.values()) {
            final String rockName = rockType.toString().toLowerCase(Locale.ENGLISH);
            registerBlock(registry, new BlockGenesisRock(rockType), rockName);
        }

        // ores
        for (final EnumOre oreType : EnumOre.values()) {
            final String oreName = oreType.toString().toLowerCase(Locale.ENGLISH);
            registerBlock(registry, new BlockGenesisOre(oreType), oreName + "_ore");
        }

        // silt
        registerBlock(registry, new BlockSilt(MapColor.SAND), "silt");
        registerBlock(registry, new BlockSilt(MapColor.ADOBE), "red_silt");
        registerBlock(registry, new BlockCrackedSilt(MapColor.SAND), "cracked_silt");
        registerBlock(registry, new BlockCrackedSilt(MapColor.ADOBE), "red_cracked_silt");
        registerBlock(registry, new BlockSiltstone(MapColor.SAND), "siltstone");
        registerBlock(registry, new BlockSiltstone(MapColor.ADOBE), "red_siltstone");

        // other
        registerBlock(registry, new BlockOoze(), "ooze");
    }

    private static void registerBlock(final IForgeRegistry<Block> registry, final Block block, final String name) {
        block.setRegistryName(GenesisMod.MOD_ID, name);
        block.setUnlocalizedName(GenesisMod.MOD_ID + "." + name);
        registry.register(block);
        BLOCKS.add(block);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();

        ITEMS.clear();

        for (final Block block : BLOCKS) {
            final ItemBlock item;
            if (block instanceof BlockGenesisLeaves) {
                item = new ItemGenesisLeaves((BlockLeaves) block);
            } else {
                item = new ItemBlock(block);
            }
            item.setRegistryName(block.getRegistryName());
            registry.register(item);
            ITEMS.add(item);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(final ModelRegistryEvent event) {
        for (final Item item : ITEMS) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
        for (final Block block : BLOCKS) {
            if (block instanceof BlockLeaves) {
                ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE).build());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerColorHandlers(final BlockColors blockColors, final ItemColors itemColors) {
        final IBlockColor grassColor = (state, world, pos, tintIndex) -> {
            if (world != null && pos != null) {
                return BiomeColorHelper.getGrassColorAtPos(world, pos);
            } else {
                return ColorizerGrass.getGrassColor(0.5D, 1.0D);
            }
        };

        final IBlockColor foliageColor = (state, world, pos, tintIndex) -> {
            if (world != null && pos != null) {
                return BiomeColorHelper.getFoliageColorAtPos(world, pos);
            } else {
                return ColorizerFoliage.getFoliageColorBasic();
            }
        };

        final IItemColor itemBlockColor = (stack, tintIndex) -> {
            final IBlockState state = Block.getBlockFromItem(stack.getItem()).getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(state, null, null, tintIndex);
        };

        for (final Block block : BLOCKS) {
            final IBlockColor blockColor;

            if (block instanceof BlockGenesisFern) {
                blockColor = grassColor;
            } else if (block instanceof BlockLeaves) {
                blockColor = foliageColor;
            } else {
                blockColor = null;
            }

            if (blockColor != null) {
                blockColors.registerBlockColorHandler(blockColor, block);
                itemColors.registerItemColorHandler(itemBlockColor, block);
            }
        }
    }
}