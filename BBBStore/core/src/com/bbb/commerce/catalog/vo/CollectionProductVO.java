package com.bbb.commerce.catalog.vo;

import java.util.List;

import atg.core.util.StringUtils;
/**
 * 
 * @author njai13
 *
 */
public class CollectionProductVO extends ProductVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean leadSKU;
	private List<ProductVO> childProducts;
	private boolean showImagesInCollection;
	private String collectionThumbnail;
	private List<String> collectionRollUp;
	private String omnitureCollectionEvar29;
	public CollectionProductVO(ProductVO productVO){
		super(productVO);
		
	}
	
	public CollectionProductVO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the leadSKU
	 */
	public Boolean getLeadSKU() {
		return leadSKU;
	}

	/**
	 * @param leadSKU the leadSKU to set
	 */
	public void setLeadSKU(Boolean leadSKU) {
		this.leadSKU = leadSKU;
	}

	/**
	 * @return the childProducts
	 */
	public List<ProductVO> getChildProducts() {
		return childProducts;
	}

	/**
	 * @param childProducts the childProducts to set
	 */
	public void setChildProducts(List<ProductVO> childProducts) {
		this.childProducts = childProducts;
	}

	/**
	 * @return the showImagesInCollection
	 */
	public boolean isShowImagesInCollection() {
		return showImagesInCollection;
	}

	/**
	 * @param showImagesInCollection the showImagesInCollection to set
	 */
	public void setShowImagesInCollection(boolean showImagesInCollection) {
		this.showImagesInCollection = showImagesInCollection;
	}
	/**
	 * @return the collectionThumbnail
	 */
	public String getCollectionThumbnail() {
		return collectionThumbnail;
	}
	/**
	 * @param collectionThumbnail the collectionThumbnail to set
	 */
	public void setCollectionThumbnail(String collectionThumbnail) {
		this.collectionThumbnail = collectionThumbnail;
	}

	/**
	 * @return the collectionRollUp
	 */
	public List<String> getCollectionRollUp() {
		return collectionRollUp;
	}

	/**
	 * @param collectionRollUp the collectionRollUp to set
	 */
	public void setCollectionRollUp(List<String> collectionRollUp) {
		this.collectionRollUp = collectionRollUp;
	}




	public String toString(){
		StringBuffer toString=new StringBuffer(" Product is a collection or a lead Product.Following are its Details \n ");
		toString.append("Is product a lead Sku ").append(leadSKU).append("\n");
		if(childProducts!=null && childProducts.isEmpty()){
			toString.append("\n********Child Products in the collection*********** ").append(" \n");
			int i=0;
			for(ProductVO childProd:childProducts){
				toString.append(++i+") ")
				.append(childProd!=null?childProd.toString():" NULL ").append("\n");
			}
		}
		else{
			toString.append(" No Child Products for the Collection ");
		}
		toString.append(" Collection Thumbnail value ").append(StringUtils.isEmpty(collectionThumbnail)?"NULL":collectionThumbnail).append("\n")
		.append(" Show Images In Collection flag value ").append(showImagesInCollection).append("\n");
		if(collectionRollUp!=null && collectionRollUp.isEmpty()){
			toString.append("\n********Collection Roll Up*********** ").append(" \n");
			int i=0;
			for(String rollUp:collectionRollUp){
				toString.append(++i+") ")
				.append(!StringUtils.isEmpty(rollUp)?rollUp:" NULL ").append(" \n");
			}
		}
		else{
			toString.append(" No Roll Up for the Collection ");
		}
		return toString.toString()+super.toString();
	}

	public String getOmnitureCollectionEvar29() {
		return omnitureCollectionEvar29;
	}

	public void setOmnitureCollectionEvar29(String omnitureCollectionEvar29) {
		this.omnitureCollectionEvar29 = omnitureCollectionEvar29;
	}
}
