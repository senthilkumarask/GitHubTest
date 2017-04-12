package com.bbb.commerce.order.droplet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.xml.bind.DatatypeConverter;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.constants.TBSConstants;

public class TBSBarcodeGeneratorDroplet extends DynamoServlet {
	
	/**
	 * mPricingTools
	 */
	private BBBPricingTools mPricingTools;

	/**
	 * @return the pricingTools
	 */
	public BBBPricingTools getPricingTools() {
		return mPricingTools;
	}

	/**
	 * @param pPricingTools the pricingTools to set
	 */
	public void setPricingTools(BBBPricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}

	/**
	 * This service method is used to generate the bar-code for the orderId and the order total using code39.
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("TBSBarcodeGeneratorDroplet ::  service() :: START");
		String orderId = (String) pRequest.getLocalParameter(TBSConstants.ORDERID);
		Double orderTotal = (Double) pRequest.getLocalParameter(TBSConstants.ORDER_TOTAL);
		
		if(StringUtils.isBlank(orderId) && orderTotal == null){
			pRequest.setParameter("errorMsg", "OrderId and OrderTotal are not valid to generate the barcode.");
			pRequest.serviceLocalParameter(TBSConstants.ERROR, pRequest, pResponse);
			return;
		}
		
		if(!StringUtils.isBlank(orderId)){
			vlogDebug("generating barcode for order id .");
			//invoking barcode creation method for orderId
			String orderIdBarcodeImg = createBarCode39(orderId);
			pRequest.setParameter(TBSConstants.ORDERID_BARCODE_IMG, orderIdBarcodeImg);
		}
		if(orderTotal != null){
			vlogDebug("generating barcode for order total .");
			orderTotal = getPricingTools().round(orderTotal);
			String barcodeTotal = orderTotal.toString();
			if (barcodeTotal.contains(".")) {
				String afterDecimal = barcodeTotal.substring(barcodeTotal.indexOf(".")+1, barcodeTotal.length());
				if(afterDecimal.length() == 1){
					barcodeTotal = barcodeTotal + "0";
				}
				barcodeTotal = barcodeTotal.replace(".", "");
			} 
			//invoking barcode creation method for orderId
			String orderTotalBarcodeImg = createBarCode39(barcodeTotal);
			pRequest.setParameter(TBSConstants.ORDER_TOTAL_BARCODE_IMG, orderTotalBarcodeImg);
		}
		pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
		vlogDebug("TBSBarcodeGeneratorDroplet ::  service() :: END");
	}
	
	/**
	 * This method is used to generate base 64 binary bar-code image using code 39.
	 * @param fileName
	 * @return
	 */
	public String createBarCode39(String fileName) {
		vlogDebug("TBSBarcodeGeneratorDroplet ::  createBarCode39() :: START");
		String imageString = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			Code39Bean bean39 = new Code39Bean();
			BitmapCanvasProvider provider = new BitmapCanvasProvider(300, BufferedImage.TYPE_BYTE_GRAY, true, 0);
			bean39.generateBarcode(provider, fileName);
		    provider.finish();
		    
		    BufferedImage barcodeImage = provider.getBufferedImage();
		    ImageIO.write( barcodeImage, "jpg", baos );
		    baos.flush();
		    byte[] imageInByteArray = baos.toByteArray();
		    imageString = DatatypeConverter.printBase64Binary(imageInByteArray);
		    
			vlogDebug("generated barcode image :: "+imageString);
		} catch (IOException e){
			vlogError("Exception occurred while generating the barcode image. "+e);
		}
        vlogDebug("TBSBarcodeGeneratorDroplet ::  createBarCode39() :: END");
		return imageString;
    }

}
