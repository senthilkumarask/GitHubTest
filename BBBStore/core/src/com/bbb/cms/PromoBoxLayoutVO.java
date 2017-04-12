package com.bbb.cms;

import java.io.Serializable;



public class PromoBoxLayoutVO implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private PromoBoxVO mPromoBoxFirstVOList;
  private PromoBoxVO mPromoBoxSecondVOList;
  private PromoBoxVO mPromoBoxThirdVOList;
  /**
   * 
   */
  public PromoBoxLayoutVO() {
    super();
  }

  
  /**
   * @param pPromoBoxFirstVOList
   * @param pPromoBoxSecondVOList
   * @param pPromoBoxThirdVOList
   */
  public PromoBoxLayoutVO(PromoBoxVO pPromoBoxFirstVOList, PromoBoxVO pPromoBoxSecondVOList,
      PromoBoxVO pPromoBoxThirdVOList) {
    super();
    mPromoBoxFirstVOList = pPromoBoxFirstVOList;
    mPromoBoxSecondVOList = pPromoBoxSecondVOList;
    mPromoBoxThirdVOList = pPromoBoxThirdVOList;
  }


  /**
   * @return the promoBoxFirstVOList
   */
  public PromoBoxVO getPromoBoxFirstVOList() {
    return mPromoBoxFirstVOList;
  }

  /**
   * @param pPromoBoxFirstVOList the promoBoxFirstVOList to set
   */
  public void setPromoBoxFirstVOList(PromoBoxVO pPromoBoxFirstVOList) {
    mPromoBoxFirstVOList = pPromoBoxFirstVOList;
  }

  /**
   * @return the promoBoxSecondVOList
   */
  public PromoBoxVO getPromoBoxSecondVOList() {
    return mPromoBoxSecondVOList;
  }

  /**
   * @param pPromoBoxSecondVOList the promoBoxSecondVOList to set
   */
  public void setPromoBoxSecondVOList(PromoBoxVO pPromoBoxSecondVOList) {
    mPromoBoxSecondVOList = pPromoBoxSecondVOList;
  }

  /**
   * @return the promoBoxThirdVOList
   */
  public PromoBoxVO getPromoBoxThirdVOList() {
    return mPromoBoxThirdVOList;
  }

  /**
   * @param pPromoBoxThirdVOList the promoBoxThirdVOList to set
   */
  public void setPromoBoxThirdVOList(PromoBoxVO pPromoBoxThirdVOList) {
    mPromoBoxThirdVOList = pPromoBoxThirdVOList;
  }
  
  public boolean isEmpty(){
	  return (getPromoBoxFirstVOList().isEmpty());
  }
  
  

public String toString(){
	  StringBuffer toString=new StringBuffer("  Details of PromoBoxLayoutVO \n ");
	  toString.append("1)First Promo Box \n").append(mPromoBoxFirstVOList!=null ?mPromoBoxFirstVOList.toString():"Null").append("\n");
	  toString.append("2)Second Promo Box \n").append(mPromoBoxSecondVOList!=null ?mPromoBoxSecondVOList.toString():"Null").append("\n");
	  toString.append("3)Third Promo Box \n").append(mPromoBoxThirdVOList!=null ?mPromoBoxThirdVOList.toString():"Null").append("\n");
	  return toString.toString();
  }
  
  
 }
