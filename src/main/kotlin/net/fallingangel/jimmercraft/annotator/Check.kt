package net.fallingangel.jimmercraft.annotator

import com.intellij.lang.annotation.AnnotationHolder
import net.fallingangel.jimmercraft.rule.PropAnnotationSite
import net.fallingangel.jimmercraft.rule.Rule

fun AnnotationHolder.check(site: PropAnnotationSite, rules: List<Rule>) {
    val diagnostics = rules.flatMap { it(site) }

    diagnostics.forEach { diagnostic ->
        val fixerBuilder = newAnnotation(diagnostic.severity, diagnostic.message)
        fixerBuilder
            .range(diagnostic.psi)
            .highlightType(diagnostic.type)
        diagnostic.fixes.forEach(fixerBuilder::withFix)
        fixerBuilder.create()
    }
}
