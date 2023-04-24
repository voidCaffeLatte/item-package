package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import voidcaffelatte.itempackage.packagerequest.PackageRequestHandler

class QueueCommand(
    private val packageRequestHandler: PackageRequestHandler) : TabExecutor
{
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>): List<String>
    {
        return listOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        val queueSize = packageRequestHandler.count
        sender.sendMessage(Component.text("Request Queue: $queueSize", NamedTextColor.GRAY))
        return true
    }
}