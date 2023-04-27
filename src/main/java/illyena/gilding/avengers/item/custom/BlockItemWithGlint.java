package illyena.gilding.avengers.item.custom;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class BlockItemWithGlint extends BlockItem {
    public BlockItemWithGlint(Block block, Settings settings) {
        super(block, settings);
    }

    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
