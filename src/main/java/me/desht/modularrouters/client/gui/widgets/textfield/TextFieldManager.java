package me.desht.modularrouters.client.gui.widgets.textfield;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Handles a collection of TextFieldWidget objects, sending events and managing Tab-focus etc.
 */
public class TextFieldManager {
    private final List<TextFieldWidgetMR> textFields = Lists.newArrayList();
    private int focusedField = -1;

    public TextFieldManager() {
    }

    public void drawTextFields(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        GlStateManager._disableBlend();
        textFields.forEach(tf -> tf.renderButton(matrixStack, mouseX, mouseY, partialTicks));
    }

    public void tick() {
        if (focusedField >= 0) {
            textFields.get(focusedField).tick();
        }
    }

    public boolean mouseClicked(double x, double y, int btn) {
        for (int i = 0; i < textFields.size(); i++) {
            if (textFields.get(i).mouseClicked(x, y, btn)) {
                focus(i);
                return true;
            }
        }
        focus(-1);
        return false;
    }

    public boolean mouseScrolled(double wheel) {
        if (wheel == 0) {
            return false;
        } else if (isFocused() && textFields.get(focusedField).isVisible()) {
            textFields.get(focusedField).onMouseWheel(wheel < 0 ? -1 : 1);
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         if (keyCode == GLFW.GLFW_KEY_TAB) {
             boolean wasTextfieldFocused = isFocused();
             cycleFocus(Screen.hasShiftDown() ? -1 : 1);
             return wasTextfieldFocused;
        } else if (isFocused() && textFields.get(focusedField).isVisible()) {
            return textFields.get(focusedField).keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }


    public boolean charTyped(char c, int modifiers) {
        return isFocused() && textFields.get(focusedField).isVisible()
                && textFields.get(focusedField).charTyped(c, modifiers);
    }

    int addTextField(TextFieldWidgetMR textField) {
        textFields.add(textField);
        return textFields.size() - 1;
    }

    public void focus(int field) {
        if (field != focusedField) {
            if (focusedField != -1) {
                textFields.get(focusedField).setFocused(false);
            }
            focusedField = field;
            if (focusedField != -1) {
                textFields.get(focusedField).setFocused(true);
            }
        }
    }

    private void cycleFocus(int dir) {
        int count = 0;
        int newFocused = focusedField;
        int oldFocused = newFocused;

        // take care here not to end up in an infinite loop if there are no suitable fields to cycle to...
        do {
            newFocused += dir;
            if (newFocused < 0) {
                newFocused = textFields.size() - 1;
            } else if (newFocused >= textFields.size()) {
                newFocused = 0;
            }
        } while (++count < textFields.size() && !textFields.get(newFocused).isVisible() && newFocused != focusedField);

        if (newFocused != oldFocused) {
            focus(newFocused);
            if (oldFocused >= 0 && oldFocused < textFields.size()) {
                textFields.get(oldFocused).setHighlightPos(textFields.get(oldFocused).getCursorPosition());
            }
        }
    }

    public boolean isFocused() {
        return focusedField >= 0;
    }

    void onTextFieldFocusChange(int ordinal, boolean newFocus) {
        TextFieldWidgetMR tf = textFields.get(ordinal);
        if (newFocus) {
            focus(ordinal);
            tf.moveCursorToEnd();
            tf.setHighlightPos(0);
        } else {
            tf.setHighlightPos(tf.getCursorPosition());
        }
    }

    public TextFieldManager clear() {
        textFields.clear();
        focusedField = -1;
        return this;
    }
}
