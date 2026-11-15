package io.github.shijianjs.ktjs.gridstack.core

import io.github.shijianjs.ktjs.gridstack.GridStack
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import web.dom.Element
import web.events.Event
import web.html.HTMLElement
import web.html.HTMLDocument

/** A number in pixels, or a CSS length string (`"10px"`, `"2rem"`, `"5px 10px 0 20px"`). */
external interface NumberOrString

/** `numberOrString` from the left, as a factory pair. */
fun numberOrString(number: Double): NumberOrString = number.unsafeCast<NumberOrString>()

/** `numberOrString` from the left, as a factory pair. */
fun numberOrString(css: String): NumberOrString = css.unsafeCast<NumberOrString>()

/** The column count option: a number, or `auto` for a nested grid that follows its parent item. */
external interface ColumnCount

/** A fixed column count. */
fun columnCount(columns: Double): ColumnCount = columns.unsafeCast<ColumnCount>()

/** `auto`: the grid takes the column count of the parent item it sits in. */
val autoColumnCount: ColumnCount = "auto".unsafeCast<ColumnCount>()

/** How widgets are laid out and kept in place. */
external interface GridStackMode

/** The `GridStackMode` literals, plus the factories for the shared `CompactOptions` branch. */
object GridStackModes {
    /** Top gravity packing (the default): widgets float up into empty space. */
    val Top: GridStackMode = "top".unsafeCast<GridStackMode>()

    /** No gravity: widgets stay where they were placed. */
    val Float: GridStackMode = "float".unsafeCast<GridStackMode>()

    /** Continuous sequential reflow, like a reorderable list. */
    val List: GridStackMode = "list".unsafeCast<GridStackMode>()

    /** Same reflow as `list`, but reuse truly empty gaps first. */
    val Compact: GridStackMode = "compact".unsafeCast<GridStackMode>()

    /** Any other string the upstream accepts (the union also contains `CompactOptions`). */
    fun of(value: String): GridStackMode = value.unsafeCast<GridStackMode>()

    /** Every literal this wrapper names. */
    val all: List<GridStackMode> get() = listOf(Top, Float, List, Compact)
}

/** Options for `compact()`: keep the reading order, or hunt for the tightest fit. */
external interface CompactOptions

/** The `CompactOptions` literals. */
object CompactOptionsValues {
    /** Keep the left to right order, leaving a gap when the next item does not fit. */
    val List: CompactOptions = "list".unsafeCast<CompactOptions>()

    /** May reorder items to fill every gap. */
    val Compact: CompactOptions = "compact".unsafeCast<CompactOptions>()

    /** Any other string, e.g. for a forward-compatible value. */
    fun of(value: String): CompactOptions = value.unsafeCast<CompactOptions>()
}

/** How widgets are re-laid out when the column count changes. */
external interface ColumnOptions

/** The `ColumnOptions` literals, plus the custom-layout branch. */
object ColumnOptionsValues {
    /** Scale and move by the ratio of the new column count to the old one (the default). */
    val MoveScale: ColumnOptions = "moveScale".unsafeCast<ColumnOptions>()

    /** Move positions but keep sizes. */
    val Move: ColumnOptions = "move".unsafeCast<ColumnOptions>()

    /** Scale sizes but keep positions. */
    val Scale: ColumnOptions = "scale".unsafeCast<ColumnOptions>()

    /** Leave everything alone unless an item no longer fits. */
    val None: ColumnOptions = "none".unsafeCast<ColumnOptions>()

    /** Any other string, e.g. `list` or `compact`, which are also accepted. */
    fun of(value: String): ColumnOptions = value.unsafeCast<ColumnOptions>()
}

/** Every event name `GridStack.on` accepts. */
external interface GridStackEvent

/** The `GridStackEvent` literals. */
object GridStackEvents {
    val Added: GridStackEvent = "added".unsafeCast<GridStackEvent>()
    val Change: GridStackEvent = "change".unsafeCast<GridStackEvent>()
    val Disable: GridStackEvent = "disable".unsafeCast<GridStackEvent>()
    val Drag: GridStackEvent = "drag".unsafeCast<GridStackEvent>()
    val DragStart: GridStackEvent = "dragstart".unsafeCast<GridStackEvent>()
    val DragStop: GridStackEvent = "dragstop".unsafeCast<GridStackEvent>()
    val Dropped: GridStackEvent = "dropped".unsafeCast<GridStackEvent>()
    val Enable: GridStackEvent = "enable".unsafeCast<GridStackEvent>()
    val Removed: GridStackEvent = "removed".unsafeCast<GridStackEvent>()
    val Resize: GridStackEvent = "resize".unsafeCast<GridStackEvent>()
    val ResizeStart: GridStackEvent = "resizestart".unsafeCast<GridStackEvent>()
    val ResizeStop: GridStackEvent = "resizestop".unsafeCast<GridStackEvent>()
    val ResizeContent: GridStackEvent = "resizecontent".unsafeCast<GridStackEvent>()

    /** Any other string, e.g. a space separated list of names. */
    fun of(value: String): GridStackEvent = value.unsafeCast<GridStackEvent>()

    /** Every literal, in the order upstream documents them. */
    val all: List<String> = listOf(
        "added",
        "change",
        "disable",
        "drag",
        "dragstart",
        "dragstop",
        "dropped",
        "enable",
        "removed",
        "resize",
        "resizestart",
        "resizestop",
        "resizecontent",
    )
}

/**
 * A widget element — the HTML element with the library's own data attached.
 *
 * It cannot be declared as `HTMLElement` plus a field: kotlin-browser models `HTMLElement` as an
 * `external class`, and a Kotlin interface cannot extend a class. This declaration therefore carries
 * only the extra field; reach the DOM surface through [asElement], which is a plain retype.
 */
external interface GridItemHTMLElement {
    /** The node backing this widget (position, size, id, ...). */
    var gridstackNode: GridStackNode?
}

/** Views a widget element as a plain element, so the DOM API is available again. */
fun GridItemHTMLElement.asElement(): HTMLElement = this.unsafeCast<HTMLElement>()

/** Views any element as a widget element, after `makeWidget` has attached its node. */
fun HTMLElement.asGridItem(): GridItemHTMLElement = this.unsafeCast<GridItemHTMLElement>()

/** A list of elements, which the library also accepts where it takes "an element". */
external interface GridStackElements

/** Wraps a list of elements as the single argument the library expects. */
fun GridStackElements(elements: Array<HTMLElement>): GridStackElements = elements.unsafeCast<GridStackElements>()

/** The x/y/w/h a widget occupies, all optional because reading a node may leave some unset. */
external interface GridStackPosition {
    /** Left column (0 based). */
    var x: Double?
    /** Top row (0 based). */
    var y: Double?
    /** Width in columns. */
    var w: Double?
    /** Height in rows. */
    var h: Double?
}

/** What to create on a drop, and how to serialise it back. */
external interface GridStackWidget : GridStackPosition {
    /** Ignore x/y and drop into the first free slot. */
    var autoPosition: Boolean?
    var minW: Double?
    var maxW: Double?
    var minH: Double?
    var maxH: Double?
    var noResize: Boolean?
    var noMove: Boolean?
    var locked: Boolean?
    /** Stored as `gs-id` on the element; [GridStack] uses it to match on `load()`. */
    var id: String?
    /** HTML appended as content (rendered through `renderCB`, plain text by default). */
    var content: String?
    var print: PrintOptions?
    var lazyLoad: Boolean?
    var sizeToContent: JsAny?
    var resizeToContentParent: String?
    var subGridOpts: GridStackOptions?
}

/** Builds a [GridStackWidget]. Unset fields stay absent, which is how the library tells "not set". */
inline fun gridStackWidget(block: GridStackWidget.() -> Unit): GridStackWidget {
    val widget = js.objects.unsafeJso<GridStackWidget>()
    widget.block()
    return widget
}

/** A widget definition plus its runtime state (element, grid, sub-grid). */
external interface GridStackNode : GridStackWidget {
    /** The widget's element. */
    var el: GridItemHTMLElement?
    /** The grid owning this node. */
    var grid: GridStack?
    /** The nested grid, when this widget hosts one. */
    var subGrid: GridStack?
    /** Used when the widget is created lazily, once it scrolls into view. */
    var visibleObservable: JsAny?
}

/** Options used by `GridStackEngine.moveNode`. */
external interface GridStackMoveOpts : GridStackPosition {
    var skip: GridStackNode?
    var pack: Boolean?
    var nested: Boolean?
    var cellWidth: Double?
    var cellHeight: Double?
    var marginTop: Double?
    var marginBottom: Double?
    var marginLeft: Double?
    var marginRight: Double?
    var rect: GridStackPosition?
    var resizing: Boolean?
    var collide: GridStackNode?
    var forceCollide: Boolean?
}

/** A widget's page position, in pixels. */
external interface Position {
    var top: Double
    /** X offset — from the left in LTR, from the right in RTL. */
    var left: Double
}

/** A pixel size. */
external interface Size {
    var width: Double
    var height: Double
}

/** A pixel rectangle. */
external interface Rect : Size, Position

/** The data handed to the drag and resize callbacks. */
external interface DDUIData {
    var position: Position?
    var size: Size?
    var draggable: HTMLElement?
}

/** An absolute pixel position, as `getCellFromPixel` wants it. */
external interface MousePosition {
    var top: Double
    var left: Double
}

/** A cell coordinate (column and row). */
external interface CellPosition {
    var x: Double
    var y: Double
}

/** `save()` returns widgets, or the whole options object when asked for the grid config. */
external interface SavedLayout

/** The serialised widget list. */
fun SavedLayout(widgets: Array<GridStackWidget>): SavedLayout = widgets.unsafeCast<SavedLayout>()

/** The full options object, `children` included. */
fun SavedLayout(options: GridStackOptions): SavedLayout = options.unsafeCast<SavedLayout>()

/** The widgets in a [SavedLayout], or null when it holds the options object instead. */
fun SavedLayout.widgetsOrNull(): Array<GridStackWidget>? = savedWidgets(this)

private val savedWidgets: (SavedLayout) -> Array<GridStackWidget>? =
    js("(saved) => Array.isArray(saved) ? saved : null")

/** `load()`'s `addRemove` argument: a flag, or a callback that decides per widget. */
external interface AddRemoveOption

/** `true` adds and removes missing widgets; `false` only updates existing ones. */
fun AddRemoveOption(enabled: Boolean): AddRemoveOption = enabled.unsafeCast<AddRemoveOption>()

/** A callback doing the add/remove itself. */
fun AddRemoveOption(callback: (HTMLElement, GridStackWidget, Boolean, Boolean) -> HTMLElement?): AddRemoveOption =
    callback.unsafeCast<AddRemoveOption>()

/** Receives the event only (the `enable` / `disable` events). */
typealias GridStackEventHandler = (Event) -> Unit

/** Receives the event and the affected element (`drag*` / `resize*`). */
typealias GridStackElementHandler = (Event, GridItemHTMLElement) -> Unit

/** Receives the event and the affected nodes (`added` / `change` / `removed`). */
typealias GridStackNodesHandler = (Event, Array<GridStackNode>) -> Unit

/** Receives the event and the drop's previous and new node (`dropped`). */
typealias GridStackDroppedHandler = (Event, GridStackNode?, GridStackNode) -> Unit

/** The union of the four handler shapes; use the typed `on…` overloads instead when you can. */
typealias GridStackEventHandlerCallback = (Event, JsAny?, JsAny?) -> Unit

/** The document or element the library resolves selectors against. */
external interface GridStackRoot

/** A document root. */
fun GridStackRoot(document: HTMLDocument): GridStackRoot = document.unsafeCast<GridStackRoot>()

/** An element root. */
fun GridStackRoot(element: Element): GridStackRoot = element.unsafeCast<GridStackRoot>()


