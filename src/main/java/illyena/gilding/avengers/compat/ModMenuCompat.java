package illyena.gilding.avengers.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import illyena.gilding.avengers.client.gui.screen.AvengersConfigMenu;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() { return AvengersConfigMenu::new; }

}
