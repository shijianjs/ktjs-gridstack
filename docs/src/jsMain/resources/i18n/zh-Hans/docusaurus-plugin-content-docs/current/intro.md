---
sidebar_position: 1
title: 简介
---

# 简介

`ktjs-gridstack` 是 [GridStack](https://github.com/gridstack/gridstack.js) 生态里 `gridstack` 14.1.0 的 Kotlin/JS wrapper：上游
`.d.ts` 里的每个类型和成员都落成可被编译器检查的 `external` 声明，不靠 `dynamic` 兜底。

封装覆盖 `gridstack` 14 整包：`GridStack` 类及其全部布局方法（`addWidget`、`load`、`save`、`compact`、`column`、`mode`、`update`、`removeWidget`、启用/禁用系列、下标辅助方法）、静态入口 `init` / `initAll` / `addGrid`、引擎 `GridStackEngine`、完整选项面（约 40 个字段的 `GridStackOptions`、`Responsive` + `Breakpoint`、`DDDragOpt` / `DDResizeOpt` / `DDRemoveOpt`）、控件类型（`GridStackWidget`、`GridStackNode`）、几何类型（`Position`、`Size`、`Rect`、`CellPosition`、`MousePosition`）以及事件名。

## 封装了哪些包

| 模块 | Gradle 坐标 | 上游 |
| --- | --- | --- |
| `ktjs-gridstack` | `io.github.shijianjs:ktjs-gridstack` | `gridstack` 14.1.0 |

一个 npm 包、一个 Gradle 模块、一个 Kotlin 包（`io.github.shijianjs.ktjs.gridstack`），类型与引擎在 `core` 子包。

## 许可证

wrapper 自身是 MIT。上游 `gridstack` 的许可证与 wrapper 不同，商用前请读
[许可证说明](guide/license.md)。

## 接下来

- [快速开始](guide/getting-started.md) — 加依赖、写第一个调用、跑构建。
- [API 概览](guide/api-overview.md) — 包结构、类型映射约定、常用写法。
- [演示页](guide/demo.md) — 浏览器里直接看效果。
