package com.viyoriya.fm.model;

import javafx.beans.property.SimpleStringProperty;

public class FmFileDetails {
	
	private SimpleStringProperty icon,name,description,size,created,modified;
	//private ImageView imageView;

    
    public FmFileDetails(String strIcon,String strName,String strDesc, String strSize, String strCreationTime, String strModifiedTime) {
    	//imageView = image;
    	icon = new SimpleStringProperty(strIcon);
    	name = new SimpleStringProperty(strName);
    	description = new SimpleStringProperty(strDesc);
    	size = new SimpleStringProperty(strSize);
    	created = new SimpleStringProperty(strCreationTime);
    	modified = new SimpleStringProperty(strModifiedTime);
    }
/*
	public ImageView getImageView() {
		return imageView;
	}
	public void setImageView(ImageView image) {
		imageView=image;
	}
*/
	public String getIcon() {
		return icon.get();
	}
	public void setIcon(String strIcon) {
		icon.set(strIcon);
	}
    
	public String getName() {
		return name.get();
	}
	public void setName(String strName) {
		name.set(strName);
	}
	public String getDescription() {
		return description.get();
	}
	public void setDescription(String strDescription) {
		description.set(strDescription);
	}
	public String getSize() {
		return size.get();
	}
	public void setSize(String strSize) {
		size.set(strSize); 
	}
	public String getCreated() {
		return created.get();
	}
	public void setCreated(String strCreated) {
		created.set(strCreated);
	}   	
	public String getModified() {
		return modified.get();
	}
	public void setModified(String strModified) {
		modified.set(strModified);
	}    

}
