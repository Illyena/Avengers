package illyena.gilding.avengers.advancement;

import net.minecraft.advancement.criterion.Criteria;

import static illyena.gilding.avengers.AvengersInit.LOGGER;
import static illyena.gilding.avengers.AvengersInit.MOD_NAME;

public class AvengersAdvancements {
    public static void registerAdvancements() { LOGGER.info("Registering advancements for {} mod.", MOD_NAME); }

    public static final RicochetAndReturnCriterion RICOCHET_AND_RETURN = Criteria.register(new RicochetAndReturnCriterion());
    public static final NotWorthyCriterion NOT_WORTHY = Criteria.register(new NotWorthyCriterion());

}
