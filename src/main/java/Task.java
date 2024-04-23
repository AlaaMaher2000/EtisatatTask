import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;


public class Task {
    public static void main(String[] args) throws InterruptedException, IOException {
        // Extracting the 3 values ( websiteURL , filterText, expectedText ) from excel sheet
        String[] sheetValues = extractSheetValues();
        String websiteURL = sheetValues[0];
        String filterText = sheetValues[1];
        String expectedText = sheetValues[2];
        Thread.sleep(1000);

        // Initialize web driver, open websiteURL in chrome browser and accept cookies
        WebDriver driver = initializeWebsite(websiteURL);

        // Filter Table with filterText extracted from sheet
        filterTable(driver, filterText);

        // Assert filterText extracted from sheet is equal to filter actual result for May Column
        assertFilterResult(driver, expectedText);
        driver.quit();
    }

    private static void assertFilterResult(WebDriver driver, String expectedText) {

        // String actualText = driver.findElement(By.cssSelector("div.ag-center-cols-container div[col-id='may']")).getText();
        String actualText = driver.findElements(By.cssSelector("div[col-id='may']")).getLast().getText();

        if (expectedText.equals(actualText)) {
            System.out.println("the actual value : " + actualText + " is equal to expected value : " + expectedText);
        } else {
            System.out.println("the actual value : " + actualText + " is NOT equal to expected value : " + expectedText);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    private static void filterTable(WebDriver driver, String filterText) throws InterruptedException {
        Thread.sleep(1000);
        WebElement element = driver.findElement(By.xpath("//input[@aria-label='Game Name Filter Input']"));
        element.sendKeys(filterText);
        element.click();

        Thread.sleep(1000);
        // Scroll to the right by a specific pixel value
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        WebElement all = driver.findElement(By.cssSelector("div[class='ag-center-cols-viewport']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollLeft += 1500;", all);


        Thread.sleep(1000);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    private static WebDriver initializeWebsite(String websiteURL) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(websiteURL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        Thread.sleep(1000);

        //accept cookies
        WebElement acceptCookiesButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
        acceptCookiesButton.click();
        return driver;
    }

    private static String[] extractSheetValues() throws IOException {
        // Load the Excel file
        FileInputStream fis = new FileInputStream("data/taskInputs.xlsx");
        Workbook workbook = new XSSFWorkbook(fis);

        // Get the first sheet from the workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Read the website URL from the Excel sheet
        Cell websiteURLCell = sheet.getRow(0).getCell(0); // URL is in the first column
        Cell filterTextCell = sheet.getRow(1).getCell(0); // filterText is in the first column
        Cell expectedTextCell = sheet.getRow(2).getCell(0); // expectedText is in the third column
        String websiteURL = websiteURLCell.getStringCellValue();
        String filterText = filterTextCell.getStringCellValue();
        String expectedText = expectedTextCell.getStringCellValue();

        // Close the workbook
        workbook.close();
        return new String[]{websiteURL, filterText, expectedText};
    }

}