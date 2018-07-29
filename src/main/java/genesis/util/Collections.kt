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

package genesis.util

import net.minecraft.item.ItemStack

/**
 * Removes the first item that satisfies the [predicate] and returns true
 * Returns false if none
 */
public inline fun <T> MutableIterable<T>.removeFirst(predicate: (T) -> Boolean): Boolean {
    val each = this.iterator()
    while (each.hasNext()) {
        if (predicate(each.next())) {
            each.remove()
            return true
        }
    }
    return false
}

/**
 * Returns true if [n] members of the collection satisfy the [predicate]
 */
public inline fun <T> Iterable<T>.atLeast(n: Int, predicate: (T) -> Boolean): Boolean {
    val each = this.iterator()
    var count = 0
    while (each.hasNext()) {
        if (predicate(each.next()) && ++count >= n) {
            return true
        }
    }
    return false
}

/**
 * Shrinks all the [ItemStack]s in the collection by [n]
 */
public inline fun Iterable<ItemStack>.shrinkAll(n: Int) { this.forEach { it.shrink(n) } }

public fun Iterable<ItemStack>.shrinkAllIf(n: Int, predicate: (ItemStack) -> Boolean) {
    this.forEach { if (predicate(it)) it.shrink(n) }
}