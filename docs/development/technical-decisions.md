# Technical decisions

Decisions taken while wrapping `gridstack` 14.1.0. Each one is forced by the upstream declarations or
by a Kotlin/JS limitation; none of them is incidental.

## 1. The class is reached through a module object, not `@JsModule` on the class

`dist/gridstack.js` ends with `export { GridStack }` — a named export, no default. A Kotlin
`@JsModule` **class** is compiled as a *default* import, and webpack fails the build with

```text
export 'default' (imported as '…GridStack…') was not found in 'gridstack'
(possible exports: DDBaseImplement, …, GridStack, GridStackEngine, Utils, gridDefaults)
```

The same happened for `GridStackEngine`. Both therefore live in an external object
(`GridStackModule`, `EngineModule`) with a single `@JsName`-ed value, which compiles to a namespace
import, and every static is a typed `js(…)` shim that takes the class value as its first argument.

## 2. Statics become functions, with `undefined` for “not passed”

`GridStack.init(options = {}, elOrString = '.grid-stack')` distinguishes an omitted argument from an
explicit `null` (the defaults only apply for `undefined`). The shims pass `undefined` when the Kotlin
argument is null:

```text
(cls, opts, el) => cls.init(opts === null ? undefined : opts, el === null ? undefined : el)
```

The Kotlin entry points (`initGrid`, `initGridBySelector`, `initAllGrids`, `addGrid`) all default to
`null`, so a caller writes `initGrid(options, element)` or `initGridBySelector(options, ".grid")`.

## 3. `GridItemHTMLElement` cannot extend `HTMLElement`

Upstream declares `interface GridItemHTMLElement extends HTMLElement { gridstackNode?: GridStackNode }`.
kotlin-browser models `HTMLElement` as an **external class**, and an interface cannot extend a class —
Kotlin rejects it with “An interface cannot extend a class”. The wrapper therefore declares
`GridItemHTMLElement` with only the extra field, and hands the DOM surface back through
`asElement()`; `asGridItem()` goes the other way.

## 4. Selector-or-element became two methods

A dozen upstream methods take `GridStackElement = string | GridItemHTMLElement`. Kotlin cannot
overload on a union implicitly (the factories are ordinary functions, not conversions), so each such
method got a twin: `makeWidget` / `makeWidgetBySelector`, `removeWidget` / `removeWidgetBySelector`,
`update` / `updateBySelector`, `movable` / `movableBySelector`, `resizable` / `resizableBySelector`,
`init` / `initBySelector`. The twins carry `@JsName` so they reach the same JavaScript method.

## 5. Typed handler overloads, distinct Kotlin names

`on(name, callback)` is overloaded upstream by event name, because each event carries a different
payload. Kotlin cannot express that with one signature, and two `(Event, X) -> Unit` types erase to
the same JVM signature, so the wrapper declares four Kotlin functions — `on`, `onElement`, `onEvent`,
`onDropped` — all `@JsName("on")`. `off` has a `GridStackEvent` overload with the same treatment.

## 6. Literal unions are sealed-shaped types plus constant objects

`GridStackMode`, `CompactOptions`, `ColumnOptions` and `GridStackEvent` are string unions upstream.
Each is an opaque `external interface` with a `of(value: String)` factory, and the literals live in
`GridStackModes`, `CompactOptionsValues`, `ColumnOptionsValues`, `GridStackEvents` so a typo is a
compile error and `all` stays in one place.

## 7. `column` and `cellHeight` need unions, not `JsAny`

`column?: number | 'auto'` became `ColumnCount` (`columnCount(12.0)` / `autoColumnCount`), and
`cellHeight?: numberOrString` / `margin?: numberOrString` became `NumberOrString`
(`numberOrString(80.0)` / `numberOrString("80px")`). Both halves of each union are real call shapes,
so they are typed rather than dropped to `JsAny`.

## 8. `save()`'s return type is a union, so it is one type with two factories

`save()` returns `GridStackWidget[] | GridStackOptions`. `SavedLayout` is the opaque union with
`SavedLayout(widgets)` / `SavedLayout(options)`, and `widgetsOrNull()` (one `Array.isArray` shim)
narrows it to the widget list — the case every caller wants.

## 9. Inventory of deliberately loose types

| Member | Kotlin type | Reason |
| --- | --- | --- |
| `acceptWidgets` | `JsAny?` | `boolean \| string \| (element) => boolean` |
| `removable` | `JsAny?` | `boolean \| string` |
| `alwaysShowResizeHandle` | `JsAny?` | `true \| false \| "mobile"` |
| `rtl` | `JsAny?` | `boolean \| "auto"` |
| `engineClass` | `JsAny?` | `typeof GridStackEngine` (a class value) |
| `DDDragOpt.helper` | `JsAny?` | `"clone" \| (el) => el` |
| `DDDragOpt.pause` | `JsAny?` | `boolean \| number` |
| `DDResizeOpt.element` | `JsAny?` | `string \| HTMLElement` |
| `GridStackWidget.sizeToContent` | `JsAny?` | `boolean \| number` |
| `GridStackEngine.findEmptyPosition(after)` | `JsAny?` | `GridStackNode \| number` |
| `GridStack.addRemoveCB`-style statics | not declared | global hooks, see coverage |

Each row is a real upstream union, not a shortcut; all are listed in `coverage.md` as well.

## 10. Options are `unsafeJso` builders

`gridStackOptions { … }`, `gridStackWidget { … }`, `gridStackEngineOptions { … }` write only the
fields the caller set. That matters for three upstream behaviours: option merging (`defaults` treats
absent and `undefined` differently from a value), `autoPosition` (absent means “use x/y”),
`sizeToContent` (absent means “ask the grid”).

## 11. `Utils` is not wrapped

`Utils` is a class whose 28 members are static DOM helpers; reaching a static on an imported class
value needs one shim per member. The wrapper covers the grid API instead and records `Utils` as
“explicitly not done” in `coverage.md`, so nobody assumes it is there by accident.

## 12. The browser tests assert positions, not pixels

GridStack lays out against a real document size, so jsTest runs in Chromium (`browser { test { chromium() } }`
in the module build script). The assertions read the library's own state (`getColumn()`,
`node.x`, `node.y`, `getRow()`), which is deterministic, instead of `getBoundingClientRect()`, which
depends on the test viewport.

## 13. The lock file is regenerated against the official registry

`kotlin-js-store/package-lock.json` is committed so CI (no mirror) resolves what a local build
resolves. Adding `gridstack` required `gradlew --stop` plus `kotlinUpgradePackageLock` with
`npm_config_registry=https://registry.npmjs.org`.

## 14. The test bundle caches a stale main module

Kotlin/JS incremental builds occasionally reuse an old test bundle after the main sources change
(symptoms: a compile error that no longer exists, or an assertion failing on code you just replaced).
`./gradlew :ktjs-gridstack:clean :ktjs-gridstack:jsBrowserTest` is the reliable form; deleting build
subdirectories by hand is not.

## 15. Localised documentation lives directly under `current/`

Docusaurus resolves a translated doc by its **doc id**, so
`i18n/zh-Hans/docusaurus-plugin-content-docs/current/guide/api-overview.md` is the only correct place
for `guide/api-overview`; an extra `docs/` segment silently falls back to the English page.
