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

import static net.minecraft.block.BlockLeaves.CHECK_DECAY;
import static net.minecraft.block.BlockLog.LOG_AXIS;

import genesis.init.GenesisBlocks;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;


public class WorldGenTreeAraucarioxylon extends WorldGenAbstractGenesisTree {

    private static final IBlockState LOG = GenesisBlocks.ARAUCARIOXYLON_LOG.getDefaultState();
    private static final IBlockState LEAF = GenesisBlocks.ARAUCARIOXYLON_LEAVES.getDefaultState().withProperty(CHECK_DECAY, false);

    public WorldGenTreeAraucarioxylon(int minHeight, int maxHeight) {
        super(minHeight, maxHeight, true);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int height = getTreeHeight(rand);

        BlockPos branchPos = pos.up(height - 1);

        int leavesBase = branchPos.getY() - 2 - rand.nextInt(2);
        boolean alternate = true;
        boolean irregular = true;
        boolean inverted = false;
        int maxLeavesLength = 2;

//        if (!BlockVolumeShape.region(0, 1, 0, 0, leavesBase, 0)
//                .and(-2, leavesBase + 1, -2, 2, height, 2)
//                .hasSpace(pos, isEmptySpace(world))) {
//            return false;
//        }

        for (int i = 0; i < height; i++) {
            setBlockInWorld(world, pos.up(i), LOG.withProperty(LOG_AXIS, EnumAxis.Y));
        }

        int base = 4 + rand.nextInt(4);
        int direction = rand.nextInt(8);

        int lFactor;

        for (int i = base; i < height; ++i) {
            ++direction;
            if (direction > 7) {
                direction = 0;
            }

            lFactor = (int) (6 * (((height - i) / (float) height)));

            generateBranch(world, pos.up(i), rand, pos.getY(), direction + 1, lFactor);

            if (rand.nextInt(8) == 0) {
                ++direction;
                if (direction > 7) {
                    direction = 0;
                }
                generateBranch(world, pos.up(i), rand, pos.getY(), direction + 1, lFactor);
            }
        }

        generateTopLeaves(world, pos, branchPos, height, leavesBase, rand, alternate, maxLeavesLength, irregular, inverted, LEAF);

//        if (treeType != TreeTypes.TYPE_3) {
//            generateResin(world, pos, height);
//        }

        return true;
    }

    private void generateBranch(World world, BlockPos pos, Random rand, int groundLevel, int direction, int lengthModifier) {
        int fallX = 1;
        int fallZ = 1;
        BlockPos upPos = pos.down();
        EnumAxis woodAxis;

        switch (direction) {
            case 0:
                fallX = 1;
                fallZ = 1;
                break;
            case 1:
                fallX = 0;
                fallZ = 1;
                break;
            case 2:
                fallX = 1;
                fallZ = 1;
                break;
            case 3:
                fallX = 1;
                fallZ = 0;
                break;
            case 4:
                fallX = 1;
                fallZ = -1;
                break;
            case 5:
                fallX = 0;
                fallZ = -1;
                break;
            case 6:
                fallX = -1;
                fallZ = -1;
                break;
            case 7:
                fallX = -1;
                fallZ = 0;
            case 8:
                fallX = -1;
                fallZ = 1;
                break;
        }

        boolean leaves = true;
        int horzCount = 0;

        for (int i = 0; i < lengthModifier; i++) {
            if (upPos.getY() < groundLevel + 3) {
                return;
            }

            upPos = upPos.add(fallX, 0, fallZ);

            if (fallX != 0) {
                woodAxis = EnumAxis.X;
            } else if (fallZ != 0) {
                woodAxis = EnumAxis.Z;
            } else {
                woodAxis = EnumAxis.Y;
            }

            if (horzCount < 1 + rand.nextInt(3)) {
                ++horzCount;

                if (rand.nextInt(3) == 0 || (fallX == 0 && fallZ == 0)) {
                    upPos = upPos.down();
                }
            } else {
                horzCount = 0;

                woodAxis = EnumAxis.Y;
                upPos = upPos.down();
            }

            setBlockInWorld(world, upPos, LOG.withProperty(LOG_AXIS, woodAxis));

            if (leaves && rand.nextInt(6) == 0) {
                generateBranchLeaves(world, upPos, rand, true, 3, true, LEAF);
                generateBranchLeaves(world, upPos.down(), rand, true, 2, true, LEAF);
            }

            leaves = !leaves;

            if (i == lengthModifier - 1) {
                generateBranchLeaves(world, upPos, rand, false, 1 + rand.nextInt(2), true, LEAF);
            }
        }
    }
}
