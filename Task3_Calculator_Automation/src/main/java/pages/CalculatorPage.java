package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Page Object for the Google Calculator app (com.google.android.calculator).
 * <p>
 * Resource IDs below match the standard Google Calculator app UI. If your
 * installed calculator app differs (a different build, OEM skin, or AOSP
 * calculator2), re-verify these IDs with Appium Inspector or
 * `uiautomatorviewer` and update the constants accordingly.
 */
public class CalculatorPage {

    private static final Logger log = LoggerFactory.getLogger(CalculatorPage.class);
    private static final String PKG = "com.google.android.calculator";

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public CalculatorPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ---------- Digit / operator buttons ----------

    public CalculatorPage pressDigit(int digit) {
        if (digit < 0 || digit > 9) {
            throw new IllegalArgumentException("Digit must be between 0 and 9, got: " + digit);
        }
        clickById(PKG + ":id/digit_" + digit);
        return this;
    }

    /**
     * Enters a full non-negative number by pressing each digit in sequence.
     * Supports a single decimal point (e.g. "12.5").
     */
    public CalculatorPage enterNumber(String number) {
        for (char c : number.toCharArray()) {
            if (c == '.') {
                clickById(PKG + ":id/dec_point");
            } else if (Character.isDigit(c)) {
                pressDigit(Character.getNumericValue(c));
            } else {
                throw new IllegalArgumentException("Unsupported character in number: " + c);
            }
        }
        return this;
    }

    public CalculatorPage pressAdd() {
        clickById(PKG + ":id/op_add");
        return this;
    }

    public CalculatorPage pressSubtract() {
        clickById(PKG + ":id/op_sub");
        return this;
    }

    public CalculatorPage pressMultiply() {
        clickById(PKG + ":id/op_mul");
        return this;
    }

    public CalculatorPage pressDivide() {
        clickById(PKG + ":id/op_div");
        return this;
    }

    public CalculatorPage pressEquals() {
        clickById(PKG + ":id/eq");
        return this;
    }

    public CalculatorPage clear() {
        try {
            clickById(PKG + ":id/clr");
        } catch (NoSuchElementException e) {
            log.info("No clear button visible (calculator likely already empty) - continuing");
        }
        return this;
    }

    // ---------- Result reading ----------

    /**
     * Reads the final calculated result. Google Calculator shows the live
     * result in {@code result_preview} while typing, then commits it to
     * {@code result_final} after '=' is pressed. We wait for result_final.
     */
    public String getResult() {
        try {
            WebElement resultEl = wait.until(
                    ExpectedConditions.presenceOfElementLocated(AppiumBy.id(PKG + ":id/result_final")));
            String text = resultEl.getText();
            log.info("Calculator result read: {}", text);
            return text;
        } catch (Exception e) {
            log.error("Failed to read calculator result", e);
            throw new RuntimeException("Could not read result from calculator screen", e);
        }
    }

    // ---------- High-level operation helper ----------

    public enum Operation { ADD, SUBTRACT, MULTIPLY, DIVIDE }

    /**
     * Performs a full binary operation (num1 [op] num2 =) and returns the
     * displayed result as a String.
     */
    public String performOperation(String num1, Operation op, String num2) {
        log.info("Performing operation: {} {} {}", num1, op, num2);
        clear();
        enterNumber(num1);
        switch (op) {
            case ADD -> pressAdd();
            case SUBTRACT -> pressSubtract();
            case MULTIPLY -> pressMultiply();
            case DIVIDE -> pressDivide();
            default -> throw new IllegalArgumentException("Unsupported operation: " + op);
        }
        enterNumber(num2);
        pressEquals();
        return getResult();
    }

    // ---------- Internal helper ----------

    private void clickById(String resourceId) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id(resourceId)));
            el.click();
        } catch (Exception e) {
            log.error("Could not find/click element with resource-id: {}", resourceId, e);
            throw new NoSuchElementException("Element not found or not clickable: " + resourceId, e);
        }
    }
}
