package e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

@Disabled("Requires running frontend/backend and local ChromeDriver")
class FavoriteRoutesE2ETest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void orderRideFromFavorite_routeCardVisible_ordersRide() {
        String baseUrl = "http://localhost:4200";
        String testUserId = "TODO-USER-ID";

        driver.get(baseUrl + "/main-page");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("localStorage.setItem('userId', arguments[0]);", testUserId);

        driver.get(baseUrl + "/order-ride");

        // TODO: Ensure favorite route exists for testUserId before running the test.
        driver.findElement(By.cssSelector("[data-testid='ride-order-favorites-section']"));
        driver.findElement(By.cssSelector("[data-testid='ride-order-favorite-order']")).click();

        driver.findElement(By.cssSelector("[data-testid='ride-order-success']"));
    }

}
