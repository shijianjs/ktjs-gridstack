---
sidebar_position: 2
title: 快速开始
---

# 快速开始

## 依赖

```kotlin
kotlin {
    js {
        binaries.executable()
        browser()
    }
    sourceSets {
        jsMain.dependencies {
            implementation("io.github.shijianjs:ktjs-gridstack:14.1.0-kt.1")
            // `kotlin-browser` 的 `web.*` 类型出现在公开 API 里，所以消费方要能编译到它们。
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.11.1")
        }
    }
}
```

要求 Kotlin 2.4.21 或更新版本；wrapper 输出 ESM，`es2015` 目标起可用。

## 注册插件

没有插件注册表：包按名字导出类。Kotlin/JS 会把 `@JsModule` **类**编译成默认导入，而 `gridstack` 没有默认导出，因此类通过模块对象取得，静态入口是函数：

```kotlin
import io.github.shijianjs.ktjs.gridstack.initGrid
import io.github.shijianjs.ktjs.gridstack.core.columnCount
import io.github.shijianjs.ktjs.gridstack.core.gridStackOptions

val grid = initGrid(gridStackOptions { column = columnCount(12.0) }, element)!!
```

其余入口是 `initGridBySelector` / `initAllGrids` / `addGrid`。`createGridStack` 是原始构造器，用于在已经是网格的元素上强制新建实例。

## 第一个调用

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
            content = "营收 — 12.4k"
        },
    )

    grid.on("change") { _, _ -> println("布局变化：${grid.getRow()} 行") }
}
```

元素需要 `grid-stack` 类与 `gridstack.min.css`；库创建的每个控件带 `grid-stack-item` 与 `grid-stack-item-content`，后者同时是拖动把手。

## 构建与测试

```bash
./gradlew build                     # 编译、Detekt、浏览器测试
./gradlew :ktjs-gridstack:jsBrowserTest     # 只跑 wrapper 的浏览器测试
./gradlew :docs:docusaurusStart     # 文档开发服务器
./gradlew pagesSite                 # 文档在 /，演示在 /demo/，Dokka 在 /api/
```
