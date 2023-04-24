package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import voidcaffelatte.itempackage.pointpreset.PointPresetManager

class EndPointEditCommand(
    private val pointPresetManager: PointPresetManager) : TabExecutor
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
        if (sender !is Player)
        {
            sender.sendMessage(Component.text("This command can only be called by players.", NamedTextColor.RED))
            return true
        }

        val editedPreset = pointPresetManager.endEditing(sender.uniqueId.toString())
        if (editedPreset == null)
        {
            sender.sendMessage(Component.text("Not started editing yet.", NamedTextColor.RED))
            return true
        }

        sender.sendMessage(Component.text("Saved the preset \"${editedPreset.id}\" with ${editedPreset.getAllPoints().size} point(s).", NamedTextColor.GRAY))

        return true
    }
}