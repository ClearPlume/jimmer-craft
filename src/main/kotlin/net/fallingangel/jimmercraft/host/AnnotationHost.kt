package net.fallingangel.jimmercraft.host

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifierList
import net.fallingangel.jimmerdto.lsi.LClass
import net.fallingangel.jimmerdto.lsi.LName
import net.fallingangel.jimmerdto.lsi.LProperty
import net.fallingangel.jimmerdto.lsi.process
import net.fallingangel.jimmerdto.util.parent
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClass

data class AnnotationHost(
    val annotationName: LName,
    val entity: LClass,
    val property: LProperty,
)

fun PsiAnnotation.host(): AnnotationHost? {
    val annotationClass = resolveAnnotationType() ?: return null
    val annotationName = process(annotationClass) { className() } ?: return null

    val psiMethod = (owner as? PsiModifierList)?.parent as? PsiMethod ?: return null
    val entityClass = psiMethod.containingClass ?: return null

    val entity = process(entityClass) { lClass() } ?: return null
    val property = process(psiMethod) { lProperty(entity) } ?: return null
    return AnnotationHost(annotationName, entity, property)
}

fun KtAnnotationEntry.host(): AnnotationHost? {
    val annotationClass = analyze(this) {
        typeReference?.type?.symbol?.psi ?: return null
    }
    val annotationName = process(annotationClass) { className() } ?: return null

    val entityClass = containingClass() ?: return null
    val entity = process(entityClass) { lClass() } ?: return null

    val ktProperty = parent<KtModifierList>()?.parent as? KtProperty ?: return null
    val property = process(ktProperty) { lProperty(entity) } ?: return null
    return AnnotationHost(annotationName, entity, property)
}
