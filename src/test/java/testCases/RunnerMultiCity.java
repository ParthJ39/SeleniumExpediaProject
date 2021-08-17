package testCases;

import java.io.File;
import java.io.FileInputStream;
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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import frameWork.Drivers;
import frameWork.SetupProperties;
import operations.ReadWriteExcelFile;

public class RunnerMultiCity extends ReadWriteExcelFile {

	public RunnerMultiCity(String pathWithFileName) {
		super(pathWithFileName);
	}
	
	static boolean flag =false;
	static WebDriver driver;
	static WebDriverWait wt;
	static int rowNum=2;
	static int testNum=1;
	static Properties prop;
	static ReadWriteExcelFile ex = new ReadWriteExcelFile("src/test/resources/DataKeys.xlsx");
	static Workbook wb = ex.getWorkbook();
	static ExtentReports exr = new ExtentReports();
	static ExtentTest tc;
	
	
	@Test(dataProvider = "dp")
	public void f(String leaving, String going1, String going2, String date1, String date2, String type) throws Exception {
		
	  exr.attachReporter(new ExtentHtmlReporter("Multi-City.html"));
	  tc=exr.createTest("MultiCity Test : " +testNum); 
	  driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
	  // For opening the Website
	  driver.get(prop.getProperty("url"));
	  driver.manage().window().maximize();
	  tc.log(Status.PASS, "Url Opened");
		
	  // For selecting Flights and Multi-City
	  driver.findElement(By.linkText("Flights")).click();  
	  driver.findElement(By.xpath(prop.getProperty("multi"))).click();

	  char[] chars = leaving.toCharArray();
	  for (char c: chars) {
		  if(Character.isDigit(c)) {
			  flag=true;
			  break;
		  }
	  }
	  
	  switch (type) {
		case "Positive":	
			RunnerMultiCity.enterData(leaving, going1, going2, date1, date2);
			RunnerMultiCity.tillCheckout();
			break;
			
		case "Negative":
						
			if (leaving.contentEquals("Na") && going1.contentEquals("Na") && going2.contentEquals("Na")) {	
				RunnerMultiCity.nullValues();
				
			}		
			else if (leaving.equals(going1)){
				RunnerMultiCity.leavingequalgoing1(leaving, going1, going2, date1, date2);
			}		

			else if (flag) {
				RunnerMultiCity.invalidFormat(leaving, going1, going2, date1, date2);
			}	
			else {
				System.out.println("Test Case not available");
			}
			break;
	  }
	  
	  RunnerMultiCity.writeExcel();
	  System.out.println("Current Row: " +rowNum);
	  rowNum++;
	  testNum++;
	  exr.flush();

	}
	
	public static void enterData(String leaving, String going1, String going2, String date1, String date2) throws Exception{

		  // Entering Flight 1 Details
		  driver.findElement(By.cssSelector(prop.getProperty("leaving"))).click();
		  driver.findElement(By.cssSelector(prop.getProperty("leaving-search"))).sendKeys(leaving+ Keys.ENTER);
		  
		  driver.findElement(By.cssSelector(prop.getProperty("going1"))).click();
		  driver.findElement(By.cssSelector(prop.getProperty("going1-search"))).sendKeys(going1+ Keys.ENTER);
		 
		  
		  // Entering Flight 2 Details
	  	  driver.findElement(By.cssSelector(prop.getProperty("going2"))).click();
		  driver.findElement(By.cssSelector(prop.getProperty("going2-search"))).sendKeys(going2 + Keys.ENTER);
		 
		  RunnerMultiCity.setDate(date1, date2);	
		  try {
		  driver.findElement(By.cssSelector(prop.getProperty("search"))).click(); //Search Button
		  tc.log(Status.PASS, "Searching started");
		  } catch(Exception e) {
			  tc.log(Status.FAIL, "Searching failed");
		  }
	}

	public static void setDate(String date1, String date2) {
		
		
		try {
		  // Selecting Date 1
		  driver.findElement(By.cssSelector(prop.getProperty("date1-button"))).click();
		  JavascriptExecutor js = (JavascriptExecutor)driver;
		  js.executeScript("window.scrollBy(0,200)"); // for scrolling down
		  String f1Date = "button[aria-label='" + date1 + "']";
		  driver.findElement(By.cssSelector(f1Date)).click(); 
		  driver.findElement(By.cssSelector(prop.getProperty("date-done"))).click();
		  
		  // Selecting Date 2
		  driver.findElement(By.xpath(prop.getProperty("date2-button"))).click();
		  js.executeScript("window.scrollBy(0,200)"); // for scrolling down
		  String f2Date = "button[aria-label='" + date2 + "']";
		  driver.findElement(By.cssSelector(f2Date)).click(); 
		  driver.findElement(By.cssSelector(prop.getProperty("date-done"))).click();
		  
		  tc.log(Status.PASS, "Date selection done successfully");
		
		}catch(Exception e) {
			tc.log(Status.FAIL, "Date selection failed");
		}
	}
	
	public static void tillCheckout() throws Exception {
		
		  tc.log(Status.PASS, "Source and Destination Entered");
		
		  // Selecting Flight 1
		  JavascriptExecutor js = (JavascriptExecutor)driver;
		  js.executeScript("window.scrollBy(0,500)"); 
		  WebDriverWait wait = new WebDriverWait(driver, 10);
		  wait.until(ExpectedConditions.urlContains("Search"));
		  driver.findElement(By.xpath(prop.getProperty("flight1"))).click();
		  driver.findElement(By.cssSelector(prop.getProperty("continue"))).click();
		  
		  wt= new WebDriverWait(driver, 20);
		
		  // Selecting Flight 2
		  js.executeScript("window.scrollBy(0,500)"); Thread.sleep(2000);
		  driver.findElement(By.xpath(prop.getProperty("flight2"))).click();
		  driver.findElement(By.cssSelector(prop.getProperty("continue"))).click();
		  
		  Set<String> allWin = driver.getWindowHandles();
		  List<String> allWin2 = new ArrayList<String>(allWin); 
		  driver.switchTo().window(allWin2.get(1));
		  
		  tc.log(Status.PASS, "Flights Selected");
		  
		  try {
		  wt.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(prop.getProperty("Checkout"))));
		  driver.findElement(By.cssSelector(prop.getProperty("Checkout"))).click();
		  ex.setCellValue("Multi", rowNum, 7, "Passed");
		  System.out.println("Passed");
		  tc.log(Status.PASS, "Checkout Done");
		  tc.pass("Test Case Passed"); // Passing Step Information
		  driver.close();
		  }
		  catch(Exception e) {
			  tc.log(Status.FAIL, "Error: Checkout Failed");
			  e.printStackTrace();
		  }
	}
	
	public static void nullValues() throws Exception {
		try {		
		driver.findElement(By.cssSelector(prop.getProperty("search"))).click();
		tc.log(Status.PASS, "Submitted Without Entering Values");
		String error1 = driver.findElement(By.id(prop.getProperty("origin-error"))).getText();
		String error2 = driver.findElement(By.id(prop.getProperty("dest1-error"))).getText();
		System.out.println("Error: " +error1 +" and " + error2);
		ex.setCellValue("Multi", rowNum, 7, "Passed");
		ex.setCellValue("Multi", rowNum, 8, error1+" & "+error2);
		tc.pass("Test Case Passed (Error: " + error1 +" & "+ error2 +")"); // Passing Step Information
		}
		catch(Exception e) {
			ex.setCellValue("Multi", rowNum, 7, "Failed");
			System.out.println("Failed");
			tc.fail("Test Case Failed");
		}
	}
	
	public static void leavingequalgoing1(String leaving, String going1, String going2, String date1, String date2) throws Exception {
			
		  RunnerMultiCity.enterData(leaving, going1, going2, date1, date2);
		  tc.log(Status.PASS, "Entered Same Source and Destinaiton");
		  
		  try {
		  String error = driver.findElement(By.id(prop.getProperty("dest1-error"))).getText();
		  System.out.println("Error: " +error);
		  ex.setCellValue("Multi", rowNum, 7, "Passed");
		  ex.setCellValue("Multi", rowNum, 8, error);	
		  tc.pass("Test Case Passed (Error: " + error +")"); // Passing Step Information
		  } 
		  catch(Exception e) {
			ex.setCellValue("Multi", rowNum, 7, "Failed");
			System.out.println("Failed");
			tc.fail("Test Case Failed");
		}
	}
	
	public static void invalidFormat(String leaving, String going1, String going2, String date1, String date2) throws Exception {
		RunnerMultiCity.enterData(leaving, going1, going2, date1, date2);
		tc.log(Status.PASS, "Entered Invalid Name");
		
			try {	  
			  String error = driver.findElement(By.id(prop.getProperty("name-error"))).getText();
			  System.out.println("Error: " +error);
			  ex.setCellValue("Multi", rowNum, 7, "Passed");
			  ex.setCellValue("Multi", rowNum, 8, error);	
			  tc.pass("Test Case Passed (Error: " + error +")"); // Passing Step Information
			} 
			catch(Exception e) {
				ex.setCellValue("Multi", rowNum, 7, "Failed");
				System.out.println("Failed");
				tc.fail("Test Case Failed");
			}
		
	}
	
	public static void writeExcel() throws Exception {
		FileOutputStream outFile = new FileOutputStream(new File("src/test/resources/DataKeys.xlsx"));
		wb.write(outFile);
	}
	
	
	@DataProvider
	public Object[][] dp() {
		int rowCount = ex.getLastRowNum("Multi");
		System.out.println("Row Count: " +rowCount);
		Object data[][] = new Object[rowCount][6];
		for (int i = 1; i < rowCount + 1; i++) { 
			for (int j = 0; j < 6; j++) { //columns
				data[i - 1][j] = ex.readData("Multi", i, j);
			}
		}
		return data;
	}

	  @BeforeMethod
		public void beforeTest() throws FileNotFoundException, IOException {
			driver = Drivers.getChromeDriver();		
			prop = SetupProperties.webProp();
		}

	  @AfterMethod
		public void afterTest() {
			// For closing the browser
			driver.quit();
		}
}

