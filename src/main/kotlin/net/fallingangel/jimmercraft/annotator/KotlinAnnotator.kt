package net.fallingangel.jimmercraft.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import net.fallingangel.jimmercraft.annotation.host
import net.fallingangel.jimmercraft.annotation.param
import net.fallingangel.jimmercraft.rule.PropAnnotationSite
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtVisitorVoid
import kotlin.reflect.KClass

class KotlinAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(KotlinAnnotatorVisitor(holder))
    }

    private class KotlinAnnotatorVisitor(private val holder: AnnotationHolder) : KtVisitorVoid() {
        @Suppress("DuplicatedCode")
        override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
            val (annotationName, entity, property) = annotationEntry.host() ?: return
            val site = object : PropAnnotationSite {
                override val host = property
                override val hostEntity = entity
                override fun <T : Any> param(name: String, type: KClass<T>) = annotationEntry.param(name, type)
            }
            holder.check(annotationName, site)
        }
    }
}
