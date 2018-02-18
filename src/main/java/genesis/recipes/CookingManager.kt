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

package genesis.recipes

import genesis.GenesisMod
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryBuilder

@Mod.EventBusSubscriber(modid = GenesisMod.MOD_ID)
object CookingManager {
    private lateinit var REGISTRY: IForgeRegistry<ICookingPotRecipe>

    @SubscribeEvent
    @JvmStatic fun createRegistry(event: RegistryEvent.NewRegistry) {
        REGISTRY = RegistryBuilder<ICookingPotRecipe>().setType(ICookingPotRecipe::class.java).setName(ResourceLocation(GenesisMod.MOD_ID, "cooking")).create()

        val recipe = OrderedCookingPotRecipe(NonNullList.from(Ingredient.EMPTY, Ingredient.fromItem(Items.BAKED_POTATO)), 1, false, ItemStack(Items.POISONOUS_POTATO))
        recipe.registryName = ResourceLocation(GenesisMod.MOD_ID, "test")
        REGISTRY.register(recipe)
    }

    @JvmStatic fun findMatchingResult(pot: CookingPotInventory, world: World): ItemStack {
        for (recipe in REGISTRY) {
            if (recipe.matches(pot, world)) {
                return recipe.getCookingResult(pot, world)
            }
        }

        return ItemStack.EMPTY
    }

    @JvmStatic fun findMatchingRecipe(pot: CookingPotInventory, worldIn: World): ICookingPotRecipe? = REGISTRY.firstOrNull { it.matches(pot, worldIn) }
}