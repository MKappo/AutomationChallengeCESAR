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

        System.out.println("The following topics are locked:");
        System.out.println("-------------------------------------------");

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


        System.out.println("Topics with a Category associated:");
        System.out.println("-----------------------------------");
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
            if (topics > 0)
                System.out.println("The category '" + category + "' has " + topics + " topics associated");
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
        List<Double> viewsHigherNumbersConverted = new ArrayList<>();

        //This variable is to control which array to use to collect views and topic name.
        boolean hasViewsOver1k = false;


        //Now with the list of elements, I created a loop run to populate my arrays
        for (WebElement extracted : valueExtracted) {

            //Both filtering a little bit more to get number of views and topic names.
            String numberOfViews = extracted.findElement(By.cssSelector("td.num.views")).getText();
            String topicName = extracted.findElement(By.cssSelector("a.title")).getText();


            //Here I created the condition to split the date in proper array I've created.
            if (numberOfViews.contains("k")) {
                hasViewsOver1k = true;
                String newValue = numberOfViews.replace("k","");
                viewsHighersNumbers.add(newValue);

                /*
                Still due to "k" design decision for number over 1 thousand, I faced a big
                challenge to make it happens. So after remove 'k' on previous line, here
                was need to filter numbers to String than convert to double.
                 */
                String filtering = viewsHighersNumbers.get(viewsHighersNumbers.size()-1);
                double converting = Double.parseDouble(filtering);

                //now with the value clean, I could add it in another array and sort properly.
                viewsHigherNumbersConverted.add(converting);
                Collections.sort(viewsHigherNumbersConverted);

                /*
                This code below is to ensure the name of topic with higher view always
                would be added at the end of array, as I did for view numbers.
                 */

                if(viewsHigherNumbersConverted.get(viewsHigherNumbersConverted.size()-1) <= converting){
                    topicTitleNameForHigher1k.add(topicName);
                }else{
                    topicTitleNameForHigher1k.add(0, topicName);
                }


            } else {

                viewsNumbers.add(Integer.parseInt(numberOfViews));
                Collections.sort(viewsNumbers);
                int indexNumber = viewsNumbers.indexOf(Integer.parseInt(numberOfViews));

                //Same logic used above, but here for cases where there is no values equal or over 1k
                if(indexNumber > topicTitleName.size()){
                    topicTitleName.add(topicName);
                }else{
                    topicTitleName.add(indexNumber, topicName);
                }
            }

        }


     //Once I have all data in my arrays i just used my "control variable" to decide what to show.
        if (hasViewsOver1k){
            System.out.println("The most viewed topic is '" + topicTitleNameForHigher1k.get(topicTitleNameForHigher1k.size()-1) + "' with " + viewsHigherNumbersConverted.get(viewsHigherNumbersConverted.size()-1) + "k views");
        }else{
            System.out.println("The most viewed topic is '" + topicTitleName.get(topicTitleName.size()-1) + "' with " + viewsNumbers.get(viewsNumbers.size()-1) + "k views");
        }

    }

    @After
    public void tearDown(){

        //Closing navigator
        navegador.quit();
    }

}
