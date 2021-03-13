# Objective
Technical evaluation about test automation and development skills.

# About Project (test scenarios created)
To be more fluid, I created only one project and two separated classes for each automation challenge.

Here in this repository you will find a project I created using __Java8__ as main language, __Maven__ to manage dependencies, __Junit__ framework to create each request of the challenge as a test, __Selenium-WebDriver__ framework to manipulate my tests and using some resources.


# How to Run the Project
* Fork the project or download zip file if you prefer.
* You should have [Java 8](https://www.java.com/en/download/help/index_installing.html) installed.
* You should have [ChromeDriver](https://chromedriver.chromium.org/downloads) installed, or other browser-driver you prefer.
* This project was created on IntelliJ but you can choose another IDE you prefer like IntelliJ or VSCode.
* Once you have environment ready just open the project folder on your IDE.
* Usually the IDE will ask to download all dependencies (described on __pom.xml__) as others dependencies needed to support Java.
  * In case of VSCode you can use this [documentation](https://code.visualstudio.com/docs/languages/java) for your reference.
* Before run the tests you should change the path from chrome-driver. This change should be done on both Java classes:
  * CesarShoolWebTest at @before block
  * DiscourseWebTest at @before block 
```shell
@Before
    public void setUp(){

        //Environment Configuration
        System.setProperty("webdriver.chrome.driver", "/home/user/drivers/chromedriver");
```
* Now, you are ready to run the tests on your IDE. This may change from IDE to IDE:
  * In case of VSCode there is a tab called "Test". You just need to select it and then press __Run All Tests__
  * Using IntelliJ you just need to right click on folder __src/test/java__ then __Run 'All Tests'__

__Note:__ You can run a single test or single java class if you prefer.
