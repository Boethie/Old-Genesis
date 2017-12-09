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
package genesis;

import genesis.init.GenesisBlocks;
import genesis.util.WorldFlags;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = GenesisMod.MOD_ID)
public class GenesisEventHandler {
    @SubscribeEvent
    public static void onUseHoe(UseHoeEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block == GenesisBlocks.HUMUS || block == GenesisBlocks.HUMUS_PATH) {
            world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!world.isRemote) {
                IBlockState farmland = GenesisBlocks.HUMUS_FARMLAND.getDefaultState();
                world.setBlockState(pos, farmland, WorldFlags.UPDATE_BLOCK_AND_CLIENT_AND_RERENDER_ON_MAIN);
            }

            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EnumFacing face = event.getFace();
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if (item instanceof ItemSpade) {
            if (player.canPlayerEdit(pos.offset(face), face, stack)) {
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();

                if (face != EnumFacing.DOWN && world.isAirBlock(pos.up()) && block == GenesisBlocks.HUMUS) {
                    world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if (!world.isRemote) {
                        IBlockState path = GenesisBlocks.HUMUS_PATH.getDefaultState();
                        world.setBlockState(pos, path, WorldFlags.UPDATE_BLOCK_AND_CLIENT_AND_RERENDER_ON_MAIN);
                        stack.damageItem(1, player);
                    }

                    event.setCanceled(true);
                }
            }
        }
    }
}
