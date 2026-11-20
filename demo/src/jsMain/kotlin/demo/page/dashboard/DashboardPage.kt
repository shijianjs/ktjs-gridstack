package demo.page.dashboard

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.grid
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.element.unaryPlus
import io.github.shijianjs.ktjs.gridstack.GridStack
import io.github.shijianjs.ktjs.gridstack.core.GridStackMode
import io.github.shijianjs.ktjs.gridstack.core.columnCount
import io.github.shijianjs.ktjs.gridstack.core.asElement
import io.github.shijianjs.ktjs.gridstack.core.gridStackOptions
import io.github.shijianjs.ktjs.gridstack.core.gridStackWidget
import io.github.shijianjs.ktjs.gridstack.core.numberOrString
import io.github.shijianjs.ktjs.gridstack.initGrid
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import kotlin.js.unsafeCast
import web.animations.requestAnimationFrame
import web.cssom.ClassName
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName

/** The library's own marker class: GridStack reads it to find the container. */
private val GRID_STACK: ClassName = ClassName("grid-stack")

/** Every string this page shows, in one language. */
private class DashboardTexts(
    val title: String,
    val summary: String,
    val gridHeading: String,
    val hint: String,
    /** The toolbar labels, in the order of [ACTIONS]. */
    val labels: List<String>,
    /** The readout's nouns: `Cards / rows / columns / packing`. */
    val cards: String,
    val rows: String,
    val columns: String,
    val packing: String,
    /** The sample cards: `title — value`, picked by position. */
    val cardTitles: List<String>,
    val cardValues: List<String>,
)

private val dashboardEnglish: DashboardTexts = DashboardTexts(
    title = "Dashboard",
    summary = "The real GridStack engine behind the wrapper: drag a card by its header or resize it " +
        "from the corner, while the toolbar drives the same grid through `addWidget`, `compact`, " +
        "`column` and `save`.",
    gridHeading = "Live grid",
    hint = "Everything below is the wrapper's API: `initGrid`, `addWidget`, `compact`, `column`, " +
        "`save`, and the `change` event.",
    labels = listOf("Add a card", "Compact", "12 columns", "6 columns", "Save layout"),
    cards = "Cards",
    rows = "Rows",
    columns = "Columns",
    packing = "Packing",
    cardTitles = listOf("Revenue", "Signups", "Latency", "Errors", "Storage", "Retention"),
    cardValues = listOf("12.4k", "3,208", "184 ms", "0.4%", "62 GB", "71%"),
)

private val dashboardChinese: DashboardTexts = DashboardTexts(
    title = "仪表盘",
    summary = "封装背后的真实 GridStack 引擎：拖动卡片标题移动、拖右下角缩放，工具栏则通过 " +
        "`addWidget`、`compact`、`column`、`save` 操作同一个网格。",
    gridHeading = "实时网格",
    hint = "下面的一切都来自 wrapper 的 API：`initGrid`、`addWidget`、`compact`、`column`、" +
        "`save`，以及 `change` 事件。",
    labels = listOf("新增卡片", "紧凑排列", "12 列", "6 列", "保存布局"),
    cards = "卡片数",
    rows = "行数",
    columns = "列数",
    packing = "排列模式",
    cardTitles = listOf("营收", "注册", "延迟", "错误率", "存储", "留存"),
    cardValues = listOf("12.4k", "3,208", "184 ms", "0.4%", "62 GB", "71%"),
)

private val dashboardTexts: Translations<DashboardTexts> = texts(
    Langs.EN to dashboardEnglish,
    Langs.ZH_HANS to dashboardChinese,
)

/** One toolbar button: the command it runs on the grid. */
private class Action(val id: String, val run: (DashboardPage, GridStack) -> Unit)

private val ACTIONS: List<Action> = listOf(
    Action("add") { page, grid -> page.addCard(grid) },
    Action("compact") { page, grid -> grid.compact(); page.refresh(grid) },
    Action("wide") { page, grid -> grid.column(12.0); page.refresh(grid) },
    Action("narrow") { page, grid -> grid.column(6.0); page.refresh(grid) },
    Action("save") { page, grid -> grid.save(); page.refresh(grid) },
)

/**
 * The dashboard page: a toolbar, a live grid, and a readout of what the engine currently holds.
 *
 * GridStack needs its own stylesheet. `index.html` links `gridstack.min.css` (that is how the file
 * ships), but the cards live in this page's shadow tree and document styles do not cross that
 * boundary, so the page re-hosts the sheet through
 * [io.github.shijianjs.ktjs.demo.page.DemoPage.addLinkedSheet].
 */
internal class DashboardPage : DemoPage() {

    private val texts = Binder(dashboardTexts)

    /** The strings the readout and the sample cards use; kept in step with the switch. */
    private var current: DashboardTexts = dashboardEnglish

    private var grid: GridStack? = null

    /** Counts the cards the toolbar added, so a click is visible even when the grid looks alike. */
    private var added = 0

    /**
     * Which sample card each label stands for.
     *
     * A card is a real `vaadin-card` the page puts inside the item GridStack created, so a language
     * switch rewrites the label in place; the layout the user arranged stays exactly as it is.
     */
    private val cardLabels = mutableMapOf<HTMLElement, Int>()

    private val viewport: HTMLElement = HtmlTagName.div {
        classList.add(GRID_STACK)
        setAttribute("data-grid", "")
    }

    /** Cards, rows, columns and packing: one line under the toolbar, marked for the e2e suite. */
    private val status = Readout()

    /** One button per action, each tagged so the end-to-end suite can address it by name. */
    private val buttons: List<ButtonElement> = ACTIONS.map { action ->
        createButton { setAttribute("data-action", action.id) }
    }


    init {
        addLinkedSheet("gridstack.min.css")

        val section = sectionBlock()
        texts.bind(section.heading, { it.gridHeading })
        texts.bind(section.hint, { it.hint })
        section.body.append(
            grid(minColumnWidth = 150, *buttons.toTypedArray()),
            status.root,
            exampleBox(null, viewport),
        )

        body.append(section.root)
        status.root.setAttribute("data-status", "")

        buttons.forEachIndexed { index, button ->
            val action = ACTIONS[index]
            button.clickEvent.addHandler {
                val target = grid ?: return@addHandler
                action.run(this, target)
            }
        }

        createOnceMounted()
    }

    /**
     * Creates the grid as soon as the container is in the document *and* laid out.
     *
     * GridStack measures the container to place its cells; a grid built while the page is still being
     * attached places every card at column zero.
     */
    private fun createOnceMounted() {
        if (grid != null) return
        if (!viewport.isConnected || viewport.clientWidth < 200) {
            requestAnimationFrame { _ -> createOnceMounted() }
            return
        }
        val created = initGrid(
            gridStackOptions {
                column = columnCount(12.0)
                // A dashboard reads better without the move animation during the first paint.
                animate = false
                margin = numberOrString(8.0)
                cellHeight = numberOrString("80px")
            },
            viewport,
        ) ?: return
        grid = created

        // A `change` listener keeps the readout honest while the user drags or resizes cards.
        created.on("change") { _, _ -> refresh(created) }

        // Four cards across the 12 columns, so the dashboard reads as a real one.
        val starts = listOf(0.0 to 1.0, 3.0 to 1.0, 6.0 to 2.0, 9.0 to 1.0)
        starts.forEachIndexed { index, (x, height) ->
            addCard(created, index, x, height)
        }
        refresh(created)
    }

    /** Adds one card to [grid]; the id is unique per call, so a click is visible on its own. */
    private fun addCard(grid: GridStack, index: Int, x: Double? = null, height: Double = 1.0) {
        val item = grid.addWidget(
            gridStackWidget {
                if (x != null) this.x = x
                y = 0.0
                w = 3.0
                h = height
                id = "card-$index"
            },
        ) ?: return
        // GridStack creates the item's content holder; the card inside it is a component, not a
        // styled `<div>`: the library only positions items, the visuals are Vaadin's.
        val holder = item.asElement().firstElementChild as? HTMLElement ?: return
        val label = HtmlTagName.p { textContent = cardText(current, index) }
        holder.append(exampleBox(null, label))
        cardLabels[label] = index
    }

    /** Adds one card after the sample dashboard; the id is unique per click. */
    fun addCard(grid: GridStack) {
        added += 1
        addCard(grid, grid.getGridItems().size)
        refresh(grid)
    }

    /** Re-reads the grid: cards, rows, columns and the packing mode. */
    fun refresh(grid: GridStack) {
        status.set(
            "${current.cards}: ${grid.getGridItems().size}" +
                " · ${current.rows}: ${grid.getRow().toInt()}" +
                " · ${current.columns}: ${grid.getColumn().toInt()}" +
                " · ${current.packing}: ${modeName(grid.getMode())}",
        )
        status.root.setAttribute("data-cards", "${grid.getGridItems().size}")
        status.root.setAttribute("data-columns", "${grid.getColumn().toInt()}")
    }

    override fun applyTranslation(lang: Lang) {
        current = dashboardTexts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        texts.apply(lang)
        buttons.forEachIndexed { index, button ->
            button.textContent = current.labels.getOrElse(index) { "" }
        }
        // The labels are the page's own nodes, so they are rewritten in place rather than rebuilt.
        cardLabels.forEach { (label, index) ->
            label.textContent = cardText(current, index)
        }
        grid?.let { refresh(it) }
    }

    companion object : CustomElementReference<DashboardPage>(DashboardPage::class.js) {
        val NAME: TagName<DashboardPage> = TagName("gridstack-dashboard-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** `title — value`: the one-line content every sample card carries. */
private fun cardText(texts: DashboardTexts, index: Int): String {
    val title = texts.cardTitles.getOrElse(index % texts.cardTitles.size) { "" }
    val value = texts.cardValues.getOrElse(index % texts.cardValues.size) { "" }
    return "$title — $value"
}

/** The packing mode's name, as the readout shows it. */
private fun modeName(mode: GridStackMode): String = mode.unsafeCast<String>()

/** Creates a defined `<gridstack-dashboard-page>`. */
internal fun newDashboardPage(): DashboardPage {
    DashboardPage.define()
    return DashboardPage.NAME()
}
