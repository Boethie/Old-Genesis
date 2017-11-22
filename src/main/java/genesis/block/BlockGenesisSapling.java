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
package genesis.block;

import genesis.init.GenesisCreativeTabs;
import genesis.combo.variant.EnumTree;
import genesis.util.BoundingBoxes;
import genesis.util.WorldFlags;
import genesis.world.gen.feature.WorldGenAbstractGenesisTree;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

public class BlockGenesisSapling extends BlockBush implements IGrowable {

    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
    private static final int STAGE_FLAG = 0b1000; // = 1 << 3 = 8

    private final EnumTree treeType;

    public BlockGenesisSapling(EnumTree treeType) {
        super();
        this.treeType = treeType;
        setDefaultState(blockState.getBaseState().withProperty(STAGE, 0));
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
        setCreativeTab(GenesisCreativeTabs.DECORATIONS);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BoundingBoxes.SAPLING;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            checkAndDropBlock(world, pos, state);
            if (world.getLightFromNeighbors(pos.up()) >= 9 && random.nextInt(7) == 0) {
                grow(world, pos, state, random);
            }
        }
    }

    public void grow(World world, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(STAGE) == 0) {
            world.setBlockState(pos, state.cycleProperty(STAGE), WorldFlags.PREVENT_RERENDER);
        } else {
            generateTree(world, pos, state, rand);
        }
    }

    public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!TerrainGen.saplingGrowTree(world, rand, pos)) {
            return;
        }
        WorldGenAbstractGenesisTree generator = treeType.getTreeGenerator(rand);
        if (generator != null) {
            boolean largeTree = false;
            int x = 0;
            int z = 0;
            IBlockState air = Blocks.AIR.getDefaultState();

            if (largeTree) {
                world.setBlockState(pos.add(x, 0, z), air, WorldFlags.PREVENT_RERENDER);
                world.setBlockState(pos.add(x + 1, 0, z), air, WorldFlags.PREVENT_RERENDER);
                world.setBlockState(pos.add(x, 0, z + 1), air, WorldFlags.PREVENT_RERENDER);
                world.setBlockState(pos.add(x + 1, 0, z + 1), air, WorldFlags.PREVENT_RERENDER);
            } else {
                world.setBlockState(pos, air, WorldFlags.PREVENT_RERENDER);
            }

            if (!generator.generate(world, rand, pos.add(x, 0, z))) {
                if (largeTree) {
                    world.setBlockState(pos.add(x, 0, z), state, WorldFlags.PREVENT_RERENDER);
                    world.setBlockState(pos.add(x + 1, 0, z), state, WorldFlags.PREVENT_RERENDER);
                    world.setBlockState(pos.add(x, 0, z + 1), state, WorldFlags.PREVENT_RERENDER);
                    world.setBlockState(pos.add(x + 1, 0, z + 1), state, WorldFlags.PREVENT_RERENDER);
                } else {
                    world.setBlockState(pos, state, WorldFlags.PREVENT_RERENDER);
                }
            }

        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return world.rand.nextFloat() < 0.45D;
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        grow(world, pos, state, rand);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        state = state.withProperty(STAGE, (meta & STAGE_FLAG) / STAGE_FLAG);
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        meta |= state.getValue(STAGE) * STAGE_FLAG;
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }
}
