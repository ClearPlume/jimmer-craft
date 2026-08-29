package net.fallingangel.jimmercraft.refenerce

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PsiJavaPatterns.psiElement
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import net.fallingangel.jimmercraft.annotation.host
import net.fallingangel.jimmercraft.annotation.param
import net.fallingangel.jimmercraft.facts.JimmerFacts
import net.fallingangel.jimmercraft.facts.References
import net.fallingangel.jimmerdto.util.parent
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument

class KotlinReferenceContributor : PsiReferenceContributor() {
    @Suppress("DuplicatedCode")
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            psiElement(KtStringTemplateExpression::class.java)
                .inside(KtValueArgument::class.java)
                .inside(KtAnnotationEntry::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                    val annotation = element.parent<KtAnnotationEntry>() ?: return PsiReference.EMPTY_ARRAY
                    val (annotationName, _, property) = annotation.host() ?: return PsiReference.EMPTY_ARRAY

                    val parameter = element.parent<KtValueArgument>() ?: return PsiReference.EMPTY_ARRAY
                    val parameterName = parameter.getArgumentName()?.asName?.asString() ?: "value"
                    val value = annotation.param<String>(parameterName)?.value ?: return PsiReference.EMPTY_ARRAY
                    val reference = JimmerFacts[References, annotationName to parameterName] ?: return PsiReference.EMPTY_ARRAY

                    return arrayOf(
                        object : PsiReferenceBase<PsiElement>(element, TextRange(1, element.textLength - 1)) {
                            override fun resolve(): PsiElement? {
                                return reference(property, value)
                            }
                        }
                    )
                }
            },
        )
    }
}
