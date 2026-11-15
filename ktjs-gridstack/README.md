# ktjs-gridstack (module)

Kotlin/JS wrapper for [`gridstack`](https://www.npmjs.com/package/gridstack) 14.1.0.

One npm package, one Gradle module, one Kotlin package (`io.github.shijianjs.ktjs.gridstack`) with a
`core` sub-package for the types and the engine.

## Installation

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-gridstack:14.1.0-kt.1")
}
```

The page also needs the shipped stylesheet (`gridstack/dist/gridstack.min.css`). The demo copies it
out of `node_modules` with a Gradle `Copy` task rather than loading it from a CDN.

## Usage

```kotlin
import io.github.shijianjs.ktjs.gridstack.core.columnCount
import io.github.shijianjs.ktjs.gridstack.core.gridStackOptions
import io.github.shijianjs.ktjs.gridstack.core.gridStackWidget
import io.github.shijianjs.ktjs.gridstack.core.numberOrString
import io.github.shijianjs.ktjs.gridstack.initGrid

val grid = initGrid(
    gridStackOptions {
        column = columnCount(12.0)
        cellHeight = numberOrString("80px")
        margin = numberOrString(8.0)
    },
    element, // <div class="grid-stack">
)!!

grid.addWidget(gridStackWidget { x = 0.0; y = 0.0; w = 3.0; h = 2.0; content = "Revenue" })
grid.on("change") { _, _ -> println("${grid.getRow()} rows") }
```

- The grid element must carry the `grid-stack` class, and the stylesheet must be loaded, or the
  widgets stack on top of each other.
- Upstream accepts a selector wherever it takes an element; each such method has a `…BySelector`
  twin (`makeWidgetBySelector`, `removeWidgetBySelector`, `updateBySelector`, …).
- `GridItemHTMLElement` carries only `gridstackNode` (a Kotlin interface cannot extend the external
  `HTMLElement` class); use `asElement()` for the DOM surface.

## Build and test

```bash
./gradlew :ktjs-gridstack:jsBrowserTest   # Chromium: builds real grids, asserts positions
./gradlew :demo:e2eTest                   # Playwright against the demo page
```

After adding or upgrading an npm dependency, refresh the shared lock file with the official registry
(the lock file is committed, and CI has no mirror):

```bash
./gradlew --stop
$env:npm_config_registry='https://registry.npmjs.org'
./gradlew kotlinUpgradePackageLock
```

## License

MIT, same as the upstream `gridstack` package (Copyright (c) 2021-2025 Alain Dumesny).
