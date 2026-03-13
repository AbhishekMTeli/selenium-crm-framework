package com.crm.framework.pages;

import com.crm.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the CRM Accounts module.
 */
public class AccountsPage extends BasePage {

    @FindBy(css = ".btn-add-account")
    private WebElement addAccountButton;

    @FindBy(id = "account-name")
    private WebElement accountNameField;

    @FindBy(id = "account-industry")
    private WebElement industryDropdown;

    @FindBy(id = "account-website")
    private WebElement websiteField;

    @FindBy(id = "account-phone")
    private WebElement phoneField;

    @FindBy(css = ".btn-save-account")
    private WebElement saveAccountButton;

    @FindBy(css = ".accounts-search-input")
    private WebElement searchInput;

    // ─── Actions ──────────────────────────────────────────────────────────────

    public AccountsPage createAccount(String name, String industry,
                                      String website, String phone) {
        log.info("Creating account: {}", name);
        click(addAccountButton);
        type(accountNameField, name);
        selectByVisibleText(By.id("account-industry"), industry);
        type(websiteField, website);
        type(phoneField, phone);
        click(saveAccountButton);
        return this;
    }

    public AccountsPage searchAccount(String query) {
        type(searchInput, query);
        searchInput.submit();
        return this;
    }

    // ─── Verifications ────────────────────────────────────────────────────────

    public boolean isAccountPresent(String accountName) {
        return isDisplayed(By.xpath("//td[contains(text(),'" + accountName + "')]"));
    }

    public boolean isAccountsPageLoaded() {
        waitForUrl("/accounts");
        return isDisplayed(By.cssSelector(".btn-add-account"));
    }
}
