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

package genesis.item

import genesis.combo.variant.CropInfo
import genesis.init.GenesisCreativeTabs
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable

class ItemGenesisSeeds(private val crop: CropInfo, amount: Int, saturation: Float) : ItemFood(amount, saturation, false), IPlantable {
    init {
        creativeTab = GenesisCreativeTabs.FOOD
    }

    constructor(crop: CropInfo): this(crop, 0, 0.0f) {
        creativeTab = GenesisCreativeTabs.MISC
    }

    override fun getItemUseAction(stack: ItemStack): EnumAction {
        return if (getHealAmount(stack) <= 0) EnumAction.NONE else EnumAction.EAT
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (player.canEat(false) && !player.isSneaking) return EnumActionResult.PASS

        val stack = player.getHeldItem(hand)
        val state = worldIn.getBlockState(pos)
        return if (facing == EnumFacing.UP && player.canPlayerEdit(pos.offset(facing), facing, stack) && state.block.canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) && worldIn.isAirBlock(pos.up())) {
            worldIn.setBlockState(pos.up(), this.getPlant(worldIn, pos.up()))

            if (player is EntityPlayerMP) CriteriaTriggers.PLACED_BLOCK.trigger(player, pos.up(), stack)

            stack.shrink(1)
            EnumActionResult.SUCCESS
        } else {
            EnumActionResult.FAIL
        }
    }

    override fun getPlantType(world: IBlockAccess, pos: BlockPos): EnumPlantType {
        val block = crop.plant.block
        return if (block is IPlantable) block.getPlantType(world, pos) else EnumPlantType.Plains
    }

    override fun getPlant(world: IBlockAccess, pos: BlockPos): IBlockState = crop.plant
}