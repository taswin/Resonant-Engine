package resonantengine.lib.prefab.tile

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.material.Material
import net.minecraft.item.ItemStack
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fluids.{BlockFluidFinite, Fluid, FluidContainerRegistry, FluidStack}
import resonantengine.api.edx.recipe.{MachineRecipes, RecipeType}

/**
 * @author Calclavia
 */
class BlockFluidMixture(fluid: Fluid) extends BlockFluidFinite(fluid, Material.water)
{
  def setQuanta(world: World, x: Int, y: Int, z: Int, quanta: Int)
  {
    if (quanta > 0 && quanta <= quantaPerBlock)
    {
      world.setBlockMetadataWithNotify(x, y, z, quanta, 3)
    }
    else
    {
      world.setBlockToAir(x, y, z)
    }
  }

  override def drain(world: World, x: Int, y: Int, z: Int, doDrain: Boolean): FluidStack =
  {
    val stack: FluidStack = new FluidStack(getFluid, (FluidContainerRegistry.BUCKET_VOLUME * this.getFilledPercentage(world, x, y, z)).asInstanceOf[Int])
    if (doDrain)
    {
      world.setBlockToAir(x, y, z)
    }
    return stack
  }

  @SideOnly(Side.CLIENT) override def colorMultiplier(access: IBlockAccess, x: Int, y: Int, z: Int): Int = getFluid.getColor()

  def mix(world: World, x: Int, y: Int, z: Int, stack: ItemStack): Boolean =
  {
    if (MachineRecipes.instance.getOutput(RecipeType.MIXER.name, stack).length > 0 && getQuantaValue(world, x, y, z) < quantaPerBlock)
    {
      if (getQuantaValue(world, x, y, z) < quantaPerBlock)
      {
        world.setBlockMetadataWithNotify(x, y, z, getQuantaValue(world, x, y, z) + 1, 3)
        world.markBlockForUpdate(x, y, z)
        return true
      }
    }
    return false
  }

  override def canDrain(world: World, x: Int, y: Int, z: Int): Boolean = true

  def getQuantaPerBlock: Int = quantaPerBlock
}