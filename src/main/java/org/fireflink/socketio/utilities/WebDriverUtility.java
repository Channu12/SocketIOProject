package org.fireflink.socketio.utilities;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverUtility {

	public List<WebDriver> openSpecifiedNumberOfThreads(int numberOfThreads, String url) {
		List<WebDriver> driversList = new LinkedList<WebDriver>();
		for (int i = 0; i < numberOfThreads; i++) {
			WebDriverManager.chromedriver().setup();
			ChromeDriver driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			driver.get(url);
			driversList.add(driver);
		}
		return driversList;
	}

	public String chatWithTheUsers(List<WebDriver> driverList, Map<String, List<String>> entireData,
			int numberOfMessages) throws InterruptedException {
		int numberOfThreads = driverList.size();
		int threadNumber = 1;
		String text = "";
		for (Entry<String, List<String>> singleUserData : entireData.entrySet()) {
			if (threadNumber <= numberOfThreads) {
				driverList.get(threadNumber - 1).findElement(By.id("input")).sendKeys(singleUserData.getKey());
				driverList.get(threadNumber - 1).findElement(By.xpath("//Button[text()='Send']")).click();
				for (int i = 0; i < numberOfMessages; i++) {
					driverList.get(threadNumber - 1).findElement(By.id("input"))
					.sendKeys(singleUserData.getValue().get(i));
					driverList.get(threadNumber - 1).findElement(By.xpath("//Button[text()='Send']")).click();
					Thread.sleep(1000);
				}
			} else {
				break;
			}
			Thread.sleep(1000);
			threadNumber++;
		}

		for (int i = 0; i < driverList.size(); i++) {
			driverList.get(i).findElement(By.xpath("//Button[text()='Disconnect']")).click();
			Thread.sleep(1000);
			if (i == driverList.size() - 1) {
				text = driverList.get(i).findElement(By.xpath("//body")).getText();
			}
			driverList.get(i).close();
		}
		return text;
	}
}

