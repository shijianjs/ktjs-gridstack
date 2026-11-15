# ktjs-gridstack

GridStack 的 Kotlin/JS 绑定 —— 响应式仪表盘布局

封装覆盖 `gridstack` 14：`GridStack` 类及其全部布局方法、静态入口 `init` / `initAll` / `addGrid`、引擎、完整选项面（含响应式断点与拖拽/缩放/移除选项组），以及控件与几何类型。包按名字导出类，因此模块绑定成 external object、静态入口变成带类型的函数；上游可用选择器调用的方法都另有 `…BySelector` 孪生。

- 上游包：[`gridstack`](https://github.com/gridstack/gridstack.js) 14.1.0
- 文档：https://shijianjs.github.io/ktjs-gridstack/
- 演示：https://shijianjs.github.io/ktjs-gridstack/demo/
- API：https://shijianjs.github.io/ktjs-gridstack/api/
- 英文说明：[README.md](README.md)

## 用法

```kotlin
implementation("io.github.shijianjs:ktjs-gridstack:14.1.0-kt.1")
```

## 构建

```bash
./gradlew build
./gradlew :ktjs-gridstack:jsBrowserTest
./gradlew :demo:e2eTest
./gradlew pagesSite
```

## 许可证

wrapper 与上游 `gridstack` 同为 MIT；版权声明见文档的「许可证」页。
