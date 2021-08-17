package testCases;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import frameWork.Drivers;
import frameWork.SetupProperties;
import operations.ReadWriteExcelFile;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;

public class RunnerOnewayFlight {
	public static WebDriver driver;
	static Properties prop;
	static String sheetName = "Oneway";
	static ReadWriteExcelFile ex = new ReadWriteExcelFile("src//test//resources//DataKeys.xlsx");
	static Workbook wb = ex.getWorkbook();
	static ExtentReports exr = new ExtentReports();
	static ExtentTest tc;
	static int rowNum =2;
	static int testNum =1;
	static String error = null;
	static boolean flag=false;
	
	
	
	@Test(dataProvider = "dp")
	public void f(String leavingFrom, String goingTo,String type) throws Exception {
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		exr.attachReporter(new ExtentHtmlReporter("OneWayFlight.html"));
		tc = exr.createTest("OneWayFlightBooking : " + testNum);
		tc.info("Opening URL");
		driver.get(prop.getProperty("url"));
		try {
		
		driver.manage().window().maximize();
		
		tc.info("Entering into One_way Flights Booking");
		driver.findElement(By.linkText("Flights")).click();
		driver.findElement(By.linkText("One-way")).click();
		
		Thread.sleep(2000);
		
		char[] chars = leavingFrom.toCharArray();
		  for (char c: chars) {
			  if(Character.isDigit(c)) {
				  flag=true;
				  break;
			  }
		  }
		
		if(type.equalsIgnoreCase("positive")) {
			Actions ac = new Actions(driver);
			tc.info("Entering valid details and clicking search");
			try {
			driver.findElement(By.className(prop.getProperty("leaving"))).sendKeys(leavingFrom);
			Action a = ac.sendKeys(Keys.ENTER).build();
			a.perform();
			driver.findElement(By.xpath(prop.getProperty("going"))).sendKeys(goingTo);
			Action b = ac.sendKeys(Keys.ENTER).build();
			b.perform();
			driver.findElement(By.xpath(prop.getProperty("flightsearch"))).click();
			
			tc.log(Status.PASS, "Flight searching started");
			
			} catch(Exception e) {
				tc.log(Status.FAIL, "Flight Searching failed");
			}
			
			try {
			driver.findElement(By.className(prop.getProperty("flightselect"))).click();
			driver.findElement(By.xpath(prop.getProperty("flightcontinue"))).click();
			tc.log(Status.PASS, "Flight selected ");
			} catch(Exception e) {
				tc.log(Status.FAIL, "Not able to select flight");
			}
			String parent = driver.getWindowHandle();
			Set<String> s = driver.getWindowHandles();
			Iterator<String> I1 = s.iterator();
			while (I1.hasNext()) {
				String child_window = I1.next();
				if (!parent.equals(child_window)) {
					driver.switchTo().window(child_window);
					tc.info("Clicking Checkout ");
					driver.findElement(By.xpath(prop.getProperty("checkout"))).click();
				}
			}
			tc.pass("Test Case Passed");
			ex.setCellValue(sheetName, rowNum, 4, "Passed");
			
		}else {
			
			if(goingTo.equalsIgnoreCase("Na")&& !leavingFrom.equalsIgnoreCase("Na")) {
				RunnerOnewayFlight.negativeTestCaseNullGoingTo(leavingFrom);
			}
			else if(leavingFrom.equalsIgnoreCase("abcdefghi")||flag) {
				RunnerOnewayFlight.negativeTestCaseInvalidLeavingFrom(leavingFrom, goingTo);
			}
				
		}
		}
		catch(Exception e) {
			tc.fail("Failed main");
			ex.setCellValue(sheetName, rowNum, 4, "Failed");
			System.out.println("failed");
		}
		exr.flush();
		rowNum++;
		testNum++;
	
	}
	
	public static void negativeTestCaseInvalidLeavingFrom(String leavingFrom,String goingTo) throws Exception {
		Actions ac = new Actions(driver);
		driver.findElement(By.className(prop.getProperty("leaving"))).sendKeys(leavingFrom);
		Action a = ac.sendKeys(Keys.ENTER).build();
		a.perform();
		driver.findElement(By.xpath(prop.getProperty("going"))).sendKeys(goingTo);
		Action b = ac.sendKeys(Keys.ENTER).build();
		b.perform();
		driver.findElement(By.xpath(prop.getProperty("flightsearch"))).click();
		
		try {
			
			error = driver.findElement(By.xpath(prop.getProperty("errorairportname"))).getText();
			ex.setCellValue(sheetName, rowNum, 5, error);
			ex.setCellValue(sheetName, rowNum, 4, "Passed");
			System.out.println(error);
			tc.pass("Passed , Error : "+error);
			
		}catch(Exception e){
			tc.fail("Failed");
			ex.setCellValue(sheetName, rowNum, 4, "Failed");
			System.out.println("failed");
		}
		
	}
	public static void negativeTestCaseNullGoingTo(String leavingFrom) throws Exception {
		Actions ac = new Actions(driver);
		driver.findElement(By.xpath(prop.getProperty("going"))).sendKeys("");
		driver.findElement(By.className(prop.getProperty("leaving"))).sendKeys(leavingFrom);
		Action a = ac.sendKeys(Keys.ENTER).build();
		a.perform();
		driver.findElement(By.xpath(prop.getProperty("flightsearch"))).click();
		try {

			error = driver.findElement(By.xpath(
					"/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/form[1]/div[2]/div[1]/div[2]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]"))
					.getText();
			ex.setCellValue(sheetName, rowNum, 5, error);
			ex.setCellValue(sheetName, rowNum, 4, "Passed");
			tc.pass("Passed , Error : "+error);
			
		}catch(Exception e ) {
			tc.fail("Failed");
			ex.setCellValue(sheetName, rowNum, 4, "Failed");
			System.out.println("failed");
		}
		
	}
	

	@DataProvider
	public Object[][] dp() {
		ReadWriteExcelFile ex = new ReadWriteExcelFile("src/test/resources/DataKeys.xlsx");

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


	@BeforeTest
	public void beforeTest() throws FileNotFoundException, IOException {
		driver = Drivers.getChromeDriver();
		prop = SetupProperties.webProp();
		
	}

	@AfterMethod
	public void afterMethod() {
//	  driver.switchTo().defaultContent();
		driver.quit();
	}
	@AfterClass
	public void afterClass() throws Exception {
		FileOutputStream outFile = new FileOutputStream(new File("src/test/resources/DataKeys.xlsx"));
		wb.write(outFile);
	}

}