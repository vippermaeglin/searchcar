package application.html;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class HtmlHandlerDMG extends IHtmlHandler{
	
	public  HtmlHandlerDMG(){
		super();
		this.name = "DETRAN (MG)";
		this.site = "https://www.detran.mg.gov.br/veiculos/veiculos-apreendidos/lista-de-veiculos-apreendidos/-/lista_veiculos_apreendidos_furtados";
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
        		System.out.println("Nome: "+name+" Propriet�rio: "+owner+" Data: "+date+" P�tio: "+local+" Endere�o: "+address+" Valor: "+value);
			//"PLACA", "EMPRESA", "UF", "NOME_PROPRIETARIO", "DATA_APREENSAO", "NOME_PATIO", "END_PATIO", "VALOR_ESTADIA", "SITE"
			//return new Object[]{car, owner, "MG", name, date, local, address, value, site};
			String info = "EMPRESA: "+owner+"\n";
			info += "NOME_PROPRIETARIO: "+name+"\n";
			info += "DATA_APREENSAO: "+date+"\n";
			info += "NOME_PATIO: "+local+"\n";
			info += "END_PATIO: "+address+"\n";
			info += "VALOR_ESTADIA: R$"+value;
			return new Object[]{car, info, site};
		}
		catch(Exception ex){
			if(Main.DEBUG)
        		ex.printStackTrace();
			Platform.runLater(new Runnable() {
				public void run() {
		    		//Alert alert = new Alert(AlertType.NONE, "N�o foi encontrado nenhum registro para a placa \""+car+"\"" , ButtonType.OK);
		    		//alert.showAndWait();
				}
			});
		}
		//Parse failed but results may be found
		//return just the car and site
		return new Object[]{car, "Placa encontrada no seguinte site --->", site};
	}
	
	@Override
	public void connect(final String car){
		this.params = new HashMap<String, Object>();
		this.params.put("data[ListarVeiculosApreendidosMarcadosComoFurtadoRoubado][municipio]", "000");
		this.params.put("data[ListarVeiculosApreendidosMarcadosComoFurtadoRoubado][placa]", car);
		try {
			String url = site;// GET = + "?" + urlWithParams();
			BoundRequestBuilder request = preparePost(url);
			for(Map.Entry<String, String> entry : requestProperties.entrySet()){
				request.addHeader(entry.getKey(), entry.getValue());
	        }
			byte[] postDataBytes = urlWithParams().toString().getBytes("UTF-8");
			request.setBody(postDataBytes);
			request.setRequestTimeout(30000);
			request.execute(new AsyncCompletionHandler<Response>(){
			    
			    @Override
			    public Response onCompleted(Response response) throws Exception{
			    	String htmlString = new String(response.getResponseBodyAsBytes(), "UTF-8");
					if(htmlString.isEmpty() || htmlString.contains("N�o foram encontrados registros com os dados informados."))
						MainController.getInstance().receiveData(new Object[]{car, "", ""});
					else
						MainController.getInstance().receiveData(parse(htmlString, car));
			        return response;
			    }
			    
			    @Override
			    public void onThrowable(Throwable t){
			        if(Main.DEBUG)
			        	System.out.print("Error connecting for "+car);
			        MainController.getInstance().receiveData(new Object[]{car, "", ""});
			    }
			});
			
			/*String htmlString = GenericHttp.connect(url, "POST", requestProperties, params, false);
			//Don't parse empty results:
			if(htmlString.isEmpty() || htmlString.contains("N�o foram encontrados registros com os dados informados."))
				return null;
			return parse(htmlString);*/
		} catch (Exception e) {
			if(Main.DEBUG)
				e.printStackTrace();
			//Occurried some error before request:
	        MainController.getInstance().receiveData(new Object[]{car, "", ""});
		}
	}
}
