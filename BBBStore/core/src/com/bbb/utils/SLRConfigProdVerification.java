package com.bbb.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import weblogic.jdbc.common.internal.RmiDataSource;
import atg.nucleus.GenericService;
import atg.nucleus.JNDIReference;
import atg.nucleus.PropertyConfiguration;

public class SLRConfigProdVerification extends GenericService {
	private Object[] mComponentsReorged;
	private HashMap<String, String> mPropertyReorged;

	public Object[] getComponentsReorged() {
		return mComponentsReorged;
	}

	public void setComponentsReorged(Object[] pComponentsReorged) {
		mComponentsReorged = pComponentsReorged;
	}

	public HashMap<String, String> getPropertyReorged() {
		return mPropertyReorged;
	}

	public void setPropertyReorged(HashMap<String, String> pPropertyReorged) {
		mPropertyReorged = pPropertyReorged;
	}

	public void generateComponentConfigList() throws IOException {
		ArrayList<Object> componentList = new ArrayList<Object>(Arrays.asList(getComponentsReorged()));
		componentList.removeAll(Arrays.asList(new Object[] { null }));
		File file = null;
		BufferedWriter writer = null;
		try {
			file = new File(System.getProperty("user.home") + "/out_" + System.getProperty("atg.dynamo.server.name") + "_" + (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date()) + ".csv");
			writer = new BufferedWriter(new FileWriter(file, false));
			writer.write("\"Component Name\",\"Property Key\",\"Property Value\"");
			writer.newLine();
			GenericService genericComponent = null;
			String value = null;
			String[] propertyReorgedArr = null;
			for (Object component : componentList) {
				if (component instanceof GenericService) {
					genericComponent = (GenericService) component;
					propertyReorgedArr = getPropertyReorged().get(genericComponent.getAbsoluteName()) != null ? getPropertyReorged().get(genericComponent.getAbsoluteName()).split("~") : null;
					if(propertyReorgedArr != null) {
						for (String key : propertyReorgedArr) {
							value = ((PropertyConfiguration) genericComponent.getServiceConfiguration()).getProperties().getProperty(key, null);
							if (value != null) {
								writer.write("\"" + genericComponent.getAbsoluteName() + "\",\"" + key + "\",\"" + value + "\"");
								writer.newLine();
							}
						}
					}
				} else {
					if(isLoggingDebug()) 
						logDebug("cannot resolve : "+component.getClass());
				}
			}
			
			// Special case
			Object obj = null;
			obj = getPropertyReorged().containsKey("/atg/dynamo/service/jdbc/DirectJTDataSource") ? getNucleus().resolveName("/atg/dynamo/service/jdbc/DirectJTDataSource") : null;
			if (obj!=null) {
			    if (obj instanceof RmiDataSource) {
				writer.write("\"/atg/dynamo/service/jdbc/DirectJTDataSource\",\"JNDIName\",\"" + ((RmiDataSource) obj).getJNDINames()[0] + "\"");
				writer.newLine();
			    } else {
				writer.write("\"/atg/dynamo/service/jdbc/DirectJTDataSource\",\"JNDIName\",\"" + ((JNDIReference) obj).getJNDIName() + "\"");
				writer.newLine();
			    } 
			}
			obj = getPropertyReorged().containsKey("/atg/dynamo/service/jdbc/DirectJTDataSource_production") ? getNucleus().resolveName("/atg/dynamo/service/jdbc/DirectJTDataSource_production"): null;
			if (obj!=null) {
			    if (obj instanceof RmiDataSource) {
				writer.write("\"/atg/dynamo/service/jdbc/DirectJTDataSource_production\",\"JNDIName\",\"" + ((RmiDataSource) obj).getJNDINames()[0] + "\"");
				writer.newLine();
			    } else {
				writer.write("\"/atg/dynamo/service/jdbc/DirectJTDataSource_production\",\"JNDIName\",\"" + ((JNDIReference) obj).getJNDIName() + "\"");
				writer.newLine();
			    } 
			}
			obj = getPropertyReorged().containsKey("/atg/dynamo/service/jdbc/DirectJTDataSource_staging") ? getNucleus().resolveName("/atg/dynamo/service/jdbc/DirectJTDataSource_staging") : null;
			if (obj!=null) {
			    if (obj instanceof RmiDataSource) {
				writer.write("\"/atg/dynamo/service/jdbc/DirectJTDataSource_staging\",\"JNDIName\",\"" + ((RmiDataSource) obj).getJNDINames()[0] + "\"");
				writer.newLine();
			    } else {
				writer.write("\"/atg/dynamo/service/jdbc/DirectJTDataSource_staging\",\"JNDIName\",\"" + ((JNDIReference) obj).getJNDIName() + "\"");
				writer.newLine();
			    } 
			}
			obj = getPropertyReorged().containsKey("/atg/dynamo/service/jdbc/EcomAdminDataSource") ? getNucleus().resolveName("/atg/dynamo/service/jdbc/EcomAdminDataSource") : null;
			if (obj!=null) {
			    if (obj instanceof RmiDataSource) {
				writer.write("\"/atg/dynamo/service/jdbc/EcomAdminDataSource\",\"JNDIName\",\"" + ((RmiDataSource) obj).getJNDINames()[0] + "\"");
				writer.newLine();
			    } else {
				writer.write("\"/atg/dynamo/service/jdbc/EcomAdminDataSource\",\"JNDIName\",\"" + ((JNDIReference) obj).getJNDIName() + "\"");
				writer.newLine();
			    } 
			}
			obj = getPropertyReorged().containsKey("/atg/dynamo/service/jdbc/InternationalDataSource") ? getNucleus().resolveName("/atg/dynamo/service/jdbc/InternationalDataSource") : null;
			if (obj!=null) {
			    if (obj instanceof RmiDataSource) {
				writer.write("\"/atg/dynamo/service/jdbc/InternationalDataSource\",\"JNDIName\",\"" + ((RmiDataSource) obj).getJNDINames()[0] + "\"");
				writer.newLine();
			    } else {
				writer.write("\"/atg/dynamo/service/jdbc/InternationalDataSource\",\"JNDIName\",\"" + ((JNDIReference) obj).getJNDIName() + "\"");
				writer.newLine();
			    } 
			}
			obj = getPropertyReorged().containsKey("/atg/dynamo/service/jdbc/SwitchingDataSourceA") ? getNucleus().resolveName("/atg/dynamo/service/jdbc/SwitchingDataSourceA") : null;
			if (obj!=null) {
			    if (obj instanceof RmiDataSource) {
				writer.write("\"/atg/dynamo/service/jdbc/SwitchingDataSourceA\",\"JNDIName\",\"" + ((RmiDataSource) obj).getJNDINames()[0] + "\"");
				writer.newLine();
			    } else {
				writer.write("\"/atg/dynamo/service/jdbc/SwitchingDataSourceA\",\"JNDIName\",\"" + ((JNDIReference) obj).getJNDIName() + "\"");
				writer.newLine();
			    } 
			}
			obj = getPropertyReorged().containsKey("/atg/dynamo/service/jdbc/SwitchingDataSourceB") ? getNucleus().resolveName("/atg/dynamo/service/jdbc/SwitchingDataSourceB") : null;
			if (obj!=null) {
			    if (obj instanceof RmiDataSource) {
				writer.write("\"/atg/dynamo/service/jdbc/SwitchingDataSourceB\",\"JNDIName\",\"" + ((RmiDataSource) obj).getJNDINames()[0] + "\"");
				writer.newLine();
			    } else {
				writer.write("\"/atg/dynamo/service/jdbc/SwitchingDataSourceB\",\"JNDIName\",\"" + ((JNDIReference) obj).getJNDIName() + "\"");
				writer.newLine();
			    }
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
