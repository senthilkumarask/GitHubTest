/**
 * 
 */
package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.utils.BBBUtility;

/**
 * @author iteggi
 *
 */
public class CategoryContainerVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String categoryId;
	private List<String> subCategories;
	private int flipTime; 
	private String title;
	private String noOfCatInCatContainer;
	private List<CategoryVO> categoryInCatContainer;
	
	/**
	 * @return the flipTime
	 */
	public int getFlipTime() {
		return flipTime;
	}
	/**
	 * @param flipTime the flipTime to set
	 */
	public void setFlipTime(int flipTime) {
		this.flipTime = flipTime;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the noOfCatInCatContainer
	 */
	public String getNoOfCatInCatContainer() {
		return noOfCatInCatContainer;
	}
	/**
	 * @param noOfCatInCatContainer the noOfCatInCatContainer to set
	 */
	public void setNoOfCatInCatContainer(String noOfCatInCatContainer) {
		this.noOfCatInCatContainer = noOfCatInCatContainer;
	}
	/**
	 * @return the categoryInCatContainer
	 */
	public List<CategoryVO> getCategoryInCatContainer() {
		return categoryInCatContainer;
	}
	/**
	 * @param categoryInCatContainer the categoryInCatContainer to set
	 */
	public void setCategoryInCatContainer(List<CategoryVO> categoryInCatContainer) {
		this.categoryInCatContainer = categoryInCatContainer;
	}
	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}
	/**
	 * @param pCategoryId the categoryId to set
	 */
	public void setCategoryId(String pCategoryId) {
		categoryId = pCategoryId;
	}
	/**
	 * @return the subCategories
	 */
	public List<String> getSubCategories() {
		return subCategories;
	}
	/**
	 * @param pSubCategories the subCategories to set
	 */
	public void setSubCategories(List<String> pSubCategories) {
		subCategories = pSubCategories;
	}
	public String toString(){
		StringBuffer toString=new StringBuffer(" Category Container VO \n ");
		toString.append(" Category Id ").append(categoryId).append("\n");
		toString.append(" Flip Time ").append(flipTime).append("\n");
		toString.append(" Title ").append(title).append("\n");
		toString.append(" No Of Cat In CatContainer ").append(noOfCatInCatContainer).append("\n");
		if(subCategories!=null && !subCategories.isEmpty()){
			int i=0;
			toString.append(" Sub Category IDs \n ");
			for(String subCat:subCategories){
				toString.append("   ").append(++i+") ").append(subCat);
			}
		}
		else{
			toString.append("Sub Categories List is null ").append("\n");
		}
		if (!BBBUtility.isListEmpty(categoryInCatContainer)) {
			int i=0;
			toString.append(" Categories in Category Container VO \n ");
			for (CategoryVO categoryVO : categoryInCatContainer) {
				toString.append("   ").append(++i+") ").append(categoryVO.toString());
			}
		} else {
			toString.append("No Categories in Category Container VO ").append("\n");
		}
		return toString.toString();
	}
}
