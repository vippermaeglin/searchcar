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

public class HtmlHandlerPSP extends IHtmlHandler{
	
	public  HtmlHandlerPSP(){
		this.name = "PREFEITURA (SP)";
		this.site = "http://www3.prefeitura.sp.gov.br/smt/pesqveic/Pesquisa.aspx";
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
		this.params.put("PageProdamSPOnChange", "");
		this.params.put("PageProdamSPPosicao", "Form=255;0/");
		this.params.put("PageProdamSPFocado", "btnPesquisar");
		this.params.put("__VIEWSTATE", "dDwtMTk0NjI5ODI4MTs7PvLb6Ngvt069kD02oNt99SLLndr5");
		this.params.put("__VIEWSTATEGENERATOR", "052E10DC");
		this.params.put("btnPesquisar", "Pesquisar");
		this.params.put("txtPlaca", car);
		/*URL url;
		try {
			url = new URL(site);
			String htmlString = GenericHttp.connect(url, "POST", requestProperties, params, false);
			//Don't parse empty results:
			if(htmlString.isEmpty() || htmlString.contains("Veículo não se encontra no pátio"))
				return null;
			return parse(htmlString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
	}
}
