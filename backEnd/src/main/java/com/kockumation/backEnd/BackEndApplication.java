package com.kockumation.backEnd;

import com.kockumation.backEnd.ValvesMaster.DetectAndSaveValvesAlarms;
import com.kockumation.backEnd.ValvesMaster.ValvesMasterManager;
import com.kockumation.backEnd.levelMaster.LevelMasterManager;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class BackEndApplication {

	public static void main(String[] args) throws IOException, URISyntaxException, DeploymentException {
		SpringApplication.run(BackEndApplication.class, args);
		System.out.println("listening on port "+ 8081);


	boolean checkDataBase = true;
     while (checkDataBase){

		 try (Connection conn = MySQLJDBCUtil.getConnection()) {
          checkDataBase = false;
			 System.out.println(String.format("Connected to database %s "
					 + "successfully.", conn.getCatalog()));
			 LevelMasterManager levelMasterManager = new LevelMasterManager();
			 ValvesMasterManager valvesMasterManager = new ValvesMasterManager();



			 valvesMasterManager.start();
			 //levelMasterManager.start();

		 } catch (SQLException ex) {
			 //System.out.println(ex.getMessage());
			 switch(ex.getErrorCode()) {
				 case 1049:
					 System.out.println("Please install database ship_master_java.");
					 break;
				 case 1045:
					 System.out.println("Please change database user to root and password to tyfon");
					 break;
			 }
			 try {
				 Thread.sleep(3000);
				 checkDataBase = true;
			 } catch (InterruptedException e) {
				 e.printStackTrace();
			 }
		 }

	 }





	/*JSONObject setTankSubscriptionOn = new JSONObject();
		JSONObject tankId = new JSONObject();
		tankId.put( "tankId",0);
		setTankSubscriptionOn.put("setTankSubscriptionOn", tankId);
		String setTankSubscriptionOnStr = setTankSubscriptionOn.toString();

	//	allTanksDataFromKsl.sendMessage(setTankSubscriptionOnStr);*/



	}



}
