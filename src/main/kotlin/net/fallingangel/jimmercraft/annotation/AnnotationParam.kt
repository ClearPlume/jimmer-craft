package net.fallingangel.jimmercraft.annotation

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiAnnotation
import net.fallingangel.jimmercraft.rule.AnnotationValue
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

fun <T : Any> PsiAnnotation.param(name: String, type: KClass<T>): AnnotationValue<T>? {
    val annotationMemberValue = findDeclaredAttributeValue(name) ?: return null
    val constant = JavaPsiFacade.getInstance(project)
        .constantEvaluationHelper
        .computeConstantExpression(annotationMemberValue)

    val value = type.safeCast(constant) ?: return null
    return AnnotationValue(value, annotationMemberValue)
}

fun <T : Any> KtAnnotationEntry.param(name: String, type: KClass<T>): AnnotationValue<T>? {
    val expression = valueArguments
        .find { it.getArgumentName()?.asName?.asString() == name }
        ?.getArgumentExpression() ?: return null
    val constant = analyze(expression) { expression.evaluate() } ?: return null

    val value = type.safeCast(constant.value) ?: return null
    return AnnotationValue(value, expression)
}
