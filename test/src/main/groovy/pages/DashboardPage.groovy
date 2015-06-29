package pages

import geb.Page

class DashboardPage extends Page {
    static url = System.properties['application.context.root']

    static at = {
        $('.lead') != null
    }

    static content = {
        searchButton {
            $('#dashboard-search > span > button')
        }
        searchTextBox {
            $('#ember424')
        }
    }
}
