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

import genesis.combo.variant.EnumOre
import genesis.init.GenesisCreativeTabs
import genesis.util.Harvest
import net.minecraft.block.BlockOre
import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

class BlockGenesisOre(private val oreType: EnumOre) : BlockOre() {

    init {
        setHardness(oreType.hardness)
        setResistance(oreType.resistance)
        soundType = SoundType.STONE
        setCreativeTab(GenesisCreativeTabs.BUILDING_BLOCKS)
        setHarvestLevel(Harvest.CLASS_PICKAXE, Harvest.LEVEL_STONE)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int) = oreType.item

    override fun quantityDropped(random: Random) = MathHelper.getInt(random, oreType.dropMin, oreType.dropMax)

    override fun getExpDrop(state: IBlockState, world: IBlockAccess, pos: BlockPos, fortune: Int): Int {
        val rand = if (world is World) world.rand else RANDOM
        return MathHelper.getInt(rand, oreType.expMin, oreType.expMax)
    }
}