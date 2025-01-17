package fi.dy.masa.malilib.gui.wrappers;

import com.mojang.blaze3d.vertex.PoseStack;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.util.KeyCodes;

public class TextFieldWrapper<T extends GuiTextFieldGeneric> {
	private final T textField;
	private final ITextFieldListener<T> listener;

	public TextFieldWrapper(T textField, ITextFieldListener<T> listener) {
		this.textField = textField;
		this.listener = listener;
	}

	public T getTextField() {
		return this.textField;
	}

	public ITextFieldListener<T> getListener() {
		return this.listener;
	}

	public boolean isFocused() {
		return this.textField.isFocused();
	}

	public void setFocused(boolean isFocused) {
		this.textField.setFocused(isFocused);
	}

	public void onGuiClosed() {
		if (this.listener != null) {
			this.listener.onGuiClosed(this.textField);
		}
	}

	public void draw(int mouseX, int mouseY, PoseStack matrixStack) {
		this.textField.render(matrixStack, mouseX, mouseY, 0f);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return this.textField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
		String textPre = this.textField.getValue();

		if (this.textField.isFocused() && this.textField.keyPressed(keyCode, scanCode, modifiers)) {
			if (this.listener != null &&
					    (keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_TAB ||
							     !this.textField.getValue().equals(textPre))) {
				this.listener.onTextChange(this.textField);
			}

			return true;
		}

		return false;
	}

	public boolean onCharTyped(char charIn, int modifiers) {
		String textPre = this.textField.getValue();

		if (this.textField.isFocused() && this.textField.charTyped(charIn, modifiers)) {
			if (this.listener != null && !this.textField.getValue().equals(textPre)) {
				this.listener.onTextChange(this.textField);
			}

			return true;
		}

		return false;
	}
}
