package demo

import io.github.shijianjs.ktjs.gridstack.core.columnCount
import io.github.shijianjs.ktjs.gridstack.core.gridStackOptions
import io.github.shijianjs.ktjs.gridstack.core.gridStackWidget
import io.github.shijianjs.ktjs.gridstack.initGrid
import kotlin.js.unsafeCast
import web.cssom.ClassName
import web.dom.document
import web.events.EventHandler
import web.html.HTMLElement
import web.html.HtmlTagName

private const val PROJECT_NAME = "ktjs-gridstack"

private val texts = Texts(
    mapOf<String, Pair<String, String>>(
        "h1" to ("$PROJECT_NAME demo" to "$PROJECT_NAME 演示"),
        "intro" to Pair(
            "A dashboard built with the real GridStack 14 engine: drag a card by its header, or " +
                "resize it from the bottom-right corner. The buttons below drive the same grid " +
                "through the wrapper's API.",
            "用真实 GridStack 14 引擎搭的仪表盘：拖动卡片标题可移动，拖右下角可缩放。下面的按钮通过 " +
                "wrapper 的 API 操作同一个网格。",
        ),
        "add" to ("Add a card" to "新增卡片"),
        "compact" to ("Compact" to "紧凑排列"),
        "wide" to ("12 columns" to "12 列"),
        "narrow" to ("6 columns" to "6 列"),
        "save" to ("Save layout" to "保存布局"),
        "status" to ("Cards" to "卡片数"),
        "rows" to ("Rows" to "行数"),
        "columns" to ("Columns" to "列数"),
        "mode" to ("Packing" to "排列模式"),
        "hint" to (
            "Everything on this page is the wrapper's API: `initGrid`, `addWidget`, `compact`, " +
                "`column`, `save`." to
                "页面上的一切都来自 wrapper 的 API：`initGrid`、`addWidget`、`compact`、`column`、`save`。"
        ),
    ),
)

private val titles = listOf("Revenue", "Signups", "Latency", "Errors", "Storage", "Retention")
private val bodies = listOf("12.4k", "3,208", "184 ms", "0.4%", "62 GB", "71%")

/** Entry point of the demo page: a live dashboard whose cards are real GridStack widgets. */
fun main() {
    val root = document.createElement(HtmlTagName.div).unsafeCast<HTMLElement>()
    root.className = ClassName("page")

    val heading = document.createElement(HtmlTagName.h1).unsafeCast<HTMLElement>()
    val intro = document.createElement(HtmlTagName.p).unsafeCast<HTMLElement>()
    intro.className = ClassName("hint")
    val hint = document.createElement(HtmlTagName.p).unsafeCast<HTMLElement>()
    hint.className = ClassName("hint small")

    val logo = document.createElement(HtmlTagName.img).unsafeCast<HTMLElement>()
    logo.className = ClassName("logo")
    logo.setAttribute("src", "img/$PROJECT_NAME-logo.png")
    logo.setAttribute("alt", "$PROJECT_NAME logo")

    val header = document.createElement(HtmlTagName.header).unsafeCast<HTMLElement>()
    header.className = ClassName("header")
    header.append(logo, heading, languageSwitch(texts))

    val gridElement = document.createElement(HtmlTagName.div).unsafeCast<HTMLElement>()
    gridElement.className = ClassName("grid-stack")
    gridElement.setAttribute("data-role", "grid")

    val grid = initGrid(
        gridStackOptions {
            column = columnCount(12.0)
            // A dashboard reads better without the move animation during the first paint.
            animate = false
            margin = io.github.shijianjs.ktjs.gridstack.core.numberOrString(8.0)
            cellHeight = io.github.shijianjs.ktjs.gridstack.core.numberOrString("80px")
        },
        gridElement,
    )!!

    // Four cards across the 12 columns, so the dashboard reads as a real one.
    val starts = listOf(0.0 to 0.0, 3.0 to 1.0, 6.0 to 2.0, 9.0 to 1.0)
    starts.forEachIndexed { index, (x, h) ->
        grid.addWidget(
            gridStackWidget {
                this.x = x
                y = 0.0
                w = 3.0
                this.h = h
                id = "card-$index"
                content = "${titles[index]} — ${bodies[index]}"
            },
        )
    }

    val status = document.createElement(HtmlTagName.p).unsafeCast<HTMLElement>()
    status.className = ClassName("view")

    fun renderStatus() {
        status.textContent = "${texts["status"]}: ${grid.getGridItems().size} · " +
            "${texts["rows"]}: ${grid.getRow()} · ${texts["columns"]}: ${grid.getColumn()} · " +
            "${texts["mode"]}: ${grid.getMode().unsafeCast<String>()}"
    }

    val buttons = listOf("add", "compact", "wide", "narrow", "save").map { key ->
        document.createElement(HtmlTagName.button).unsafeCast<HTMLElement>().also {
            it.setAttribute("type", "button")
            it.setAttribute("data-action", key)
        }
    }
    val row = document.createElement(HtmlTagName.div).unsafeCast<HTMLElement>()
    row.className = ClassName("row")
    row.append(*buttons.toTypedArray())

    buttons[0].onclick = EventHandler {
        val index = grid.getGridItems().size
        grid.addWidget(
            gridStackWidget {
                w = 3.0
                h = 2.0
                id = "card-$index"
                content = "${titles[index % titles.size]} — ${bodies[index % bodies.size]}"
            },
        )
        renderStatus()
    }
    buttons[1].onclick = EventHandler {
        grid.compact()
        renderStatus()
    }
    buttons[2].onclick = EventHandler {
        grid.column(12.0)
        renderStatus()
    }
    buttons[3].onclick = EventHandler {
        grid.column(6.0)
        renderStatus()
    }
    buttons[4].onclick = EventHandler {
        // `save()` is the serialisation the library offers; showing the widget count keeps the demo
        // honest without dumping a JSON blob into the page.
        grid.save()
        renderStatus()
    }

    // A `change` listener keeps the counters honest while the user drags or resizes cards.
    grid.on("change") { _, _ -> renderStatus() }

    fun renderTexts() {
        heading.textContent = texts["h1"]
        intro.textContent = texts["intro"]
        hint.textContent = texts["hint"]
        buttons[0].textContent = texts["add"]
        buttons[1].textContent = texts["compact"]
        buttons[2].textContent = texts["wide"]
        buttons[3].textContent = texts["narrow"]
        buttons[4].textContent = texts["save"]
        renderStatus()
    }
    texts.onChange { renderTexts() }
    renderTexts()

    val content = document.createElement(HtmlTagName.main).unsafeCast<HTMLElement>()
    content.className = ClassName("content")
    content.append(row, status, gridElement)

    root.append(header, intro, hint, content)
    document.body?.append(root)
}

