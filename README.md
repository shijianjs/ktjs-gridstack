# ktjs-gridstack

Kotlin/JS bindings for GridStack — responsive dashboard layout

The wrapper covers `gridstack` 14: the `GridStack` class with every layout method, the static `init` / `initAll` / `addGrid` entry points, the engine, the full options surface (including responsive breakpoints and the drag/resize/remove option groups), and the widget and geometry types. The package exports its class by name, so the module is bound as an external object and the statics become typed functions; every method that upstream lets you call with a selector also has a `…BySelector` twin.

- Upstream package: [`gridstack`](https://github.com/gridstack/gridstack.js) 14.1.0
- Docs: https://shijianjs.github.io/ktjs-gridstack/
- Demo: https://shijianjs.github.io/ktjs-gridstack/demo/
- API: https://shijianjs.github.io/ktjs-gridstack/api/
- 中文说明：[README.zh-CN.md](README.zh-CN.md)

## Usage

```kotlin
implementation("io.github.shijianjs:ktjs-gridstack:14.1.0-kt.1")
```

## Build

```bash
./gradlew build
./gradlew :ktjs-gridstack:jsBrowserTest
./gradlew :demo:e2eTest
./gradlew pagesSite
```

## License

The wrapper is MIT, same as the upstream `gridstack` — see the "License" page of the docs.
