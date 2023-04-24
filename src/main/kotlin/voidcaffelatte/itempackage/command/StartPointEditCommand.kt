package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import voidcaffelatte.itempackage.pointpreset.PointPresetRepository
import voidcaffelatte.itempackage.pointpreset.PointPresetManager

class StartPointEditCommand(
    private val pointPresetManager: PointPresetManager,
    private val pointPresetRepository: PointPresetRepository) : TabExecutor
{
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>): List<String>
    {
        val currentText = args.lastOrNull() ?: return listOf()
        return when (args.size)
        {
            2 -> sequenceOf("<point-preset-id>")
                .plus(pointPresetRepository.getAll()
                    .asSequence()
                    .map { it.id }
                    .filter { it.startsWith(currentText) })
                .toList()

            else -> listOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        if (sender !is Player)
        {
            sender.sendMessage(Component.text("This command can only be called by players.", NamedTextColor.RED))
            return true
        }

        val arguments = args.iterator()

        if (!arguments.hasNext()) return false
        arguments.next()

        if (!arguments.hasNext()) return false
        val pointPresetId = arguments.next()

        val editingPresetId = pointPresetManager.getEditingPresetId(sender.uniqueId.toString())
        if (editingPresetId != null)
        {
            sender.sendMessage(Component.text("Already editing the preset \"$editingPresetId\".", NamedTextColor.RED))
            return true
        }

        val editorId = pointPresetManager.getEditorId(pointPresetId)
        if (editorId != null)
        {
            val editorName = Bukkit.getServer().onlinePlayers.firstOrNull { it.uniqueId.toString() == editorId }?.name ?: "another player";
            sender.sendMessage(Component.text("The preset \"$pointPresetId\" is still being edited by $editorName.", NamedTextColor.RED))
            return true
        }

        pointPresetManager.startEditing(sender.uniqueId.toString(), pointPresetId)
        sender.sendMessage(Component.text("Start editing the preset \"${pointPresetId}\".", NamedTextColor.GRAY))

        return true
    }
}