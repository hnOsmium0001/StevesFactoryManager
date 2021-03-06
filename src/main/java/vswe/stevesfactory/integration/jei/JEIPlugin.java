package vswe.stevesfactory.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.ui.intake.ItemIntakeGUI;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

@JeiPlugin
public final class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(StevesFactoryManager.MODID, "jei");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(FactoryManagerGUI.class, new JEIContainerHandler<>());
        registration.addGuiContainerHandler(ItemIntakeGUI.class, new JEIContainerHandler<>());
    }
}
