package net.fallingangel.jimmercraft.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import net.fallingangel.jimmercraft.annotation.host
import net.fallingangel.jimmercraft.rule.AnnotationValue
import net.fallingangel.jimmercraft.rule.PropAnnotationSite
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtVisitorVoid
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

class KotlinAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(KotlinAnnotatorVisitor(holder))
    }

    private class KotlinAnnotatorVisitor(private val holder: AnnotationHolder) : KtVisitorVoid() {
        override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
            val (annotationName, entity, property) = annotationEntry.host() ?: return

            val site = object : PropAnnotationSite {
                override val host = property

                override val hostEntity = entity

                override fun <T : Any> param(name: String, type: KClass<T>): AnnotationValue<T>? {
                    val expression = annotationEntry.valueArguments
                        .find { it.getArgumentName()?.asName?.asString() == name }
                        ?.getArgumentExpression() ?: return null
                    val constant = analyze(expression) { expression.evaluate() } ?: return null

                    val value = type.safeCast(constant.value) ?: return null
                    return AnnotationValue(value, expression)
                }
            }
            holder.check(annotationName, site)
        }
    }
}
