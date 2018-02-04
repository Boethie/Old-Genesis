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

import genesis.GenesisMod
import genesis.block.tile.campfire.TileEntityCampfire
import genesis.proxy.GenesisGuiHandler
import genesis.util.to
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.SoundType
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.templates.VoidFluidHandler
import java.util.*
import kotlin.collections.ArrayList

@Suppress("OverridingDeprecatedMember")
class BlockCampfire : BlockGenesis(Material.WOOD, MapColor.WOOD, SoundType.WOOD) {
    companion object {
        @JvmField val FACING: PropertyDirection = BlockHorizontal.FACING
        @JvmField val PILLAR = AxisAlignedBB(0.4375, 0.0, 0.4375, 0.5625, 1.0, 0.5625)
        @JvmField val STONES = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0)

        private val LIGHTERS: MutableList<Pair<Ingredient, SoundEvent>> = ArrayList()

        @JvmStatic fun addLighter(ingredient: Ingredient, soundEvent: SoundEvent) {
            LIGHTERS.add(Pair(ingredient, soundEvent))
        }

        @JvmStatic fun addLighter(ingredient: ItemStack, soundEvent: SoundEvent) {
            LIGHTERS.add(Pair(Ingredient.fromStacks(ingredient), soundEvent))
        }

        @JvmStatic fun addLighter(ingredient: Item, soundEvent: SoundEvent) {
            LIGHTERS.add(Pair(Ingredient.fromItem(ingredient), soundEvent))
        }

        @JvmStatic fun addLighter(ingredient: ItemStack): ItemStack {
            LIGHTERS.add(Pair(Ingredient.fromStacks(ingredient), SoundEvents.ITEM_FLINTANDSTEEL_USE))
            return ingredient
        }

        @JvmStatic fun addLighter(ingredient: Item) {
            LIGHTERS.add(Pair(Ingredient.fromItem(ingredient), SoundEvents.ITEM_FLINTANDSTEEL_USE))
        }
    }

    init {
        tickRandomly = true
        defaultState = blockState.baseState.withProperty(FACING, EnumFacing.NORTH)
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, STONES)

        val facing = state.getValue(FACING)
        addCollisionBoxToList(pos, entityBox, collidingBoxes, PILLAR.offset(facing.frontOffsetZ * 0.4375, 0.0, facing.frontOffsetX * 0.4375))
        addCollisionBoxToList(pos, entityBox, collidingBoxes, PILLAR.offset(facing.frontOffsetZ * -0.4375, 0.0, facing.frontOffsetX * -0.4375))
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val held = playerIn.getHeldItem(hand)

        val campfire = worldIn.getTileEntity(pos)

        if (campfire is TileEntityCampfire) {
            if (campfire.hasFuel()) {
                for ((lighter, sound) in LIGHTERS) {
                    if (lighter.apply(held)) {
                        if (campfire.tryLight()) {
                            if (!playerIn.isCreative) held.damageItem(1, playerIn)
                            worldIn.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f)
                        }
                        return true
                    }
                }
            }

            if (douse(campfire, worldIn, pos, playerIn, held, hand)) return true

            playerIn.openGui(GenesisMod.getInstance(), GenesisGuiHandler.CAMPFIRE, worldIn, pos.x, pos.y, pos.z)
        }

        return true
    }

    private fun douse(campfire: TileEntityCampfire, worldIn: World, pos: BlockPos, playerIn: EntityPlayer, held: ItemStack, hand: EnumHand): Boolean {
        val fluidHandler = FluidUtil.getFluidHandler(held)

        if (fluidHandler != null) {
            val rand = worldIn.rand

            val rangeXZ = 0.25 to 0.75
            val rangeY = 0.2 to 0.5
            val speedXZ = -0.08 to 0.08
            val speedY = 0.1 to 0.3

            val bigSmokeCount = 1
            val smokeCount = 25
            val waterCount = 50
            val maxCount = Math.max(bigSmokeCount, Math.max(smokeCount, waterCount))

            for (i in 0 until maxCount) {
                val x = pos.x + rangeXZ.get(rand)
                val y = pos.y + rangeY.get(rand)
                val z = pos.z + rangeXZ.get(rand)

                if (campfire.burnTime > 0) {
                    if (i < bigSmokeCount) worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0.0, 0.0, 0.0)

                    if (i < smokeCount) worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0)
                }

                if (i < waterCount) worldIn.spawnParticle(EnumParticleTypes.WATER_DROP, x, y, z, speedXZ.get(rand), speedY.get(rand), speedXZ.get(rand))
            }

            campfire.douse()

            val emptyContainer = FluidUtil.tryEmptyContainer(held, VoidFluidHandler(), 1000, playerIn, true)

            if (emptyContainer.isSuccess && !playerIn.isCreative) playerIn.setHeldItem(hand, emptyContainer.result)

            return true
        }

        return false
    }

    override fun randomDisplayTick(state: IBlockState, world: World, pos: BlockPos, rand: Random) {
        val campfire = world.getTileEntity(pos)

        if (campfire is TileEntityCampfire) {
            if (campfire.burnTime == 0) return

            var rangeXZ = 0.25 to 0.75
            var rangeY = 0.0 to 0.5

            for (i in 0..3) {
                val x = pos.x + rangeXZ.get(rand)
                val y = pos.y + rangeY.get(rand)
                val z = pos.z + rangeXZ.get(rand)

                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0)
            }

            val input = campfire.input

            if (!input.isEmpty) {
                rangeXZ = 0.4 to 0.6
                rangeY = 0.9 to 1.0

                for (i in 0..1) {
                    val x = pos.x + rangeXZ.get(rand)
                    val y = pos.y + rangeY.get(rand)
                    val z = pos.z + rangeXZ.get(rand)

                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0)
                }
            }
        }
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
        val facing = state.getValue(FACING)
        return when (face) {
            facing.rotateY() -> BlockFaceShape.MIDDLE_POLE_THIN
            facing.rotateYCCW() -> BlockFaceShape.MIDDLE_POLE_THIN
            else -> BlockFaceShape.UNDEFINED
        }
    }

    override fun getLightOpacity(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int = 1

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val campfire = world.getTileEntity(pos)

        return if (campfire is TileEntityCampfire && campfire.burnTime > 0) 15
        else 0
    }

    override fun createBlockState() = BlockStateContainer(this, FACING)

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState {
        return defaultState.withProperty(FACING, placer.horizontalFacing.opposite)
    }

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(FACING, EnumFacing.getHorizontal(meta))
    override fun getMetaFromState(state: IBlockState): Int = state.getValue(FACING).horizontalIndex

    override fun hasTileEntity(state: IBlockState) = true
    override fun createTileEntity(world: World, state: IBlockState) = TileEntityCampfire()

    override fun getBlockLayer() = BlockRenderLayer.CUTOUT

    override fun isOpaqueCube(state: IBlockState) = false
    override fun isFullCube(state: IBlockState) = false
}