package net.fallingangel.jimmercraft.completion

import com.intellij.patterns.PsiJavaPatterns.psiElement
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiNameValuePair
import com.intellij.psi.impl.source.tree.ElementType
import net.fallingangel.jimmercraft.annotation.host
import net.fallingangel.jimmercraft.annotation.name
import net.fallingangel.jimmercraft.facts.Completions
import net.fallingangel.jimmercraft.facts.JimmerFacts
import net.fallingangel.jimmerdto.lsi.LProperty
import net.fallingangel.jimmerdto.util.parent

class JavaCompletionContributor : CompletionContributor() {
    init {
        // 补全注解参数值
        completeAnnotationValue()
    }

    /**
     * 补全注解参数值
     */
    private fun completeAnnotationValue() {
        complete(
            psiElement(ElementType.STRING_LITERAL)
                .withParent(psiElement(PsiLiteralExpression::class.java))
                .inside(PsiNameValuePair::class.java)
                .inside(PsiAnnotation::class.java),
        ) { parameters, result ->
            val annotation = parameters.position.parent<PsiAnnotation>() ?: return@complete
            val annotationName = annotation.name() ?: return@complete

            val parameter = parameters.position.parent<PsiNameValuePair>() ?: return@complete
            val parameterName = parameter.name()

            val candidate = JimmerFacts[Completions, annotationName to parameterName] ?: return@complete
            val (_, property) = annotation.host() ?: return@complete
            result.addAllElements(candidate(property).map(LProperty::lookupProperty))
        }
    }
}
