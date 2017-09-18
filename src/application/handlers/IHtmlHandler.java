package application.handlers;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.asynchttpclient.DefaultAsyncHttpClient;

import application.Main;

public abstract class IHtmlHandler extends DefaultAsyncHttpClient{
	public String name = "";
	public String site = "";
	public Map<String,String> requestProperties = new LinkedHashMap<String,String>();
	public Map<String,Object> params = new LinkedHashMap<String,Object>();

	public IHtmlHandler(){
		super();
	}
	
	protected abstract Object[] parse(String htmlString, String car);
	
	public abstract void connect(final String c);
	
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
}
