package testCases;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import frameWork.Drivers;
import frameWork.SetupProperties;
import operations.ReadWriteExcelFile;

public class RunnerSignUp extends ReadWriteExcelFile {

	static WebDriver driver;
	static Properties prop;
	static ReadWriteExcelFile ex = new ReadWriteExcelFile("src//test//resources//DataKeys.xlsx");
	static Workbook wb = ex.getWorkbook();
	static int rowNum = 2;
	static int testNum = 1;
	static ExtentReports exr = new ExtentReports(); // creating objects for ExtentReports class
	static ExtentTest tc;
	static String sheetName = "Signup";

	public RunnerSignUp(String pathWithFileName) {
		super(pathWithFileName);
	}
    @Test(dataProvider = "dp")
	public static void signupTest(String fname, String lname, String email, String pwd, String cpwd) throws IOException {
		try {
			String error = null;
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			exr.attachReporter(new ExtentHtmlReporter("SignUpReport.html"));
			tc = exr.createTest("CreateAccountTest : " + testNum);
			tc.info("Opening URL");
			driver.get(prop.getProperty("url"));
			RunnerSignUp.getSignUpPage();
			tc.info("Entering the details and clicking the create account button");
			
			//Finding elements in the website and performing particular operations on it

			driver.findElement(By.id(prop.getProperty("firstnameid"))).clear();
			driver.findElement(By.id(prop.getProperty("lastnameid"))).clear();
			driver.findElement(By.id(prop.getProperty("emailid"))).clear();
			driver.findElement(By.id(prop.getProperty("passwordid"))).clear();
			driver.findElement(By.id(prop.getProperty("confirmpasswordid"))).clear();

			driver.findElement(By.id(prop.getProperty("firstnameid"))).sendKeys(fname); // Please enter first name using
																						// letters only.
			driver.findElement(By.id(prop.getProperty("lastnameid"))).sendKeys(lname);
			driver.findElement(By.id(prop.getProperty("emailid"))).sendKeys(email);
			driver.findElement(By.id(prop.getProperty("passwordid"))).sendKeys(pwd);
			driver.findElement(By.id(prop.getProperty("confirmpasswordid"))).sendKeys(cpwd);
			 RunnerSignUp.handlingNullFileds(fname,lname,email,pwd,cpwd);
			driver.findElement(By.id(prop.getProperty("createaccountbuttonid"))).click();
			
			if (driver.findElement(By.id("create_first_name_error")).isDisplayed()) {

				error = driver.findElement(By.id("create_first_name_error")).getText();
				System.out.println(error);

			}
 
			else if (driver.findElement(By.id("create_last_name_error")).isDisplayed()) {
				error = driver.findElement(By.id("create_last_name_error")).getText();
				System.out.println(error);

			}

			else if (driver.findElement(By.id("create_email_error")).isDisplayed()) {
				error = driver.findElement(By.id("create_email_error")).getText();
				System.out.println(error);
			}

			else if (driver.findElement(By.xpath("//h5[normalize-space()='Password must be at least 6 characters.']"))
					.isDisplayed()) {
				error = driver
						.findElement(By.xpath("//h5[normalize-space()='Password must be at least 6 characters.']"))
						.getText();
				System.out.println(error);

			}

			else if (driver.findElement(By.xpath("//h5[normalize-space()='Passwords do not match.']")).isDisplayed()) {
				error = driver.findElement(By.xpath("//h5[normalize-space()='Passwords do not match.']")).getText();
				System.out.println(error);
			}
			else if(driver.findElement(By.xpath("//p[@id='create_confirm_password_error']")).isDisplayed()) {
				error = driver.findElement(By.xpath("//p[@id='create_confirm_password_error']")).getText();
				System.out.println(error);
			}
			tc.pass("Test Case Passed  Error: " + error);
			ex.setCellValue(sheetName, rowNum, 6, error);
			ex.setCellValue(sheetName, rowNum, 7, "Passed");
			
		} catch (Exception e) {
			tc.fail("Test Case Failed");
			ex.setCellValue("Signup", rowNum, 6, "Failed");
			ex.setCellValue("Signup", rowNum, 7, "Error occured while executing testcases");
		}
		
		RunnerSignUp.writeExcel();
		System.out.println(rowNum);
		rowNum++;
		testNum++;
		exr.flush();

	}

		
	@DataProvider
	public Object[][] dp() {
		int rowCount = ex.getLastRowNum(sheetName);
		System.out.println(rowCount);
		Object data[][] = new Object[rowCount][5];
		for (int i = 1; i < rowCount + 1; i++) {
			for (int j = 0; j < 5; j++) {
				data[i - 1][j] = ex.readData(sheetName, i, j);
			}
		}
		return data;

	}
	
	public static void handlingNullFileds(String fname, String lname, String email, String pwd, String cpwd) throws InterruptedException {
		if(fname.equals("Na")) {
			driver.findElement(By.id(prop.getProperty("firstnameid"))).clear();
		}else if(lname.equals("Na")) {
			driver.findElement(By.id(prop.getProperty("lastnameid"))).clear();
		}else if(email.equals("Na")) {
			driver.findElement(By.id(prop.getProperty("emailid"))).clear();
		}else if(pwd.equals("Na")) {
			driver.findElement(By.id(prop.getProperty("passwordid"))).clear();
		}else if(cpwd.equals("Na")) {
			driver.findElement(By.id(prop.getProperty("confirmpasswordid"))).clear();
		}
		Thread.sleep(2000);
	}

	public static void writeExcel() throws IOException {
		FileOutputStream outFile = new FileOutputStream(new File("src/test/resources/DataKeys.xlsx"));
		wb.write(outFile);
	}
	
	public static void getSignUpPage() throws Exception {
		try {
			driver.findElement(By.xpath(prop.getProperty("account"))).click();
			driver.findElement(By.xpath(prop.getProperty("createAccount"))).click();
		} catch (Exception e) {
		}
	}

	@BeforeClass
	public void beforeClass() throws FileNotFoundException, IOException {

		driver = Drivers.getChromeDriver();
		prop = SetupProperties.webProp();

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
