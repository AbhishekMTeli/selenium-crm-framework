package com.crm.framework.pages;

import com.crm.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object for the CRM Leads module.
 */
public class LeadsPage extends BasePage {

    @FindBy(css = ".btn-add-lead")
    private WebElement addLeadButton;

    @FindBy(id = "lead-name")
    private WebElement leadNameField;

    @FindBy(id = "lead-email")
    private WebElement leadEmailField;

    @FindBy(id = "lead-source")
    private WebElement leadSourceDropdown;

    @FindBy(id = "lead-status")
    private WebElement leadStatusDropdown;

    @FindBy(id = "lead-owner")
    private WebElement leadOwnerField;

    @FindBy(css = ".btn-save-lead")
    private WebElement saveLeadButton;

    @FindBy(css = ".leads-search-input")
    private WebElement searchInput;

    @FindBy(css = ".lead-list-row")
    private List<WebElement> leadRows;

    @FindBy(css = ".convert-lead-btn")
    private WebElement convertLeadButton;

    // ─── Actions ──────────────────────────────────────────────────────────────

    public LeadsPage clickAddLead() {
        click(addLeadButton);
        return this;
    }

    public LeadsPage fillLeadForm(String name, String email,
                                  String source, String status) {
        type(leadNameField, name);
        type(leadEmailField, email);
        selectByVisibleText(By.id("lead-source"), source);
        selectByVisibleText(By.id("lead-status"), status);
        return this;
    }

    public LeadsPage saveLead() {
        click(saveLeadButton);
        return this;
    }

    public LeadsPage createLead(String name, String email,
                                String source, String status) {
        log.info("Creating lead: {}", name);
        return clickAddLead()
                .fillLeadForm(name, email, source, status)
                .saveLead();
    }

    public LeadsPage searchLead(String query) {
        type(searchInput, query);
        searchInput.submit();
        return this;
    }

    public ContactsPage convertLead() {
        click(convertLeadButton);
        click(By.cssSelector(".confirm-convert-btn"));
        return new ContactsPage();
    }

    // ─── Verifications ────────────────────────────────────────────────────────

    public boolean isLeadsPageLoaded() {
        waitForUrl("/leads");
        return isDisplayed(By.cssSelector(".btn-add-lead"));
    }

    public boolean isLeadPresent(String leadName) {
        return isDisplayed(By.xpath("//td[contains(text(),'" + leadName + "')]"));
    }

    public int getLeadCount() {
        return leadRows.size();
    }

    public boolean isSuccessToastDisplayed() {
        return isDisplayed(By.cssSelector(".lead-success-toast"));
    }
}
