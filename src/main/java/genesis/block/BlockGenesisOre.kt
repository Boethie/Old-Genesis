package genesis.block

import genesis.combo.variant.EnumOre
import genesis.init.GenesisCreativeTabs
import genesis.util.Harvest
import net.minecraft.block.BlockOre
import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

class BlockGenesisOre(private val oreType: EnumOre) : BlockOre() {

    init {
        setHardness(oreType.hardness)
        setResistance(oreType.resistance)
        setSoundType(SoundType.STONE)
        setCreativeTab(GenesisCreativeTabs.BUILDING_BLOCKS)
        setHarvestLevel(Harvest.CLASS_PICKAXE, Harvest.LEVEL_STONE)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int) = oreType.item

    override fun quantityDropped(random: Random) = MathHelper.getInt(random, oreType.dropMin, oreType.dropMax)

    override fun getExpDrop(state: IBlockState, world: IBlockAccess, pos: BlockPos, fortune: Int): Int {
        val rand = if (world is World) world.rand else RANDOM
        return MathHelper.getInt(rand, oreType.expMin, oreType.expMax)
    }
}