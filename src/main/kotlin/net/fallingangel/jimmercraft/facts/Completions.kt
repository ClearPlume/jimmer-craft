package net.fallingangel.jimmercraft.facts

import net.fallingangel.jimmercraft.rule.MappedBy
import net.fallingangel.jimmerdto.lsi.LName
import net.fallingangel.jimmerdto.lsi.LProperty
import net.fallingangel.jimmerdto.lsi.jimmer.JimmerAnnotations

typealias Candidate = (LProperty) -> List<LProperty>

object Completions : Facet<Pair<LName, String>, Candidate?> {
    override fun get(key: Pair<LName, String>): Candidate? {
        return when (key) {
            JimmerAnnotations.OneToMany to "mappedBy" -> MappedBy.OneToMany::candidates
            JimmerAnnotations.ManyToMany to "mappedBy" -> MappedBy.ManyToMany::candidates
            JimmerAnnotations.OneToOne to "mappedBy" -> MappedBy.OneToOne::candidates
            else -> null
        }
    }
}
