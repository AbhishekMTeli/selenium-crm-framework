# Test Data - Excel File Structure

Create `TestData.xlsx` in this directory with the following sheets:

## Sheet: Contacts
| FirstName | LastName | Email | Phone | Company |
|-----------|----------|-------|-------|---------|
| John | Smith | john@test.com | 5551001001 | TestCo |
| Jane | Doe | jane@test.com | 5551002002 | ExCorp |

## Sheet: Leads
| Name | Email | Source | Status |
|------|-------|--------|--------|
| Alice Walker | alice@co.com | Web | New |
| Bob Martin | bob@co.com | Referral | Contacted |

## Sheet: LoginData
| Username | Password | ExpectedResult |
|----------|----------|----------------|
| admin@crm.com | Admin@123 | success |
| wrong@user.com | bad | failure |
