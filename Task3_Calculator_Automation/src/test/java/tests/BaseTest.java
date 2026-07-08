package tests;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import pages.CalculatorPage;

public class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723";
    private static final String APPIUM_STATUS_URL = APPIUM_SERVER_URL + "/status";

    private static final int SERVER_READY_TIMEOUT_SECONDS = 30;
    private static final int SESSION_CREATE_MAX_ATTEMPTS = 3;
    private static final long SESSION_CREATE_RETRY_DELAY_MS = 3000;

    protected AndroidDriver driver;
    protected CalculatorPage calculatorPage;

    @BeforeClass
    public void waitForAppiumServer() {

        long deadline = System.currentTimeMillis()
                + Duration.ofSeconds(SERVER_READY_TIMEOUT_SECONDS).toMillis();

        while (System.currentTimeMillis() < deadline) {

            if (isAppiumServerUp()) {
                log.info("Appium server is up at {}", APPIUM_SERVER_URL);
                return;
            }

            sleep(1000);
        }

        throw new RuntimeException(
                "Appium server did not become ready at "
                        + APPIUM_SERVER_URL
                        + " within "
                        + SERVER_READY_TIMEOUT_SECONDS
                        + " seconds.");
    }

    private boolean isAppiumServerUp() {

        try {

            HttpURLConnection conn =
                    (HttpURLConnection) URI.create(APPIUM_STATUS_URL)
                            .toURL()
                            .openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);

            int code = conn.getResponseCode();

            conn.disconnect();

            return code == 200;

        } catch (IOException e) {
            return false;
        }
    }

    @BeforeMethod
    public void setUp() {

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setAppPackage("com.google.android.calculator")
                .setAppActivity("com.android.calculator2.Calculator")
                .setAutomationName("UiAutomator2")
                .setNoReset(true)
                .setNewCommandTimeout(Duration.ofSeconds(60));

        URL serverUrl;

        try {
            serverUrl = new URL(APPIUM_SERVER_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium Server URL", e);
        }

        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= SESSION_CREATE_MAX_ATTEMPTS; attempt++) {

            try {

                driver = new AndroidDriver(serverUrl, options);

                // Bring Calculator to foreground
                driver.activateApp("com.google.android.calculator");

                System.out.println("Current Package : " + driver.getCurrentPackage());
                System.out.println("Current Activity: " + driver.currentActivity());

                calculatorPage = new CalculatorPage(driver);

                log.info("Appium session started successfully (Attempt {})", attempt);

                return;

            } catch (RuntimeException e) {

                lastFailure = e;

                log.warn("Driver creation failed on attempt {}/{} : {}",
                        attempt,
                        SESSION_CREATE_MAX_ATTEMPTS,
                        e.getMessage());

                sleep(SESSION_CREATE_RETRY_DELAY_MS);
            }
        }

        throw new RuntimeException(
                "Failed to create Appium session after "
                        + SESSION_CREATE_MAX_ATTEMPTS
                        + " attempts.",
                lastFailure);
    }

    @AfterMethod
    public void tearDown() {

        if (driver != null) {

            try {

                driver.quit();

                log.info("Appium session closed");

            } catch (Exception e) {

                log.error("Error while closing Appium session", e);
            }
        }
    }

    private void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}