package universalelectricity.components.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import universalelectricity.components.common.BasicComponents;
import universalelectricity.prefab.UETab;

public class BlockBCOre extends Block
{
	private Icon textureCopper;
	private Icon textureTin;

	public BlockBCOre(int id)
	{
		super(id, Material.rock);
		this.setCreativeTab(UETab.INSTANCE);
		this.setUnlocalizedName("bcOre");
		this.setHardness(2f);
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int side, int metadata)
	{
		if (metadata == 1)
		{
			return this.textureTin;
		}

		return this.textureCopper;
	}

	@Override
	public void func_94332_a(IconRegister par1IconRegister)
	{
		this.textureCopper = par1IconRegister.func_94245_a("copper");
		this.textureTin = par1IconRegister.func_94245_a("tin");
	}

	@Override
	public int damageDropped(int metadata)
	{
		return metadata;
	}

	@Override
	public String getTextureFile()
	{
		return BasicComponents.BLOCK_TEXTURE_DIRECTORY;
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
	}
}
