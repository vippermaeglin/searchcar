package application.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import application.Main;
import application.api.GenericHttp;
import application.controller.MainController;
import application.handlers.IHtmlHandler;
import application.util.Statics;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class HtmlHandlerTRP extends IHtmlHandler{

	private String sessionId;
	
	public  HtmlHandlerTRP(){
		this.name = "RIB. PRETO-SP";
		this.site = "http://www.coderp.com.br/JW07/veiculoPatio.xhtml";
		this.site2 = "http://www.coderp.com.br/JW07/veiculoPatio.xhtml";
		this.sessionId = "";
		this.requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
	}
	
	@Override
	protected Object[] parse(String htmlString, String car){
		
		//Parse failed but results may be found
		//return just the car and site
		return new Object[]{car, htmlString/*"Placa encontrada no site "+name+" --->"*/, site2};
	}
	
	public void getIdThenConnect(List<String> cars, final int countDown){
		//First check JSESSIONID and perform GET:
		if(sessionId.isEmpty()){
			String url = site;
			BoundRequestBuilder request = prepareGet(url);
			for(Map.Entry<String, String> entry : requestProperties.entrySet()){
				request.addHeader(entry.getKey(), entry.getValue());
	        }
			request.setRequestTimeout(Statics.Connection.TIMEOUT);
			request.execute(new AsyncCompletionHandler<Response>(){
			    
			    @Override
			    public Response onCompleted(Response response) throws Exception{
					//TODO: site is not working, always return the same initial page
			    	List<String>values = response.getHeaders("Set-Cookie");
			    	for(String v : values){
		        		if(v.contains("JSESSIONID")){
		        			String[] split = v.split("JSESSIONID=");
		        			for(int i=0; split[1].charAt(i)!=';'; i++)
		        				sessionId+=split[1].charAt(i);
		        			if(Main.DEBUG)
		        				System.out.println("JSESSIONID: "+sessionId);
		        			for(String c:cars){
		        				connect(c, Statics.Connection.ATTEMPTS);
		        			}
		        		}
		        	}
			        return response;
			    }
			    
			    @Override
			    public void onThrowable(Throwable t){
			        if(countDown>1){
				        if(Main.DEBUG)
				        	System.out.println("TRP try again GET!");
				        getIdThenConnect(cars, countDown-1);
			        }
			        else{
				        if(Main.DEBUG)
				        	System.out.println("TRP error GET!");
			        }
			    }
			});
		}
		else{
			for(String c:cars){
				connect(c, Statics.Connection.ATTEMPTS);
			}
		}
	}
	
	@Override
	public void connect(final String car, final int countDown){
		this.params = new HashMap<String, Object>();
		this.params.put("javax.faces.partial.ajax", "true");
		this.params.put("javax.faces.source", "j_idt64:j_idt91");
		this.params.put("javax.faces.partial.execute", "@all");
		this.params.put("javax.faces.partial.render", "j_idt64");
		this.params.put("j_idt64:j_idt91", "j_idt64:j_idt91");
		this.params.put("j_idt64", "j_idt64");
		this.params.put("j_idt64:j_idt87", car);
		this.params.put("javax.faces.ViewState", "-6016496097231714323:1125612151544006923");
		try {
    		//Perform POST method with JSESSIONID:
			try {
				requestProperties.put("Cookie", "JSESSIONID="+sessionId+"; SERVERID=gfprod03j8");
				String url = site;// GET = + "?" + urlWithParams();
				BoundRequestBuilder request = preparePost(url);
				for(Map.Entry<String, String> entry : requestProperties.entrySet()){
					request.addHeader(entry.getKey(), entry.getValue());
		        }
				byte[] postDataBytes = urlWithParams().toString().getBytes("UTF-8");
				request.setBody(postDataBytes);
				request.setRequestTimeout(Statics.Connection.TIMEOUT);
				request.execute(new AsyncCompletionHandler<Response>(){
				    
				    @Override
				    public Response onCompleted(Response response) throws Exception{
				    	if(response.getStatusCode()==200){
    				    	String htmlString = new String(response.getResponseBodyAsBytes(), "UTF-8");
    						if(htmlString.isEmpty() || htmlString.contains("Nenhum registro encontrado."))
    							MainController.getInstance().receiveData(new Object[]{car, "", ""});
    						else
    							MainController.getInstance().receiveData(parse(htmlString, car));
				    	}else{
				    		connect(car, countDown-1);
				    	}
				        return response;
				    }
				    
				    @Override
				    public void onThrowable(Throwable t){
				        if(countDown>1){
					        if(Main.DEBUG)
					        	System.out.println("TRP try again "+car);
				        	connect(car, countDown-1);
				        }
				        else{
					        if(Main.DEBUG)
					        	System.out.println("TRP error "+car);
					        MainController.getInstance().receiveData(new Object[]{car, "", ""});
				        }
				    }
				});
			} catch (Exception e) {
				if(Main.DEBUG)
					e.printStackTrace();
				//Occurried some error before request:
		        MainController.getInstance().receiveData(new Object[]{car, "", ""});
			}
		} catch (Exception e) {
			if(Main.DEBUG)
				e.printStackTrace();
			//Occurried some error before request:
	        MainController.getInstance().receiveData(new Object[]{car, "", ""});
		}
	}
}
