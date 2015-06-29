package com.nicusa.testing.tests.geb

import geb.Browser
import org.junit.Test
import pages.SignInPage

/**
 * Created by mchurch on 6/27/15.
 */
class SignInControllerTest {

    @Test
    public void testSignIn() {
        def browser = new Browser();
        browser.drive {
            to SignInPage
        }
    }
}
