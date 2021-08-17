package frameWork;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Drivers {

    public static  WebDriver getChromeDriver(){
        //setting the path of the executable driver
        System.setProperty("webdriver.chrome.driver","D:\\1. LTI\\Training\\Testing\\Automation\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
		return driver;
    }
    public static WebDriver getFireFoxDriver(){
        //setting the path of the executable driver
        System.setProperty("webdriver.gecko.driver","D:\\1. LTI\\Training\\Testing\\Automation\\geckodriver.exe");
        WebDriver driver = new FirefoxDriver();
		return driver;
    }
}