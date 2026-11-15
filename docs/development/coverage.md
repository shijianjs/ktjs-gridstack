# API Coverage

## 来源与范围

- 来源：`gridstack` 14.1.0（npm），源码克隆 `S:\workspace\github\gridstack\gridstack.js`
- 声明文件：`dist/gridstack.d.ts`、`dist/types.d.ts`、`dist/gridstack-engine.d.ts`、`dist/utils.d.ts`
- 核对日期：2026-11-16
- 范围：`gridstack` 包的入口导出 —— `GridStack` 类（实例方法与静态入口）、`GridStackEngine`、
  `types` 里的全部公开类型、`Utils` 的静态辅助函数。
- 不纳入统计：`angular/`、`react/`、`vue/` 三个框架适配子包（各自是独立入口，不属于本 wrapper），
  以及 `dd-*` 内部实现类。

## 统计

| 状态 | 数量 |
|---|---:|
| 总计 | 31 |
| 已完成 | 18 |
| 明确不做 | 5 |
| 待完成 | 8 |

已完成组（不逐项列名，实现见源码）：

1. `GridStack` 的构造与 `init` / `initAll` / `addGrid` 静态入口（`GridStack.kt` 的 `initGrid`、
   `initGridBySelector`、`initAllGrids`、`addGrid`、`createGridStack`）
2. 控件增删：`addWidget`、`createWidgetDivs`、`makeWidget`（元素与选择器两种入口）、`removeWidget`、
   `removeAll`、`update`（两种入口）
3. 布局：`compact`、`column`、`getColumn`、`mode`、`getMode`、`cellHeight`、`getCellHeight`、
   `cellWidth`、`margin`、`getMargin`、`getRow`、`isAreaEmpty`、`getCellFromPixel`、`batchUpdate`
4. 序列化：`save`（含 `SavedLayout` 联合与 `widgetsOrNull()`）、`load`（含 `AddRemoveOption`）
5. 交互开关：`movable` / `resizable`（两种入口）、`disable` / `enable`、`enableMove` / `enableResize`、
   `setAnimation`、`setStatic`、`prepareDragDrop`、`refreshDragHandles`
6. 事件：`on`（节点/元素/状态/`dropped` 四种回调形状）与 `off`、`offAll`，`GridStackEvents` 常量
7. `updateOptions`（`GridStackPartialOptions`）、`willItFit`、`onResize`、`destroy`、
   `resizeToContent`、`rotate`、`isIgnoreChangeCB`、`getGridItems`、`makeSubGrid`、`removeAsSubGrid`
8. 选项面：`GridStackOptions`（约 40 字段）、`Responsive`、`Breakpoint`、`DDDragOpt`、`DDResizeOpt`、
   `DDRemoveOpt`、`PrintOptions`、`GridStackEngineOptions`，以及对应 `unsafeJso` 构造器
9. 控件与几何类型：`GridStackWidget`、`GridStackNode`、`GridStackPosition`、`GridStackMoveOpts`、
   `Position`、`Size`、`Rect`、`DDUIData`、`MousePosition`、`CellPosition`、`GridItemHTMLElement`
10. `GridStackEngine`（`createEngine` 与 24 个公开方法，见 `core/Engine.kt`）

## 明确不做

| 名称 | 排除理由 |
|---|---|
| `angular/`、`react/`、`vue/` 三个适配子包 | 各自是独立的 npm 入口与框架绑定，Kotlin/JS 侧没有对应框架，纳入会引入无法使用的类型 |
| `dd-draggable`、`dd-droppable`、`dd-resizable`、`dd-resizable-handle`、`dd-touch` | 拖拽实现的内部类，上游以 `@internal` 为主，调用它们会绕过 `GridStack` 的状态管理 |
| `dd-manager` / `DDManager` | 单例内部状态（当前拖拽的 context），上游公开只为了内部类之间通信 |
| 头文件里的 `GridStack.GDRev` | 上游标注 `@internal`，版本由构建脚本注入 |
| `gridstack-all.js` 打包产物 | 同一份代码的另一种打包形式，不是独立 API |

## 待完成

### 工具与静态钩子

- [ ] `Utils` 的静态辅助函数（`sort`、`parseHeight`、`areaIntercept`、`cloneDeep` 等 28 个）
- [ ] 类级静态钩子（`addRemoveCB`、`saveCB`、`renderCB`、`updateCB`、`resizeToContentCB`、
      `resizeToContentParent`）
- [ ] `GridStack.setupDragIn` 的类型化封装（从工具栏拖入）
- [ ] `GridStack.registerEngine` 与 `engineClass` 的强类型（当前 `engineClass` 是 `JsAny?`）

### 拖拽实现

- [ ] `GridStack.getDD()` 返回的 `DDGridStack` / `DDDraggable` / `DDResizable` 类型
- [ ] `DDElement` 与 `DDBaseImplement` 的公开成员

### 回调

- [ ] 过滤器与回调的完整签名（`GridStackEventHandlerCallback` 的四种形状分别建模）
- [ ] 上游升级后的声明对齐

## 验证依据

| 命令 | 结果 |
|---|---|
| `./gradlew :ktjs-gridstack:compileKotlinJs` | 通过 |
| `./gradlew :ktjs-gridstack:jsBrowserTest` | 8 个用例全部通过（Chromium） |
| `./gradlew :demo:jsBrowserDistribution` | 通过 |
| `./gradlew :demo:e2eTest` | 通过（Playwright：卡片数、新增、列数切换、语言切换、无 console 报错） |
| `./gradlew :docs:docusaurusBuild` | en 与 zh-Hans 均成功 |
