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

JimmerCraft works in both Java and Kotlin.

## Features

### Property names written as strings

Jimmer annotations carry property names as plain strings; the IDE checks none of them.

JimmerCraft resolves them against the entity model. Covered so far:

#### `mappedBy` — `@OneToOne`, `@OneToMany`, `@ManyToMany`

`mappedBy` names a property on the target entity.

Rename that property, or mistype the name, and nothing tells you until the annotation processor runs.

- reports invalid names
    - no such property on the target entity
    - the property is not an association
    - it does not point back to this entity
    - it lacks the matching annotation
    - it already declares its own `mappedBy`
- completes the properties that can be the inverse side
- navigates to the property declaration on Ctrl+Click
