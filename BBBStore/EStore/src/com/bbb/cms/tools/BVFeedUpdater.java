package com.bbb.cms.tools;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.bbb.common.BBBGenericService;

public class BVFeedUpdater extends BBBGenericService{
	
	private static final String IMAGE_URL = "ImageUrl";
	private static final String PRODUCT_REVIEWS_URL = "ProductReviewsUrl";
	private static final String PRODUCT_PAGE_URL = "ProductPageUrl";
	private static final String CATEGORY_NAME = "CategoryName";
	private static final String CATEGORY_ITEM_ID = "CategoryItemId";
	private static final String CATEGORY_ID = "CategoryId";
	private static final String CATEGORY_EXTERNAL_ID = "Category_ExternalId";
	private static final String NUM_REVIEWS = "NumReviews";
	private static final String ATTR_VALUE = "Attr_Value";
	private static final String ATTR_ID = "Attr_Id";
	private static final String BRAND_EXTERNAL_ID = "Brand_ExternalId";
	private static final String BRAND_NAME = "Brand_Name";
	private static final String DESCRIPTION = "Description";
	private static final String PRODUCT_NAME = "Product_Name";
	private static final String PRODUCT_EXTERNAL_ID = "Product_ExternalId";
	private static final String SOURCE = "Source";
	private static final String REMOVED = "removed";
	private static final String PRODUCT_ID = "productId";
	Document XMLDocument = null;
	int StatisticsCount;

	@SuppressWarnings("rawtypes")
	private ArrayList mProductIdList;
	private String mMaxRating;
	private String mFileName;
	
	@SuppressWarnings("rawtypes")
	public ArrayList getProductIdList() {
		return mProductIdList;
	}

	@SuppressWarnings("rawtypes")
	public void setProductIdList(ArrayList pProductIdList) {
		this.mProductIdList = pProductIdList;
	}

	public String getMaxRating() {
		return mMaxRating;
	}

	public void setMaxRating(String pMaxRating) {
		this.mMaxRating = pMaxRating;
	}

	public String getFileName() {
		return mFileName;
	}

	public void setFileName(String pFileName) {
		this.mFileName = pFileName;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute()
	{
		Element rootElement = xmlRootTagGenerator();
		
		HashMap ProductsReviews = new HashMap();
		HashMap ProductAttributes = new HashMap();
		Random random = new Random();
	
		DecimalFormat format = new DecimalFormat("#.##");
		String genFinalRating;
		String natFinalRating;
		float genRandomRating;
		float natRandomRating;
		int maxRating = Integer.parseInt(getMaxRating());
		
		ProductAttributes.put(REMOVED, "false");
		ProductAttributes.put(SOURCE, "BedBathBeyond");
		ProductAttributes.put(PRODUCT_NAME, "Oxo Good Grips 1.5\" Pastry Brush");
		ProductAttributes.put(DESCRIPTION, "Made of natural boar bristles to retain softness and pliability, this dishwasher safe pastry brush has a sealed base to keep food and liquids from accumulating.");
		ProductAttributes.put(BRAND_NAME, "Oxo");
		ProductAttributes.put(BRAND_EXTERNAL_ID, "1hbqe910s2d9ak96nvyxn4ey8");
		ProductAttributes.put(ATTR_ID, "NAME");
		ProductAttributes.put(ATTR_VALUE, "Oxo");
		ProductAttributes.put(NUM_REVIEWS, "2");
		ProductAttributes.put(CATEGORY_ITEM_ID, "BBB_550_100");
		ProductAttributes.put(CATEGORY_EXTERNAL_ID, "BBB_550_100");
		ProductAttributes.put(CATEGORY_ID, "957417");
		ProductAttributes.put(CATEGORY_NAME, "GADGETS");
		ProductAttributes.put(PRODUCT_PAGE_URL, "http://www.bedbathandbeyond.com/Product.asp?SKU=11079156");
		ProductAttributes.put(PRODUCT_REVIEWS_URL, "http://reviews.bedbathandbeyond.com/bvstaging/2009/BBB11079156/reviews.htm");
		ProductAttributes.put(IMAGE_URL, "http://www.bedbathandbeyond.com/assets/product_images/230/719812738819.jpg");
		
		ProductAttributes.put("Gen_TotalReviewCount", "2");
		ProductAttributes.put("Gen_RatingsOnlyReviewCount", "0");
		ProductAttributes.put("Gen_RecommendedCount", "0");
		ProductAttributes.put("Gen_RatingValue_1", "4");
		ProductAttributes.put("Gen_RatingCount_1", "1");
		ProductAttributes.put("Gen_RatingValue_2", "5");
		ProductAttributes.put("Gen_RatingCount_2", "2");
		
		ProductAttributes.put("Nat_TotalReviewCount", "2");
		ProductAttributes.put("Nat_RatingsOnlyReviewCount", "0");
		ProductAttributes.put("Nat_RecommendedCount", "2");
		ProductAttributes.put("Nat_RatingValue_1", "4");
		ProductAttributes.put("Nat_RatingCount_1", "1");
		ProductAttributes.put("Nat_RatingValue_2", "5");
		ProductAttributes.put("Nat_RatingCount_2", "2");
		
		for(int i=0; i<getProductIdList().size(); i++)
		{
				
			genRandomRating = random.nextInt(maxRating) + random.nextFloat();						
			genFinalRating = format.format(genRandomRating);
			
			natRandomRating = random.nextInt(maxRating) + random.nextFloat();
			natFinalRating = format.format(natRandomRating);
			
			
			ProductAttributes.put(PRODUCT_ID, getProductIdList().get(i));
			
			ProductAttributes.put(PRODUCT_EXTERNAL_ID, getProductIdList().get(i));
			
			ProductAttributes.put("Gen_AverageOverallRating", genFinalRating);
			ProductAttributes.put("Gen_OverallRatingRange", getMaxRating());			
			
			ProductAttributes.put("Nat_AverageOverallRating", natFinalRating);
			ProductAttributes.put("Nat_OverallRatingRange", getMaxRating());
	
			ProductsReviews.put("ProductAttributes", ProductAttributes);
			
			xmlBodyGenerator(rootElement, ProductsReviews);
		}
		
		xmlFileGenerator();
		
	}	
	
	
	
	@SuppressWarnings({ })
	public Element xmlRootTagGenerator()
	{
		Element rootElement = null;
		try {	
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				
				// root elements
				XMLDocument = docBuilder.newDocument();
				rootElement = XMLDocument.createElement("Feed");
				XMLDocument.appendChild(rootElement);
				
				rootElement.setAttribute("xmlns", "http://www.bazaarvoice.com/xs/PRR/SyndicationFeed/4.3");
				rootElement.setAttribute("name", "BedBathBeyond");
				rootElement.setAttribute("extractDate", "2011-12-21T14:44:16.865-06:00");
			}catch (ParserConfigurationException e) 
			{
				logError(e.getMessage(),e);
			}
		return rootElement;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void xmlBodyGenerator(Element rootElement, HashMap ProductReview)	
	{

				HashMap ProductAttributes = (HashMap) ProductReview.get("ProductAttributes");
				
				Element product = XMLDocument.createElement("Product");
				product.setAttribute("id", (String) ProductAttributes.get(PRODUCT_ID));
				product.setAttribute(REMOVED, (String) ProductAttributes.get(REMOVED));
				rootElement.appendChild(product);

				Element source = XMLDocument.createElement("Source");
				source.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(SOURCE)));
				product.appendChild(source);
				
				Element externalId = XMLDocument.createElement("ExternalId");
				externalId.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(PRODUCT_EXTERNAL_ID)));
				product.appendChild(externalId);
				
				Element name = XMLDocument.createElement("Name");
				name.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(PRODUCT_NAME)));
				product.appendChild(name);
				
				Element description = XMLDocument.createElement("Description");
				description.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(DESCRIPTION)));
				product.appendChild(description);
				
				Element brand = XMLDocument.createElement("Brand");
				product.appendChild(brand);
				
				Element brandName = XMLDocument.createElement("Name");
				brandName.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(BRAND_NAME)));
				brand.appendChild(brandName);
				
				Element brandExternalId = XMLDocument.createElement("ExternalId");
				brandExternalId.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(BRAND_EXTERNAL_ID)));
				brand.appendChild(brandExternalId);
				
				Element attributes = XMLDocument.createElement("Attributes");
				brand.appendChild(attributes);
				
				Element attribute = XMLDocument.createElement("Attribute");
				attribute.setAttribute("id", (String) ProductAttributes.get(ATTR_ID));
				attributes.appendChild(attribute);
				
				Element value = XMLDocument.createElement("Value");
				value.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(ATTR_VALUE)));
				attribute.appendChild(value);
				
				Element numReviews = XMLDocument.createElement("NumReviews");
				numReviews.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(NUM_REVIEWS)));
				product.appendChild(numReviews);
				
				Element categoryItems = XMLDocument.createElement("CategoryItems");
				product.appendChild(categoryItems);
				
				Element categoryItem = XMLDocument.createElement("CategoryItem");
				categoryItem.setAttribute("id", (String) ProductAttributes.get(CATEGORY_ITEM_ID));
				categoryItems.appendChild(categoryItem);
				
				Element categoryExternalId = XMLDocument.createElement("ExternalId");
				categoryExternalId.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(CATEGORY_EXTERNAL_ID)));
				categoryItem.appendChild(categoryExternalId);
				
				Element categoryId = XMLDocument.createElement("CategoryId");
				categoryId.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(CATEGORY_ID)));
				categoryItem.appendChild(categoryId);
				
				Element categoryName = XMLDocument.createElement("CategoryName");
				categoryName.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(CATEGORY_NAME)));
				categoryItem.appendChild(categoryName);
				
				Element productPageUrl = XMLDocument.createElement("ProductPageUrl");
				productPageUrl.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(PRODUCT_PAGE_URL)));
				product.appendChild(productPageUrl);
				
				Element productReviewsUrl = XMLDocument.createElement("ProductReviewsUrl");
				productReviewsUrl.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(PRODUCT_REVIEWS_URL)));
				product.appendChild(productReviewsUrl);
				
				Element imageUrl = XMLDocument.createElement("ImageUrl");
				imageUrl.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(IMAGE_URL)));
				product.appendChild(imageUrl);
				
				/* ---------------------------------	*/	
				
				Element generalReviewStatistics = XMLDocument.createElement("ReviewStatistics");
				product.appendChild(generalReviewStatistics);
				
				Element nativeReviewStatistics = XMLDocument.createElement("NativeReviewStatistics");
				product.appendChild(nativeReviewStatistics);
				
				ArrayList reviewStatistics = new ArrayList();
				reviewStatistics.add(generalReviewStatistics);
				reviewStatistics.add(nativeReviewStatistics);
				
				for(StatisticsCount=0; StatisticsCount<2; StatisticsCount++)
				{
					String key;
					if(StatisticsCount == 0)
					{
						key = "Gen_";
					}
					else
					{
						key = "Nat_";
					}	
					Element averageOverallRating = XMLDocument.createElement("AverageOverallRating");
					averageOverallRating.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(key+"AverageOverallRating")));
					((Node) reviewStatistics.get(StatisticsCount)).appendChild(averageOverallRating);
					
					Element overallRatingRange = XMLDocument.createElement("OverallRatingRange");
					overallRatingRange.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(key+"OverallRatingRange")));
					((Node) reviewStatistics.get(StatisticsCount)).appendChild(overallRatingRange);
					
					Element totalReviewCount = XMLDocument.createElement("TotalReviewCount");
					totalReviewCount.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(key+"TotalReviewCount")));
					((Node) reviewStatistics.get(StatisticsCount)).appendChild(totalReviewCount);
					
					Element ratingsOnlyReviewCount = XMLDocument.createElement("RatingsOnlyReviewCount");
					ratingsOnlyReviewCount.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(key+"RatingsOnlyReviewCount")));
					((Node) reviewStatistics.get(StatisticsCount)).appendChild(ratingsOnlyReviewCount);
					
					Element recommendedCount = XMLDocument.createElement("RecommendedCount");
					recommendedCount.appendChild(XMLDocument.createTextNode((String) ProductAttributes.get(key+"RecommendedCount")));
					((Node) reviewStatistics.get(StatisticsCount)).appendChild(recommendedCount);
					
					Element averageRatingValues = XMLDocument.createElement("AverageRatingValues");
					((Node) reviewStatistics.get(StatisticsCount)).appendChild(averageRatingValues);
					
					Element ratingDistribution = XMLDocument.createElement("RatingDistribution");
					((Node) reviewStatistics.get(StatisticsCount)).appendChild(ratingDistribution);
					
					writeRatingDistribution(ratingDistribution, ProductAttributes);
					
				}
			
			/* ---------------------------------	*/
	}
	
	public void xmlFileGenerator()
	{
		try	// write the content into xml file
		{	TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(XMLDocument);
			StreamResult result = new StreamResult(new File(getFileName()));
			
			transformer.transform(domSource, result);			 
			logDebug("File saved!");
			
		}
		catch (TransformerException e) 
		{
			logError(e.getMessage(),e);
		}
 
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void writeRatingDistribution(Element RatingDistribution, HashMap ProductAttributes)
	{
		ArrayList RatingValueList = new ArrayList();
		ArrayList RatingCountList = new ArrayList();
		if(StatisticsCount == 0)
		{
			int count = Integer.parseInt((String)ProductAttributes.get("Gen_TotalReviewCount"));
			for(int i=1; i<=count; i++)
			{
				RatingValueList.add(ProductAttributes.get("Gen_RatingValue_"+i));
				RatingCountList.add(ProductAttributes.get("Gen_RatingCount_"+i));
			}
		}
		else if(StatisticsCount == 1)
		{
			int count = Integer.parseInt((String) ProductAttributes.get("Nat_TotalReviewCount"));
			for(int i=1; i<=count; i++)
			{
				RatingValueList.add(ProductAttributes.get("Nat_RatingValue_"+i));
				RatingCountList.add(ProductAttributes.get("Nat_RatingCount_"+i));
			}
		}
		
		for(int i=0; i<RatingValueList.size(); i++)
		{
			Element ratingDistributionItem = XMLDocument.createElement("RatingDistributionItem");
			RatingDistribution.appendChild(ratingDistributionItem);
			
			Element ratingValue = XMLDocument.createElement("RatingValue");
			ratingValue.appendChild(XMLDocument.createTextNode(RatingValueList.get(i).toString()));
			ratingDistributionItem.appendChild(ratingValue);
			
			Element ratingCount = XMLDocument.createElement("Count");
			ratingCount.appendChild(XMLDocument.createTextNode(RatingCountList.get(i).toString()));
			ratingDistributionItem.appendChild(ratingCount);
		}

	}
}
