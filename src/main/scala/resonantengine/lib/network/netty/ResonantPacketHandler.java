package resonantengine.lib.network.netty;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import resonantengine.core.ResonantEngine;

/**
 * @author tgame14
 * @since 31/05/14
 */
@ChannelHandler.Sharable
public class ResonantPacketHandler extends SimpleChannelInboundHandler<AbstractPacket>
{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AbstractPacket packet) throws Exception
	{
		INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();

		switch (FMLCommonHandler.instance().getEffectiveSide())
		{
			case CLIENT:
				packet.handleClientSide(ResonantEngine.proxy().getClientPlayer());
				break;
			case SERVER:
				packet.handleServerSide(((NetHandlerPlayServer) netHandler).playerEntity);
				break;
			default:
				break;
		}

	}

}
