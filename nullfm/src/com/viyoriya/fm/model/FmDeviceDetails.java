package com.viyoriya.fm.model;

public class FmDeviceDetails {
	String strName,strType,strFSType,strSize,strMountPoint,strUUID;

	public FmDeviceDetails(String strName, String strType, String strFSType, String strSize, String strMountPoint,
			String strUUID) {
		this.strName = strName;
		this.strType = strType;
		this.strFSType = strFSType;
		this.strSize = strSize;
		this.strMountPoint = strMountPoint;
		this.strUUID = strUUID;
	}

	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getStrFSType() {
		return strFSType;
	}

	public void setStrFSType(String strFSType) {
		this.strFSType = strFSType;
	}

	public String getStrSize() {
		return strSize;
	}

	public void setStrSize(String strSize) {
		this.strSize = strSize;
	}

	public String getStrMountPoint() {
		return strMountPoint;
	}

	public void setStrMountPoint(String strMountPoint) {
		this.strMountPoint = strMountPoint;
	}

	public String getStrUUID() {
		return strUUID;
	}

	public void setStrUUID(String strUUID) {
		this.strUUID = strUUID;
	}

	@Override
	public String toString() {
		return "DevicesDetails [strName=" + strName + ", strType=" + strType + ", strFSType=" + strFSType + ", strSize="
				+ strSize + ", strMountPoint=" + strMountPoint + ", strUUID=" + strUUID + "]";
	}
	
}
