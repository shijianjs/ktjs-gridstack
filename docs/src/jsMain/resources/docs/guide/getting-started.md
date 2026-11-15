---
sidebar_position: 2
title: Getting started
---

# Getting started

## Dependency

```kotlin
kotlin {
    js {
        binaries.executable()
        browser()
    }
    sourceSets {
        jsMain.dependencies {
            implementation("io.github.shijianjs:ktjs-gridstack:14.1.0-kt.1")
            // The public API exposes `kotlin-browser`'s `web.*` types, so a consumer must be able to
            // compile against them.
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.11.1")
        }
    }
}
```

Kotlin 2.4.21 or newer; the module emits ESM and targets `es2015` or later.

## Register the plugins

There is no plugin registry: the package exports the classes by name. Kotlin/JS compiles a `@JsModule` *class* into a default import, which `gridstack` does not have, so the class is reached through the module object and the static entry points are functions:

```kotlin
import io.github.shijianjs.ktjs.gridstack.initGrid
import io.github.shijianjs.ktjs.gridstack.core.columnCount
import io.github.shijianjs.ktjs.gridstack.core.gridStackOptions

val grid = initGrid(gridStackOptions { column = columnCount(12.0) }, element)!!
```

`initGridBySelector` / `initAllGrids` / `addGrid` are the other entry points. `createGridStack` is the raw constructor, for when you want a new instance on an element that already is a grid.

## First call

```kotlin
import io.github.shijianjs.ktjs.gridstack.core.columnCount
import io.github.shijianjs.ktjs.gridstack.core.gridStackOptions
import io.github.shijianjs.ktjs.gridstack.core.gridStackWidget
import io.github.shijianjs.ktjs.gridstack.initGrid

fun buildDashboard(element: web.html.HTMLElement) {
    val grid = initGrid(
        gridStackOptions {
            column = columnCount(12.0)
            cellHeight = numberOrString("80px")
            margin = numberOrString(8.0)
        },
        element,
    )!!

    grid.addWidget(
        gridStackWidget {
            x = 0.0
            y = 0.0
            w = 3.0
            h = 2.0
            id = "revenue"
            content = "Revenue — 12.4k"
        },
    )

    grid.on("change") { _, _ -> println("layout changed: ${grid.getRow()} rows") }
}
```

The element needs the `grid-stack` class and `gridstack.min.css`; every widget the library creates carries `grid-stack-item` and a `grid-stack-item-content` that doubles as the drag handle.

## Build and test

```bash
./gradlew build                     # compile, Detekt, browser tests
./gradlew :ktjs-gridstack:jsBrowserTest     # only the wrapper's browser tests
./gradlew :docs:docusaurusStart     # documentation dev server
./gradlew pagesSite                 # docs at /, demo at /demo/, Dokka at /api/
```
