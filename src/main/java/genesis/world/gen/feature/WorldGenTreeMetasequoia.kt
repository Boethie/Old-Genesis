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
import genesis.util.BranchDirection
import genesis.util.shift
import net.minecraft.block.BlockLeaves
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

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

        // TODO: move this out of generator and let generator assume the sapling type given is correct?
        pos.toImmutable().let {
            for (x in -1..1) {
                if (isCrossShapeSaplings(world, pos.add(x, 0, 0)) ) {
                    setVariant(Variant.TYPE_2)
                    pos.shift(x, 0, 0)
                    break
                }
                if (isCrossShapeSaplings(world, pos.add(0, 0, x))) {
                    setVariant(Variant.TYPE_2)
                    pos.shift(0, 0, x)
                    break
                }
            }
        }

        if (variant == Variant.TYPE_2) {
            return generateBigTree(world, rand, pos);
        }

        val height = getTreeHeight(rand)

        val trunkHeight = 2 + rand.nextInt(3)
        val leavesBase = pos.y + trunkHeight

        for (i in 0 until height) {
            setBlockInWorld(world, pos.up(i), LOG, true)
        }

        val branchPos = pos.up(height - 1)

        generateTopLeaves(world, pos, branchPos, height, leavesBase, rand, false, 4, true, false, LEAVES)


//      generateResin(world, pos, height)

        return true
    }

    private fun isCrossShapeSaplings(world: World, pos: BlockPos): Boolean {
        val block = GenesisBlocks.METASEQUOIA_SAPLING
        for (x in -1..1) {
            if (!isTypeAt(world, pos.add(x, 0, 0), block) || !isTypeAt(world, pos.add(0, 0, x), block)) {
                return false
            }
        }
        return true
    }

    // TODO: Big one in separate class


    private fun generateBigTree(world: World, rand: Random, pos: BlockPos.MutableBlockPos): Boolean {
        if (BlockPos.getAllInBoxMutable(pos.add(-4, 2, -4), pos.add(4, 15, 4))
                .any { world.getBlockState(it).material.isSolid }) {
            return false
        }
        // find the lowest Y with a sapling in the 2x2 area (so no part of the tree floats in the air)
        for (cornerPos in BlockPos.getAllInBoxMutable(pos, pos.add(1, 0, 1))) {
            if (cornerPos == pos) continue

            val groundPos = getTreePos(world, cornerPos, -1, SAPLING) ?: return false

            pos.y = Math.min(pos.y, groundPos.y)
        }

        val height = getTreeHeight(rand)

        val trunkHeight = 2 + rand.nextInt(3)

        // generate the 2x2 trunk
        for (dy in 0 until height) {
            for (dx in -1..1) {
                for (dz in -1..1) {
                    // 2/3th of the way to the top, only generate the center
                    if (dy > 2 * height / 3 && (dx != 0 || dz != 0)) {
                        continue
                    }
                    if (dx == 0 || dz == 0) {
                        setBlockInWorld(world, pos.add(dx, dy, dz), LOG, true)
                    }
                }
            }
        }

        var branchY = trunkHeight + 1.0

        // create shuffled list of random directions, and iterator
        // this makes sure that for tall enough tree, all possible branch directions are used
        // with no chance to make the tree look too different in any direction
        val directions: ArrayList<BranchDirection> = getShuffledDirections()
        var directionsIt = directions.iterator()

        val branchYPosList = ArrayList<Int>()

        while (branchY < height - 2) { // no branches very close to the top -> more spiky
            branchYPosList.add(branchY.toInt())
            branchY += MathHelper.getInt(rand, 1, 3) * 0.5
        }

        val branchLengths = ArrayList<Int>()

        // generate random branch lengths, and sort them. They will be used in that order for "random" branch length
        // this makes the branches predictably longest at the bottom and shortest at the top, but keeps some randomness
        for (i in 0 until branchYPosList.size) {
            branchLengths.add(MathHelper.getInt(rand, 1, 7))
        }
        branchLengths.sort()

        val lengthsIt = branchLengths.asReversed().iterator()
        for(y in branchYPosList) {
            // if we run out of directions, shuffle and restart
            if (!directionsIt.hasNext()) {
                directions.shuffle()
                directionsIt = directions.iterator()
            }
            // start from random block in the 2x2 trunk
            generateBranch(world, rand, pos.add(0, y, 0), directionsIt.next(), lengthsIt.next())
        }

        val topLeavesHeight = 4;
        // generate leaves at the top
        for(y in height-topLeavesHeight until height) {
            // the bottom part of the top leaves should be "bigger", makes the tree more spiky near the top
            val size = if((y - height + topLeavesHeight) < 2) 2 else 1
            generateBranchLeaves(world, pos.add(0, y, 0), LEAVES, rand, size, y == height-1, true)
        }
        return true
    }

    private fun generateBranch(world: World, rand: Random, start: BlockPos, dir: BranchDirection, branchLength: Int) {
        val length = branchLength
        var dy = 0
        for (i in 0..length) {
            val straightPos = dir.shiftPos(start, i + 1) // skip the first because it's in the trunk
            // about halfway through the branch, start going up (with a bit of randomness)
            if (i > (length + rand.nextInt(3)) / 2) {
                dy++
            }
            val pos = straightPos.add(0, dy, 0)
            setBlockInWorld(world, pos, LOG, true)
            // 2/3rd of the way along the branch, start adding leaves
            if (i >= 2 * length / 3) {
                val leavesSize = when (i) {
                    length -> min(length + 1, 3) // less leaves right at the top
                    length - 1 -> min(length, 2)
                    else -> 1
                }
                generateBranchLeaves(world, pos, LEAVES, rand, leavesSize, i == length, true)
            }
        }
    }

    // generates array of all branch directions we want to use, shuffled
    private fun getShuffledDirections(): ArrayList<BranchDirection> {
        val dirs = ArrayList<BranchDirection>()
        for (dx in -2..2) {
            for (dz in -2..2) {
                if (dx != 0 && dz != 0) {
                    dirs.add(BranchDirection(dx, 0, dz))
                }
            }
        }
        dirs.shuffle()
        return dirs
    }

    enum class Variant(val minHeight: Int, val maxHeight: Int) {
        SINGLE(17, 23), TYPE_2(28, 31)
    }
}
