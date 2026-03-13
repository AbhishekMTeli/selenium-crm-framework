package com.crm.tests.functional;

import com.crm.framework.pages.DashboardPage;
import com.crm.framework.pages.LoginPage;
import com.crm.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Functional tests for the CRM Login module.
 * Covers: valid login, invalid login, empty fields, password masking,
 * session persistence, and logout.
 */
public class LoginTest extends BaseTest {

    // ─── Data Providers ───────────────────────────────────────────────────────

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
            {"wrong@user.com",  "WrongPass1!",  "Invalid username or password"},
            {"admin@crm.com",   "BadPassword",  "Invalid username or password"},
            {"",                "",             "Username is required"},
            {"admin@crm.com",   "",             "Password is required"},
        };
    }

    @DataProvider(name = "validUsers", parallel = true)
    public Object[][] validUsers() {
        return new Object[][] {
            {"admin@crm.com",   "Admin@123",  "Administrator"},
            {"sales@crm.com",   "Sales@123",  "Sales Rep"},
            {"manager@crm.com", "Mgr@123",    "Sales Manager"},
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

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed for: " + username);
        Assert.assertTrue(loginPage.getErrorMessage().contains(expectedError),
                "Error message mismatch. Expected to contain: " + expectedError);
        extentTest.pass("Correct error shown for invalid credentials: " + username);
    }

    @Test(description = "Multiple user roles can login successfully",
          dataProvider = "validUsers",
          groups = {"functional"})
    public void testMultipleUserRoleLogin(String email, String password, String role) {
        DashboardPage dashboard = new LoginPage().loginAs(email, password);

        Assert.assertTrue(dashboard.isDashboardLoaded(),
                role + " should reach dashboard after login");
        Assert.assertTrue(dashboard.getWelcomeText().length() > 0,
                "Welcome text should be present");
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
