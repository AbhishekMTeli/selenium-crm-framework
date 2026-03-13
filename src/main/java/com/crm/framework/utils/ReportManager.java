package com.crm.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.crm.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe ExtentReports manager.
 * Each thread owns its own ExtentTest instance via ThreadLocal.
 * Report output directory is driven by {@code report.dir} in config.properties.
 */
public class ReportManager {

    private static final Logger log = LogManager.getLogger(ReportManager.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private ReportManager() {}

    public static synchronized void initReports() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportDir = ConfigManager.getInstance().getReportDir();
        String reportPath = reportDir + "/CRM_Report_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("CRM Automation Report");
        sparkReporter.config().setReportName("Selenium CRM Test Execution");
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("Framework",   "Selenium + TestNG + Maven");
        extentReports.setSystemInfo("Environment", System.getProperty("env", "QA"));
        extentReports.setSystemInfo("Browser",     System.getProperty("browser", "Chrome"));
        extentReports.setSystemInfo("OS",          System.getProperty("os.name"));
        extentReports.setSystemInfo("Java",        System.getProperty("java.version"));

        log.info("Extent report will be saved at: {}", reportPath);
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest test = extentReports.createTest(testName);
        extentTest.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static synchronized void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
