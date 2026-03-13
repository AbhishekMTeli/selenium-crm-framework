package com.crm.tests.functional;

import com.crm.framework.pages.ContactsPage;
import com.crm.framework.pages.LoginPage;
import com.crm.framework.utils.ExcelUtils;
import com.crm.tests.base.BaseTest;
import com.github.javafaker.Faker;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Functional tests for the CRM Contacts module.
 * Demonstrates data-driven testing with both Excel and DataProvider.
 */
public class ContactsTest extends BaseTest {

    private final Faker faker = new Faker();

    // ─── Data Providers ───────────────────────────────────────────────────────

    @DataProvider(name = "contactData")
    public Object[][] contactData() {
        return new Object[][] {
            {"John",    "Smith",    "john.smith@testco.com",    "5551001001", "TestCo Inc"},
            {"Jane",    "Doe",      "jane.doe@example.org",     "5551002002", "Example Org"},
            {"Alice",   "Johnson",  "alice.j@techcorp.io",      "5551003003", "TechCorp IO"},
        };
    }

    /** Loads contact test data from Excel sheet "Contacts" */
    @DataProvider(name = "contactDataFromExcel")
    public Object[][] contactDataFromExcel() {
        return ExcelUtils.readSheetData("src/test/resources/testdata/TestData.xlsx", "Contacts");
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @Test(description = "Create a new contact with valid data",
          dataProvider = "contactData",
          groups = {"smoke", "regression"})
    public void testCreateContact(String firstName, String lastName,
                                  String email, String phone, String company) {
        ContactsPage contactsPage = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass())
                .goToContacts();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(contactsPage.isContactsPageLoaded(),
                "Contacts page should load");

        contactsPage.createContact(firstName, lastName, email, phone, company);

        softAssert.assertTrue(contactsPage.isSuccessToastDisplayed(),
                "Success toast should appear after creating contact");
        softAssert.assertTrue(contactsPage.isContactPresent(firstName + " " + lastName),
                "New contact should appear in the list");
        softAssert.assertAll();

        extentTest.pass("Contact created: " + firstName + " " + lastName);
    }

    @Test(description = "Create contacts from Excel data source",
          dataProvider = "contactDataFromExcel",
          groups = {"data-driven"})
    public void testCreateContactFromExcel(String firstName, String lastName,
                                            String email, String phone, String company) {
        ContactsPage contactsPage = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass())
                .goToContacts();

        contactsPage.createContact(firstName, lastName, email, phone, company);

        Assert.assertTrue(contactsPage.isSuccessToastDisplayed(),
                "Contact creation should succeed for Excel data: " + email);
        extentTest.pass("Excel-driven contact created: " + email);
    }

    @Test(description = "Search returns correct contact",
          groups = {"functional", "regression"})
    public void testSearchContact() {
        String firstName = faker.name().firstName();
        String lastName  = faker.name().lastName();
        String email     = firstName.toLowerCase() + "@search-test.com";

        ContactsPage contactsPage = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass())
                .goToContacts();

        contactsPage.createContact(firstName, lastName, email, "5559999999", "SearchCo");
        contactsPage.searchContact(firstName);

        Assert.assertTrue(contactsPage.isContactPresent(firstName + " " + lastName),
                "Searched contact should be visible");
        extentTest.pass("Search verified for: " + firstName);
    }

    @Test(description = "Delete a contact removes it from the list",
          groups = {"functional", "regression"})
    public void testDeleteContact() {
        String firstName = "Delete" + faker.number().digits(4);
        String lastName  = "Test";

        ContactsPage contactsPage = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass())
                .goToContacts();

        contactsPage.createContact(firstName, lastName,
                firstName.toLowerCase() + "@del.com", "5550000000", "DelCo");
        contactsPage.deleteContact(firstName + " " + lastName);

        Assert.assertFalse(contactsPage.isContactPresent(firstName + " " + lastName),
                "Deleted contact should no longer appear");
        extentTest.pass("Contact deletion verified: " + firstName);
    }

    @Test(description = "Contacts page shows correct number of records",
          groups = {"functional"})
    public void testContactListNotEmpty() {
        ContactsPage contactsPage = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass())
                .goToContacts();

        Assert.assertTrue(contactsPage.getContactRowCount() > 0,
                "Contacts list should not be empty");
        extentTest.pass("Contacts list is populated");
    }
}
