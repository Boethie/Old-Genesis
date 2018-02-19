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
import net.minecraft.block.BlockLog
import net.minecraft.block.BlockLog.EnumAxis
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

class WorldGenTreeFicus(minHeight: Int, maxHeight: Int) : WorldGenAbstractGenesisTree(minHeight, maxHeight, true) {
    companion object {
        private val LOG = GenesisBlocks.FICUS_LOG.defaultState
        private val LEAVES = GenesisBlocks.FICUS_LEAVES.defaultState.withProperty(BlockLeaves.CHECK_DECAY, false)
    }

    override fun generate(world: World, rand: Random, pos: BlockPos): Boolean {
        val height = getTreeHeight(rand)
        val base = 1 + rand.nextInt(3)

//		if (!BlockVolumeShape.region(-1, 1, -1, 1, base - 1, 1)
//					 .and(-5, base, -5, 5, base + height, 5)
//					 .hasSpace(pos, isEmptySpace(world)))
//			return false;

        val mainBranches = 1 + rand.nextInt(8)

        for (i in 0 until mainBranches) branchUp(world, pos, rand, height, base)

        return true
    }

    private fun branchUp(world: World, pos: BlockPos, rand: Random, height: Int, base: Int) {
        val xShift = 1 - rand.nextInt(3)
        val zShift = 1 - rand.nextInt(3)

        val upPos = BlockPos.MutableBlockPos(pos).move(EnumFacing.DOWN)
        var woodAxis = EnumAxis.Y

        for (i in 0 until height) {
            if (i > base) {
                upPos.shift(xShift, 0, zShift)

                //Have some variation in the orientation of the logs
                woodAxis = if (rand.nextBoolean()) {
                    when {
                        xShift != 0 -> EnumAxis.X
                        zShift != 0 -> EnumAxis.Z
                        else -> EnumAxis.Y
                    }
                } else {
                    when {
                        zShift != 0 -> EnumAxis.Z
                        xShift != 0 -> EnumAxis.X
                        else -> EnumAxis.Y
                    }
                }
            }

            upPos.y++

            setBlockInWorld(world, upPos, LOG.withProperty(BlockLog.LOG_AXIS, woodAxis), true)

            if (i >= base - rand.nextInt(2)) generateBranchLeaves(world, upPos, LEAVES, rand, 3 + rand.nextInt(2), i >= height - 1, true)
        }
    }
}
