package net.fallingangel.jimmercraft.facts

import net.fallingangel.jimmercraft.rule.Rule
import net.fallingangel.jimmercraft.rule.MappedBy
import net.fallingangel.jimmerdto.lsi.LName
import net.fallingangel.jimmerdto.lsi.jimmer.JimmerAnnotations

object Rules : Facet<LName, List<Rule>> {
    override fun get(key: LName): List<Rule> {
        return when (key) {
            JimmerAnnotations.OneToMany -> listOf(MappedBy(JimmerAnnotations.ManyToOne))
            JimmerAnnotations.ManyToMany -> listOf(MappedBy(JimmerAnnotations.ManyToMany))
            JimmerAnnotations.OneToOne -> listOf(MappedBy(JimmerAnnotations.OneToOne))
            else -> emptyList()
        }
    }
}
