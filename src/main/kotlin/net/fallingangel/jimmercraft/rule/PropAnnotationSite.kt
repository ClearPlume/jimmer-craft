package net.fallingangel.jimmercraft.rule

import net.fallingangel.jimmerdto.lsi.LClass
import net.fallingangel.jimmerdto.lsi.LProperty
import kotlin.reflect.KClass

interface PropAnnotationSite {
    val host: LProperty
    val hostEntity: LClass
    fun <T : Any> param(name: String, type: KClass<T>): AnnotationValue<T>?
}

inline fun <reified T : Any> PropAnnotationSite.param(name: String) = param(name, T::class)
