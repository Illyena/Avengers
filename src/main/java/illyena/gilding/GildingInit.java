package illyena.gilding;

import illyena.gilding.core.enchantment.GildingEnchantments;
import illyena.gilding.core.item.util.GildingItemGroups;
import illyena.gilding.core.networking.GildingPackets;
import illyena.gilding.core.particle.GildingParticles;
import illyena.gilding.core.util.GildingTags;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GildingInit implements ModInitializer {
	public static final String SUPER_MOD_ID = "gilding";
	public static final String SUPER_MOD_NAME = "Gilding";

	public static final Logger LOGGER = LogManager.getLogger(SUPER_MOD_NAME);

	@Override
	public void onInitialize() {
		LOGGER.info("Welcome to the " + SUPER_MOD_NAME + " Mod!");

		GildingTags.callGildingTags();
//		BlockSources.setBlockSources();
		GildingItemGroups.callGildingItemGroups();
		GildingEnchantments.callEnchantments();
		GildingParticles.callGildingParticles();

		GildingPackets.registerC2SPackets();

//		GildedInitializer.initialize();
//		IcingInitializer.initialize();
//		VerdureInitializer.initialize();



	}
}
