package net.fallingangel.jimmercraft.facts

object JimmerFacts {
    operator fun <K, V> get(facet: Facet<K, V>, key: K): V {
        return facet[key]
    }
}
