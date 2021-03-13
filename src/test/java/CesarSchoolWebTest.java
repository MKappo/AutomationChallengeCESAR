import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CesarSchoolWebTest {

    private WebDriver navegador;

    @Before
    public void setUp(){

        //Environment Configuration
        System.setProperty("webdriver.chrome.driver", "/home/mkapobianco/Drivers/chromedriver");
        navegador = new ChromeDriver();
        navegador.get("https://www.cesar.school/");
        navegador.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        navegador.manage().window().maximize();

        //Reaching Blog Page
        Actions action = new Actions(navegador);
        WebElement dropDownMenu = navegador.findElement(By.linkText("SCHOOL"));
        action.moveToElement(dropDownMenu).build().perform();
        navegador.findElement(By.linkText("Blog")).click();

        //Access second page
        navegador.findElement(By.linkText("2")).click();
    }

    @Test
    public void testPrintTitleAndDateFromSecondPostAtSecondPage() {

        //Inspecting code I decided to collect all articles in the page
        List<WebElement> valueExtracted = navegador.findElements((By.cssSelector("article.post")));

        //I created these two list to manipulate data later.
        List<String> articleTitles = new ArrayList<>();
        List<String> articleDates = new ArrayList<>();

        //This loop is just to populate my arrays and later choose and date I need
        for (WebElement extracted : valueExtracted) {

            //Filtering date to attend my objective in this testing.
            String title = extracted.findElement(By.className("entry-title")).getText();
            String month = extracted.findElement(By.className("date-month")).getText();
            String day = extracted.findElement(By.className("date-day")).getText();
            String year = extracted.findElement(By.className("date-year")).getText();

            //Just a way to merge data before populate the Array
            String completeDate = (day + "-" + month + "-" + year);

            articleTitles.add(title);
            articleDates.add(completeDate);

        }

        System.out.println("Second article details at second School blog page:");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Article Name: " + articleTitles.get(1));
        System.out.println("Created Date: " + articleDates.get(1));

    }

    @Test
    public void testPrintTitleAndAuthorFromThirdPostAtSecondPage() {

        //I created these two list to manipulate data later.
        List<WebElement> valueExtracted = navegador.findElements((By.cssSelector("article.post")));

        //I created these two list to manipulate data later.
        List<String> articleTitles = new ArrayList<>();
        List<String> articleAuthor = new ArrayList<>();

        //This loop is just to populate my arrays and later choose and date I need
        for (WebElement extracted : valueExtracted) {

            //Filtering date to attend my objective in this testing.
            String title = extracted.findElement(By.className("entry-title")).getText();
            String author = extracted.findElement(By.className("author-name")).getText();

            articleTitles.add(title);
            articleAuthor.add(author);

        }

        System.out.println("Third article details at second School blog page:");
        System.out.println("-------------------------------------------------------------");
        System.out.println("Article Name: " + articleTitles.get(2));
        System.out.println("Written By: " + articleAuthor.get(2));

    }

    @Test
    public void testPrintSchoolAddressFoundInTheEndOfBlogPage() {

        /*Scroll down until end of the page using first keyboard commands
        and then finding a fix element in the end of the page
        */
        JavascriptExecutor js = (JavascriptExecutor) navegador;
        navegador.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
        WebElement textEndOfPage = navegador.findElement(By.id("custom_html-3"));
        js.executeScript("arguments[0].scrollIntoView();", textEndOfPage);



        navegador.findElement(By.id("custom_html-3")).findElement(By.className("redes")).findElement(By.cssSelector("a")).click();

        String Address = navegador.findElement(By.className("_2wzd")).getText();

        System.out.println("Note: There is  no address in the end of School page as informed");
        System.out.println("----------------------------------------------------------------");
        System.out.println("So, the address from school found on Facebook is: ");
        System.out.println(Address);



    }

    @After
    public void tearDown(){

        //Closing navigator
        navegador.quit();
    }
}
