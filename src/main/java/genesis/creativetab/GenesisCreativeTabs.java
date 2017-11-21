package genesis.creativetab;

import genesis.GenesisMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GenesisCreativeTabs {

    public static final CreativeTabs BLOCKS = new CreativeTabs(GenesisMod.MOD_ID + ".blocks") {
        @SideOnly(Side.CLIENT)
        @Override
        public ItemStack getTabIconItem() {
            return ItemStack.EMPTY;
        }
    };


    public static final CreativeTabs DECORATIONS = new CreativeTabs(GenesisMod.MOD_ID + ".decorations") {
        @SideOnly(Side.CLIENT)
        @Override
        public ItemStack getTabIconItem() {
            return ItemStack.EMPTY;
        }
    };
}
