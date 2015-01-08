package resonant.lib.debug

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent
import resonant.lib.transform.vector.VectorWorld

import scala.collection.convert.wrapAll._

/**
 * A handler that allows GUI display on the F3 GUI
 * @author Calclavia
 */
object F3Handler
{
  @SubscribeEvent
  def onDrawDebugText(event: RenderGameOverlayEvent.Text)
  {
    if (Minecraft.getMinecraft().gameSettings.showDebugInfo)
    {
      val world = Minecraft.getMinecraft().theWorld
      val player = Minecraft.getMinecraft.thePlayer
      val objectPosition = player.rayTrace(8, 1)

      if (objectPosition != null)
      {
        val tile = new VectorWorld(world, objectPosition.blockX, objectPosition.blockY, objectPosition.blockZ).getTileEntity

        if (tile.isInstanceOf[DebugInfo])
        {
          event.left.addAll(tile.asInstanceOf[DebugInfo].getDebugInfo)
        }
      }
    }
  }

}
