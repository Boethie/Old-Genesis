package genesis.block

import genesis.init.GenesisCreativeTabs
import genesis.util.Harvest
import net.minecraft.block.Block
import net.minecraft.block.BlockFalling
import net.minecraft.block.SoundType
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

open class BlockGenesis(material: Material, mapColor: MapColor, soundType: SoundType) : Block(material, mapColor) {
    init {
        setCreativeTab(GenesisCreativeTabs.BUILDING_BLOCKS)
        setSoundType(soundType)
    }
}

open class BlockGenesisRock(hardness: Float, resistance: Float) : Block(Material.ROCK) {
    init {
        setHardness(hardness)
        setResistance(resistance)
        setSoundType(SoundType.STONE)
        setCreativeTab(GenesisCreativeTabs.BUILDING_BLOCKS)
        setHarvestLevel(Harvest.CLASS_PICKAXE, Harvest.LEVEL_WOOD)
    }
}

class BlockSilt(private val mapColor: MapColor) : BlockFalling() {
    init {
        setHardness(0.5F)
        setSoundType(SoundType.SAND)
        setCreativeTab(GenesisCreativeTabs.BUILDING_BLOCKS)
        setHarvestLevel(Harvest.CLASS_SHOVEL, Harvest.LEVEL_WOOD)
    }

    override fun getMapColor(state: IBlockState, world: IBlockAccess, pos: BlockPos) = mapColor
}

class BlockCrackedSilt(mapColor: MapColor) : BlockGenesis(Material.SAND, mapColor, SoundType.SAND) {
    init {
        setHardness(0.6F)
        setHarvestLevel(Harvest.CLASS_SHOVEL, Harvest.LEVEL_WOOD)
    }
}

class BlockSiltstone(mapColor: MapColor) : BlockGenesis(Material.ROCK, mapColor, SoundType.STONE) {
    init {
        setHardness(0.9F)
        setHarvestLevel(Harvest.CLASS_PICKAXE, Harvest.LEVEL_WOOD)
    }
}

class BlockOoze : BlockGenesis(Material.CLAY, MapColor.LIME, SoundType.GROUND) {
    init {
        slipperiness = 0.88F
        setHardness(0.6F)
        setHarvestLevel(Harvest.CLASS_SHOVEL, Harvest.LEVEL_WOOD)
    }
}