package pages

import geb.Page

/**
 * Created by mchurch on 6/28/15.
 */
class SearchResultsPage extends Page {
    static at = {
        waitFor(10) { $('#search-results > div > div > div > div.page-header > h2').text().startsWith("SEARCH RESULTS FOR") }
    }

    static content = {
        firstResult {
            waitFor(20) { $('#search-result-list > div > p > a') }
        }
    }
}
