package application.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import application.handlers.ExcelHandler;

public final class GenericMethods {
	
	public static Double convertCentimeterPPI(double cm){
		return cm * 0.393600787 * 72d;
	}
	
	public static void saveFileOnDisk(BufferedImage image, String fileType, String filePath){
		File myFile = new File(filePath);
		try {
			//TODO: ImageIO.write(image, fileType, myFile);
		} catch (Exception e) {
			if(Main.DEBUG)
        		e.printStackTrace();
		}
	}

    /**
     * @see https://stackoverflow.com/a/14114122
     */
    public static void checkAllPaths(){
    	Class<?> c = Statics.Path.class;
	    Field[] fields = c.getDeclaredFields();
	    for( Field field : fields ){
	    	try {
	    		if(field.getName().equals("this$0"))
	    			continue;
				String path = (String) field.get(null);
		        File files = new File(path);
		        if (!files.exists()) {
		            if (files.mkdirs()) {
		                System.out.println("Dir created: "+path);
		            } else {
		                System.out.println("Failed dir create:"+path);
		            }
		        }
			} catch (Exception e) {
				if(Main.DEBUG)
	        		e.printStackTrace();
			}
    	}
    }
    

    public static void checkInputFile(){
    	String fPath = Statics.Path.APP_INPUT+"\\"+Statics.File.INPUT;
    	File file = new File(fPath);
    	if(!file.exists()){
    		List<Object[]> data = new ArrayList<Object[]>();
    		data.add(new Object[]{"PLACAS"});
    		ExcelHandler.write(fPath, data);
    	}
    }
}
