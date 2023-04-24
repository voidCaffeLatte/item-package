package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import voidcaffelatte.itempackage.pointpreset.PointPresetRepository

class PointPresetsCommand(private val pointPresetRepository: PointPresetRepository) : TabExecutor
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
            2 -> sequenceOf("[point-preset-id]")
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
        val arguments = args.iterator()

        if (!arguments.hasNext()) return false
        arguments.next()

        if (!arguments.hasNext())
        {
            val textComponent = Component.text()
                .append(Component.text("=== All Point Presets ==="))
            for (pointPreset in pointPresetRepository.getAll()) textComponent.appendNewline().append(Component.text("- ${pointPreset.id}"))
            sender.sendMessage(textComponent.color(NamedTextColor.GRAY))
            return true
        }

        val pointPresetId = arguments.next()
        val pointPreset = pointPresetRepository.get(pointPresetId)
        if (pointPreset == null)
        {
            sender.sendMessage(Component.text("The point preset \"$pointPresetId\" is not found.", NamedTextColor.RED))
            return true
        }

        val textComponent = Component.text()
            .append(Component.text("=== Point Preset Details ($pointPresetId) ==="))
        for (point in pointPreset.getAllPoints())
        {
            val pointTextComponent = Component.text("- (${point.blockX}, ${point.blockY}, ${point.blockZ})")
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tp ${point.blockX} ${point.blockY + 1} ${point.blockZ}"))
            textComponent.appendNewline().append(pointTextComponent)
        }

        sender.sendMessage(textComponent.color(NamedTextColor.GRAY))

        return true
    }
}