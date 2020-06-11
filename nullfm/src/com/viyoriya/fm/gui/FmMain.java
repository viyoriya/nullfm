package com.viyoriya.fm.gui;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.viyoriya.fm.util.FmMountDrives;
import com.viyoriya.fm.util.FmUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FmMain extends Application {

	final static Logger log = LogManager.getLogger(FmMain.class);
	FmMountDrives fmMountDrives = new FmMountDrives();
	
    @Override
    public void start(Stage stage) {
        stage.setTitle("File Manager");
        stage.getIcons().add(new Image(FmUtil.APP_ICON));
		stage.setScene(createScene(loadMainPane()));
		stage.setResizable(true);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();
    }

    //Dont wanna use multiple fxml's so avoiding the FmUtil loadforms method 
	private Pane loadMainPane()  {
		FXMLLoader fxmlLoader = new FXMLLoader();
		Pane pane =null;
		try {
			fxmlLoader.setResources(ResourceBundle.getBundle(FmUtil.I18N, Locale.getDefault()));
			pane = (Pane) fxmlLoader.load(getClass().getResourceAsStream(FmUtil.MAIN));
			//return (Pane) new FXMLLoader().load(getClass().getResourceAsStream(FmUtil.MAIN));
		} catch (IOException ioe) {
			   log.error("IOException occurred while loading the main pane :: loadMainPane ::",ioe); 
		}
		return  pane;
	}

    private Scene createScene(Pane mainPane) {        
    	Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    	Scene scene = new Scene(mainPane,screenBounds.getWidth(),screenBounds.getHeight());
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
