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
import genesis.block.BlockGenesisFern;
import genesis.block.BlockGenesisLeaves;
import genesis.block.BlockGenesisLog;
import genesis.block.BlockGenesisSapling;
import genesis.combo.variant.EnumFern;
import genesis.combo.variant.EnumTree;
import genesis.item.ItemGenesisLeaves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
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
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.LinkedHashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = GenesisMod.MOD_ID)
@GameRegistry.ObjectHolder(GenesisMod.MOD_ID)
public class GenesisBlocks {

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

    private static final Set<Block> BLOCKS = new LinkedHashSet<>();
    private static final Set<Item> ITEMS = new LinkedHashSet<>();

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();

        if (registerIfFilled(registry, BLOCKS)) {
            return;
        }

        // ferns
        registerBlock(registry, new BlockGenesisFern(EnumFern.DRYOPTERIS), "dryopteris");
        registerBlock(registry, new BlockGenesisFern(EnumFern.PHLEBOPTERIS), "phlebopteris");
        registerBlock(registry, new BlockGenesisFern(EnumFern.TODITES), "todites");

        // leaves
        registerBlock(registry, new BlockGenesisLeaves(EnumTree.ARAUCARIOXYLON), "araucarioxylon_leaves");
        registerBlock(registry, new BlockGenesisLeaves(EnumTree.DRYOPHYLLUM), "dryophyllum_leaves");
        registerBlock(registry, new BlockGenesisLeaves(EnumTree.FICUS), "ficus_leaves");
        registerBlock(registry, new BlockGenesisLeaves(EnumTree.GINKGO), "ginkgo_leaves");
        registerBlock(registry, new BlockGenesisLeaves(EnumTree.METASEQUOIA), "metasequoia_leaves");

        // logs
        registerBlock(registry, new BlockGenesisLog(), "araucarioxylon_log");
        registerBlock(registry, new BlockGenesisLog(), "dryophyllum_log");
        registerBlock(registry, new BlockGenesisLog(), "ficus_log");
        registerBlock(registry, new BlockGenesisLog(), "ginkgo_log");
        registerBlock(registry, new BlockGenesisLog(), "metasequoia_log");

        // saplings
        registerBlock(registry, new BlockGenesisSapling(EnumTree.ARAUCARIOXYLON), "araucarioxylon_sapling");
        registerBlock(registry, new BlockGenesisSapling(EnumTree.DRYOPHYLLUM), "dryophyllum_sapling");
        registerBlock(registry, new BlockGenesisSapling(EnumTree.FICUS), "ficus_sapling");
        registerBlock(registry, new BlockGenesisSapling(EnumTree.GINKGO), "ginkgo_sapling");
        registerBlock(registry, new BlockGenesisSapling(EnumTree.METASEQUOIA), "metasequoia_sapling");
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();

        if (registerIfFilled(registry, ITEMS)) {
            return;
        }

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
            IBlockState state = Block.getBlockFromItem(stack.getItem()).getStateFromMeta(stack.getMetadata());
            return blockColors.colorMultiplier(state, null, null, tintIndex);
        };

        for (final Block block : BLOCKS) {
            IBlockColor blockColor = null;

            if (block instanceof BlockGenesisFern) {
                blockColor = grassColor;
            } else if (block instanceof BlockLeaves) {
                blockColor = foliageColor;
            }

            if (blockColor != null) {
                blockColors.registerBlockColorHandler(blockColor, block);
                itemColors.registerItemColorHandler(itemBlockColor, block);
            }
        }
    }

    private static void registerBlock(final IForgeRegistry<Block> registry, final Block block, final String name) {
        block.setRegistryName(GenesisMod.MOD_ID, name);
        block.setUnlocalizedName(GenesisMod.MOD_ID + "." + name);
        registry.register(block);
        BLOCKS.add(block);
    }

    private static <T extends IForgeRegistryEntry<T>> boolean registerIfFilled(final IForgeRegistry<T> registry, final Set<T> entries) {
        if (!entries.isEmpty()) {
            for (final T entry : entries) {
                registry.register(entry);
            }
            return true;
        }
        return false;
    }
}