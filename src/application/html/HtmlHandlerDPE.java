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
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class HtmlHandlerDPE extends IHtmlHandler{
	
	public  HtmlHandlerDPE(){
		this.name = "DETRAN (PE)";
		this.site = "http://online9.detran.pe.gov.br/ServicosWeb/Veiculo/frmConsultaPlaca.aspx";
		this.requestProperties.put("Content-Type", "application/x-www-form-urlencoded");
	}
	
	@Override
	protected Object[] parse(String htmlString, String car){
		try{
			Document html = Jsoup.parse(htmlString);
			String restricoes = "";
			for(int i=1; i<4; i++){
				if(i!=1)
					restricoes += ", ";
				if(html.getElementById("lblRestricao"+i)!=null){
				String rest = html.getElementById("lblRestricao"+i).text();
					if(rest!=null && !rest.isEmpty())
						restricoes+=rest;
					else{
						//finish
						restricoes = restricoes.substring(0,  restricoes.length()-2);
						break;
					}
				}
				else{
					//finish
					restricoes = restricoes.substring(0,  restricoes.length()-2);
					break;
				}
			}
			String marca = "";
			if(html.getElementById("lblMarcaModelo")!=null)
				marca = html.getElementById("lblMarcaModelo").text();
			String ano = "";
			if(html.getElementById("lblAnoFab")!=null)
				ano = html.getElementById("lblAnoFab").text()+"/"+html.getElementById("lblAnoModelo").text();
			String cor = "";
			if(html.getElementById("lblCor")!=null)
				cor = html.getElementById("lblCor").text();
			String combustivel = "";
			if(html.getElementById("lblCombustivel")!=null)
				combustivel = html.getElementById("lblCombustivel").text();
			String chassi = "";
			if(html.getElementById("lblChassi")!=null)
				chassi = html.getElementById("lblChassi").text();
			String especie = "";
			if(html.getElementById("lblEspecie")!=null)
				especie = html.getElementById("lblEspecie").text();
			String categoria = "";
			if(html.getElementById("lblCategoria")!=null)
				categoria = html.getElementById("lblCategoria").text();
			String info = "RESTRICOES: "+restricoes+"\n";
			info += "MARCA: "+marca+"\n";
			info += "ANO/MODELO: "+ano+"\n";
			info += "COR: "+cor+"\n";
			info += "COMBUSTIVEL: "+combustivel+"\n";
			info += "CHASSI: "+chassi+"\n";
			info += "ESPECIE: "+especie+"\n";
			info += "CATEGORIA: "+categoria;
			return new Object[]{car, info, site};
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
		return new Object[]{car, "Placa encontrada no seguinte site --->", site};
	}
	
	@Override
	public void connect(final String car){
		this.params = new HashMap<String, Object>();
		this.params.put("placa", car);
		try {
			String url = site + "?" + urlWithParams();
			BoundRequestBuilder request = prepareGet(url);
			for(Map.Entry<String, String> entry : requestProperties.entrySet()){
				request.addHeader(entry.getKey(), entry.getValue());
	        }
			request.setRequestTimeout(30000);
			request.execute(new AsyncCompletionHandler<Response>(){
			    
			    @Override
			    public Response onCompleted(Response response) throws Exception{
			    	String htmlString = new String(response.getResponseBodyAsBytes(), "UTF-8");
			    	if(htmlString.isEmpty() || htmlString.contains("Veículo não pertence a PE e sem débitos a pagar!")
							|| htmlString.contains("Ocorreu um problema na sua consulta, tente novamente(2)!"))
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
			if(htmlString.isEmpty() || htmlString.contains("Não foram encontrados registros com os dados informados."))
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
