package me.desht.modularrouters.item.module;

import me.desht.modularrouters.block.tile.TileEntityItemRouter;
import me.desht.modularrouters.client.gui.module.GuiModule;
import me.desht.modularrouters.client.gui.module.GuiModuleActivator;
import me.desht.modularrouters.logic.compiled.CompiledActivatorModule;
import me.desht.modularrouters.logic.compiled.CompiledModule;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.List;

public class ActivatorModule extends ItemModule {
    public ActivatorModule(Properties props) {
        super(props);
    }

    @Override
    public void addSettingsInformation(ItemStack stack, List<ITextComponent> list) {
        super.addSettingsInformation(stack, list);

        CompiledActivatorModule cam = new CompiledActivatorModule(null, stack);
        list.add(new TextComponentString(
                TextFormatting.YELLOW + I18n.format("guiText.tooltip.activator.action") + ": "
                + TextFormatting.AQUA + I18n.format("itemText.activator.action." + cam.getActionType()))
        );
        if (cam.getActionType() != CompiledActivatorModule.ActionType.USE_ITEM_ON_ENTITY) {
            list.add(new TextComponentString(
                    TextFormatting.YELLOW + I18n.format("guiText.tooltip.activator.lookDirection") + ": "
                    + TextFormatting.AQUA + I18n.format("itemText.activator.direction." + cam.getLookDirection()))
            );
        }
        if (cam.isSneaking()) {
            list.add(new TextComponentString(
                    TextFormatting.YELLOW + I18n.format("guiText.tooltip.activator.sneak"))
            );
        }
    }

    @Override
    public CompiledModule compile(TileEntityItemRouter router, ItemStack stack) {
        return new CompiledActivatorModule(router, stack);
    }

    @Override
    public Class<? extends GuiModule> getGuiClass() {
        return GuiModuleActivator.class;
    }

    @Override
    public Color getItemTint() {
        return new Color(255, 255, 195);
    }
}
