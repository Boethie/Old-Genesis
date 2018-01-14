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

import genesis.combo.variant.EnumCrop
import net.minecraft.block.BlockCrops
import net.minecraft.block.BlockDoublePlant
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf
import net.minecraft.block.IGrowable
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import java.util.*

@Suppress("OverridingDeprecatedMember")
class BlockDoubleCrop(private val crop: EnumCrop) : BlockCrops(), IGrowable {
    companion object {
        val HALF: PropertyEnum<EnumBlockHalf> = BlockDoublePlant.HALF
        val AGE: PropertyInteger = BlockCrops.AGE
    }

    private val boxes: Array<AxisAlignedBB>

    init {
        defaultState = defaultState.withProperty(HALF, EnumBlockHalf.LOWER)

        boxes = Array(16, { i ->
            val height = crop.heightFunc(i and 0b111)

            val nearCorner = 0.5 - crop.width * 0.5
            val farCorner = 0.5 + crop.width * 0.5

            when {
                (i and 0b1000) != 0 -> AxisAlignedBB(nearCorner, 0.0, nearCorner, farCorner, height - 0.0625, farCorner)    //Lower half, always breaks the top half
                crop.breakTogether -> AxisAlignedBB(nearCorner, -1.0, nearCorner, farCorner, height - 1.0625, farCorner)    //Upper half, when it breaks the bottom with it
                else -> AxisAlignedBB(nearCorner, 0.0, nearCorner, farCorner, height - 1.0625, farCorner)                   //Upper half when it can break independently
            }
        })
    }

    override fun getSeed(): Item = crop.seed()
    override fun getCrop(): Item = crop.crop()
    override fun getMaxAge(): Int = crop.growthStages

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        this.checkAndDropBlock(worldIn, pos, state)

        if (state.getValue(HALF) === EnumBlockHalf.UPPER) return

        if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
            val i = this.getAge(state)

            if (i < this.maxAge) {
                val f = getGrowthChance(this, worldIn, pos) * crop.growthMultiplier

                if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((25.0f / f).toInt() + 1) == 0)) {
                    this.doGrowth(1, worldIn, pos, state)
                    ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos))
                }
            }
        }
    }

    override fun grow(worldIn: World, pos: BlockPos, state: IBlockState) {
        doGrowth(this.getBonemealAgeIncrease(worldIn), worldIn, pos, state)
    }

    private fun doGrowth(growth: Int, worldIn: World, pos: BlockPos, state: IBlockState) {
        if (growth == 0) return

        val oldAge = state.getValue(AGE)
        var newAge = oldAge + growth
        if (newAge > this.maxAge) {
            newAge = this.maxAge
        }

        //Prevent it from growing if there isn't room
        if (crop.growthAge in (oldAge+1)..newAge && worldIn.getBlockState(pos.up()).block !== Blocks.AIR) {
            newAge = crop.growthAge - 1
        }

        val state = state.withProperty(AGE, newAge)
        worldIn.setBlockState(pos, state, 2)

        if (newAge >= crop.growthAge) {
            if (state.getValue(HALF) === EnumBlockHalf.LOWER) {
                worldIn.setBlockState(pos.up(), state.withProperty(HALF, EnumBlockHalf.UPPER), 2)
            } else {
                worldIn.setBlockState(pos.down(), state.withProperty(HALF, EnumBlockHalf.LOWER), 2)
            }
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return if (state.getValue(HALF) === EnumBlockHalf.UPPER && this.isMaxAge(state)) this.getCrop() else this.seed
    }

    override fun onBlockHarvested(worldIn: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
        if (state.getValue(HALF) == EnumBlockHalf.UPPER) {
            if (worldIn.getBlockState(pos.down()).block === this) {
                if (crop.breakTogether) {
                    worldIn.setBlockToAir(pos.down())
                } else {
                    worldIn.setBlockState(pos.down(), defaultState.withProperty(AGE, crop.growthAge - 1), 2)
                }
            }
        } else if (worldIn.getBlockState(pos.up()).block === this) {
            worldIn.destroyBlock(pos.up(), true)
        }
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        if (state.block !== this) return super.canBlockStay(worldIn, pos, state)

        if (state.getValue(HALF) === EnumBlockHalf.LOWER) {
            return if (crop.breakTogether && state.getValue(AGE) >= crop.growthAge) {
                val upperHalf = worldIn.getBlockState(pos.up())
                super.canBlockStay(worldIn, pos, state) && upperHalf.block === this && upperHalf.getValue(AGE) == state.getValue(AGE)
            } else
                super.canBlockStay(worldIn, pos, state)
        }

        val lowerHalf = worldIn.getBlockState(pos.down())
        return lowerHalf.block === this && lowerHalf.getValue(AGE) == state.getValue(AGE)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = boxes[getMetaFromState(state)]

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState = defaultState

    override fun createBlockState() = BlockStateContainer(this, AGE, HALF)

    //Store the age in the first three bits and the half in the fourth
    override fun getMetaFromState(state: IBlockState) = state.getValue(AGE) or (state.getValue(HALF).ordinal shl 3)

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState
                .withProperty(AGE, meta and 0b111)
                .withProperty(HALF, if ((meta and 0b1000) != 0) EnumBlockHalf.LOWER else EnumBlockHalf.UPPER)
    }
}