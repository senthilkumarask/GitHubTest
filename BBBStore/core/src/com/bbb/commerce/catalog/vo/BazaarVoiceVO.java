package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.util.List;

public class BazaarVoiceVO  implements Serializable{

  /**
	 * 
	 */
private static final long serialVersionUID = 1L;

private List<BazaarVoiceProductVO> bazaarVoiceProduct;

 /**
   * 
   */
 public BazaarVoiceVO() {
    super();
  }

  
  /**
   * @param pBazaarVoiceProduct
   */
  public BazaarVoiceVO(List<BazaarVoiceProductVO> pBazaarVoiceProduct) {
    super();
    bazaarVoiceProduct = pBazaarVoiceProduct;
  }

  /**
   * @return the bazaarVoiceProduct
   */
  public List<BazaarVoiceProductVO> getBazaarVoiceProduct() {
    return bazaarVoiceProduct;
  }

  /**
   * @param pBazaarVoiceProduct the bazaarVoiceProduct to set
   */
  public void setBazaarVoiceProduct(List<BazaarVoiceProductVO> pBazaarVoiceProduct) {
    bazaarVoiceProduct = pBazaarVoiceProduct;
  }
  
}
