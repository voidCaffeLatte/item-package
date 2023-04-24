package voidcaffelatte.itempackage.packagerequest

import voidcaffelatte.itempackage.ItemPackage
import kotlin.collections.ArrayDeque

class PackageRequestHandler(
    plugin: ItemPackage)
{
    private val requests: ArrayDeque<PackageRequest> = ArrayDeque()
    private var spawningCount: Int = 0

    val count: Int
        get() = requests.size

    init
    {
        plugin.server.scheduler.runTaskTimer(plugin, ::onTick, 0, 1)
    }

    fun handle(packageRequest: PackageRequest)
    {
        requests.add(packageRequest)
    }

    fun unHandleAll()
    {
        requests.clear()
    }

    private fun onTick()
    {
        if (requests.isEmpty()) return
        if (spawningCount >= MaxConcurrentSpawningCount) return
        ++spawningCount
        val request = requests.removeFirst()
        request.generateAsync(
            {
                --spawningCount
            },
            {
                --spawningCount
                handle(request)
            })
    }

    companion object
    {
        private const val MaxConcurrentSpawningCount = 3
    }
}