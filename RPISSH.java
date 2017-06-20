package org.jointheleague.ecolban.cleverrobot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.regex.Pattern;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class RPISSH {
	public static void main(String[] args) {
		String nmapLocation = "C:\\users\\jointheleague\\Desktop\\nmap-7.50\\nmap.exe";
		String airportLocation = "/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport ";

		ProcessBuilder builder = new ProcessBuilder(nmapLocation, "-sP", "192.168.1.1/24");

		try {
			JSch jsch = new JSch();
			Process proc = builder.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.contains("rpi")) {
					System.out.println(line);
					String split = line.trim().split(Pattern.quote("("))[1];
					split = split.substring(0, split.length() - 1);

					System.out.print("Connecting to " + split + "... ");
					Session session = jsch.getSession("pi", split, 22);
					session.setPassword("pi");
					session.setConfig("StrictHostKeyChecking", "no");
					session.connect();
					System.out.println("connected.");
					System.out.print("Sending channel data... ");
					ChannelExec channel = (ChannelExec) session.openChannel("exec");
					channel.setCommand("sudo shutdown -t 0");
					channel.connect();
					System.out.println("sent.");

					System.out.println("Disconnecting from " + split + "...");
					Thread.sleep(1000);
					channel.disconnect();
					session.disconnect();
					System.out.println("Disconnected.");
				}
			}
			in.close();

			Process airport = new ProcessBuilder(airportLocation, "-s").start();
			in = new BufferedReader(new InputStreamReader(airport.getInputStream()));
			while ((line = in.readLine()) != null) {
			}
		} catch (ConnectException e) {
			System.out.print("failed to connect.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
