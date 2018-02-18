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

import com.google.gson.JsonObject
import genesis.util.removeFirst
import genesis.util.shrinkAll
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.registries.IForgeRegistryEntry

class ShapelessCookingPotRecipe(private val ingredients: NonNullList<Ingredient>,
                                private val consumePot: Boolean = false,
                                override val output: ItemStack = ItemStack.EMPTY)
    : ICookingPotRecipe, IForgeRegistryEntry.Impl<ICookingPotRecipe>() {

    override fun matches(pot: CookingPotInventory, world: World): Boolean {
        val unused = pot.ingredients.toMutableList() // Copy the inventory, we'll need to modify it to make sure we don't get a false positive on duplicate ingredients
        unused.removeAll { it.isEmpty } //We don't need to think about empty items
        return ingredients.all { ingredient -> unused.removeFirst { ingredient.apply(it) } } //Kotlin magic
    }

    override fun getCookingResult(pot: CookingPotInventory, world: World): ItemStack = output.copy()

    override fun consumeIngredients(pot: CookingPotInventory, world: World) {
        pot.ingredients.shrinkAll(1)
        pot.cookingPotItem.shrink(1)
    }

    companion object {
        @JvmStatic fun factory(context: JsonContext, json: JsonObject): OrderedCookingPotRecipe {
            TODO()
        }
    }
}