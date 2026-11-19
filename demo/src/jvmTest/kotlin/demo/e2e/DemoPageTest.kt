package demo.e2e

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.WaitUntilState
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Smoke checks for the demo page: the shell comes from the kit, the dashboard places its cards, its
 * buttons drive the grid through the wrapper, and the language switch relabels the page without
 * rebuilding the layout.
 *
 * The shell lives in a shadow root; Playwright's CSS pierces shadow boundaries, `document.querySelector`
 * does not — so the waits use locators instead of `waitForFunction`.
 */
@UsePlaywright(DemoOptions::class)
class DemoPageTest {
    @RetryingTest(maxAttempts = 2)
    fun `page renders the shell, the logo and an overview`(page: Page) {
        openPage(page)

        assertTrue(page.title().contains("ktjs-gridstack"), "title: ${page.title()}")
        assertTrue(page.locator("img.layout__logo").isVisible(), "the shell shows the project logo")
        assertEquals("Overview", page.locator("ktjs-demo-page h1").textContent())
        assertTrue(
            page.locator("vaadin-side-nav-item[data-route='dashboard']").isVisible(),
            "the sidebar lists the dashboard route",
        )
    }

    @RetryingTest(maxAttempts = 2)
    fun `the dashboard starts with four cards`(page: Page) {
        openPage(page)
        goToDashboard(page)
        waitForGrid(page)

        assertEquals("4", page.locator("[data-status]").getAttribute("data-cards"), "four sample cards")
        assertEquals("12", page.locator("[data-status]").getAttribute("data-columns"), "twelve columns")
        assertTrue(
            page.locator(".grid-stack .grid-stack-item").first().isVisible(),
            "GridStack lays the cards out inside the host",
        )
    }

    @RetryingTest(maxAttempts = 2)
    fun `the add button inserts a fifth card`(page: Page) {
        openPage(page)
        goToDashboard(page)
        waitForGrid(page)

        page.locator("[data-action=add]").click()

        assertEquals("5", page.locator("[data-status]").getAttribute("data-cards"), "a fifth card appears")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the column buttons change the column count`(page: Page) {
        openPage(page)
        goToDashboard(page)
        waitForGrid(page)

        page.locator("[data-action=narrow]").click()
        assertEquals("6", page.locator("[data-status]").getAttribute("data-columns"), "six columns")

        page.locator("[data-action=wide]").click()
        assertEquals("12", page.locator("[data-status]").getAttribute("data-columns"), "twelve columns")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the language switch relabels the toolbar and keeps the cards`(page: Page) {
        openPage(page)
        goToDashboard(page)
        waitForGrid(page)

        page.locator("[data-menu='lang']").first().click()
        page.locator("[data-action='lang:zh-Hans']").click()

        assertEquals("zh-Hans", page.locator("html").getAttribute("lang"))
        assertEquals("仪表盘", page.locator("vaadin-side-nav-item[data-route='dashboard']").textContent())
        assertEquals("新增卡片", page.locator("[data-action=add]").textContent())
        assertEquals("4", page.locator("[data-status]").getAttribute("data-cards"), "the cards survive the switch")
        assertTrue(
            (page.locator(".grid-stack .grid-stack-item").first().textContent() ?: "").contains("营收"),
            "the card text is rewritten in place",
        )
    }

    @RetryingTest(maxAttempts = 2)
    fun `page logs no console errors or failed requests`(page: Page) {
        val consoleErrors = mutableListOf<String>()
        val failedRequests = mutableListOf<String>()
        page.onConsoleMessage { message -> if (message.type() == "error") consoleErrors += message.text() }
        page.onRequestFailed { request -> failedRequests += "${request.url()} ${request.failure()}" }

        openPage(page)
        goToDashboard(page)
        waitForGrid(page)
        page.locator("[data-action=compact]").click()

        assertTrue(consoleErrors.isEmpty(), "console errors: $consoleErrors")
        assertTrue(failedRequests.isEmpty(), "failed requests: $failedRequests")
    }

    private fun goToDashboard(page: Page) {
        page.locator("vaadin-side-nav-item[data-route='dashboard']").click()
        page.waitForSelector("[data-grid]", Page.WaitForSelectorOptions().setTimeout(20_000.0))
    }

    private fun openPage(page: Page) {
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
    }

    /** GridStack lays the cards out on the first frame; wait until the readout has counted them. */
    private fun waitForGrid(page: Page) {
        page.locator("[data-status][data-cards='4']").waitFor(Locator.WaitForOptions().setTimeout(20_000.0))
        page.locator(".grid-stack .grid-stack-item").first().waitFor(Locator.WaitForOptions().setTimeout(20_000.0))
    }
}
