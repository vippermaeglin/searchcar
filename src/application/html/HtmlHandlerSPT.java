package application.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

public class HtmlHandlerSPT extends IHtmlHandler{
	
	public  HtmlHandlerSPT(){
		this.name = "TRANS-SP";
		this.site = "http://200.99.150.163/CvaWeb/hcvainiciolov.aspx";
		this.site2 = "http://www.sptrans.com.br/veiculos_apreendidos/";
		this.requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
	}
	
	@Override
	protected Object[] parse(String htmlString, String car){
		//TODO:
		return new Object[]{car, htmlString/*"Placa encontrada no site "+name+" --->"*/, site2};
	}
	
	@Override
	public void connect(final String car, final int countDown){
		this.params = new HashMap<String, Object>();
		this.params.put("CVAVEIPLACA", car);
		try {
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
					//TODO: site is not working, always return the same initial page
			    	//isOffline = false;
			    	if(response.getStatusCode()==200){
				    	String htmlString = new String(response.getResponseBodyAsBytes(), "UTF-8");
						if(true/*htmlString.isEmpty() || htmlString.contains("Veículo não se encontra no pátio")*/)
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
				        	System.out.println("SPT try again "+car);
			        	connect(car, countDown-1);
			        }
			        else{
				        if(Main.DEBUG)
				        	System.out.println("SPT error "+car);
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
		/*URL url;
		try {
			url = new URL(site);
			String htmlString = GenericHttp.connect(url, "POST", requestProperties, params, false);
			//Don't parse empty results:
			//TODO: site is not working, I dont know if that message is correct
			if(htmlString.isEmpty() || htmlString.contains("Veículo não se encontra no pátio"))
				return null;
			return parse(htmlString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
	}
}
