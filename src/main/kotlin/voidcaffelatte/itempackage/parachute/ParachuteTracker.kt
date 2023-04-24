package voidcaffelatte.itempackage.parachute

import voidcaffelatte.itempackage.ItemPackage

class ParachuteTracker(
    plugin: ItemPackage)
{
    private val parachutes: ArrayDeque<Parachute> = ArrayDeque()

    init
    {
        plugin.server.scheduler.runTaskTimer(plugin, ::onTick, 0, 1)
    }

    fun add(parachute: Parachute)
    {
        parachutes.add(parachute)
    }

    private fun onTick()
    {
        val iterator = parachutes.iterator()
        while (iterator.hasNext())
        {
            val parachute = iterator.next()
            if (parachute.isValid)
            {
                parachute.onUpdate()
            }
            else
            {
                parachute.onRemove()
                iterator.remove()
            }
        }
    }
}