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
        userApi.post(path: '/signin/facebook', requestContentType: URLENC, body: [
                
        ]
        )
        userApi.post(path: '/drug', body: [
            name: 'ASPIRIN',
            unni: '3G6A5W338E'
        ]) {
            response.success = { response ->
                println "Success"
                println response;
            }

            response.failure = { response ->
                println "Failure"
                println response;
            }
        }

    }
}
