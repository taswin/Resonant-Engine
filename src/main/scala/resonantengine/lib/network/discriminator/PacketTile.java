package resonantengine.lib.network.discriminator;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import resonantengine.api.network.IPacketReceiver;
import resonantengine.lib.transform.vector.Vector3;

/**
 * Packet type designed to be used with Tiles
 *
 * @author tgame14
 * @since 26/05/14
 */
public class PacketTile extends PacketType
{
	public int x;
	public int y;
	public int z;

	public PacketTile()
	{

	}

	/**
	 * @param x    - location
	 * @param y    - location
	 * @param z    - location
	 * @param args -  data to send, first arg should be packetID if
	 *             the tile is an instance of {@code IPacketIDReceiver}
	 *             Should never be null
	 */
	public PacketTile(int x, int y, int z, Object... args)
	{
		super(args);

		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @param tile - TileEntity to send this packet to, only used for location data
	 * @param args -  data to send, first arg should be packetID if
	 *             the tile is an instance of {@code IPacketIDReceiver}
	 *             Should never be null
	 */
	public PacketTile(TileEntity tile, Object... args)
	{
		this(tile.xCoord, tile.yCoord, tile.zCoord, args);
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeBytes(data());
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		data_$eq(buffer.slice());
	}

	@Override
	public void handleClientSide(EntityPlayer player)
	{
		handle(player);
	}

	@Override
	public void handleServerSide(EntityPlayer player)
	{
		handle(player);
	}

	public void handle(EntityPlayer player)
	{
		sender_$eq(player);

		TileEntity tile = player.getEntityWorld().getTileEntity(this.x, this.y, this.z);

		if (tile instanceof IPacketReceiver)
		{
			try
			{
				IPacketReceiver receiver = (IPacketReceiver) player.getEntityWorld().getTileEntity(this.x, this.y, this.z);
				receiver.read(data().slice(), player, this);
			}
			catch (IndexOutOfBoundsException e)
			{
				System.out.println("Packet sent to a TileEntity was read out side its bounds [" + tile + "] in " + new Vector3(x, y, z));
			}
			catch (Exception e)
			{
				System.out.println("Packet sent to a TileEntity failed to be received [" + tile + "] in " + new Vector3(x, y, z));
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Packet was sent to a tile not implementing IPacketReceiver, this is a coding error [" + tile + "] in " + new Vector3(x, y, z));
		}
	}
}
