package genesis.item;

import net.minecraft.block.BlockLeaves;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemStack;

public class ItemGenesisLeaves extends ItemLeaves {

    public ItemGenesisLeaves(BlockLeaves block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName();
    }

}
