package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.gui.DirectorySelectorScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.util.FileUtils;

public class DirectoryConfigWidget extends BaseConfigOptionWidget<File, DirectoryConfig>
{
    public DirectoryConfigWidget(int x, int y, int width, int height, int listIndex,
                                 int originalListIndex, DirectoryConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        final File rootDir = FileUtils.getRootDirectory();
        final File file = FileUtils.getCanonicalFileIfPossible(this.config.getValue().getAbsoluteFile());
        final File dir = file == null || file.isDirectory() == false ? (file != null ? file.getParentFile() : rootDir) : file;

        FileSelectorScreenFactory factory = () -> new DirectorySelectorScreen(dir, rootDir, (d) -> {
            this.config.setValueFromString(d.getAbsolutePath());
            this.reAddSubWidgets();
        });

        this.createFileSelectorWidgets(this.getY(), this.config, factory,
                                       "malilib.gui.button.config.select_directory",
                                       "malilib.gui.button.config.hover.selected_directory");
    }
}
