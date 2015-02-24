package com.resonant.core.prefab.itemblock

import java.util
import java.util.Optional

import com.resonant.wrapper.lib.wrapper.StringWrapper._
import nova.core.item.Item
import nova.core.player.Player
import nova.core.render.Color
import org.lwjgl.input.Keyboard

/**
 * @author Calclavia
 */
trait TooltipItem extends Item {

	override def getTooltips(player: Optional[Player], tooltips: util.List[String]) {
		super.getTooltips(player, tooltips)
		val tooltipID = getID + ".tooltip"
		val tooltip = tooltipID.getLocal

		if (tooltip != null && !tooltip.isEmpty && !tooltip.equals(tooltipID)) {
			if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				tooltips.add("tooltip.noShift".getLocal.replace("#0", Color.blue.toString).replace("#1", Color.gray.toString))
			}
			else {
				tooltips.addAll(tooltip.listWrap(20))
			}
		}
	}
}
