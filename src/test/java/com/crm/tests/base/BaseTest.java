package com.crm.tests.base;

import com.crm.framework.config.ConfigManager;
import com.crm.framework.drivers.DriverManager;
import com.crm.framework.utils.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * BaseTest wired to TestNG lifecycle hooks.
 * Handles driver init/teardown and ExtentReports logging per test.
 */
public class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected ConfigManager config = ConfigManager.getInstance();
    protected ExtentTest extentTest;

    @BeforeSuite(alwaysRun = true)
    public void initReport() {
        ReportManager.initReports();
        log.info("ExtentReports initialized");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        // Validate required config before test starts — fail fast with a clear message
        // rather than a NullPointerException deep inside a page object.
        String baseUrl  = config.getBaseUrl();      // throws IllegalStateException if missing
        String browser  = System.getProperty("browser", config.getBrowser());
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", String.valueOf(config.isHeadless())));

        DriverManager.initDriver(browser, headless);
        log.info("Starting test: {} | Browser: {}", method.getName(), browser);

        extentTest = ReportManager.createTest(method.getName());
        extentTest.log(Status.INFO, "Browser: " + browser + " | URL: " + baseUrl);
        DriverManager.getDriver().get(baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test FAILED: {}", result.getName());
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            extentTest.fail(result.getThrowable())
                      .addScreenCaptureFromBase64String(
                          java.util.Base64.getEncoder().encodeToString(screenshot));
        } else if (result.getStatus() == ITestResult.SKIP) {
            extentTest.skip(result.getThrowable());
        } else {
            extentTest.pass("Test passed");
        }

        DriverManager.quitDriver();
        log.info("Driver closed for test: {}", result.getName());
    }

    @AfterSuite(alwaysRun = true)
    public void flushReport() {
        ReportManager.flushReports();
        log.info("ExtentReports flushed");
    }
}
