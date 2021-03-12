import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DiscourseWebTest {

    private WebDriver navegador;

    @Before
    public void setUp(){

        //Environment Configuration
        System.setProperty("webdriver.chrome.driver", "/home/mkapobianco/Drivers/chromedriver");
        navegador = new ChromeDriver();
        navegador.get("https://www.discourse.org/");
        navegador.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        navegador.manage().window().maximize();

        /*
        Since all links on Discourse website has the attribute target="_blank" the code bellow
        was created to switch the focus to the new tab opened and continues running the script
        */
        String oldTab = navegador.getWindowHandle();
        navegador.findElement(By.linkText("Demo")).click();
        navegador.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        ArrayList<String> newTab = new ArrayList<>(navegador.getWindowHandles());
        newTab.remove(oldTab);
        navegador.switchTo().window(newTab.get(0));


        /*Scroll down until end of the page using first keyboard commands
        and then finding a fix element in the end of the page
        */
        JavascriptExecutor js = (JavascriptExecutor) navegador;
        navegador.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
        WebElement textEndOfPage = navegador.findElement(By.tagName("h3"));
        js.executeScript("arguments[0].scrollIntoView();", textEndOfPage);

    }

    @Test
    public void testPrintTitlesFromLockedTopics(){

        /*
        I inspected the website code and noticed the class "closed" inside tr tag
        was the key to find locked topics. So I created a list to collect all elements
        with this class name.
        */

        List<WebElement> lockedTopics = navegador.findElements((By.cssSelector("tr.closed")));

        System.out.println("The following topics are locked: ");

        //Now with the list of elements, I created a loop run throw the list and print the element
        for (int i = 0; i < lockedTopics.size(); i++) {

            /*
            I still used this filter to get text only from locked topic and by inspecting
            website code I found the class title inside the tag "a" as unique key.
            */
            String topicsName = lockedTopics.get(i).findElement(By.cssSelector("a.title")).getText();

            //printing all topics found one by one.
            System.out.println((i+1) + ". " + topicsName);
        }
    }

    @Test
    public void testPrintQtyOfItemsFromEachCategory(){

        /*
        I inspected the website code and noticed the class "category-name" was the key
        to find all topics with at least one category associated. So I created a list
        to collect all elements with this class name.
        */
        List<WebElement> categories = navegador.findElements((By.className("category-name")));

        //I created a new string list to filter and manipulate it later
        List<String> list = new ArrayList<>();

        //Now with the list of elements, I created a loop run to populate my new list
        for (WebElement webElement : categories) {

            list.add(webElement.getText());
        }

        //Now with the list I created a map to manipulate the list
        Map<String, Integer> map = new HashMap<>();

        for (String categoriesName : list) {
            if (map.containsKey(categoriesName)) {
                map.put(categoriesName, map.get(categoriesName) + 1); // increase counter if contains
            } else
                map.put(categoriesName, 1);
        }

        map.forEach((category, topics) -> {
            if (topics > 1)
                System.out.println("The category " + category + " has " + topics + " topics associated");
        });

    }

    @Test
    public void testPrintQtyOfTopicsUncategorized() {

        /*
        I inspected the website code and noticed the class "category-uncategorized" was
        the key to find all topics without a category associated. So I created
        a list to collect all elements with this class name and then print it.
        */
        List<WebElement> uncategorized = navegador.findElements(By.className("category-uncategorized"));
        System.out.println("The Quantity of Topics without Categories is: " + uncategorized.size());
    }

    @Test
    public void testPrintMostCommentedTopic() {

        //After inspect code I decided to collect all information coming from all topics to use it later
        List<WebElement> valueExtracted = navegador.findElements((By.cssSelector("tr.topic-list-item")));

        /*
        I noticed the site decided to use 'k' string for views over 1 thousand so, just convert views
        from string to int and than sort to got higher number would be impossible. Then I decided to
        create different arrays to address this situation.
        */
        List<Integer> viewsNumbers = new ArrayList<>();
        List<String> viewsHighersNumbers = new ArrayList<>();
        List<String> topicTitleName = new ArrayList<>();
        List<String> topicTitleNameForHigher1k = new ArrayList<>();

        //This variable is to control which array to use to collect views and topic name.
        boolean hasViewsOver1k = false;

        //I used this variable to create a correlation between higher number of view and topic name.
        int arrayPosition;

        //Now with the list of elements, I created a loop run to populate my arrays
        for (WebElement extracted : valueExtracted) {

            //Both filtering a little bit more to get number of views and topic names.
            String numberOfViews = extracted.findElement(By.cssSelector("td.num.views")).getText();
            String topicName = extracted.findElement(By.cssSelector("a.title")).getText();

            //Here I created the condition to split the date in proper array I've created.
            if (numberOfViews.contains("k")) {
                hasViewsOver1k = true;
                viewsHighersNumbers.add(numberOfViews);
                Collections.sort(viewsHighersNumbers);

                /*
                This code below is to ensure the name of topic with higher view always
                would be added to end the end of array, as I did for view number.
                 */
                arrayPosition = viewsHighersNumbers.size()-1;
                topicTitleNameForHigher1k.add(arrayPosition, topicName);
                Collections.sort(topicTitleNameForHigher1k);

            } else {
                viewsNumbers.add(Integer.parseInt(numberOfViews));
                Collections.sort(viewsNumbers);

                //Same logic used above, but here for cases where there is no values equal or over 1k
                arrayPosition = viewsNumbers.size()-1;
                topicTitleName.add(arrayPosition, topicName);
                Collections.sort(topicTitleName);

            }

        }

        //Once I have all data in my arrays i juest used my "control variable" to decide what to show.
        if (hasViewsOver1k){
            System.out.println("The most viewed topic (" + topicTitleNameForHigher1k.get(topicTitleNameForHigher1k.size()-1) + ") has " + viewsHighersNumbers.get(viewsHighersNumbers.size()-1) + " views");
        }else{
            System.out.println("The most viewed topic (" + topicTitleName.get(topicTitleName.size()-1) + ") has " + viewsNumbers.get(viewsNumbers.size()-1) + " views");
        }

    }

    @After
    public void tearDown(){

        //Closing navigator
        navegador.quit();
    }

}
