package application.html;

import java.net.MalformedURLException;
import java.net.URL;
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

public class HtmlHandlerSPT extends IHtmlHandler{
	
	public  HtmlHandlerSPT(){
		this.name = "SP-TRANS (SP)";
		this.site = "http://200.99.150.163/CvaWeb/hcvainiciolov.aspx";
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
		this.params.put("CVAVEIPLACA", car);
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
