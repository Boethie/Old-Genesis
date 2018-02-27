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
package genesis.init

import genesis.GenesisMod
import genesis.block.*
import genesis.combo.variant.*
import genesis.item.ItemGenesisLeaves
import net.minecraft.block.Block
import net.minecraft.block.BlockLeaves
import net.minecraft.block.material.MapColor
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.client.renderer.color.BlockColors
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.client.renderer.color.ItemColors
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraft.world.ColorizerFoliage
import net.minecraft.world.ColorizerGrass
import net.minecraft.world.biome.BiomeColorHelper
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import java.util.*

@Mod.EventBusSubscriber(modid = GenesisMod.MOD_ID)
object GenesisBlocks {

    // humus
    val HUMUS: Block by name("humus")
    val HUMUS_FARMLAND: Block by name("humus_farmland")
    val HUMUS_PATH: Block by name("humus_path")

    // plants
    val SANMIGUELIA: Block by name("sanmiguelia")
    val PALAEOASTER: Block by name("palaeoaster")
    val AQUILAPOLLENITES: Block by name("aquilapollenites")
    val ZINGIBEROPSIS: Block by name("zingiberopsis")

    // ferns
    val GLEICHENIIDITES: Block by name("gleicheniidites")
    val PHLEBOPTERIS: Block by name("phlebopteris")
    val TODITES: Block by name("todites")
    val ZYGOPTERIS: Block by name("zygopteris")

    // leaves
    val ARAUCARIOXYLON_LEAVES: Block by name("araucarioxylon_leaves")
    val DRYOPHYLLUM_LEAVES: Block by name("dryophyllum_leaves")
    val FICUS_LEAVES: Block by name("ficus_leaves")
    val GINKGO_LEAVES: Block by name("ginkgo_leaves")
    val METASEQUOIA_LEAVES: Block by name("metasequoia_leaves")

    // logs
    val ARAUCARIOXYLON_LOG: Block by name("araucarioxylon_log")
    val DRYOPHYLLUM_LOG: Block by name("dryophyllum_log")
    val FICUS_LOG: Block by name("ficus_log")
    val GINKGO_LOG: Block by name("ginkgo_log")
    val METASEQUOIA_LOG: Block by name("metasequoia_log")

    // saplings
    val ARAUCARIOXYLON_SAPLING: Block by name("araucarioxylon_sapling")
    val DRYOPHYLLUM_SAPLING: Block by name("dryophyllum_sapling")
    val FICUS_SAPLING: Block by name("ficus_sapling")
    val GINKGO_SAPLING: Block by name("ginkgo_sapling")
    val METASEQUOIA_SAPLING: Block by name("metasequoia_sapling")

    // fences
    val ARAUCARIOXYLON_WATTLE_FENCE: Block by name("araucarioxylon_wattle_fence")
    val DRYOPHYLLUM_WATTLE_FENCE: Block by name("dryophyllum_wattle_fence")
    val FICUS_WATTLE_FENCE: Block by name("ficus_wattle_fence")
    val GINKGO_WATTLE_FENCE: Block by name("ginkgo_wattle_fence")
    val METASEQUOIA_WATTLE_FENCE: Block by name("metasequoia_wattle_fence")

    // rocks
    val GRANITE: Block by name("granite")
    val MOSSY_GRANITE: Block by name("mossy_granite")
    val KOMATIITE: Block by name("komatiite")
    val ORTHOGNEISS: Block by name("orthogneiss")
    val PEGMATITE: Block by name("pegmatite")
    val CARBONADO: Block by name("carbonado")

    // ores
    val ZIRCON_ORE: Block by name("zircon_ore")
    val GARNET_ORE: Block by name("garnet_ore")
    val SIDERITE_ORE: Block by name("siderite_ore")
    val TOURMALINE_ORE: Block by name("tourmaline_ore")
    val AQUAMARINE_ORE: Block by name("aquamarine_ore")
    val PYRITE_ORE: Block by name("pyrite_ore")
    val AZURITE_ORE: Block by name("azurite_ore")

    // silt
    val SILT: Block by name("silt")
    val RED_SILT: Block by name("red_silt")
    val CRACKED_SILT: Block by name("cracked_silt")
    val RED_CRACKED_SILT: Block by name("red_cracked_silt")
    val SILTSTONE: Block by name("siltstone")
    val RED_SILTSTONE: Block by name("red_siltstone")

    // other
    val OOZE: Block by name("ooze")
    val RED_CLAY: Block by name("red_clay")
    val CAMPFIRE: Block by name("campfire")
    val STORAGE_BOX: Block by name("storage_box")

    private val BLOCKS = LinkedHashSet<Block>()
    private val ITEMS = LinkedHashSet<Item>()

    @SubscribeEvent
    @JvmStatic fun registerBlocks(event: RegistryEvent.Register<Block>) {
        val registry = event.registry

        BLOCKS.clear()

        // humus
        registerBlock(registry, BlockHumus(), "humus")
        registerBlock(registry, BlockHumusFarmland(), "humus_farmland")
        registerBlock(registry, BlockHumusPath(), "humus_path")

        // plants
        registerBlock(registry, BlockGenesisFlower(), "sanmiguelia")
        registerBlock(registry, BlockGenesisFlower(), "palaeoaster")
        registerBlock(registry, BlockGenesisFlower(), "aquilapollenites")

        for (crop in EnumCrop.values()) if (crop.isDoubleCrop) registerBlock(registry, BlockDoubleCrop(crop), crop.cropName)
        //TODO: Add class for single crops when we need to and register them here

        // ferns
        for (fernType in EnumFern.values()) {
            val fernName = fernType.toString().toLowerCase(Locale.ENGLISH)
            registerBlock(registry, BlockGenesisFern(), fernName)
        }

        // leaves, logs, saplings
        for (treeType in EnumTree.values()) {
            val treeName = treeType.toString().toLowerCase(Locale.ENGLISH)
            registerBlock(registry, BlockGenesisLeaves(treeType), treeName + "_leaves")
            registerBlock(registry, BlockGenesisLog(), treeName + "_log")
            registerBlock(registry, BlockGenesisSapling(treeType), treeName + "_sapling")
            registerBlock(registry, BlockWattleFence(), treeName + "_wattle_fence")
        }

        // rocks
        for (rockType in EnumRock.values()) {
            val rockName = rockType.toString().toLowerCase(Locale.ENGLISH)
            registerBlock(registry, BlockGenesisRock(rockType), rockName)
        }

        // ores
        for (oreType in EnumOre.values()) {
            val oreName = oreType.toString().toLowerCase(Locale.ENGLISH)
            registerBlock(registry, BlockGenesisOre(oreType), oreName + "_ore")
        }

        // silt
        registerBlock(registry, BlockSilt(MapColor.SAND), "silt")
        registerBlock(registry, BlockSilt(MapColor.ADOBE), "red_silt")
        registerBlock(registry, BlockCrackedSilt(MapColor.SAND), "cracked_silt")
        registerBlock(registry, BlockCrackedSilt(MapColor.ADOBE), "red_cracked_silt")
        registerBlock(registry, BlockSiltstone(MapColor.SAND), "siltstone")
        registerBlock(registry, BlockSiltstone(MapColor.ADOBE), "red_siltstone")

        // other
        registerBlock(registry, BlockOoze(), "ooze")
        registerBlock(registry, BlockRedClay(), "red_clay")

        registerBlock(registry, BlockCampfire(), "campfire")
        registerBlock(registry, BlockStorageBox(), "storage_box")
    }

    private fun registerBlock(registry: IForgeRegistry<Block>, block: Block, name: String) {
        block.setRegistryName(GenesisMod.MOD_ID, name)
        block.unlocalizedName = GenesisMod.MOD_ID + "." + name
        registry.register(block)
        BLOCKS.add(block)
    }

    @SubscribeEvent
    @JvmStatic fun registerItems(event: RegistryEvent.Register<Item>) {
        val registry = event.registry

        ITEMS.clear()

        for (block in BLOCKS) {
            val item: ItemBlock
            if (block is BlockGenesisLeaves) {
                item = ItemGenesisLeaves(block as BlockLeaves)
            } else if (block is BlockDoubleCrop) {
                continue
            } else {
                item = ItemBlock(block)
            }
            item.registryName = block.registryName!!
            registry.register(item)
            ITEMS.add(item)
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @JvmStatic fun registerModels(event: ModelRegistryEvent) {
        for (item in ITEMS) {
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName!!, "inventory"))
        }
        for (block in BLOCKS) {
            if (block is BlockLeaves) {
                ModelLoader.setCustomStateMapper(block, StateMap.Builder().ignore(BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE).build())
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @JvmStatic fun registerColorHandlers(blockColors: BlockColors, itemColors: ItemColors) {
        val grassColor = IBlockColor { _, world, pos, _ ->
            if (world != null && pos != null) {
                BiomeColorHelper.getGrassColorAtPos(world, pos)
            } else {
                ColorizerGrass.getGrassColor(0.5, 1.0)
            }
        }

        val foliageColor = IBlockColor { _, world, pos, _ ->
            if (world != null && pos != null) {
                BiomeColorHelper.getFoliageColorAtPos(world, pos)
            } else {
                ColorizerFoliage.getFoliageColorBasic()
            }
        }

        val itemBlockColor = IItemColor { stack, tintIndex ->
            val state = Block.getBlockFromItem(stack.item).getStateFromMeta(stack.metadata)
            blockColors.colorMultiplier(state, null, null, tintIndex)
        }

        for (block in BLOCKS) {
            val blockColor = when (block) {
                is BlockGenesisFern -> grassColor
                is BlockLeaves -> foliageColor
                else -> null
            }

            if (blockColor != null) {
                blockColors.registerBlockColorHandler(blockColor, block)
                itemColors.registerItemColorHandler(itemBlockColor, block)
            }
        }
    }

    private fun name(name: String) : Lazy<Block> = lazy { Block.REGISTRY.getObject(ResourceLocation(GenesisMod.MOD_ID, name)) }
}
