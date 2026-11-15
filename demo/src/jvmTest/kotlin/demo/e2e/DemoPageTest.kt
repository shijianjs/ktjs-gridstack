package demo.e2e

import com.microsoft.playwright.Page
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.WaitUntilState
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Smoke checks for the demo page: the bundle loads, the dashboard renders, its buttons drive the
 * grid through the wrapper, and the language switch swaps every visible string.
 */
@UsePlaywright(DemoOptions::class)
class DemoPageTest {
    @RetryingTest(maxAttempts = 2)
    fun `page renders heading, logo and styles`(page: Page) {
        openPage(page)

        assertTrue(page.title().contains("ktjs-gridstack"), "title: ${page.title()}")
        assertEquals("ktjs-gridstack demo", page.locator("h1").textContent())
        assertTrue(page.locator("img.logo").isVisible(), "the demo serves the project logo")

        val background = page.locator(".grid-stack").evaluate("(node) => getComputedStyle(node).backgroundColor") as String
        assertTrue(background != "rgba(0, 0, 0, 0)", "the stylesheet applies to .grid-stack ($background)")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the dashboard starts with four cards`(page: Page) {
        openPage(page)

        waitForGrid(page)
        assertEquals(4, page.locator(".grid-stack .grid-stack-item").count(), "four cards")
        assertTrue(page.locator(".view").textContent().contains("4"), "the counter shows the card count")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the add button inserts a fifth card`(page: Page) {
        openPage(page)

        waitForGrid(page)
        page.locator("[data-action=add]").click()

        assertEquals(5, page.locator(".grid-stack .grid-stack-item").count(), "a fifth card appears")
        assertTrue(page.locator(".view").textContent().contains("5"), "the counter follows the grid")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the column buttons change the column count`(page: Page) {
        openPage(page)

        waitForGrid(page)
        page.locator("[data-action=narrow]").click()
        assertTrue(page.locator(".view").textContent().contains("6"), "6 columns are reported")

        page.locator("[data-action=wide]").click()
        assertTrue(page.locator(".view").textContent().contains("12"), "12 columns are reported")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the language switch swaps every visible string`(page: Page) {
        openPage(page)

        page.locator("button.lang").click()

        assertEquals("ktjs-gridstack 演示", page.locator("h1").textContent())
        assertEquals("zh-Hans", page.locator("html").getAttribute("lang"))
        assertEquals("新增卡片", page.locator(".content .row button").first().textContent())

        page.locator("button.lang").click()

        assertEquals("ktjs-gridstack demo", page.locator("h1").textContent())
        assertEquals("en", page.locator("html").getAttribute("lang"))
    }

    @RetryingTest(maxAttempts = 2)
    fun `page logs no console errors or failed requests`(page: Page) {
        val consoleErrors = mutableListOf<String>()
        val failedRequests = mutableListOf<String>()
        page.onConsoleMessage { message -> if (message.type() == "error") consoleErrors += message.text() }
        page.onRequestFailed { request -> failedRequests += "${request.url()} ${request.failure()}" }

        openPage(page)
        waitForGrid(page)
        page.locator("[data-action=compact]").click()

        assertTrue(consoleErrors.isEmpty(), "console errors: $consoleErrors")
        assertTrue(failedRequests.isEmpty(), "failed requests: $failedRequests")
    }

    private fun openPage(page: Page) {
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
    }

    /** GridStack lays the cards out on the first frame; wait for the items to have a size. */
    private fun waitForGrid(page: Page) {
        page.waitForFunction(
            "() => { const items = document.querySelectorAll('.grid-stack .grid-stack-item');" +
                " return items.length > 0 && items[0].getBoundingClientRect().height > 10; }",
        )
    }
}
