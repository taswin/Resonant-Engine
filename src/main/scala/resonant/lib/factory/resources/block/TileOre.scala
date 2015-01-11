package resonant.lib.factory.resources.block

import java.awt.Color

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.IIcon
import org.lwjgl.opengl.GL11
import resonant.engine.Reference
import resonant.lib.factory.resources.ResourceFactory
import resonant.lib.prefab.tile.spatial.SpatialBlock
import resonant.lib.render.RenderUtility
import resonant.lib.transform.vector.Vector3

/**
 * A generic ore block that is automatically colored/textured based on a color multiplier.
 * @author Calclavia
 */
class TileOre extends SpatialBlock(Material.rock) with TBlockResource
{
  var renderingForeground = false

  setTextureName("oreBackground")
  setCreativeTab(CreativeTabs.tabBlock)
  normalRender = false

  @SideOnly(Side.CLIENT)
  override def registerIcons(iconRegister: IIconRegister)
  {
    super.registerIcons(iconRegister)
    SpatialBlock.icon.put("oreForeground", iconRegister.registerIcon(Reference.prefix + "oreForeground"))
  }

  @SideOnly(Side.CLIENT)
  override def colorMultiplier: Int = 0xFFFFFF

  @SideOnly(Side.CLIENT)
  override def getIcon: IIcon = if (renderingForeground) SpatialBlock.icon.get("oreForeground") else super.getIcon

  @SideOnly(Side.CLIENT)
  override def renderInventory(itemStack: ItemStack)
  {
    renderingForeground = false
    //Render background
    GL11.glPushMatrix()
    RenderUtility.renderNormalBlockAsItem(itemStack.getItem.asInstanceOf[ItemBlock].field_150939_a, itemStack.getItemDamage, RenderUtility.renderBlocks)
    GL11.glPopMatrix()

    renderingForeground = true
    //Render foreground
    GL11.glPushMatrix()
    val material = ResourceFactory.getMaterial(itemStack.getItem.asInstanceOf[ItemBlock].field_150939_a)
    val color = new Color(ResourceFactory.getColor(material))
    GL11.glColor4f(color.getRed / 255f, color.getGreen / 255f, color.getBlue / 255f, 1)
    RenderUtility.renderBlocks.setOverrideBlockTexture(SpatialBlock.icon.get("oreForeground"))
    RenderUtility.renderNormalBlockAsItem(itemStack.getItem.asInstanceOf[ItemBlock].field_150939_a, itemStack.getItemDamage, RenderUtility.renderBlocks)
    RenderUtility.renderBlocks.clearOverrideBlockTexture()
    GL11.glPopMatrix()
  }

  @SideOnly(Side.CLIENT)
  override def renderStatic(renderer: RenderBlocks, pos: Vector3, pass: Int): Boolean =
  {
    renderingForeground = false
    //Render background
    renderer.renderStandardBlock(block, pos.xi, pos.yi, pos.zi)

    renderingForeground = true
    //Render foreground
    val color = new Color(ResourceFactory.getColor(resourceMaterial))

    if (Minecraft.isAmbientOcclusionEnabled && block.getLightValue() == 0)
      if (renderer.partialRenderBounds)
        renderer.renderStandardBlockWithAmbientOcclusionPartial(block, pos.xi, pos.yi, pos.zi, color.getRed / 255f, color.getGreen / 255f, color.getBlue / 255f)
      else
        renderer.renderStandardBlockWithAmbientOcclusion(block, pos.xi, pos.yi, pos.zi, color.getRed / 255f, color.getGreen / 255f, color.getBlue / 255f)
    else
      renderer.renderStandardBlockWithColorMultiplier(block, pos.xi, pos.yi, pos.zi, color.getRed / 255f, color.getGreen / 255f, color.getBlue / 255f)
    return true
  }
}
