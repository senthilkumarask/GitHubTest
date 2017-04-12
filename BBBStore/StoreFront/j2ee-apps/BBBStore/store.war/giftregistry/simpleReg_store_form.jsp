<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/account/droplet/BBBConfigKeysDroplet" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />

    <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>
    <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
    <dsp:getvalueof var="inputListMap" param="inputListMap"/>
	
	
	<c:choose>
		<c:when test="${event == 'BA1' }">
		<div id="" class="grid_8 alpha omega clearfix changeStoreStep_Preview">
		 <div class="clearfix grid_8 alpha omega">
		</c:when>
		<c:otherwise>
		<div id="" class="grid_4 alpha omega clearfix changeStoreStep_Preview">
		<div class="clearfix grid_4 alpha omega">
		</c:otherwise>
	</c:choose>
	
	
	 <c:if test="${inputListMap['favoriteStore'].isDisplayonForm}">
            <div class="clearfix grid_4 alpha omega favoriteStore">
                <div class="clear"></div>
     
         <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['favoriteStore'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
    <c:set var="BuyBuyBabySite">
        <bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>

                    <c:choose>
                    <c:when test="${currentSiteId eq 'BedBathUS'}">
                        <c:set var="radius_default_selected"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set>
                    </c:when>
                    <c:when test="${currentSiteId eq 'BuyBuyBaby'}">
                        <c:set var="radius_default_selected"> <bbbc:config key="radius_default_baby" configName="MapQuestStoreType" /></c:set>
                    </c:when>
                    <c:when test="${currentSiteId eq 'BedBathCanada'}">
                        <c:set var="radius_default_selected"> <bbbc:config key="radius_default_ca" configName="MapQuestStoreType" /></c:set>
                    </c:when>
                </c:choose>
    <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
	<dsp:getvalueof var="storeIDFromBean" bean="GiftRegistryFormHandler.registryVO.prefStoreNum" />
	<c:if test="${empty storeIDFromBean}">
		<dsp:getvalueof var="storeIDFromBean" value="${regVO.prefStoreNum}"/>
	</c:if>
    <c:set var="existingfavouriteStoreId"><dsp:valueof value="${storeIDFromBean}"/></c:set>
	<c:choose>
	   <c:when  test="${ (empty existingfavouriteStoreId)}">
	   <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" bean="Profile.userSiteItems"/>
        <dsp:param name="elementName" value="sites"/>
        <dsp:oparam name="output">
            <dsp:getvalueof id="key" param="key"/>
            <c:if test="${siteId eq key}">
                <dsp:getvalueof id="favouriteStoreId" param="sites.favouriteStoreId" scope="session"/>
            </c:if>
        </dsp:oparam>
    </dsp:droplet>
    </c:when>
	<c:otherwise>
	<c:choose>
	<c:when test="${empty storeIDFromBean}">
		<c:set var="favouriteStoreId"><dsp:valueof value="${regVO.prefStoreNum}"/></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="favouriteStoreId"><dsp:valueof value="${storeIDFromBean}"/></c:set>
	</c:otherwise>
	</c:choose>
	</c:otherwise>
	</c:choose>
	<c:set var="zipFromshipping"></c:set>
	<c:set var="favStoreIDForUI" value="${favouriteStoreId}"></c:set>
	
	<c:if test="${empty favouriteStoreId}">
	  <c:set var="zipFromshipping"><dsp:valueof bean="Profile.shippingAddress.postalCode" /></c:set>
	</c:if>
	
				          <c:choose>
				                <c:when test="${!empty favouriteStoreId}"> 
				                    <div id="" class="grid_4 alpha omega form clearfix">
				                    <div class="grid_4 alpha clearfix">
				                        <div class="grid_4 alpha omega clearfix finStrBtmBorder">
				                            <h2 class="formTitle marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></h2>
				                            <p class="marTop_5"><bbbl:label key="lbl_registry_store_pref_store" language ="${pageContext.request.locale.language}"/></p>
				                        </div>
				                        <div class="clear"></div>
										<div class="grid_4 alpha clearfix">
				                             <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
				                                <dsp:param name="storeId" value="${favouriteStoreId}"/>
				                                <dsp:param name="searchType" value="2"/>
				                                <dsp:oparam name="output">
				                                    <%-- do not show this div "registryForm" if there is no fav store currently selected by the user --%>
				                                    <c:set var="favStoreIDForUI" ><dsp:valueof param="storeId"/></c:set>
				                                        <div class="grid_4 alpha omega location marTop_20">
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
				                                            
				                                         <%--    <div class="actionLink">
				                                                <a href="#viewDirections"  data-storeid="${favouriteStoreId}" class="viewDirectionsNew" title="Get Map & Directions">Get Map & Directions</a>
				                                            </div> --%>
				                                            <%-- <div class="actionLink">
				                                                <a href="#viewMap" class="viewMapModal"><bbbl:label key="lbl_registry_store_view_on_map" language ="${pageContext.request.locale.language}"/></a>
				                                            </div> --%>
				                                            <div class="clear"></div>
				                                            </div>
				                                            <div class="grid_2 omega">
					                                          <div class="button">
				                                            	<input tabindex="9" type="button" name="changeStore" value="Change Store" class="changeStore" role="button" aria-label=" <bbbl:label key="lbl_change_your_fav_store" language ="${pageContext.request.locale.language}"/>" />
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
					                                                <a href="#viewDirections"  data-storeid="${favouriteStoreId}" class="viewDirectionsNew" title="Map & Directions" aria-label="map and directions">Map & Directions</a>
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
					                 <c:otherwise>
										<div id="" class="grid_4 alpha omega  form clearfix">
					                    <div class="grid_4 alpha clearfix">
					                        <c:set var="zip">
					                                 <dsp:valueof
					                                bean="Profile.shippingAddress.postalCode" />
					                            </c:set>
					                        <c:if test="${!empty zipFromshipping && zipFromshipping ne '' && zipFromshipping ne null }">
					                            <dsp:droplet name="/com/bbb/selfservice/SimpleRegSearchStoreDroplet">
					                                <dsp:param name="searchString" value="${zip}"/>
					                                <dsp:param name="radius" value="${radius_default_selected}" />
					                                 <dsp:param name="searchType" value="2"/>
					                                <dsp:oparam name="output">
														<c:set var="favStoreIDForUI" ><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeId"/></c:set>
					                                    <div class="grid_4 alpha omega clearfix finStrBtmBorder">
					                            <h2 class="formTitle marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></h2>
					                            <p class="marTop_5"><bbbl:label key="lbl_reg_favStoreSelected" language ="${pageContext.request.locale.language}"/></p>
					                        </div>
					                      
					                        <div class="clear"></div>
					                                    <div class="grid_4 alpha clearfix">
					                                        <div class="grid_4 alpha omega location marTop_20">
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
					                                            
					                                            <%-- <div class="actionLink">
					                                                <a href="#viewDirections"  data-storeid="${favouriteStoreId}" class="viewDirectionsNew" title="Get Map & Directions">Get Map & Directions</a>
					                                            </div> --%>
					                                            <%-- <div class="actionLink">
					                                                <a href="#viewMap" class="viewMapModal"><bbbl:label key="lbl_registry_store_view_on_map" language ="${pageContext.request.locale.language}"/></a>
					                                            </div> --%>
					                                            <div class="clear"></div>
					                                          </div>  
					                                          <div class="grid_2 omega">
					                                          <div class="button">
				                                            	<input tabindex="9" type="button" name="changeStore" value="Change Store" class="changeStore" role="button" aria-label=" <bbbl:label key="lbl_change_your_fav_store" language ="${pageContext.request.locale.language}"/>"/>
															 </div>
					                                          </div>
					                                        </div>
					                                        <div class="expendContent"><a class="expend" href="javascript:void(0)"><span class="expend-plusicon">+</span>Store Info</a></div>
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
					                                                <a href="#viewDirections"  data-storeid="${favStoreIDForUI}" class="viewDirectionsNew" aria-label="map & directions" title="Map & Directions">Map & Directions</a>
					                                            </div>
					                                            </div>
					                                            <div class="clear"></div>
					                                        </div>
					                                        <div class="clear"></div>
					                                    </div>
					                                    <div class="clear"></div>
					                                </dsp:oparam>
					                            </dsp:droplet>
					                        </c:if>
					                    </div>
					                    <div class="clear"></div>
					                	</div>	                 	
					                 </c:otherwise>
				                </c:choose>
               
                  <c:if test="${empty favStoreIDForUI}">
                   <div id="" class="grid_4 alpha omega  form clearfix">
                   <div class="grid_4 alpha clearfix">
                         <div class="grid_4 alpha omega clearfix">
	                            <label    for="txtFindStore"><h2 class="formTitle marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></h2></label>
	                            <p class="marTop_10 findStoreText"><bbbl:label key="lbl_reg_selectFavStore" language ="${pageContext.request.locale.language}"/></p>
	                        </div>
                            <input id="txtFindStore"  maxlength="30" name="txtFindStoreName" value="" class="${isRequired} cannotStartWithWhiteSpace escapeHTMLTag widthHundred" type="text" aria-required="true" placeholder="<bbbl:label key="lbl_reg_ph_favstore" language ="${pageContext.request.locale.language}"/>" autocomplete="off">
                            <span class="txtFindStoresearch"><span class="icon icon-search block" aria-label="search store" aria-hidden="false" role='button'></span></span>
                			</div>
               		</div>
                
                </c:if>
                 </div>
                   <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.prefStoreNum"  value="${favStoreIDForUI}" id="favStoreIDForUI"/>
                   </c:if>
				 
                 <c:if test="${inputListMap['nurseryTheme'].isDisplayonForm}">
                    <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['nurseryTheme'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
			        <c:if test="${event == 'BA1'}">
					    <div id="" class="grid_4 alpha omega  form clearfix fr nurseryDecor">
                   			<div class="grid_2 alpha clearfix">
                         		<div class="grid_3 alpha omega clearfix">
	                            	<label class="formTitle marBottom_5" id="lblbabyNurserytheme" for="babyNurserytheme"><bbbl:label key="lbl_nursery_decor_theme" language ="${pageContext.request.locale.language}"/></label>
	                        	</div>
	                        	<c:set var="reg_lbl_ph_nursery_decor_theme"><bbbl:label key="reg_lbl_ph_nursery_decor_theme" language ="${pageContext.request.locale.language}"/></c:set>
                            	<dsp:input id="babyNurserytheme" maxlength="30" name="babyNurserythemeName" iclass="${isRequired} cannotStartWithWhiteSpace escapeHTMLTag widthHundred" type="text" autocomplete="off" bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme">
                			         <dsp:tagAttribute name="placeholder" value="${reg_lbl_ph_nursery_decor_theme}" />
                			         <dsp:tagAttribute name="aria-required" value="true" />
                			         <dsp:tagAttribute name="aria-labelledby" value="lblbabyNurserytheme" />
                			</dsp:input>
                			</div>
               		</div>

			        </c:if>
			        </c:if>
               
                
            </div>
             
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
        
        <div class="clear"></div>
    
 
    <div class="clear"></div>
</dsp:page>