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
package genesis

import genesis.init.GenesisBlocks
import genesis.util.WorldFlags
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemSpade
import net.minecraft.util.EnumFacing
import net.minecraft.util.SoundCategory
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.entity.player.UseHoeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = GenesisMod.MOD_ID)
object GenesisEventHandler {
    @SubscribeEvent
    @JvmStatic fun onUseHoe(event: UseHoeEvent) {
        val player = event.entityPlayer
        val world = event.world
        val pos = event.pos
        val state = world.getBlockState(pos)
        val block = state.block

        if (block === GenesisBlocks.HUMUS || block === GenesisBlocks.HUMUS_PATH) {
            world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0f, 1.0f)

            if (!world.isRemote) {
                val farmland = GenesisBlocks.HUMUS_FARMLAND.defaultState
                world.setBlockState(pos, farmland, WorldFlags.UPDATE_BLOCK_AND_CLIENT_AND_RERENDER_ON_MAIN)
            }

            event.result = Event.Result.ALLOW
        }
    }

    @SubscribeEvent
    @JvmStatic fun onRightClickBlock(event: PlayerInteractEvent.RightClickBlock) {
        val player = event.entityPlayer
        val world = event.world
        val pos = event.pos
        val face = event.face
        val stack = event.itemStack
        val item = stack.item

        if (item is ItemSpade) {
            if (player.canPlayerEdit(pos.offset(face!!), face, stack)) {
                val state = world.getBlockState(pos)
                val block = state.block

                if (face != EnumFacing.DOWN && world.isAirBlock(pos.up()) && block === GenesisBlocks.HUMUS) {
                    world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0f, 1.0f)

                    if (!world.isRemote) {
                        val path = GenesisBlocks.HUMUS_PATH.defaultState
                        world.setBlockState(pos, path, WorldFlags.UPDATE_BLOCK_AND_CLIENT_AND_RERENDER_ON_MAIN)
                        stack.damageItem(1, player)
                    }

                    event.isCanceled = true
                }
            }
        }
    }
}
