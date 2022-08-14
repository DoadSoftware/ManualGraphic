package com.manual.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.manual.model.Configurations;
import com.manual.model.Container;
import com.manual.model.ContainerData;
import com.manual.model.Scene;
import com.manual.service.ManualService;
import com.manual.util.ManualUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class IndexController 
{
	@Autowired
	ManualService manualService;
	public static Socket session_socket;
	public static Configurations session_Configurations;
	public static PrintWriter print_writer;
	public static ContainerData session_Data;
	String session_selected_sports;
	String Data;
	String Scene;
	@RequestMapping(value = {"/","/initialise"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String initialisePage(ModelMap model) throws JAXBException  
	{
		if(new File(ManualUtil.CONFIGURATION_DIRECTORY + ManualUtil.OUTPUT_XML).exists()) {
			session_Configurations = (Configurations)JAXBContext.newInstance(Configurations.class).createUnmarshaller().unmarshal(
					new File(ManualUtil.CONFIGURATION_DIRECTORY  + ManualUtil.OUTPUT_XML));
		} else {
			session_Configurations = new Configurations();
			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_Configurations, 
					new File(ManualUtil.CONFIGURATION_DIRECTORY + ManualUtil.OUTPUT_XML));
		}
		model.addAttribute("session_Configurations",session_Configurations);
		
		return "initialise";
	}

	@RequestMapping(value = {"/manual"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String manualPage(ModelMap model, MultipartHttpServletRequest request,
			@RequestParam(value = "select_sports", required = false, defaultValue = "") String select_sports,
			@RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddresss,
			@RequestParam(value = "vizPortNumber", required = false, defaultValue = "") int vizPortNumber)
			throws UnknownHostException,JAXBException, IOException,IllegalAccessException,InvocationTargetException
	{
		session_selected_sports = select_sports;
		session_socket = new Socket(vizIPAddresss, Integer.valueOf(vizPortNumber));
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		session_Configurations = new Configurations(vizIPAddresss, vizPortNumber);
		JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_Configurations, 
				new File(ManualUtil.CONFIGURATION_DIRECTORY + ManualUtil.OUTPUT_XML));
		switch(session_selected_sports) {
		case "BADMINTON,":
			model.addAttribute("session_viz_scenes", new File(ManualUtil.BADMINTON_SCENE_DIRECTORY + 
					ManualUtil.SCENES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".sum") && pathname.isFile();
			    }
			}));
			
			model.addAttribute("scene_files", new File(ManualUtil.BADMINTON_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + ManualUtil.DATA_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".xml") && pathname.isFile();
			    }
			}));
			break;
		case "CRICKET,":
			model.addAttribute("session_viz_scenes", new File(ManualUtil.CRICKET_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".sum") && pathname.isFile();
			    }
			}));
			
			model.addAttribute("scene_files", new File(ManualUtil.CRICKET_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + ManualUtil.DATA_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".xml") && pathname.isFile();
			    }
			}));
			break;
		}
		
		model.addAttribute("session_selected_sports", session_selected_sports);
		return "manual";
	}
	
	@RequestMapping(value = {"/save_data"}, method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String uploadFormDataToSessionObjects(MultipartHttpServletRequest request)
					throws IllegalAccessException, InvocationTargetException, IOException, JAXBException
	{	
		String file_name = "";
			if (request.getRequestURI().contains("save_data")) {
				
				List<Container> containers = new ArrayList<Container>();
				
				for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
					if(entry.getKey().contains("previous_xml_data") || entry.getKey().contains("selectedScene") 
							|| entry.getKey().contains("scene_path") || entry.getKey().contains("manual_file_timestamp") 
							|| entry.getKey().contains("select_sport")) {
					}
					else if(entry.getKey().contains("file_name")) {
						file_name = entry.getValue()[0];
					}
					else {
						containers.add(new Container(Integer.valueOf(entry.getKey().split("_")[0]), entry.getKey(), entry.getValue()[0]));
					}		
				}
				switch (session_selected_sports) {
				case "BADMINTON,":
					JAXBContext.newInstance(ContainerData.class).createMarshaller().marshal(new ContainerData(containers), 
							new File(ManualUtil.BADMINTON_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + 
									ManualUtil.DATA_DIRECTORY + file_name + ManualUtil.XML));
					break;
				case "CRICKET,":
					JAXBContext.newInstance(ContainerData.class).createMarshaller().marshal(new ContainerData(containers), 
							new File(ManualUtil.CRICKET_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + 
									ManualUtil.DATA_DIRECTORY + file_name + ManualUtil.XML));
					break;
				}
			}
		return JSONObject.fromObject(null).toString();
	}

	@RequestMapping(value = {"/processManualProcedures"}, method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String processManualProcedures(
			@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess,
			@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess) 
					throws IOException, IllegalAccessException, InvocationTargetException, JAXBException, InterruptedException
	{	
		switch (whatToProcess.toUpperCase()) {
		
		case "LOAD_SCENE": case "LOAD_DATA": case "LOAD_PREVIOUS_SCENE": case "ANIMATE-OUT": case "ANIMATE-IN": case "CLEAR-ALL": case "BADMINTON-OPTIONS":
			switch (session_selected_sports) {
			
			case "BADMINTON,":
				switch(whatToProcess.toUpperCase()) {
				case "LOAD_PREVIOUS_SCENE":
					//Data = valueToProcess.split("_")[1];
					//System.out.println("Scene =" + Scene);
					/*new Scene(ManualUtil.BADMINTON_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + Scene).
					scene_load(print_writer,ManualUtil.BADMINTON_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + Scene);*/
					new Scene(ManualUtil.BADMINTON_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + Scene).
						scene_load(print_writer,ManualUtil.BADMINTON_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + Scene);
					break;
				case "LOAD_SCENE":
					Scene = valueToProcess;
					new Scene(ManualUtil.BADMINTON_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + valueToProcess).
						scene_load(print_writer,ManualUtil.BADMINTON_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + valueToProcess);
					break;
				}
				switch (whatToProcess.toUpperCase()) {
				case "LOAD_PREVIOUS_SCENE":
					
					//print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
					//print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
					//Collections.sort(valueToProcess);
					//System.out.println("2 = "  + which_graphic_on_screen);
					
					session_Data = (ContainerData)JAXBContext.newInstance(ContainerData.class).createUnmarshaller().unmarshal(
							new File(ManualUtil.BADMINTON_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + ManualUtil.DATA_DIRECTORY + valueToProcess));
					Collections.sort(session_Data.getContainers());
					/*for(Container cont : session_Data.getContainers())
						System.out.println("container = " + cont);*/
					
					for(int i = 0; i < session_Data.getContainers().size(); i++) {
						//System.out.println(session_Data.getContainers().get(i).getContainer_key().replaceFirst((i+1)+"_", ""));
						//Collections.sort(session_Data.getContainers().get(i).getContainer_id());
						//System.out.println(session_Data.getContainers().get(i).getContainer_value());
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET " + session_Data.getContainers().get(i).getContainer_key().replaceFirst((i+1)
								+"_", "") + " " + session_Data.getContainers().get(i).getContainer_value() + ";");
						
					}
					/*System.out.println(Scene);
					if(Scene == "Order_Of_Play.sum") {
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*TrumpLoop START;");
					}*/
					//System.out.println(valueToProcess);
					//switch(valueToProcess.replace(".xml", "").toUpperCase()) {
					switch(Scene.replace(".sum", "").toUpperCase()) {
					case "DD_SCHEDULE":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 98.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						//TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "FF_ROW_COLUMM":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 89.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "LT_MATCHID_SCORE_SINGLE":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 106.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "LT_MATCHID_SCORE_DOUBLE":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 75.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
						break;
					case "MATCHID_SINGLES":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 123.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
						break;
					case "MATCHID_DOUBLE":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 82.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
						break;
					case "LT_TIEID":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 75.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
						break;
					case "TIEID":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 98.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
						break;
					case "LT_SIDES":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 149.00;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
						break;
					case "ORDER_OF_PLAY":
						
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*TrumpLoop START;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 109.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
						break;
					case "TEAMS":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 98.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "PLAYER_PROFILE":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 127.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
						break;
					case "SQUAD":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 83.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "POINTS_TABLE":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 115.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "FF_RULES":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 84.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "LT_SUPER":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 79.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "FF_SUPERMATCH":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "FASTRACK":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 83.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					}
					//print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In START;");
					//which_graphic_on_screen = "ANY_SCENE";
					
					return JSONObject.fromObject(session_Data).toString();
					
				case "LOAD_DATA":
					
					print_writer.println("LAYER1*EVEREST*GLOBAL TEMPLATE_SAVE " + ManualUtil.BADMINTON_SPORTS_DIRECTORY + 
							ManualUtil.MANUAL_DIRECTORY + ManualUtil.CONTAINER_FILE + ";");
					TimeUnit.SECONDS.sleep(2);
					/*if(new File(ManualUtil.CRICKET_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + CONTAINER_FILE).exists()) {
						Files.delete(Paths.get(ManualUtil.CRICKET_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + CONTAINER_FILE));
						print_writer.println("LAYER1*EVEREST*GLOBAL TEMPLATE_SAVE " + ManualUtil.CRICKET_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + CONTAINER_FILE + ";");
					}
					else {
						print_writer.println("LAYER1*EVEREST*GLOBAL TEMPLATE_SAVE " + ManualUtil.CRICKET_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + CONTAINER_FILE + ";");
					}*/
					
					boolean exitLoop = false; int numberOfAttempts = 5;
					List<String> allLines = new ArrayList<String>();
					while (exitLoop == false){
						if(new File(ManualUtil.BADMINTON_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + ManualUtil.CONTAINER_FILE).exists()) {
							allLines = Files.readAllLines(Paths.get(ManualUtil.BADMINTON_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + ManualUtil.CONTAINER_FILE));
							break;
						} else {
							TimeUnit.SECONDS.sleep(1);
							numberOfAttempts = numberOfAttempts - 1;
						}
						if(numberOfAttempts <= 0)
						{
							break;
						}
					}
					return JSONArray.fromObject(allLines).toString();
				}
				break;
				
			case "CRICKET,":
				switch(whatToProcess.toUpperCase()) {
				case "LOAD_PREVIOUS_SCENE":  
					new Scene(ManualUtil.CRICKET_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + valueToProcess.replace(".xml", ".sum")).
						scene_load(print_writer,ManualUtil.CRICKET_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + valueToProcess.replace(".xml", ".sum"));
					break;
				case "LOAD_SCENE":
					
					new Scene(ManualUtil.CRICKET_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + valueToProcess).
						scene_load(print_writer,ManualUtil.CRICKET_SCENE_DIRECTORY + ManualUtil.SCENES_DIRECTORY + valueToProcess);
					break;
				}
				switch (whatToProcess.toUpperCase()) {
				case "LOAD_PREVIOUS_SCENE":
					
					print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
					print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
					//Collections.sort(valueToProcess);
					//System.out.println("2 = "  + which_graphic_on_screen);

					session_Data = (ContainerData)JAXBContext.newInstance(ContainerData.class).createUnmarshaller().unmarshal(
							new File(ManualUtil.CRICKET_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + ManualUtil.DATA_DIRECTORY + valueToProcess));
					Collections.sort(session_Data.getContainers());
					/*for(Container cont : session_Data.getContainers())
						System.out.println("container = " + cont);*/
					
					for(int i = 0; i < session_Data.getContainers().size() ; i++) {
						//Collections.sort(session_Data.getContainers().get(i).getContainer_id());
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET " + session_Data.getContainers().get(i).getContainer_key().replaceFirst((i+1)+"_", "") + " " + session_Data.getContainers().get(i).getContainer_value() + ";");
					}
					//System.out.println(valueToProcess);
					switch(valueToProcess.replace(".xml", "").toUpperCase()) {
					case "DD_SCHEDULE":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 98.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						//TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					case "FF_ROW_COLUMM":
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 89.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.bmp;");
						print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
						//TimeUnit.SECONDS.sleep(1);
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
						print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
						break;
					}
					return JSONObject.fromObject(session_Data).toString();
					
				case "LOAD_DATA":
					
					print_writer.println("LAYER1*EVEREST*GLOBAL TEMPLATE_SAVE " + ManualUtil.CRICKET_SPORTS_DIRECTORY + 
							ManualUtil.MANUAL_DIRECTORY + ManualUtil.CONTAINER_FILE + ";");
					TimeUnit.SECONDS.sleep(2);
					boolean exitLoop = false; int numberOfAttempts = 5;
					List<String> allLines = new ArrayList<String>();
					while (exitLoop == false){
						if(new File(ManualUtil.CRICKET_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + ManualUtil.CONTAINER_FILE).exists()) {
							allLines = Files.readAllLines(Paths.get(ManualUtil.CRICKET_SPORTS_DIRECTORY + ManualUtil.MANUAL_DIRECTORY + ManualUtil.CONTAINER_FILE));
							break;
						} else {
							TimeUnit.SECONDS.sleep(1);
							numberOfAttempts = numberOfAttempts - 1;
						}
						if(numberOfAttempts <= 0)
						{
							break;
						}
					}
					return JSONArray.fromObject(allLines).toString();
				}
				break;
			}
			switch (whatToProcess.toUpperCase()) {
			
			case "ANIMATE-OUT":
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In COUNTINUE_REVERSE;");
				return JSONObject.fromObject(null).toString();
			case "ANIMATE-IN":
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In START;");
				return JSONObject.fromObject(null).toString();
			case "CLEAR-ALL":
				print_writer.println("LAYER1*EVEREST*SINGLE_SCENE CLEAR;");
				return JSONObject.fromObject(null).toString();
			}
		
		default:
			return JSONObject.fromObject(null).toString();
		}
	}
	
}