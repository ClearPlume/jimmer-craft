package net.fallingangel.jimmercraft.reference

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PsiJavaPatterns.psiElement
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import net.fallingangel.jimmercraft.annotation.host
import net.fallingangel.jimmercraft.annotation.name
import net.fallingangel.jimmercraft.annotation.param
import net.fallingangel.jimmercraft.facts.JimmerFacts
import net.fallingangel.jimmercraft.facts.References
import net.fallingangel.jimmerdto.util.parent

class JavaReferenceContributor : PsiReferenceContributor() {
    @Suppress("DuplicatedCode")
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            psiElement(PsiLiteralExpression::class.java)
                .inside(PsiNameValuePair::class.java)
                .inside(PsiAnnotation::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                    val annotation = element.parent<PsiAnnotation>() ?: return PsiReference.EMPTY_ARRAY
                    val annotationName = annotation.name() ?: return PsiReference.EMPTY_ARRAY

                    val parameter = element.parent<PsiNameValuePair>() ?: return PsiReference.EMPTY_ARRAY
                    val parameterName = parameter.name()

                    val reference = JimmerFacts[References, annotationName to parameterName] ?: return PsiReference.EMPTY_ARRAY
                    val (_, property) = annotation.host() ?: return PsiReference.EMPTY_ARRAY
                    val value = annotation.param<String>(parameterName)?.value ?: return PsiReference.EMPTY_ARRAY

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
