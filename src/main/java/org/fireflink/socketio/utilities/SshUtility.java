package org.fireflink.socketio.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SshUtility {

	public Session connectToSSHHost(String host, String port, String username, String password) {
		Session session = null;
		try {
			JSch jsch = new JSch();

			// Create session
			session = jsch.getSession(username, host, Integer.parseInt(port));
			session.setPassword(password);

			// Set additional configurations, e.g., for accepting unknown host keys
			session.setConfig("StrictHostKeyChecking", "no");

			// Connect to the remote server
			session.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}

	public void disconnectSshHost(Session session) {
		try {
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ftpFromLocalToRemote(Session session, String localFilePath, String remoteDirectory) {
		ChannelSftp sftpChannel = null;

		// Check if local file exists
		File localFile = new File(localFilePath);
		if (!localFile.exists()) {
			System.out.println("Local file not found: " + localFilePath);
		}

		try {
			// Create SFTP channel
			sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();

			// Upload a file to the remote server
			// Extract filename from localFilePath
			String localFileName = new File(localFilePath).getName();
			String remoteFilePath = remoteDirectory + localFileName;

			try {
				// Check if remote file exists
				sftpChannel.stat(remoteFilePath);

				// If the file exists, remove it before uploading the new file
				sftpChannel.rm(remoteFilePath);
			} catch (SftpException e) {
				// TODO Auto-generated catch block
			}

			// Transfer file
			sftpChannel.put(new FileInputStream(localFilePath), remoteFilePath);
			System.out.println("File transfer successful!");

		} catch (Exception e) {
			System.out.println("Failed to transfer the files.." + e);
			e.printStackTrace();
		} finally {
			sftpChannel.disconnect();
		}
	}

	public String executeLinuxCommand(Session session, String command) {
		String res = "";
		ChannelExec channelExec = null;

		try {
			// Execute a command (e.g., "whoami")
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);

			// Set up input and output streams
			InputStream in = channelExec.getInputStream();
			channelExec.connect();

			// Read the command output
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				res = res+line+"\n";
			}
			System.out.println(command+" Command Execution is Successful");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(command+" Command Execution is Failed");
		}finally {
			channelExec.disconnect();
		}
		return res.trim();
	}
	
	public String getContainerID(String response) {
		return response.split("\n")[1].split(" ")[0].trim();
	}
}
