package net.fallingangel.jimmercraft.completion

import com.intellij.codeInsight.completion.CompletionConfidence
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.ThreeState
import net.fallingangel.jimmerdto.util.haveParent

class JavaCompletionConfidence : CompletionConfidence() {
    override fun shouldSkipAutopopup(editor: Editor, contextElement: PsiElement, psiFile: PsiFile, offset: Int): ThreeState {
        return if (contextElement.haveParent<PsiAnnotation>()) {
            ThreeState.NO
        } else {
            ThreeState.UNSURE
        }
    }
}
