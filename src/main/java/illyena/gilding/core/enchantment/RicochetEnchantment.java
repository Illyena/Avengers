package illyena.gilding.core.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Optional;

public class RicochetEnchantment extends Enchantment {
    public ItemStack thrownItem;

    protected RicochetEnchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot... slotTypes) {
        super(weight, type, slotTypes);
    }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public int getMinPower(int level) { return level * 25; }

    @Override
    public int getMaxPower(int level) { return this.getMinPower(level) + 50; }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.PUNCH;
    }

    @Override
    public boolean isTreasure() { return true; }

    @Override
    public boolean isAvailableForEnchantedBookOffer() { return false; }

    @Override
    public boolean isAvailableForRandomSelection() { return true; }
}
