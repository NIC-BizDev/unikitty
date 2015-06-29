package pages

import geb.Page

/**
 * Created by mchurch on 6/28/15.
 */
class SearchResultsPage extends Page {
    static at = {
        $('#search-results > div > div > div > div.page-header > h2').text().startsWith("Search Results for")
    }

    static content = {
        firstResult {
            $('#search-results > div > div > div > div > div > h3 > a')
        }
    }
}
