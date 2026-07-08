# Calculator Automation (Appium + TestNG + POM)

Automated tests for the native Android Calculator app using Appium,
Java, TestNG, and the Page Object Model pattern.

## Prerequisites

- Java 11+ (JDK)
- Maven 3.6+
- Node.js + Appium 2.x (`npm install -g appium`)
- Appium's UiAutomator2 driver (`appium driver install uiautomator2`)
- Android SDK, with `ANDROID_HOME` (or `ANDROID_SDK_ROOT`) set and
  `platform-tools` added to your PATH
- An Android emulator (e.g. `emulator-5554`) or a physical device with
  USB debugging enabled
- Calculator app installed on the device/emulator — either from the
  Play Store, or sideloaded:
  ```
  adb -s emulator-5554 install path\to\calculator.apk
  ```

## Verify your environment before running tests

```powershell
adb devices                     # your device/emulator should show as "device"
adb -s emulator-5554 shell pm list packages | Select-String calculator
```

If the package name/activity for your installed calculator differs from
`com.google.android.calculator` / `com.android.calculator2.Calculator`,
update those two capabilities in `BaseTest.java`. You can confirm the
real values with Appium Inspector or `adb shell dumpsys window` while
the calculator app is in the foreground.

## Running the tests

1. Start Appium in its own terminal window and leave it running:
   ```
   appium
   ```
2. In a separate terminal, from the project root:
   ```
   mvn clean test
   ```

## Project structure

```
pom.xml                                     - Maven project + dependencies (Appium, Selenium, TestNG, SLF4J)
testng.xml                                  - TestNG suite definition (referenced by surefire in pom.xml)
src/main/java/pages/CalculatorPage.java     - Page Object for the Calculator UI
src/test/java/tests/BaseTest.java           - Driver lifecycle (setup/teardown, readiness checks)
src/test/java/tests/CalculatorArithmeticTest.java - Test cases for +, -, *, /
docs/3DS_Transaction_Flow.md                - Task 1 deliverable
docs/Refund_Test_Scenarios.md               - Task 2 deliverable
```

## Quick start (from a fresh unzip)

```powershell
cd calculator-automation
mvn clean test
```

That's it — the pom.xml already wires up TestNG, Appium's Java client, Selenium, and SLF4J, and points surefire at `testng.xml`. Just make sure Appium is running (`appium` in another terminal) and your emulator/device with the calculator app installed is available first (see Prerequisites above).

## Design notes

- **BaseTest** waits for Appium's `/status` endpoint before the first
  test runs, and retries session creation a few times, to avoid
  flakiness from the emulator/server not being fully ready yet.
- **CalculatorPage** wraps every element interaction with logging and
  throws a descriptive exception if an element can't be found/clicked,
  rather than letting a raw Appium exception surface.
- Each test method in **CalculatorArithmeticTest** logs a clear
  PASS/FAIL line and includes a descriptive assertion message with the
  expected vs. actual result.

## Extending this suite

- Add negative test cases (division by zero, decimal results, negative
  numbers) as additional `@Test` methods.
- Add a `testng.xml` if you want to control run order, grouping, or
  parallel execution explicitly.
