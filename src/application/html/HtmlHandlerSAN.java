package application.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import application.Main;
import application.api.GenericHttp;
import application.handlers.IHtmlHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class HtmlHandlerSAN extends IHtmlHandler{
	
	public  HtmlHandlerSAN(){
		this.name = "CET-SANTOS (SP)";
		this.site = "http://www.vtx2.com.br/Patio/";
		this.requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
	}
	
	@Override
	protected Object[] parse(String htmlString, String car){
		//TODO:
		return new Object[]{car, "-", "-", "-", "-", "-", "-", "-", site};
	}
	
	@Override
	public void connect(final String car){
		this.params = new HashMap<String, Object>();
		this.params.put("p", "userPesq");
		this.params.put("placa", car);
		this.params.put("submit", "Pesquisar%A0%A0%3E%3E");
		/*URL url;
		try {
			url = new URL(site);
			String htmlString = GenericHttp.connect(url, "POST", requestProperties, params, false);
			//Don't parse empty results:
			if(htmlString.isEmpty() || htmlString.contains("Ve&iacute;culo n&atilde;o localizado")
					|| htmlString.contains("N&atilde;o se encontra no p&aacute;tio"))
				return null;
			return parse(htmlString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
	}
}
