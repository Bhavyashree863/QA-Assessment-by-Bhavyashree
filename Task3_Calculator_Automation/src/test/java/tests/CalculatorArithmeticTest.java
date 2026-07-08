package tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CalculatorPage;

/**
 * Arithmetic operation tests for the Calculator app.
 * Each test performs one operation end-to-end and asserts the displayed
 * result matches the expected value, with a descriptive failure message.
 */
public class CalculatorArithmeticTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(CalculatorArithmeticTest.class);

    @Test(description = "Addition: 7 + 3 = 10")
    public void testAddition() {
        String expected = "10";
        try {
            String actual = calculatorPage.performOperation("7", CalculatorPage.Operation.ADD, "3");
            Assert.assertEquals(actual, expected,
                    String.format("Addition failed: expected 7 + 3 = %s but got %s", expected, actual));
            log.info("PASS - Addition: 7 + 3 = {}", actual);
        } catch (Exception e) {
            log.error("FAIL - Addition test threw an exception", e);
            throw e;
        }
    }

    @Test(description = "Subtraction: 10 - 4 = 6")
    public void testSubtraction() {
        String expected = "6";
        try {
            String actual = calculatorPage.performOperation("10", CalculatorPage.Operation.SUBTRACT, "4");
            Assert.assertEquals(actual, expected,
                    String.format("Subtraction failed: expected 10 - 4 = %s but got %s", expected, actual));
            log.info("PASS - Subtraction: 10 - 4 = {}", actual);
        } catch (Exception e) {
            log.error("FAIL - Subtraction test threw an exception", e);
            throw e;
        }
    }

    @Test(description = "Multiplication: 5 * 6 = 30")
    public void testMultiplication() {
        String expected = "30";
        try {
            String actual = calculatorPage.performOperation("5", CalculatorPage.Operation.MULTIPLY, "6");
            Assert.assertEquals(actual, expected,
                    String.format("Multiplication failed: expected 5 * 6 = %s but got %s", expected, actual));
            log.info("PASS - Multiplication: 5 * 6 = {}", actual);
        } catch (Exception e) {
            log.error("FAIL - Multiplication test threw an exception", e);
            throw e;
        }
    }

    @Test(description = "Division: 20 / 4 = 5")
    public void testDivision() {
        String expected = "5";
        try {
            String actual = calculatorPage.performOperation("20", CalculatorPage.Operation.DIVIDE, "4");
            Assert.assertEquals(actual, expected,
                    String.format("Division failed: expected 20 / 4 = %s but got %s", expected, actual));
            log.info("PASS - Division: 20 / 4 = {}", actual);
        } catch (Exception e) {
            log.error("FAIL - Division test threw an exception", e);
            throw e;
        }
    }
}
