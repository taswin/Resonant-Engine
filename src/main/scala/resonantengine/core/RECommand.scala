package resonantengine.core

import java.util.List

import net.minecraft.command.{CommandBase, ICommandSender, WrongUsageException}
import net.minecraft.util.ChatComponentText
import resonantengine.lib.grid.core.UpdateTicker

object RECommand extends CommandBase
{
  override def processCommand(sender: ICommandSender, args: Array[String])
  {
      if (args == null || args.length == 0 || args(0).equalsIgnoreCase("help"))
      {
        sender.addChatMessage(new ChatComponentText("/" + getCommandName + " version"))
        sender.addChatMessage(new ChatComponentText("/" + getCommandName + " gridinfo"))
        sender.addChatMessage(new ChatComponentText("/" + getCommandName + " gridpause"))
        return
      }
      if (args(0).equalsIgnoreCase("version"))
      {
        sender.addChatMessage(new ChatComponentText("Version: " + Reference.version + "  Build: " + Reference.buildVersion))
      }
      if (args(0).equalsIgnoreCase("gridinfo"))
      {
        sender.addChatMessage(new ChatComponentText("[Universal Electricity Grid] Tick rate: " + (if (UpdateTicker.threaded.pause) "Paused" else (if (UpdateTicker.threaded.getDeltaTime > 0) 1 / UpdateTicker.threaded.getDeltaTime.asInstanceOf[Double] else 0) * 1000 + "/s")))
        sender.addChatMessage(new ChatComponentText("[Universal Electricity Grid] Grids running: " + UpdateTicker.threaded.getUpdaterCount))
        return
      }
      if (args(0).equalsIgnoreCase("gridpause"))
      {
        UpdateTicker.threaded.pause = !UpdateTicker.threaded.pause
        sender.addChatMessage(new ChatComponentText("[Universal Electricity Grid] Ticking grids running state: " + !UpdateTicker.threaded.pause))
        return
      }

        throw new WrongUsageException(this.getCommandUsage(sender))
  }

  override def getCommandName: String = "ue"

  override def getCommandUsage(par1ICommandSender: ICommandSender): String = "/ue help"

  override def getRequiredPermissionLevel: Int = 0

  override def addTabCompletionOptions(sender: ICommandSender, args: Array[String]): List[_] =
  {
    return if (args.length == 1) CommandBase.getListOfStringsMatchingLastWord(args, "tps") else null
  }
}