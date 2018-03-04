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

package genesis.world.gen.feature

import genesis.init.GenesisBlocks
import genesis.util.shift
import net.minecraft.block.BlockLeaves
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

class WorldGenTreeMetasequoia(var variant: Variant = Variant.SINGLE) : WorldGenAbstractGenesisTree(variant.minHeight, variant.maxHeight, true) {
    companion object {
        private val LOG = GenesisBlocks.METASEQUOIA_LOG.defaultState
        private val LEAVES = GenesisBlocks.METASEQUOIA_LEAVES.defaultState.withProperty(BlockLeaves.CHECK_DECAY, false)
        private val SAPLING = GenesisBlocks.METASEQUOIA_SAPLING.defaultState
    }

    init {
//		this.saplingCountProvider = new WeightedIntProvider(
//				WeightedIntItem.of(104, 0),
//				WeightedIntItem.of(6, IntRange.create(1, 3)));
    }

    fun setVariant(variant: Variant): WorldGenTreeMetasequoia {
        minHeight = variant.minHeight
        maxHeight = variant.minHeight
        this.variant = variant
        return this
    }

    override fun generate(world: World, rand: Random, pos: BlockPos): Boolean {
        val pos = BlockPos.MutableBlockPos(pos)
        val height = getTreeHeight(rand)

        val trunkHeight = 2 + rand.nextInt(3)
        val leavesBase = pos.y + trunkHeight

        val block = GenesisBlocks.METASEQUOIA_SAPLING

        pos.toImmutable().let {
            for (x in 0 downTo -1) {
                for (z in 0 downTo -1) {
                    if (isTwoByTwoOfType(world, it, x, z, block)) {
                        pos.shift(x, 0, z)
                        setVariant(Variant.TYPE_2)
                        return@let
                    }
                }
            }
        }

        if (variant == Variant.TYPE_2) {
            for (cornerPos in BlockPos.getAllInBoxMutable(pos, pos.add(1, 0, 1))) {
                if (cornerPos == pos) continue

                val groundPos = getTreePos(world, cornerPos, -1, SAPLING) ?: return false

                pos.y = Math.min(pos.y, groundPos.y)
            }
        }

        for (i in 0 until height) {
            when (variant) {
                Variant.TYPE_2 -> {
                    setBlockInWorld(world, pos.add(1, i, 0), LOG, true)
                    setBlockInWorld(world, pos.add(0, i, 1), LOG, true)
                    setBlockInWorld(world, pos.add(1, i, 1), LOG, true)
                    setBlockInWorld(world, pos.add(0, i, 0), LOG, true)
                }
                else -> setBlockInWorld(world, pos.up(i), LOG, true)
            }
        }

        val branchPos = pos.up(height - 1)

        when (variant) {
            Variant.TYPE_2 -> {
                generateTopLeaves(world, pos, branchPos.add(0, 0, 0), height, leavesBase, rand, false, 4, true, false, LEAVES)
                generateTopLeaves(world, pos, branchPos.add(1, 0, 1), height, leavesBase, rand, false, 4, true, false, LEAVES)
                generateTopLeaves(world, pos, branchPos.add(1, 0, 0), height, leavesBase, rand, false, 4, true, false, LEAVES)
                generateTopLeaves(world, pos, branchPos.add(0, 0, 1), height, leavesBase, rand, false, 4, true, false, LEAVES)
            }
            else -> generateTopLeaves(world, pos, branchPos, height, leavesBase, rand, false, 4, true, false, LEAVES)
        }

//        when (treeType) {
//            TYPE_2 -> {
//                generateResin(world, pos.add(1, 0, 0), height)
//                generateResin(world, pos.add(0, 0, 1), height)
//                generateResin(world, pos.add(1, 0, 1), height)
//                generateResin(world, pos.add(0, 0, 0), height)
//            }
//            else -> generateResin(world, pos, height)
//        }

        return true
    }

    enum class Variant(val minHeight: Int, val maxHeight: Int) {
        SINGLE(17, 23), TYPE_2(22, 27)
    }
}
