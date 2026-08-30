package net.fallingangel.jimmercraft.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import net.fallingangel.jimmercraft.annotation.host
import net.fallingangel.jimmercraft.annotation.name
import net.fallingangel.jimmercraft.annotation.param
import net.fallingangel.jimmercraft.facts.JimmerFacts
import net.fallingangel.jimmercraft.facts.Rules
import net.fallingangel.jimmercraft.rule.PropAnnotationSite
import kotlin.reflect.KClass

class JavaAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(JavaAnnotatorVisitor(holder))
    }

    private class JavaAnnotatorVisitor(private val holder: AnnotationHolder) : JavaElementVisitor() {
        @Suppress("DuplicatedCode")
        override fun visitAnnotation(annotation: PsiAnnotation) {
            val annotationName = annotation.name() ?: return
            val rules = JimmerFacts[Rules, annotationName]
            if (rules.isEmpty()) return

            val (entity, property) = annotation.host() ?: return
            val site = object : PropAnnotationSite {
                override val host = property
                override val hostEntity = entity
                override fun <T : Any> param(name: String, type: KClass<T>) = annotation.param(name, type)
            }
            holder.check(site, rules)
        }
    }
}
