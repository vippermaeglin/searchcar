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

public class HtmlHandlerDPB extends IHtmlHandler{
	
	public  HtmlHandlerDPB(){
		this.name = "DETRAN (PB)";
		this.site = "http://wsdetran.pb.gov.br/VeiculosApreendidos/CONSULTAR_VEICULO_APREENDIDO/consultar";
		this.requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
	}
	
	@Override
	protected Object[] parse(String htmlString, String car){
		//TODO: this one returns in JSON!
		return new Object[]{car, "-", "-", "-", "-", "-", "-", "-", site};
	}
	
	@Override
	public void connect(final String car){
		this.params = new HashMap<String, Object>();
		this.params.put("placa", car);
		/*URL url;
		try {
			//GET method just accept params in url!
			url = new URL(site+"?placa="+car);
			String htmlString = GenericHttp.connect(url, "GET", requestProperties, params, false);
			//Don't parse empty results:
			if(htmlString.isEmpty() || htmlString.contains("Ve�culo com a placa informada n�o est� com apreens�o cadastrada."))
				return null;
			return parse(htmlString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
	}
}
