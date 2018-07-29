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

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ContainerCampfire(playerInventory: InventoryPlayer, private val campfire: IInventory) : Container() {

    companion object {
        private const val playerStartIndex: Int = 7
        private const val playerHotBarIndex: Int = 34
        private const val playerEndIndex: Int = 43

        private val playerMain = playerStartIndex..playerHotBarIndex
        private val playerHotBar = playerHotBarIndex..playerEndIndex
        private val player = playerStartIndex..playerEndIndex

        private val campfireInputs = 0..TileEntityCampfire.cookingSlot3+1
        private val campfireIngredients = TileEntityCampfire.cookingSlot1..TileEntityCampfire.cookingSlot3+1
    }

    init {
        this.addSlotToContainer(Slot(campfire, TileEntityCampfire.inputSlot, 47, 44))
        this.addSlotToContainer(SlotCampfireFuel(campfire, TileEntityCampfire.fuelSlot, 47, 80))

        this.addSlotToContainer(SlotCampfireIngredients(campfire, TileEntityCampfire.cookingSlot1, 23, 18))
        this.addSlotToContainer(SlotCampfireIngredients(campfire, TileEntityCampfire.cookingSlot2, 47, 18))
        this.addSlotToContainer(SlotCampfireIngredients(campfire, TileEntityCampfire.cookingSlot3, 71, 18))

        this.addSlotToContainer(SlotFurnaceOutput(playerInventory.player, campfire, TileEntityCampfire.outputSlot, 135, 34))
        this.addSlotToContainer(Slot(campfire, TileEntityCampfire.bowlOutputSlot, 135, 64))

        for (y in 0..2) {
            for (x in 0..8) {
                this.addSlotToContainer(Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 113 + y * 18))
            }
        }

        for (i in 0..8) {
            this.addSlotToContainer(Slot(playerInventory, i, 8 + i * 18, 171))
        }
    }

    //Store fields in an array because we never need to access them directly
    private val properties: IntArray = IntArray(5, { 0 })

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        listener.sendAllWindowProperties(this, campfire)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()

        for (i in 0 until 5) {
            val field = campfire.getField(i)
            if (properties[i] != field) {
                for (listener in listeners) {
                    listener.sendWindowProperty(this, i, field)
                }
            }
            properties[i] = field
        }
    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        this.campfire.setField(id, data)
    }

    override fun canInteractWith(playerIn: EntityPlayer) = campfire.isUsableByPlayer(playerIn)

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var outStack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val stack = slot.stack
            outStack = stack.copy()

            if (index == TileEntityCampfire.outputSlot) {
                if (!merge(stack, player)) return ItemStack.EMPTY

                slot.onSlotChange(stack, outStack)
            } else if (index !in campfireInputs) {
                if (TileEntityCampfire.isItemFuel(stack))
                    merge(stack, TileEntityCampfire.fuelSlot)

                if (TileEntityCampfire.isItemCookingPot(stack) || !FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty) {
                    if (!merge(stack, TileEntityCampfire.inputSlot))
                        return ItemStack.EMPTY
                } else if (TileEntityCampfire.hasCookingPot(campfire)) { //TODO: Proper testing for ingredients
                    if (!merge(stack, campfireIngredients))
                        return ItemStack.EMPTY
                } else if (index in playerMain) {
                    if (!merge(stack, playerHotBar))
                        return ItemStack.EMPTY
                } else if (index in playerHotBar && !merge(stack, playerMain))
                    return ItemStack.EMPTY
            } else if (!merge(stack, player))
                return ItemStack.EMPTY

            if (stack.isEmpty) slot.putStack(ItemStack.EMPTY)
            else slot.onSlotChanged()

            if (stack.count == outStack.count) return ItemStack.EMPTY

            slot.onTake(playerIn, stack)
        }

        return outStack
    }

    private fun merge(stack: ItemStack, slot: Int) = this.mergeItemStack(stack, slot, slot + 1, false)
    private fun merge(stack: ItemStack, range: IntRange) = this.mergeItemStack(stack, range.start, range.endInclusive, false)

    class SlotCampfireFuel(inventoryIn: IInventory, slotIndex: Int, xPosition: Int, yPosition: Int)
        : Slot(inventoryIn, slotIndex, xPosition, yPosition) {
        override fun isItemValid(stack: ItemStack) = TileEntityCampfire.isItemFuel(stack)
    }

    class SlotCampfireIngredients(inventoryIn: IInventory, slotIndex: Int, xPosition: Int, yPosition: Int)
        : Slot(inventoryIn, slotIndex, xPosition, yPosition) {

        override fun isEnabled() = TileEntityCampfire.hasCookingPot(inventory)
    }
}