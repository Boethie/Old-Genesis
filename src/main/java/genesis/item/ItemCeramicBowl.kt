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

import genesis.init.GenesisItems
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

class ItemCeramicBowl : ItemGenesis() {

    private val isWaterBowl: Boolean by lazy { this === GenesisItems.CERAMIC_BOWL_WATER }

    override fun getItemUseAction(stack: ItemStack) = if (isWaterBowl) EnumAction.DRINK else EnumAction.NONE

    override fun getMaxItemUseDuration(stack: ItemStack) = 32

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val held = player.getHeldItem(hand)

        if (isWaterBowl) {
            player.activeHand = hand
            return ActionResult(EnumActionResult.SUCCESS, held)
        }

        // We don't trust that it always returns non-null
        val hit: RayTraceResult? = this.rayTrace(world, player, true)

        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            val hitPos = hit.blockPos

            if (!world.isBlockModifiable(player, hitPos) || !player.canPlayerEdit(hitPos.offset(hit.sideHit), hit.sideHit, held))
                return ActionResult(EnumActionResult.FAIL, held)

            if (world.getBlockState(hitPos).material === Material.WATER) {
                StatList.getObjectUseStats(this)?.let { player.addStat(it) } //Get the linter to shut up about nullables

                val filledStack = ItemStack(GenesisItems.CERAMIC_BOWL_WATER)

                if (--held.count <= 0) return ActionResult(EnumActionResult.SUCCESS, filledStack)

                if (!player.inventory.addItemStackToInventory(filledStack)) {
                    player.dropItem(filledStack, false)
                }

                return ActionResult(EnumActionResult.SUCCESS, held)
            }
        }

        return ActionResult(EnumActionResult.FAIL, held)
    }

    override fun onItemUseFinish(stack: ItemStack, world: World, entityLiving: EntityLivingBase): ItemStack {
        if (isWaterBowl) {
            val empty = ItemStack(GenesisItems.CERAMIC_BOWL)

            if (--stack.count <= 0) return empty

            (entityLiving as? EntityPlayer)?.let { player ->
                if (!player.inventory.addItemStackToInventory(empty))
                    player.dropItem(empty, false)
            }
        }

        return stack
    }
}