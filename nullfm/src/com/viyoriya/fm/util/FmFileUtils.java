package com.viyoriya.fm.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;

public final class FmFileUtils {

   final static Logger log = LogManager.getLogger(FmFileUtils.class);
	
   public static final String MKDIR    		= "Create directory";
   public static final String MKFILE   		= "Create file";
   public static final String RENAME   		= "Rename";
   public static final String ERR_EXIST    	= "Already exist";
   public static final String ERR_RENAME   	= "Error while rename";
   public static final String ERR_DELETE   	= "Error while delete";
   
   private 	FmFileUtils() {}
   
   public static void createDirOrFile(String strPath, boolean isDir) {
		String strDirName = showInputDialog(isDir ? MKDIR:MKFILE,false,FmUtil.EMPTY_2S);
        if (strDirName != null && strDirName.length()>0) {
        	Path newDirPath = Paths.get(strPath).resolve(strDirName);
        	if(!Files.exists(newDirPath)) {
	            try {
	            	if(isDir)
	            		Files.createDirectory(newDirPath);
	            	else
	            		Files.createFile(newDirPath);
	            } catch (IOException ioe) {
	            	log.error("IOException occurred while creating the directory :: createDirOrFile ::",ioe);
	                showAlert(Alert.AlertType.INFORMATION, ERR_EXIST, newDirPath.toString());
	            } 
        	}else {
        		showAlert(Alert.AlertType.INFORMATION, ERR_EXIST, newDirPath.toString());
        	}
        }
    }

   public static void deleteDirOrFile(List<String> strPathList)  {
       try {
    	   for(String strPath:strPathList ) {
               if (Paths.get(strPath).toFile().isDirectory()) {
                   FileUtils.deleteDirectory(Paths.get(strPath).toFile());
               } else {
            	   FileUtils.deleteQuietly(Paths.get(strPath).toFile());
               }
    	   }
       } catch (IOException ioe) {
    	   log.error("IOException occurred while deleting the directory :: deleteDirOrFile ::",ioe);
    	   showAlert(Alert.AlertType.INFORMATION ,ERR_DELETE,FmUtil.EMPTY_2S);
       }    
   }   

   public static void renameDirOrFile(String strPath) {
       String strDirName = showInputDialog(RENAME,true,strPath);
       if (strDirName != null && strDirName.length()>0) {
    	   Path newPath = Paths.get(strPath).resolveSibling(strDirName);
           try {
        		   Files.move(Paths.get(strPath), newPath);
           } catch (IOException ioe) {
        	   log.error("IOException occurred while renaming the directory :: renameDirOrFile ::",ioe); 
               showAlert(Alert.AlertType.INFORMATION, ERR_RENAME, newPath.toString());
           }
       }
   }
   
   public static void copyDirOrFile(List<String> strSourceList, String strDistPath) {
       try {
    	   for(String strSourcePath: strSourceList) {
               if (Paths.get(strSourcePath).toFile().isDirectory()) {
                   FileUtils.copyDirectoryToDirectory(Paths.get(strSourcePath).toFile(),Paths.get(strDistPath).toFile());
               } else {
            	    FileUtils.copyFileToDirectory(Paths.get(strSourcePath).toFile(),Paths.get(strDistPath).toFile());
               }    		   
    	   }
       } catch (IOException ioe) {
    	   log.error("IOException occurred while copying the directory :: copyDirOrFile ::",ioe); 
       }	
   }
   
    public static void showAlert(AlertType alertType, String strMsg, String strNewPath) {
    	String strCssPath = FmFileUtils.class.getResource(FmUtil.ALERT_CSS).toExternalForm();
        Alert alert = new Alert(alertType);
        alert.setTitle(strMsg);
        alert.setHeaderText(null);
        alert.setGraphic(null);          
        alert.setContentText(strNewPath+FmUtil.EMPTY_2S+strMsg);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        //alert.initStyle(StageStyle.UTILITY);
        alert.getDialogPane().getStylesheets().add(strCssPath);
        alert.showAndWait();
    }

	public static String showInputDialog(String strHeader,boolean isFilename, String strFileName) {
    	String strCssPath = FmFileUtils.class.getResource(FmUtil.ALERT_CSS).toExternalForm();
    	TextInputDialog dialog = null;
        if(isFilename) {
        	dialog = new TextInputDialog(FmUtil.substringAfterLast(strFileName, File.separator));
        }else {dialog = new TextInputDialog();}
        dialog.setHeaderText(null);
        dialog.setGraphic(null);        
        dialog.setTitle(strHeader);
        dialog.setHeight(200.0);
        dialog.setWidth(250.0);
        //dialog.initStyle(StageStyle.UTILITY);
        dialog.getDialogPane().getStylesheets().add(strCssPath);
        Optional<String> strOutput = dialog.showAndWait();
        return strOutput.isPresent() ? strOutput.get() : null;
    }
    
    
}
