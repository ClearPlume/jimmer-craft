package net.fallingangel.jimmercraft.completion

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import net.fallingangel.jimmerdto.lsi.LProperty

fun LProperty.lookupProperty(): LookupElement {
    return LookupElementBuilder.create(dependencyItem, name)
        .withIcon(dependencyItem.getIcon(0))
        .withTypeText(presentableType, true)
}
