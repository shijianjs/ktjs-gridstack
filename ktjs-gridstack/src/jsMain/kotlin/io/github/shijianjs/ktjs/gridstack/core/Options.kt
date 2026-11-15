package io.github.shijianjs.ktjs.gridstack.core

import js.objects.unsafeJso
import kotlin.js.JsAny
import web.html.HTMLElement

/**
 * Grid options, upstream `GridStackOptions`.
 *
 * Every field is optional: an unset field keeps the library default, which is why the builder
 * ([gridStackOptions]) writes only what the caller sets.
 */
external interface GridStackOptions {
    /** Accept widgets dragged in from other grids or from outside: a flag, a class name, or a check. */
    var acceptWidgets: JsAny?

    /** Resize handles always visible (`true`), on hover (`false`), or only on mobile (`"mobile"`). */
    var alwaysShowResizeHandle: JsAny?

    /** Animate moves (default `true`). */
    var animate: Boolean?

    /** Initialise the items already in the element (default `true`). */
    var auto: Boolean?

    /** One cell height: pixels, a CSS length, `auto`, `initial`, `fill`, or `0` for no styles. */
    var cellHeight: NumberOrString?

    /** Throttle for `cellHeight: auto` recalculation, in milliseconds (default `100`). */
    var cellHeightThrottle: Double?

    /** Unit of [cellHeight] when a unit-carrying string was given (internal). */
    var cellHeightUnit: String?

    /** Widgets to create on `load()` / `addGrid()`. */
    var children: Array<GridStackWidget>?

    /** Column count (default `12`); `auto` for a nested grid that matches its parent item. */
    var column: ColumnCount?

    /** Responsive column behaviour. */
    var columnOpts: Responsive?

    /** Extra class added on top of `.grid-stack` by `addGrid()`. */
    var `class`: String?

    /** Disallow dragging (default `false`). */
    var disableDrag: Boolean?

    /** Disallow resizing (default `false`). */
    var disableResize: Boolean?

    /** Overrides for the drag implementation. */
    var draggable: DDDragOpt?

    /** The engine class to construct, so a grid can subclass `GridStackEngine`. */
    var engineClass: JsAny?

    /** Packing mode (default `top`). */
    var mode: GridStackMode?

    /** Handle selector for dragging (default `.grid-stack-item-content`). */
    var handle: String?

    /** Handle class; when set, [handle] is ignored. */
    var handleClass: String?

    /** Widget class (default `grid-stack-item`). */
    var itemClass: String?

    /** Re-layout mode for a sub-grid being resized (default `list`). */
    var layout: ColumnOptions?

    /** Create widgets only once they scroll into view. */
    var lazyLoad: Boolean?

    /** Gap between a widget and its content (default `10`). */
    var margin: NumberOrString?

    /** Per-side margins; prefer the shorthand [margin]. */
    var marginTop: NumberOrString?

    /** Per-side margins; prefer the shorthand [margin]. */
    var marginRight: NumberOrString?

    /** Per-side margins; prefer the shorthand [margin]. */
    var marginBottom: NumberOrString?

    /** Per-side margins; prefer the shorthand [margin]. */
    var marginLeft: NumberOrString?

    /** Unit of [margin] when a unit-carrying string was given (internal). */
    var marginUnit: String?

    /** Row cap (default `0`, no cap). */
    var maxRow: Double?

    /** Row floor, so an empty grid does not collapse. */
    var minRow: Double?

    /** CSP nonce for the `<style>` elements the library creates. */
    var nonce: String?

    /** Placeholder class (default `grid-stack-placeholder`). */
    var placeholderClass: String?

    /** Placeholder content (default empty). */
    var placeholderText: String?

    /** Overrides for the resize implementation. */
    var resizable: DDResizeOpt?

    /** Allow removal by dragging out: a flag, or the selector of a trash area. */
    var removable: JsAny?

    /** Overrides for the remove implementation. */
    var removableOptions: DDRemoveOpt?

    /** Fixed row count: shorthand for `minRow = maxRow = row` (default `0`, unconstrained). */
    var row: Double?

    /** Right-to-left layout: `true`, `false`, or `auto` (the default). */
    var rtl: JsAny?

    /** Size each widget to its content height instead of its `h`. */
    var sizeToContent: Boolean?

    /** Make the grid permanently static. */
    var staticGrid: Boolean?

    /** Deprecated upstream: styles are CSS variables now. */
    var styleInHead: Boolean?

    /** Options inherited by sub-grids created inside this grid. */
    var subGridOpts: GridStackOptions?

    /** Create sub-grids by dragging an item fully over another. */
    var subGridDynamic: Boolean?
}

/** A plain options object with every field editable in place; used by `updateOptions`. */
external interface GridStackPartialOptions

/** Builds a [GridStackOptions]; unset fields stay absent so the library defaults apply. */
inline fun gridStackOptions(block: GridStackOptions.() -> Unit): GridStackOptions {
    val options = unsafeJso<GridStackOptions>()
    options.block()
    return options
}

/** A patch for `updateOptions`: only the fields you set are changed. */
inline fun gridStackPartialOptions(block: GridStackPartialOptions.() -> Unit): GridStackPartialOptions {
    val options = unsafeJso<GridStackPartialOptions>()
    options.block()
    return options
}

/** Responsive column configuration, upstream `Responsive`. */
external interface Responsive {
    /** Target column width (±50%) used to pick a column count automatically. */
    var columnWidth: Double?

    /** Highest column count considered (default `12`). */
    var columnMax: Double?

    /** Explicit width to column breakpoints instead of [columnWidth]. */
    var breakpoints: Array<Breakpoint>?

    /** Whether the breakpoints apply to the window width instead of the grid width. */
    var breakpointForWindow: Boolean?

    /** Re-layout mode when the column count changes. */
    var layout: ColumnOptions?
}

/** One responsive breakpoint. */
external interface Breakpoint {
    /** Width at or below which this breakpoint applies. */
    var w: Double?

    /** Column count to use. */
    var c: Double

    /** Re-layout mode for this breakpoint only. */
    var layout: ColumnOptions?
}

/** A breakpoint as a plain object. */
fun breakpoint(c: Double, w: Double? = null, layout: ColumnOptions? = null): Breakpoint =
    unsafeJso {
        this.c = c
        if (w != null) this.w = w
        if (layout != null) this.layout = layout
    }

/** Drag implementation options, upstream `DDDragOpt`. */
external interface DDDragOpt {
    /** Class selector of the draggable part (default `.grid-stack-item-content`). */
    var handle: String?

    /** Where the dragged helper is appended (default `body`). */
    var appendTo: String?

    /** Only place the item after the user pauses. */
    var pause: JsAny?

    /** Scroll the container while dragging (default `true`). */
    var scroll: Boolean?

    /** Elements that should not start a drag, comma separated. */
    var cancel: String?

    /** Drag helper: `clone`, or a factory of your own. */
    var helper: JsAny?

    var start: ((web.events.Event, DDUIData) -> Unit)?
    var stop: ((web.events.Event) -> Unit)?
    var drag: ((web.events.Event, DDUIData) -> Unit)?
    var rtl: Boolean?
}

/** Resize implementation options, upstream `DDResizeOpt`. */
external interface DDResizeOpt {
    /** Hide the handles until hover (default `true` on desktop, `false` on mobile). */
    var autoHide: Boolean?

    /** Dirties to resize from, e.g. `"e, se, s, sw, w"` (default `se`). */
    var handles: String?

    /** Element or selector used instead of the generated handle. */
    var element: JsAny?
}

/** Remove implementation options, upstream `DDRemoveOpt`. */
external interface DDRemoveOpt {
    /** Class that may be removed (default `itemClass`). */
    var accept: String?

    /** Class that may not be removed. */
    var decline: String?
}

/** Per-widget print options. */
external interface PrintOptions {
    /** Do not print this widget. */
    var hide: Boolean?

    /** Start a new page before this widget. */
    var pageBreak: Boolean?

    /** Printed page orientation (default `portrait`). */
    var orientation: String?

    /** Allow the widget to break across pages instead of being pushed to the next one. */
    var breakInside: Boolean?

    /** Application-specific print mode. */
    var mode: String?
}

/** Engine creation options, upstream `GridStackEngineOptions`. */
external interface GridStackEngineOptions {
    var column: Double?
    var maxRow: Double?
    var mode: GridStackMode?
    var nodes: Array<GridStackNode>?
    /** Called with the nodes that changed, so a host can update the DOM. */
    var onChange: ((Array<GridStackNode>) -> Unit)?
}

/** Builds a [GridStackEngineOptions]. */
inline fun gridStackEngineOptions(block: GridStackEngineOptions.() -> Unit): GridStackEngineOptions {
    val options = unsafeJso<GridStackEngineOptions>()
    options.block()
    return options
}

/** A widget element created by hand and passed to `makeWidget`. */
fun widgetElement(element: HTMLElement): GridItemHTMLElement = element.unsafeCast<GridItemHTMLElement>()
