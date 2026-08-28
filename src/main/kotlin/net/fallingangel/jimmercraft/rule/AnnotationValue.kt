package net.fallingangel.jimmercraft.rule

import com.intellij.psi.PsiElement

data class AnnotationValue<T>(val value: T, val psi: PsiElement)
