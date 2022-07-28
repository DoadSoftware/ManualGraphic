package com.manual.model;

import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class Scene {
	
	private String scene_path;
	private String broadcaster;
	
	public Scene() {
		super();
	}

	public Scene(String scene_path) {
		super();
		this.scene_path = scene_path;
	}
	
	public String getScene_path() {
		return scene_path;
	}

	public void setScene_path(String scene_path) {
		this.scene_path = scene_path;
	}
	
	public String getBroadcaster() {
		return broadcaster;
	}

	public void setBroadcaster(String broadcaster) {
		this.broadcaster = broadcaster;
	}

	public void scene_load(PrintWriter print_writer,String scene_path) throws InterruptedException
	{
		print_writer.println("LAYER1*EVEREST*SINGLE_SCENE LOAD " + scene_path + ";");
		
		//print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
		//print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
		TimeUnit.SECONDS.sleep(1);
		
		//print_writer.println("LAYER1*EVEREST*SINGLE_SCENE CLEAR;");
	}
}
