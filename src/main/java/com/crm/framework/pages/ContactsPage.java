package com.crm.framework.pages;

import com.crm.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object for the CRM Contacts module.
 *
 * Locator strategy for dynamic rows:
 *   Rows are expected to carry a {@code data-contact-name} attribute
 *   (e.g. <tr data-contact-name="John Smith">).
 *   Coordinate with the dev team to ensure this attribute is present on
 *   all contact rows — it is the most stable, test-friendly selector.
 *   As a safety net, {@link #xpathSafeValue(String)} guards against
 *   contact names containing single quotes (e.g. "O'Brien").
 */
public class ContactsPage extends BasePage {

    @FindBy(css = ".btn-add-contact")
    private WebElement addContactButton;

    @FindBy(id = "contact-first-name")
    private WebElement firstNameField;

    @FindBy(id = "contact-last-name")
    private WebElement lastNameField;

    @FindBy(id = "contact-email")
    private WebElement emailField;

    @FindBy(id = "contact-phone")
    private WebElement phoneField;

    @FindBy(id = "contact-company")
    private WebElement companyField;

    @FindBy(css = ".btn-save-contact")
    private WebElement saveContactButton;

    @FindBy(css = ".contact-search-input")
    private WebElement searchInput;

    @FindBy(css = ".contact-list-row")
    private List<WebElement> contactRows;

    @FindBy(css = ".contact-success-toast")
    private WebElement successToast;

    @FindBy(css = ".pagination-next")
    private WebElement nextPageButton;

    // ─── Actions ──────────────────────────────────────────────────────────────

    public ContactsPage clickAddContact() {
        click(addContactButton);
        return this;
    }

    public ContactsPage fillContactForm(String firstName, String lastName,
                                        String email, String phone, String company) {
        type(firstNameField, firstName);
        type(lastNameField, lastName);
        type(emailField, email);
        type(phoneField, phone);
        type(companyField, company);
        return this;
    }

    public ContactsPage saveContact() {
        click(saveContactButton);
        return this;
    }

    /** Full create-contact flow in one call. */
    public ContactsPage createContact(String firstName, String lastName,
                                      String email, String phone, String company) {
        log.info("Creating contact: {} {}", firstName, lastName);
        return clickAddContact()
                .fillContactForm(firstName, lastName, email, phone, company)
                .saveContact();
    }

    public ContactsPage searchContact(String query) {
        type(searchInput, query);
        searchInput.submit();
        return this;
    }

    /**
     * Deletes the contact row identified by {@code contactName}.
     *
     * Uses a CSS attribute selector on {@code data-contact-name} for precise,
     * injection-safe targeting instead of XPath text concatenation.
     */
    public ContactsPage deleteContact(String contactName) {
        log.info("Deleting contact: {}", contactName);
        // CSS attribute selector — stable and immune to quote injection
        By deleteBtn = By.cssSelector(
            "[data-contact-name='" + cssSafeValue(contactName) + "'] .btn-delete");
        click(deleteBtn);
        click(By.cssSelector(".confirm-delete-btn"));
        return this;
    }

    // ─── Verifications ────────────────────────────────────────────────────────

    public boolean isSuccessToastDisplayed() {
        return isDisplayed(By.cssSelector(".contact-success-toast"));
    }

    public String getSuccessMessage() {
        return getText(successToast);
    }

    public int getContactRowCount() {
        return contactRows.size();
    }

    /**
     * Checks whether a contact row with the given name is visible.
     *
     * Uses a CSS attribute selector on {@code data-contact-name} so that
     * names containing XPath-special characters (apostrophes, quotes) are
     * handled correctly without custom escaping logic.
     */
    public boolean isContactPresent(String contactName) {
        return isDisplayed(By.cssSelector(
            "[data-contact-name='" + cssSafeValue(contactName) + "']"));
    }

    public boolean isContactsPageLoaded() {
        waitForUrl("/contacts");
        return isDisplayed(By.cssSelector(".btn-add-contact"));
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────

    /**
     * Escapes single quotes in a value so it can be safely embedded inside a
     * CSS attribute selector wrapped in single quotes.
     * E.g. "O'Brien" → "O\\'Brien"
     */
    private static String cssSafeValue(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }
}
