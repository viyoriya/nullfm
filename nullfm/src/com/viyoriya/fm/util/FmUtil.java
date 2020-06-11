package com.viyoriya.fm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.viyoriya.fm.model.FmDeviceDetails;
import com.viyoriya.fm.model.FmFileDetails;

import javafx.scene.control.cell.PropertyValueFactory;

public final class FmUtil {

	final static Logger log = LogManager.getLogger(FmUtil.class);
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static Map<String, String> iconsMap = new HashMap<String,String>();
	
	public static final String APP_NAME 	= System.getProperty("user.name")+"fm";
	public static final String I18N		 	= "i18n/fm";
	public static final int INDEX_NOT_FOUND = -1;
	public static final String EMPTY 		= "";
	public static final String EMPTY_2S 	= "  ";
	public static final String APP_ICON    	= "/img/icon.png";
    public static final String MAIN    		= "/fxml/fm.fxml";
    public static final String ALERT_CSS    = "/css/fm.css";
    public static final String ALERT_BTN    = "alert.button.text";
    public static final String ALERT_ABT    = "alert.about";
    
    public static final String HOME_WOS		= "/home/"+System.getProperty("user.name");
    public static final String BM_PATH      = HOME_WOS+File.separator+".config/fm/fm.bookmark"; 
    public static final String RUBBISH      = "Trash";
    public static final String RUBBISH_PATH = HOME_WOS+File.separator+".local/share/Trash/files";  
    public static final String SH 		    = "/bin/sh";
    public static final String PARAM_C 		= "-c";
    //public static final String TILDE 		= "~";
    public static final String CD 			= "cd ";    
    public static final String AND 			= " && ";        
    public static final String SPACE 	    = "\\s+";
    public static final String EQUAL 	    = "=";
    public static final String QUOTE 	    = "\"";    
    public static final String NEW_LINE     = "\n";
    public static final String FOLDER       = "Folder";
    public static final String FILE         = "File";
    public static final String TERMINAL 	= "/usr/bin/x-terminal-emulator";   
    public static final String LSBLK 	    = "lsblk --pairs --output NAME,TYPE,FSTYPE,SIZE,MOUNTPOINT,UUID";
    public static final String DF 	    	= "df -H / | tail -n 1";
    public static final String PART         = "part";
    public static final String SWAP         = "swap";
    public static final String VFAT         = "vfat";
    public static final String DISK         = "disk";
    public static final String ROOT         = "/";
    public static final String DEV_ROOT     = "/run/media/"+System.getProperty("user.name");
    public static final String MNT_CMD      = "udisksctl mount -b /dev/";
    public static final String UN_MNT_CMD   = "udisksctl unmount -b /dev/";
    public static final String XDG_OPEN   	= "xdg-open ";
    public static final String XDG		    = "F";
    public static final String TERM		    = "T";
    public static final String MNT		    = "M";
    public static final String UN_MNT       = "U";
    public static final String FREE    		= "Free";
    public static final String USED		    = "Used";
    public static final String PERCENTAGE   = "Percentage";

    public static final String TAB_US       = "tab_";
    public static final String TAB_VIEW     = "tabView_";
    public static final String TAB_PLUS     = "tabPlus";
    public static final String TAB_HOME     = "tabHome";
    public static final String TAB_TXT_HOME = "Home";
    
    public static final String ICON         = "icon";
    public static final String DESC         = "Description";
    public static final String SIZE         = "Size";
    public static final String NAME         = "Name";
    public static final String CREATED      = "Created";
    public static final String MODIFIED     = "Modified";
    public static final String EMPTY_DIR    = "Empty directory";
    public static final String EMPTY_LBL    = "labelEmpty";

    public static final String BIN_ICON     = "\uf1f8";
    public static final String FOLDER_ICON  = "\uf07b";
    public static final String FILE_ICON    = "\uf15b";
    public static final String TICK_ICON    = "\uf14a";
    public static final String UNTICK_ICON  = "\uf0c8";    
    public static final String CREATE_ICON  = "\uf067";    
    public static final String COPY_ICON    = "\uf0c5";    
    public static final String PASTE_ICON   = "\uf0ea";    
    public static final String RENAME_ICON  = "\uf079";    
    public static final String DELETE_ICON  = "\uf00d"; 
    public static final String EJECT_ICON   = "\uf052";    
    public static final String HDD_ICON     = "\uf0a0"+EMPTY_2S;
    public static final String BM_ICON      = "\uf02e"+EMPTY_2S;
    public static final String EX_HDD_ICON  = "\uf287"+EMPTY_2S;
    public static final String PLUS_ICON    = EMPTY_2S+"\uf067"+EMPTY_2S;
            
    public static final String MENU_HIDDEN  = UNTICK_ICON+EMPTY_2S+"Show Hidden";
    public static final String NOT_HIDDEN   = TICK_ICON+EMPTY_2S+"Show Hidden";
    public static final String MENU_CREATE  = CREATE_ICON+EMPTY_2S+"Create";
    public static final String MENU_DIR     = FOLDER_ICON+EMPTY_2S+"Directory";
    public static final String MENU_FILE    = FILE_ICON+EMPTY_2S+"File";
    public static final String MENU_COPY    = COPY_ICON+EMPTY_2S+"Copy";
    public static final String MENU_PASTE   = PASTE_ICON+EMPTY_2S+"Paste";
    public static final String MENU_RENAME  = RENAME_ICON+EMPTY_2S+"Rename";
    public static final String MENU_DELETE  = DELETE_ICON+EMPTY_2S+"Delete";

    
    public static final FmFileDetails sfdObject  = new FmFileDetails(BIN_ICON,RUBBISH,FOLDER,EMPTY,EMPTY,EMPTY);    
    
    private FmUtil() {}
    
	public static List<FmFileDetails> directoriesList(Path path, boolean isHidden, boolean isOnlyDirs) {
	    List<FmFileDetails> fdList = new ArrayList<>();
	    BasicFileAttributes bfa = null;
	    if(Files.isDirectory(path)) {
		    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		        for (Path subPath : directoryStream) {
		        	if(isHidden) {
			        		if(isOnlyDirs) {
				        		if (Files.isDirectory(subPath) && ! Files.isHidden(subPath)) {
					                bfa = Files.readAttributes(subPath, BasicFileAttributes.class);
					                fdList.add(new FmFileDetails(getDirIcon(subPath.getFileName().toString().trim()),
					                		                          subPath.getFileName().toString().trim(),
					                								  bfa.isDirectory()?FOLDER:FILE , 
					                								  getFileSize(bfa.size()), 
					                								  dateFormat.format(bfa.creationTime().toMillis()),
					                								  dateFormat.format(bfa.lastModifiedTime().toMillis())));
				        		}
			        		}else {
				        		if (! Files.isHidden(subPath)) {
					                bfa = Files.readAttributes(subPath, BasicFileAttributes.class);
					                fdList.add(new FmFileDetails(bfa.isDirectory()?FOLDER_ICON:FILE_ICON ,
					                		                          subPath.getFileName().toString().trim(),
					                								  bfa.isDirectory()?FOLDER:FILE , 
					                								  getFileSize(bfa.size()), 
					                								  dateFormat.format(bfa.creationTime().toMillis()),
					                								  dateFormat.format(bfa.lastModifiedTime().toMillis())));
				        		}
			        		}
		        	}else {
		                bfa = Files.readAttributes(subPath, BasicFileAttributes.class);
		                fdList.add(new FmFileDetails(bfa.isDirectory()?FOLDER_ICON:FILE_ICON ,
		                								  subPath.getFileName().toString().trim(),
		                								  bfa.isDirectory()?FOLDER:FILE , 
		                								  getFileSize(bfa.size()), 
		                								  dateFormat.format(bfa.creationTime().toMillis()),
		                								  dateFormat.format(bfa.lastModifiedTime().toMillis())));
		        	}
		        }
		    } catch (IOException ioe) {
		    	log.error("IOException occurred while reading the directory list :: directoriesList ::",ioe);
		    }
	    }
	    if(isHidden){
	    	fdList.sort((FmFileDetails s1, FmFileDetails s2)->s1.getName().compareTo(s2.getName())); 
	    }
	    else {
	    	fdList.sort((FmFileDetails s1, FmFileDetails s2)->s2.getDescription().compareTo(s1.getDescription()));
	    }
	    return fdList;
	}
	
	private static String getDirIcon(String strDirName) {
		return iconsMap.getOrDefault(strDirName, FOLDER_ICON);
	}	
	public static void setDirIcons() {
		//cant use enum coz of null issue while accessing default icon
		//Very old way
		iconsMap.put("Desktop",		"\uf108");
		iconsMap.put("Documents", 	"\uf07b");
		iconsMap.put("Downloads", 	"\uf019");
		iconsMap.put("Pictures", 	"\uf302");
		iconsMap.put("Music", 		"\uf7a6");
		iconsMap.put("Public", 		"\uf07b");
		iconsMap.put("Templates", 	"\uf15c");
		iconsMap.put("Videos", 		"\uf03d");
		iconsMap.put("Default", 	FOLDER_ICON);		
	}
	
    public static List<FmDeviceDetails> devicesList() {
		List<FmDeviceDetails> devicesList = new ArrayList<FmDeviceDetails>();
    	try {
			Process process = new ProcessBuilder(SH, PARAM_C, LSBLK).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			reader.lines().forEach(
					line -> {
						String[] strArray = line.split(SPACE);
						devicesList.add(new FmDeviceDetails(
									substringAfterLast(strArray[0],EQUAL).replaceAll(QUOTE,EMPTY).trim(), 
									substringAfterLast(strArray[1],EQUAL).replaceAll(QUOTE,EMPTY).trim(),
									substringAfterLast(strArray[2],EQUAL).replaceAll(QUOTE,EMPTY).trim(),
									substringAfterLast(strArray[3],EQUAL).replaceAll(QUOTE,EMPTY).trim(),
									substringAfterLast(strArray[4],EQUAL).replaceAll(QUOTE,EMPTY).trim(),
									substringAfterLast(strArray[5],EQUAL).replaceAll(QUOTE,EMPTY).trim()
								));
					}
			);
			reader.close();
		} catch (IOException ioe) {
			log.error("IOException occurred while reading the devices list :: devicesList ::",ioe);
		}
    	return devicesList;
    }	

    
    public static String deviceSize(){
    	//Map<String,String> deviceSizeMap =new TreeMap<String, String>();
    	StringBuilder strInfoBuilder = new StringBuilder();
		try {
			Process process = new ProcessBuilder(SH, PARAM_C, DF).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			reader.lines().forEach(
					line -> {
						String[] strArray = line.split(SPACE);
						strInfoBuilder.append(EMPTY_2S);
						strInfoBuilder.append(substringAfterLast(strArray[0], File.separator));
						strInfoBuilder.append(EMPTY_2S);
						strInfoBuilder.append(strArray[1]);
						strInfoBuilder.append(EMPTY_2S);
						strInfoBuilder.append(ROOT);
						strInfoBuilder.append(EMPTY_2S);
						strInfoBuilder.append(FREE);
						strInfoBuilder.append(EMPTY_2S);
						strInfoBuilder.append(strArray[3]);
						strInfoBuilder.append(EMPTY_2S);
						strInfoBuilder.append(EQUAL);
						strInfoBuilder.append(substringBeforeLast(strArray[4], "%"));
						//deviceSizeMap.put(DISK, substringAfterLast(strArray[0], File.separator));
						//deviceSizeMap.put(SIZE, strArray[1]);
						//deviceSizeMap.put(USED, strArray[2]);
						//deviceSizeMap.put(FREE, strArray[3]);
						//deviceSizeMap.put(PERCENTAGE, strArray[4]);
					}
			);
			reader.close();
		} catch (IOException ioe) {
			log.error("IOException occurred while reading the device size :: deviceSize ::",ioe);
		}
    	//return deviceSizeMap;
		return strInfoBuilder.toString();
    }

    public static List<PropertyValueFactory<FmFileDetails, String>> getColumnsList() {
    	List<PropertyValueFactory<FmFileDetails, String>> colList = new ArrayList<PropertyValueFactory<FmFileDetails, String>>();
    	colList.add(0, new PropertyValueFactory<FmFileDetails, String>(ICON));
    	colList.add(1, new PropertyValueFactory<FmFileDetails, String>(NAME));
    	colList.add(2, new PropertyValueFactory<FmFileDetails, String>(DESC));
    	colList.add(3, new PropertyValueFactory<FmFileDetails, String>(SIZE));
    	colList.add(4, new PropertyValueFactory<FmFileDetails, String>(CREATED));
    	colList.add(5, new PropertyValueFactory<FmFileDetails, String>(MODIFIED));
    	return colList;
    }
    
    
	private static String getFileSize(long fileSize) {
		
        if (fileSize < (1024)) {
        	return Long.toString(fileSize) + " B";
        } else if (fileSize >= (1024) && fileSize < (1024 * 1024)) {
            return Long.toString(fileSize / 1024) + " KB";
        } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
            return Long.toString(fileSize / (1024 * 1024)) + " MB";
        } else if (fileSize >= (1024 * 1024 * 1024)) {
            return Long.toString(fileSize / (1024 * 1024 * 1024)) + " GB";
        }
        return EMPTY;
	}

	//Modified Apache Commons-lang method
	public static String substringBeforeLast(final String str, final String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        if(str.length()-INDEX_NOT_FOUND == pos) { 
        	return substringBeforeLast(str.substring(0, pos),separator);
        }        
        return str.substring(0, pos);
    }    
	public static String substringAfterLast(final String str, final String separator) {
	    if (isEmpty(str)) {
	        return str;
	    }
	    if (isEmpty(separator)) {
	        return EMPTY;
	    }
	    final int pos = str.lastIndexOf(separator);
        if(str.length()-INDEX_NOT_FOUND == pos) { 
        	return substringAfterLast(str.substring(0, pos),separator);
        }   	    
	    return str.substring(pos + separator.length());
	}
	
	public static boolean isEmpty(final CharSequence cs) {
	       return cs == null || cs.length() == 0;
	}

	
    //felt bit of lag while using common method (runProcess) so added these duplicate methods 
	//(to avoid process blocking) - Ugly workaround
	
    public static void openTerminal(String strDirName){
	    String[] cmd = { SH,PARAM_C, CD+strDirName+AND+TERMINAL };
	    try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException ioe) {
			log.error("IOException occurred while opening terminal :: openTerminal ::",ioe);
		}
	}

    public static void openFile(String strFileName) {
	    try {
	    	String[] cmd = { SH,PARAM_C, XDG_OPEN+strFileName };
			Runtime.getRuntime().exec(cmd);
		} catch (IOException ioe) {
			log.error("IOException occurred while unmounting the drive :: unmountDrive ::",ioe);
		}      
    }    
    
    public static void mountDrive(String strPartitionName) {
	    try {
	    	String[] cmd = { SH,PARAM_C, MNT_CMD+strPartitionName };
			Runtime.getRuntime().exec(cmd);
		} catch (IOException ioe) {
			log.error("IOException occurred while mounting the drive :: mountDrive ::",ioe);
		}      
    }    

    public static void unmountDrive(String strPartitionName) {
	    try {
	    	String[] cmd = { SH,PARAM_C, UN_MNT_CMD+strPartitionName };
			Runtime.getRuntime().exec(cmd);
		} catch (IOException ioe) {
			log.error("IOException occurred while unmounting the drive :: unmountDrive ::",ioe);
		}      
    }     
    
    //Common method to run the process but bit of lag so commented out and using above 
    //duplicate methods
    /*
    public static void runProcess(String strName,String strOption) {
    	String[] cmd = new String[3];
    	cmd[0] = SH; cmd[1] = PARAM_C;
    	
    	if(strOption.equalsIgnoreCase(TERM)) {
    		 cmd[2] = CD+strName+AND+TERMINAL;
    	}else if(strOption.equalsIgnoreCase(MNT)) {
    		cmd[2] = MNT_CMD+strName;
    	}else if(strOption.equalsIgnoreCase(UN_MNT)) {
    		cmd[2] = UN_MNT_CMD+strName; 
    	}else if(strOption.equalsIgnoreCase(XDG)) {
    		cmd[2] = XDG_OPEN+strName; 
    	}
    	
	    try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException ioe) {
			log.error("IOException occurred while starting new process :: runProcess ::",ioe);
		}
    	
    }
	*/
    
    /*
    public static void loadForms(String fxmlFileName) {
        try {
        	Node nodes = (Node) FXMLLoader.load(FmUtil.class.getResource(fxmlFileName));
            mainController.setFormNodes(nodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

	
	
	
}
