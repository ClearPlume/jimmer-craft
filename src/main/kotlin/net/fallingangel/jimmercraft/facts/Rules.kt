package net.fallingangel.jimmercraft.facts

import net.fallingangel.jimmercraft.rule.Rule
import net.fallingangel.jimmercraft.rule.MappedBy
import net.fallingangel.jimmerdto.lsi.LName
import net.fallingangel.jimmerdto.lsi.jimmer.JimmerAnnotations

object Rules : Facet<LName, List<Rule>> {
    override fun get(key: LName): List<Rule> {
        return when (key) {
            JimmerAnnotations.OneToMany -> listOf(MappedBy.OneToMany)
            JimmerAnnotations.ManyToMany -> listOf(MappedBy.ManyToMany)
            JimmerAnnotations.OneToOne -> listOf(MappedBy.OneToOne)
            else -> emptyList()
        }
    }
}
