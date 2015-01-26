package resonantengine.core

import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import resonantengine.lib.mod.AbstractProxy

/**
 * The Resonant Engine common proxy
 *
 * @author Calclavia
 */
class CommonProxy extends AbstractProxy
{
  def isPaused = false

  def getClientPlayer: EntityPlayer = null

  override def init()
  {
    if (Loader.isModLoaded("UniversalElectricity"))
    {
      throw new RuntimeException("UniversalElectricity is already contained within Resonant Engine and shouldn't be installed as a standalone")
    }
  }

  def registerTileEntity(name: String, prefix: String, clazz: Class[_ <: TileEntity])
  {
    GameRegistry.registerTileEntityWithAlternatives(clazz, prefix + name, name)
  }

  def registerDummyRenderer(clazz: Class[_ <: TileEntity])
  {
  }
}