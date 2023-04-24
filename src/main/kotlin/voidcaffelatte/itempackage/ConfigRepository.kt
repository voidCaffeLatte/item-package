package voidcaffelatte.itempackage

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigRepository(
    private val dataFolder: File)
{
    private var cache: Config? = null

    fun get(): Config?
    {
        if (cache == null) load()
        return cache
    }

    fun load()
    {
        val file = File(dataFolder, FILE_NAME)
        val yaml = YamlConfiguration.loadConfiguration(file)
        val parachuteSpeed = yaml.getDouble("parachute-speed", 0.09)
        val config = Config(parachuteSpeed)
        cache = config
    }

    companion object
    {
        private const val FILE_NAME = "config.yml"
    }
}