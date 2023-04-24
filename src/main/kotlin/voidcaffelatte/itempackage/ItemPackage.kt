package voidcaffelatte.itempackage

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import voidcaffelatte.itempackage.command.*
import voidcaffelatte.itempackage.item.ItemRepository
import voidcaffelatte.itempackage.lot.LotRepository
import voidcaffelatte.itempackage.packagerequest.PackageRequestHandler
import voidcaffelatte.itempackage.parachute.ParachuteTracker
import voidcaffelatte.itempackage.pointpreset.PointPresetRepository
import voidcaffelatte.itempackage.pointpreset.PointPresetManager

class ItemPackage : JavaPlugin()
{
    private lateinit var configRepository: ConfigRepository
    private lateinit var itemRepository: ItemRepository
    private lateinit var lotRepository: LotRepository
    private lateinit var pointPresetRepository: PointPresetRepository
    private lateinit var pointPresetManager: PointPresetManager
    private lateinit var parachuteTracker: ParachuteTracker
    private lateinit var packageRequestHandler: PackageRequestHandler
    private lateinit var commands: MutableMap<String, TabExecutor>

    override fun onEnable()
    {
        configRepository = ConfigRepository(dataFolder)
        itemRepository = ItemRepository(dataFolder)
        lotRepository = LotRepository(dataFolder)
        pointPresetRepository = PointPresetRepository(dataFolder)
        loadConfig()

        pointPresetManager = PointPresetManager(this, pointPresetRepository)

        parachuteTracker = ParachuteTracker(this)
        packageRequestHandler = PackageRequestHandler(this)

        commands = createCommands()

        // TODO: Validate lot items
    }

    override fun onDisable()
    {
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean
    {
        val arguments = args.iterator()
        if (!arguments.hasNext()) return false

        val subCommand = arguments.next()
        return commands[subCommand]?.onCommand(sender, command, label, args) ?: false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>): List<String>
    {
        val arguments = args.iterator()
        if (!arguments.hasNext()) return listOf()

        val subCommand = arguments.next()
        if (!arguments.hasNext())
        {
            return commands.keys
                .asSequence()
                .filter { it.startsWith(subCommand) }
                .toList()
        }

        return commands[subCommand]?.onTabComplete(sender, command, alias, args) ?: return listOf()
    }

    fun loadConfig()
    {
        configRepository.load()
        itemRepository.load()
        lotRepository.load()
        pointPresetRepository.load()
    }

    private fun createCommands(): HashMap<String, TabExecutor>
    {
        return hashMapOf(
            "wbplace3d" to WorldBorderBasedPlace3DCommand(itemRepository, lotRepository, packageRequestHandler),
            "wbparachute" to WorldBorderBasedParachuteCommand(configRepository, itemRepository, lotRepository, packageRequestHandler, parachuteTracker),
            "placepoint" to PlacePointCommand(lotRepository, itemRepository, pointPresetRepository, packageRequestHandler),
            "exportitem" to ExportItemCommand(itemRepository),
            "getitem" to GetItemCommand(itemRepository),
            "giveitem" to GiveItemCommand(this, itemRepository),
            "givelot" to GiveLotCommand(this, lotRepository, itemRepository),
            "setitemslot" to SetItemSlotCommand(this, itemRepository),
            "setlotitem" to SetLotItemCommand(lotRepository, itemRepository),
            "removelotitem" to RemoveLotItem(lotRepository, itemRepository),
            "startpointedit" to StartPointEditCommand(pointPresetManager, pointPresetRepository),
            "endpointedit" to EndPointEditCommand(pointPresetManager),
            "lots" to LotsCommand(lotRepository),
            "items" to ItemsCommand(itemRepository),
            "pointpresets" to PointPresetsCommand(pointPresetRepository),
            "clearqueue" to ClearQueueCommand(packageRequestHandler),
            "queue" to QueueCommand(packageRequestHandler),
            "reload" to ReloadCommand(this))
    }
}
