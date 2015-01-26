package resonantengine.lib.network.handle

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import resonantengine.lib.network.discriminator.PacketType

/**
 * A trait that is applied to packet receivers
 * @author Calclavia
 */
trait TPacketReceiver extends IPacketReceiver
{
  override final def read(buf: ByteBuf, player: EntityPlayer, packet: PacketType)
  {
    val id = buf.readInt()
    read(buf, id, packet)
  }

  def read(buf: ByteBuf, id: Int, packetType: PacketType)
  {

  }
}
