package com.bbb.internationalshipping.fulfillment.poservice;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

import com.bbb.common.BBBGenericService;

/**
 * Utility class for Files related operations.
 * 
 * 
 * 
 */
public class BBBFileUtils extends BBBGenericService {

	 private static final ApplicationLogging LOG = ClassLoggingFactory.getFactory().getLoggerForClass(BBBFileUtils.class);


	/**
	 * This returns array of file names from passed in directory.
	 * 
	 * @param directory
	 *            - Absolute path to directory from where the files to be picked
	 *            up.
	 * @return List<File> - List of file names from specified directory path
	 */
	public static List<File> getFileNamesFromDirectory(String directory) {
		List<File> files = new ArrayList<File>();
		if (!StringUtils.isEmpty(directory)){

			File dir = new File(directory);
			File[] allFiles = dir.listFiles();
			if(null != allFiles && allFiles.length > 0) {
				for (File file : allFiles) {
					if (file.isFile()) {
						files.add(file);
					}
				}
			}

		}
		return files;
	}

	

	/**
	 * Returns the File object based on directory path and file name.
	 * 
	 * @param absolutePath
	 *            - directory path
	 * @param fileName
	 *            - file name
	 * @return - File object
	 */
	public static File getFile(String absolutePath, String fileName) {
		File file = null;
		if (StringUtils.isEmpty(fileName)){
			return file;
		}
			file = new File(absolutePath, fileName);
		
		return file;
	}



	/**
	 * This method is used to move a file from source to destination(archive)
	 * 
	 * @param archivePath
	 * @param srcFile
	 * @return boolean
	 */
	public synchronized static boolean moveFile(String archivePath, File srcFile) {
		boolean success = false;
		
			String filePath = archivePath;

			if (!archivePath.endsWith("/")) {
				filePath = archivePath + "/";
			}

			File destFile = new File(filePath + srcFile.getName());
			success = copyFile(srcFile, destFile);
			delay(10);
			if (success) {
				srcFile.delete();
				delay(10);
			}
		
		return success;
	}

	/**
	 * This mathod is used to copy a content of source to destination file
	 * (archive)
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 */
	

	public static boolean copyFile(File srcFile, File destFile) {
		boolean ret = false;
		FileReader in = null;
		FileWriter out = null;
		try {
			in = new FileReader(srcFile);
			out = new FileWriter(destFile);
			int c;
			while ((c = in.read()) != -1)
				out.write(c);

			ret = true;
		} catch (IOException e) {
			 LOG.logDebug("IO exception while copying file from "+srcFile.getAbsolutePath()+ " to "+destFile.getAbsolutePath(),e);
		} finally {

			if (in != null) {
				try{
					in.close();
				} catch (IOException e) {

					 LOG.logDebug("Unable to close fileReader in copyFile method", e);
				}
			}
			if (out != null) {
				try{
					out.close();
				}catch (IOException e) {

					 LOG.logDebug("Unable to close fileWriter in copyFile method", e);
				}
			}
		}
		return ret;
	}

	/**
	 * This method is used to delay time for some seconds.
	 * 
	 */
	public static void delay(long milSec) {
		try {
			Thread.currentThread();
			Thread.sleep(10);
		} catch (InterruptedException ie) {
			LOG.logDebug("Interuppted exception ", ie);
			Thread.currentThread().interrupt();
		}

	}


	


}
