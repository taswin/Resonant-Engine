package resonantengine.core

import javax.swing._

import cpw.mods.fml.client.FMLClientHandler
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.Loader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import resonantengine.core.content.debug.{GuiCreativeBuilder, TileCreativeBuilder}
import resonantengine.core.debug.F3Handler

/**
 * The Resonant Engine client proxy
 */
class ClientProxy extends CommonProxy
{
  override def preInit()
  {
    MinecraftForge.EVENT_BUS.register(F3Handler)
  }

  override def init()
  {
    if (Loader.isModLoaded("UniversalElectricity"))
    {
      JOptionPane.showMessageDialog(null, "UniversalElectricity is now part of Resonant Engine and should no longer be installed. \n To prevent world corruption the game will now close with a warning.", "Install Error", JOptionPane.ERROR_MESSAGE)
      throw new RuntimeException("UniversalElectricity is already contained within Resonant Engine and shouldn't be installed as a standalone")
    }
  }

  override def isPaused: Boolean =
  {
    if (FMLClientHandler.instance.getClient.isSingleplayer && !FMLClientHandler.instance.getClient.getIntegratedServer.getPublic)
    {
      val screen: GuiScreen = FMLClientHandler.instance.getClient.currentScreen
      if (screen != null)
      {
        if (screen.doesGuiPauseGame)
        {
          return true
        }
      }
    }
    return false
  }

  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef =
  {
    val ent: TileEntity = world.getTileEntity(x, y, z)
    if (ent.isInstanceOf[TileCreativeBuilder])
    {
      return new GuiCreativeBuilder(ent.asInstanceOf[TileCreativeBuilder])
    }
    return null
  }

  override def getClientPlayer: EntityPlayer =Minecraft.getMinecraft.thePlayer

  override def registerDummyRenderer(clazz: Class[_ <: TileEntity])
  {
    if (!TileEntityRendererDispatcher.instance.mapSpecialRenderers.containsKey(clazz))
    {
      ClientRegistry.bindTileEntitySpecialRenderer(clazz, new RenderTileDummy)
    }
  }
}