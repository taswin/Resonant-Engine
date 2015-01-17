package resonant.lib.prefab.tile.spatial

import java.util.{HashSet => JHashSet, Set => JSet}

import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.Packet
import resonant.api.tile.IPlayerUsing
import resonant.engine.ResonantEngine
import resonant.lib.network.netty.AbstractPacket
import resonant.lib.prefab.tile.traits.TTicker

/**
 * All tiles inherit this class.
 *
 * @author Calclavia
 */
abstract class SpatialTile(material: Material) extends SpatialBlock(material) with IPlayerUsing with TTicker
{
  /**
   * The players to send packets to for machine update info.
   */
  final val playersUsing = new JHashSet[EntityPlayer]()

  override def update()
  {
    super[TTicker].update()
  }

  override def getDescriptionPacket: Packet =
  {
    ResonantEngine.packetHandler.toMCPacket(getDescPacket)
  }

  def getDescPacket: AbstractPacket = null

  override def tile: SpatialTile = this

  override def getPlayersUsing: JSet[EntityPlayer] = playersUsing
}