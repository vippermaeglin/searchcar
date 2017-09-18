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

public class HtmlHandlerTRP extends IHtmlHandler{
	
	public  HtmlHandlerTRP(){
		this.name = "RIBEIRÃO PRETO (SP)";
		this.site = "http://www.coderp.com.br/JW07/veiculoPatio.xhtml";
		this.requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
	}
	
	@Override
	protected Object[] parse(String htmlString, String car){
		try{
			Document html = Jsoup.parse(htmlString);
			//get first table:
			Element table = html.select("table").get(0); 
			Elements rows = table.select("tr");
			//get data on second row:
			Elements data = rows.get(1).select("td");
			String nameOwner = data.get(0).text();
			String name = "";
			String owner = "";
			int index = nameOwner.indexOf("/");
			if(index != -1){
				name = nameOwner.substring(0, index);
				owner = nameOwner.substring(index+1, nameOwner.length());
			}
			else{
				name = nameOwner;
				owner = "";
			}
			String date = data.get(1).text();
			String local = data.get(2).text();
			String address = data.get(3).text();
			String value = data.get(4).text();
			if(Main.DEBUG)
	    		System.out.println("DETRAN PARSE:");
			if(Main.DEBUG)
	    		System.out.println("Nome: "+name+" Proprietário: "+owner+" Data: "+date+" Pátio: "+local+" Endereço: "+address+" Valor: "+value);
			//"PLACA", "EMPRESA", "UF", "NOME_PROPRIETARIO", "DATA_APREENSAO", "NOME_PATIO", "END_PATIO", "VALOR_ESTADIA", "SITE"
			return new Object[]{car, owner, "MG", name, date, local, address, value, site};
		}
		catch(Exception ex){
			if(Main.DEBUG)
	    		ex.printStackTrace();
			Platform.runLater(new Runnable() {
				public void run() {
		    		//Alert alert = new Alert(AlertType.NONE, "Não foi encontrado nenhum registro para a placa \""+car+"\"" , ButtonType.OK);
		    		//alert.showAndWait();
				}
			});
		}
		//Parse failed but results may be found
		//return just the car and site
		return new Object[]{car, "-", "-", "-", "-", "-", "-", "-", site};
	}
	
	@Override
	public void connect(final String car){
		this.params = new HashMap<String, Object>();
		this.params.put("javax.faces.partial.ajax", "true");
		this.params.put("javax.faces.source", "j_idt64:j_idt91");
		this.params.put("javax.faces.partial.execute", "@all");
		this.params.put("javax.faces.partial.render", "j_idt64");
		this.params.put("j_idt64:j_idt91", "j_idt64:j_idt91");
		this.params.put("j_idt64", "j_idt64");
		this.params.put("j_idt64:j_idt87", car);
		this.params.put("javax.faces.ViewState", "-6016496097231714323:1125612151544006923");
		/*URL url;
		try {
			url = new URL(site);
			//First execute a GET to get JSESSIONID:
			String sessionId = GenericHttp.connect(url, "GET", new HashMap<String, String>(), new HashMap<String, Object>(), true);
			this.requestProperties.put("Cookie", "JSESSIONID="+sessionId+"; SERVERID=gfprod03j8");
			
			String htmlString = GenericHttp.connect(url, "POST", requestProperties, params, false);
			//Don't parse empty results:
			if(htmlString.isEmpty() || htmlString.contains("Nenhum registro encontrado."))
				return null;
			return parse(htmlString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
	}
}
