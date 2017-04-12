/**
 * 
 */
package atg.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * @author vagra4
 * 
 */
public class NotZero extends DynamoServlet {

	private static final String PRICE = "Price";

	private static final String OUTPUT = "output";

	private static final String EMPTY = "empty";

	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {

		Double price = (Double) pReq.getLocalParameter(PRICE); 

		pReq.serviceLocalParameter(
				(price != null && Double.valueOf(price) > 0) ? OUTPUT : EMPTY,
				pReq, pRes);

	}

}
