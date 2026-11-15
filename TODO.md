# TODO

## ktjs-gridstack 上游 API 对齐

把上游 `gridstack` 14.1.0-kt.1 的类型声明逐个落成 Kotlin/JS 声明，并让 demo、文档、
测试与之一致。

- 总计：31
- 已完成：18
- 明确不做：5
- 待完成：8

详细清单：[coverage.md](docs/development/coverage.md)
技术决策：[technical-decisions.md](docs/development/technical-decisions.md)

### 当前待办

- [ ] `Utils` 的静态辅助函数（`sort`、`parseHeight`、`areaIntercept` 等 28 个）
- [ ] 类级静态钩子（`addRemoveCB`、`saveCB`、`renderCB`、`updateCB`、`resizeToContentCB`、`resizeToContentParent`）
- [ ] `GridStack.setupDragIn` 的类型化封装（从工具栏拖入）
- [ ] `GridStack.registerEngine` 与 `engineClass` 的类型化（当前 `engineClass` 是 `JsAny?`）
- [ ] `GridStack.getDD()` 返回的 `DDGridStack` / `DDDraggable` / `DDResizable` 类型
- [ ] 过滤器与回调的完整签名（`GridStackEventHandlerCallback` 的四种形状分别建模）
- [ ] `dd-*` 内部类的公开成员（`DDElement`、`DDManager`）
- [ ] 上游升级后的声明对齐

### 注意事项

- 只封装 MIT 授权的 `gridstack` 包，不含 `angular` / `react` / `vue` 三个框架适配子包（它们各自是独立入口，属于其他 wrapper 的职责）。
- `<div class="grid-stack">` 与 `gridstack.min.css` 是布局生效的前提，写在快速开始第一段。
- `HTMLElement` 无法被 Kotlin 接口继承，`GridItemHTMLElement` 因此只带 `gridstackNode`，DOM 面用 `asElement()` 取回。
- 有意放宽的类型逐条记在 `technical-decisions.md`。
