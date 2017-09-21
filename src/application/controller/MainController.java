package application.controller;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
import javafx.scene.control.CheckBox;
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
	private List<String> carsFound = new ArrayList<String>();
	private Timer timer;
	private int timerDown;
	private String sitesDown;
	private IHtmlHandler h1, h2, h3, h4, h5, h6, h7, h8;
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
	@FXML
	private CheckBox cbDMG;
	@FXML
	private CheckBox cbPSP;
	@FXML
	private CheckBox cbSPT;
	@FXML
	private CheckBox cbDPE;
	@FXML
	private CheckBox cbTRP;
	@FXML
	private CheckBox cbSAN;
	@FXML
	private CheckBox cbSAL;
	@FXML
	private CheckBox cbDPB;
	@FXML
	private TextArea txtOut;
	
	
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
			txtInput.setFocusTraversable(false);
			txtOutput.setText(Statics.Path.APP_OUTPUT+"\\"+Statics.File.OUTPUT);
			txtOutput.setEditable(false);
			txtOutput.setFocusTraversable(false);
			btnInput.setOnMousePressed(new SelectFileHandler(this,Statics.Path.APP_INPUT, true));
			btnOutput.setOnMousePressed(new SelectFileHandler(this,Statics.Path.APP_OUTPUT, false));
			btnSearch.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					if(!cbDMG.isSelected()&&!cbDPB.isSelected()&&!cbDPE.isSelected()&&!cbPSP.isSelected()&&
							!cbSAL.isSelected()&&!cbSAN.isSelected()&&!cbSPT.isSelected()&&!cbTRP.isSelected()){
						Platform.runLater(new Runnable() {
							public void run() {
								Alert alert = new Alert(AlertType.NONE, "Selecione pelo menos um site." , ButtonType.OK);
								alert.showAndWait();
							}
						});
						return;
					}
					btnSearch.setDisable(true);
					new Thread() {
						@Override
						public void run() {
							search();
						}
					}.start();
				}
			});
			txtOut.setEditable(false);
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
		try{
			//read data from input excel:
			List<String> cars = ExcelHandler.read(txtInput.getText());
			if(cars.size()==0){
				Platform.runLater(new Runnable() {
					public void run() {
						Alert alert = new Alert(AlertType.NONE, "Nenhuma placa encontrada." , ButtonType.OK);
						alert.showAndWait();
						btnSearch.setDisable(false);
					}
				});
				return;
			}
			if(cars.size()>500){
				Platform.runLater(new Runnable() {
					public void run() {
						Alert alert = new Alert(AlertType.NONE, "Devido aos limites de conexões nos sites não é recomendado consultar arquivos com mais de 500 placas.\nO desempenho será otimizado nesta consulta mas ainda estará sujeito a restrições de acesso impostas pelos sites." , ButtonType.OK);
						alert.showAndWait();
					}
				});
			}
			//TODO: search for every car on all sites:
			dataReceived = new ArrayList<Object[]>();
			carsFound = new ArrayList<String>();
			//data.add(new Object[]{"PLACA", "EMPRESA", "UF", "NOME_PROPRIETARIO", "DATA_APREENSAO", "NOME_PATIO", "END_PATIO", "VALOR_ESTADIA", "SITE"});
			dataReceived.add(new Object[]{"PLACA", "INFORMAÇÕES", "SITE"});
			
			if(Main.DEBUG)
				System.out.println("CARREGADO "+cars.size()+" PLACAS");
			
			printOut("CARREGADO "+cars.size()+" PLACAS", true);
				
			sitesDown = "";
			
			//Set the Receiver timer:
			int timerPause = 0;
			timerDown = 0;
			timer = new Timer();
			  TimerTask task = new TimerTask() {
			      @Override
			   public void run() {
			    	  timerDown++;
			    	  if(timerDown>Statics.Connection.REPEAT_WD){
			    		  	timer.cancel();
					  		if(h1.isOffline && cbDMG.isSelected())
					  			setSiteDown(h1.name);
					  		if(h2.isOffline && cbDPB.isSelected())
					  			setSiteDown(h2.name);
					  		if(h3.isOffline && cbDPE.isSelected())
					  			setSiteDown(h3.name);
					  		if(h4.isOffline && cbPSP.isSelected())
					  			setSiteDown(h4.name);
					  		if(h5.isOffline && cbSAL.isSelected())
					  			setSiteDown(h5.name);
					  		if(h6.isOffline && cbSAN.isSelected())
					  			setSiteDown(h6.name);
					  		if(h7.isOffline && cbSPT.isSelected())
					  			setSiteDown(h7.name);
					  		if(h8.isOffline && cbTRP.isSelected())
					  			setSiteDown(h8.name);
					  		h1.close();
					  		h2.close();
					  		h3.close();
					  		h4.close();
					  		h5.close();
					  		h6.close();
					  		h7.close();
					  		h8.close();
					  		printOut("\n---------------------------------------------\nBUSCA FINALIZADA\nEncontrado "+carsFound.size()+" de "+dataReceived.size(), false);
				    	  	printOut(sitesDown, false);
					  		//write data to output excel:
							ExcelHandler.write(txtOutput.getText(), dataReceived);
							Platform.runLater(new Runnable() {
								public void run() {
									String msg = "Pesquisa concluída!";
									if(carsFound.size()==0)
										msg+="\nAlguns sites bloqueiam o seu IP após diversas consultas, pode ser necessário aguardar alguns minutos caso isto ocorra.\nDICA: Para saber se sua máquina está bloqueada tente acessar o(s) site(s) pelo navegador.";
									Alert alert = new Alert(AlertType.NONE,  msg, ButtonType.OK);
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
			  };
			  timer.schedule(task, Statics.Connection.DELAY_WD, Statics.Connection.PERIOD_WD);
			  
			//DMG
	        h1 = new HtmlHandlerDMG();
			//DPB
			h2 = new HtmlHandlerDPB();
			//DPE
			h3 = new HtmlHandlerDPE();
			//PSP
			h4 = new HtmlHandlerPSP();
			//SAL
			h5 = new HtmlHandlerSAL();
			//SAN
			h6 = new HtmlHandlerSAN();
			//SPT
			h7 = new HtmlHandlerSPT();
			//TRP
			h8 = new HtmlHandlerTRP();
			//Perform TRP alone:
			if(cbTRP.isSelected())
				((HtmlHandlerTRP)h8).getIdThenConnect(cars, Statics.Connection.ATTEMPTS);
			for(String c:cars){
				if(c.length()<3 || c.length()>9 || alreadyFound(c)){
					continue;	  
				}
				printOut("Buscando "+c+"...", false);
				if(cbDMG.isSelected())
					h1.connect(c, Statics.Connection.ATTEMPTS);	
				if(cbDPB.isSelected())
					h2.connect(c, Statics.Connection.ATTEMPTS);	
				if(cbDPE.isSelected())
					h3.connect(c, Statics.Connection.ATTEMPTS);
				if(cbPSP.isSelected())
					h4.connect(c, Statics.Connection.ATTEMPTS);
				if(cbSAL.isSelected())
					h5.connect(c, Statics.Connection.ATTEMPTS);		
				if(cbSAN.isSelected())
					h6.connect(c, Statics.Connection.ATTEMPTS);	
				if(cbSPT.isSelected())
					h7.connect(c, Statics.Connection.ATTEMPTS);	
		        /*      
		        // Take a break:
		        timerPause++;
		        if(timerPause>1000){
		        	timerPause = 0;
		        	timerDown = 0;
		        	try {
						Thread.sleep((long)10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	timerDown = 0;
					h1.close();
					h2.close();
		            h1 = new HtmlHandlerDMG();
		    		h2 = new HtmlHandlerDPE();
		        }*/
			}	
		}catch(Exception e){
			if(Main.DEBUG)
				e.printStackTrace();
		}
		
	}

	public synchronized boolean alreadyFound(String c){
		if(carsFound.contains(c)){
			return true;
		}
		return false;
	}
	
	public synchronized void receiveData(Object[] object){
		timerDown = 0;
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
		if(!((String)object[1]).isEmpty()){
			carsFound.add((String)object[0]);
			if(Main.DEBUG)
				System.out.println("-> Encontrado: "+(String)object[0]+" ("+IHtmlHandler.getNameBySite((String)object[2])+")");
			printOut("-> Encontrado: "+(String)object[0]+" ("+IHtmlHandler.getNameBySite((String)object[2])+")", false);
		}
		
	}
	
	public synchronized void printOut(String msg, boolean reset){
		Platform.runLater(new Runnable() {
			public void run() {
				if(reset)
					txtOut.setText("");
				txtOut.setEditable(true);
				txtOut.appendText("\n"+msg);
				txtOut.setEditable(false);
			}
		});
	}
	
	public synchronized void setSiteDown(String site){
		if(sitesDown.isEmpty())
			sitesDown = "Sites com 100% de falha: "+site;
		else
			sitesDown+=", "+site;
			
	}
}
