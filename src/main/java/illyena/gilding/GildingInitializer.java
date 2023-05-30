package illyena.gilding;

import illyena.gilding.core.enchantment.GildingEnchantments;
import net.fabricmc.api.ModInitializer;

public class GildingInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		GildingEnchantments.callEnchantments();
	}
}
