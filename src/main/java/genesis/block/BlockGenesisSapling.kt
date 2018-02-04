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
package genesis.block

import genesis.combo.variant.EnumTree
import genesis.init.GenesisCreativeTabs
import genesis.util.BoundingBoxes
import genesis.util.WorldFlags
import net.minecraft.block.BlockBush
import net.minecraft.block.IGrowable
import net.minecraft.block.SoundType
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.event.terraingen.TerrainGen
import java.util.*

class BlockGenesisSapling(private val treeType: EnumTree) : BlockBush(), IGrowable {
    companion object {
        val STAGE: PropertyInteger = PropertyInteger.create("stage", 0, 1)
        const val STAGE_FLAG = 8 // = 1 << 3 = 8
    }

    init {
        defaultState = blockState.baseState.withProperty(STAGE, 0)
        setHardness(0.0f)
        soundType = SoundType.PLANT
        setCreativeTab(GenesisCreativeTabs.DECORATIONS)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return BoundingBoxes.SAPLING
    }

    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, random: Random) {
        if (!world.isRemote) {
            checkAndDropBlock(world, pos, state)
            if (world.getLightFromNeighbors(pos.up()) >= 9 && random.nextInt(7) == 0) {
                grow(world, pos, state, random)
            }
        }
    }

    private fun grow(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (state.getValue(STAGE) == 0) {
            world.setBlockState(pos, state.cycleProperty(STAGE), WorldFlags.PREVENT_RERENDER)
        } else {
            generateTree(world, pos, state, rand)
        }
    }

    private fun generateTree(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!TerrainGen.saplingGrowTree(world, rand, pos)) {
            return
        }
        val generator = treeType.getTreeGenerator(rand)
        if (generator != null) {
            val x = 0
            val z = 0

            clearArea(world, pos, x, z)

            if (!generator.generate(world, rand, pos.add(x, 0, z))) {
                clearArea(world, pos, x, z)
            }
        }
    }

    private fun clearArea(world: World, pos: BlockPos, x: Int, z: Int) {
        val air = Blocks.AIR.defaultState
        if (treeType.growsIntoLargeTree()) {
            world.setBlockState(pos.add(x, 0, z), air, WorldFlags.PREVENT_RERENDER)
            world.setBlockState(pos.add(x + 1, 0, z), air, WorldFlags.PREVENT_RERENDER)
            world.setBlockState(pos.add(x, 0, z + 1), air, WorldFlags.PREVENT_RERENDER)
            world.setBlockState(pos.add(x + 1, 0, z + 1), air, WorldFlags.PREVENT_RERENDER)
        } else {
            world.setBlockState(pos, air, WorldFlags.PREVENT_RERENDER)
        }
    }

    override fun damageDropped(state: IBlockState) = 0

    override fun canGrow(world: World, pos: BlockPos, state: IBlockState, isClient: Boolean) = true

    override fun canUseBonemeal(world: World, rand: Random, pos: BlockPos, state: IBlockState) = world.rand.nextFloat() < 0.45

    override fun grow(world: World, rand: Random, pos: BlockPos, state: IBlockState) {
        grow(world, pos, state, rand)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var state = defaultState
        state = state.withProperty(STAGE, (meta and STAGE_FLAG) / STAGE_FLAG)
        return state
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var meta = 0
        meta = meta or state.getValue(STAGE) * STAGE_FLAG
        return meta
    }

    override fun createBlockState() = BlockStateContainer(this, STAGE)
}
