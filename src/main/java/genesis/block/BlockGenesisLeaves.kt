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
import net.minecraft.block.BlockLeaves
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

class BlockGenesisLeaves(private val treeType: EnumTree) : BlockLeaves() {
    companion object {
        const val DECAYABLE_FLAG = 4 // = 4
        const val CHECK_DECAY_FLAG = 8 // = 8
    }

    init {
        defaultState = blockState.baseState.withProperty(BlockLeaves.CHECK_DECAY, true).withProperty(BlockLeaves.DECAYABLE, true)
        setCreativeTab(GenesisCreativeTabs.DECORATIONS)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var state = defaultState
        state = state.withProperty(BlockLeaves.DECAYABLE, meta and DECAYABLE_FLAG == 0)
        state = state.withProperty(BlockLeaves.CHECK_DECAY, meta and CHECK_DECAY_FLAG > 0)
        return state
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var meta = 0
        if (!state.getValue(BlockLeaves.DECAYABLE)) meta = meta or DECAYABLE_FLAG
        if (state.getValue(BlockLeaves.CHECK_DECAY)) meta = meta or CHECK_DECAY_FLAG
        return meta
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE)
    }

    override fun quantityDropped(random: Random) = if (random.nextInt(40) == 0) 1 else 0

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item = Item.getItemFromBlock(treeType.sapling)


    override fun onSheared(item: ItemStack, world: IBlockAccess, pos: BlockPos, fortune: Int): NonNullList<ItemStack> {
        return NonNullList.withSize(1, ItemStack(this))
    }

    override fun getWoodType(meta: Int) = null

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        setGraphicsLevel(Minecraft.isFancyGraphicsEnabled())
        return super.getBlockLayer()
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        setGraphicsLevel(Minecraft.isFancyGraphicsEnabled())
        return super.isOpaqueCube(state)
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        setGraphicsLevel(Minecraft.isFancyGraphicsEnabled())
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side)
    }
}
