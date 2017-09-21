package application.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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

public class HtmlHandlerSAL extends IHtmlHandler{
	
	public  HtmlHandlerSAL(){
		this.name = "SALVADOR-BA";
		this.site = "http://www.transalvador.salvador.ba.gov.br/conteudo/index.php/veiculoApreendido/consultaPlaca";
		this.site2 = "http://www.transalvador.salvador.ba.gov.br/conteudo/index.php/veiculoApreendido/consultaPlaca";
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
		this.params.put("placa", car);
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
			    	isOffline = false;
			    	if(response.getStatusCode()==200){
				    	String htmlString = new String(response.getResponseBodyAsBytes(), "UTF-8");
						if(htmlString.isEmpty() || htmlString.contains("ENCONTRA EM PODER DA TRANSALVADOR!"))
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
				        	System.out.println("SAL try again "+car);
			        	connect(car, countDown-1);
			        }
			        else{
				        if(Main.DEBUG)
				        	System.out.println("SAL error "+car);
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
	}
}
