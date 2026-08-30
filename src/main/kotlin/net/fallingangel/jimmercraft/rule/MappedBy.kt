package net.fallingangel.jimmercraft.rule

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import net.fallingangel.jimmerdto.lsi.LClass
import net.fallingangel.jimmerdto.lsi.LName
import net.fallingangel.jimmerdto.lsi.LProperty
import net.fallingangel.jimmerdto.lsi.jimmer.JimmerAnnotations
import net.fallingangel.jimmerdto.lsi.jimmer.isEntityAssociation

class MappedBy(private val expectedInverse: LName) : Rule {
    val isAssociation = LProperty::isEntityAssociation
    val targetHostSelf: (LClass, LProperty) -> Boolean = { host, property -> host == property.targetClass }
    val hasInverseAnnotation: (LProperty) -> Boolean = { it.hasAnnotation(expectedInverse) }
    val noMappedByParam: (LProperty) -> Boolean = noMappedByParam@{ property ->
        val annotation = property.findAnnotation(expectedInverse) ?: return@noMappedByParam false
        annotation.findParam("mappedBy")?.value == null
    }

    override fun invoke(site: PropAnnotationSite): List<Diagnostic> {
        val (value, mappedBy) = site.param<String>("mappedBy") ?: return emptyList()
        val hostEntity = site.hostEntity
        val targetEntity = site.host.targetClass ?: return emptyList()
        val mappedByProp = targetEntity.findProperty(value)

        return buildList {
            // 空属性名称
            if (value.isBlank()) {
                add(Diagnostic("The 'mappedBy' cannot be blank", mappedBy, HighlightSeverity.ERROR))
                return@buildList
            }

            // 属性是否存在
            if (mappedByProp == null) {
                add(Diagnostic("No property '$value' is found in '${targetEntity.fqName}'", mappedBy, HighlightSeverity.ERROR))
                return@buildList
            }

            // 属性类型是否为实体
            if (!isAssociation(mappedByProp)) {
                add(Diagnostic("The property '$value' is not an association property", mappedBy, HighlightSeverity.ERROR))
                return@buildList
            }

            // 属性实体是否指回当前实体
            if (!targetHostSelf(hostEntity, mappedByProp)) {
                add(
                    Diagnostic(
                        "The association property '$value' does not target '${hostEntity.fqName}'",
                        mappedBy,
                        HighlightSeverity.ERROR,
                    )
                )
            }

            // 属性是否标注正确注解
            if (!hasInverseAnnotation(mappedByProp)) {
                add(
                    Diagnostic(
                        "The association property '$value' should be annotated with '${expectedInverse.fqName}'",
                        mappedBy,
                        HighlightSeverity.ERROR,
                    )
                )
                return@buildList
            }

            // 对向注解是否也包含 mappedBy 参数
            if (!noMappedByParam(mappedByProp)) {
                add(
                    Diagnostic(
                        "The association property '$value' is also declared with 'mappedBy'",
                        mappedBy,
                        HighlightSeverity.ERROR,
                    )
                )
            }
        }
    }

    fun candidates(host: LProperty): List<LProperty> {
        return host.targetClass?.properties.orEmpty().filter {
            isAssociation(it)
                    && targetHostSelf(host.containingLClass, it)
                    && hasInverseAnnotation(it)
                    && noMappedByParam(it)
        }
    }

    fun resolve(host: LProperty, value: String): PsiElement? {
        return host.targetClass?.findProperty(value)?.dependencyItem
    }

    companion object {
        val OneToMany = MappedBy(JimmerAnnotations.ManyToOne)
        val ManyToMany = MappedBy(JimmerAnnotations.ManyToMany)
        val OneToOne = MappedBy(JimmerAnnotations.OneToOne)
    }
}
