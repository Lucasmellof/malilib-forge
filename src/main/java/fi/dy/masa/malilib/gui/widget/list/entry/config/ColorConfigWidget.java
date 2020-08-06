package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.WidgetColorIndicator;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;

public class ColorConfigWidget extends BaseConfigOptionWidget<ColorConfig>
{
    protected final ColorConfig config;
    protected final WidgetColorIndicator colorIndicatorWidget;
    protected final WidgetTextFieldBase textField;
    protected final int initialValue;

    public ColorConfigWidget(int x, int y, int width, int height, int listIndex, ColorConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getIntegerValue();

        this.colorIndicatorWidget = new WidgetColorIndicator(x, y, 18, 18, this.config, (newValue) -> {
            this.config.setIntegerValue(newValue);
            this.reAddSubWidgets();
        });

        this.textField = new WidgetTextFieldBase(x, y, 80, 16, this.config.getStringValue());
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.reAddSubWidgets();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY();
        int elementWidth = this.gui.getConfigElementsWidth();

        this.colorIndicatorWidget.setPosition(x, y + 2);
        this.textField.setPosition(x + this.colorIndicatorWidget.getWidth() + 4, y + 3);
        this.updateResetButton(x + elementWidth + 4, y + 1, this.config);

        this.addWidget(this.colorIndicatorWidget);
        this.addWidget(this.textField);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getIntegerValue() != this.initialValue;
    }
}
