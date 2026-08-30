package net.fallingangel.jimmercraft.completion

import com.intellij.patterns.PsiJavaPatterns.psiElement
import net.fallingangel.jimmercraft.annotation.host
import net.fallingangel.jimmercraft.annotation.name
import net.fallingangel.jimmercraft.facts.Completions
import net.fallingangel.jimmercraft.facts.JimmerFacts
import net.fallingangel.jimmerdto.lsi.LProperty
import net.fallingangel.jimmerdto.util.parent
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument

class KotlinCompletionContributor : CompletionContributor() {
    init {
        // 补全注解参数值
        completeAnnotationValue()
    }

    /**
     * 补全注解参数值
     */
    private fun completeAnnotationValue() {
        complete(
            psiElement(KtTokens.REGULAR_STRING_PART)
                .withParent(KtLiteralStringTemplateEntry::class.java)
                .withSuperParent(2, KtStringTemplateExpression::class.java)
                .inside(KtValueArgument::class.java)
                .inside(KtAnnotationEntry::class.java),
        ) { parameters, result ->
            val annotation = parameters.position.parent<KtAnnotationEntry>() ?: return@complete
            val annotationName = annotation.name() ?: return@complete

            val parameter = parameters.position.parent<KtValueArgument>() ?: return@complete
            val parameterName = parameter.name()

            val candidate = JimmerFacts[Completions, annotationName to parameterName] ?: return@complete
            val (_, property) = annotation.host() ?: return@complete
            result.addAllElements(candidate(property).map(LProperty::lookupProperty))
        }
    }
}
