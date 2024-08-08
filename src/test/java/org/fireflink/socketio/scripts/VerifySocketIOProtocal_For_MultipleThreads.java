package org.fireflink.socketio.scripts;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.fireflink.socketio.utilities.ExcelUtility;
import org.fireflink.socketio.utilities.SshUtility;
import org.fireflink.socketio.utilities.WebDriverUtility;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jcraft.jsch.Session;

public class VerifySocketIOProtocal_For_MultipleThreads {

	// Create Object for utilities
	ExcelUtility excelUtility = new ExcelUtility();
	WebDriverUtility webDriverUtility = new WebDriverUtility();
	SshUtility sshUtility = new SshUtility();
	Session session = null;
	String dockerId = "";
	
	@BeforeMethod
	public void preCondition() {
		// Connect to ec2 machine
		String sshHost = "ec2-13-236-179-177.ap-southeast-2.compute.amazonaws.com";
		String portNumber = "22";
		String sshUsername = "ec2-user";
		String sshPassword = "Fireflink@123";
		session = sshUtility.connectToSSHHost(sshHost, portNumber, sshUsername, sshPassword);

		// Execute commands to deploy and start the ChatApp
		sshUtility.executeLinuxCommand(session, "docker run -d -p 3000:3000 chatapp:v1");

		//get the docker app PS ID
		String response = sshUtility.executeLinuxCommand(session, "docker ps");		
		dockerId = sshUtility.getContainerID(response);
		System.out.println(dockerId);
	}

	@Test
	public void verifySocketIOProtocal_For_MultipleThreads() throws EncryptedDocumentException, IOException, InterruptedException {

		// Get data from excel
		Map<String, List<String>> entireData = excelUtility.getDataFromExcelAsMap(".\\src\\test\\resources\\SocketIO_TestData.xlsx", "messages");
		System.out.println(entireData);

		int numberOfThreads = 5;
		int numberOfMessage = 2;

		// Open specified number of threads
		List<WebDriver> browserThreadCountList = webDriverUtility.openSpecifiedNumberOfThreads(numberOfThreads, "http://13.236.179.177:3000/");

		// Chat with the users
		String chatText = webDriverUtility.chatWithTheUsers(browserThreadCountList, entireData, numberOfMessage);
		System.out.println(chatText);
	}

	@AfterMethod
	public void postCondition() {
		// Execute commands to stop the ChatApp
		sshUtility.executeLinuxCommand(session, "docker stop "+dockerId);

		// Disconnect from the ec2 machine
		sshUtility.disconnectSshHost(session);
	}
}
