package com.nicusa.testing.tests.geb

import geb.Browser
import org.junit.Test
import pages.DashboardPage
import pages.SearchResultsPage
import pages.SignInPage

class SearchTest {

    @Test
    public void testSignIn() {
        def browser = new Browser();
        browser.drive {
            to DashboardPage
            largeSearchTextBox.value('ASPIRIN')
            largeSearchButton.click()
            at SearchResultsPage
            firstResult.click()
        }
    }

}
