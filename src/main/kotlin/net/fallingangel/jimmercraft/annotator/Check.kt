package net.fallingangel.jimmercraft.annotator

import com.intellij.lang.annotation.AnnotationHolder
import net.fallingangel.jimmercraft.facts.JimmerFacts
import net.fallingangel.jimmercraft.facts.Rules
import net.fallingangel.jimmercraft.rule.PropAnnotationSite
import net.fallingangel.jimmerdto.lsi.LName

fun AnnotationHolder.check(name: LName, site: PropAnnotationSite) {
    val diagnostics = JimmerFacts[Rules, name].flatMap { it(site) }

    diagnostics.forEach { diagnostic ->
        val fixerBuilder = newAnnotation(diagnostic.severity, diagnostic.message)
        fixerBuilder
            .range(diagnostic.psi)
            .highlightType(diagnostic.type)
        diagnostic.fixes.forEach(fixerBuilder::withFix)
        fixerBuilder.create()
    }
}
