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
package genesis.block

import genesis.init.GenesisCreativeTabs
import genesis.util.Harvest
import net.minecraft.block.Block
import net.minecraft.block.BlockFalling
import net.minecraft.block.SoundType
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

open class BlockGenesis(material: Material, mapColor: MapColor, soundType: SoundType) : Block(material, mapColor) {
    init {
        setCreativeTab(GenesisCreativeTabs.BUILDING_BLOCKS)
        setSoundType(soundType)
    }
}

open class BlockGenesisRock(hardness: Float, resistance: Float) : Block(Material.ROCK) {
    init {
        setHardness(hardness)
        setResistance(resistance)
        setSoundType(SoundType.STONE)
        setCreativeTab(GenesisCreativeTabs.BUILDING_BLOCKS)
        setHarvestLevel(Harvest.CLASS_PICKAXE, Harvest.LEVEL_WOOD)
    }
}

class BlockSilt(private val mapColor: MapColor) : BlockFalling() {
    init {
        setHardness(0.5F)
        setSoundType(SoundType.SAND)
        setCreativeTab(GenesisCreativeTabs.BUILDING_BLOCKS)
        setHarvestLevel(Harvest.CLASS_SHOVEL, Harvest.LEVEL_WOOD)
    }

    override fun getMapColor(state: IBlockState, world: IBlockAccess, pos: BlockPos) = mapColor
}

class BlockCrackedSilt(mapColor: MapColor) : BlockGenesis(Material.SAND, mapColor, SoundType.SAND) {
    init {
        setHardness(0.6F)
        setHarvestLevel(Harvest.CLASS_SHOVEL, Harvest.LEVEL_WOOD)
    }
}

class BlockSiltstone(mapColor: MapColor) : BlockGenesis(Material.ROCK, mapColor, SoundType.STONE) {
    init {
        setHardness(1.4F)
        setHarvestLevel(Harvest.CLASS_PICKAXE, Harvest.LEVEL_WOOD)
    }
}

class BlockOoze : BlockGenesis(Material.CLAY, MapColor.LIME, SoundType.GROUND) {
    init {
        slipperiness = 0.88F
        setHardness(0.6F)
        setHarvestLevel(Harvest.CLASS_SHOVEL, Harvest.LEVEL_WOOD)
    }
}