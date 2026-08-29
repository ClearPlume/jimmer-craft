package net.fallingangel.jimmercraft.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import net.fallingangel.jimmercraft.annotation.host
import net.fallingangel.jimmercraft.rule.AnnotationValue
import net.fallingangel.jimmercraft.rule.PropAnnotationSite
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

class JavaAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(JavaAnnotatorVisitor(holder))
    }

    private class JavaAnnotatorVisitor(private val holder: AnnotationHolder) : JavaElementVisitor() {
        override fun visitAnnotation(annotation: PsiAnnotation) {
            val (annotationName, entity, property) = annotation.host() ?: return

            val site = object : PropAnnotationSite {
                override val host = property

                override val hostEntity = entity

                override fun <T : Any> param(name: String, type: KClass<T>): AnnotationValue<T>? {
                    val annotationMemberValue = annotation.findDeclaredAttributeValue(name) ?: return null
                    val constant = JavaPsiFacade.getInstance(annotation.project)
                        .constantEvaluationHelper
                        .computeConstantExpression(annotationMemberValue)

                    val value = type.safeCast(constant) ?: return null
                    return AnnotationValue(value, annotationMemberValue)
                }
            }
            holder.check(annotationName, site)
        }
    }
}
