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
package genesis.world.gen.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public abstract class WorldGenAbstractGenesisTree extends WorldGenAbstractTree {

    private int minHeight;
    private int maxHeight;

    public WorldGenAbstractGenesisTree(int minHeight, int maxHeight, boolean notify) {
        super(notify);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    protected int getTreeHeight(Random rand) {
        return MathHelper.getInt(rand, minHeight, maxHeight);
    }

    protected void setBlockInWorld(World world, BlockPos pos, IBlockState state) {
        setBlockAndNotifyAdequately(world, pos, state); // TODO: is the logic from the old WorldGenBaseTree#setBlockInWorld needed here?
    }

    protected void generateTopLeaves(World world, BlockPos genPos, BlockPos branchPos, int treeHeight, int leavesBase, Random rand, boolean alternate,
            int maxLeavesLength, boolean irregular, boolean inverted, IBlockState leaves) {
        boolean alt = false;
        float percent;
        int leavesLength;

        if (leavesBase > branchPos.getY()) {
            return;
        }

        generateBranchLeaves(world, branchPos, rand, true, 1, leaves);

        while (branchPos.getY() > leavesBase) {
            branchPos = branchPos.add(0, -1, 0);

            percent = ((branchPos.getY() - leavesBase) / (float) (genPos.getY() + treeHeight - leavesBase));

            if (!inverted) {
                percent = 1 - percent;
            }

            leavesLength = MathHelper.ceil(maxLeavesLength * percent);

            if (leavesLength > maxLeavesLength) {
                leavesLength = maxLeavesLength;
            }

            if (alt || !alternate || (irregular && rand.nextInt(5) == 0)) {
                generateBranchLeaves(world, branchPos, rand, false, leavesLength, irregular, leaves);
            }

            alt = !alt;
        }
    }

    protected void generateBranchLeaves(World world, BlockPos pos, Random random, boolean cap, int length, IBlockState leaves) {
        generateBranchLeaves(world, pos, random, cap, length, false, leaves);
    }

    protected void generateBranchLeaves(World world, BlockPos pos, Random random, boolean cap, int length, boolean irregular, IBlockState leaves) {
        for (int i = 1; i <= length - ((irregular && random.nextInt(3) == 0) ? (random.nextInt(length + 1)) : 0); ++i) {
            setBlockInWorld(world, pos.north(i), leaves);
            if (!irregular || !(random.nextInt(6) == 0)) {
                setBlockInWorld(world, pos.north(i - 1).east(), leaves);
            }
            if (!irregular || !(random.nextInt(6) == 0)) {
                setBlockInWorld(world, pos.north(i - 1).west(), leaves);
            }
        }

        for (int i = 1; i <= length - ((irregular && random.nextInt(3) == 0) ? (random.nextInt(length + 1)) : 0); ++i) {
            setBlockInWorld(world, pos.south(i), leaves);
            if (!irregular || !(random.nextInt(6) == 0)) {
                setBlockInWorld(world, pos.south(i - 1).east(), leaves);
            }
            if (!irregular || !(random.nextInt(6) == 0)) {
                setBlockInWorld(world, pos.south(i - 1).west(), leaves);
            }
        }

        for (int i = 1; i <= length - ((irregular && random.nextInt(3) == 0) ? (random.nextInt(length + 1)) : 0); ++i) {
            setBlockInWorld(world, pos.east(i), leaves);
            if (!irregular || !(random.nextInt(6) == 0)) {
                setBlockInWorld(world, pos.east(i - 1).north(), leaves);
            }
            if (!irregular || !(random.nextInt(6) == 0)) {
                setBlockInWorld(world, pos.east(i - 1).south(), leaves);
            }
        }

        for (int i = 1; i <= length - ((irregular && random.nextInt(3) == 0) ? (random.nextInt(length + 1)) : 0); ++i) {
            setBlockInWorld(world, pos.west(i), leaves);
            if (!irregular || !(random.nextInt(6) == 0)) {
                setBlockInWorld(world, pos.west(i - 1).north(), leaves);
            }
            if (!irregular || !(random.nextInt(6) == 0)) {
                setBlockInWorld(world, pos.west(i - 1).south(), leaves);
            }
        }

        if (cap) {
            setBlockInWorld(world, pos.up(1), leaves);
            setBlockInWorld(world, pos.up(1).north(), leaves);
            setBlockInWorld(world, pos.up(1).south(), leaves);
            setBlockInWorld(world, pos.up(1).east(), leaves);
            setBlockInWorld(world, pos.up(1).west(), leaves);
            setBlockInWorld(world, pos.up(2), leaves);
        }
    }
}
