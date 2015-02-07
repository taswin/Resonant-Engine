package com.resonant.lib.render;

import com.resonant.lib.utility.LanguageUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import nova.core.util.Direction;
import nova.core.util.transform.Vector3d;
import org.lwjgl.opengl.GL11;
import resonantengine.lib.transform.rotation.Quaternion;
import resonantengine.lib.utility.WorldUtility;

import java.util.EnumSet;

@SideOnly(Side.CLIENT)
public class RenderItemOverlayUtility {
	public static final Direction[] forge_sides = { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };

	public static RenderBlocks renderBlocks = new RenderBlocks();
	public static RenderItem renderItem = ((RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class));

	public static void renderTopOverlay(TileEntity tileEntity, ItemStack[] inventory, Direction dir, double x, double y, double z) {
		renderTopOverlay(tileEntity, inventory, dir, 3, 3, x, y, z, 0.7f);
	}

	public static void renderTopOverlay(TileEntity tileEntity, ItemStack[] inventory, Direction dir, int matrixX, int matrixZ, double x, double y, double z, float scale) {
		GL11.glPushMatrix();

		/** Render the Crafting Matrix */
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		boolean isLooking = false;

		MovingObjectPosition objectPosition = player.rayTrace(8, 1);

		if (objectPosition != null) {
			isLooking = objectPosition.blockX == tileEntity.xCoord && objectPosition.blockY == tileEntity.yCoord && objectPosition.blockZ == tileEntity.zCoord;
		}

		for (int i = 0; i < (matrixX * matrixZ); i++) {
			if (inventory[i] != null) {
				Vector3d translation = new Vector3d((double) (i / matrixX) / ((double) matrixX) + (0.5 / (matrixX)), 1.1, (double) (i % matrixZ) / ((double) matrixZ) + (0.5 / (matrixZ))).add(-0.5);
				translation.multiply(0.85);
				GL11.glPushMatrix();
				GL11.glTranslated(x + 0.5f, y + 0.5f, z + 0.5f);

				if (dir != null) {
					RenderUtility.rotateBlockBasedOnDirection(dir);
				}

				GL11.glTranslated(0, 0, scale / 6);

				GL11.glTranslated(translation.x(), translation.y(), translation.z());
				GL11.glScalef(scale, scale, scale);
				renderItem(tileEntity.getWorldObj(), inventory[i], new Vector3d(0, 0, 0), 0, 1);
				GL11.glPopMatrix();

				if (isLooking) {
					GL11.glPushMatrix();
					GL11.glTranslated(x, y, z);
					int angle = dir != null ? WorldUtility.getAngleFromDirection(WorldUtility.invertX(dir)) : 0;
					RenderUtility.renderFloatingText("" + inventory[i].stackSize, translation.transform(new Quaternion(angle, Vector3d.up())).add(0.5).add(new Vector3d(0, 0.5, 0)));
					GL11.glPopMatrix();
				}
			}
		}
		GL11.glPopMatrix();

	}

	public static void renderItemOnSides(TileEntity tile, ItemStack itemStack, double x, double y, double z) {
		renderItemOnSides(tile, itemStack, x, y, z, LanguageUtility.getLocal("tooltip.noOutput"));
	}

	public static void renderItemOnSides(TileEntity tile, ItemStack itemStack, double x, double y, double z, String renderText) {
		renderItemOnSides(tile, itemStack, x, y, z, renderText, EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
	}

	//TODO replace tile param with world to reduce errors and increases uses of this method
	public static void renderItemOnSides(TileEntity tile, ItemStack itemStack, double x, double y, double z, String renderText, EnumSet<Direction> sides) {
		if (tile != null && tile.getWorldObj() != null) {
			/** Render the Output */
			String amount = "";

			if (itemStack != null) {
				renderText = itemStack.getDisplayName();
				amount = Integer.toString(itemStack.stackSize);
			}

			for (Direction direction : sides) {
				if (direction != Direction.UNKNOWN) {
					if (tile.getBlockType().isSideSolid(tile.getWorldObj(), tile.xCoord + direction.offsetX, tile.yCoord, tile.zCoord + direction.offsetZ, direction.getOpposite())) {
						continue;
					}

					renderItemOnSide(tile, itemStack, direction, x, y, z, renderText, amount);

					GL11.glPushMatrix();
					setupLight(tile, direction.offsetX, direction.offsetZ);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
					GL11.glDisable(2896);
					RenderUtility.renderText(renderText, direction, 0.02f, x, y - 0.35f, z);
					RenderUtility.renderText(amount, direction, 0.02f, x, y - 0.15f, z);
					GL11.glEnable(2896);
					GL11.glPopMatrix();
				}
			}
		}
	}

	protected static void renderItemSingleSide(TileEntity tile, double x, double y, double z, ItemStack itemStack, Direction direction, String renderText) {
		if (!tile.getBlockType().isSideSolid(tile.getWorldObj(), tile.xCoord + direction.offsetX, tile.yCoord, tile.zCoord + direction.offsetZ, direction.getOpposite())) {
			String amount = "";

			if (itemStack != null) {
				renderText = itemStack.getDisplayName();
				amount = Integer.toString(itemStack.stackSize);
			}

			renderItemOnSide(tile, itemStack, direction, x, y, z, renderText, amount);

			GL11.glPushMatrix();
			setupLight(tile, direction.offsetX, direction.offsetZ);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			GL11.glDisable(2896);
			RenderUtility.renderText(renderText, direction, 0.02f, x, y - 0.35f, z);
			RenderUtility.renderText(amount, direction, 0.02f, x, y - 0.15f, z);
			GL11.glEnable(2896);
			GL11.glPopMatrix();
		}
	}

	@SuppressWarnings("incomplete-switch")
	protected static void renderItemOnSide(TileEntity tile, ItemStack itemStack, Direction direction, double x, double y, double z, String renderText, String amount) {
		if (itemStack != null) {
			GL11.glPushMatrix();

			switch (direction) {
				case NORTH:
					GL11.glTranslated(x + 0.65, y + 0.9, z - 0.01);
					break;
				case SOUTH:
					GL11.glTranslated(x + 0.35, y + 0.9, z + 1.01);
					GL11.glRotatef(180, 0, 1, 0);
					break;
				case WEST:
					GL11.glTranslated(x - 0.01, y + 0.9, z + 0.35);
					GL11.glRotatef(90, 0, 1, 0);
					break;
				case EAST:
					GL11.glTranslated(x + 1.01, y + 0.9, z + 0.65);
					GL11.glRotatef(-90, 0, 1, 0);
					break;
				case UP:
					//TODO fix rotation
					GL11.glTranslated(x + 0.65, y + 1.01, z + 0.9);
					GL11.glRotatef(90, 1, 0, 0);
					break;
				case DOWN:
					//TODO Fix rotation
					GL11.glTranslated(x + 0.65, y - 0.01, z - 0.01);
					GL11.glRotatef(-90, 1, 0, 0);
					break;
			}

			float scale = 0.03125F;
			GL11.glScalef(0.6f * scale, 0.6f * scale, -0.00001f);
			GL11.glRotatef(180, 0, 0, 1);

			TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;

			setupLight(tile, direction.offsetX, direction.offsetZ);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			GL11.glDisable(2896);

			if (!ForgeHooksClient.renderInventoryItem(renderBlocks, renderEngine, itemStack, true, 0.0F, 0.0F, 0.0F)) {
				renderItem.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, renderEngine, itemStack, 0, 0);
			}

			GL11.glEnable(2896);
			GL11.glPopMatrix();
		}
	}

	private static void setupLight(TileEntity tileEntity, int xDifference, int zDifference) {
		World world = tileEntity.getWorldObj();

		if (tileEntity.getBlockType().isOpaqueCube()) {
			return;
		}

		int br = world.getLightBrightnessForSkyBlocks(tileEntity.xCoord + xDifference, tileEntity.yCoord, tileEntity.zCoord + zDifference, 0);
		int var11 = br % 65536;
		int var12 = br / 65536;
		float scale = 1;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var11 * scale, var12 * scale);
	}

	public static void renderItem(World world, ItemStack itemStack, Vector3d position, float rotationYaw, int angle) {
		if (itemStack != null) {
			EntityItem entityItem = new EntityItem(world, position.x(), position.y(), position.z(), itemStack.copy());
			entityItem.getEntityItem().stackSize = 1;
			entityItem.hoverStart = 0.0F;
			GL11.glPushMatrix();

			if (!(itemStack.getItem() instanceof ItemBlock)) {
				GL11.glRotatef(180 + rotationYaw, 0, 1, 0);
				GL11.glRotatef(90 * angle, 1, 0, 0);
			} else {
				GL11.glTranslated(0, 0, -0.15);
			}

			RenderItem renderItem = ((RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class));

			boolean fancyGraphics = RenderManager.instance.options.fancyGraphics;
			RenderManager.instance.options.fancyGraphics = true;
			renderItem.doRender(entityItem, 0, 0, 0, 0, 0);
			RenderManager.instance.options.fancyGraphics = fancyGraphics;

			GL11.glPopMatrix();
		}
	}
}
