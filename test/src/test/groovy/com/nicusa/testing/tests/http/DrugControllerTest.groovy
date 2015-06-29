package com.nicusa.testing.tests.http

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import org.apache.http.client.params.ClientPNames
import org.junit.Test

class DrugControllerTest {

    def String apiDrugEndpoint = System.properties['api.drug.endpoint']

    @Test
    public void whenOpenFDAReturnsNotFoundSearchShouldContinue() {
        def userApi = new HTTPBuilder( 'http://localhost:8080' )
        userApi.client.params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false)
        def userResourceResponse = userApi.get(path: '/drug', query: [name: 'ADVIL ALLERGY AND CONGESTION RELIEF']) { response ->
            assert response.status == 200
        }
    }


}
