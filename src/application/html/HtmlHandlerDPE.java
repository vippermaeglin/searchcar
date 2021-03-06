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

public class HtmlHandlerDPE extends IHtmlHandler{
	
	public  HtmlHandlerDPE(){
		this.name = "DETRAN-PE";
		this.site = "http://online9.detran.pe.gov.br/ServicosWeb/Veiculo/frmConsultaPlaca.aspx";
		this.site2 = "http://www.detran.pe.gov.br/index.php?option=com_content&amp;view=article&amp;id=396&amp;Itemid=15";
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
						if(restricoes.contains(", "))
							restricoes = restricoes.substring(0,  restricoes.length()-2);
						break;
					}
				}
				else{
					//finish
					if(restricoes.contains(", "))
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
			return new Object[]{car, info, site2};
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
		return new Object[]{car, htmlString/*"Placa encontrada no site "+name+" --->"*/, site2};
	}
	
	@Override
	public void connect(final String car, final int countDown){
		this.params = new HashMap<String, Object>();
		this.params.put("placa", car);
		try {
			String url = site + "?" + urlWithParams();
			BoundRequestBuilder request = prepareGet(url);
			for(Map.Entry<String, String> entry : requestProperties.entrySet()){
				request.addHeader(entry.getKey(), entry.getValue());
	        }
			request.setRequestTimeout(Statics.Connection.TIMEOUT);
			request.execute(new AsyncCompletionHandler<Response>(){
			    
			    @Override
			    public Response onCompleted(Response response) throws Exception{
			    	isOffline = false;
			    	if(response.getStatusCode()==200){
				    	String htmlString = new String(response.getResponseBodyAsBytes(), "UTF-8");
				    	if(htmlString.isEmpty() || htmlString.contains("Ve�culo n�o pertence a PE e sem d�bitos a pagar!")
								|| htmlString.contains("Ocorreu um problema na sua consulta, tente novamente(2)!"))
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
				        	System.out.println("DPE try again "+car);
			        	connect(car, countDown-1);
			        }
			        else{
				        if(Main.DEBUG)
				        	System.out.println("DPE error "+car);
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
