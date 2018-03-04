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
import net.minecraft.block.*
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.IPlantable
import java.util.*

//This doesn't extend BlockCrop because it has too much in common with both BlockCrop and BlockDoublePlant
//It makes more sense to fall back to the common superclass
@Suppress("OverridingDeprecatedMember")
class BlockDoubleCrop(private val crop: EnumCrop) : BlockBush(), IGrowable {
    companion object {
        @JvmField val HALF: PropertyEnum<EnumBlockHalf> = BlockDoublePlant.HALF
        @JvmField val AGE: PropertyInteger = BlockCrops.AGE
    }

    private val boxes: Array<AxisAlignedBB> = Array(16, { i ->
        val height = crop.getHeight(i and 0b111)

        val nearCorner = 0.5 - crop.width * 0.5
        val farCorner = 0.5 + crop.width * 0.5

        return@Array when {
            (i and 0b1000) == 0 -> AxisAlignedBB(nearCorner, 0.0, nearCorner, farCorner, height - 0.0625, farCorner)    //Lower half, always breaks the top half
            crop.breakTogether -> AxisAlignedBB(nearCorner, -1.0, nearCorner, farCorner, height - 1.0625, farCorner)    //Upper half, when it breaks the bottom with it
            else -> AxisAlignedBB(nearCorner, 0.0, nearCorner, farCorner, height - 1.0625, farCorner)                   //Upper half when it can break independently
        }
    })

    init {
        tickRandomly = true
        setCreativeTab(null)
        setHardness(0.0f)
        soundType = SoundType.PLANT
        disableStats()
        defaultState = blockState.baseState.withProperty(HALF, EnumBlockHalf.LOWER).withProperty(AGE, 0)
    }

    fun getMaxAge() = crop.growthStages
    private fun getAge(state: IBlockState): Int = state.getValue(AGE)
    private fun isMaxAge(state: IBlockState): Boolean = state.getValue(AGE) == crop.growthStages

    private fun getBonemealAgeIncrease(worldIn: World): Int = MathHelper.getInt(worldIn.rand, 2, 5)

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        this.checkAndDropBlock(worldIn, pos, state)

        if (state.getValue(HALF) === EnumBlockHalf.UPPER) return //Without this it would start growing faster when it gets older

        if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
            val i = this.getAge(state)

            if (i < crop.growthStages) {
                val f = getGrowthChance(worldIn, pos) * crop.growthMultiplier

                if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((25.0f / f).toInt() + 1) == 0)) {
                    this.doGrowth(1, worldIn, pos, state)
                    ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos))
                }
            }
        }
    }

    private fun getGrowthChance(worldIn: World, pos: BlockPos): Float {
        var chance = 1.0f
        val down = pos.down()
        val soilPos = BlockPos.MutableBlockPos(down)

        for (x in -1..1) {
            for (z in -1..1) {
                var chanceBonus = 0.0f

                soilPos.setPos(down.x + x, down.y, down.z + z)

                val soil = worldIn.getBlockState(soilPos)

                if (soil.block.canSustainPlant(soil, worldIn, soilPos, EnumFacing.UP, this as IPlantable)) {
                    chanceBonus = if (soil.block.isFertile(worldIn, soilPos)) 3.0f else 1.0f
                }

                if (x != 0 || z != 0) chanceBonus /= 4.0f

                chance += chanceBonus
            }
        }

        val north = pos.north()
        val south = pos.south()
        val west = pos.west()
        val east = pos.east()
        val neighborX = this === worldIn.getBlockState(west).block || this === worldIn.getBlockState(east).block
        val neighborZ = this === worldIn.getBlockState(north).block || this === worldIn.getBlockState(south).block

        if (neighborX && neighborZ) {
            chance /= 2.0f
        } else {
            val diagonalNeighbors =
                    this === worldIn.getBlockState(west.north()).block ||
                            this === worldIn.getBlockState(east.north()).block ||
                            this === worldIn.getBlockState(east.south()).block ||
                            this === worldIn.getBlockState(west.south()).block

            if (diagonalNeighbors) chance /= 2.0f
        }

        return chance
    }

    private fun doGrowth(growth: Int, worldIn: World, pos: BlockPos, state: IBlockState) {
        if (growth == 0) return

        val oldAge = getAge(state)
        var newAge = oldAge + growth
        if (newAge > crop.growthStages) {
            newAge = crop.growthStages
        }

        //Prevent it from growing if there isn't room
        if (crop.growthAge in (oldAge + 1)..newAge && worldIn.getBlockState(pos.up()).block !== Blocks.AIR && worldIn.getBlockState(pos.up()).block !== this) {
            newAge = crop.growthAge - 1
        }

        val state = state.withProperty(AGE, newAge)
        worldIn.setBlockState(pos, state, 2)

        if (newAge >= crop.growthAge) {
            if (state.getValue(HALF) === EnumBlockHalf.LOWER)
                worldIn.setBlockState(pos.up(), state.withProperty(HALF, EnumBlockHalf.UPPER), 2)
            else
                worldIn.setBlockState(pos.down(), state.withProperty(HALF, EnumBlockHalf.LOWER), 2)
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return if (state.getValue(HALF) === EnumBlockHalf.UPPER && isMaxAge(state)) crop.crop else crop.seed
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack {
        return ItemStack(crop.seed)
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        super.getDrops(drops, world, pos, state, fortune)

        if (state.getValue(HALF) === EnumBlockHalf.LOWER || !this.isMaxAge(state)) return //The good stuff only comes from the top

        val age = getAge(state)
        val rand = if (world is World) world.rand else Random()

        for (i in 0 until 3 + fortune) {
            if (rand.nextInt(2 * crop.growthStages) <= age * crop.seedDropMultiplier) {
                drops.add(ItemStack(crop.seed, 1, 0))
            }
        }
    }

    override fun onBlockHarvested(worldIn: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
        if (state.getValue(HALF) == EnumBlockHalf.UPPER) {
            if (worldIn.getBlockState(pos.down()).block === this) {
                if (crop.breakTogether)
                    worldIn.setBlockState(pos.down(), Blocks.AIR.defaultState, 2)
                else
                    worldIn.setBlockState(pos.down(), defaultState.withProperty(AGE, crop.growthAge - 1), 2)
            }
        } else if (worldIn.getBlockState(pos.up()).block === this) {
            if (player.isCreative)
                worldIn.setBlockState(pos.up(), Blocks.AIR.defaultState, 2)
            else
                worldIn.destroyBlock(pos.up(), true)
        }
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        if (state.block !== this) return super.canBlockStay(worldIn, pos, state)

        if (state.getValue(HALF) === EnumBlockHalf.LOWER) {
            val soil = worldIn.getBlockState(pos.down())
            val soilValid = (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && soil.block.canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this)

            return if (crop.breakTogether && state.getValue(AGE) >= crop.growthAge) {
                val upperHalf = worldIn.getBlockState(pos.up())
                soilValid && upperHalf.block === this && getAge(upperHalf) == getAge(state)
            } else soilValid
        }

        val lowerHalf = worldIn.getBlockState(pos.down())
        return lowerHalf.block === this && getAge(lowerHalf) == getAge(state)
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (state.getValue(AGE) >= crop.growthAge && state.getValue(HALF) === EnumBlockHalf.LOWER) {
            val block = worldIn.getBlockState(pos.up()).block
            if (block === this || block === Blocks.AIR)
                worldIn.setBlockState(pos.up(), state.withProperty(HALF, EnumBlockHalf.UPPER), 2)
            else
                worldIn.setBlockState(pos, state.withProperty(AGE, crop.growthAge - 1), 2)
        }
        super.onBlockAdded(worldIn, pos, state)
    }

    override fun canUseBonemeal(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState) = true
    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean) = !this.isMaxAge(state)

    override fun grow(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState) {
        doGrowth(this.getBonemealAgeIncrease(worldIn), worldIn, pos, state)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = boxes[getMetaFromState(state)]

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState = defaultState

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, AGE, HALF)

    //Store the age in the first three bits and the half in the fourth
    override fun getMetaFromState(state: IBlockState): Int {
        val half = if (state.getValue(HALF) === EnumBlockHalf.LOWER) 0 else 0b1000
        return getAge(state) or half
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState
                .withProperty(AGE, meta and 0b0111)
                .withProperty(HALF, if ((meta and 0b1000) == 0) EnumBlockHalf.LOWER else EnumBlockHalf.UPPER)
    }
}