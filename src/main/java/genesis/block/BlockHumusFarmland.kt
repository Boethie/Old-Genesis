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

import genesis.init.GenesisBlocks
import genesis.util.WorldFlags
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable
import java.util.*

class BlockHumusFarmland : BlockHumusPathBase() {
    companion object {
        @JvmStatic
        val MOISTURE: PropertyInteger = PropertyInteger.create("moisture", 0, 7)
    }

    init {
        setHardness(0.5F)
        tickRandomly = true
    }

    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        val moisture = state.getValue(MOISTURE)
        if (!hasWater(world, pos) && !world.isRainingAt(pos.up())) {
            if (moisture > 0) {
                world.setBlockState(pos, state.withProperty(MOISTURE, moisture - 1), WorldFlags.UPDATE_CLIENT)
            } else if (!hasCrops(world, pos)) {
                turnToHumus(world, pos)
            }
        } else if (moisture < 7) {
            world.setBlockState(pos, state.withProperty(MOISTURE, 7), WorldFlags.UPDATE_CLIENT)
        }
    }

    override fun onFallenUpon(world: World, pos: BlockPos, entity: Entity, fallDistance: Float) {
        if (!world.isRemote && entity.canTrample(world, this, pos, fallDistance)) {
            turnToHumus(world, pos)
        }
        super.onFallenUpon(world, pos, entity, fallDistance)
    }

    private fun hasCrops(world: World, pos: BlockPos): Boolean {
        val aboveBlock = world.getBlockState(pos.up()).block
        return aboveBlock is IPlantable && canSustainPlant(world.getBlockState(pos), world, pos, EnumFacing.UP, aboveBlock)
    }

    private fun hasWater(world: World, pos: BlockPos): Boolean {
        for (mutablePos in BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (world.getBlockState(mutablePos).material == Material.WATER) {
                return true
            }
        }
        return false
    }

    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(MOISTURE, meta and 7)

    override fun getMetaFromState(state: IBlockState): Int = state.getValue(MOISTURE)

    override fun createBlockState() = BlockStateContainer(this, MOISTURE)

    override fun isFertile(world: World, pos: BlockPos) = world.getBlockState(pos).getValue(MOISTURE) > 0

    override fun canSustainPlant(state: IBlockState, world: IBlockAccess, pos: BlockPos, direction: EnumFacing, plantable: IPlantable): Boolean {
        val plantType = plantable.getPlantType(world, pos.offset(direction))
        return when (plantType) {
            EnumPlantType.Crop, EnumPlantType.Plains -> true
            else -> false
        }
    }

    override fun onPlantGrow(state: IBlockState, world: World, pos: BlockPos, source: BlockPos) {
        world.setBlockState(pos, GenesisBlocks.HUMUS.defaultState, WorldFlags.UPDATE_CLIENT)
    }
}