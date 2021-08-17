package testCases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.Screen;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.jcraft.jsch.JSch;
import com.sun.jna.platform.unix.X11.Window;

import frameWork.Drivers;
import frameWork.SetupProperties;
import operations.ReadWriteExcelFile;

public class RunnerHotelBooking extends ReadWriteExcelFile {

	public static WebDriver driver;
	public static Properties prop;
	public static JavascriptExecutor js;
	static int rowNum = 2;
	static int testNum = 1;
	static boolean flag = false;
	static ReadWriteExcelFile ex = new ReadWriteExcelFile("src//test//resources//DataKeys.xlsx");
	static Workbook wb = ex.getWorkbook();
	static ExtentReports exr = new ExtentReports();
	static ExtentTest tc;

	public RunnerHotelBooking(String pathWithFileName) {
		super(pathWithFileName);
	}

	@Test(dataProvider = "dp")
	public void f(String destination, String checkIn, String checkOut, String adultTraveler, String childTraveler,
			String ageOfChild, String type) throws Exception {
		exr.attachReporter(new ExtentHtmlReporter("HotelBookingReport.html"));
		tc = exr.createTest("HotelBooking : " + testNum);

		driver.get(prop.getProperty("url"));
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		char[] chars = destination.toCharArray();
		for (char c : chars) {
			if (Character.isDigit(c)) {
				flag = true;
			}
		}

		switch (type) {
		case "Positive":
			tc.info("Selecting the options for hotel booking");
			RunnerHotelBooking.positiveTestCase(destination, checkIn, checkOut, adultTraveler, childTraveler,
					ageOfChild);
			break;
		case "Negative":
//			RunnerHotelBooking.getSignInPage();
			if (destination.equals("Na")) {
				tc.info("Selection without inputing destination");
				RunnerHotelBooking.negativeTestCaseNullDest(checkIn, checkOut, adultTraveler, childTraveler,
						ageOfChild);
			} else if (ageOfChild.equals("Na")) {
				tc.info("Selection without inputing age of child");
				RunnerHotelBooking.negativeTestCaseNullAgeChild(destination, checkIn, checkOut, adultTraveler,
						childTraveler);
			} else if (flag) {
				tc.info("Inputing invalid destination");
				RunnerHotelBooking.negativeTestCaseGoingto(destination);
			} else {
				System.out.println("Test Case not available ");
			}
		}

		RunnerHotelBooking.writeExcel();
		System.out.println(rowNum);
		rowNum++;
		testNum++;
		exr.flush();
	}

	private static void positiveTestCase(String destination, String checkIn, String checkOut, String adultTraveler,
			String childTraveler, String ageOfChild) {
		WebDriverWait wait = new WebDriverWait(driver, 25);

		try {
			// Select the Going to field
			driver.findElement(By.cssSelector(prop.getProperty("placevisit"))).click();
			// Sending the place to visit initial values
			driver.findElement(By.id(prop.getProperty("destination"))).sendKeys(destination);

			// Click the first option from the list of suggested place
			try {
				driver.findElement(By.cssSelector(prop.getProperty("selectDestination"))).click();
				tc.log(Status.PASS, "Destination Place Selected");
			} catch (Exception e) {
				tc.log(Status.FAIL, "Error in selecting the destination");
			}

			// Click on the check-in date option
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id(prop.getProperty("checkIn"))))).click();
			try {
				// Selecting the dates for check-In and check-out
				String formatDateIn = checkIn.substring(1, 2) + " " + checkIn.substring(3, 6) + " "
						+ checkIn.substring(7);
				String startDate = "button[aria-label='" + formatDateIn + "']";
				System.out.println(startDate);
				driver.findElement(By.cssSelector(startDate)).click();

				String formatDateOut = checkOut.substring(0, 2) + " " + checkOut.substring(3, 6) + " "
						+ checkOut.substring(7);
				String endDate = "button[aria-label='" + formatDateOut + "']";
				System.out.println(endDate);
				driver.findElement(By.cssSelector(endDate)).click();

				// Clicking done after selecting the dates
				driver.findElement(By.cssSelector(prop.getProperty("dateDone"))).click();
				tc.log(Status.PASS, "Dates selection done");
			} catch (Exception e) {
				tc.log(Status.FAIL, "Error in dates selection");
			}
			// Selecting travelers
			wait.until(
					ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(prop.getProperty("travellers")))))
					.click();

			// Adding adults in the travelers
			try {
				char adtp = adultTraveler.charAt(0);
				int qep = Integer.parseInt(String.valueOf(adtp));
				System.out.println(qep);

				for (int i = 0; i < qep; i++) {
					driver.findElement(By.cssSelector(prop.getProperty("adultTravelers"))).click();
				}

				// Adding children in travelers
				driver.findElement(By.cssSelector(prop.getProperty("childTravelers"))).click();

				// Adding age for the children
				WebElement E = driver.findElement(By.id(prop.getProperty("childAge")));
				E.click();
				Select child = new Select(E);
				char aoc = adultTraveler.charAt(0);
				int wep = Integer.parseInt(String.valueOf(aoc)) + 1;
				System.out.println(wep);
				child.selectByIndex(wep);

				// Clicking done after selecting the travelers
				driver.findElement(By.xpath(prop.getProperty("travelersDone"))).click();
				tc.log(Status.PASS, "Travelers Selection Done");
				System.out.println("Hii line 171");

			} catch (Exception e) {
				tc.log(Status.FAIL, "Error in travelers Selection");

			}
			// Searching the hotels for the provided information
			driver.findElement(By.xpath(prop.getProperty("searching"))).click();

			try {
				// Creating a list of all the visible hotels
				List<WebElement> hotels = driver.findElements(By.tagName("li"));
				// Selecting the 4th hotel from the provided list
				WebElement qq = hotels.get(1);
				qq.click();

				wait.until(ExpectedConditions.numberOfWindowsToBe(2));
				// Getting all the windows Id
				Set<String> allWin = driver.getWindowHandles();

				List<String> allwin2 = new ArrayList<String>(allWin);
				driver.switchTo().window(allwin2.get(1));
				js.executeScript("window.scrollBy(0,1800)");

				tc.log(Status.PASS, "Hotel Booking Page Opened");

			} catch (Exception e) {
				tc.log(Status.FAIL, "Error in opening hotel booking page");
				e.printStackTrace();
			}

			try {
				Screen sc = new Screen();
				sc.wait("C:\\\\Users\\\\parth\\\\Downloads\\Reserve.jpg", 20);
				sc.click("C:\\\\Users\\\\parth\\\\Downloads\\Reserve.jpg");

				sc.wait("C:\\Users\\parth\\Downloads\\PayNow.jpg", 10);
				sc.click("C:\\Users\\parth\\Downloads\\PayNow.jpg");
				tc.log(Status.PASS, "Hotel booking is done");
				Thread.sleep(5000);

			} catch (Exception e) {
				tc.log(Status.FAIL, "Hotel is not available for booking");
			}

			ex.setCellValue("HotelBooking", rowNum, 8, "Passed");
			tc.log(Status.PASS, "Booking available");
			tc.pass("Test Case Passed");
		} catch (Exception e) {
			ex.setCellValue("HotelBooking", rowNum, 8, "Failed");
			tc.log(Status.FAIL, "Test Case Failed");
			e.printStackTrace();
		}

	}

	private static void negativeTestCaseNullDest(String checkIn, String checkOut, String adultTraveler,
			String childTraveler, String ageOfChild) {

		WebDriverWait wait = new WebDriverWait(driver, 25);
		// Click on the check-in date option
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id(prop.getProperty("checkIn"))))).click();
		try {
			// Selecting the dates for check-In and check-out
			String formatDateIn = checkIn.charAt(1) + " " + checkIn.substring(3, 6) + " " + checkIn.substring(7);
			String startDate = "button[aria-label='" + formatDateIn + "']";
			System.out.println(startDate);
			driver.findElement(By.cssSelector(startDate)).click();

			String formatDateOut = checkOut.charAt(1) + " " + checkOut.substring(3, 6) + " " + checkOut.substring(7);
			String endDate = "button[aria-label='" + formatDateOut + "']";
			System.out.println(endDate);
			driver.findElement(By.cssSelector(endDate)).click();

			// Clicking done after selecting the dates
			driver.findElement(By.cssSelector(prop.getProperty("dateDone"))).click();
			tc.log(Status.PASS, "Dates selection done");

		} catch (Exception e) {
			tc.log(Status.FAIL, "Error in dates selection");
		}

		try {
			// Selecting travelers
			driver.findElement(By.cssSelector(prop.getProperty("travellers"))).click();

			// Adding adults in the travelers
			char adt = adultTraveler.charAt(0);
			int qe = Integer.parseInt(String.valueOf(adt));
			System.out.println(qe);

			for (int i = 0; i < qe; i++) {
				driver.findElement(By.cssSelector(prop.getProperty("adultTravelers"))).click();
			}

			// Adding children in travelers
			driver.findElement(By.cssSelector(prop.getProperty("childTravelers"))).click();

			// Adding age for the children
			WebElement E = driver.findElement(By.id(prop.getProperty("childAge")));
			E.click();
			Select child = new Select(E);
			child.selectByValue("9");

			// Clicking done after selecting the travellers
			driver.findElement(By.xpath(prop.getProperty("travelersDone"))).click();
			tc.log(Status.PASS, "Travelers Selection Done");
		} catch (Exception e) {
			tc.log(Status.FAIL, "Error in travelers Selection");
		}
		// Searching the hotels for the provided information
		driver.findElement(By.xpath(prop.getProperty("searching"))).click();

		try {

			WebElement element = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.id("location-field-destination-input-error"))));
			String error = element.getText();
			ex.setCellValue("HotelBooking", rowNum, 8, "Passed");
			System.out.println(error);
			ex.setCellValue("HotelBooking", rowNum, 9, error);
			tc.pass("Test Case Passed " + " Error" + error);

		} catch (Exception e) {
			System.out.println("failed");
			ex.setCellValue("HotelBooking", rowNum, 8, "Failed");
			tc.fail("Test Case Failed");
		}

	}

	private static void negativeTestCaseNullAgeChild(String destination, String checkIn, String checkOut,
			String adultTraveler, String childTraveler) throws InterruptedException {

		WebDriverWait wait = new WebDriverWait(driver, 25);
		try {
			// Select the Going to field
			driver.findElement(By.cssSelector(prop.getProperty("placevisit"))).click();
			// Sending the place to visit initial values
			driver.findElement(By.id(prop.getProperty("destination"))).sendKeys(destination);

			// Click the first option from the list of suggested place
			driver.findElement(By.cssSelector(prop.getProperty("selectDestination"))).click();

			tc.log(Status.PASS, "Destination Place Selected");
		} catch (Exception e) {
			tc.log(Status.FAIL, "Error in selecting the destination");

		}
		// Click on the check-in date option
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id(prop.getProperty("checkIn"))))).click();
		try {
			// Selecting the dates for check-In and check-out
			String formatDateIn = checkIn.charAt(1) + " " + checkIn.substring(3, 6) + " " + checkIn.substring(7);
			String startDate = "button[aria-label='" + formatDateIn + "']";
			System.out.println(startDate);
			driver.findElement(By.cssSelector(startDate)).click();

			String formatDateOut = checkOut.charAt(1) + " " + checkOut.substring(3, 6) + " " + checkOut.substring(7);
			String endDate = "button[aria-label='" + formatDateOut + "']";
			System.out.println(endDate);
			driver.findElement(By.cssSelector(endDate)).click();

			// Clicking done after selecting the dates
			driver.findElement(By.cssSelector(prop.getProperty("dateDone"))).click();

			tc.log(Status.PASS, "Dates selection done");
		} catch (Exception e) {
			tc.log(Status.FAIL, "Error in dates selection");

		}

		try {
			// Selecting travelers
			driver.findElement(By.cssSelector(prop.getProperty("travellers"))).click();

			// Adding adults in the travelers
			char adtn = adultTraveler.charAt(0);
			int qen = Integer.parseInt(String.valueOf(adtn));
			System.out.println(qen);
			for (int i = 0; i < qen; i++) {
				driver.findElement(By.cssSelector(prop.getProperty("adultTravelers"))).click();

			}
			Thread.sleep(5000);
			tc.log(Status.PASS, "Adult passenger selection done");
		} catch (Exception e) {
			tc.log(Status.FAIL, "Error in adult passenger selection");

		}
		// Adding children in travelers
		driver.findElement(By.cssSelector(prop.getProperty("childTravelers"))).click();
		try {

			WebElement element = wait.until(
					ExpectedConditions.visibilityOf(driver.findElement(By.className("uitk-error-summary-heading"))));
			String error = element.getText();
			ex.setCellValue("HotelBooking", rowNum, 8, "Passed");
			System.out.println(error);
			ex.setCellValue("HotelBooking", rowNum, 9, error);
			tc.pass("Test Case Passed " + " Error: " + error);

		} catch (Exception e) {
			System.out.println("failed");
			tc.fail("Test Case Failed");
			ex.setCellValue("HotelBooking", rowNum, 8, "Failed");
		}

	}

	public static void negativeTestCaseGoingto(String destination) {
		WebDriverWait wait = new WebDriverWait(driver, 25);

		driver.findElement(By.cssSelector(prop.getProperty("placevisit"))).click();
		// Sending the place to visit initial values
		driver.findElement(By.id(prop.getProperty("destination"))).sendKeys(destination);
		driver.findElement(By.xpath(prop.getProperty("searching"))).click();

		try {

			WebElement element = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.id("location-field-destination-input-error"))));
			String error = element.getText();
			ex.setCellValue("HotelBooking", rowNum, 8, "Passed");
			System.out.println(error);
			ex.setCellValue("HotelBooking", rowNum, 9, error);
			tc.pass("Test Case Passed " + " Error: " + error);

		} catch (Exception e) {
			System.out.println("failed");
			ex.setCellValue("HotelBooking", rowNum, 8, "Failed");
			tc.fail("Test Case Failed");
		}

	}

	public static void writeExcel() throws IOException {
		FileOutputStream outFile = new FileOutputStream(new File("src/test/resources/DataKeys.xlsx"));
		wb.write(outFile);
	}

	@DataProvider
	public Object[][] dp() {
//		ReadSignupExcelFile ex = new ReadSignupExcelFile("src//test//resources//DataKeys.xlsx");

		int rowCount = ex.getLastRowNum("HotelBooking");
		System.out.println(rowCount);
		Object data[][] = new Object[rowCount][7];
		for (int i = 1; i < rowCount + 1; i++) {
			for (int j = 0; j < 7; j++) {
				data[i - 1][j] = ex.readData("HotelBooking", i, j);
			}
		}
		return data;
	}

	@BeforeMethod
	public void beforeTest() throws FileNotFoundException, IOException {

		driver = Drivers.getChromeDriver();
		prop = SetupProperties.webProp();
		js = (JavascriptExecutor) driver;

	}

	@AfterMethod
	public void afterMethod() {
		driver.quit();
	}

	@AfterClass
	public void afterClass() {
		try {
			FileOutputStream outFile = new FileOutputStream(new File(prop.getProperty("excelData")));
			wb.write(outFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
