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


class WorldGenTreeAraucarioxylon(minHeight: Int, maxHeight: Int) : WorldGenAbstractGenesisTree(minHeight, maxHeight, true) {
    companion object {
        private val LOG = GenesisBlocks.ARAUCARIOXYLON_LOG.defaultState
        private val LEAF = GenesisBlocks.ARAUCARIOXYLON_LEAVES.defaultState.withProperty(CHECK_DECAY, false)
    }

    override fun generate(world: World, rand: Random, pos: BlockPos): Boolean {
        val height = getTreeHeight(rand)

        val groundLevel = pos.y
        val leavesBase = groundLevel + height - 3 - rand.nextInt(2)
        val alternate = true
        val irregular = true
        val inverted = false
        val maxLeavesLength = 2

        //        if (!BlockVolumeShape.region(0, 1, 0, 0, leavesBase, 0)
        //                .and(-2, leavesBase + 1, -2, 2, height, 2)
        //                .hasSpace(pos, isEmptySpace(world))) {
        //            return false;
        //        }

        val trunkPos = BlockPos.MutableBlockPos(pos)
        for (i in 0 until height) {
            setBlockInWorld(world, trunkPos, LOG.withProperty(LOG_AXIS, EnumAxis.Y), true)
            trunkPos.y++
        }

        val base = 4 + rand.nextInt(4)
        var direction = rand.nextInt(8)

        val branchPos = BlockPos.MutableBlockPos(pos.x, pos.y + base, pos.z)

        for (i in base until height) {
            if (++direction > 7) direction = 0

            val lFactor = (6 * ((height - i) / height.toFloat())).toInt()

            val immutable = branchPos.toImmutable()
            generateBranch(world, immutable, rand, groundLevel, direction + 1, lFactor)

            if (rand.nextInt(8) == 0) {
                if (++direction > 7) direction = 0

                generateBranch(world, immutable, rand, groundLevel, direction + 1, lFactor)
            }
            branchPos.y++
        }

        generateTopLeaves(world, pos, pos.up(height - 1), height, leavesBase, rand, alternate, maxLeavesLength, irregular, inverted, LEAF)

        //        if (treeType != TreeTypes.TYPE_3) {
        //            generateResin(world, pos, height);
        //        }

        return true
    }

    private fun generateBranch(world: World, pos: BlockPos, rand: Random, groundLevel: Int, direction: Int, lengthModifier: Int) {
        var fallX = 1
        var fallZ = 1
        val pos = BlockPos.MutableBlockPos(pos).move(EnumFacing.DOWN)
        var woodAxis: EnumAxis

        when (direction) {
            0 -> {
                fallX = 1
                fallZ = 1
            }
            1 -> {
                fallX = 0
                fallZ = 1
            }
            2 -> {
                fallX = 1
                fallZ = 1
            }
            3 -> {
                fallX = 1
                fallZ = 0
            }
            4 -> {
                fallX = 1
                fallZ = -1
            }
            5 -> {
                fallX = 0
                fallZ = -1
            }
            6 -> {
                fallX = -1
                fallZ = -1
            }
            7 -> {
                fallX = -1
                fallZ = 0
            }
            8 -> {
                fallX = -1
                fallZ = 1
            }
        }

        var leaves = true
        var horizontalCount = 0

        for (i in 0 until lengthModifier) {
            if (pos.y < groundLevel + 3) {
                return
            }

            pos.shift(fallX, 0, fallZ)

            woodAxis = when {
                fallX != 0 -> EnumAxis.X
                fallZ != 0 -> EnumAxis.Z
                else -> EnumAxis.Y
            }

            if (horizontalCount < 1 + rand.nextInt(3)) {
                ++horizontalCount

                if (rand.nextInt(3) == 0 || fallX == 0 && fallZ == 0) {
                    pos.y--
                }
            } else {
                horizontalCount = 0

                woodAxis = EnumAxis.Y
                pos.y--
            }

            setBlockInWorld(world, pos, LOG.withProperty(LOG_AXIS, woodAxis), true)

            if (leaves && rand.nextInt(6) == 0) {
                generateBranchLeaves(world, pos, LEAF, rand, 3, true, true)
                generateBranchLeaves(world, pos.down(), LEAF, rand, 2, true, true)
            }

            leaves = !leaves

            if (i == lengthModifier - 1) {
                generateBranchLeaves(world, pos, LEAF, rand, 1 + rand.nextInt(2), false, true)
            }
        }
    }
}
