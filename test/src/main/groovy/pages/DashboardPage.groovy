package pages

import geb.Page

class DashboardPage extends Page {
    static url = System.properties['application.context.root']

    static at = {
        waitFor { $('#dashboard-header').text().startsWith("Look up drug") }
    }

    static content = {
        largeSearchButton {
            $('#large-search-button')
        }
        largeSearchTextBox {
            $('#large-search-text-box')
        }
    }
}
