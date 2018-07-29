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

import net.minecraft.block.Block
import net.minecraft.block.BlockBush
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenAbstractTree
import java.util.*

abstract class WorldGenAbstractGenesisTree(protected var minHeight: Int, protected var maxHeight: Int, notify: Boolean) : WorldGenAbstractTree(notify) {

    protected fun getTreeHeight(rand: Random): Int {
        return MathHelper.getInt(rand, minHeight, maxHeight)
    }

    protected fun setBlockInWorld(world: World, pos: BlockPos, state: IBlockState, overwrite: Boolean = false) {
        if (overwrite)
            setBlockAndNotifyAdequately(world, pos, state)
        else if (world.isAirBlock(pos))
            setBlockAndNotifyAdequately(world, pos, state)

        // TODO: is the logic from the old WorldGenBaseTree#setBlockInWorld needed here?
    }

    protected fun generateTopLeaves(world: World, genPos: BlockPos, branchPos: BlockPos, treeHeight: Int, leavesBase: Int, rand: Random, alternate: Boolean, maxLeavesLength: Int, irregular: Boolean, inverted: Boolean, leaves: IBlockState) {
        val branchPos = BlockPos.MutableBlockPos(branchPos)
        var alt = false
        var percent: Float
        var leavesLength: Int

        if (leavesBase > branchPos.y) {
            return
        }

        generateBranchLeaves(world, branchPos, leaves, rand, 1, true)

        while (branchPos.y > leavesBase) {
            branchPos.y--

            percent = (branchPos.y - leavesBase) / (genPos.y + treeHeight - leavesBase).toFloat()

            if (!inverted) {
                percent = 1 - percent
            }

            leavesLength = MathHelper.ceil(maxLeavesLength * percent)

            if (leavesLength > maxLeavesLength) {
                leavesLength = maxLeavesLength
            }

            if (alt || !alternate || irregular && rand.nextInt(5) == 0) {
                generateBranchLeaves(world, branchPos, leaves, rand, leavesLength, false, irregular)
            }

            alt = !alt
        }
    }

    protected fun generateBranchLeaves(world: World, pos: BlockPos, leaves: IBlockState, random: Random, length: Int, cap: Boolean, irregular: Boolean = false) {
        for (i in 1..length - if (irregular && random.nextInt(3) == 0) random.nextInt(length + 1) else 0) {
            setBlockInWorld(world, pos.north(i), leaves)
            //We always do this twice because of the random check
            if (!irregular || random.nextInt(6) != 0) setBlockInWorld(world, pos.north(i - 1).east(), leaves)
            if (!irregular || random.nextInt(6) != 0) setBlockInWorld(world, pos.north(i - 1).west(), leaves)
        }

        for (i in 1..length - if (irregular && random.nextInt(3) == 0) random.nextInt(length + 1) else 0) {
            setBlockInWorld(world, pos.south(i), leaves)
            if (!irregular || random.nextInt(6) != 0) setBlockInWorld(world, pos.south(i - 1).east(), leaves)
            if (!irregular || random.nextInt(6) != 0) setBlockInWorld(world, pos.south(i - 1).west(), leaves)
        }

        for (i in 1..length - if (irregular && random.nextInt(3) == 0) random.nextInt(length + 1) else 0) {
            setBlockInWorld(world, pos.east(i), leaves)
            if (!irregular || random.nextInt(6) != 0) setBlockInWorld(world, pos.east(i - 1).north(), leaves)
            if (!irregular || random.nextInt(6) != 0) setBlockInWorld(world, pos.east(i - 1).south(), leaves)
        }

        for (i in 1..length - if (irregular && random.nextInt(3) == 0) random.nextInt(length + 1) else 0) {
            setBlockInWorld(world, pos.west(i), leaves)
            if (!irregular || random.nextInt(6) != 0) setBlockInWorld(world, pos.west(i - 1).north(), leaves)
            if (!irregular || random.nextInt(6) != 0) setBlockInWorld(world, pos.west(i - 1).south(), leaves)
        }

        if (cap) {
            val up = pos.up(1)
            setBlockInWorld(world, up, leaves)
            setBlockInWorld(world, up.north(), leaves)
            setBlockInWorld(world, up.south(), leaves)
            setBlockInWorld(world, up.east(), leaves)
            setBlockInWorld(world, up.west(), leaves)
            setBlockInWorld(world, pos.up(2), leaves)
        }
    }

    /**
     * @return The position the sapling would be at above the soil, or null if the tree cannot grow there.
     */
    fun getTreePos(world: World, pos: BlockPos, distance: Int, sapling: IBlockState): BlockPos? {
        if (world.getBlockState(pos).block === sapling.block) return pos

        val soilPos = BlockPos.MutableBlockPos(pos)
        var checkState: IBlockState

        do {
            if (distance != -1 && pos.y - soilPos.y > distance) return null

            checkState = world.getBlockState(soilPos)

            if (!checkState.block.isAir(checkState, world, soilPos) && !checkState.block.isLeaves(checkState, world, soilPos)) break

            soilPos.y--
        } while (soilPos.y > 0)

        // Begin checking whether tree can grow here.
        val saplingPos = soilPos.up()

        if (!(sapling.block as BlockBush).canBlockStay(world, saplingPos, sapling)) return null

        val replacing = world.getBlockState(saplingPos)

        return if (!replacing.block.isReplaceable(world, saplingPos)) null else saplingPos
    }

    protected fun isTwoByTwoOfType(worldIn: World, pos: BlockPos, xOff: Int, zOff: Int, state: Block): Boolean {
        return  isTypeAt(worldIn, pos.add(xOff, 0, zOff), state) &&
                isTypeAt(worldIn, pos.add(xOff + 1, 0, zOff), state) &&
                isTypeAt(worldIn, pos.add(xOff, 0, zOff + 1), state) &&
                isTypeAt(worldIn, pos.add(xOff + 1, 0, zOff + 1), state)
    }

    /**
     * Check whether the given BlockPos has a Sapling of the given type
     */
    protected fun isTypeAt(worldIn: World, pos: BlockPos, state: Block) = worldIn.getBlockState(pos).block === state
}
