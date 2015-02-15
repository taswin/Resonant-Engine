package com.resonant.wrapper.core

import java.util.List

import net.minecraft.command.{CommandBase, ICommandSender, WrongUsageException}
import net.minecraft.util.ChatComponentText
import nova.internal.tick.UpdateTicker

object RECommand extends CommandBase {
	override def processCommand(sender: ICommandSender, args: Array[String]) {
		if (args == null || args.length == 0 || args(0).equalsIgnoreCase("help")) {
			sender.addChatMessage(new ChatComponentText("/" + getCommandName + " version"))
			sender.addChatMessage(new ChatComponentText("/" + getCommandName + " gridinfo"))
			sender.addChatMessage(new ChatComponentText("/" + getCommandName + " gridpause"))
			return
		}
		if (args(0).equalsIgnoreCase("version")) {
			sender.addChatMessage(new ChatComponentText("Version: " + Reference.version + "  Build: " + Reference.buildVersion))
		}
		if (args(0).equalsIgnoreCase("gridinfo")) {
			sender.addChatMessage(new
					ChatComponentText("[Universal Electricity Grid] Tick rate: " + (if (UpdateTicker.ThreadTicker.instance.pause) "Paused" else UpdateTicker.ThreadTicker.ticker.getDeltaTime + "/s")))
			sender.addChatMessage(new ChatComponentText("[Universal Electricity Grid] Grids running: " + UpdateTicker.ThreadTicker.ticker.getDeltaTime))
			return
		}
		if (args(0).equalsIgnoreCase("gridpause")) {
			UpdateTicker.ThreadTicker.instance.pause = !UpdateTicker.ThreadTicker.instance.pause
			sender.addChatMessage(new ChatComponentText("[Universal Electricity Grid] Ticking grids running state: " + !UpdateTicker.ThreadTicker.instance.pause))
			return
		}

		throw new WrongUsageException(this.getCommandUsage(sender))
	}

	override def getCommandName: String = "ue"

	override def getCommandUsage(par1ICommandSender: ICommandSender): String = "/ue help"

	override def getRequiredPermissionLevel: Int = 0

	override def addTabCompletionOptions(sender: ICommandSender, args: Array[String]): List[_] = {
		return if (args.length == 1) CommandBase.getListOfStringsMatchingLastWord(args, "tps") else null
	}
}