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
import genesis.init.GenesisItems
import genesis.recipes.CookingManager
import genesis.recipes.CookingPotInventory
import genesis.util.set
import genesis.util.to
import net.minecraft.block.Block
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.EnumAction
import net.minecraft.item.Item
import net.minecraft.item.ItemBucket
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.crafting.Ingredient
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.ITickable
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

class TileEntityCampfire : TileEntityInventory(7), ITickable {

    companion object {
        const val inputSlot: Int = 0
        const val fuelSlot: Int = 1

        const val cookingSlot1: Int = 2
        const val cookingSlot2: Int = 3
        const val cookingSlot3: Int = 4

        const val outputSlot: Int = 5
        const val bowlOutputSlot: Int = 6

        const val maxCookTime: Int = 300
        const val maxWetTime: Int = 100

        private val allowedOutputs: ArrayList<Ingredient> = ArrayList()

        @JvmStatic fun isAllowedOutput(stack: ItemStack) = allowedOutputs.any { ingredient -> ingredient.apply(stack) }

        @JvmStatic fun addAllowedOutput(vararg items: Item) {
            allowedOutputs.add(Ingredient.fromItems(*items))
        }

        @JvmStatic fun addAllowedOutput(vararg items: ItemStack) {
            allowedOutputs.add(Ingredient.fromStacks(*items))
        }

        @JvmStatic fun isBurning(campfire: IInventory) = campfire.getField(0) > 0
        @JvmStatic fun isWet(campfire: IInventory) = campfire.getField(2) > 0

        @JvmStatic fun hasCookingPot(campfire: IInventory) = isItemCookingPot(campfire.getStackInSlot(inputSlot))

        @JvmStatic fun isItemFuel(stack: ItemStack): Boolean = TileEntityFurnace.isItemFuel(stack) && !stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)

        @JvmStatic fun canBurnItem(stack: ItemStack): Boolean {
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

        @JvmStatic fun isItemCookingPot(stack: ItemStack) = stack.item === GenesisItems.CERAMIC_BOWL_WATER
    }

    var cookTime: Int = 0
        set(value) { field = MathHelper.clamp(value, 0, maxCookTime) }
    var wetTime: Int = 0
        set(value) { field = MathHelper.clamp(value, 0, maxWetTime) }

    @JvmField var burnTime: Int = 0
    @JvmField var maxBurnTime: Int = 0

    var rainTime: Int = 0
        set(value) { field = Math.max(value, 0) }

    //Visual fields
    @JvmField var rotation: Float = 0.0f
    @JvmField var rotationLastTick: Float = 0.0f
    private var ticksExisted: Long = 0

    private var needsPacket = true

    val fuel: ItemStack get() = inventory[fuelSlot]

    val input: ItemStack get() = inventory[inputSlot]

    val output: ItemStack get() = inventory[outputSlot]

    val isBurning: Boolean get() = burnTime > 0

    val isWet: Boolean get() = burnTime > 0

    val hasFuel: Boolean get() = isItemFuel(fuel)

    val hasCookingPot: Boolean get() = isItemCookingPot(input)

    private val potInventory: CookingInventoryWrapper = CookingInventoryWrapper(this)

    fun tryLight(): Boolean = burnTime == 0 && wetTime == 0 && consumeFuel()

    fun douse(time: Int = maxWetTime) {
        wetTime = time

        if (burnTime > 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.6f, 1.0f)
            burnTime = 0

            world.checkLight(pos)
        }

        markDirty()
    }

    /**
     * Whether or not this campfire can cook the NON COOKING POT items
     */
    private fun canCookNormally(): Boolean {
        val input = input
        if (!canBurnItem(input)) return false

        val result = FurnaceRecipes.instance().getSmeltingResult(input)

        if (result.isEmpty) return false

        return isResultCompatible(output, result)
    }

    private fun canCookCookingPot(): Boolean {
        if (!hasCookingPot) return false

        CookingManager.findMatchingRecipe(potInventory, world)?.let {
            val result = it.getCookingResult(potInventory, world)

            if (result.isEmpty) return false

            return isResultCompatible(output, result)
        }
        cookTime = 0
        return false
    }

    private fun isResultCompatible(currentOutput: ItemStack, result: ItemStack) = when {
        currentOutput.isEmpty -> true
        !result.isItemEqual(currentOutput) -> false
        currentOutput.count + result.count <= inventoryStackLimit && currentOutput.count + result.count <= currentOutput.maxStackSize -> true
        else -> currentOutput.count + result.count <= result.maxStackSize
    }

    private fun cook() {
        val input = input
        val result = FurnaceRecipes.instance().getSmeltingResult(input)

        if (output.isEmpty) inventory[outputSlot] = result.copy()
        else inventory[outputSlot].count += result.count

        input.count--

        cookTime = 0

        markDirty()
    }

    private fun cookCookingPot() {
        CookingManager.findMatchingRecipe(potInventory, world)?.let {
            val result = it.getCookingResult(potInventory, world)

            if (output.isEmpty) inventory[outputSlot] = result.copy()
            else inventory[outputSlot].count += result.count

            it.consumeIngredients(potInventory, world)
        }

        cookTime = 0

        markDirty()
    }

    override fun update() {
        rotationLastTick = rotation

        val raining = world.isRainingAt(pos.up())

        if (raining) {
            if (rainTime++ > 400 && rainTime % 4 == 0) {
                if (++wetTime == 0) douse(0)
            }
        } else {
            rainTime -= 2
        }

        if (wetTime > 0) {
            burnTime = 0
            cookTime = 0
            if (!raining && rainTime < 400) wetTime--
        }

        if (burnTime > 0) {
            burnTime--

            if (canCookCookingPot() && cookTime++ == maxCookTime) cookCookingPot()
            else if (canCookNormally() && cookTime++ == maxCookTime) cook()

            if (burnTime == 0) consumeFuel()
        } else if (cookTime > 0) {
            cookTime -= 2
        }

        if (needsPacket) {
            sendDescriptionPacket()
            needsPacket = false
        }

        if (world.isRemote) {
            if (burnTime > 0 && canCookNormally() && !TileEntityCampfireRenderer.hasCookingModel(input))
                rotation += 1.5f
        }

        ticksExisted++
    }

    override fun markDirty() {
        super.markDirty()
        needsPacket = true
    }

    private fun consumeFuel(): Boolean {
        if (hasFuel) {
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

    private fun sendDescriptionPacket() {
        val state = world.getBlockState(pos)
        world.notifyBlockUpdate(pos, state, state, 11)
    }

    override fun writeData(compound: NBTTagCompound) {
        compound["cookTime"] = cookTime
        compound["wetTime"] = wetTime
        compound["burnTime"] = burnTime
        compound["maxBurnTime"] = maxBurnTime
        compound["rainTime"] = rainTime
        super.writeData(compound)
    }

    override fun readData(compound: NBTTagCompound) {
        cookTime = compound.getInteger("cookTime")
        wetTime = compound.getInteger("wetTime")
        burnTime = compound.getInteger("burnTime")
        maxBurnTime = compound.getInteger("maxBurnTime")
        rainTime = compound.getInteger("rainTime")
        super.readData(compound)
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        val compound = NBTTagCompound()
        compound["f"] = IntArray(5, { getField(it) })

        val input = input
        if (!input.isEmpty) compound["i"] = input

        val fuel = fuel
        if (!fuel.isEmpty) compound["b"] = fuel

        return SPacketUpdateTileEntity(pos, 0, compound)
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        val compound = pkt.nbtCompound
        compound.getIntArray("f").forEachIndexed { i, v -> setField(i, v) }

        inventory[inputSlot] = ItemStack(compound.getCompoundTag("i"))
        inventory[fuelSlot] = ItemStack(compound.getCompoundTag("b"))

        world.checkLight(pos)
    }

    override fun getGuiID() = "genesis:campfire"

    override fun createContainer(playerInventory: InventoryPlayer, playerIn: EntityPlayer): Container = ContainerCampfire(playerInventory, this)

    override fun getFieldCount() = 5

    override fun getField(id: Int): Int {
        return when (id) {
            0 -> burnTime
            1 -> maxBurnTime
            2 -> wetTime
            3 -> cookTime
            4 -> rainTime
            else -> 0
        }
    }

    override fun setField(id: Int, value: Int) {
        when (id) {
            0 -> burnTime = value
            1 -> maxBurnTime = value
            2 -> wetTime = value
            3 -> cookTime = value
            4 -> rainTime = value
        }
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        if (index == inputSlot && !inventory[inputSlot].isItemEqual(stack)) cookTime = 0

        if (!world.isRemote && index == inputSlot && stack.isEmpty) {
            val rangeXZ = 0.25 to 0.75
            val rangeY = 0.2 to 0.8
            for (i in cookingSlot1..cookingSlot3) {
                val stack1 = ItemStackHelper.getAndRemove(inventory, i)
                val entityItem = EntityItem(world, pos.x + rangeXZ.get(world.rand), pos.y + rangeY.get(world.rand), pos.z + rangeXZ.get(world.rand), stack1)
                entityItem.setVelocity(0.0, 0.0, 0.0)
                world.spawnEntity(entityItem)
            }
        }

        super.setInventorySlotContents(index, stack)
    }

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return when (index) {
            fuelSlot -> TileEntityFurnace.isItemFuel(stack) && stack.item !is ItemBucket
            inputSlot -> isItemCookingPot(stack) || canBurnItem(stack)
            else -> true
        }
    }

    class CookingInventoryWrapper(private val campfire: TileEntityCampfire) : CookingPotInventory {
        private val inputs: NonNullList<ItemStack> = NonNullList.withSize(3, ItemStack.EMPTY)
        override val ingredients: NonNullList<ItemStack> get() {
            inputs.clear()
            for ((index, i) in (cookingSlot1..cookingSlot3).withIndex()) inputs[index] = campfire.inventory[i]
            return inputs
        }

        override val cookingPotItem: ItemStack get() = campfire.input
        override val output: ItemStack get() = campfire.output
        override val fuel: ItemStack get() = campfire.fuel
    }
}