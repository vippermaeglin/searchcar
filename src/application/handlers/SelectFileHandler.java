/**
 * 
 */
package application.handlers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import application.Main;
import application.controller.MainController;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Vinicius
 *
 */
public class SelectFileHandler implements EventHandler<MouseEvent>{
	private MainController mainController;
	private String filePath;
	private boolean isInput;
	
	public SelectFileHandler(MainController mainController, String filePath, boolean isInput){
		this.mainController = mainController;
		this.filePath = filePath;
		this.isInput = isInput;
	}

	public void handle(MouseEvent event) {
		File file = null;
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Arquivo Excel");
    	fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel 2007/2010/2013", "*.xlsx"),
                new FileChooser.ExtensionFilter("Excel 97/2000/XP/2003", "*.xls")
            );
    	fileChooser.setInitialDirectory(new File(filePath));
    	file = fileChooser.showOpenDialog(null);
    	if (file != null) {
    		try {
    			if(isInput){
    				mainController.setFile(file.getPath(), true);
    			}
    			else{
    				mainController.setFile(file.getPath(), false);
    			}
    		
			}catch(Exception ex){
				if(Main.DEBUG)
	        		ex.printStackTrace();
	    		Alert alert = new Alert(AlertType.NONE, "Erro ao selecionar arquivo: "+ex.getMessage() , ButtonType.OK);
	    		alert.showAndWait();
			}
        }
	}

}