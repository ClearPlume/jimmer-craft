package net.fallingangel.jimmercraft.facts

import com.intellij.psi.PsiElement
import net.fallingangel.jimmercraft.rule.MappedBy
import net.fallingangel.jimmerdto.lsi.LName
import net.fallingangel.jimmerdto.lsi.LProperty
import net.fallingangel.jimmerdto.lsi.jimmer.JimmerAnnotations

typealias Reference = (LProperty, String) -> PsiElement?

object References : Facet<Pair<LName, String>, Reference?> {
    override fun get(key: Pair<LName, String>): Reference? {
        return when (key) {
            JimmerAnnotations.OneToMany to "mappedBy" -> MappedBy.OneToMany::resolve
            JimmerAnnotations.ManyToMany to "mappedBy" -> MappedBy.ManyToMany::resolve
            JimmerAnnotations.OneToOne to "mappedBy" -> MappedBy.OneToOne::resolve
            else -> null
        }
    }
}
