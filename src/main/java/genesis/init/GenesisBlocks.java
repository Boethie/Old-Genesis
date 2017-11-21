package genesis.init;

import com.google.common.collect.Iterables;
import genesis.block.BlockGenesisFern;
import genesis.block.BlockGenesisLeaves;
import genesis.block.BlockGenesisLog;
import genesis.block.BlockGenesisSapling;
import genesis.combo.variant.EnumFern;
import genesis.item.ItemGenesisLeaves;
import genesis.combo.variant.EnumTree;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class GenesisBlocks {

    // ferns
    public static final Block DRYOPTERIS = new BlockGenesisFern(EnumFern.DRYOPTERIS).setRegistryName("dryopteris");
    public static final Block PHLEBOPTERIS = new BlockGenesisFern(EnumFern.PHLEBOPTERIS).setRegistryName("phlebopteris");
    public static final Block TODITES = new BlockGenesisFern(EnumFern.TODITES).setRegistryName("todites");

    // leaves
    public static final Block ARAUCARIOXYLON_LEAVES = new BlockGenesisLeaves(EnumTree.ARAUCARIOXYLON).setRegistryName("araucarioxylon_leaves");
    public static final Block DRYOPHYLLUM_LEAVES = new BlockGenesisLeaves(EnumTree.DRYOPHYLLUM).setRegistryName("dryophyllum_leaves");
    public static final Block FICUS_LEAVES = new BlockGenesisLeaves(EnumTree.FICUS).setRegistryName("ficus_leaves");
    public static final Block GINKGO_LEAVES = new BlockGenesisLeaves(EnumTree.GINKGO).setRegistryName("ginkgo_leaves");
    public static final Block METASEQUOIA_LEAVES = new BlockGenesisLeaves(EnumTree.METASEQUOIA).setRegistryName("metasequoia_leaves");

    // logs
    public static final Block ARAUCARIOXYLON_LOG = new BlockGenesisLog().setRegistryName("araucarioxylon_log");
    public static final Block DRYOPHYLLUM_LOG = new BlockGenesisLog().setRegistryName("dryophyllum_log");
    public static final Block FICUS_LOG = new BlockGenesisLog().setRegistryName("ficus_log");
    public static final Block GINKGO_LOG = new BlockGenesisLog().setRegistryName("ginkgo_log");
    public static final Block METASEQUOIA_LOG = new BlockGenesisLog().setRegistryName("metasequoia_log");

    // saplings
    public static final Block ARAUCARIOXYLON_SAPLING = new BlockGenesisSapling(EnumTree.ARAUCARIOXYLON).setRegistryName("araucarioxylon_sapling");
    public static final Block DRYOPHYLLUM_SAPLING = new BlockGenesisSapling(EnumTree.DRYOPHYLLUM).setRegistryName("dryophyllum_sapling");
    public static final Block FICUS_SAPLING = new BlockGenesisSapling(EnumTree.FICUS).setRegistryName("ficus_sapling");
    public static final Block GINKGO_SAPLING = new BlockGenesisSapling(EnumTree.GINKGO).setRegistryName("ginkgo_sapling");
    public static final Block METASEQUOIA_SAPLING = new BlockGenesisSapling(EnumTree.METASEQUOIA).setRegistryName("metasequoia_sapling");

    private static final Block[] blocks = getBlocks();


    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(blocks);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(getItemBlocks());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(final ModelRegistryEvent event) {
        for (Block block : blocks) {
            final Item item = Item.getItemFromBlock(block);
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }


    private static Block[] getBlocks() {
        final List<Block> blocks = new ArrayList<Block>();
        for (Field field : GenesisBlocks.class.getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            if (field.getType().isAssignableFrom(Block.class) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                try {
                    blocks.add((Block) field.get(null));
                } catch (Exception ex) {
                }
            }
        }
        return Iterables.toArray(blocks, Block.class);
    }

    private static final ItemBlock[] getItemBlocks() {
        final List<ItemBlock> items = new ArrayList<ItemBlock>();
        for (Block block : blocks) {
            ItemBlock item;
            if (block instanceof BlockGenesisLeaves) {
                item = new ItemGenesisLeaves((BlockLeaves) block);
            } else {
                item = new ItemBlock(block);
            }
            item.setRegistryName(block.getRegistryName());
            items.add(item);
        }
        return Iterables.toArray(items, ItemBlock.class);
    }

}
