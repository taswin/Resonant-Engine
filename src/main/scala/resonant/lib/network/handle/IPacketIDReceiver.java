package resonant.lib.network.handle;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import resonant.lib.network.discriminator.PacketType;

/**
 * Created by Darkguardsman on 8/5/2014.
 */
public interface IPacketIDReceiver
{
	public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type);
}
