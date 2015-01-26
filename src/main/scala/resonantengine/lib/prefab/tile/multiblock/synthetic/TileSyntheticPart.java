package resonantengine.lib.prefab.tile.multiblock.synthetic;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import resonantengine.core.ResonantEngine;
import resonantengine.lib.network.discriminator.PacketTile;
import resonantengine.lib.network.discriminator.PacketType;
import resonantengine.api.network.IPacketReceiver;
import resonantengine.lib.prefab.tile.multiblock.reference.IMultiBlock;
import resonantengine.lib.prefab.tile.spatial.ResonantBlock;
import resonantengine.lib.prefab.tile.spatial.ResonantTile;
import resonantengine.lib.transform.vector.Vector3;

/**
 * This is a multiblock to be used for blocks that are bigger than one block.
 *
 * @author Calclavia
 */
public class TileSyntheticPart extends ResonantTile implements IPacketReceiver
{
	// The the position of the main block. Relative to this block's position.
	private Vector3 mainBlockPosition;

	public TileSyntheticPart()
	{
		super(Material.piston);
		setBlockHardness(0.8f);
		setNormalRender(false);
		setIsOpaqueCube(false);
	}

	public Vector3 getMainBlock()
	{
		if (this.mainBlockPosition != null)
		{
			return toVector3().add(this.mainBlockPosition);
		}

		return null;
	}

	public void setMainBlock(Vector3 mainBlock)
	{
		this.mainBlockPosition = mainBlock.clone().add(toVector3().multiply(-1));

		if (!this.worldObj.isRemote)
		{
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
	}

	@Override
	public IIcon getIcon(IBlockAccess access, int side)
	{
		try
		{
			Vector3 main = ((TileSyntheticPart) access.getTileEntity(xi(), yi(), zi())).getMainBlock();
			Block block = main.getBlock(access);

			if (block != null)
			{

				return block.getIcon(access, main.xi(), main.yi(), main.zi(), side);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void onRemove(Block block, int par6)
	{
		if (getMainBlock() != null)
		{
			TileEntity tileEntity = this.getMainBlock().getTileEntity(this.worldObj);

			if (tileEntity instanceof IMultiBlock)
			{
				SyntheticMultiblock.instance.destroy((IMultiBlock) tileEntity);
			}
		}
	}

	/**
	 * Called when the block is right clicked by the player. This modified version detects electric
	 * items and wrench actions on your machine block. Do not override this function. Use
	 * machineActivated instead! (It does the same thing)
	 */
	@Override
	public boolean activate(EntityPlayer player, int side, Vector3 hit)
	{
		if (this.getMainBlock() != null)
		{
			TileEntity tileEntity = this.getMainBlock().getTileEntity(this.worldObj);

			if (tileEntity instanceof ResonantBlock)
			{
				return ((ResonantBlock) tileEntity).activate(player, side, hit);
			}
		}

		return false;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	@Override
	public int quantityDropped(int meta, int fortune)
	{
		return 0;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		if (this.mainBlockPosition != null)
		{
			final PacketTile packetTile = new PacketTile(this, new Object[] { this.mainBlockPosition.xi(), this.mainBlockPosition.yi(), this.mainBlockPosition.zi() });
			return ResonantEngine.packetHandler().toMCPacket(packetTile);
		}

		return null;
	}

	@Override
	public void read(ByteBuf data, EntityPlayer player, PacketType packet)
	{
		try
		{
			this.mainBlockPosition = new Vector3(data.readInt(), data.readInt(), data.readInt());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		if (nbt.hasKey("mainBlockPosition"))
		{
			this.mainBlockPosition = new Vector3(nbt.getCompoundTag("mainBlockPosition"));
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		if (this.mainBlockPosition != null)
		{
			nbt.setTag("mainBlockPosition", this.mainBlockPosition.toNBT());
		}
	}

	/**
	 * Determines if this TileEntity requires update calls.
	 *
	 * @return True if you want updateEntity() to be called, false if not
	 */
	@Override
	public boolean canUpdate()
	{
		return false;
	}
}