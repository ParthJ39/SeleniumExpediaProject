package testCases;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import frameWork.Drivers;
import frameWork.SetupProperties;
import operations.ReadWriteExcelFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RunnerReturnFlight extends ReadWriteExcelFile {

	public RunnerReturnFlight(String pathWithFileName) {
		super(pathWithFileName);
	}

	public static WebDriver driver;
	public static Properties prop;
	static ReadWriteExcelFile ex = new ReadWriteExcelFile("src//test//resources//DataKeys.xlsx");
	static Workbook wb = ex.getWorkbook();
	static int rowNum = 2;
	static int testNum = 1;
	static ExtentReports exr = new ExtentReports();
	static ExtentTest tc;
	static String sheetName = "Roundtrip";
	static String error = null;

	@Test(dataProvider = "dp")
	public void f(String leavingfrom, String Goingto, String type)
			throws FileNotFoundException, IOException, Exception {

		exr.attachReporter(new ExtentHtmlReporter("ReturnFlight.html"));
		tc = exr.createTest("ReturnFilghtBooking : " + testNum);
		tc.info("Opening URL");

		driver.get(prop.getProperty("url"));

		driver.manage().window().maximize();
		tc.info("Entering into returnflight tab");
		try {
			driver.findElement(By.xpath(prop.getProperty("flight"))).click();
			driver.findElement(By.xpath(prop.getProperty("Return"))).click();

			if (type.equals("Positive")) {
				RunnerReturnFlight.postiveTestCase(leavingfrom, Goingto);

			} else if (leavingfrom.equalsIgnoreCase("Na")) {
				System.out.println("Na");
				RunnerReturnFlight.negativeTestCaseNullLeaving(leavingfrom, Goingto);

			} else if (Goingto.equalsIgnoreCase("Na")) {
				RunnerReturnFlight.negativeTestCaseNullGoing(leavingfrom, Goingto);

			} else if (leavingfrom.equalsIgnoreCase("abcdgef")) {
				RunnerReturnFlight.negativeTestCaseInvalidLeaving(leavingfrom, Goingto);
			}

			tc.pass("Test Case Passed");
			ex.setCellValue(sheetName, rowNum, 4, "Passed");

		} catch (Exception e) {
			ex.setCellValue(sheetName, rowNum, 4, "Failed");
			tc.fail("Failed");
			System.out.println("Failed");

		}

		RunnerReturnFlight.writeExcel();
		rowNum++;
		exr.flush();
		testNum++;

	}

	public static void postiveTestCase(String leavingfrom, String Goingto) throws InterruptedException {
		     
		    JavascriptExecutor js = (JavascriptExecutor)driver;
		
			WebDriverWait wait = new WebDriverWait(driver, 25);
			try {
			driver.findElement(By.xpath(prop.getProperty("Leavingfrom"))).click();
			driver.findElement(By.id(prop.getProperty("leavingfromvalue"))).sendKeys(leavingfrom);
			Actions ac = new Actions(driver);
			Action a = ac.sendKeys(Keys.ENTER).build();
			a.perform();

			driver.findElement(By.xpath(prop.getProperty("Goingto"))).click();
			driver.findElement(By.id(prop.getProperty("goingtovalue"))).sendKeys(Goingto);
			Action c = ac.sendKeys(Keys.ENTER).build();
			c.perform();

			driver.findElement(By.xpath(prop.getProperty("Search"))).click();

			js.executeScript("window.scrollBy(0,500)"); 
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[1]//div[1]//div[1]//div[1]//button[1]"))).click();
			Thread.sleep(2000);
			driver.findElement(By.xpath("//button[normalize-space()='Continue']")).click();
			
			js.executeScript("window.scrollBy(0,500)"); 
			Thread.sleep(5000);
			driver.findElement(By.xpath("//li[1]//div[1]//div[1]//div[1]//button[1]")).click();
			Thread.sleep(2000);
			driver.findElement(By.xpath("//button[normalize-space()='Continue']")).click();
            
			} catch(Exception e) {
				e.printStackTrace();
			}

			wait.until(ExpectedConditions.numberOfWindowsToBe(2));
			// Getting all the windows Id
			Set<String> allWin = driver.getWindowHandles();
			List<String> allwin2 = new ArrayList<String>(allWin);
			driver.switchTo().window(allwin2.get(1));
             try {
            	 wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[aria-label='Continue to checkout']"))).click();;
			tc.pass("Test Case Passed");
			ex.setCellValue(sheetName, rowNum, 4, "Passed");
		} catch (Exception e) {
			tc.fail("Test case fail");
			e.printStackTrace();
		}
	}

	public static void negativeTestCaseNullGoing(String leavingfrom, String Goingto) throws InterruptedException {
		tc.info("Entering leaving from and clicking enter");
		driver.findElement(By.xpath(prop.getProperty("Leavingfrom"))).click();
		driver.findElement(By.id(prop.getProperty("leavingfromvalue"))).sendKeys(leavingfrom);
		Actions ac = new Actions(driver);
		Action a = ac.sendKeys(Keys.ENTER).build();
		a.perform();
		driver.findElement(By.xpath(prop.getProperty("Search"))).click();
		try {

			error = driver.findElement(By.xpath("//div[@id='location-field-leg1-destination-input-error']")).getText();
			System.out.println(error);
			ex.setCellValue(sheetName, rowNum, 5, error);
			tc.pass("Passed , Error : " + error);

		} catch (Exception e) {
			ex.setCellValue(sheetName, rowNum, 4, "Failed");
			System.out.println("failed");
		}

	}

	public static void negativeTestCaseNullLeaving(String leavingfrom, String Goingto) throws InterruptedException {
		tc.info("Entering Going to  and clicking enter");
		driver.findElement(By.xpath(prop.getProperty("Goingto"))).click();
		driver.findElement(By.id(prop.getProperty("goingtovalue"))).sendKeys(Goingto);
		Actions ac = new Actions(driver);
		Action c = ac.sendKeys(Keys.ENTER).build();
		c.perform();

		driver.findElement(By.xpath(prop.getProperty("Search"))).click();
		System.out.println("search error");
		try {
			error = driver.findElement(By.xpath("//div[@id='location-field-leg1-origin-input-error']")).getText();
			System.out.println(error);
			ex.setCellValue(sheetName, rowNum, 5, error);
			tc.pass("Passed , Error : " + error);

		} catch (Exception e) {
			ex.setCellValue(sheetName, rowNum, 4, "Failed");
			System.out.println(" FAILED");
		}

	}

	public static void negativeTestCaseInvalidLeaving(String leavingfrom, String Goingto) throws InterruptedException {

		tc.info("Entering Invalid leaving from and clicking enter");
		driver.findElement(By.xpath(prop.getProperty("Leavingfrom"))).click();
		driver.findElement(By.id(prop.getProperty("leavingfromvalue"))).sendKeys(leavingfrom);
		Actions ac = new Actions(driver);
		Action a = ac.sendKeys(Keys.ENTER).build();
		a.perform();
		driver.findElement(By.xpath(prop.getProperty("Goingto"))).click();
		driver.findElement(By.id(prop.getProperty("goingtovalue"))).sendKeys(Goingto);
		Action c = ac.sendKeys(Keys.ENTER).build();
		c.perform();

		driver.findElement(By.xpath(prop.getProperty("Search"))).click();
		try {
			error = driver.findElement(By.xpath(
					"/html[1]/body[1]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/section[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]"))
					.getText();
			ex.setCellValue(sheetName, rowNum, 5, error);
			System.out.println(error);
			tc.pass("Passed , Error : " + error);
		} catch (Exception e) {
			ex.setCellValue(sheetName, rowNum, 4, "Failed");
			System.out.println(" FAILED");
		}

	}

	public static void writeExcel() throws Exception {
		FileOutputStream outFile = new FileOutputStream(new File("src/test/resources/DataKeys.xlsx"));
		wb.write(outFile);
	}

	@DataProvider
	public Object[][] dp() {
		int rowCount = ex.getLastRowNum(sheetName);
		System.out.println(rowCount);
		Object data[][] = new Object[rowCount][3];
		for (int i = 1; i < rowCount + 1; i++) {
			for (int j = 0; j < 3; j++) {
				data[i - 1][j] = ex.readData(sheetName, i, j);
			}
		}
		return data;
	}

	@BeforeMethod
	public void beforeMethod() throws FileNotFoundException, IOException {
		driver = Drivers.getChromeDriver();
		prop = SetupProperties.webProp();
	}

	@AfterMethod
	public void afterMethod() {

		driver.quit();
	}

	@AfterClass
	public void afterClass() throws Exception {
		FileOutputStream outFile = new FileOutputStream(new File("src//test//resources//DataKeys.xlsx"));
		wb.write(outFile);
	}

}