package testCases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import frameWork.Drivers;
import frameWork.SetupProperties;
import operations.ReadWriteExcelFile;

public class RunnerSignin extends ReadWriteExcelFile {

	static int rowNum = 2;
	static int testNum = 1;
	public static WebDriver driver;
	static Properties prop;
	static ReadWriteExcelFile ex = new ReadWriteExcelFile("src//test//resources//DataKeys.xlsx");
	static Workbook wb = ex.getWorkbook();
	static ExtentReports exr = new ExtentReports();
	static ExtentTest tc;

	public RunnerSignin(String pathWithFileName) {
		super(pathWithFileName);
	}

	@Test(dataProvider = "dp")
	public void f(String email, String pwd, String type) throws Exception {
		exr.attachReporter(new ExtentHtmlReporter("SigninReport.html"));
		tc = exr.createTest("LoginTest : " + testNum);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		tc.info("Opening URL");
		driver.get(prop.getProperty("url"));

		switch (type) {
		case "Positive":
			tc.info("Opening SignIn Page");
			RunnerSignin.getSignInPage();
			tc.info("Entering Email and Password and clicking on the submit button");
			RunnerSignin.positiveTestCase(email, pwd);
			break;
		case "Negative":
			RunnerSignin.getSignInPage();
			if (email.contentEquals("Na") && pwd.equals("Na")) {
				tc.info("clicking on the submit button");
				RunnerSignin.negativeTestCaseNullIdPwd();
			} else if (pwd.equals("Na")) {
				tc.info("Entering Email and clicking on the submit button");
				RunnerSignin.negativeTestCaseNullPwd(email);
			} else if (email.contentEquals("Na")) {
				tc.info("Entering Password and clicking on the submit button");
				RunnerSignin.negativeTestCaseNullEmail(pwd);
			} else if (!email.contentEquals("checrc10@gmail.com") || !pwd.contentEquals("cheram@123")) {
				tc.info("Entering worng Password,correct Emailid and clicking on the submit button");
				RunnerSignin.negativeTestCaseInvalidIdorPwd(email, pwd);
			} else {
				System.out.println("Test Case not available ");
			}
		}
		RunnerSignin.writeExcel();
		System.out.println(rowNum);
		rowNum++;
		testNum++;
		exr.flush();

	}

	public static void writeExcel() throws IOException {
		FileOutputStream outFile = new FileOutputStream(new File("src/test/resources/DataKeys.xlsx"));
		wb.write(outFile);
	}

	public static void getSignInPage() throws Exception {
		prop.load(new FileInputStream("src/test/resources/app.properties"));
		driver.findElement(By.xpath(prop.getProperty("account"))).click();
		driver.findElement(By.linkText(prop.getProperty("aSignIn"))).click();
	}

	public static void positiveTestCase(String email, String pwd) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 25);
		driver.findElement(By.id(prop.getProperty("siEmailAddress"))).sendKeys(email);
		driver.findElement(By.id(prop.getProperty("siPassword"))).sendKeys(pwd);
		driver.findElement(By.id(prop.getProperty("siSubmit"))).click();
		boolean element = false;
		try {
			element = wait.until(ExpectedConditions.urlContains("userName"));
		} catch (Exception e) {
		}
		if (element) {
			ex.setCellValue("Signin", rowNum, 4, "Passed");
			tc.pass("Test Case Passed");
			System.out.println("Passed");
		} else {
			ex.setCellValue("Signin", rowNum, 4, "Failed");
			tc.fail("Test Case Failed");
			System.out.println("Failed");
		}
	}

	public static void negativeTestCaseNullPwd(String email) throws Exception {
		driver.findElement(By.id(prop.getProperty("siEmailAddress"))).sendKeys(email);
		driver.findElement(By.id(prop.getProperty("siSubmit"))).click();

		try {
			String error = driver
					.findElement(By.xpath("/html/body/div[4]/div[1]/div/article/div[1]/form/fieldset[1]/label[2]/p"))
					.getText();
			ex.setCellValue("Signin", rowNum, 4, "Passed");
			ex.setCellValue("Signin", rowNum, 5, error);
			System.out.println(error);
			tc.pass("Test Case Passed " + " Error: " + error);
		} catch (Exception e) {
			ex.setCellValue("Signin", rowNum, 4, "Failed");
			tc.fail("Test Case Failed");
			System.out.println("failed");
		}
	}

	public static void negativeTestCaseNullEmail(String pwd) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 25);
		driver.findElement(By.id(prop.getProperty("siPassword"))).sendKeys(pwd);
		driver.findElement(By.id(prop.getProperty("siSubmit"))).click();

		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOf(driver
					.findElement(By.xpath("/html/body/div[4]/div[1]/div/article/div[1]/form/fieldset[1]/label[1]/p"))));
			String error = element.getText();
			ex.setCellValue("Signin", rowNum, 4, "Passed");
			ex.setCellValue("Signin", rowNum, 5, error);
			tc.pass("Test Case Passed " + " Error: " + error);
			System.out.println(error);
		} catch (Exception e) {
			ex.setCellValue("Signin", rowNum, 4, "Failed");
			tc.fail("Test Case Failed");
			System.out.println("failed");
		}
	}

	public static void negativeTestCaseInvalidIdorPwd(String email, String pwd) throws Exception {
		driver.findElement(By.id(prop.getProperty("siEmailAddress"))).sendKeys(email);
		driver.findElement(By.id(prop.getProperty("siPassword"))).sendKeys(pwd);
		driver.findElement(By.id(prop.getProperty("siSubmit"))).click();

		try {
			String error = driver
					.findElement(By.xpath("/html/body/div[4]/div[1]/div/article/div[1]/form/div[1]/div/h5")).getText();
			ex.setCellValue("Signin", rowNum, 4, "Passed");
			ex.setCellValue("Signin", rowNum, 5, error);
			tc.pass("Test Case Passed " + " Error: " + error);
			System.out.println(error);
		} catch (Exception e) {
			ex.setCellValue("Signin", rowNum, 4, "Failed");
			tc.fail("Test Case Failed");
			System.out.println("failed");
		}
	}

	public static void negativeTestCaseNullIdPwd() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 25);
		driver.findElement(By.id(prop.getProperty("siSubmit"))).click();

		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
					"/html[1]/body[1]/div[4]/div[1]/div[1]/article[1]/div[1]/form[1]/fieldset[1]/label[1]/p[1]"))));
			String error = element.getText();
			element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
					"/html[1]/body[1]/div[4]/div[1]/div[1]/article[1]/div[1]/form[1]/fieldset[1]/label[2]/p[1]"))));
			String error2 = element.getText();
			ex.setCellValue("Signin", rowNum, 4, "Passed");
			ex.setCellValue("Signin", rowNum, 5, error + " ," + error2);
			tc.pass("Test Case Passed " + " Error: " + error + " " + error2);
			System.out.println(error);
		} catch (Exception e) {
			ex.setCellValue("Signin", rowNum, 4, "Failed");
			tc.fail("Test Case Failed");
			System.out.println("failed");
		}
	}

	@DataProvider
	public Object[][] dp() {
//		ReadSignupExcelFile ex = new ReadSignupExcelFile("src//test//resources//DataKeys.xlsx");
		int rowCount = ex.getLastRowNum("Signin");
		System.out.println(rowCount);
		Object data[][] = new Object[rowCount][3];
		for (int i = 1; i < rowCount + 1; i++) {
			for (int j = 0; j < 3; j++) {
				data[i - 1][j] = ex.readData("Signin", i, j);
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

		driver.close();
	}

}
