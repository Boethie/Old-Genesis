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

import genesis.init.GenesisBlocks;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.block.BlockLeaves.CHECK_DECAY;
import static net.minecraft.block.BlockLog.LOG_AXIS;

public class WorldGenTreeGinkgo extends WorldGenAbstractGenesisTree {

    private static final IBlockState LOG = GenesisBlocks.INSTANCE.getGINKGO_LOG().getDefaultState();
    private static final IBlockState LEAF = GenesisBlocks.INSTANCE.getGINKGO_LEAVES().getDefaultState().withProperty(CHECK_DECAY, false);

    public WorldGenTreeGinkgo(int minHeight, int maxHeight) {
        super(minHeight, maxHeight, true);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int height = getTreeHeight(rand);
        int base = 2 + rand.nextInt(4);

//        if (!BlockVolumeShape.region(-1, 1, -1, 1, base - 1, 1)
//                .and(-4, base, -4, 4, base + height, 4)
//                .hasSpace(pos, isEmptySpace(world)))
//            return false;

        int mainBranches = 2 + rand.nextInt(2);

        for (int i = 0; i < mainBranches; ++i) {
            base = 2 + rand.nextInt(4);
            branchUp(world, pos, rand, height, (base >= height - 2) ? height - 5 : base);
        }

        return true;
    }

    private void branchUp(World world, BlockPos pos, Random rand, int height, int base) {
        int fallX = 1 - rand.nextInt(3);
        int fallZ = 1 - rand.nextInt(3);
        int fallCount = 0;
        BlockPos upPos = pos.down();
        EnumAxis woodAxis;

        for (int i = 0; i < height; i++) {
            if (rand.nextInt(3) == 0 && i > base && fallCount < 3) {
                fallCount++;

                upPos = upPos.add(fallX, 0, fallZ);

                if (fallX != 0) {
                    woodAxis = EnumAxis.X;
                } else if (fallZ != 0) {
                    woodAxis = EnumAxis.Z;
                } else {
                    woodAxis = EnumAxis.Y;
                }

                if (rand.nextInt(3) == 0 || (fallX == 0 && fallZ == 0)) {
                    upPos = upPos.up();
                }
            } else {
                fallCount = 0;

                woodAxis = EnumAxis.Y;
                upPos = upPos.up();
            }

            setBlockInWorld(world, upPos, LOG.withProperty(LOG_AXIS, woodAxis));

            if (i == base) {
                generateBranchLeaves(world, upPos, rand, false, 2, true, LEAF);
            }

            if (i > base) {
                if (i > base + 2) {
                    generateBranchLeaves(world, upPos.down(2), rand, false, 2, true, LEAF);
                }

                if (i > base + 1) {
                    generateBranchLeaves(world, upPos.down(), rand, false, 3, true, LEAF);
                }

                generateBranchLeaves(world, upPos, rand, true, 4, true, LEAF);
            }
        }
    }
}
