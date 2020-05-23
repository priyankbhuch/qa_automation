package com.crossover.e2e;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GMailTest extends TestCase {
    private WebDriver driver;
    private Properties properties = new Properties();

    public void setUp() throws Exception {
        
        properties.load(new FileReader(new File("src/test/resources/test.properties")));
        //Dont Change below line. Set this value in test.properties file incase you need to change it..
        System.setProperty("webdriver.chrome.driver",properties.getProperty("webdriver.chrome.driver"));
             
        driver = new ChromeDriver();
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
                
    }

    public void tearDown() throws Exception {
        driver.quit();
    }

    /*
     * Please focus on completing the task
     * 
     */
    @Test
    public void testSendEmail() throws Exception {
    	
    	try
    	{
        
    	//	WebDriverWait wait = new WebDriverWait(driver, 15);		
    		
    	//Navigate to Gmail
    	driver.get("https://mail.google.com/");
        Thread.sleep(1000);
            	
    	//Login to Gmail
        WebElement userElement = driver.findElement(By.id("identifierId"));
        userElement.sendKeys(properties.getProperty("username"));
        driver.findElement(By.id("identifierNext")).click();
        
        Thread.sleep(5000);
//      driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        WebElement passwordElement = driver.findElement(By.name("password"));
        passwordElement.sendKeys(properties.getProperty("password"));
        driver.findElement(By.id("passwordNext")).click();
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        Thread.sleep(10000);
        
        //Compose an email from subject and body as mentioned in src/test/resources/test.properties
        WebElement composeElement = driver.findElement(By.xpath("//*[@role='button' and (.)='Compose']"));
        composeElement.click();
        driver.findElement(By.name("to")).clear();
        driver.findElement(By.name("to")).sendKeys(String.format("%s@gmail.com", properties.getProperty("username")));
        
        // emailSubject and emailbody to be used in this unit test.
        String emailSubject = properties.getProperty("email.subject");
        String emailBody = properties.getProperty("email.body");
        driver.findElement(By.name("subjectbox")).clear();
        driver.findElement(By.name("subjectbox")).sendKeys(emailSubject);
        driver.findElement(By.cssSelector(".Ar.Au div")).sendKeys(emailBody);
               
        //Label email as "Social"
        Thread.sleep(5000);
        driver.findElement(By.xpath("//*[@data-tooltip='More options' and @role='button']")).click();    
        driver.findElement(By.xpath("//*[@class='J-Ph Gi J-N']")).click();
        WebElement labelSearchBox = driver.findElement(By.xpath("//*[@class='bqf']"));
        labelSearchBox.click();
        labelSearchBox.sendKeys("Social");
        driver.findElement(By.xpath("//*[@class='J-LC-Jz']//span")).click();
        
        Thread.sleep(2000);
                     
    	//Send the email to the same account which was used to login (from and to addresses would be the same)
        driver.findElement(By.xpath("//*[@role='button' and text()='Send']")).click();
        
        //Page refresh
        
        //Wait for the email to arrive in the Inbox
        Thread.sleep(3000);
        driver.findElement(By.xpath("//*[@aria-label='Social' and @role='tab']")).click();
        
/*        //Search the mail
        driver.findElement(By.xpath("//*[@aria-label='Search mail' and @class='gb_nf']")).click();
        driver.findElement(By.xpath("//*[@aria-label='Search mail' and @class='gb_nf']")).sendKeys(emailSubject);
        driver.findElement(By.xpath("//*[@aria-label='Search Mail' and @role='button']")).click();
*/        
        Thread.sleep(5000);
        
        //Get the row index
        int rowIndex = getRowIndexFromSubjectLine(emailSubject);
        
    	//Mark email as starred
        driver.findElement(By.xpath("(//*[@class='Cp'])[4]//tbody/tr["+rowIndex+"]/td[@class='apU xY']/span")).click();
        
        String currentLabel = driver.findElement(By.xpath("(//*[@class='Cp'])[4]//tbody/tr[1]/td[@class='xY a4W']//div[@class='at']")).getAttribute("title");
        if (currentLabel.equalsIgnoreCase("SOCIAL"))
        {
        	System.out.println("Matching Label");
        }
        
        //Open the received email
        driver.findElement(By.xpath("(//*[@class='Cp'])[4]//tbody/tr["+rowIndex+"]/td[@class='xY a4W']/div/div/div[@class='y6']")).click();
        Thread.sleep(2000);
        
    	//Verify email came under proper Label i.e. "Social"
        WebElement socialTag = driver.findElement(By.xpath("//*[@class='cf hX']/tbody/tr[@class='hR']//div[text()='Social']"));
        if(socialTag!=null)
        {
        	System.out.println("Matching Social Label");
        }
        
    	//Verify the subject and body of the received email
    	String currentSubject = driver.findElement(By.xpath("//*[@class='hP']")).getText();
    	if (currentSubject.equals(emailSubject))
    	{
    		System.out.println("Matching Subject Line");
    	}
    	
    	String currentBody = driver.findElement(By.xpath("//*[@class='a3s aXjCH ']/div[@dir]")).getText();
    	if (currentBody.equalsIgnoreCase(emailBody))
    	{
    		System.out.println("Matching body");
    	}
        
        //Generate test execution report at the end
    	
    	//Logout
    	
    	System.out.println("End of script");
    	tearDown();
         
    	}
    	catch (Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
         
        
    }
    
    public int getRowIndexFromSubjectLine (String subjectLine)
    {
    	int rowIndex = 0;
    	String currentSubjectLine = null;
    	
    	int NoOfRows = driver.findElements(By.xpath("(//*[@class='Cp'])[4]//tbody/tr")).size();
    	
    	try
    	{
    		for (int i = 1 ; i<=NoOfRows;)
    		{
    			int d = driver.findElements(By.xpath("(//*[@class='Cp'])[4]//tbody/tr["+i+"]/td[@class='xY a4W']/div/div/div[@class='y6']//span[@class]")).size();
    			currentSubjectLine = driver.findElement(By.xpath("(//*[@class='Cp'])[4]//tbody/tr["+i+"]/td[@class='xY a4W']//div[@class='y6']//span[@class]")).getText();
    			if (currentSubjectLine.equalsIgnoreCase(subjectLine))
    			{
    				return i;
    			}
    			else
    			{
    				i++;
    			}
    		}
    		
    	}
    	catch (Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
    	return rowIndex;
    }
    
   }
