package net.fallingangel.jimmercraft.rule

interface Rule {
    operator fun invoke(site: PropAnnotationSite): List<Diagnostic>
}
