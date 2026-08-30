package net.fallingangel.jimmercraft.completion

import com.intellij.codeInsight.completion.CompletionConfidence
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.ThreeState
import net.fallingangel.jimmercraft.annotation.name
import net.fallingangel.jimmercraft.facts.Completions
import net.fallingangel.jimmercraft.facts.JimmerFacts
import net.fallingangel.jimmerdto.util.parent
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtValueArgument

class KotlinCompletionConfidence : CompletionConfidence() {
    override fun shouldSkipAutopopup(editor: Editor, contextElement: PsiElement, psiFile: PsiFile, offset: Int): ThreeState {
        val annotation = contextElement.parent<KtAnnotationEntry>()
        return if (annotation != null) {
            val annotationName = annotation.name() ?: return ThreeState.UNSURE

            val parameter = contextElement.parent<KtValueArgument>() ?: return ThreeState.UNSURE
            val parameterName = parameter.name()

            if (JimmerFacts[Completions, annotationName to parameterName] != null) {
                ThreeState.NO
            } else {
                ThreeState.UNSURE
            }
        } else {
            ThreeState.UNSURE
        }
    }
}
