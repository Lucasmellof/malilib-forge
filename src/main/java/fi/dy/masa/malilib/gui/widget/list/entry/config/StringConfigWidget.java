package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;

public class StringConfigWidget extends BaseConfigOptionWidget<StringConfig>
{
    protected final StringConfig config;
    protected final String initialValue;
    protected final WidgetTextFieldBase textField;

    public StringConfigWidget(int x, int y, int width, int height, int listIndex, StringConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getStringValue();

        this.textField = new WidgetTextFieldBase(x, y, 20, 16, this.config.getStringValue());
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.textField.setText(this.config.getStringValue());
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY() + 1;
        int elementWidth = this.gui.getConfigElementsWidth();

        this.textField.setPosition(x, y + 2);
        this.textField.setWidth(elementWidth);
        this.textField.setText(this.config.getStringValue());

        this.updateResetButton(x + elementWidth + 4, y, this.config);

        this.addWidget(this.textField);
        this.addWidget(this.resetButton);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getStringValue().equals(this.initialValue) == false;
    }
}
