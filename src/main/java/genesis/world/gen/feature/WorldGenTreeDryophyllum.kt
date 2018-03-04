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
import net.minecraft.block.BlockLeaves.CHECK_DECAY
import net.minecraft.block.BlockLog
import net.minecraft.block.BlockLog.LOG_AXIS
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import java.util.*

class WorldGenTreeDryophyllum(private val variant: DryophyllumVariant, minHeight: Int, maxHeight: Int) : WorldGenAbstractGenesisTree(minHeight, maxHeight, true) {
    companion object {
        private val LOG = GenesisBlocks.DRYOPHYLLUM_LOG.defaultState
        private val LEAF = GenesisBlocks.DRYOPHYLLUM_LEAVES.defaultState.withProperty(CHECK_DECAY, false)

        private fun xzEqual(vecA: Vec3i, vecB: Vec3i): Boolean {
            return vecA.x == vecB.x && vecA.z == vecB.z
        }
    }

    override fun generate(world: World, rand: Random, pos: BlockPos): Boolean {
        val height = getTreeHeight(rand)
        val trunkHeight = height - variant.leavesHeightTotal

        //        if (!BlockVolumeShape.region(-1, 1, -1, 1, trunkHeight - 1, 1)
        //                .and(-3, trunkHeight, -3, 3, height, 3)
        //                .hasSpace(pos, isEmptySpace(world)))
        //            return false;

        for (i in 0 until trunkHeight) {
            setBlockInWorld(world, pos.up(i), LOG.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y), true)
        }

        generateTreeLeavesAndBranches(world, pos.add(0, trunkHeight, 0), pos, rand)

        return true
    }

    private fun generateTreeLeavesAndBranches(world: World, trunkTop: BlockPos, treeBottom: BlockPos, rand: Random) {

        for (i in 0 until variant.branchTargetsAmount) {
            val r = variant.xzBranchTargetSpread
            val yr = variant.yBranchTargetSpread * 2

            var currentPos = Vec3d(trunkTop).add(Vec3d((rand.nextDouble() * 2 - 1) * r, rand.nextDouble() * yr, (rand.nextDouble() * 2 - 1) * r))
            var branchSrc = trunkTop.up(MathHelper.getInt(rand, variant.branchSourceRelativeMinY, variant.branchSourceRelativeMaxY))
            val dy = currentPos.y - trunkTop.y
            val maxLen = Math.max(yr, (trunkTop.y - treeBottom.y).toDouble())
            if (Math.abs(dy) > maxLen) {
                branchSrc = branchSrc.up(Math.round(Math.abs(dy) - maxLen).toInt())
                if (branchSrc.y > trunkTop.y) {
                    val diff = branchSrc.y - trunkTop.y
                    branchSrc = branchSrc.down(diff)
                    currentPos = currentPos.addVector(0.0, diff.toDouble(), 0.0)
                }
            }
            if (Math.round(currentPos.y) < treeBottom.y + MathHelper.ceil(variant.leavesRadius) + 2) {
                currentPos = Vec3d(currentPos.x, (treeBottom.y + MathHelper.ceil(variant.leavesRadius) + 2).toDouble(), currentPos.z)
            }

            generateBranch(world, rand, branchSrc, currentPos)
        }

    }

    private fun generateBranch(world: World, rand: Random, trunkPos: BlockPos, endPos: Vec3d) {
        var curr = Vec3d(trunkPos)
        var next = next(world, curr, endPos.subtract(curr).normalize(), endPos, trunkPos)
        var prev: BlockPos
        do {
            val currBlock = BlockPos(curr)
            val dir = endPos.subtract(curr).normalize()
            prev = currBlock
            curr = next
            next = next(world, curr, dir, endPos, trunkPos)

            val state = if (xzEqual(currBlock, trunkPos)) LOG else LOG.withProperty(BlockLog.LOG_AXIS, getLogAxis(world, currBlock, dir))
            setBlockInWorld(world, currBlock, state, true)

            // check to avoid long straight up branches
            val nextBlock = BlockPos(next)
            if (endPos.squareDistanceTo(next) > Math.sqrt(3.0) && xzEqual(prev, currBlock) && xzEqual(currBlock, nextBlock)) {
                next = next.addVector((if (rand.nextBoolean()) -1 else 1).toDouble(), 0.0, (if (rand.nextBoolean()) -1 else 1).toDouble())
            }
        } while (endPos.squareDistanceTo(curr) > Math.sqrt(3.0))

        generateLeaves(world, rand, curr)
        generateLeaves(world, rand, Vec3d(prev))
    }

    private fun generateLeaves(world: World, rand: Random, pos: Vec3d) {
        val leavesRadius = MathHelper.ceil(variant.leavesRadius)
        val expConst = variant.distanceExpConstant

        for (dx in -leavesRadius..leavesRadius) {
            for (dy in -leavesRadius..leavesRadius) {
                for (dz in -leavesRadius..leavesRadius) {
                    // lower value of p makes it closer to diamond shape, higher value makes it closer to cube, value p=2 is perfect sphere.
                    if (Math.pow(Math.abs(dx).toDouble(), expConst) + Math.pow(Math.abs(dy).toDouble(), expConst) + Math.pow(Math.abs(dz).toDouble(), expConst) - rand.nextFloat().toDouble() * variant.leavesShapeRandomization * Math.pow(leavesRadius.toDouble(), expConst) <= Math.pow(leavesRadius.toDouble(), expConst)) {
                        setBlockInWorld(world, BlockPos(pos.addVector(dx.toDouble(), dy.toDouble(), dz.toDouble())), LEAF)
                    }
                }
            }
        }
    }

    private fun next(world: World, previousPos: Vec3d, direction: Vec3d, target: Vec3d, trunkSrcPos: Vec3i): Vec3d {
        var bestPos: Vec3d? = null
        var bestDist = previousPos.squareDistanceTo(target) - 0.5

        var bestNoTrunkPos: Vec3d? = null
        var bestNoTrunkDist = previousPos.squareDistanceTo(target) - 0.5
        for (dx in -1..1) {
            for (dz in -1..1) {
                // try to find point closer to target that isn't at trunk XZ coords.
                val blockPosXZ = BlockPos(previousPos.addVector(dx.toDouble(), 0.0, dz.toDouble()))
                if (xzEqual(trunkSrcPos, blockPosXZ)) {
                    val newPos = Vec3d(blockPosXZ)
                    val dist = newPos.squareDistanceTo(target)
                    if (dist < bestNoTrunkDist) {
                        bestNoTrunkPos = newPos
                        bestNoTrunkDist = dist
                    }
                }
                for (dy in -1..1) {
                    if (dx != 0 || dy != 0 || dz != 0) {
                        // find surrounding existing blocks that aren't trunk
                        val blockPos = BlockPos(previousPos.addVector(dx.toDouble(), dy.toDouble(), dz.toDouble()))
                        if (!xzEqual(trunkSrcPos, blockPos) && world.getBlockState(blockPos).block === LOG.block) {
                            val newPos = Vec3d(blockPos)
                            val dist = newPos.squareDistanceTo(target)
                            if (dist < bestDist) {
                                bestPos = newPos
                                bestDist = dist
                            }
                        }
                    }
                }
            }
        }
        var nextDirect = previousPos
        val origPos = BlockPos(previousPos)
        do {
            nextDirect = nextDirect.add(direction)
        } while (origPos == BlockPos(nextDirect))

        if (bestPos != null) {
            val trunkVec = Vec3d(trunkSrcPos)
            val diff = 1 + previousPos.subtract(trunkVec).normalize().dotProduct(target.subtract(previousPos).normalize())
            val distDirect = nextDirect.squareDistanceTo(target)
            val distBest = bestPos.squareDistanceTo(target)
            if (distBest < distDirect + diff) {
                return bestPos
            }
        }
        return if (bestNoTrunkPos != null) {
            bestNoTrunkPos
        } else nextDirect
    }

    private fun getLogAxis(world: World, pos: BlockPos, dir: Vec3d): BlockLog.EnumAxis {
        // this finds the right direction based on neighbors
        var weightX = 0
        var weightY = 0
        var weightZ = 0
        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    if (dx != 0 || dy != 0 || dz != 0) {
                        if (world.getBlockState(pos.add(dx, dy, dz)).block === LOG.block) {
                            weightX += if (dx == 0) 0 else 1
                            weightY += if (dy == 0) 0 else 1
                            weightZ += if (dz == 0) 0 else 1
                        }
                    }
                }
            }
        }
        var axis: BlockLog.EnumAxis = BlockLog.EnumAxis.NONE
        // when X wins
        if (weightX > weightY && weightX > weightZ) {
            axis = BlockLog.EnumAxis.X
        } else if (weightX == weightZ && weightX > weightY) {
            axis = BlockLog.EnumAxis.NONE
        } else if (weightY > weightZ && weightY > weightX) {
            axis = BlockLog.EnumAxis.Y
        } else if (weightZ > weightY && weightZ > weightX) {
            axis = BlockLog.EnumAxis.Z
        } // some of this is probably redundant, but check if Y wins
        // when horizontal is ambiguous and Y is smaller

        if (axis == BlockLog.EnumAxis.NONE) {
            val dx = dir.x * dir.x
            val dy = dir.y * dir.y
            val dz = dir.z * dir.z
            when {
                dx > dy + dz -> axis = BlockLog.EnumAxis.X
                dy > dx + dz -> axis = BlockLog.EnumAxis.Y
                dz > dx + dy -> axis = BlockLog.EnumAxis.Z
            }
        }
        return axis
    }

    enum class DryophyllumVariant constructor(val branchTargetsAmount: Int, val branchSourceRelativeMinY: Int, val xzBranchTargetSpread: Double, val yBranchTargetSpread: Double) {
        TYPE_1(8, -3, 2.0, 2.2),
        TYPE_2(15, -4, 4.3, 3.5);

        val branchSourceRelativeMaxY = -1
        val leavesRadius = 2.2
        val distanceExpConstant = 1.6
        val leavesShapeRandomization = 1.0
        val leavesHeightTotal: Int

        init {
            leavesHeightTotal = MathHelper.ceil(leavesRadius + yBranchTargetSpread * 2)
        }
    }
}
