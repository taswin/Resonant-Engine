package com.resonant.core.prefab.item

import java.util
import java.util.Optional

import com.resonant.wrapper.lib.render.EnumColor
import com.resonant.wrapper.lib.wrapper.StringWrapper._
import nova.core.item.Item
import nova.core.player.Player
import org.lwjgl.input.Keyboard

/**
 * @author Calclavia
 */
trait TItemToolTip extends Item {

	override def getTooltips(player: Optional[Player]): util.List[String] = {
		val tooltipID = getID + ".tooltip"
		val tooltip = tooltipID.getLocal
		val list = new util.ArrayList[String]()

		if (tooltip != null && !tooltip.isEmpty && !tooltip.equals(tooltipID)) {
			if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				list.add("tooltip.noShift".getLocal.replace("#0", EnumColor.AQUA.toString).replace("#1", EnumColor.GREY.toString))
			}
			else {
				list.addAll(tooltip.listWrap(20))
			}
		}

		return list
	}
}
