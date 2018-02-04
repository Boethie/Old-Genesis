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

package genesis.block.tile.campfire

import genesis.block.BlockCampfire
import genesis.block.tile.TileEntityInventory
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.IInventory
import net.minecraft.item.EnumAction
import net.minecraft.item.Item
import net.minecraft.item.ItemBucket
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.crafting.Ingredient
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.ITickable
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB

class TileEntityCampfire : TileEntityInventory(3), ITickable {

    companion object {
        const val inputSlot: Int = 0
        const val fuelSlot: Int = 1
        const val outputSlot: Int = 2

        const val maxCookTime: Int = 300
        const val maxWetTime: Int = 100

        private val allowedOutputs: ArrayList<Ingredient> = ArrayList()

        @JvmStatic fun isAllowedOutput(stack: ItemStack) = allowedOutputs.any { ingredient -> ingredient.apply(stack) }

        @JvmStatic fun addAllowedOutput(stack: ItemStack): ItemStack {
            allowedOutputs.add(Ingredient.fromStacks(stack))
            return stack
        }

        @JvmStatic fun addAllowedOutput(item: Item) {
            allowedOutputs.add(Ingredient.fromItem(item))
        }

        @JvmStatic fun addAllowedOutput(vararg items: Item) {
            allowedOutputs.add(Ingredient.fromItems(*items))
        }

        @JvmStatic fun addAllowedOutput(vararg items: ItemStack) {
            allowedOutputs.add(Ingredient.fromStacks(*items))
        }

        @JvmStatic fun isBurning(campfire: IInventory) = campfire.getField(0) > 0
        @JvmStatic fun isWet(campfire: IInventory) = campfire.getField(2) > 0
        fun canBurnItem(stack: ItemStack): Boolean {
            if (stack.itemUseAction == EnumAction.EAT) return true

            if (Block.getBlockFromItem(stack.item) === Blocks.CACTUS) return true

            val smeltResult = FurnaceRecipes.instance().getSmeltingResult(stack)

            if (!smeltResult.isEmpty) {
                if (smeltResult.itemUseAction == EnumAction.EAT) return true

                if (smeltResult.item === Items.COAL) return true

                if (isAllowedOutput(smeltResult)) return true
            }

            return false
        }
    }

    var cookTime: Int = 0
    var wetTime: Int = 0
    var burnTime: Int = 0
    var maxBurnTime: Int = 0

    //Visual fields
    var rotation: Float = 0.0f
    var rotationLastTick: Float = 0.0f

    override fun getField(id: Int): Int {
        return when (id) {
            0 -> burnTime
            1 -> maxBurnTime
            2 -> wetTime
            3 -> maxWetTime
            4 -> cookTime
            5 -> maxCookTime
            else -> 0
        }
    }

    override fun setField(id: Int, value: Int) {
        when (id) {
            0 -> burnTime = value
            1 -> maxBurnTime = value
            2 -> wetTime = value
            4 -> cookTime = value
        }
    }

    fun isBurning(): Boolean = burnTime > 0

    fun isWet(): Boolean = burnTime > 0

    fun hasFuel(): Boolean = !inventory[fuelSlot].isEmpty

    val fuel: ItemStack
        get() = inventory[fuelSlot]

    val input: ItemStack
        get() = inventory[inputSlot]

    val output: ItemStack
        get() = inventory[outputSlot]

    fun tryLight(): Boolean = burnTime == 0 && wetTime == 0 && consumeFuel()

    fun douse() {
        wetTime = 100

        if (burnTime > 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.6f, 1.0f)
            burnTime = 0

            world.checkLight(pos)
        }

        markDirty()
    }

    private fun canCook(): Boolean {
        val input = input
        if (input.isEmpty) return false

        //TODO add logic for pot cooking
        val result = FurnaceRecipes.instance().getSmeltingResult(input)

        if (result.isEmpty) return false

        val currentOutput = output

        return when {
            currentOutput.isEmpty -> true
            !result.isItemEqual(currentOutput) -> false
            currentOutput.count + result.count <= inventoryStackLimit && currentOutput.count + result.count <= currentOutput.maxStackSize -> true
            else -> currentOutput.count + result.count <= result.maxStackSize
        }
    }

    override fun update() {
        rotationLastTick = rotation

        if (wetTime > 0) {
            burnTime = 0
            cookTime = 0
            wetTime--
        }

        if (burnTime > 0) {

            burnTime--

            if (canCook()) {
                if (++cookTime == maxCookTime) {
                    val input = input
                    val result = FurnaceRecipes.instance().getSmeltingResult(input)

                    if (output.isEmpty)
                        inventory[outputSlot] = result.copy()
                    else
                        inventory[outputSlot].count += result.count
                    input.count--

                    cookTime = 0
                }
            }

            if (burnTime == 0) {
                consumeFuel()
            }
        } else if (cookTime > 0) {
            cookTime -= 2
        }

        if (world.isRemote) {

            if (burnTime > 0 && canCook() && !TileEntityCampfireRenderer.hasCookingModel(input))
                rotation += 1.5f
        }
    }

    private fun consumeFuel(): Boolean {
        if (hasFuel()) {
            maxBurnTime = TileEntityFurnace.getItemBurnTime(inventory[fuelSlot])

            burnTime = maxBurnTime
            inventory[fuelSlot].shrink(1)

            world.checkLight(pos)
            markDirty()
            return true
        }
        return false
    }

    override fun getRenderBoundingBox(): AxisAlignedBB {
        val pos = pos
        val state = world.getBlockState(pos)
        val facing = state.getValue(BlockCampfire.FACING)

        return AxisAlignedBB(pos.offset(facing.rotateYCCW()), pos.offset(facing.rotateY()).add(1, 2, 1))
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("cookTime", cookTime)
        compound.setInteger("wetTime", wetTime)
        compound.setInteger("burnTime", burnTime)
        compound.setInteger("maxBurnTime", maxBurnTime)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        cookTime = compound.getInteger("cookTime")
        wetTime = compound.getInteger("wetTime")
        burnTime = compound.getInteger("burnTime")
        maxBurnTime = compound.getInteger("maxBurnTime")
        super.readFromNBT(compound)
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        if (index == inputSlot && !inventory[inputSlot].isItemEqual(stack)) cookTime = 0

        super.setInventorySlotContents(index, stack)
    }

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return when (index) {
            fuelSlot -> TileEntityFurnace.isItemFuel(stack) && stack.item !is ItemBucket
            inputSlot -> canBurnItem(stack)
            outputSlot -> false
            else -> true
        }
    }
}