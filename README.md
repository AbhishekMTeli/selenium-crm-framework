# CRM Selenium Automation Framework

An enterprise-grade UI test automation framework for CRM applications, built with **Selenium 4**, **TestNG**, and **Maven**. Follows the Page Object Model pattern with full CI/CD integration via Jenkins.

---

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 11 | Language |
| Selenium WebDriver | 4.18.1 | Browser automation |
| TestNG | 7.9.0 | Test runner & parallel execution |
| WebDriverManager | 5.8.0 | Automatic driver binary management |
| ExtentReports | 5.1.1 | HTML test reports |
| Apache POI | 5.2.5 | Excel data-driven testing |
| Log4j2 | 2.22.1 | Structured logging |
| Java Faker | 1.0.2 | Dynamic test data generation |
| Maven | 3.x | Build & dependency management |

---

## Project Structure

```
selenium-crm-framework/
├── pom.xml                          # Maven build & dependency config
├── Jenkinsfile                      # CI/CD pipeline definition
├── testng-smoke.xml                 # Smoke test suite
├── testng-regression.xml            # Regression test suite (parallel)
├── testng-parallel.xml              # Full parallel execution suite
│
├── src/main/java/com/crm/framework/
│   ├── base/           BasePage.java          # Selenium wrapper methods & waits
│   ├── config/         ConfigManager.java     # Singleton config loader
│   ├── drivers/        DriverManager.java     # ThreadLocal WebDriver factory
│   ├── exceptions/     ExcelReaderException   # Custom exceptions
│   ├── listeners/      RetryListener.java     # Flaky test retry (max 2)
│   ├── pages/          Login/Dashboard/...    # Page Object classes
│   └── utils/          ExcelUtils.java        # Excel reader for data-driven tests
│                       ReportManager.java     # ExtentReports manager
│
└── src/test/
    ├── java/com/crm/tests/
    │   ├── base/           BaseTest.java      # TestNG lifecycle (setup/teardown)
    │   └── functional/     LoginTest.java     # Feature test classes
    │                       ContactsTest.java
    │                       LeadsTest.java
    └── resources/
        ├── config.properties                  # Default QA environment config
        ├── config-staging.properties          # Staging environment overrides
        ├── log4j2.xml                         # Logging configuration
        └── testdata/
            └── TestData.xlsx                  # Excel test data (see below)
```

---

## Prerequisites

- Java 11+
- Maven 3.6+
- Chrome / Firefox / Edge browser installed
- No manual WebDriver downloads needed — WebDriverManager handles it automatically

---

## Configuration

All settings live in `src/test/resources/config.properties`. Every property can be overridden at runtime via `-D` system properties.

```properties
base.url=https://crm-qa.example.com
browser=chrome
headless=false
explicit.wait=15
page.load.timeout=30
admin.username=admin@crm.com
admin.password=Admin@123
report.dir=target/extent-reports
screenshot.dir=target/screenshots
```

### Environment-specific configs

| File | Environment |
|------|-------------|
| `config.properties` | QA (default) |
| `config-staging.properties` | Staging |

Switch environment at runtime:
```bash
mvn test -Psmoke -Denv=staging
```

---

## Running Tests

### Smoke tests
```bash
mvn test -Psmoke
```

### Regression tests (parallel, 4 threads)
```bash
mvn test -Pregression
```

### Parallel execution
```bash
mvn test -Pparallel
```

### Override browser and environment
```bash
mvn test -Psmoke -Dbrowser=firefox -Denv=staging
```

### Headless mode (for CI)
```bash
mvn test -Pregression -Dheadless=true
```

### Run against a specific URL
```bash
mvn test -Psmoke -Dbase.url=https://crm-uat.example.com
```

---

## Test Reports

After a test run, the HTML report is saved to:
```
target/extent-reports/CRM_Report_<timestamp>.html
```

Open it in any browser. Reports include:
- Pass / Fail / Skip counts
- Screenshots attached on failure
- System info (OS, Java, Browser, Environment)

---

## Excel Test Data

Place `TestData.xlsx` in `src/test/resources/testdata/` with the following sheets:

### Sheet: Contacts
| FirstName | LastName | Email | Phone | Company |
|-----------|----------|-------|-------|---------|
| John | Smith | john@test.com | 5551001001 | TestCo |

### Sheet: Leads
| Name | Email | Source | Status |
|------|-------|--------|--------|
| Alice Walker | alice@co.com | Web | New |

### Sheet: LoginData
| Username | Password | ExpectedResult |
|----------|----------|----------------|
| admin@crm.com | Admin@123 | success |

---

## CI/CD — Jenkins

The `Jenkinsfile` defines a parameterized pipeline with the following inputs:

| Parameter | Options | Default |
|-----------|---------|---------|
| `BROWSER` | chrome, firefox, edge | chrome |
| `ENV` | qa, staging | qa |
| `SUITE` | smoke, regression, parallel | smoke |
| `HEADLESS` | true, false | true |

**Post-build actions:**
- TestNG results published
- ExtentReports HTML archived
- Failure screenshots archived
- Console logs archived

---

## Key Design Decisions

- **ThreadLocal WebDriver** — each test thread gets its own driver instance, enabling safe parallel execution
- **Explicit waits everywhere** — all interactions in `BasePage` wait for element visibility/clickability; no `Thread.sleep()`
- **Fail-fast config validation** — `ConfigManager.getRequired()` throws a clear `IllegalStateException` if a required property is missing, surfacing misconfigurations before any browser opens
- **CSS attribute selectors** — dynamic row lookups use `[data-contact-name='...']` instead of XPath string concatenation, preventing quote-injection issues
- **SoftAssert for multi-step validations** — tests with multiple assertions collect all failures before reporting, giving complete feedback in one run
- **RetryListener** — automatically retries flaky tests up to 2 times without any test-level annotation

---

## Author

**Abhishek Teli** — [github.com/AbhishekMTeli](https://github.com/AbhishekMTeli)
