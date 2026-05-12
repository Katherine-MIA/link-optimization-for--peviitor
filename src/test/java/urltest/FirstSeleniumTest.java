package urltest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FirstSeleniumTest {
    //Interface that provides web browser control and helps
    //in finding elements
    WebDriver driver;
    //Code with this annotation will run before class
    //used for test setup
    @BeforeClass
    public void setUp(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://api.peviitor.ro/v1/search/?page=1");
    }

    //After class is finished
    //Usually cleanup code
    @AfterTest
    public void tearDown(){
        //Selenium standard: closes every window and quits the driver;
        driver.quit();
        //Only closes current window
        //driver.close();
    }

    @Test
    public void testInvalidLinks() throws InterruptedException{
        Thread.sleep(20000);

        System.out.println( driver.getCurrentUrl());
    }
}
