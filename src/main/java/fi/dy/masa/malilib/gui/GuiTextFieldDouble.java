package fi.dy.masa.malilib.gui;

import java.util.regex.Pattern;

import net.minecraft.client.gui.Font;

public class GuiTextFieldDouble extends GuiTextFieldGeneric {
	private static final Pattern PATTER_NUMBER = Pattern.compile("^-?([0-9]+(\\.[0-9]*)?)?");

	public GuiTextFieldDouble(int x, int y, int width, int height, Font fontRenderer) {
		super(x, y, width, height, fontRenderer);

		this.setFilter(input -> input.length() <= 0 || PATTER_NUMBER.matcher(input).matches());
	}
}
