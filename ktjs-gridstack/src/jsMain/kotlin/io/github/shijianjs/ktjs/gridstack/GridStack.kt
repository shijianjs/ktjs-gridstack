package io.github.shijianjs.ktjs.gridstack

import io.github.shijianjs.ktjs.gridstack.core.AddRemoveOption
import io.github.shijianjs.ktjs.gridstack.core.CellPosition
import io.github.shijianjs.ktjs.gridstack.core.ColumnOptions
import io.github.shijianjs.ktjs.gridstack.core.CompactOptions
import io.github.shijianjs.ktjs.gridstack.core.GridItemHTMLElement
import io.github.shijianjs.ktjs.gridstack.core.GridStackDroppedHandler
import io.github.shijianjs.ktjs.gridstack.core.GridStackElementHandler
import io.github.shijianjs.ktjs.gridstack.core.GridStackEngine
import io.github.shijianjs.ktjs.gridstack.core.GridStackEvent
import io.github.shijianjs.ktjs.gridstack.core.GridStackEventHandler
import io.github.shijianjs.ktjs.gridstack.core.GridStackEvents
import io.github.shijianjs.ktjs.gridstack.core.GridStackMode
import io.github.shijianjs.ktjs.gridstack.core.GridStackNode
import io.github.shijianjs.ktjs.gridstack.core.GridStackNodesHandler
import io.github.shijianjs.ktjs.gridstack.core.GridStackOptions
import io.github.shijianjs.ktjs.gridstack.core.GridStackPartialOptions
import io.github.shijianjs.ktjs.gridstack.core.GridStackWidget
import io.github.shijianjs.ktjs.gridstack.core.MousePosition
import io.github.shijianjs.ktjs.gridstack.core.NumberOrString
import io.github.shijianjs.ktjs.gridstack.core.Position
import io.github.shijianjs.ktjs.gridstack.core.SavedLayout
import kotlin.js.JsAny
import kotlin.js.JsName
import kotlin.js.definedExternally
import web.html.HTMLElement

/**
 * The `gridstack` package, which exports the class by name rather than as a default export.
 *
 * A Kotlin `@JsModule` *class* is compiled as a default import, so the class is reached through this
 * object instead (`import * as gridstack from 'gridstack'`), and every static is a small typed
 * `js(…)` shim that takes the class value as its first argument.
 */
@JsModule("gridstack")
external object GridStackModule {
    /** The `GridStack` class itself. */
    @JsName("GridStack")
    val GridStackClass: JsAny
}

private val constructGridStack: (JsAny, HTMLElement, GridStackOptions?) -> GridStack =
    js("(cls, el, opts) => opts === null ? new cls(el) : new cls(el, opts)")

private val callInit: (JsAny, GridStackOptions?, HTMLElement?) -> GridStack? =
    js("(cls, opts, el) => cls.init(opts === null ? undefined : opts, el === null ? undefined : el)")

private val callInitBySelector: (JsAny, GridStackOptions?, String?) -> GridStack? =
    js("(cls, opts, selector) => cls.init(opts === null ? undefined : opts, selector === null ? undefined : selector)")

private val callInitAll: (JsAny, GridStackOptions?, String?) -> Array<GridStack> =
    js("(cls, opts, selector) => cls.initAll(opts === null ? undefined : opts, selector === null ? undefined : selector)")

private val callAddGrid: (JsAny, HTMLElement, GridStackOptions?) -> GridStack? =
    js("(cls, parent, opts) => opts === null ? cls.addGrid(parent) : cls.addGrid(parent, opts)")

/**
 * Creates a grid on [el]. Prefer [initGrid]: `GridStack.init` also returns the existing instance when
 * the element already is a grid.
 */
fun createGridStack(el: HTMLElement, options: GridStackOptions? = null): GridStack =
    constructGridStack(GridStackModule.GridStackClass, el, options)

/**
 * The main GridStack class: call [initGrid] once per grid element, then drive the layout through the
 * instance methods.
 *
 * The grid element must carry the `grid-stack` class, and every widget a `grid-stack-item` wrapper
 * with a `grid-stack-item-content` inside, for the shipped CSS to lay them out.
 *
 * Upstream also accepts a selector wherever it takes an element. Kotlin cannot overload on that union
 * implicitly, so each such method has a `…BySelector` twin.
 */
external class GridStack {
    /** The grid element this instance was built from. */
    var el: HTMLElement

    /** The options in effect. Read them; change them through [updateOptions] or the setters. */
    var opts: GridStackOptions

    /** The engine doing the non-DOM math (positioning, collisions, packing). */
    var engine: GridStackEngine

    /** Set when this grid is nested inside a widget of a parent grid. */
    var parentGridNode: GridStackNode?

    /** How long to wait for the move animation before sizing content. */
    var animationDelay: Double

    /** Adds a widget built from [w] and returns its element, or null when it does not fit. */
    fun addWidget(w: GridStackWidget): GridItemHTMLElement?

    /** Builds the default item divs and content for [n] without adding it to the grid. */
    fun createWidgetDivs(n: GridStackNode): HTMLElement

    /** Converts the widget [el] into a sub-grid and returns it. */
    fun makeSubGrid(
        el: HTMLElement,
        ops: GridStackOptions? = definedExternally,
        nodeToAdd: GridStackNode? = definedExternally,
        saveContent: Boolean? = definedExternally,
    ): GridStack

    /** Turns a previously created sub-grid back into a plain widget. */
    fun removeAsSubGrid(nodeThatRemoved: GridStackNode? = definedExternally)

    /**
     * Serialises the layout: an array of widgets by default, or the full options object (with
     * `children`) when [saveGridOpt] is true.
     */
    fun save(
        saveContent: Boolean? = definedExternally,
        saveGridOpt: Boolean? = definedExternally,
        saveCB: JsAny? = definedExternally,
        column: Double? = definedExternally,
    ): SavedLayout

    /** Updates, adds and removes widgets so the grid matches [items] — the inverse of [save]. */
    fun load(items: Array<GridStackWidget>, addRemove: AddRemoveOption? = definedExternally): GridStack

    /** Defers every relayout until called again with `false`, so a batch triggers one event. */
    fun batchUpdate(flag: Boolean? = definedExternally): GridStack

    /** The current cell height in pixels (converting a unit-carrying value when needed). */
    fun getCellHeight(forcePixel: Boolean? = definedExternally): Double

    /** Sets the cell height: pixels, a CSS length, `auto`, `initial`, `fill` or `0` for no styles. */
    fun cellHeight(`val`: NumberOrString? = definedExternally): GridStack

    /** The current cell width in pixels. */
    fun cellWidth(): Double

    /** Reclaims empty space; `list` keeps the reading order, `compact` may reorder. */
    fun compact(layout: CompactOptions? = definedExternally, doSort: Boolean? = definedExternally): GridStack

    /** Changes the column count, re-laying the widgets out with [layout] (`moveScale` by default). */
    fun column(column: Double, layout: ColumnOptions? = definedExternally): GridStack

    /** The current column count. */
    fun getColumn(): Double

    /** Every widget element, in DOM order, placeholder excluded. */
    fun getGridItems(): Array<GridItemHTMLElement>

    /** True while change events are suppressed (column change, load, size-to-content, ...). */
    fun isIgnoreChangeCB(): Boolean

    /** Destroys the instance. Pass `false` to leave the DOM in place. */
    fun destroy(removeDOM: Boolean? = definedExternally): GridStack

    /** Sets the packing mode (`top`, `float`, `list`, `compact`). */
    fun mode(`val`: GridStackMode): GridStack

    /** The current packing mode. */
    fun getMode(): GridStackMode

    /** Converts a pixel position (absolute `top`/`left`) into the cell under it. */
    fun getCellFromPixel(position: MousePosition, useDocRelative: Boolean? = definedExternally): CellPosition

    /** The current row count (at least `minRow`). */
    fun getRow(): Double

    /** True when the `x, y, w, h` area is free. */
    fun isAreaEmpty(x: Double, y: Double, w: Double, h: Double): Boolean

    /** Turns an existing element into a widget — the counterpart of [addWidget]. */
    fun makeWidget(els: HTMLElement, options: GridStackWidget? = definedExternally): GridItemHTMLElement

    /** Same as [makeWidget], with a selector. */
    @JsName("makeWidget")
    fun makeWidgetBySelector(els: String, options: GridStackWidget? = definedExternally): GridItemHTMLElement

    /** Registers an `added` / `change` / `removed` / `resizecontent` handler. */
    fun on(name: String, callback: GridStackNodesHandler): GridStack

    /** Registers an `added` / `change` / `removed` / `resizecontent` handler, by event constant. */
    @JsName("on")
    fun on(name: GridStackEvent, callback: GridStackNodesHandler): GridStack

    /** Registers a `drag*` / `resize*` handler, which receives the affected element. */
    @JsName("on")
    fun onElement(name: String, callback: GridStackElementHandler): GridStack

    /** Registers an `enable` / `disable` handler. */
    @JsName("on")
    fun onEvent(name: GridStackEvent, callback: GridStackEventHandler): GridStack

    /** Registers a `dropped` handler. */
    @JsName("on")
    fun onDropped(name: String, callback: GridStackDroppedHandler): GridStack

    /** Removes every handler registered for [name]. */
    fun off(name: String): GridStack

    /** Removes every handler registered for [name]. */
    @JsName("off")
    fun off(name: GridStackEvent): GridStack

    /** Removes every handler of every event. */
    fun offAll(): GridStack

    /** Removes the widget [els]; pass `false` to keep its DOM element. */
    fun removeWidget(
        els: HTMLElement,
        removeDOM: Boolean? = definedExternally,
        triggerEvent: Boolean? = definedExternally,
    ): GridStack

    /** Same as [removeWidget], with a selector. */
    @JsName("removeWidget")
    fun removeWidgetBySelector(
        els: String,
        removeDOM: Boolean? = definedExternally,
        triggerEvent: Boolean? = definedExternally,
    ): GridStack

    /** Removes every widget. */
    fun removeAll(removeDOM: Boolean? = definedExternally, triggerEvent: Boolean? = definedExternally): GridStack

    /** Turns the move animation on or off. */
    fun setAnimation(doAnimate: Boolean? = definedExternally, delay: Boolean? = definedExternally): GridStack

    /** Makes the grid permanently static (no drag and drop), unlike [disable]. */
    fun setStatic(
        `val`: Boolean,
        updateClass: Boolean? = definedExternally,
        recurse: Boolean? = definedExternally,
    ): GridStack

    /** Patches the grid options in place: only the passed keys change. */
    fun updateOptions(o: GridStackPartialOptions): GridStack

    /** Updates one widget's position, size or other properties. */
    fun update(els: HTMLElement, opt: GridStackWidget): GridStack

    /** Same as [update], with a selector. */
    @JsName("update")
    fun updateBySelector(els: String, opt: GridStackWidget): GridStack

    /** Grows or shrinks a widget so its content fits without scrolling. */
    fun resizeToContent(el: HTMLElement)

    /** Swaps a widget's width and height — what pressing `r` during a drag does. */
    fun rotate(els: HTMLElement, relative: Position? = definedExternally): GridStack

    /** Sets all four margins at once, from a number or a CSS shorthand string. */
    fun margin(value: NumberOrString): GridStack

    /** The margin in pixels, or null when the four sides differ. */
    fun getMargin(): Double?

    /** True when [node] would fit in the grid's vertical constraint. */
    fun willItFit(node: GridStackWidget): Boolean

    /** Tells the grid its container was resized, so it can re-check the column count. */
    fun onResize(clientWidth: Double? = definedExternally, clientHeight: Double? = definedExternally): GridStack

    /** Enables or disables dragging for the widget [els]. */
    fun movable(els: HTMLElement, `val`: Boolean): GridStack

    /** Same as [movable], with a selector. */
    @JsName("movable")
    fun movableBySelector(els: String, `val`: Boolean): GridStack

    /** Enables or disables resizing for the widget [els]. */
    fun resizable(els: HTMLElement, `val`: Boolean): GridStack

    /** Same as [resizable], with a selector. */
    @JsName("resizable")
    fun resizableBySelector(els: String, `val`: Boolean): GridStack

    /** Temporarily stops moving and resizing everywhere. */
    fun disable(recurse: Boolean? = definedExternally): GridStack

    /** Undoes [disable]. */
    fun enable(recurse: Boolean? = definedExternally): GridStack

    /** Enables or disables moving for every widget. */
    fun enableMove(doEnable: Boolean, recurse: Boolean? = definedExternally): GridStack

    /** Enables or disables resizing for every widget. */
    fun enableResize(doEnable: Boolean, recurse: Boolean? = definedExternally): GridStack

    /** Re-scans widget content for the drag handle, after it was rendered late. */
    fun refreshDragHandles(els: HTMLElement): GridStack

    /** Prepares [el] for drag and drop (normally done by [makeWidget]). */
    fun prepareDragDrop(el: HTMLElement, force: Boolean? = definedExternally): GridStack
}

/** `GridStack.init(options, element)`: the usual entry point. */
fun initGrid(options: GridStackOptions? = null, el: HTMLElement? = null): GridStack? =
    callInit(GridStackModule.GridStackClass, options, el)

/** `GridStack.init(options, selector)`. */
fun initGridBySelector(options: GridStackOptions? = null, selector: String? = null): GridStack? =
    callInitBySelector(GridStackModule.GridStackClass, options, selector)

/** `GridStack.addGrid(parent, options)`: build a whole grid from one options object. */
fun addGrid(parent: HTMLElement, options: GridStackOptions? = null): GridStack? =
    callAddGrid(GridStackModule.GridStackClass, parent, options)

/** `GridStack.initAll(options, selector)`: every `.grid-stack` element, or every match of [selector]. */
fun initAllGrids(options: GridStackOptions? = null, selector: String? = null): Array<GridStack> =
    callInitAll(GridStackModule.GridStackClass, options, selector)

/** Every event name [GridStack.on] understands. */
val gridStackEventNames: List<String> get() = GridStackEvents.all
