package com.nicusa.testing.tests.http

import groovyx.net.http.HTTPBuilder
import org.junit.Test

/**
 * Created by mchurch on 6/28/15.
 */
class SaveADrugToAPortfolioTest {

    @Test
    public void saveADrugToAPortfolio() {
        def userApi = new HTTPBuilder( System.properties['application.context.root'] )
        def redirectLocation;
        userApi.post(path: '/signin/facebook' ) { resp ->
            assert resp.statusLine.statusCode == 302
            assert resp.headers.Location.startsWith('https://www.facebook.com/v1.0/dialog/oauth')
            redirectLocation = resp.headers.Location
        }

        userApi.get(uri: redirectLocation) { resp ->
            assert resp.statusLine.statusCode == 302
            assert resp.headers.Location.startsWith('https://www.facebook.com/login.php')
            redirectLocation = resp.headers.Location
        }

        userApi.get(uri: redirectLocation) { resp ->

        }

        userApi.post(path: '/api/drug', body: [
            name: 'ASPIRIN',
            unni: '3G6A5W338E'
        ]) { resp ->
            assert resp.statusLine.statusCode == 201
        }

    }
}
