package resonantengine.lib.prefab.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import resonantengine.core.Reference;
import resonantengine.lib.render.RenderUtility;
import resonantengine.lib.transform.region.Rectangle;
import resonantengine.lib.transform.vector.Vector2;
import resonantengine.lib.utility.LanguageUtility;
import resonantengine.lib.utility.science.UnitDisplay;
import resonantengine.lib.utility.science.UnitDisplay.Unit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class GuiContainerBase extends GuiContainer
{
	protected static int energyType = 0;
	public ResourceLocation baseTexture;
	public String tooltip = "";
	protected int meterX = 54;
	protected int meterHeight = 49;
	protected int meterWidth = 14;
	protected int meterEnd = meterX + meterWidth;
	protected HashMap<Rectangle, String> tooltips = new HashMap();
	protected int containerWidth;
	protected int containerHeight;
	private float lastChangeFrameTime;

	public GuiContainerBase(Container container)
	{
		super(container);
		this.ySize = 217;
		this.baseTexture = Reference.guiBase();
	}

	public GuiContainerBase()
	{
		this(new ContainerDummy());
		this.baseTexture = Reference.guiEmpty();
	}

	protected void drawString(String str, int x, int y)
	{
		fontRendererObj.drawString(str, x, y, 4210752);
	}

	protected void drawString(String str, int y)
	{
		fontRendererObj.drawString(str, xSize / 2, y, 4210752);
	}

	protected void drawStringCentered(String str, int x, int y)
	{
		drawString(str, x - (fontRendererObj.getStringWidth(str) / 2), y);
	}

	protected void drawStringCentered(String str, int y)
	{
		drawStringCentered(str, xSize / 2, y);
	}

	protected void drawStringCentered(String str)
	{
		drawStringCentered(str, 6);
	}

	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		Iterator<Entry<Rectangle, String>> it = this.tooltips.entrySet().iterator();

		while (it.hasNext())
		{
			Entry<Rectangle, String> entry = it.next();

			if (entry.getKey().isWithin2D(new Vector2(mouseX - this.guiLeft, mouseY - this.guiTop)))
			{
				this.tooltip = entry.getValue();
				break;
			}
		}

		if (this.tooltip != null && this.tooltip != "")
		{
			this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, LanguageUtility.splitStringPerWordIntoArray(this.tooltip, 5));
		}

		this.tooltip = "";

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
	{
		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;

		this.mc.renderEngine.bindTexture(this.baseTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, this.xSize, this.ySize);
	}

	protected void drawBulb(int x, int y, boolean isOn)
	{
		this.mc.renderEngine.bindTexture(this.baseTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (isOn)
		{
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 161, 0, 6, 6);

		}
		else
		{
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 161, 4, 6, 6);
		}
	}

	protected void drawSlot(int x, int y, ItemStack itemStack)
	{
		this.mc.renderEngine.bindTexture(this.baseTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

		this.drawItemStack(itemStack, this.containerWidth + x, this.containerHeight + y);
	}

	protected void drawItemStack(ItemStack itemStack, int x, int y)
	{
		x += 1;
		y += 1;
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);

		// drawTexturedModelRectFromIcon
		// GL11.glEnable(GL11.GL_BLEND);
		// GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		itemRender.renderItemAndEffectIntoGUI(fontRendererObj, this.mc.renderEngine, itemStack, x, y);
		// GL11.glDisable(GL11.GL_BLEND);
	}

	protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY)
	{
		this.drawTextWithTooltip(textName, format, x, y, mouseX, mouseY, 4210752);
	}

	protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY, int color)
	{
		String name = LanguageUtility.getLocal("gui." + textName + ".name");
		String text = format.replaceAll("%1", name);
		fontRendererObj.drawString(text, x, y, color);

		String tooltip = LanguageUtility.getLocal("gui." + textName + ".tooltip");

		if (tooltip != null && tooltip != "")
		{
			if (new Rectangle(x, y, (int) (text.length() * 4.8), 12).isWithin2D(new Vector2(mouseX, mouseY)))
			{
				this.tooltip = tooltip;
			}
		}
	}

	protected void drawTextWithTooltip(String textName, int x, int y, int mouseX, int mouseY)
	{
		this.drawTextWithTooltip(textName, "%1", x, y, mouseX, mouseY);
	}

	protected void drawSlot(int x, int y, GuiSlotType type, float r, float g, float b)
	{
		this.mc.renderEngine.bindTexture(Reference.guiComponents());
		GL11.glColor4f(r, g, b, 1.0F);

		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

		if (type != GuiSlotType.NONE)
		{
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 18 * type.ordinal(), 18, 18);
		}
	}

	protected void drawSlot(int x, int y, GuiSlotType type)
	{
		this.drawSlot(x, y, type, 1, 1, 1);
	}

	protected void drawSlot(int x, int y)
	{
		this.drawSlot(x, y, GuiSlotType.NONE);
	}

	protected void drawBar(int x, int y, float scale)
	{
		this.mc.renderEngine.bindTexture(Reference.guiComponents());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		/** Draw background progress bar/ */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 0, 22, 15);

		if (scale > 0)
		{
			/** Draw white color actual progress. */
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 15, 22 - (int) (scale * 22), 15);
		}
	}

	protected void drawForce(int x, int y, float scale)
	{
		this.mc.renderEngine.bindTexture(Reference.guiComponents());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		/** Draw background progress bar/ */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 0, 107, 11);

		if (scale > 0)
		{
			/** Draw white color actual progress. */
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, meterX, 11, (int) (scale * 107), 11);
		}
	}

	protected void drawLongBlueBar(int x, int y, float scale)
	{
		mc.renderEngine.bindTexture(Reference.guiComponents());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		/** Draw background progress bar/ */
		drawTexturedModalRect(containerWidth + x, containerHeight + y, meterX, 33, 140, 14);

		if (scale > 0)
		{
			/** Draw white color actual progress. */
			drawTexturedModalRect(containerWidth + x, containerHeight + y, meterX, 48, (int) (scale * 140), 14);
		}
	}

	protected void drawElectricity(int x, int y, float scale)
	{
		this.mc.renderEngine.bindTexture(Reference.guiComponents());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		/** Draw background progress bar/ */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 0, 107, 11);

		if (scale > 0)
		{
			/** Draw white color actual progress. */
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 22, (int) (scale * 107), 11);
		}
	}

	protected void drawMeter(int x, int y, float scale, float r, float g, float b)
	{
		this.mc.renderEngine.bindTexture(Reference.guiComponents());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		/** Draw the background meter. */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 0, this.meterWidth, this.meterHeight);

		/** Draw liquid/gas inside */
		GL11.glColor4f(r, g, b, 1.0F);
		int actualScale = (int) ((this.meterHeight - 1) * scale);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y + (this.meterHeight - 1 - actualScale), 40, 49, this.meterHeight - 1, actualScale);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		/** Draw measurement lines */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 49 * 2, this.meterWidth, this.meterHeight);
	}

	protected void drawMeter(int x, int y, float scale, FluidStack liquidStack)
	{
		this.mc.renderEngine.bindTexture(Reference.guiComponents());

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		/** Draw the background meter. */
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 0, meterWidth, meterHeight);

		/** Draw liquid/gas inside */
		if (liquidStack != null)
		{
			this.displayGauge(this.containerWidth + x, this.containerHeight + y, -10, 1, 12, (int) ((meterHeight - 1) * scale), liquidStack);
		}

		/** Draw measurement lines */
		this.mc.renderEngine.bindTexture(Reference.guiComponents());
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 49 * 2, meterWidth, meterHeight);
	}

	protected void drawSlot(int x, int y, SlotType type, float r, float g, float b)
	{
		this.mc.renderEngine.bindTexture(Reference.guiComponents());
		GL11.glColor4f(r, g, b, 1.0F);

		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

		if (type != SlotType.NONE)
		{
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 18 * type.ordinal(), 18, 18);
		}
	}

	protected void drawSlot(int x, int y, SlotType type)
	{
		this.drawSlot(x, y, type, 1, 1, 1);
	}

	public void renderUniversalDisplay(int x, int y, double energy, int mouseX, int mouseY, Unit unit)
	{
		renderUniversalDisplay(x, y, energy, mouseX, mouseY, unit, false);
	}

	public void renderUniversalDisplay(int x, int y, double energy, double maxEnergy, int mouseX, int mouseY, Unit unit, boolean symbol)
	{
		String displaySuffix = "";

		if (unit == Unit.WATT)
		{
			displaySuffix = "/s";
		}

		String display = new UnitDisplay(unit, energy).symbol(symbol) + "/" + new UnitDisplay(unit, maxEnergy).symbol(symbol);

		/*
		// Check different energy system types.
		if (unit == Unit.WATT || unit == Unit.JOULES)
		{
			switch (energyType)
			{
				case 3:
					display = UnitDisplay.roundDecimals(energy * ModuleThermalExpansion$.MODULE$.ratio) + " RF" + displaySuffix + "/" + UnitDisplay.roundDecimals(maxEnergy * ModuleThermalExpansion$.MODULE$.ratio) + " RF" + displaySuffix;
					break;
			}
		}*/

		//TODO: Check if this rect works.
		if (new Rectangle(x, y, x + display.length() * 5, y + 9).isWithin2D(new Vector2(mouseX, mouseY)))
		{
			if (Mouse.isButtonDown(0) && this.lastChangeFrameTime <= 0)
			{
				energyType = (energyType + 1) % 4;
				this.lastChangeFrameTime = 60;
			}
			else
			{
				this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Click to change unit.");
			}
		}

		this.lastChangeFrameTime--;

		fontRendererObj.drawString(display, x, y, 4210752);
	}

	public void renderUniversalDisplay(int x, int y, double energy, int mouseX, int mouseY, Unit unit, boolean small)
	{
		String displaySuffix = "";

		if (unit == Unit.WATT)
		{
			displaySuffix = "/s";
		}

		String display = new UnitDisplay(unit, energy).symbol(small).toString();
/*
		// Check different energy system types.
		if (unit == Unit.WATT || unit == Unit.JOULES)
		{
			switch (energyType)
			{
				case 3:
					display = UnitDisplay.roundDecimals(energy * ModuleThermalExpansion$.MODULE$.ratio) + " RF" + displaySuffix;
					break;
			}
		}
*/
		if (new Rectangle(x, y, x + display.length() * 5, y + 9).isWithin2D(new Vector2(mouseX, mouseY)))
		{
			if (Mouse.isButtonDown(0) && this.lastChangeFrameTime <= 0)
			{
				energyType = (energyType + 1) % 4;
				this.lastChangeFrameTime = 60;
			}
			else
			{
				this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Click to change unit.");
			}
		}

		this.lastChangeFrameTime--;

		fontRendererObj.drawString(display, x, y, 4210752);
	}

	public void drawTooltip(int x, int y, String... toolTips)
	{
		if (!GuiScreen.isShiftKeyDown())
		{
			if (toolTips != null)
			{
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
				GL11.glDisable(GL11.GL_DEPTH_TEST);

				int var5 = 0;
				int var6;
				int var7;

				for (var6 = 0; var6 < toolTips.length; ++var6)
				{
					var7 = fontRendererObj.getStringWidth(toolTips[var6]);

					if (var7 > var5)
					{
						var5 = var7;
					}
				}

				var6 = x + 12;
				var7 = y - 12;

				int var9 = 8;

				if (toolTips.length > 1)
				{
					var9 += 2 + (toolTips.length - 1) * 10;
				}

				if (this.guiTop + var7 + var9 + 6 > this.height)
				{
					var7 = this.height - var9 - this.guiTop - 6;
				}

				this.zLevel = 300;
				int var10 = -267386864;
				this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
				this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
				this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
				this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
				this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
				int var11 = 1347420415;
				int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
				this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
				this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
				this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
				this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

				for (int var13 = 0; var13 < toolTips.length; ++var13)
				{
					String var14 = toolTips[var13];

					fontRendererObj.drawStringWithShadow(var14, var6, var7, -1);
					var7 += 10;
				}

				this.zLevel = 0;

				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			}
		}
	}

	/**
	 * Based on BuildCraft
	 */
	protected void displayGauge(int j, int k, int line, int col, int width, int squaled, FluidStack liquid)
	{
		squaled -= 1;

		if (liquid == null)
		{
			return;
		}

		int start = 0;

		IIcon liquidIcon = null;
		Fluid fluid = liquid.getFluid();

		if (fluid != null && fluid.getStillIcon() != null)
		{
			liquidIcon = fluid.getStillIcon();
		}

		RenderUtility.setSpriteTexture(fluid.getSpriteNumber());

		if (liquidIcon != null)
		{
			while (true)
			{
				int x;

				if (squaled > 16)
				{
					x = 16;
					squaled -= 16;
				}
				else
				{
					x = squaled;
					squaled = 0;
				}

				this.drawTexturedModelRectFromIcon(j + col, k + line + 58 - x - start, liquidIcon, width, 16 - (16 - x));
				start = start + 16;

				if (x == 0 || squaled == 0)
				{
					break;
				}
			}
		}
	}

	public enum SlotType
	{
		NONE,
		BATTERY,
		LIQUID,
		GAS,
		ARR_UP,
		ARR_DOWN,
		ARR_LEFT,
		ARR_RIGHT,
		ARR_UP_RIGHT,
		ARR_UP_LEFT,
		ARR_DOWN_LEFT,
		ARR_DOWN_RIGHT
	}
}
