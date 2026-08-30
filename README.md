# JimmerCraft

IDE support for the [Jimmer](https://github.com/babyfish-ct/jimmer) framework.

Jimmer trades static guarantees for flexibility — it moves a set of invariants from compile time to runtime:

- object shapes
- property references inside annotations
- the existence of generated types

The type system says nothing about any of them.

JimmerCraft brings the ones that can be brought forward back to edit time.

JimmerCraft depends on [JimmerDTO](https://plugins.jetbrains.com/plugin/22618), installing JimmerCraft installs it as well, and it stays installed on its own if you later remove JimmerCraft.

Install JimmerDTO alone if you only write `.dto` files.

## Features

### Property names written as strings

Annotations such as `@OneToMany(mappedBy = "...")`, `@ManyToMany(mappedBy = "...")` and `@OneToOne(mappedBy = "...")` carry property names as plain strings that the IDE never checks.

JimmerCraft resolves them against the target entity:

- reports names the target entity does not declare
- completes the available properties
- navigates to the property declaration (Ctrl+Click)

Works in both Java and Kotlin.
