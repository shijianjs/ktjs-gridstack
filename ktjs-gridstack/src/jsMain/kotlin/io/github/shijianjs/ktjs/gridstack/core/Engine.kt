package io.github.shijianjs.ktjs.gridstack.core

import kotlin.js.JsAny
import kotlin.js.JsName
import kotlin.js.definedExternally

/** The `gridstack` module binding for the engine class (a named export, not a default one). */
@JsModule("gridstack")
external object EngineModule {
    /** The `GridStackEngine` class. */
    @JsName("GridStackEngine")
    val EngineClass: JsAny
}

private val constructEngine: (JsAny, GridStackEngineOptions?) -> GridStackEngine =
    js("(cls, opts) => opts === null ? new cls() : new cls(opts)")

/** Creates an engine, which is what `new GridStack.Engine(…)` does. */
fun createEngine(options: GridStackEngineOptions? = null): GridStackEngine =
    constructEngine(EngineModule.EngineClass, options)


/**
 * The layout engine, upstream `GridStackEngine`: everything the grid does without touching the DOM.
 *
 * It is also what `GridStack.Engine` exposes, and what an `engineClass` option replaces.
 */
external class GridStackEngine(opts: GridStackEngineOptions? = kotlin.js.definedExternally) {
    /** Column count (default 12). */
    var column: Double

    /** Row cap, `0` for none. */
    var maxRow: Double

    /** Every node in the engine. */
    var nodes: Array<GridStackNode>

    /** Nodes added since the last batch flush. */
    var addedNodes: Array<GridStackNode>

    /** Nodes removed since the last batch flush. */
    var removedNodes: Array<GridStackNode>

    /** True while relayouts are deferred. */
    var batchMode: Boolean

    /** The column count used as the layout cache key (12 unless overridden). */
    var defaultColumn: Double

    /** Defers packing until called again with `false`. */
    fun batchUpdate(flag: Boolean? = kotlin.js.definedExternally, doPack: Boolean? = kotlin.js.definedExternally): GridStackEngine

    /** The first node that collides with [skip] (or with [area]). */
    fun collide(
        skip: GridStackNode,
        area: GridStackNode? = kotlin.js.definedExternally,
        skip2: GridStackNode? = kotlin.js.definedExternally,
    ): GridStackNode?

    /** Every node that collides with [skip] (or with [area]). */
    fun collideAll(
        skip: GridStackNode,
        area: GridStackNode? = kotlin.js.definedExternally,
        skip2: GridStackNode? = kotlin.js.definedExternally,
    ): Array<GridStackNode>

    /** Swaps two nodes' positions, when both can move. */
    fun swap(a: GridStackNode, b: GridStackNode): Boolean?

    /** True when the `x, y, w, h` area is free. */
    fun isAreaEmpty(x: Double, y: Double, w: Double, h: Double): Boolean

    /** Reclaims empty space. */
    fun compact(
        layout: CompactOptions? = kotlin.js.definedExternally,
        doSort: Boolean? = kotlin.js.definedExternally,
    ): GridStackEngine

    /** Sorts the nodes by position; `1` ascending, `-1` descending. */
    fun sortNodes(dir: Double? = kotlin.js.definedExternally): GridStackEngine

    /** Fills in the defaults of [node] (size, constraints) before it is added. */
    fun prepareNode(node: GridStackNode, resizing: Boolean? = kotlin.js.definedExternally): GridStackNode

    /** Clamps [node] into the grid bounds. */
    fun nodeBoundFix(node: GridStackNode, resizing: Boolean? = kotlin.js.definedExternally): GridStackEngine

    /** The nodes whose position changed since the last flush. */
    fun getDirtyNodes(verify: Boolean? = kotlin.js.definedExternally): Array<GridStackNode>

    /** Finds a free slot for [node] and moves it there; false when nothing fits. */
    fun findEmptyPosition(
        node: GridStackNode,
        nodeList: Array<GridStackNode>? = kotlin.js.definedExternally,
        column: Double? = kotlin.js.definedExternally,
        after: JsAny? = kotlin.js.definedExternally,
    ): Boolean

    /** Adds [node], returning the node that was actually placed. */
    fun addNode(
        node: GridStackNode,
        triggerAddEvent: Boolean? = kotlin.js.definedExternally,
        after: JsAny? = kotlin.js.definedExternally,
    ): GridStackNode

    /** Removes [node]. */
    fun removeNode(
        node: GridStackNode,
        removeDOM: Boolean? = kotlin.js.definedExternally,
        triggerEvent: Boolean? = kotlin.js.definedExternally,
    ): GridStackEngine

    /** Removes every node. */
    fun removeAll(
        removeDOM: Boolean? = kotlin.js.definedExternally,
        triggerEvent: Boolean? = kotlin.js.definedExternally,
    ): GridStackEngine

    /** Applies a move and reports whether anything changed. */
    fun moveNodeCheck(node: GridStackNode, o: GridStackMoveOpts): Boolean

    /** True when [node] fits vertically. */
    fun willItFit(node: GridStackNode): Boolean

    /** True when the constrained position differs from [p]. */
    fun changedPosConstrain(node: GridStackNode, p: GridStackPosition): Boolean

    /** Moves [node] to the position in [o]. */
    fun moveNode(node: GridStackNode, o: GridStackMoveOpts): Boolean

    /** The current row count. */
    fun getRow(): Double

    /** Marks the start of an update on [node]. */
    fun beginUpdate(node: GridStackNode): GridStackEngine

    /** Applies the update started by [beginUpdate]. */
    fun endUpdate(): GridStackEngine

    /** The node list, cleaned for serialisation. */
    fun save(
        saveElement: Boolean? = kotlin.js.definedExternally,
        saveCB: JsAny? = kotlin.js.definedExternally,
        column: Double? = kotlin.js.definedExternally,
    ): Array<GridStackNode>

    /** Remembers a layout for [column], so switching back restores positions. */
    fun cacheLayout(
        nodes: Array<GridStackNode>,
        column: Double,
        clear: Boolean? = kotlin.js.definedExternally,
    ): GridStackEngine

    /** Remembers one node's layout for [column]. */
    fun cacheOneLayout(n: GridStackNode, column: Double): GridStackEngine

    /** Drops the cached layout of [n]. */
    fun removeNodeFromLayoutCache(n: GridStackNode)

    /** Strips runtime fields off [node] before it is saved. */
    fun cleanupNode(node: GridStackNode): GridStackEngine
}

