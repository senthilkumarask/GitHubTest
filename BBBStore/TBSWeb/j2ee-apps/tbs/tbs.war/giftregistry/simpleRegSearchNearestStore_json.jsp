<%@page contentType="application/json"%>
<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
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
										                            <label for="txtFindStore" class="marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></label>
										                            <p class="marTop_10 invite-co findStoreText">Based on your state and city or zip code, we've selected the following store for you.</p>
										                        </div>
									                          
									                </div>
                                       </div>
                                       <div class="clearfix"></div>
					                                  <div class="large-12 alpha clearfix" id="dynamicLocationCreated">
					                                        <div class="row alpha omega location marTop_20">
					                                        <div class="large-6 column  alpha omega">
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
					                                          <div class="large-6 column  omega">
					                                           <div class="changeStore-button">
				                                            	<a href="#" aria-label="<bbbl:label key="lbl_change_your_fav_store" language ="${pageContext.request.locale.language}"/>" data-href="../selfservice/store/find_store_reg.jsp"  id="nearbyStoreLink" class="changeStore"  data-reveal-ajax="true">Change Store</a>
				                                               </div>
					                                          </div>
					                                        </div>
					                                        <div class="expendContent"><a class="expend" href="javascript:void(0)"><span class="expend-plusicon">+</span>Store Info</a></div>
					                                        <div class="omega clearfix marTop_20 storePickupBenifitsWrap alpha content">
					                                            <div class="large-6 small-6 column pickupTimimngs noMar noPadLeft clearfix">
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].weekdaysStoreTimings" id="weekdaysStoreTimings"/>
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].satStoreTimings" id="satStoreTimings"/>
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].sunStoreTimings" id="sunStoreTimings"/>
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].otherTimings1" id="otherTimings1"/>
						                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].otherTimings2" id="otherTimings2"/>
						                                            <ul>
							                                            <li class="storePhone"><strong><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storePhone"/></strong></li>
							                                            <li><c:forTokens  items="${weekdaysStoreTimings}" delims="," var="item">${item}</c:forTokens></li>
							                                            <li><c:forTokens  items="${satStoreTimings}" delims="," var="item">${item}</c:forTokens></li>
							                                            <li><c:forTokens  items="${sunStoreTimings}" delims="," var="item">${item}</c:forTokens></li>
							                                            <li><c:forTokens  items="${otherTimings1}" delims="," var="item">${item}</c:forTokens></li>
							                                            <li> <c:forTokens  items="${otherTimings2}" delims="," var="item">${item}</c:forTokens></li>
						                                            </ul>						                                          
					                                                <dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeDescription" />					                                                
					                                            </div>
					                                            <div class="large-6 small-6 column omega alpha getMapDirection noPadLeft noPadRight marTop_20">
					                                            <div class="actionLink"> 
					                                                <a aria-label="get map and directions" title="Get Map &amp; Directions" onclick="javascript:externalLinks('mapquest: view maps')" data-storeid="${favStoreIDForUI}" href="#" class="viewDirectionsNew"  id="viewMapOfStore">Get Map &amp; Directions</a>
					                                            </div>
					                                            </div>
					                                            <div class="clear"></div>
					                                        </div>
					                                        <div class="clear"></div>
					                                    </div>
		                            </dsp:oparam>
		                             <dsp:oparam name="empty">
		                             <c:if test='${siteId eq TBS_BuyBuyBabySite}'>
				                		<c:set var="babyTheme" value="large-4"/>
				                	</c:if>
				                	<input type="hidden" id="storeFound" value="false" />
		                               <div class="${babyTheme} alpha clearfix pos-relative">
                         <div class="grid_4 alpha omega clearfix ">
	                            <label for="txtFindStore" class="marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></label>
	                            <p class="marTop_10 invite-co">Based on the given zip / city and state combo we could not find a store.Please try with a different combo </p>
	                        </div>
                            <input id="txtFindStore" aria-labelledby="txtFindStore" maxlength="30" name="txtFindStoreName" value="" class="cannotStartWithWhiteSpace escapeHTMLTag widthHundred" type="text" aria-required="true" placeholder="Enter City and State or Zip Code" autocomplete="off">
                            <span class="txtFindStoresearch"><span class="icon icon-search block" aria-hidden="true"></span></span>
                </div>
		                             </dsp:oparam>
		                        </dsp:droplet>
		                        

</dsp:page>