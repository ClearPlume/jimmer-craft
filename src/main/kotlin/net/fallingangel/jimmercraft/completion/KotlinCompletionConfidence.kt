package net.fallingangel.jimmercraft.completion

import com.intellij.codeInsight.completion.CompletionConfidence
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.ThreeState
import net.fallingangel.jimmerdto.util.haveParent
import org.jetbrains.kotlin.psi.KtAnnotationEntry

class KotlinCompletionConfidence : CompletionConfidence() {
    override fun shouldSkipAutopopup(editor: Editor, contextElement: PsiElement, psiFile: PsiFile, offset: Int): ThreeState {
        return if (contextElement.haveParent<KtAnnotationEntry>()) {
            ThreeState.NO
        } else {
            ThreeState.UNSURE
        }
    }
}
