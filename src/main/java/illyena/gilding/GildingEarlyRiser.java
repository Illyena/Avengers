package illyena.gilding;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.world.Heightmap;

import java.util.function.Predicate;

public class GildingEarlyRiser implements Runnable{

    @Override
    public void run() {
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String enchantmentTarget = mappingResolver.mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("THROWABLE", "illyena.gilding.core.enchantment.ThrowableTarget").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("PROJECTILE", "illyena.gilding.core.enchantment.ProjectileTarget").build();

    }
}
