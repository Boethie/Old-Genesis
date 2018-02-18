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
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import java.util.*

operator fun NBTTagCompound.get(key: String): NBTBase = this.getTag(key)

operator fun NBTTagCompound.set(key: String, value: ItemStack) = this.setTag(key, value.serializeNBT())

operator fun NBTTagCompound.set(key: String, value: NBTBase) = this.setTag(key, value)

operator fun NBTTagCompound.set(key: String, value: Byte) = this.setByte(key, value)
operator fun NBTTagCompound.set(key: String, value: Short) = this.setShort(key, value)
operator fun NBTTagCompound.set(key: String, value: Int) = this.setInteger(key, value)
operator fun NBTTagCompound.set(key: String, value: Long) = this.setLong(key, value)
operator fun NBTTagCompound.set(key: String, value: Float) = this.setFloat(key, value)
operator fun NBTTagCompound.set(key: String, value: Double) = this.setDouble(key, value)
operator fun NBTTagCompound.set(key: String, value: Boolean) = this.setBoolean(key, value)

operator fun NBTTagCompound.set(key: String, value: String) = this.setString(key, value)

operator fun NBTTagCompound.set(key: String, value: UUID) = this.setUniqueId(key, value)

operator fun NBTTagCompound.set(key: String, value: IntArray) = this.setIntArray(key, value)
operator fun NBTTagCompound.set(key: String, value: ByteArray) = this.setByteArray(key, value)