package application.controller;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import application.api.GenericHttp;
import application.handlers.ExcelHandler;
import application.handlers.IHtmlHandler;
import application.handlers.SelectFileHandler;
import application.html.HtmlHandlerDMG;
import application.html.HtmlHandlerDPB;
import application.html.HtmlHandlerDPE;
import application.html.HtmlHandlerPSP;
import application.html.HtmlHandlerSAL;
import application.html.HtmlHandlerSAN;
import application.html.HtmlHandlerSPT;
import application.html.HtmlHandlerTRP;
import application.util.Statics;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainController {

	private static MainController instance = null;
	private Main main;
	private List<Object[]> dataReceived = new ArrayList<Object[]>();
	private int countReceived = 0;
	private List<String> carsFound = new ArrayList<String>();

	@FXML
	private ImageView imgTitle;
	@FXML
	private TextField txtInput;
	@FXML
	private TextField txtOutput;
	@FXML
	private Button btnInput;
	@FXML
	private Button btnOutput;
	@FXML
	private Button btnSearch;
	@FXML
	private Hyperlink linkEngComp;
	
	
	public MainController() {
		instance = this;
	}

    public static MainController getInstance() {
        return instance;
    }
	
	/**
	 * Default initialization
	 */
	@FXML
    private void initialize() { 
		try{
			imgTitle = new ImageView();   
			try{
				imgTitle.setImage(new Image(Main.class.getResourceAsStream("search_title.png")));
			}catch(Exception e){
				if(Main.DEBUG)
	        		e.printStackTrace();
			}
			txtInput.setText(Statics.Path.APP_INPUT+"\\"+Statics.File.INPUT);
			txtInput.setEditable(false);
			txtOutput.setText(Statics.Path.APP_OUTPUT+"\\"+Statics.File.OUTPUT);
			txtOutput.setEditable(false);
			btnInput.setOnMousePressed(new SelectFileHandler(this,Statics.Path.APP_INPUT, true));
			btnOutput.setOnMousePressed(new SelectFileHandler(this,Statics.Path.APP_OUTPUT, false));
			btnSearch.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					btnSearch.setDisable(true);
					new Thread() {
						@Override
						public void run() {
							search();
						}
					}.start();
				}
			});
			linkEngComp.setFocusTraversable(false);
			linkEngComp.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					try{
						Desktop.getDesktop().browse(new URI("https://www.facebook.com/EngComp-Soluções-Tecnológicas-251215981587192"));
					}catch(Exception ex){
						if(Main.DEBUG)
			        		ex.printStackTrace();
					}
				}
			});
			Platform.runLater(new Runnable() {
				public void run() {
					btnSearch.requestFocus();
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
    		Alert alert = new Alert(AlertType.NONE, "Erro ao inicializar[1]: "+ex.getMessage() , ButtonType.OK);
    		alert.showAndWait();
		}
	}
	
	/**
	 * @return the main
	 */
	public Main getMain() {
		return main;
	}

	/**
	 * @param main the main to set
	 */
	public void setMain(Main main) {
		this.main = main;
	}
	
	/**
	 * @param fName
	 */
	public void setFile(String fName, boolean isInput){
		if(isInput)
			txtInput.setText(fName);
		else
			txtOutput.setText(fName);
	}
	
	public void search(){
		//read data from input excel:
		List<String> cars = ExcelHandler.read(txtInput.getText());
		if(cars.size()==0){
			Platform.runLater(new Runnable() {
				public void run() {
					Alert alert = new Alert(AlertType.NONE, "Nenhuma placa encontrada." , ButtonType.OK);
					alert.showAndWait();
				}
			});
		}
		//TODO: search for every car on all sites:
		dataReceived = new ArrayList<Object[]>();
		carsFound = new ArrayList<String>();
		//data.add(new Object[]{"PLACA", "EMPRESA", "UF", "NOME_PROPRIETARIO", "DATA_APREENSAO", "NOME_PATIO", "END_PATIO", "VALOR_ESTADIA", "SITE"});
		dataReceived.add(new Object[]{"PLACA", "INFORMAÇÕES", "SITE"});

		//Set the Receiver Count-Down:
		countReceived = 0;

		//DMG
        IHtmlHandler handler = new HtmlHandlerDMG();
        countReceived += cars.size();
		for(String c:cars){
			if(c.length()<3 || c.length()>9 || alreadyFound(c)){
				countReceived--;
				continue;	  
			}
	        handler.connect(c);	        
		}
		//DPE
        handler = new HtmlHandlerDPE();
        countReceived += cars.size();
		for(String c:cars){
			if(c.length()<3 || c.length()>9 || alreadyFound(c)){
				countReceived--;
				continue;	        
			}
	        handler.connect(c);	        
		}
		
	}

	public synchronized boolean alreadyFound(String c){
		if(carsFound.contains(c)){
			return true;
		}
		return false;
	}
	
	public synchronized void receiveData(Object[] object){
		countReceived--;
		int indexExists = -1;
		for (int i=0; i<dataReceived.size(); i++) {
	        if(((String)dataReceived.get(i)[0]).equals((String)object[0])) {
	        	indexExists = i;
	        }
	    }
		if(indexExists==-1)
			dataReceived.add(object);
		else if(((String) dataReceived.get(indexExists)[1]).length()<((String) object[1]).length()){
			//Update if theres more info:
			dataReceived.set(indexExists, object);
		}
			
		//remove from search cars already found:
		if(!((String)object[1]).isEmpty())
			carsFound.add((String)object[0]);
		//TODO: general timeout if zero is never achieved?
		if(countReceived<=0){
			countReceived = 1000;
			//write data to output excel:
			ExcelHandler.write(txtOutput.getText(), dataReceived);
			Platform.runLater(new Runnable() {
				public void run() {
					Alert alert = new Alert(AlertType.NONE, "Pesquisa concluída!" , ButtonType.OK);
					alert.showAndWait();
					btnSearch.setDisable(false);
				}
			});
			try{
				Desktop.getDesktop().open(new File(txtOutput.getText()));
			}catch(Exception ex){
				if(Main.DEBUG)
	        		ex.printStackTrace();
			}
			
		}
	}
}
