package resonantengine.prefab.block.itemblock

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import resonantengine.lib.utility.LanguageUtility

class ItemBlockMetadata(block: Block) extends ItemBlockTooltip(block)
{
  setHasSubtypes(true)

  override def getMetadata(damage: Int): Int =
  {
    return damage
  }

  override def getUnlocalizedName(itemstack: ItemStack): String =
  {
    val localized: String = LanguageUtility.getLocal(getUnlocalizedName() + "." + itemstack.getItemDamage() + ".name")
    if (localized != null && !localized.isEmpty)
    {
      return getUnlocalizedName() + "." + itemstack.getItemDamage
    }
    return getUnlocalizedName()
  }
}