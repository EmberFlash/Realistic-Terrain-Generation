package rtg.client.gui;


import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class RTGConfigGUIFactory implements IModGuiFactory
{
    public static final String LOCATION = "rtg.client.gui.RTGConfigGUIFactory";
    @Override public void initialize(Minecraft minecraftInstance) {}
    @Override public boolean hasConfigGui() { return true; }
    @Override public GuiScreen createConfigGui(GuiScreen parentScreen) { return new RTGConfigGUI(parentScreen); }
    @Nullable @Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {return null;}
}
