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
package genesis.init;

import genesis.GenesisMod;
import genesis.config.Config;
import genesis.world.GenesisTeleporter;
import genesis.world.GenesisWorldProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Dimensions {

    public static final DimensionType GENESIS = DimensionType.register(GenesisMod.MOD_ID, "_" + GenesisMod.MOD_ID, Config.dimensionId,
            GenesisWorldProvider.class, false);

    public static void register() {
        DimensionManager.registerDimension(GENESIS.getId(), GENESIS);
    }

    public static boolean teleportToDimension(Entity entity, DimensionType dim) {
        // currently basic code for teleporting between dimensions
        if (!entity.world.isRemote) {
            int dimID = dim.getId();

            EntityPlayerMP player = null;

            if (entity instanceof EntityPlayerMP) {
                player = (EntityPlayerMP) entity;
            }

            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            PlayerList manager = server.getPlayerList();

            WorldServer newWorld = DimensionManager.getWorld(dimID);

            boolean teleported = false;
            if (player != null) {
                // Transfer the original player.
                manager.transferPlayerToDimension(player, dimID, new GenesisTeleporter(newWorld));
                teleported = true;
            }

            return teleported;
        }
        return true;
    }
}
