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
     <c:if test="${inputListMap['favoriteStore'].isDisplayonForm}">
         <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['favoriteStore'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
    <c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
    
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
	<div class="clearfix  alpha omega row">
		<c:set var="babyMoPadLeft" value="noPadLeft"/>
	    <c:if test="${registryEventTypeName eq 'BA1' || registryEventTypeName eq 'Baby' || event eq 'BA1'}">
            <c:set var="babyClassCol" value="large-6"/>
            <c:set var="babyMoPadLeft" value=""/>
        </c:if>
        <div id="" class="${babyClassCol} ${babyMoPadLeft} columns alpha omega clearfix changeStoreStep_Preview">
            <div class="clearfix grid_4 alpha omega favoriteStore">
                <div class="clear"></div>
                 
				          <c:choose>
				                <c:when test="${!empty favouriteStoreId}"> 
				
				                    <div id="" class="grid_4 alpha omega form clearfix">
				                    <div class="grid_4 alpha clearfix">
				                        <div class="grid_4 alpha omega clearfix finStrBtmBorder">
				                            <label for="txtFindStore" class="marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></label>
				                            <p class="marTop_5 invite-co">Based on your preferences in your profile.You have selected the following store.</p>
				                        </div>
				                        <div class="clear"></div>
										<div class="grid_4 alpha clearfix">
				                             <dsp:droplet name="/com/bbb/selfservice/SimpleRegSearchStoreDroplet">
				                                <dsp:param name="storeId" value="${favouriteStoreId}"/>
				                                <dsp:param name="searchType" value="2"/>
				                                <dsp:oparam name="output">
				                                    <%-- do not show this div "registryForm" if there is no fav store currently selected by the user --%>
				                                    <c:set var="favStoreIDForUI" ><dsp:valueof param="storeId"/></c:set>
				                                        <div class="row alpha omega location marTop_20">
				                                        <div class="large-6 column alpha omega">
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
				                                            <div class="large-6 column omega">
				                                            <div class="changeStore-button">
				                                            	<a href="#" data-href="../selfservice/store/find_store_reg.jsp"  id="nearbyStoreLink" class="changeStore"  data-reveal-ajax="true" aria-label="<bbbl:label key="lbl_change_your_fav_store" language ="${pageContext.request.locale.language}"/>">Change Store</a>
				                                               </div>
															 </div>
					                                          </div>
				                                      
				                                        <div class="expendContent"><a class="expend" href="javascript:void(0)"><span class="expend-plusicon">+</span>Store Info</a></div>
				                                       <div class="row omega clearfix marTop_20 storePickupBenifitsWrap alpha content">
				                                            <div class="large-6 columns pickupTimimngs noMar clearfix">
					                                            <dsp:getvalueof param="StoreDetails.weekdaysStoreTimings" id="weekdaysStoreTimings"/>
					                                            <dsp:getvalueof param="StoreDetails.satStoreTimings" id="satStoreTimings"/>
					                                            <dsp:getvalueof param="StoreDetails.sunStoreTimings" id="sunStoreTimings"/>
					                                            <dsp:getvalueof param="StoreDetails.otherTimings1" id="otherTimings1"/>
					                                            <dsp:getvalueof param="StoreDetails.otherTimings2" id="otherTimings2"/>
					                                            <ul>
					                                            	<li class="storePhone"><strong><dsp:valueof param="StoreDetails.storePhone"/></strong></li>
					                                            	<li><c:forTokens  items="${weekdaysStoreTimings}" delims="," var="item">${item}</c:forTokens></li>
					                                            	<li><c:forTokens  items="${satStoreTimings}" delims="," var="item">${item}</c:forTokens></li>
					                                            	<li><c:forTokens  items="${sunStoreTimings}" delims="," var="item">${item}</c:forTokens></li>
					                                            	<li><c:forTokens  items="${otherTimings1}" delims="," var="item">${item}</c:forTokens></li>
					                                            	<li><c:forTokens  items="${otherTimings2}" delims="," var="item">${item}</c:forTokens></li>
					                                            	<li><c:forTokens  items="${StoreDetails.storeDescription}" delims="," var="item">${item}</c:forTokens></li>
					                                            </ul>				                                               
						                                     </div>
				                                            <div class="large-6 columns omega alpha">
					                                             <div class="actionLink">                       
					                                                <a title="Get Map &amp; Directions" onclick="javascript:externalLinks('mapquest: view maps')" data-storeid="${favStoreIDForUI}" href="#" class="viewDirectionsNew" id="viewMapOfStore">Get Map &amp; Directions</a>
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
					                    	<c:set var="zip"><dsp:valueof bean="Profile.shippingAddress.postalCode" /></c:set>
					                        <c:if test="${!empty zipFromshipping && zipFromshipping ne '' && zipFromshipping ne null }">
					                            <dsp:droplet name="/com/bbb/selfservice/SimpleRegSearchStoreDroplet">
					                                <dsp:param name="searchString" value="${zip}"/>
					                                <dsp:param name="radius" value="${radius_default_selected}" />
					                                 <dsp:param name="searchType" value="2"/>
					                                <dsp:oparam name="output">
														<c:set var="favStoreIDForUI" ><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeId"/></c:set>
					                                    <div class="grid_4 alpha omega clearfix finStrBtmBorder">
					                            <label for="txtFindStore" class="marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></label>
					                            <p class="marTop_5 invite-co"><bbbl:label key="lbl_reg_favStoreSelected" language ="${pageContext.request.locale.language}"/></p>
					                        </div>
					                      
					                        <div class="clear"></div>
					                                    <div class="grid_4 alpha clearfix">
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
					                                            
					                                            <%-- <div class="actionLink">
					                                                <a href="#viewDirections"  data-storeid="${favouriteStoreId}" class="viewDirectionsNew" title="Get Map & Directions">Get Map & Directions</a>
					                                            </div> --%>
					                                            <%-- <div class="actionLink">
					                                                <a href="#viewMap" class="viewMapModal"><bbbl:label key="lbl_registry_store_view_on_map" language ="${pageContext.request.locale.language}"/></a>
					                                            </div> --%>
					                                            <div class="clear"></div>
					                                          </div>  
					                                          <div class="large-6 column omega">
					                                          <div class="changeStore-button">
				                                            	<a href="#" data-href="../selfservice/store/find_store_reg.jsp"  id="nearbyStoreLink" class="changeStore"  data-reveal-ajax="true" aria-label="<bbbl:label key="lbl_change_your_fav_store" language ="${pageContext.request.locale.language}"/>">Change Store</a>
				                                               </div>
															 </div>
					                                          </div>
					                                        </div>
					                                        <div class="expendContent"><a class="expend" href="javascript:void(0)"><span class="expend-plusicon">+</span>Store Info</a></div>
					                                        <div class="row omega clearfix marTop_20 storePickupBenifitsWrap alpha content">
					                                            <div class="large-6 columns pickupTimimngs noMar clearfix">
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
						                                            	<li><c:forTokens  items="${otherTimings2}" delims="," var="item">${item}</c:forTokens></li>						                                            	
						                                            </ul>
					                                                <dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeDescription" />
					                                            </div>
					                                            <div class="large-6 columns omega alpha">
						                                            <div class="actionLink">
					                                                <a href="#viewDirections" data-storeid="${favStoreIDForUI}" class="viewDirectionsNew" id="viewMapOfStore" title="Map &amp; Directions">Map &amp; Directions</a>
						                                            </div>
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
                     <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.prefStoreNum"  value="${favStoreIDForUI}"/>
                   
                <div class="clearfix"></div>
               <div id="" class="grid_4 alpha omega  form clearfix">
                  <c:if test="${empty favStoreIDForUI}">
                	<c:if test="${registryEventTypeName eq 'BA1' || registryEventTypeName eq 'Baby' || event eq 'BA1'}">
                		<c:set var="babyTheme" value="large-4"/>
                	</c:if>
                    <div class="alpha clearfix pos-relative" >
                   
                         <div class="grid_4 alpha omega noPadLeft clearfix">
	                            <label for="txtFindStore" class="marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></label>
	                            <p class="invite-co ajaxHide findStoreText">You have not selected a Favorite Store.</p>
	                        </div>	
	                        <div class="ajaxHide inputField ">
                            <input id="txtFindStore"  maxlength="30" name="txtFindStoreName" value="" class="${isRequired} cannotStartWithWhiteSpace escapeHTMLTag widthHundred" type="text" aria-required="true" placeholder="Enter City and State or Zip Code" autocomplete="off">
                            <span class="txtFindStoresearch"><span class="icon icon-search block" aria-hidden="true"></span></span>
                            </div>
                </div>
                
				</c:if>
				<c:if test="${isTransient || (empty favStoreIDForUI && !isTransient)}">    
				<div class="grid_4 alpha clearfix hidden" id="dynamicLocationCreated">
                     <div class="row alpha omega location marTop_20">
                     <div class="large-6 columns  alpha omega">
                         <div class="clear"></div>
                       </div>  
                       <div class="large-6 columns  omega">
                        <div class="changeStore-button"> 
                        	<a href="#" data-href="../selfservice/store/find_store_reg.jsp"  id="nearbyStoreLink" class="changeStore"  data-reveal-ajax="true" aria-label="<bbbl:label key="lbl_change_your_fav_store" language ="${pageContext.request.locale.language}"/>">Change Store</a>
                        </div>
                       </div>
                     </div>
                     <div class="expendContent"><a href="javascript:void(0)" class="expend"><span class="expend-plusicon">+</span>Store Info</a></div>
                     <div class="row omega clearfix marTop_20 storePickupBenifitsWrap alpha content">
                         <div class="large-6 columns pickupTimimngs noMar clearfix">
                          
                         </div>
                         <div class="large-6 columns omega alpha">
                         <div class="actionLink">
                             <a id="viewMapOfStore" class="viewDirectionsNew" href="#" data-storeid="303" onclick="javascript:externalLinks('mapquest: view maps')" title="Get Map &amp; Directions">Get Map &amp; Directions</a>
                         </div>
                         </div>
                         <div class="clear"></div>
                     </div>
                     <div class="clear"></div>
                 </div>    
                </c:if>       
                </div>

            </div>
             <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.prefStoreNum"  id="favStoreIDForUI"/>
            <div class="clear"></div>
        </div>

		<div class="clear"></div>
		<div data-reveal="" class="reveal-modal small" id="nearbyStore"></div>
        <c:if test="${event == 'BA1'}">
	        <c:if test="${inputListMap['nurseryTheme'].isDisplayonForm}">
	            <c:set var="isRequired" value=""></c:set> 
				 <c:if test="${inputListMap['nurseryTheme'].isMandatoryOnCreate}">
					 <c:set var="isRequired" value="required"></c:set>
				 </c:if>
			        <div class="inputField columns large-6 cb nurseryTheme">                
						<label class="padBottom_5" for="babyNurseryTheme" id="lblbabyNurserytheme"><bbbl:label key="lbl_nursery_decor_theme" language ="${pageContext.request.locale.language}"/></label>
						<div class="row">
							<div class="inputField cb large-6 columns">
								<c:set var="reg_lbl_ph_nursery_decor_theme"><bbbl:label key="reg_lbl_ph_nursery_decor_theme" language ="${pageContext.request.locale.language}"/></c:set>
	                            	<dsp:input id="babyNurserytheme" maxlength="100" name="babyNurserythemeName" iclass="${isRequired} cannotStartWithWhiteSpace escapeHTMLTag widthHundred" type="text" autocomplete="off" bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme">
	                			        <dsp:tagAttribute name="placeholder" value="${reg_lbl_ph_nursery_decor_theme}" />
	                			        <dsp:tagAttribute name="aria-required" value="true" />
	                			         <dsp:tagAttribute name="aria-labelledby" value="lblbabyNurserytheme" />
	                				</dsp:input>
							</div>
						</div>
					</div>
			</c:if>
		</c:if>
    </div>
    </c:if>
    <div class="clear"></div>
</dsp:page>