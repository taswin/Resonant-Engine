package resonant.lib.network.handle

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import resonant.lib.network.discriminator.PacketType
import resonant.lib.transform.vector.TVectorWorld

/**
 * A trait that is applied to packet receivers
 * @author Calclavia
 */
trait TPacketReceiver extends IPacketReceiver
{
  override def read(buf: ByteBuf, player: EntityPlayer, packet: PacketType)
  {
    val id = buf.readInt()
    read(buf, id, packet)
  }

  def read(buf: ByteBuf, id: Int, packetType: PacketType)
  {

  }
}
