package io.github.shijianjs.ktjs.gridstack

import io.github.shijianjs.ktjs.gridstack.core.GridStackEvents
import io.github.shijianjs.ktjs.gridstack.core.GridStackModes
import io.github.shijianjs.ktjs.gridstack.core.GridStackWidget
import io.github.shijianjs.ktjs.gridstack.core.columnCount
import io.github.shijianjs.ktjs.gridstack.core.gridStackOptions
import io.github.shijianjs.ktjs.gridstack.core.gridStackWidget
import io.github.shijianjs.ktjs.gridstack.core.numberOrString
import io.github.shijianjs.ktjs.gridstack.core.widgetsOrNull
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import web.cssom.ClassName
import web.dom.document
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * The module's behavioural tests. They run in Chromium because GridStack is a DOM library: a grid
 * can only be laid out in a real document.
 */
class GridStackTest {
    @Test
    fun aGridCanBeInitialisedOnAnElement() {
        val element = freshGrid(markup = 3)
        val grid = initGrid(gridStackOptions { column = columnCount(3.0) }, element)

        assertNotNull(grid)
        assertEquals(3.0, grid.getColumn())
        assertEquals(3.0, grid.getGridItems().size.toDouble(), "three widgets are picked up")
    }

    @Test
    fun addingAWidgetPlacesItInTheFirstFreeCell() {
        val element = freshGrid(markup = 0)
        val grid = initGrid(gridStackOptions { column = columnCount(2.0) }, element)!!
        grid.addWidget(gridStackWidget { w = 2.0; h = 1.0; content = "first" })

        val node = grid.getGridItems().single().gridstackNode
        assertNotNull(node)
        assertEquals(0.0, node.x)
        assertEquals(1.0, node.h)
    }

    @Test
    fun savingAndLoadingRoundTripsTheLayout() {
        val element = freshGrid(markup = 2)
        val grid = initGrid(gridStackOptions { column = columnCount(4.0) }, element)!!

        val widgets = grid.save().widgetsOrNull()
        assertNotNull(widgets)
        assertEquals(2.0, widgets.size.toDouble())

        val target = initGrid(gridStackOptions { column = columnCount(4.0) }, freshGrid(markup = 0))!!
        target.load(widgets)
        assertEquals(2.0, target.getGridItems().size.toDouble(), "loading the saved layout recreates both widgets")
    }

    @Test
    fun compactingPullsWidgetsToTheTop() {
        val element = freshGrid(markup = 0)
        val grid = initGrid(gridStackOptions { column = columnCount(2.0) }, element)!!
        grid.addWidget(gridStackWidget { x = 0.0; y = 3.0; w = 2.0; h = 1.0; content = "low" })
        grid.compact()

        val node = grid.getGridItems().single().gridstackNode
        assertNotNull(node)
        assertEquals(0.0, node.y, "the only widget floats to row 0")
    }

    @Test
    fun aColumnChangeRekeysTheGrid() {
        val element = freshGrid(markup = 2)
        val grid = initGrid(gridStackOptions { column = columnCount(2.0) }, element)!!
        grid.column(6.0)

        assertEquals(6.0, grid.getColumn())
        assertTrue(grid.cellWidth() > 0.0, "cells still have a width after the column change")
    }

    @Test
    fun theChangeEventReachesTheHandler() {
        val element = freshGrid(markup = 0)
        val grid = initGrid(gridStackOptions { column = columnCount(2.0) }, element)!!
        var changed = 0
        grid.on(GridStackEvents.Added) { _, nodes -> changed += nodes.size }

        grid.addWidget(gridStackWidget { w = 2.0; h = 1.0; content = "added" })

        assertTrue(changed > 0, "adding a widget fires an added event")
    }

    @Test
    fun theModeAndMarginAccessorsReportWhatWasSet() {
        val element = freshGrid(markup = 1)
        val grid = initGrid(gridStackOptions { column = columnCount(2.0) }, element)!!
        grid.mode(GridStackModes.Float)
        grid.margin(numberOrString(4.0))

        assertEquals(GridStackModes.Float, grid.getMode())
        assertEquals(4.0, grid.getMargin())
    }

    @Test
    fun aWidgetDefinitionKeepsUnsetFieldsAbsent() {
        val widget: GridStackWidget = gridStackWidget { w = 2.0 }

        assertEquals(2.0, widget.w)
        assertEquals(null, widget.h, "h was never set, so it stays absent")
    }

    /** A grid element `markup` widgets wide, already carrying the classes the CSS expects. */
    private fun freshGrid(markup: Int): HTMLElement {
        val grid = document.createElement(HtmlTagName.div).unsafeCast<HTMLElement>()
        grid.className = ClassName("grid-stack")
        grid.setAttribute("data-role", "grid")
        repeat(markup) {
            val item = document.createElement(HtmlTagName.div).unsafeCast<HTMLElement>()
            item.className = ClassName("grid-stack-item")
            val content = document.createElement(HtmlTagName.div).unsafeCast<HTMLElement>()
            content.className = ClassName("grid-stack-item-content")
            content.textContent = "item $it"
            item.append(content)
            grid.append(item)
        }
        document.body?.append(grid)
        return grid
    }
}
