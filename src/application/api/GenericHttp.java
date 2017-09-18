package application.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import application.Main;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public final class GenericHttp {
		public static String connect(URL url, String method, Map<String,String> requestProperties, Map<String,Object> params, boolean getSessionId){
			StringBuilder result = new StringBuilder(1024);
			try{
		        StringBuilder postData = new StringBuilder();
		        for (Map.Entry<String,Object> param : params.entrySet()) {
		            if (postData.length() != 0) postData.append('&');
		            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
		            postData.append('=');
		            if(!param.getValue().equals("Pesquisar%A0%A0%3E%3E"))
		            	postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		            else
		            	postData.append(param.getValue());
		        }
		        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
	
		        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		        conn.setReadTimeout(7000);
		        for(Map.Entry<String, String> entry : requestProperties.entrySet()){
		        	conn.setRequestProperty(entry.getKey(), entry.getValue());
		        }
		        conn.setRequestMethod(method);
		        if(method.equals("POST")){
			        conn.setDoOutput(true);
			        conn.getOutputStream().write(postDataBytes);
		        }
	
		        if(getSessionId){
			        for(Entry<String, List<String>>e:conn.getHeaderFields().entrySet()){
			        	if(e.getKey()!=null && e.getKey().toString().equals("Set-Cookie")){
			        		List<String>values = e.getValue();
				        	for(String v : values){
				        		if(v.contains("JSESSIONID")){
				        			String[] split = v.split("JSESSIONID=");
				        			String id = "";
				        			for(int i=0; split[1].charAt(i)!=';'; i++)
				        				id+=split[1].charAt(i);
				        			System.out.println("JSESSIONID: "+id);
				        			return id;
				        		}
				        	}
			        	}
			        }
		        }
		        else{
			        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			        String s;
			        while((s=in.readLine())!=null)
			        {
			        	result.append(s);
			        	if(Main.DEBUG)
			        		System.out.println(s);
			        } 
		        }
			}
			catch(Exception ex){
	        	if(Main.DEBUG)
	        		ex.printStackTrace();
	        	return "";
	        	/*Platform.runLater(new Runnable() {
					public void run() {
		    		Alert alert = new Alert(AlertType.NONE, "Erro ao fazer requisição web.", ButtonType.OK);
		    		alert.showAndWait();
					}
	        	});*/
			}
			return result.toString();
		}
}
