package com.crm.framework.pages;

import com.crm.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the CRM Dashboard/Home page.
 */
public class DashboardPage extends BasePage {

    @FindBy(css = ".dashboard-header .welcome-text")
    private WebElement welcomeText;

    @FindBy(css = "nav a[href='/contacts']")
    private WebElement contactsNavLink;

    @FindBy(css = "nav a[href='/leads']")
    private WebElement leadsNavLink;

    @FindBy(css = "nav a[href='/accounts']")
    private WebElement accountsNavLink;

    @FindBy(css = "nav a[href='/opportunities']")
    private WebElement opportunitiesNavLink;

    @FindBy(css = ".user-avatar-dropdown")
    private WebElement userDropdown;

    @FindBy(css = ".logout-btn")
    private WebElement logoutButton;

    @FindBy(css = ".dashboard-kpi-total-contacts .kpi-value")
    private WebElement totalContactsKpi;

    @FindBy(css = ".dashboard-kpi-open-leads .kpi-value")
    private WebElement openLeadsKpi;

    // ─── Navigation ───────────────────────────────────────────────────────────

    public ContactsPage goToContacts() {
        click(contactsNavLink);
        return new ContactsPage();
    }

    public LeadsPage goToLeads() {
        click(leadsNavLink);
        return new LeadsPage();
    }

    public AccountsPage goToAccounts() {
        click(accountsNavLink);
        return new AccountsPage();
    }

    public LoginPage logout() {
        click(userDropdown);
        click(logoutButton);
        return new LoginPage();
    }

    // ─── Verifications ────────────────────────────────────────────────────────

    public boolean isDashboardLoaded() {
        waitForUrl("/dashboard");
        return isDisplayed(By.cssSelector(".dashboard-header"));
    }

    public String getWelcomeText() {
        return getText(welcomeText);
    }

    public String getTotalContactsCount() {
        return getText(totalContactsKpi);
    }

    public String getOpenLeadsCount() {
        return getText(openLeadsKpi);
    }
}
