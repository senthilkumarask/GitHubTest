<dsp:page>
	<dsp:getvalueof var="favouriteStoreId" param="favouriteStoreId"/>
	<input type="hidden" name="favStoreIDForUI" id="favStoreIDForUIres" value="${favouriteStoreId}"/>
        <div id="" class="grid_4 alpha omega clearfix changeStoreStep_Preview">
            
			
		                           
                <div class="clear"></div>
				          <c:choose>
				                <c:when test="${!empty favouriteStoreId}"> 
				                    <div id="" class="grid_4 alpha omega form clearfix">
				                    <div class="grid_4 alpha clearfix">
				                        <div class="clear"></div>
										<div class="grid_4 alpha clearfix">
				                             <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
				                                <dsp:param name="storeId" value="${favouriteStoreId}"/>
				                                <dsp:param name="searchType" value="2"/>
				                                <dsp:oparam name="output">
				                                    <%-- do not show this div "registryForm" if there is no fav store currently selected by the user --%>
				                                    <c:set var="favStoreIDForUI" ><dsp:valueof param="storeId"/></c:set>
													   <div id="" class="grid_4 alpha omega  form clearfix finStrBtmBorder">
                                                                                  <div class="grid_4 alpha clearfix">
                                                                                       <div class="grid_4 alpha omega clearfix">
                                                                                      
                                                                                                 <label for="txtFindStore" class="formTitle marBottom_5"><bbbl:label key="lbl_reg_favStore" language="${pageContext.request.locale.language}" /></label>
                                                                                                 <p class="marTop_10 findStoreText"> <bbbl:label key="lbl_reg_favStoreSelected" language ="${pageContext.request.locale.language}"/></p>
                                                                                             </div>
                                                                                        
                                                                              </div>
                                       </div>

				                                        <div class="grid_4 alpha omega location marTop_20 marBottom_10">
				                                        <div class="grid_2 alpha omega">
				                                            <h3 class="storeName"><dsp:valueof param="StoreDetails.storeName"/></h3>
				                                            <div class="address">
				                                                <div class="street"><dsp:valueof param="StoreDetails.address"/></div>
				                                                <div>
				                                                    <span class="city"><dsp:valueof param="StoreDetails.city"/>,</span>
				                                                    <span class="state"><dsp:valueof param="StoreDetails.state"/></span>
				                                                    <span class="zip"><dsp:valueof param="StoreDetails.postalCode"/></span>
				                                                    <dsp:getvalueof var="contactFlag" param="StoreDetails.contactFlag"/>
				                                                </div>
				                                            </div>
				                                            
				                                            <div class="clear"></div>
				                                            </div>
				                                            <div class="grid_2 omega">
					                                          <div class="">
				                                            	<input id="changStrBtn" tabindex="9" type="button" name="changeStore" value="Change Store" class="changeStore btnSecondary button-Med" role="button" aria-label="<bbbl:label key="lbl_change_your_fav_store" language ="${pageContext.request.locale.language}"/>"/>
															 </div>
					                                          </div>
				                                        </div>
				                                        <div class="expendContent"><a class="expend" href="javascript:void(0)"><span class="expend-plusicon">+</span>Store Info</a></div>
				                                        <div class="grid_4 omega clearfix marTop_20 storePickupBenifitsWrap alpha content">
				                                            <p class="grid_2 pickupTimimngs noMar clearfix">
					                                            <dsp:getvalueof param="StoreDetails.weekdaysStoreTimings" id="weekdaysStoreTimings"/>
					                                            <dsp:getvalueof param="StoreDetails.satStoreTimings" id="satStoreTimings"/>
					                                            <dsp:getvalueof param="StoreDetails.sunStoreTimings" id="sunStoreTimings"/>
					                                            <dsp:getvalueof param="StoreDetails.otherTimings1" id="otherTimings1"/>
					                                            <dsp:getvalueof param="StoreDetails.otherTimings2" id="otherTimings2"/>
				                                                <strong><dsp:valueof param="StoreDetails.storePhone"/></strong>
				                                                <br/>
					                                        	<c:forTokens  items="${weekdaysStoreTimings}" delims="," var="item">${item}</c:forTokens>
																<br/>
																<c:forTokens  items="${satStoreTimings}" delims="," var="item">${item}</c:forTokens>
																<br/>
																<c:forTokens  items="${sunStoreTimings}" delims="," var="item">${item}</c:forTokens>
						                                        <br/>
																<c:forTokens  items="${otherTimings1}" delims="," var="item">${item}</c:forTokens>
						                                        <br/>
																<c:forTokens  items="${otherTimings2}" delims="," var="item">${item}</c:forTokens>
						                                        <br/>
																<c:forTokens  items="${StoreDetails.storeDescription}" delims="," var="item">${item}</c:forTokens>
						                                        
						                                        
						                                    </p>
				                                            <div class="grid_2 omega alpha marLeft_20 marTop_25">
					                                            <div class="actionLink">
					                                                <a href="#viewDirections"  data-storeid="${favouriteStoreId}" class="viewDirectionsNew" title="Map & Directions">Map & Directions</a>
					                                            </div>
					                                            </div>
				                                            <div class="clear"></div>
				                                        </div>
				                                        <div class="clear"></div>
				                                	</dsp:oparam>
				                            	</dsp:droplet>
										</div>
				                      	<div class="clear"></div>
				                    	</div>
				                    	<div class="clear"></div>
				                	 </div>
				               		 </c:when>
					                 
				                </c:choose>
               
                  <c:if test="${empty favStoreIDForUI}">
                   <div id="" class="grid_4 alpha omega  form clearfix">
                   <div class="grid_4 alpha clearfix">
                         <div class="grid_4 alpha omega clearfix">
	                            <label for="txtFindStore" class="formTitle marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></label>
	                            <p class="marTop_10 findStoreText"><bbbl:label key="lbl_reg_selectFavStore" language ="${pageContext.request.locale.language}"/></p>
	                        </div>
                            <input id="txtFindStore" aria-labelledby="txtFindStore" maxlength="30" name="txtFindStoreName" value="" class="${isRequired} cannotStartWithWhiteSpace escapeHTMLTag widthHundred" type="text" aria-required="true" placeholder="<bbbl:label key="lbl_reg_ph_favstore" language ="${pageContext.request.locale.language}"/>" autocomplete="off">
                            <span class="txtFindStoresearch"><span class="icon icon-search block" aria-label="search store" aria-hidden="false"></span></span>
                </div>
                </div>
                
                </c:if>
                
               
                
      
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
        
        <div class="clear"></div>
    <div class="clear"></div>
</dsp:page>