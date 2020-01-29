package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.gui.interfaces.IBackgroundRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class WidgetBase
{
    private static final ArrayListMultimap<Long, String> DEBUG_STRINGS = ArrayListMultimap.create();
    public static final IBackgroundRenderer DEBUG_TEXT_BG_RENDERER = (x, y, w, h) -> { RenderUtils.drawOutlinedBox(x - 2, y - 2, w + 4, h + 4, 0xC0000000, 0xFFC0C0C0); };

    protected final Minecraft mc;
    protected final FontRenderer textRenderer;
    protected final List<String> hoverStrings = new ArrayList<>();
    protected final int fontHeight;
    protected int x;
    protected int y;
    protected int xRight;
    protected int width;
    protected int height;
    protected float zLevel;
    protected boolean rightAlign;

    public WidgetBase(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mc = Minecraft.getMinecraft();
        this.textRenderer = this.mc.fontRenderer;
        this.fontHeight = this.textRenderer.FONT_HEIGHT;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setPosition(int x, int y)
    {
        this.setX(x);
        this.setY(y);
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setRightX(int x)
    {
        this.xRight = x;
        this.updatePositionIfRightAligned();
    }

    public void setRightAlign(boolean rightAlign, int xRight)
    {
        this.rightAlign = rightAlign;

        if (rightAlign)
        {
            this.setRightX(xRight);
        }
    }

    protected void updatePositionIfRightAligned()
    {
        if (this.rightAlign)
        {
            this.x = this.xRight - this.width;

            if (this.x < 0)
            {
                this.xRight += -this.x;
                this.x = 0;
            }
        }
    }

    public void setZLevel(float zLevel)
    {
        this.zLevel = zLevel;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void setWidth(int width)
    {
        this.width = width;
        this.updatePositionIfRightAligned();
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isMouseOver(mouseX, mouseY))
        {
            return this.onMouseClickedImpl(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.onMouseReleasedImpl(mouseX, mouseY, mouseButton);
    }

    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
    }

    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.isMouseOver(mouseX, mouseY))
        {
            return this.onMouseScrolledImpl(mouseX, mouseY, mouseWheelDelta);
        }

        return false;
    }

    public boolean onMouseScrolledImpl(int mouseX, int mouseY, double mouseWheelDelta)
    {
        return false;
    }

    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        return this.onKeyTypedImpl(typedChar, keyCode);
    }

    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        return false;
    }

    /**
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return true;
    }

    public boolean hasHoverText()
    {
        return this.hoverStrings.isEmpty() == false;
    }

    public void clearHoverStrings()
    {
        this.hoverStrings.clear();
    }

    public void addHoverStrings(String... hoverStrings)
    {
        this.addHoverStrings(Arrays.asList(hoverStrings));
    }

    public void addHoverStrings(List<String> hoverStrings)
    {
        for (String str : hoverStrings)
        {
            this.addHoverString(str);
        }
    }

    public void addHoverString(@Nullable String translationKey, Object... args)
    {
        if (translationKey != null)
        {
            String str = StringUtils.translate(translationKey, args);

            String[] parts = str.split("\\\\n");

            for (String part : parts)
            {
                this.hoverStrings.add(part);
            }
        }
    }

    public List<String> getHoverStrings()
    {
        return this.hoverStrings;
    }

    public void bindTexture(ResourceLocation texture)
    {
        RenderUtils.bindTexture(texture);
    }

    public int getStringWidth(String text)
    {
        return this.textRenderer.getStringWidth(text);
    }

    public void drawString(int x, int y, int color, String text)
    {
        this.textRenderer.drawString(text, x, y, color);
    }

    public void drawCenteredString(int x, int y, int color, String text)
    {
        this.textRenderer.drawString(text, x - this.getStringWidth(text) / 2, y, color);
    }

    public void drawStringWithShadow(int x, int y, int color, String text)
    {
        this.textRenderer.drawStringWithShadow(text, x, y, color);
    }

    public void drawCenteredStringWithShadow(int x, int y, int color, String text)
    {
        this.textRenderer.drawStringWithShadow(text, x - this.getStringWidth(text) / 2, y, color);
    }

    public void render(int mouseX, int mouseY, boolean selected)
    {
    }

    public boolean shouldRenderHoverInfo(int mouseX, int mouseY)
    {
        return this.canSelectAt(mouseX, mouseY, 0);
    }

    public void postRenderHovered(int mouseX, int mouseY, boolean selected)
    {
        if (this.hasHoverText() && this.shouldRenderHoverInfo(mouseX, mouseY))
        {
            RenderUtils.drawHoverText(mouseX, mouseY, this.getHoverStrings());
            RenderUtils.disableItemLighting();
        }
    }

    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        int x = this.x;
        int y = this.y;
        double z = this.zLevel;
        int w = this.width;
        int h = this.height;
        int color = 0xFFFF4040;

        if (hovered || renderAll)
        {
            renderDebugOutline(x, y, z, w, h, color, hovered);
        }

        if (hovered || infoAlways)
        {
            int posX = infoAlways ? x      : mouseX;
            int posY = infoAlways ? y - 12 : mouseY;
            addDebugText(posX, posY, x, y, z, w, h, color, this.getClass().getName());
        }
    }

    public static void renderDebugOutline(double x, double y, double z, double w, double h, int color, boolean hovered)
    {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >>  8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        float lineWidth = hovered ? 2f : 1.0f;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(lineWidth);

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(x    , y    , z).color(r, g, b, a).endVertex();
        buffer.pos(x    , y + h, z).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y    , z).color(r, g, b, a).endVertex();

        tessellator.draw();

        GlStateManager.enableTexture2D();
    }

    public static void addDebugText(int mouseX, int mouseY, int x, int y, double z, int w, int h, int color, String text)
    {
        String str = String.format("x: %d .. %d, y: %d .. %d, z: %.1f w: %d, h: %d - %s", x, x + w - 1, y, y + h - 1, z, w, h, text);
        int posY = mouseY - 2;

        Long posLong = Long.valueOf((long) posY << 32 | (long) mouseX);
        DEBUG_STRINGS.put(posLong, str);
    }

    public static void renderDebugTextAndClear()
    {
        if (DEBUG_STRINGS.isEmpty() == false)
        {
            for (Long posLong : DEBUG_STRINGS.keySet())
            {
                int x = (int) (posLong.longValue() & 0xFFFFFFFF);
                int y = (int) ((posLong.longValue() >>> 32) & 0xFFFFFFFF);
                RenderUtils.drawHoverText(x, y, DEBUG_STRINGS.get(posLong), 0xFFFF4040, DEBUG_TEXT_BG_RENDERER);
            }

            DEBUG_STRINGS.clear();
            RenderUtils.disableItemLighting();
        }
    }
}
