package net.fallingangel.jimmercraft.rule

import com.intellij.lang.annotation.HighlightSeverity
import net.fallingangel.jimmerdto.lsi.LName
import net.fallingangel.jimmerdto.lsi.jimmer.isEntityAssociation

class MappedBy(private val expectedInverse: LName) : Rule {
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
            if (!mappedByProp.isEntityAssociation) {
                add(Diagnostic("The property '$value' is not an association property", mappedBy, HighlightSeverity.ERROR))
                return@buildList
            }

            val mappedByTargetEntity = mappedByProp.targetClass

            // 属性实体是否指回当前实体
            if (hostEntity != mappedByTargetEntity) {
                add(
                    Diagnostic(
                        "The association property '$value' does not target '${hostEntity.fqName}'",
                        mappedBy,
                        HighlightSeverity.ERROR,
                    )
                )
            }

            // 属性是否标注正确注解
            val expectedAnnotation = mappedByProp.annotations.find { it.fqName == expectedInverse.fqName }
            if (expectedAnnotation == null) {
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
            if (expectedAnnotation.params.find { it.name == "mappedBy" }?.value != null) {
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
}
