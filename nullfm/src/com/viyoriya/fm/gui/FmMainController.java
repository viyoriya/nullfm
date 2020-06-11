package com.viyoriya.fm.gui;

//Avoided the static import coz its loads of them....

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.viyoriya.fm.model.FmDeviceDetails;
import com.viyoriya.fm.model.FmFileDetails;
import com.viyoriya.fm.util.FmFileUtils;
import com.viyoriya.fm.util.FmUtil;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class FmMainController {

	final static Logger log = LogManager.getLogger(FmMainController.class);
	private double x,y = 0;
	private Set<String> bookmarkSet 			= new TreeSet<String>();
	private String rightClickedFileName 		= new String();
	private String currentDirOrFileName 		= new String();
	private List<String> copyPath 				= new ArrayList<String>();
	private Map<String,String> devicesMap 		= new TreeMap<String,String>();
	private Map<String,String> devicesSizeMap 	= new TreeMap<String,String>();
	private Map<String,String> deviceMountMap 	= new TreeMap<String,String>();
	private boolean isHidden					= true;
	private int tabsSize 						= 1;
	private Map<String,String> tabPathMap 	    = new TreeMap<String,String>();
	private TableView<FmFileDetails> tableViewMain;
	
	@FXML private VBox vboxHomeDir,vboxDevices,vboxBookmarkDir;
    @FXML private TextField tf_path;
    //@FXML private HBox hboxStatusBar;
    @FXML private TabPane tabPaneMain;
    //@FXML private Tab tabPlus;
    //@FXML private AnchorPane tabAnchorPane;
    @FXML private Label labelTotalDiskSize,labelDiskInfo, labelFm;
    @FXML private ProgressBar progressBar;
    @FXML private ResourceBundle resources;    
    	                         
    @FXML
    private void initialize()  {
    	labelFm.setText(FmUtil.APP_NAME);
    	FmUtil.setDirIcons();
    	listHomeDirectories();
    	listPartitionNames(false);
    	listBookmarks();
    	listStatusBar();
    	addTabs();    	
	}

	private void listHomeDirectories() {
		List<FmFileDetails> homeDirList = FmUtil.directoriesList(Paths.get(FmUtil.HOME_WOS), isHidden, true);
		homeDirList.add(FmUtil.sfdObject);
    	for(FmFileDetails fileDetails : homeDirList) {
	    		Button button = new Button(fileDetails.getIcon()+FmUtil.EMPTY_2S+fileDetails.getName());
	    		vboxHomeDir.getChildren().add(button);
	        	button.setOnAction(e -> {
	        		if(fileDetails.getName().equalsIgnoreCase(FmUtil.RUBBISH)) {
	        			setDefaultValues(FmUtil.RUBBISH_PATH);
	        		}else {
	        			setDefaultValues(FmUtil.HOME_WOS+File.separator+fileDetails.getName());
	        		}
	        	});
    	}
    	setPathText(FmUtil.HOME_WOS);
    	createTab(homeDirList,FmUtil.TAB_HOME);
	}	

	private void listPartitionNames(boolean isDataAvailable) {
		if(!isDataAvailable) {
			Map<String,String> partitionMap = getPartitionNames(FmUtil.devicesList());
			setDevicesMap(partitionMap);
		}
		for (Map.Entry<String, String> entry : getDevicesMap().entrySet()) {
			Button button = new Button(FmUtil.HDD_ICON+entry.getKey());
			Button eButton = new Button(FmUtil.EJECT_ICON);
			eButton.setStyle("-fx-text-fill	: -vBgColorDefault");
			vboxDevices.getChildren().add(new HBox(45,button,eButton));
			deviceMountMap.put(entry.getKey(), FmUtil.MNT);
			button.setOnAction(e -> {
				if(deviceMountMap.get(entry.getKey()).equalsIgnoreCase(FmUtil.UN_MNT)) {
					//FmUtil.runProcess(entry.getKey(),FmUtil.MNT);
					FmUtil.mountDrive(entry.getKey());
					deviceMountMap.put(entry.getKey(),FmUtil.MNT);
					threadSleep(200);	
				}
				setDefaultValues(FmUtil.DEV_ROOT+File.separator+entry.getValue());
			});
			eButton.setOnAction(e -> {
				//FmUtil.runProcess(entry.getKey(),FmUtil.UN_MNT);
				FmUtil.unmountDrive(entry.getKey());
				deviceMountMap.put(entry.getKey(),FmUtil.UN_MNT);
				tableViewMain.getItems().clear();
			});
		}
	}
	
	private void listBookmarks() {
		File bookmarkFile = new File(FmUtil.BM_PATH);
		bookmarkFile.getParentFile().mkdirs();
		try {
			bookmarkFile.createNewFile();
			bookmarkSet = new TreeSet<String>(Files.readAllLines(Paths.get(FmUtil.BM_PATH)));
		} catch (IOException ioe) {
			log.error("IOException occurred while creating bookmark file :: listBookmarks ::",ioe);
		}  	
		setBookmarkSet(bookmarkSet);
    	for(String strDirName: bookmarkSet) {
    		Button button = new Button(FmUtil.BM_ICON+FmUtil.substringAfterLast(strDirName,File.separator));
    		vboxBookmarkDir.getChildren().add(button);
        	button.setOnAction(e -> {
        		setDefaultValues(strDirName);
        	});    		
    	}
	}

	private void listStatusBar() {
    	Iterator<Entry<String, String>> iterator = this.getDevicesSizeMap().entrySet().iterator();
    	while (iterator.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) iterator.next();
			labelTotalDiskSize.setText(entry.getKey()+FmUtil.EMPTY_2S+entry.getValue());
   	 	}	
    	
    	String strDeviceInfo = FmUtil.deviceSize();
        labelDiskInfo.setText(FmUtil.substringBeforeLast(strDeviceInfo, FmUtil.EQUAL));
        progressBar.setProgress(Double.valueOf(FmUtil.substringAfterLast(strDeviceInfo, FmUtil.EQUAL)).doubleValue()/100);
	}
	
	private void addTabs() {		
		tabPaneMain.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
		      @Override
		      public void changed(ObservableValue<? extends Tab> observable, Tab oldSelectedTab, Tab newSelectedTab) 
		      {
		    	  //System.out.println("====1==="+newSelectedTab.getId()+"---viewName---"+tableViewMain.getId());
		    	  if (! newSelectedTab.getId().equalsIgnoreCase(FmUtil.TAB_PLUS)) {
		    		  setTableView(FmUtil.TAB_VIEW+newSelectedTab.getId());
		    		  //System.out.println("====2===="+tableViewMain.getId()+"=="+tabPathMap);
		    		  setDefaultValues(getTabPath(tableViewMain.getId()));
		    		  
		    	  }
		    	  //System.out.println("====3==="+newSelectedTab.getId()+"---"+tabsSize+"---viewName---"+tableViewMain.getId());
		    	  if (newSelectedTab.getId().equalsIgnoreCase(FmUtil.TAB_PLUS)) {
		    		  createTab(new ArrayList<FmFileDetails>(),FmUtil.TAB_US+tabsSize);
		    		  
		    	  }
		    	  newSelectedTab.setOnCloseRequest(e->{
		    		  			tabPathMap.remove(tableViewMain.getId());
		    	  });
		      }
		});
	}
	
	private void createTab(List<FmFileDetails> homeDirList,String strTitle) {
		tabsSize++;
	    Tab tab = new Tab();
	    tab.setId(strTitle);
	    if(strTitle.equalsIgnoreCase(FmUtil.TAB_HOME)) {
	    	tab.setClosable(false);
	    	tab.setText(FmUtil.TAB_TXT_HOME);
	    }else {
	    	tab.setText(FmUtil.substringAfterLast(getPathText(), File.separator));
	    }
	    
	    tab.setContent(createTabContent(strTitle));
	    final ObservableList<Tab> tabs = tabPaneMain.getTabs();
	    tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
	    tabs.add(tabs.size() - 1, tab);
	    tabPaneMain.getSelectionModel().select(tab);
	    
	    if(strTitle.equalsIgnoreCase(FmUtil.TAB_HOME)) {
	    	listTableData(homeDirList);
	    	this.addTabPath(FmUtil.TAB_VIEW+strTitle, FmUtil.HOME_WOS);
	    }
	  }	
	
	@SuppressWarnings({ "static-access" })
	private AnchorPane createTabContent(String strIndex) {
		AnchorPane mainAnchorPane = new AnchorPane();
		mainAnchorPane.setMaxWidth(-1.0);
			VBox vBox = new VBox();
			mainAnchorPane.setBottomAnchor(vBox, 0.0);
			mainAnchorPane.setLeftAnchor(vBox, 0.0);
			mainAnchorPane.setRightAnchor(vBox, 0.0);
			mainAnchorPane.setTopAnchor(vBox, 0.0);
				AnchorPane innerAnchorPane = new AnchorPane();
				innerAnchorPane.setMaxHeight(-1.0);
				innerAnchorPane.setMaxWidth(-1.0);
				vBox.setVgrow(innerAnchorPane, Priority.ALWAYS);
					GridPane gridPane = new GridPane();
					innerAnchorPane.setBottomAnchor(gridPane, 0.0);
					innerAnchorPane.setLeftAnchor(gridPane, 0.0);
					innerAnchorPane.setRightAnchor(gridPane, 0.0);
					innerAnchorPane.setTopAnchor(gridPane, 0.0);					
						ColumnConstraints colConst = new ColumnConstraints();
						colConst.setMinWidth(10.0);
						colConst.setPrefWidth(100.0);
						colConst.setHgrow(Priority.ALWAYS);
						RowConstraints rowConst = new RowConstraints();
						rowConst.setMinHeight(10.0);
						rowConst.setPrefHeight(30.0);
						rowConst.setVgrow(Priority.SOMETIMES);
						gridPane.getColumnConstraints().add(colConst);
						gridPane.getRowConstraints().add(rowConst);
							TableView<FmFileDetails> tableView = new TableView<FmFileDetails>();
							gridPane.setHgrow(tableView, Priority.ALWAYS);
							gridPane.setVgrow(tableView, Priority.ALWAYS);
							tableView.setId(FmUtil.TAB_VIEW+strIndex);
					        for (PropertyValueFactory<FmFileDetails, String> pvf : FmUtil.getColumnsList() ) {
					        	TableColumn<FmFileDetails, String> col = null;
					        	switch (pvf.getProperty()) {
					    		case FmUtil.ICON:
					    			col = new TableColumn<FmFileDetails, String>();
					        		col.setMaxWidth(-1.0);col.setMinWidth(25.0);col.setPrefWidth(25.0);
					    			break;
					    		case FmUtil.NAME:
					    			col = new TableColumn<FmFileDetails, String>(pvf.getProperty());
					        		col.setMaxWidth(-1.0);col.setMinWidth(400.0);col.setPrefWidth(500.0);
					    			break;
					    		case FmUtil.DESC:
					    			col = new TableColumn<FmFileDetails, String>(pvf.getProperty());
					        		col.setMaxWidth(-1.0);col.setMinWidth(100.0);col.setPrefWidth(100.0);
					    			break;
					    		case FmUtil.SIZE:
					    			col = new TableColumn<FmFileDetails, String>(pvf.getProperty());
					        		col.setMaxWidth(-1.0);col.setMinWidth(100.0);col.setPrefWidth(100.0);
					    			break;
					    		case FmUtil.CREATED:
					    			col = new TableColumn<FmFileDetails, String>(pvf.getProperty());
					        		col.setMaxWidth(-1.0);col.setMinWidth(150.0);col.setPrefWidth(150.0);
					    			break;	
					    		case FmUtil.MODIFIED:
					    			col = new TableColumn<FmFileDetails, String>(pvf.getProperty());
					        		col.setMaxWidth(-1.0);col.setMinWidth(150.0);col.setPrefWidth(150.0);
					    			break;						    			
					    		default:
					    			col = new TableColumn<FmFileDetails, String>(pvf.getProperty());
					        		col.setMaxWidth(-1.0);col.setMinWidth(150.0);col.setPrefWidth(150.0);
					    		}
					        	col.setCellValueFactory(pvf);
					        	tableView.getColumns().add(col);
					        }								
						gridPane.setHgrow(tableView, Priority.ALWAYS);
						gridPane.setVgrow(tableView, Priority.ALWAYS);
						gridPane.getChildren().add(tableView);	
					innerAnchorPane.getChildren().add(gridPane);
				vBox.getChildren().add(innerAnchorPane);
			mainAnchorPane.getChildren().add(vBox);		
			
			Label lblEmpty = new Label(FmUtil.EMPTY_DIR);
			lblEmpty.setId(FmUtil.EMPTY_LBL);
			tableView.setPlaceholder(lblEmpty);
			tableView.setContextMenu(getContextMenu());
			tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			tableView.setOnMouseClicked(new tableViewMouseEventHandler());
			tableView.setOnKeyReleased(new tableViewKeyEventHandler());
			setTableView(tableView,FmUtil.TAB_VIEW+strIndex);
			addTabPath(FmUtil.TAB_VIEW+strIndex, getPathText());
			//System.out.println("==createTab=="+mainAnchorPane);
			return mainAnchorPane;
	}
		
	private class tableViewMouseEventHandler implements EventHandler<MouseEvent> {
		@SuppressWarnings("unchecked")
		@Override
		public void handle(MouseEvent me) {
	    	if (me.getButton().equals(MouseButton.PRIMARY) && me.getClickCount() == 2){
	    		
	    		if(((TableView<FmFileDetails>)me.getSource()).getSelectionModel().getSelectedItem()!=null) {
	    			String strDirName = getPathText()+File.separator+((TableView<FmFileDetails>)me.getSource()).getSelectionModel().getSelectedItem().getName();
	        		if(FmUtil.substringAfterLast(strDirName, File.separator).equalsIgnoreCase(FmUtil.RUBBISH)) {
            			setDefaultValues(FmUtil.RUBBISH_PATH);
	        		}
	        		else if(new File(strDirName).isDirectory()) {
            			setDefaultValues(strDirName);
            		}
	        		else if(new File(strDirName).isFile()) {
	        			//FmUtil.runProcess(strDirName, FmUtil.XDG);
	        			FmUtil.openFile(strDirName);
	        		}

	    		}
	    	}
	        if(me.getButton() == MouseButton.SECONDARY) {
	        	((TableView<FmFileDetails>)me.getSource()).getContextMenu().show((TableView<FmFileDetails>)me.getSource(), me.getScreenX(), me.getScreenY());
	        	setRightClickedFileName(getPathText());

	        	if(((TableView<FmFileDetails>)me.getSource()).getSelectionModel().getSelectedItem()!=null) 
	    			setRightClickedFileName(getPathText()+File.separator+((TableView<FmFileDetails>)me.getSource()).getSelectionModel().getSelectedItem().getName());

	        }else {
	        	((TableView<FmFileDetails>)me.getSource()).getContextMenu().hide();
	        }
		}
	}

	private class tableViewKeyEventHandler implements EventHandler<KeyEvent> {
		@SuppressWarnings("unchecked")
		@Override
		public void handle(KeyEvent ke) {
			if(ke.getCode().equals(KeyCode.ENTER)) {
				if(((TableView<FmFileDetails>)ke.getSource()).getSelectionModel().getSelectedItem()!=null) {
					String strDirName = getPathText()+File.separator+((TableView<FmFileDetails>)ke.getSource()).getSelectionModel().getSelectedItem().getName();
	        		if(((TableView<FmFileDetails>)ke.getSource()).getSelectionModel().getSelectedItem().getName().equalsIgnoreCase(FmUtil.RUBBISH)) {
	        			setDefaultValues(FmUtil.RUBBISH_PATH);
	        		}
	        		if(new File(strDirName).isDirectory()) {
	        			setDefaultValues(strDirName);
	        		}	 
	        		else if(new File(strDirName).isFile()) {
	        			//FmUtil.runProcess(strDirName, FmUtil.XDG);
	        			FmUtil.openFile(strDirName);
	        		}

				}
			}else if(ke.getCode().equals(KeyCode.BACK_SPACE)) {
				moveToPreviousDirectory();
			}
			
		}
	}	
	
	private ContextMenu getContextMenu() {
		
	 	    ContextMenu contextMenu = new ContextMenu();
	 	    MenuItem menuItemHidden = new MenuItem(FmUtil.MENU_HIDDEN);
	 	    Menu menuCreate		 	= new Menu(FmUtil.MENU_CREATE);
	 	    MenuItem menuItemDir    = new MenuItem(FmUtil.MENU_DIR);
	 	    MenuItem menuItemFile   = new MenuItem(FmUtil.MENU_FILE);
	 	    MenuItem menuItemCopy   = new MenuItem(FmUtil.MENU_COPY);
	 	    MenuItem menuItemPaste  = new MenuItem(FmUtil.MENU_PASTE);
	 	    MenuItem menuItemRename = new MenuItem(FmUtil.MENU_RENAME);
	 	    MenuItem menuItemDelete = new MenuItem(FmUtil.MENU_DELETE);
	 	   // MenuItem menuItemProperties = new MenuItem(FmUtil.MENU_PROP);
	 	    menuCreate.getItems().add(menuItemDir);
	 	    menuCreate.getItems().add(menuItemFile); 	    
	 	    contextMenu.getItems().add(menuItemHidden);
	 	    contextMenu.getItems().add(menuCreate);
	 	    contextMenu.getItems().add(menuItemCopy);
	 	    contextMenu.getItems().add(menuItemPaste);
	 	    contextMenu.getItems().add(menuItemRename);
	 	    contextMenu.getItems().add(menuItemDelete);
	 	   // contextMenu.getItems().add(menuItemProperties);	
	 	    
	 	    menuItemHidden.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
	 	    menuItemCopy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
	 	    menuItemPaste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
	 	    menuItemDelete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

	 	    menuItemHidden.setOnAction(e -> {
		 		if(isHidden) { 
		 			this.isHidden=false; 
		 			menuItemHidden.setText(FmUtil.NOT_HIDDEN);
		 		}
		 		else { 
		 			this.isHidden=true; 
		 			menuItemHidden.setText(FmUtil.MENU_HIDDEN);
		 		}
		 		listTableData(getPathText());
		 	});	 	    
	 	    menuItemDir.setOnAction(e -> {
                FmFileUtils.createDirOrFile(getRightClickedFileName(),true);
                listTableData(getRightClickedFileName());
		 	});
		 	menuItemFile.setOnAction(e -> {
		 		if(new File(getRightClickedFileName()).isFile()) {
		 			setRightClickedFileName(FmUtil.substringBeforeLast(getRightClickedFileName(), File.separator));
		 		}
		 		FmFileUtils.createDirOrFile(getRightClickedFileName(),false);
                listTableData(getRightClickedFileName());
		 	});	
		 	menuItemCopy.setOnAction(e -> {
		 		List<String> pathList = new ArrayList<String>();
		 		for (FmFileDetails sfd : (ObservableList<FmFileDetails>) tableViewMain.getSelectionModel().getSelectedItems()) {
		 			pathList.add(getCurrentDirOrFileName()+File.separator+sfd.getName());
		 		}
		 		setCopyPath(pathList);
		 	});
		 	menuItemPaste.setOnAction(e -> {
		 		FmFileUtils.copyDirOrFile(getCopyPath(),getPathText());
		 		listTableData(getPathText()); 
		 	});
		 	menuItemRename.setOnAction(e -> {
                FmFileUtils.renameDirOrFile(getRightClickedFileName());		
	            setRightClickedFileName(FmUtil.substringBeforeLast(getRightClickedFileName(), File.separator));
                listTableData(getRightClickedFileName());                
		 	});
		 	menuItemDelete.setOnAction(e -> {
		 		List<String> pathList = new ArrayList<String>();
		 		for (FmFileDetails sfd : (ObservableList<FmFileDetails>) tableViewMain.getSelectionModel().getSelectedItems()) {
		 			pathList.add(getCurrentDirOrFileName()+File.separator+sfd.getName());
		 		}
                FmFileUtils.deleteDirOrFile(pathList);
                setRightClickedFileName(FmUtil.substringBeforeLast(getCurrentDirOrFileName(), File.separator));
                listTableData(getCurrentDirOrFileName());
		 	});
 	    return contextMenu;
	}

    private Map<String, String> getPartitionNames(List<FmDeviceDetails> devicesList) {
    	Map<String, String> devicesMap =  devicesList.stream()
							.filter(dd -> dd.getStrType().equals(FmUtil.PART)
						             && !dd.getStrFSType().equals(FmUtil.SWAP)
						             && !dd.getStrFSType().equals(FmUtil.VFAT)
						  			 && !dd.getStrMountPoint().equals(FmUtil.ROOT)
									 && dd.getStrFSType().length()>0 )
							.collect(Collectors.toMap(FmDeviceDetails::getStrName, FmDeviceDetails::getStrUUID,
									(oldValue, newValue) -> oldValue, TreeMap::new
									));
    	Map<String, String> usbMap = devicesList.stream()
							.filter(dd -> dd.getStrType().equals(FmUtil.PART)
						             && dd.getStrFSType().equals(FmUtil.VFAT)
						  			 && !dd.getStrMountPoint().equals(FmUtil.ROOT))
							.collect(Collectors.toMap(FmDeviceDetails::getStrName, FmDeviceDetails::getStrUUID,
									(oldValue, newValue) -> oldValue, TreeMap::new
									));
    	Map<String, String> totalSizeMap = devicesList.stream()
				.filter(dd -> dd.getStrType().equals(FmUtil.DISK))
				.collect(Collectors.toMap(FmDeviceDetails::getStrName, FmDeviceDetails::getStrSize,
						(oldValue, newValue) -> oldValue, TreeMap::new
						));
    	
    	setDevicesSizeMap(totalSizeMap);
    	//devicesSizeMap.putAll(totalSizeMap);
    	devicesMap.putAll(usbMap);
		return devicesMap;
    }  	

	private void listTableData(List<FmFileDetails> homeDirList) {
	    //getTableView(getTableViewName()).getItems().clear();
		tableViewMain.getItems().setAll(FXCollections.observableList(homeDirList));
	}	
    
	private void listTableData(String strDirName) {
		//System.out.println(":::listTableData:::"+strDirName);
		tableViewMain.getItems().clear();
		tableViewMain.getItems().setAll(FXCollections.observableList(FmUtil.directoriesList(Paths.get(strDirName), isHidden, false)));
    	setPathText(strDirName);
	}	
	
    @FXML
    private void homeImageButtonClicked(MouseEvent event) {
    	setDefaultValues(FmUtil.HOME_WOS);
    }

	@FXML
	private void pathTextFieldOnEnter(ActionEvent ae){
    	if(getPathText().length()<1) {
    		setDefaultValues(FmUtil.ROOT);
    		
    	}else {
    		setDefaultValues(getPathText());
    	}
	}

    @FXML
    private void backButtonClicked(MouseEvent event) {
    	moveToPreviousDirectory();
    }	
    
    private void moveToPreviousDirectory() {
    	if(getPathText().length()<=5) {
    		setDefaultValues(FmUtil.ROOT);
    	}else {
    		setDefaultValues(FmUtil.substringBeforeLast(getPathText(), File.separator));
    	}
    }

    @FXML
    private void bookmarkButtonClicked(MouseEvent event) {
    	try {
    		String strDirName=getPathText();
    		File bookmarkFile = new File(FmUtil.BM_PATH);
    		bookmarkFile.getParentFile().mkdirs();
    		if(!bookmarkFile.exists()) {
		    	int vboxSize = vboxBookmarkDir.getChildren().size()-1;
		    	for (int i = 0; i <= vboxSize; i++) {
		    		vboxBookmarkDir.getChildren().remove(i);
		    		vboxSize--;i--;
				}
		    	setBookmarkSet(new TreeSet<String>());
    		}
    		bookmarkFile.createNewFile(); 
    		if(new File(strDirName).exists()) {
	    		int iBefore = getBookmarkSet().size();
	    		getBookmarkSet().add(strDirName);
	    		int iAfter = getBookmarkSet().size();
	    		if(iAfter>iBefore) {
	    			Files.write(Paths.get(FmUtil.BM_PATH),(strDirName+FmUtil.NEW_LINE).getBytes(),StandardOpenOption.APPEND);
		    		Button button = new Button(FmUtil.BM_ICON+FmUtil.substringAfterLast(strDirName,File.separator));
		    		vboxBookmarkDir.getChildren().add(button);
		    		button.setOnAction(e -> {	        		
		        		setDefaultValues(strDirName);
		        	});  
	    		}
    		}
    	}catch (IOException ioe) {
    		log.error("IOException occurred while creating bookmark file :: bookmarkButtonClicked ::",ioe);
    	}
    }	

    @FXML
    private void terminalButtonClicked(ActionEvent event){
    	//FmUtil.runProcess(getPathText(),FmUtil.TERM);
    	FmUtil.openTerminal(getPathText());
    }

 	@FXML
    private void devicesButtonClicked(MouseEvent event) {
    	Map<String,String> partitionMap = getPartitionNames(FmUtil.devicesList());
    	if(partitionMap.size() < getDevicesMap().size()) {
	    	int vboxSize = vboxDevices.getChildren().size()-1;
	    	for (int i = 0; i <= vboxSize; i++) {
	    		vboxDevices.getChildren().remove(i);
	    		vboxSize--;i--;
			}
	    	setDevicesMap(partitionMap);
    		listPartitionNames(true);
    	}
    	else if(partitionMap.size()>getDevicesMap().size()) 
    	{
	    	Iterator<Entry<String, String>> iterator = this.getDevicesMap().entrySet().iterator();
	    	 while (iterator.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) iterator.next();
	    	       if (partitionMap.containsKey(entry.getKey())) {          
	    	    	   partitionMap.remove(entry.getKey());
	    	    	   partitionMap.remove(entry.getValue());
	    	       }
	    	 }
	    	 if(partitionMap.size()>0) {
	    		 for (Map.Entry<String, String> entry : partitionMap.entrySet()) {
	    			 	//FmUtil.runProcess(entry.getKey(),FmUtil.MNT);
	    			 	FmUtil.mountDrive(entry.getKey());
	    			    this.getDevicesMap().put(entry.getKey(), entry.getValue());
	    				Button button = new Button(FmUtil.EX_HDD_ICON+entry.getKey());
	    				Button eButton = new Button(FmUtil.EJECT_ICON);
	    				eButton.setStyle("-fx-text-fill	: -vBgColorDefault");
	    				vboxDevices.getChildren().add(new HBox(45,button,eButton));
	    				deviceMountMap.put(entry.getKey(), FmUtil.MNT);
	    				button.setOnAction(e -> {
	    					if(deviceMountMap.get(entry.getKey()).equalsIgnoreCase(FmUtil.UN_MNT)) {
	    						//FmUtil.runProcess(entry.getKey(),FmUtil.MNT);
	    						FmUtil.mountDrive(entry.getKey());
	    						deviceMountMap.put(entry.getKey(),FmUtil.MNT);
	    						threadSleep(200);
	    					}
	    					setDefaultValues(FmUtil.DEV_ROOT+File.separator+entry.getValue());
	    				});
	    				eButton.setOnAction(e -> {
	    					//FmUtil.runProcess(entry.getKey(),FmUtil.UN_MNT);
	    					FmUtil.unmountDrive(entry.getKey());
	    					deviceMountMap.put(entry.getKey(),FmUtil.UN_MNT);
	    					tableViewMain.getItems().clear();
	    				});	    				
	    				threadSleep(200);
	    		 }    		 
	    	 
	    	 }
    	}
    	setDefaultValues(FmUtil.DEV_ROOT);
    }
    

 	private void threadSleep(long sleepTime) {
		try {
			//udisksctl is too slow to mount so 200 msec delay workaround :(
			TimeUnit.MILLISECONDS.sleep(sleepTime);
		} catch (InterruptedException ie) {
			log.error("InterruptedException occurred while thread sleep :: threadSleep ::",ie);
		}
 	}
 	

	@SuppressWarnings("unchecked")
	private void setTableView(String strTableViewName) {
		this.tableViewMain = (TableView<FmFileDetails>)tabPaneMain.getSelectionModel().getSelectedItem().getContent().lookup("#"+strTableViewName);
	}
	private void setTableView(TableView<FmFileDetails> tableView, String strTableViewName) {
		this.tableViewMain = tableView;
	}
	
	private void setDefaultValues(String strDirName) {
		addTabPath(FmUtil.TAB_VIEW+tabPaneMain.getSelectionModel().getSelectedItem().getId(), strDirName);
		setPathText(strDirName);
		setCurrentDirOrFileName(strDirName);
		listTableData(strDirName);
		if(strDirName.equalsIgnoreCase(FmUtil.RUBBISH_PATH)) {strDirName=FmUtil.RUBBISH;}
		setTabLabel(strDirName);		
	}
	
	private void setTabLabel(String strDirName) {
		tabPaneMain.getSelectionModel().getSelectedItem().setText(FmUtil.substringAfterLast(strDirName,File.separator));
	}
	
	private String getTabPath(String strTabName) {
		return tabPathMap.get(strTabName);
	}

	private void addTabPath(String strTabName, String strPath) {
		this.tabPathMap.put(strTabName, strPath);
	}

	private String getCurrentDirOrFileName() {
		return currentDirOrFileName;
	}

	private void setCurrentDirOrFileName(String currentDirOrFileName) {
		//System.out.println("==currentDirOrFileName==>"+currentDirOrFileName);
		this.currentDirOrFileName = currentDirOrFileName;
	}

	private String getRightClickedFileName() {
		return rightClickedFileName;
	}

	private void setRightClickedFileName(String rightClickedFileName) {
		//System.out.println("==rightClickedFileName==>"+rightClickedFileName);
		this.rightClickedFileName = rightClickedFileName;
	}

	private List<String> getCopyPath() {
		return copyPath;
	}

	private void setCopyPath(List<String> copyPath) {
		this.copyPath = copyPath;
	}

	private Set<String> getBookmarkSet() {
		return bookmarkSet;
	}

    private void setBookmarkSet(Set<String> bookmarkSet) {
		this.bookmarkSet = bookmarkSet;
	}

	private Map<String, String> getDevicesMap() {
		return devicesMap;
	}

	private void setDevicesMap(Map<String, String> devicesMap) {
		this.devicesMap = devicesMap;
	}	
	private Map<String, String> getDevicesSizeMap() {
		return devicesSizeMap;
	}

	private void setDevicesSizeMap(Map<String, String> devicesSizeMap) {
		this.devicesSizeMap = devicesSizeMap;
	}	

	private void setPathText(String strDirPath) {
		this.tf_path.setText(strDirPath);
	}
	private String getPathText() {
		return this.tf_path.getText().trim();
	}
	
	@FXML
    void minButtonClicked(MouseEvent event) {
	   Stage stage = (Stage)((ImageView)event.getSource()).getScene().getWindow();
       stage.setIconified(true);
    }
	
    @FXML
    void quitImgClicked(MouseEvent event) {
    	System.exit(0);
    }
	
    @FXML
    private void mouseDragged(MouseEvent event) {
    	Node node = (Node) event.getSource();
    	Stage stage = (Stage) node.getScene().getWindow();
    	stage.setX(event.getScreenX() - x );
    	stage.setY(event.getScreenY() - y );
    }

    @FXML
    private void mousePressed(MouseEvent event) {
    	x = event.getSceneX();
    	y = event.getSceneY();
    }
    
    @FXML
    private void aboutButtonClicked(ActionEvent event){

    	 Alert alert = new Alert(AlertType.NONE, FmUtil.EMPTY,
    			 new ButtonType(resources.getString(FmUtil.ALERT_BTN),ButtonBar.ButtonData.LEFT)); 
    	 alert.getDialogPane().setContent(new TextFlow(new Text(resources.getString(FmUtil.ALERT_ABT))));
    	 alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    	 alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
    	 alert.getDialogPane().getStylesheets().add(this.getClass().getResource(FmUtil.ALERT_CSS).toExternalForm());
    	 alert.show(); 

 	}
    

}