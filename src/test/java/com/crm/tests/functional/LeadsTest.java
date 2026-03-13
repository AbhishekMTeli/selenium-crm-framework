package com.crm.tests.functional;

import com.crm.framework.pages.ContactsPage;
import com.crm.framework.pages.LeadsPage;
import com.crm.framework.pages.LoginPage;
import com.crm.tests.base.BaseTest;
import com.github.javafaker.Faker;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Functional tests for the CRM Leads module.
 */
public class LeadsTest extends BaseTest {

    private final Faker faker = new Faker();

    @DataProvider(name = "leadData")
    public Object[][] leadData() {
        return new Object[][] {
            {"Alice Walker",  "alice@leads.com", "Web",     "New"},
            {"Bob Martin",    "bob@leads.com",   "Referral","Contacted"},
            {"Carol White",   "carol@leads.com", "Email",   "Qualified"},
        };
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @Test(description = "Create lead with valid data",
          dataProvider = "leadData",
          groups = {"smoke", "regression"})
    public void testCreateLead(String name, String email, String source, String status) {
        LeadsPage leadsPage = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass())
                .goToLeads();

        Assert.assertTrue(leadsPage.isLeadsPageLoaded(), "Leads page should load");

        leadsPage.createLead(name, email, source, status);

        Assert.assertTrue(leadsPage.isSuccessToastDisplayed(),
                "Success toast should appear after creating lead");
        Assert.assertTrue(leadsPage.isLeadPresent(name),
                "New lead should appear in the list: " + name);

        extentTest.pass("Lead created: " + name);
    }

    @Test(description = "Convert a lead to a contact",
          groups = {"functional", "regression"})
    public void testConvertLeadToContact() {
        String leadName = "ConvertMe " + faker.number().digits(4);
        String email    = "convert" + faker.number().digits(3) + "@leads.com";

        LeadsPage leadsPage = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass())
                .goToLeads();

        leadsPage.createLead(leadName, email, "Web", "Qualified");
        leadsPage.searchLead(leadName);

        ContactsPage contactsPage = leadsPage.convertLead();
        Assert.assertTrue(contactsPage.isContactsPageLoaded(),
                "Should navigate to Contacts after conversion");
        Assert.assertTrue(contactsPage.isContactPresent(leadName),
                "Converted lead should appear as a contact");

        extentTest.pass("Lead converted to contact: " + leadName);
    }

    @Test(description = "Search returns matching lead",
          groups = {"functional"})
    public void testSearchLead() {
        String leadName = "Search" + faker.number().digits(4);

        LeadsPage leadsPage = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass())
                .goToLeads();

        leadsPage.createLead(leadName, "s@search.com", "Email", "New");
        leadsPage.searchLead(leadName);

        Assert.assertTrue(leadsPage.isLeadPresent(leadName),
                "Searched lead should be visible");
        extentTest.pass("Lead search verified: " + leadName);
    }

    @Test(description = "Leads list displays records",
          groups = {"smoke"})
    public void testLeadsListNotEmpty() {
        LeadsPage leadsPage = new LoginPage()
                .loginAs(config.getAdminUser(), config.getAdminPass())
                .goToLeads();

        Assert.assertTrue(leadsPage.getLeadCount() > 0,
                "Leads list should contain records");
        extentTest.pass("Leads list is populated: " + leadsPage.getLeadCount() + " leads");
    }
}
