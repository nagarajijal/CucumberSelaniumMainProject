package com.vytrack.utilities;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class BrowserUtils {

    //It will be used to pause our test execution
    //just provide number of seconds as a parameter
    public static void wait(int seconds) {
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Waits for element to be not stale
     *
     * @param element
     */
    public static void waitForStaleElement(WebElement element) {
        int y = 0;
        while (y <= 15) {
            try {
                element.isDisplayed();
                break;
            } catch (StaleElementReferenceException st) {
                y++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    /**
     * Waits for the provided element to be visible on the page
     *
     * @param element
     * @param timeToWaitInSec
     * @return
     */
    public static WebElement waitForVisibility(WebElement element, int timeToWaitInSec) {
        WebDriverWait wait = new WebDriverWait(Driver.get(), timeToWaitInSec);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Clicks on an element using JavaScript
     *
     * @param element
     */
    public static void clickWithJS(WebElement element) {
        ((JavascriptExecutor) Driver.get()).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) Driver.get()).executeScript("arguments[0].click();", element);
    }

    /**
     * Waits for provided element to be clickable
     *
     * @param element
     * @param timeout
     * @return
     */
    public static WebElement waitForClickablility(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(Driver.get(), timeout);
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    //    PLEASE INSERT THIS METHOD INTO BROWSER UTILS
    /*
     * takes screenshot
     * whenever you call this method
     * it takes screenshot and returns location of the screenshot
     * @param name of test or whatever your like
     * take a name of a test and returns a path to screenshot takes
     */
    public static String getScreenshot(String name) {
        // name the screenshot with the current date time to avoid duplicate name
//        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));???
        SimpleDateFormat df = new SimpleDateFormat("-yyyy-MM-dd-HH-mm");
        String date = df.format(new Date());
        // TakesScreenshot ---> interface from selenium which takes screenshots
        TakesScreenshot ts = (TakesScreenshot) Driver.get();
        File source = ts.getScreenshotAs(OutputType.FILE);
        // full path to the screenshot location
        //where screenshot will be stored
        //System.getProperty("user.dir") returns path to the project as a string
        String target = System.getProperty("user.dir") + "/test-output/Screenshots/" + name + date + ".png";
        File finalDestination = new File(target);
        // save the screenshot to the path given
        try {
            FileUtils.copyFile(source, finalDestination);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return target;
    }

    /**
     * Wait 15 seconds with polling interval of 200 milliseconds then click
     *
     * @param webElement of element
     */
    public static void clickWithWait(WebElement webElement) {
        Wait wait = new FluentWait<>(Driver.get())
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(800))
                .ignoring(NoSuchElementException.class)
                .ignoring(ElementNotVisibleException.class)
                .ignoring(ElementClickInterceptedException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(WebDriverException.class);
        WebElement element = (WebElement) wait.until((Function<WebDriver, WebElement>) driver -> webElement);
        try {
            element.click();
        } catch (WebDriverException e) {
            System.out.println(e.getMessage());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * waits for backgrounds processes on the browser to complete
     *
     * @param timeOutInSeconds
     */
    public static void waitForPageToLoad(long timeOutInSeconds) {
        ExpectedCondition<Boolean> expectation = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
        try {
            WebDriverWait wait = new WebDriverWait(Driver.get(), timeOutInSeconds);
            wait.until(expectation);
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }

    /**
     * Wait for proper page title
     *
     * @param pageTitle
     */
    public static void waitForPageTitle(String pageTitle) {
        WebDriverWait wait = new WebDriverWait(Driver.get(), 10);
        wait.until(ExpectedConditions.titleIs(pageTitle));
    }


    /**
     * This method will convert/retrieve list of WebElements into list of string
     *
     * @param listOfWebElements
     * @return list of strings
     */
    public static List<String> getListOfString(List<WebElement> listOfWebElements) {
        List<String> listOfStrings = new ArrayList<>();
        for (WebElement element : listOfWebElements) {
            String value = element.getText().trim();
            //if there is no text
            //do not add this blank text into list
            if (value.length() > 0) {
                listOfStrings.add(value);
            }
        }
        return listOfStrings;
    }

    /**
     * If checkbox/radio button is selected and selectedOrUnselected is also equals to selected, then method will return true
     * If checkbox/radio button is not selected and selectedOrUnselected is also equals to unselected, then method will return true
     * Otherwise, method will return false
     *
     * @param checkboxOrRadioButton to check
     * @param selectedOrUnselected  status of checkbox either selected or unselected
     * @return
     */
    public static boolean checkIfStatusOfCheckBoxORRadioButtonIsCorrect(WebElement checkboxOrRadioButton, String selectedOrUnselected) {
        if (selectedOrUnselected.endsWith("s")) {
            selectedOrUnselected = selectedOrUnselected.substring(0, selectedOrUnselected.length() - 1);
        }
        if (checkboxOrRadioButton.isSelected() && selectedOrUnselected.equalsIgnoreCase("selected")) {
            return true;
        } else if (!checkboxOrRadioButton.isSelected() && selectedOrUnselected.equalsIgnoreCase("unselected")) {
            return true;
        }
        return false;
    }

    /**
     * This method will click on radio button/checkbox only in 2 cases:
     * - if button/checkbox is not selected yet, and selectOrUnselect equals to "select"
     * - if button/checkbox is already selected, and selectOrUnselect equals to "unselect"
     * otherwise, it will not do anything
     *
     * @param checkboxOrRadioButton to
     * @param selectOrUnselect
     */
    public static void selectOrUnSelectCheckboxOrRadioButton(WebElement checkboxOrRadioButton, String selectOrUnselect) {
        if (selectOrUnselect.endsWith("s")) {
            selectOrUnselect = selectOrUnselect.substring(0, selectOrUnselect.length() - 1);
        }
        if (!checkboxOrRadioButton.isSelected() && selectOrUnselect.equalsIgnoreCase("select")) {
            BrowserUtils.clickWithWait(checkboxOrRadioButton);
        } else if (checkboxOrRadioButton.isSelected() && selectOrUnselect.equalsIgnoreCase("unselect")) {
            BrowserUtils.clickWithWait(checkboxOrRadioButton);
        }
    }

    /**
     * Capitalize and return string
     * Before: appLLE
     * After: Apple
     *
     * @param str
     * @return
     */
    public static String capitalizeString(String str) {
        return str = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

}
