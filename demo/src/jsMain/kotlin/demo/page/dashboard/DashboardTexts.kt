package demo.page.dashboard

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.element.unaryPlus

/** Every string this page shows, in one language. */
internal class DashboardTexts(
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
