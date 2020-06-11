package com.viyoriya.fm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.viyoriya.fm.model.FmDeviceDetails;

// Device is not mounted default so mount the drives before the UI starts
// This Object called once from FmMain class. 

public class FmMountDrives {

	final static Logger log = LogManager.getLogger(FmMountDrives.class);
    
    public FmMountDrives() {
    	init();
    }

    private void init() {
		Map<String,String> partitionMap = getPartitionNames(devicesList());
		for (Map.Entry<String, String> entry : partitionMap.entrySet()) {
				mountDrive(entry.getKey());
			}
	}
	
    private  void mountDrive(String strPartitionName) {
	    try {
	    	String[] cmd = { FmUtil.SH,FmUtil.PARAM_C, FmUtil.MNT_CMD+strPartitionName };
			Runtime.getRuntime().exec(cmd);
		} catch (IOException ioe) {
			log.error("IOException occurred while mount the drive :: mountDrive ::",ioe);
		}
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
    	
    	devicesMap.putAll(usbMap);
		return devicesMap;
    } 
	
    private List<FmDeviceDetails> devicesList() {
		List<FmDeviceDetails> devicesList = new ArrayList<FmDeviceDetails>();
    	try {
			Process process = new ProcessBuilder(FmUtil.SH, FmUtil.PARAM_C, FmUtil.LSBLK).start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			reader.lines().forEach(
					line -> {
						String[] strArray = line.split(FmUtil.SPACE);
						devicesList.add(new FmDeviceDetails(
								FmUtil.substringAfterLast(strArray[0],FmUtil.EQUAL).replaceAll(FmUtil.QUOTE,FmUtil.EMPTY).trim(), 
								FmUtil.substringAfterLast(strArray[1],FmUtil.EQUAL).replaceAll(FmUtil.QUOTE,FmUtil.EMPTY).trim(),
								FmUtil.substringAfterLast(strArray[2],FmUtil.EQUAL).replaceAll(FmUtil.QUOTE,FmUtil.EMPTY).trim(),
								FmUtil.substringAfterLast(strArray[3],FmUtil.EQUAL).replaceAll(FmUtil.QUOTE,FmUtil.EMPTY).trim(),
								FmUtil.substringAfterLast(strArray[4],FmUtil.EQUAL).replaceAll(FmUtil.QUOTE,FmUtil.EMPTY).trim(),
								FmUtil.substringAfterLast(strArray[5],FmUtil.EQUAL).replaceAll(FmUtil.QUOTE,FmUtil.EMPTY).trim()
								));
					}
			);
		} catch (IOException ioe) {
			log.error("IOException occurred while creating device list :: devicesList ::",ioe);
		}
    	return devicesList;
    }

}
