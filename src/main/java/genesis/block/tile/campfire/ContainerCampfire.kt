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
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ContainerCampfire(playerInventory: InventoryPlayer, private val campfire: IInventory) : Container() {
    init {
        this.addSlotToContainer(Slot(campfire, TileEntityCampfire.inputSlot, 47, 44))
        this.addSlotToContainer(SlotCampfireFuel(campfire, TileEntityCampfire.fuelSlot, 47, 80))
        this.addSlotToContainer(SlotFurnaceOutput(playerInventory.player, campfire, TileEntityCampfire.outputSlot, 135, 34))

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
    private val properties: Array<Int> = Array(6, { 0 })

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        listener.sendAllWindowProperties(this, campfire)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()

        for (i in 0..5) {
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
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val stackInSlot = slot.stack
            itemstack = stackInSlot.copy()

            if (index == 2) {
                if (!this.mergeItemStack(stackInSlot, 3, 39, true))
                    return ItemStack.EMPTY

                slot.onSlotChange(stackInSlot, itemstack)
            } else if (index != 1 && index != 0) {
                if (!FurnaceRecipes.instance().getSmeltingResult(stackInSlot).isEmpty) {
                    if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (TileEntityFurnace.isItemFuel(stackInSlot)) {
                    if (!this.mergeItemStack(stackInSlot, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 3..29) {
                    if (!this.mergeItemStack(stackInSlot, 30, 39, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index in 30..38 && !this.mergeItemStack(stackInSlot, 3, 30, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(stackInSlot, 3, 39, false)) {
                return ItemStack.EMPTY
            }

            if (stackInSlot.isEmpty) slot.putStack(ItemStack.EMPTY)
            else slot.onSlotChanged()

            if (stackInSlot.count == itemstack.count) return ItemStack.EMPTY

            slot.onTake(playerIn, stackInSlot)
        }

        return itemstack
    }

    class SlotCampfireFuel(inventoryIn: IInventory, slotIndex: Int, xPosition: Int, yPosition: Int)
        : Slot(inventoryIn, slotIndex, xPosition, yPosition) {
        override fun isItemValid(stack: ItemStack) = TileEntityFurnace.isItemFuel(stack)
    }
}