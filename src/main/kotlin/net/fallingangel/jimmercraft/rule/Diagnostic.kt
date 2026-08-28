package net.fallingangel.jimmercraft.rule

import com.intellij.codeInsight.intention.CommonIntentionAction
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

data class Diagnostic(
    val message: String,
    val psi: PsiElement,
    val severity: HighlightSeverity,
    val type: ProblemHighlightType = ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
    val fixes: List<CommonIntentionAction> = emptyList(),
)
