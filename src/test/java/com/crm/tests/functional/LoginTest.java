package com.crm.tests.functional;

import com.crm.framework.pages.DashboardPage;
import com.crm.framework.pages.LoginPage;
import com.crm.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Functional tests for the CRM Login module.
 * Covers: valid login, invalid login, empty fields, password masking,
 * session persistence, and logout.
 */
public class LoginTest extends BaseTest {

    // ─── Data Providers ───────────────────────────────────────────────────────

    /**
     * Invalid credential scenarios for negative testing.
     * Columns: username, password, expected error message fragment.
     * These are intentionally wrong values — keeping them inline is appropriate.
     */
    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
            {"wrong@user.com",  "WrongPass1!",  "Invalid username or password"},
            {"admin@crm.com",   "BadPassword",  "Invalid username or password"},
            {"",                "",             "Username is required"},
            {"admin@crm.com",   "",             "Password is required"},
        };
    }

    /**
     * Multi-role login scenarios.
     * Credentials are loaded from config so environment-specific values
     * (QA vs Staging) can be supplied via config-{env}.properties or
     * -D system properties without touching test code.
     * Columns: username key, password key, display role name.
     */
    @DataProvider(name = "validUsers", parallel = true)
    public Object[][] validUsers() {
        return new Object[][] {
            {config.get("admin.username"),   config.get("admin.password"),   "Administrator"},
            {config.get("sales.username"),   config.get("sales.password"),   "Sales Rep"},
            {config.get("manager.username"), config.get("manager.password"), "Sales Manager"},
        };
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @Test(description = "Valid admin login redirects to dashboard",
          groups = {"smoke", "regression"})
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboard = loginPage.loginAs(
                config.getAdminUser(), config.getAdminPass());

        Assert.assertTrue(dashboard.isDashboardLoaded(),
                "Dashboard should be loaded after valid login");
        extentTest.pass("Valid login successful - Dashboard loaded");
    }

    @Test(description = "Invalid credentials show error message",
          dataProvider = "invalidCredentials",
          groups = {"functional", "regression"})
    public void testInvalidLogin(String username, String password, String expectedError) {
        LoginPage loginPage = new LoginPage();
        loginPage.submitInvalidLogin(username, password);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed for: " + username);
        softAssert.assertTrue(loginPage.getErrorMessage().contains(expectedError),
                "Error message mismatch. Expected to contain: " + expectedError);
        softAssert.assertAll();

        extentTest.pass("Correct error shown for invalid credentials: " + username);
    }

    @Test(description = "Multiple user roles can login successfully",
          dataProvider = "validUsers",
          groups = {"functional"})
    public void testMultipleUserRoleLogin(String email, String password, String role) {
        DashboardPage dashboard = new LoginPage().loginAs(email, password);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(dashboard.isDashboardLoaded(),
                role + " should reach dashboard after login");
        softAssert.assertFalse(dashboard.getWelcomeText().isEmpty(),
                "Welcome text should be present for " + role);
        softAssert.assertAll();

        extentTest.pass(role + " login verified");
    }

    @Test(description = "User can logout successfully",
          groups = {"smoke", "regression"})
    public void testLogout() {
        DashboardPage dashboard = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass());
        LoginPage loginPage = dashboard.logout();

        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Should redirect to login page after logout");
        extentTest.pass("Logout successful");
    }

    @Test(description = "Login page is accessible via direct URL",
          groups = {"smoke"})
    public void testLoginPageLoads() {
        LoginPage loginPage = new LoginPage();
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Login page should be visible on app start");
    }
}
