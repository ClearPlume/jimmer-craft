package net.fallingangel.jimmercraft.facts

interface Facet<K, V> {
    operator fun get(key: K): V
}
