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

package genesis.block.tile.storagebox

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityLockableLoot
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.common.util.Constants

class TileEntityStorageBox : TileEntityLockableLoot() {

    private val inventory: NonNullList<ItemStack> = NonNullList.withSize(27, ItemStack.EMPTY)

    private var adjacentChestChecked: Boolean = false
    private val adjacent: MutableMap<EnumFacing, TileEntityStorageBox?> = HashMap() //Might change this to four fields

    var connected: EnumFacing? = null //Which face our buddy's on

    private fun setNeighbor(facing: EnumFacing, te: TileEntityStorageBox?) {
        if (facing == connected && te == null) connected = null //We lost our buddy

        adjacent[facing] = te
    }

    fun getAdjacent(side: EnumFacing): TileEntityStorageBox? {
        val pos = this.pos.offset(side)

        (this.world.getTileEntity(pos) as? TileEntityStorageBox)?.let {
            it.setNeighbor(side.opposite, this)
            return it
        }

        return null
    }

    fun checkForAdjacent() {
        if (!this.adjacentChestChecked) {
            this.adjacentChestChecked = true

            EnumFacing.HORIZONTALS.forEach { setNeighbor(it, getAdjacent(it)) }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)

        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.inventory)
        }

        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName)
        }

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.inventory.clear()

        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.inventory)
        }

        if (compound.hasKey("CustomName", Constants.NBT.TAG_STRING)) {
            this.customName = compound.getString("CustomName")
        }
    }

    override fun isEmpty() = inventory.all { it.isEmpty }

    override fun getName(): String = if (this.hasCustomName()) this.customName else "genesis.container.storage_box"

    override fun getInventoryStackLimit() = 64

    override fun getItems(): NonNullList<ItemStack> = inventory

    override fun getSizeInventory() = 27

    override fun createContainer(playerInventory: InventoryPlayer, playerIn: EntityPlayer): Container = ContainerChest(playerInventory, this, playerIn)

    override fun getGuiID() = "genesis:storage_box"
}