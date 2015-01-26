package resonantengine.lib.prefab.item

import java.util

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import org.lwjgl.input.Keyboard
import resonantengine.lib.render.EnumColor
import resonantengine.lib.utility.TooltipUtility
import resonantengine.lib.wrapper.CollectionWrapper._
import resonantengine.lib.wrapper.StringWrapper._

/**
 * @author Calclavia
 */
trait TItemToolTip extends Item
{
  override def addInformation(itemStack: ItemStack, par2EntityPlayer: EntityPlayer, list: util.List[_], par4: Boolean)
  {
    val tooltipID = getUnlocalizedName(itemStack) + ".tooltip"
    val tooltip = tooltipID.getLocal

    if (tooltip != null && !tooltip.isEmpty && !tooltip.equals(tooltipID))
    {
      if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
      {
        list.add("tooltip.noShift".getLocal.replace("#0", EnumColor.AQUA.toString).replace("#1", EnumColor.GREY.toString))
      }
      else
      {
        list.addAll(tooltip.listWrap(20))
      }
    }

    if (Keyboard.isKeyDown(Keyboard.KEY_J))
    {
      TooltipUtility.addTooltip(itemStack, list.asInstanceOf[util.List[String]])
    }
    else
    {
      list.add("info.recipes.tooltip".getLocal.replace("#0", EnumColor.AQUA.toString).replace("#1", EnumColor.GREY.toString))
    }
  }
}
