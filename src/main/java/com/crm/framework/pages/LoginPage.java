package com.crm.framework.pages;

import com.crm.framework.base.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the CRM Login page.
 */
public class LoginPage extends BasePage {

    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "loginBtn")
    private WebElement loginButton;

    @FindBy(css = ".login-error-msg")
    private WebElement errorMessage;

    @FindBy(css = ".forgot-password-link")
    private WebElement forgotPasswordLink;

    // ─── Actions ──────────────────────────────────────────────────────────────

    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    public DashboardPage clickLogin() {
        click(loginButton);
        return new DashboardPage();
    }

    /** Full login flow in one call. Returns DashboardPage on success. */
    public DashboardPage loginAs(String username, String password) {
        log.info("Logging in as: {}", username);
        return enterUsername(username)
                .enterPassword(password)
                .clickLogin();
    }

    public LoginPage submitInvalidLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return this;
    }

    // ─── Verifications ────────────────────────────────────────────────────────

    public boolean isErrorDisplayed() {
        return isDisplayed(org.openqa.selenium.By.cssSelector(".login-error-msg"));
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(org.openqa.selenium.By.id("loginBtn"));
    }

    public void clickForgotPassword() {
        click(forgotPasswordLink);
    }
}
