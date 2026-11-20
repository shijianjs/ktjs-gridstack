package demo.page.dashboard

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.element.unaryPlus
import io.github.shijianjs.ktjs.gridstack.GridStack

/** One toolbar button: the command it runs on the grid. */
internal class Action(val id: String, val run: (DashboardPage, GridStack) -> Unit)
