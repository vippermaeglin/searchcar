package application.handlers;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import application.Main;

public abstract class IHtmlHandler extends DefaultAsyncHttpClient{
	public String name = "";
	public String site = "";
	public String site2 = "";
	public boolean isOffline = true;
	public Map<String,String> requestProperties = new LinkedHashMap<String,String>();
	public Map<String,Object> params = new LinkedHashMap<String,Object>();

	public IHtmlHandler(){
		super();
	}
	
	protected abstract Object[] parse(String htmlString, String car);
	
	public abstract void connect(final String c, final int countDown);
	
	protected String urlWithParams(){
		try{
			StringBuilder postData = new StringBuilder();
	        for (Map.Entry<String,Object> param : params.entrySet()) {
	            if (postData.length() != 0) postData.append('&');
	            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
	            postData.append('=');
	            if(!param.getValue().equals("Pesquisar%A0%A0%3E%3E"))
	            	postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
	            else
	            	postData.append(param.getValue());
	        }
	        return postData.toString();
		}
		catch(Exception ex){
			if(Main.DEBUG)
				ex.printStackTrace();
		}
		return "";
	}

	public static String getNameBySite(String site){
		switch(site){
		case "https://www.detran.mg.gov.br/veiculos/veiculos-apreendidos/lista-de-veiculos-apreendidos/-/lista_veiculos_apreendidos_furtados":
		case "https://www.detran.mg.gov.br/veiculos/veiculos-apreendidos/lista-de-veiculos-apreendidos":
			return "DETRAN-MG";
		case "http://wsdetran.pb.gov.br/VeiculosApreendidos/CONSULTAR_VEICULO_APREENDIDO/consultar":
		case "http://www.detran.pb.gov.br/index.php/consultar-veiculo-apreendido.html":
			return "DETRAN-PB";
		case "http://online9.detran.pe.gov.br/ServicosWeb/Veiculo/frmConsultaPlaca.aspx":
		case "http://www.detran.pe.gov.br/index.php?option=com_content&amp;view=article&amp;id=396&amp;Itemid=15":
			return "DETRAN-PE";
		case "http://www3.prefeitura.sp.gov.br/smt/pesqveic/Pesquisa.aspx":
			return "PREFEITURA-SP";
		case "http://www.transalvador.salvador.ba.gov.br/conteudo/index.php/veiculoApreendido/consultaPlaca":
			return "SALVADOR-BA";
		case "http://www.vtx2.com.br/Patio/":
			return "SANTOS-SP";
		case "http://200.99.150.163/CvaWeb/hcvainiciolov.aspx":
		case "http://www.sptrans.com.br/veiculos_apreendidos/":
			return "TRANS-SP";
		case "http://www.coderp.com.br/JW07/veiculoPatio.xhtml":
			return "RIB. PRETO-SP";
		default:
			return "";
		
		}
	}
}
