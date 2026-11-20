package demo.page.overview

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import demo.API_URL
import demo.DOCS_URL
import demo.SOURCE_URL
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.anchor
import io.github.shijianjs.ktjs.demo.page.codeText
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.row
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.element.unaryPlus
import web.html.HtmlTagName

/** Every string this page shows, in one language. */
private class OverviewTexts(
    val title: String,
    val summary: String,
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val purposeHeading: String,
    val body: String,
    val bullets: List<String>,
    val optionsHeading: String,
    val optionsBody: String,
    val nextHeading: String,
    val nextBody: String,
)

private val overviewEnglish: OverviewTexts = OverviewTexts(
    title = "Overview",
    summary = "A Kotlin/JS wrapper for the GridStack dashboard engine: typed options, typed widgets, " +
        "and every command reachable from Kotlin.",
    docsLink = "Documentation",
    sourceLink = "Source repository",
    apiLink = "API reference",
    purposeHeading = "What it wraps",
    body = "GridStack lays cards out on a column grid and lets the user drag and resize them; the " +
        "wrapper makes its options and its widgets typed objects. Instead of an untyped literal, " +
        "`gridStackOptions { column = columnCount(12.0); cellHeight = numberOrString(\"80px\") }` is " +
        "checked, `gridStackWidget { x = 0.0; w = 3.0; content = … }` describes a card, and the " +
        "`change` event arrives as `GridStack.on`.",
    bullets = listOf(
        "initGrid(gridStackOptions { … }, element) — one engine over one container.",
        "grid.addWidget(gridStackWidget { … }) — a card, as data.",
        "grid.on(\"change\") { … } — the engine's own event, not a DOM listener.",
    ),
    optionsHeading = "Options are values",
    optionsBody = "Columns, cell height, margins and the packing mode are all part of the options " +
        "value, so a dashboard's layout is a `gridStackOptions` block instead of a dictionary of " +
        "strings. Changing the column count later is one call: `grid.column(6.0)`.",
    nextHeading = "Reading order",
    nextBody = "The overview, then the dashboard: drag a card, resize it, and watch the readout " +
        "follow the engine.",
)

private val overviewChinese: OverviewTexts = OverviewTexts(
    title = "概览",
    summary = "GridStack 仪表盘引擎的 Kotlin/JS 封装：选项有类型、卡片有类型，每条命令都能从 Kotlin 发起。",
    docsLink = "使用文档",
    sourceLink = "源码仓库",
    apiLink = "API 文档",
    purposeHeading = "它封装了什么",
    body = "GridStack 把卡片排在列网格上，并允许拖动、缩放；封装把选项与卡片都变成有类型的对象：" +
        "不再是字面量，`gridStackOptions { column = columnCount(12.0); cellHeight = numberOrString(\"80px\") }` " +
        "会被检查，`gridStackWidget { x = 0.0; w = 3.0; content = … }` 描述一张卡片，" +
        "`change` 事件通过 `GridStack.on` 抵达。",
    bullets = listOf(
        "initGrid(gridStackOptions { … }, element) —— 一个容器上的一个引擎。",
        "grid.addWidget(gridStackWidget { … }) —— 卡片本身就是数据。",
        "grid.on(\"change\") { … } —— 引擎自己的事件，而不是 DOM 监听。",
    ),
    optionsHeading = "选项就是值",
    optionsBody = "列数、单元格高度、间距与排列模式都属于同一个选项值，所以一个仪表盘的版式是 " +
        "`gridStackOptions` 块，而不是一堆字符串字典。之后改列数是调用一次：`grid.column(6.0)`。",
    nextHeading = "建议顺序",
    nextBody = "先看概览，再看仪表盘：拖一张卡片、改一次大小，读数会跟着引擎走。",
)

private val overviewTexts: Translations<OverviewTexts> = texts(
    Langs.EN to overviewEnglish,
    Langs.ZH_HANS to overviewChinese,
)

/**
 * Landing page: what the library wraps, how it is consumed, where the reference lives.
 */
internal class OverviewPage : DemoPage() {

    private val texts = Binder(overviewTexts)


    init {
        addLink(texts.bind(anchor("", DOCS_URL, external = true), { it.docsLink }))
        addLink(texts.bind(anchor("", API_URL, external = true), { it.apiLink }))
        addLink(texts.bind(anchor("", SOURCE_URL, external = true), { it.sourceLink }))

        val purpose = sectionBlock()
        texts.bind(purpose.heading, { it.purposeHeading })
        // One bound <li> per bullet, so a language switch rewrites each line in place.
        val bullets = HtmlTagName.ul {
            List(3) { index -> texts.bind(+HtmlTagName.li()) { it.bullets.getOrNull(index).orEmpty() } }
        }
        purpose.body.append(texts.bind(paragraph(), { it.body }), bullets)

        val options = sectionBlock()
        texts.bind(options.heading, { it.optionsHeading })
        options.body.append(
            texts.bind(paragraph(), { it.optionsBody }),
            row(codeText("gridStackOptions { column = columnCount(12.0); cellHeight = numberOrString(\"80px\") }")),
        )

        val next = sectionBlock()
        texts.bind(next.heading, { it.nextHeading })
        next.body.append(texts.bind(paragraph(), { it.nextBody }))

        body.append(purpose.root, options.root, next.root)
    }

    override fun applyTranslation(lang: Lang) {
        val current = overviewTexts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        texts.apply(lang)
    }

    companion object : CustomElementReference<OverviewPage>(OverviewPage::class.js) {
        val NAME: TagName<OverviewPage> = TagName("gridstack-overview-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<gridstack-overview-page>`. */
internal fun newOverviewPage(): OverviewPage {
    OverviewPage.define()
    return OverviewPage.NAME()
}
