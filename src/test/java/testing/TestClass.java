package testing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import frameWork.Drivers;
import frameWork.SetupProperties;

public class TestClass {
  @Test
  public void f() throws FileNotFoundException, IOException {
	  WebDriver driver = Drivers.getChromeDriver();
	  Properties prop = SetupProperties.webProp();
	  driver.get(prop.getProperty("url"));
	  driver.findElement(By.xpath(prop.getProperty("account"))).click();
	  driver.findElement(By.xpath(prop.getProperty("createAccount"))).click();
  }
}
