package io.github.jadefalke2.connectivity.practice;

import io.github.jadefalke2.Script;
import out.OutPacket;
import out.PlayerGo;
import server.Server;
import util.Util;

import java.io.IOException;

public class ServerConnector {

	public record GoConfig(String stageName, int scenario, String entrance) {}

	private final Server server;
	private Thread thread;

	public ServerConnector() {
		server = new Server(7901, 7902);
		thread = null;
	}

	public void runScript(Script script, GoConfig dest) throws IOException, InterruptedException {
		OutPacket[] scriptPackets = Util.sendScript(script.getFull(), script.getName());
		server.sendPackets(scriptPackets);

		OutPacket warpAndStart = new PlayerGo(dest.stageName(), dest.scenario(), dest.entrance(), true);
		server.sendPacket(warpAndStart);
	}

	public void setRunning(boolean state){
		if(isRunning() == state) { //matches in state, do nothing
			return;
		} else if(!isRunning() && state) { //start server
			thread = server.startLoopThread();
		} else { //stop server
			server.stopLoopThread();
			thread = null;
		}
	}
	public boolean isRunning() {
		return thread != null;
	}

}
