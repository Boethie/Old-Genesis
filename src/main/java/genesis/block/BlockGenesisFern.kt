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

import genesis.util.BoundingBoxes
import net.minecraft.block.IGrowable
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IShearable
import java.util.*


class BlockGenesisFern : BlockPlant(Material.VINE), IGrowable, IShearable {
    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) = BoundingBoxes.FERN

    override fun isReplaceable(world: IBlockAccess, pos: BlockPos) = true

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int) = null

    override fun quantityDroppedWithBonus(fortune: Int, rand: Random) = 1 + rand.nextInt(fortune * 2 + 1)

    override fun canGrow(world: World, pos: BlockPos, state: IBlockState, isClient: Boolean) = true

    override fun canUseBonemeal(world: World, rand: Random, pos: BlockPos, state: IBlockState) = true

    override fun grow(world: World, rand: Random, pos: BlockPos, state: IBlockState) {
        // TODO: grow into double fern
    }

    override fun getOffsetType() = EnumOffsetType.XYZ

    override fun isShearable(item: ItemStack, world: IBlockAccess, pos: BlockPos) = true

    override fun onSheared(item: ItemStack, world: IBlockAccess, pos: BlockPos, fortune: Int) = NonNullList.withSize(1, ItemStack(this))
}