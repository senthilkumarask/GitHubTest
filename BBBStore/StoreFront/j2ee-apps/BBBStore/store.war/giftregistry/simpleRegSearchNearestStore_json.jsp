<%@page contentType="application/json"%>
<dsp:page>
	<dsp:droplet
		name="/com/bbb/simplifyRegistry/droplet/SimpleRegNearestStoreDroplet">
		<dsp:param name="origin" param="origin" />
		<dsp:oparam name="output">
		</dsp:oparam>
	</dsp:droplet>
	 <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
		                            <dsp:param name="searchString" param="origin"/>
		                            <dsp:oparam name="output">
		                            <c:set var="favStoreIDForUI" ><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeId"/></c:set>
		                           <input type="hidden" name="favStoreIDForUI" id="favStoreIDForUIres" value="${favStoreIDForUI}"/>
		                            <div id="" class="grid_4 alpha omega  form clearfix finStrBtmBorder">
									                    <div class="grid_4 alpha clearfix">
									                         <div class="grid_4 alpha omega clearfix">
									                        
										                            <label for="txtFindStore" class="formTitle marBottom_5"><bbbl:label key="lbl_reg_favStore" language="${pageContext.request.locale.language}" /></label>
										                            <p class="marTop_10 findStoreText"> <bbbl:label key="lbl_reg_favStoreSelected" language ="${pageContext.request.locale.language}"/></p>
										                        </div>
									                          
									                </div>
                                       </div>
					                                  <div class="grid_4 alpha clearfix">
					                                        <div class="grid_4 alpha omega location marTop_20 marBottom_10">
					                                        <div class="grid_2 alpha omega">
					                                            <h3 class="storeName"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeName"/></h3>
					                                            <div class="address">
					                                                <div class="street"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].address"/></div>
					                                                <div>
					                                                    <span class="city"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].city"/>,</span>
					                                                    <span class="state"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].state"/></span>
					                                                    <span class="zip"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].postalCode"/></span>
					                                                    <dsp:getvalueof var="contactFlag" param="StoreDetailsWrapper.storeDetails[0].contactFlag"/>
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
					                                        <div class="expendContent"><a class="expend" href="javascript:void(0)" aria-label="Store Info expand"><span aria-hidden='true' class="expend-plusicon">+</span>Store Info</a></div>
					                                        <div class="grid_4 omega clearfix marTop_20 storePickupBenifitsWrap alpha content">
					                                            <p class="grid_2 pickupTimimngs noMar clearfix">
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].weekdaysStoreTimings" id="weekdaysStoreTimings"/>
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].satStoreTimings" id="satStoreTimings"/>
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].sunStoreTimings" id="sunStoreTimings"/>
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].otherTimings1" id="otherTimings1"/>
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].otherTimings2" id="otherTimings2"/>
						                                            <strong><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storePhone"/></strong><br/>
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
					                                                <dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeDescription" />
					                                                
					                                            </p>
					                                            <div class="grid_2 omega alpha marLeft_20 marTop_25">
					                                            <div class="actionLink">
					                                                <a href="#viewDirections"  data-storeid="${favStoreIDForUI}" class="viewDirectionsNew" aria-label="map and directions" title="Map & Directions">Map & Directions</a>
					                                            </div>
					                                            </div>
					                                            <div class="clear"></div>
					                                        </div>
					                                        <div class="clear"></div>
					                                    </div>
		                            </dsp:oparam>
		                             <dsp:oparam name="empty">
									 <input id="storeFound" value="false" type="hidden">
		                               <div class="grid_4 alpha clearfix">
                         <div class="grid_4 alpha omega clearfix">
	                            <label for="txtFindStore" class="formTitle marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></label>
	                            <p class="marTop_10 findStoreText"><bbbl:label key="lbl_reg_selectFavStore" language ="${pageContext.request.locale.language}"/> </p>
	                        </div>
							<div class="hidden" >
				                                            	<input id="changStrBtn" tabindex="9" type="button" name="changeStore" value="Change Store" class="changeStore btnSecondary button-Med" role="button" aria-label="<bbbl:label key="lbl_change_your_fav_store" language ="${pageContext.request.locale.language}"/>" />
															 </div>
                            <input id="txtFindStore"  maxlength="30" name="txtFindStoreName" value="" class="cannotStartWithWhiteSpace escapeHTMLTag widthHundred" type="text" aria-required="true" placeholder="<bbbl:label key="lbl_reg_ph_favstore" language ="${pageContext.request.locale.language}"/>" autocomplete="off">
                            <span class="txtFindStoresearch"><span class="icon icon-search block" aria-label="search store" aria-hidden="false"></span></span>
                </div>
		                             </dsp:oparam>
		                        </dsp:droplet>
		                        

</dsp:page>