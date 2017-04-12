package com.bbb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import atg.core.util.StringUtils;

/**
 * This class contains various String utilties
 * 
 * 
 */
public final class BBBStringUtils {

  private BBBStringUtils() {

  }

  /**
   * Writes exception stack trace to String
   * 
   * @param exception
   *          The exception to write.
   * @return A string consisting of stack trace.
   */

  public static String stack2string(final Exception exception) {
    try {
      final StringWriter stringWriter = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(stringWriter);
      exception.printStackTrace(printWriter);
      
      final StringBuffer output = stringWriter.getBuffer();
      return output.toString();
    } catch (Exception e2) {
      return "bad stack to string";
    }
  }

  /**
   * This method takes in a String and will return true if all the characters
   * are numeric characters.
   * 
   * @param pString
   *          the string which may contain all numeric characters.
   * @return true if the string is numeric, false otherwise.
   */
  public static boolean isNumericString(final String pString) {
    return isNumericString(pString, "");
  }

  /**
   * Takes in a String and will return true if all the characters are numeric
   * characters. You may pass in permitted non-numeric characters in
   * "inPermittedChar" and your result will still be true.
   * 
   * @param pString
   *          the string which may contain all numeric characters.
   * @param pPermittedChar
   *          the non numeric characters that are permitted in the string.
   * @return true if the string returns the valid requirements.
   */
  public static boolean isNumericString(final String pString, final String pPermittedChar) {
    if (StringUtils.isEmpty(pString)) {
      return false;
    }
    final char[] inChars = pString.toUpperCase().toCharArray();
    for (int i = 0; i < inChars.length; ++i) {
      if (!(Character.isDigit(inChars[i]) || (pPermittedChar.indexOf(inChars[i]) >= 0))) {
        return false;
      }
    }
    return true;
  }

  /**
   * 
   * @param pStr
   * @return
   */
  public static String escapeHtmlString(String pStr) {
    return escapeHtmlString(pStr, false);
  }

  /**
   * Method used to escape HTML Strings
   * 
   * @param pStr
   * @param pEscapeAmp
   * @return
   */
  public static String escapeHtmlString(String pStr, boolean pEscapeAmp) {
    String str = null;
    if (pEscapeAmp && pStr != null && pStr.contains(" & ")) {

      str = pStr.replace("&", "&amp;");
    } else {

      str = pStr;
    }

    if (str == null)
      return null;
    StringBuilder strbuf = null;
    int len = pStr.length();
    for (int i = 0; i < len; i++) {
      char chCur = str.charAt(i);
      String strReplace;
      switch (chCur) {
      case 39: // '\''
        strReplace = "&#39;";
        break;

      case 92: // '\\'
        strReplace = "&#92;";
        break;

      case 63: // '?'
        strReplace = "&#63;";
        break;

      default:
        strReplace = null;
        break;
      }
      if (strReplace != null) {
        if (strbuf == null) {
          strbuf = new StringBuilder(len + 3);
          strbuf.insert(0, pStr, 0, i);
        }
        strbuf.append(strReplace);
        continue;
      }
      if (strbuf != null)
        strbuf.append(chCur);
    }

    if (strbuf == null)
      return pStr;
    else
      return strbuf.toString();
  }

  public static String copyFile(String srFile, String dtFile) {
	 
	InputStream in = null;
	OutputStream out = null;
    try {
      File f1 = new File(srFile);
      File f2 = new File(dtFile);

      in = new FileInputStream(f1);

      // For Append the file.
      // OutputStream out = new FileOutputStream(f2,true);

      // For Overwrite the file.
      out = new FileOutputStream(f2);

      byte[] buf = new byte[1024];
      int len = in.read(buf);
      while (len > 0) {
        if ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        } else {
          break;
        }
      }
      f1.delete();

    } catch (FileNotFoundException ex) {

      return stack2string(ex);

    } catch (IOException e) {

      return stack2string(e);
    }
    finally{
    	try{
	    	if(in!=null){
	    		in.close();
	    	}
	    	if(out!=null){
	    	    out.close();
	    	}
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
    return "File copied successfully";
  }

  /**
   * Method used to escape special character/space in product URL
   * 
   * @param displayName
   * @return resutlString
   */
  public static String formattedDisplayName(String displayName) {

    final String HYPHENS = "-";
    String resutlString = displayName;
    
    
    if(resutlString != null){
    	
    	resutlString = resutlString.replaceAll("[^a-zA-Z0-9]+", HYPHENS);
    
    	if (resutlString.startsWith(HYPHENS)) {
    		resutlString = resutlString.substring(1);
    	}
    	if (resutlString.endsWith(HYPHENS)) {
    		resutlString = resutlString.substring(0, resutlString.length() - 1);
    	}
    	resutlString = resutlString.toLowerCase();
    }
    return resutlString;
  }

}
