package application;
	
import application.controller.MainController;
import application.util.GenericMethods;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {

	public static boolean DEBUG = false;
	
    private Stage primaryStage;
    MainController mainController;
    

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
	        
			GenericMethods.checkAllPaths();
			GenericMethods.checkInputFile();
			
			initMainLayout();
			
		} catch(Exception e) {
			if(Main.DEBUG)
        		e.printStackTrace();

    		Alert alert = new Alert(AlertType.NONE, "Erro ao inicializar[2]: "+e.getCause().toString() , ButtonType.OK);
    		alert.showAndWait();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
    
    /**
     * 
     * @return the primaryStage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    

    /**
     * Set main layout
     */
    public void initMainLayout() {
        try {
            // Load main layout from xml
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/MainView.fxml"));
            
            AnchorPane mainLayout = (AnchorPane) loader.load();
            //rootLayout.setCenter(mainLayout);
            
            // Set controller
            MainController mainController = loader.getController();
            mainController.setMain(this);
            
            // Show layout on scene
            Scene scene = new Scene(mainLayout);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.resizableProperty().setValue(false);
			//primaryStage.setMaximized(true);
			//primaryStage.setFullScreen(true);
			primaryStage.setTitle("Consulta de Placas");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
            primaryStage.show();           
			
            
        } catch (Exception e) {
        	if(Main.DEBUG)
        		e.printStackTrace();

    		Alert alert = new Alert(AlertType.NONE, "Erro ao incializar[3]: "+e.getCause().toString() , ButtonType.OK);
    		alert.showAndWait();
        }
    }
}
