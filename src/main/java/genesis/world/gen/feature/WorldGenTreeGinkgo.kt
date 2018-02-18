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
import net.minecraft.block.BlockLeaves.CHECK_DECAY
import net.minecraft.block.BlockLog.EnumAxis
import net.minecraft.block.BlockLog.LOG_AXIS
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

class WorldGenTreeGinkgo(minHeight: Int, maxHeight: Int) : WorldGenAbstractGenesisTree(minHeight, maxHeight, true) {
    companion object {
        private val LOG = GenesisBlocks.GINKGO_LOG.defaultState
        private val LEAF = GenesisBlocks.GINKGO_LEAVES.defaultState.withProperty(CHECK_DECAY, false)
    }

    override fun generate(world: World, rand: Random, pos: BlockPos): Boolean {
        val height = getTreeHeight(rand)
        var base: Int // = 2 + rand.nextInt(4);

        //        if (!BlockVolumeShape.region(-1, 1, -1, 1, base - 1, 1)
        //                .and(-4, base, -4, 4, base + height, 4)
        //                .hasSpace(pos, isEmptySpace(world)))
        //            return false;

        val mainBranches = 2 + rand.nextInt(2)

        for (i in 0 until mainBranches) {
            base = 2 + rand.nextInt(4)
            branchUp(world, pos, rand, height, if (base >= height - 2) height - 5 else base)
        }

        return true
    }

    private fun branchUp(world: World, pos: BlockPos, rand: Random, height: Int, base: Int) {
        val fallX = 1 - rand.nextInt(3)
        val fallZ = 1 - rand.nextInt(3)
        var fallCount = 0
        val upPos = BlockPos.MutableBlockPos(pos).move(EnumFacing.DOWN)
        var woodAxis: EnumAxis

        for (i in 0 until height) {
            if (rand.nextInt(3) == 0 && i > base && fallCount < 3) {
                fallCount++

                upPos.shift(fallX, 0, fallZ)

                woodAxis = when {
                    fallX != 0 -> EnumAxis.X
                    fallZ != 0 -> EnumAxis.Z
                    else -> EnumAxis.Y
                }

                if (rand.nextInt(3) == 0 || fallX == 0 && fallZ == 0) {
                    upPos.y++
                }
            } else {
                fallCount = 0

                woodAxis = EnumAxis.Y
                upPos.y++
            }

            setBlockInWorld(world, upPos, LOG.withProperty(LOG_AXIS, woodAxis), true)

            if (i == base) {
                generateBranchLeaves(world, upPos, LEAF, rand, 2, false, true)
            }

            if (i > base) {
                if (i > base + 2) {
                    generateBranchLeaves(world, upPos.down(2), LEAF, rand, 2, false, true)
                }

                if (i > base + 1) {
                    generateBranchLeaves(world, upPos.down(), LEAF, rand, 3, false, true)
                }

                generateBranchLeaves(world, upPos, LEAF, rand, 4, true, true)
            }
        }
    }
}
