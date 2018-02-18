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

package genesis.block.tile

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.world.ILockableContainer
import net.minecraft.world.LockCode
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.wrapper.InvWrapper

abstract class TileEntityInventory(private val size: Int) : TileEntity(), ILockableContainer {
    protected val inventory: NonNullList<ItemStack> = NonNullList.withSize(size, ItemStack.EMPTY)

    final override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        writeData(compound)
        return super.writeToNBT(compound)
    }

    final override fun readFromNBT(compound: NBTTagCompound) {
        readData(compound)
        super.readFromNBT(compound)
    }

    open fun writeData(compound: NBTTagCompound) {
        ItemStackHelper.saveAllItems(compound, inventory)
    }

    open fun readData(compound: NBTTagCompound) {
        ItemStackHelper.loadAllItems(compound, inventory)
    }

    override fun hasCustomName() = false

    override fun getName() = ""

    override fun getStackInSlot(index: Int) = inventory[index]

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        markDirty()
        return ItemStackHelper.getAndSplit(inventory, index, count)
    }

    override fun clear() = inventory.clear()

    override fun getSizeInventory() = size

    override fun isEmpty() = inventory.all { stack -> stack.isEmpty }

    override fun isItemValidForSlot(index: Int, stack: ItemStack) = true

    override fun getInventoryStackLimit(): Int = 64

    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        return if (this.world.getTileEntity(this.pos) !== this)
            false
        else
            getDistanceSq(player.posX, player.posY, player.posZ) <= 64.0
    }

    override fun openInventory(player: EntityPlayer) {}

    override fun closeInventory(player: EntityPlayer) {}

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        inventory[index] = stack
        markDirty()
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        markDirty()
        return ItemStackHelper.getAndRemove(inventory, index)
    }

    override fun getFieldCount() = 0

    override fun getField(id: Int) = 0

    override fun setField(id: Int, value: Int) {}

    private var code: LockCode = LockCode.EMPTY_CODE

    override fun setLockCode(code: LockCode) {
        this.code = code
    }

    override fun getLockCode(): LockCode = code

    override fun isLocked() = !this.code.isEmpty

    private val wrapper: InvWrapper by lazy { InvWrapper(this) }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) = capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            capability.cast(wrapper)
        else
            super.getCapability(capability, facing)
    }
}