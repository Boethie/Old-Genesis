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
package genesis.block

import genesis.init.GenesisCreativeTabs
import net.minecraft.block.BlockLog
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState

class BlockGenesisLog : BlockLog() {
    companion object {
        const val LOG_X_AXIS_FLAG = 4 // = 4
        const val LOG_Z_AXIS_FLAG = 8 // = 8
        const val LOG_NO_AXIS_FLAG = LOG_X_AXIS_FLAG or LOG_Z_AXIS_FLAG // = 12
    }

    init {
        defaultState = blockState.baseState.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y)
        setCreativeTab(GenesisCreativeTabs.BUILDING_BLOCKS)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var state = defaultState
        state = when (meta and LOG_NO_AXIS_FLAG) {
            0 -> state.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Y)
            LOG_X_AXIS_FLAG -> state.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X)
            LOG_Z_AXIS_FLAG -> state.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z)
            else -> state.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE)
        }
        return state
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var meta = 0
        when (state.getValue(BlockLog.LOG_AXIS)) {
            BlockLog.EnumAxis.X -> meta = meta or LOG_X_AXIS_FLAG
            BlockLog.EnumAxis.Z -> meta = meta or LOG_Z_AXIS_FLAG
            BlockLog.EnumAxis.NONE -> meta = meta or LOG_NO_AXIS_FLAG
        }
        return meta
    }

    override fun createBlockState() = BlockStateContainer(this, BlockLog.LOG_AXIS)
}
