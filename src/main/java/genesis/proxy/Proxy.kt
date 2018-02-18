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
package genesis.proxy

import genesis.block.BlockCampfire
import genesis.block.tile.campfire.TileEntityCampfire
import genesis.combo.variant.EnumOre
import genesis.combo.variant.EnumTree
import genesis.config.Config
import genesis.init.Dimensions
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLInterModComms
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry

open class Proxy {
    open fun preInit(event: FMLPreInitializationEvent) {
        Config.init(event.suggestedConfigurationFile)
        Dimensions.register()
        TileEntity.register("genesis:campfire", TileEntityCampfire::class.java)
    }

    open fun init(event: FMLInitializationEvent) {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "genesis.compat.top.PluginGenesis")
        BlockCampfire.addLighter(Items.FLINT_AND_STEEL)

        registerFurnaceRecipes()
    }

    open fun postInit(event: FMLPostInitializationEvent) {}

    private fun registerFurnaceRecipes() {
        val charcoal = ItemStack(Items.COAL, 1, 1)
        EnumTree.values().forEach { GameRegistry.addSmelting(it.log, charcoal, 0.15f) }

        EnumOre.values().forEach { GameRegistry.addSmelting(it.block, ItemStack(it.item), it.expFurnace) }
    }
}
