package com.bbb.certona.parser;

import java.io.StringReader;
//import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.bbb.common.BBBGenericService;

import com.bbb.certona.vo.CertonaResonanceItemVO;
import com.bbb.certona.vo.CertonaResponseVO;
import com.bbb.framework.httpquery.parser.ResultParserIF;
import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;
import com.bbb.framework.jaxb.certona.response.Resonance;
import com.bbb.framework.jaxb.certona.response.Resonance.Schemes;
import com.bbb.framework.jaxb.certona.response.Resonance.Schemes.Scheme;
import com.bbb.framework.jaxb.certona.response.Resonance.Schemes.Scheme.Items;
import com.bbb.framework.jaxb.certona.response.Resonance.Schemes.Scheme.Items.Item;
/**
 * 
 * @author ikhan2
 *
 */
public class CertonaProductsResultParser extends BBBGenericService 
	implements ResultParserIF{

	private static volatile JAXBContext context;// NOPMD: TODO:PMD needs to be fixed
	
	/**
	 * Parse method unmarshall the xml foramt remoteResponse
	 * and populate the list of productIds into responseVO object 
	 * 
	 * @param remoteResponse
	 * @return HTTPServiceResponseIF
	 * 
	 * Working set
	 */
	public HTTPServiceResponseIF parse(String remoteResponse) {

		CertonaResponseVO responseVO = new CertonaResponseVO();
		
		CertonaResonanceItemVO resonanceItem = null;
		
		Unmarshaller unMarshaller = null;
		
		Resonance resonanceResponse = null;
		Schemes schemes = null;
		List<Scheme> schemeList = null;
		List<String> productIDsList = null;

		Map<String, CertonaResonanceItemVO> resonanceMap = new HashMap<String, CertonaResonanceItemVO>();
		
		try {
			
			if (context == null) {
				context = JAXBContext.newInstance(Resonance.class);
			}
			
			unMarshaller = context.createUnmarshaller();

			Source source = new StreamSource(new StringReader(remoteResponse));
			resonanceResponse = (Resonance) unMarshaller.unmarshal(source);
			
			if(resonanceResponse !=null){
				schemes = resonanceResponse.getSchemes();
						
			}

			if(resonanceResponse ==null || schemes ==null || schemes.getScheme() ==null ){
				responseVO.setError("ERR_EMPTY_CERTONA_RESULT");
				
			} else{
			
				schemeList = schemes.getScheme();
				Iterator<Scheme> schemeIter = schemeList.iterator();
				
				//for each scheme find the products
				while(schemeIter.hasNext()){
					
					Scheme scheme = schemeIter.next();
					
					if(scheme != null && scheme.getId()!= null){
					
						productIDsList = new ArrayList<String>();
						
						Items products = scheme.getItems();
	
						//add products as a list
						if(products != null && products.getItem() !=null ){
						
							List<Item> productList = products.getItem();
							//add item id into producIDs List
							for(int index=0; index<productList.size(); index++){
								
								Item productItem = productList.get(index);
								String productId = productItem.getProductid();
								productIDsList.add(productId);
							}
							
							resonanceItem = new CertonaResonanceItemVO ();
						
							resonanceItem.setProductIDsList(productIDsList);
						}
						
						//map of schemes & related products
						resonanceMap.put( scheme.getId(), resonanceItem);
						resonanceItem = null;
					}
				}
				
				if(resonanceResponse.getTrackingid() !=null){
					responseVO.setTrackingId(resonanceResponse.getTrackingid().toString());
				}
				responseVO.setPageId( resonanceResponse.getPageid() );
			
			}
			responseVO.setResonanceMap(resonanceMap);

		} catch (JAXBException e) {
			
			responseVO.setError("ERR_CERTONA_PARSING");
			
			logError(e.getMessage(), e);
		} catch (Exception ex ){
			
			responseVO.setError("ERR_CERTONA_PARSING");
			logError(ex.getMessage(), ex
					);
		}
		
		return responseVO;

	}	

}
