package net.fallingangel.jimmercraft.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.*
import net.fallingangel.jimmercraft.rule.AnnotationValue
import net.fallingangel.jimmercraft.rule.PropAnnotationSite
import net.fallingangel.jimmerdto.lsi.process
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

class JavaAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(JavaAnnotatorVisitor(holder))
    }

    private class JavaAnnotatorVisitor(private val holder: AnnotationHolder) : JavaElementVisitor() {
        override fun visitAnnotation(annotation: PsiAnnotation) {
            val annotationClass = annotation.resolveAnnotationType() ?: return
            val annotationName = process(annotationClass) { className() } ?: return

            val psiMethod = (annotation.owner as? PsiModifierList)?.parent as? PsiMethod ?: return
            val entityClass = psiMethod.containingClass ?: return

            val entity = process(entityClass) { lClass() } ?: return
            val property = process(psiMethod) { lProperty(entity) } ?: return

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
