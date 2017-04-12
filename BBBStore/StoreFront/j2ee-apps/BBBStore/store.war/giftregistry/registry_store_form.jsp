<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/account/droplet/BBBConfigKeysDroplet" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />

    <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>

    <c:set var="BuyBuyBabySite">
        <bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>
    
    <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
    <c:set var="existingfavouriteStoreId"><dsp:valueof value="${regVO.prefStoreNum}"/></c:set>
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
	 <c:set var="favouriteStoreId"><dsp:valueof value="${regVO.prefStoreNum}"/></c:set>
	</c:otherwise>
	</c:choose>
	<c:set var="zipFromshipping"></c:set>
	<c:set var="favStoreIDForUI" value="${favouriteStoreId}"></c:set>
	
	<c:if test="${empty favouriteStoreId}">
	  <c:set var="zipFromshipping"><dsp:valueof bean="Profile.shippingAddress.postalCode" /></c:set>
	</c:if>
	<div class="steps step4 clearfix grid_12 alpha omega">
        <div id="step4Preview" class="grid_12 alpha omega clearfix changeStoreStep_Preview">
            <div class="clearfix grid_12 alpha omega favoriteStore">
                <div class="grid_12 alpha omega clearfix">
                    <div class="clearfix regsitryItem">
						<h3>
                        <span class="regsitryItemNumber">4</span>
                        <span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key="lbl_registry_store_tell_us" language ="${pageContext.request.locale.language}"/> 
                            <c:if test="${empty regVO}"><a href="#edit4" id="step4EditLink" class="editRegistryLink"><bbbl:label key="lbl_registry_store_edit" language ="${pageContext.request.locale.language}"/></a></c:if>
                        </span>
						</h3>
                    </div>
                </div>
                <div class="clear"></div>
                <c:choose>
                <c:when test="${!empty favouriteStoreId}"> 
                    <div id="step4Information" class="grid_12 alpha omega stepInformation form clearfix">
                    <div class="grid_8 alpha clearfix">
                        <div class="grid_8 alpha omega clearfix">
                            <h2 class="formTitle marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></h2>
                        </div>
                        <div class="clear"></div>
						<div class="registryForm grid_8 alpha clearfix">
                        <c:if test="${!empty favouriteStoreId}">
                            <c:set var="passShippingAddr">
                                <dsp:valueof
                                bean="Profile.shippingAddress.firstName" />, <dsp:valueof
                                bean="Profile.shippingAddress.lastName" />, <dsp:valueof
                                bean="Profile.shippingAddress.address1" />, <dsp:valueof
                                bean="Profile.shippingAddress.address2" />, <dsp:valueof
                                bean="Profile.shippingAddress.city" />, <dsp:valueof
                                bean="Profile.shippingAddress.state" />
                            </c:set>
                                
                            <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
                                <dsp:param name="storeId" value="${favouriteStoreId}"/>
                                <dsp:param name="searchString" value="${passShippingAddr}"/>
                                <dsp:param name="searchType" value="2"/>
                                <dsp:oparam name="output">
                                    <%-- do not show this div "registryForm" if there is no fav store currently selected by the user --%>
                                    
                                        <div class="grid_4 alpha omega location">
                                            <h3 class="formTitle storeName"><dsp:valueof param="StoreDetails.storeName"/></h3>
                                            <div class="address">
                                                <div class="street"><dsp:valueof param="StoreDetails.address"/></div>
                                                <div>
                                                    <span class="city"><dsp:valueof param="StoreDetails.city"/>,</span>
                                                    <span class="state"><dsp:valueof param="StoreDetails.state"/></span>
                                                    <span class="zip"><dsp:valueof param="StoreDetails.postalCode"/></span>
                                                    <dsp:getvalueof var="contactFlag" param="StoreDetails.contactFlag"/>
                                                </div>
                                            </div>
                                            
                                            <div class="actionLink">
                                                <a href="#viewDirections"  data-storeid="${favouriteStoreId}" class="viewDirectionsNew" title="Get Map & Directions">Get Map & Directions</a>
                                            </div>
                                            <%-- <div class="actionLink">
                                                <a href="#viewMap" class="viewMapModal"><bbbl:label key="lbl_registry_store_view_on_map" language ="${pageContext.request.locale.language}"/></a>
                                            </div> --%>
                                            <div class="clear"></div>
                                        </div>
                                        <div class="grid_4 omega clearfix marTop_20 storePickupBenifitsWrap">
                                            <p class="grid_3 suffix_1 pickupTimimngs noMar clearfix">
	                                            <dsp:getvalueof param="StoreDetails.weekdaysStoreTimings" id="weekdaysStoreTimings"/>
	                                            <dsp:getvalueof param="StoreDetails.satStoreTimings" id="satStoreTimings"/>
	                                            <dsp:getvalueof param="StoreDetails.sunStoreTimings" id="sunStoreTimings"/>
	                                            <dsp:getvalueof param="StoreDetails.otherTimings1" id="otherTimings1"/>
	                                            <dsp:getvalueof param="StoreDetails.otherTimings2" id="otherTimings2"/>
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
		                                        <br/>
		                                        <strong><dsp:valueof param="StoreDetails.storePhone"/></strong>
		                                    </p>
                                            <c:if test="${!empty code && code != null}" >
                                                <div class="formRow clearfix">
                                                    <dsp:getvalueof param="StoreDetails.specialtyShopsCd" id="code"/>
                                                    <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
                                                        <dsp:param name="code" value="${code}"/>
                                                        <dsp:param name="queryRQL" value="specialityShopCd=:code"/>
                                                        <dsp:param name="repository" value="/com/bbb/selfservice/repository/StoreRepository"/>
                                                        <dsp:param name="itemDescriptor" value="specialityCodeMap"/>
                                                        <dsp:param name="elementName" value="item"/>
                                                        <dsp:oparam name="output">
                                                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                                <dsp:param name="array" param="item.specialityCd"/>
                                                                <dsp:param name="elementName" value="list"/>
                                                                <dsp:oparam name="output">
                                                                    <dsp:getvalueof param="list.codeImage" id="image"/>
                                                                    <dsp:getvalueof param="list.storeListTitleText" id="storeListTitleText"/>
                                                                    <dsp:getvalueof param="list.storeListAltText" id="storeListAltText"/>
                                                                    <div class="benefitsItem"><img src="${imagePath}${image}" width="32" height="31" alt="${storeListAltText}" /><span>${storeListTitleText}</span></div>
                                                                </dsp:oparam>    
                                                            </dsp:droplet>
                                                        </dsp:oparam>
                                                    </dsp:droplet>
                                                    <div class="clear"></div>
                                                </div>
                                            </c:if>
                                            <div class="clear"></div>
                                        </div>
                                        <div class="clear"></div>
                                	</dsp:oparam>
                            	</dsp:droplet>
                        	</c:if>
						</div>
                      	<div class="clear"></div>
                    	</div>
                    	<div class="clear"></div>
                	 </div>
               		 </c:when>
	                 <c:otherwise>
						<div id="step4Information" class="grid_12 alpha omega  form stepInformation clearfix">
	                    <div class="grid_8 alpha clearfix">
	                        <div class="grid_8 alpha omega clearfix">
	                            <h2 class="formTitle marBottom_5"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></h2>
	                        </div>
	                        <div class="clear"></div>
	                        <c:if test="${!empty zipFromshipping}">
	                            <c:set var="zip">
	                                 <dsp:valueof
	                                bean="Profile.shippingAddress.postalCode" />
	                            </c:set>
	                                
	                            <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
	                                <dsp:param name="searchString" param="${zip}"/>
	                                <dsp:oparam name="output">
										<c:set var="favStoreIDForUI" ><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeId"/></c:set>
	                                    <%-- do not show this div "registryForm" if there is no fav store currently selected by the user --%>
	                                    <div class="registryForm grid_8 alpha clearfix">
	                                        <div class="grid_4 alpha omega location">
	                                            <h3 class="formTitle storeName"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeName"/></h3>
	                                            <div class="address">
	                                                <div class="street"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].address"/></div>
	                                                <div>
	                                                    <span class="city"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].city"/>,</span>
	                                                    <span class="state"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].state"/></span>
	                                                    <span class="zip"><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].postalCode"/></span>
	                                                    <dsp:getvalueof var="contactFlag" param="StoreDetailsWrapper.storeDetails[0].contactFlag"/>
	                                                </div>
	                                            </div>
	                                            
	                                            <div class="actionLink">
	                                                <a href="#viewDirections"  data-storeid="${favouriteStoreId}" class="viewDirectionsNew" title="Get Map & Directions">Get Map & Directions</a>
	                                            </div>
	                                            <%-- <div class="actionLink">
	                                                <a href="#viewMap" class="viewMapModal"><bbbl:label key="lbl_registry_store_view_on_map" language ="${pageContext.request.locale.language}"/></a>
	                                            </div> --%>
	                                            <div class="clear"></div>
	                                        </div>
	                                        <div class="grid_4 omega clearfix marTop_20 storePickupBenifitsWrap">
	                                            <p class="grid_3 suffix_1 pickupTimimngs noMar clearfix">
		                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].weekdaysStoreTimings" id="weekdaysStoreTimings"/>
		                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].satStoreTimings" id="satStoreTimings"/>
		                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].sunStoreTimings" id="sunStoreTimings"/>
		                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].otherTimings1" id="otherTimings1"/>
		                                            <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].otherTimings2" id="otherTimings2"/>
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
	                                                <dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storeDescription" /><br/>
	                                                <strong><dsp:valueof param="StoreDetailsWrapper.storeDetails[0].storePhone"/></strong>
	                                            </p>
	                                            <c:if test="${!empty code && code != null}" >
	                                                <div class="formRow clearfix">
	                                                    <dsp:getvalueof param="StoreDetailsWrapper.storeDetails[0].specialtyShopsCd" id="code"/>
	                                                    <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
	                                                        <dsp:param name="code" value="${code}"/>
	                                                        <dsp:param name="queryRQL" value="specialityShopCd=:code"/>
	                                                        <dsp:param name="repository" value="/com/bbb/selfservice/repository/StoreRepository"/>
	                                                        <dsp:param name="itemDescriptor" value="specialityCodeMap"/>
	                                                        <dsp:param name="elementName" value="item"/>
	                                                        <dsp:oparam name="output">
	                                                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
	                                                                <dsp:param name="array" param="item.specialityCd"/>
	                                                                <dsp:param name="elementName" value="list"/>
	                                                                <dsp:oparam name="output">
	                                                                    <dsp:getvalueof param="list.codeImage" id="image"/>
	                                                                    <dsp:getvalueof param="list.storeListTitleText" id="storeListTitleText"/>
	                                                                    <dsp:getvalueof param="list.storeListAltText" id="storeListAltText"/>
	                                                                    <div class="benefitsItem"><img src="${imagePath}${image}" width="32" height="31" alt="${storeListAltText}" /><span>${storeListTitleText}</span></div>
	                                                                </dsp:oparam>    
	                                                            </dsp:droplet>
	                                                        </dsp:oparam>
	                                                    </dsp:droplet>
	                                                    <div class="clear"></div>
	                                                </div>
	                                            </c:if>
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
            
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
        <div id="step4Form" class="grid_12 alpha omega clearfix changeStoreStep_Edit <c:if test='${empty favStoreIDForUI}'>noFavStore</c:if>">
            <div class="grid_12 alpha omega clearfix favoriteStore">
                <div id="step4FormPost" class="grid_12 alpha omega form post clearfix padTop_20">
                    <fieldset class="grid_8 alpha clearfix padRight_10">
                        <legend class="formTitle"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></legend>
                        <dsp:input type="hidden" name="favStoreId" bean="GiftRegistryFormHandler.registryVO.prefStoreNum"  value="${favStoreIDForUI}">
                            <dsp:tagAttribute name="data-change-store-storeid" value="favStoreId"/>
                        </dsp:input>
                        <%-- set the value for this hidden input field to the store id of the currently selected store ... if none then keep it blank --%>
						<c:if test="${empty zipFromshipping}">
                            
							<p class="customFavStore hidden">
							  <bbbl:label key="lbl_registry_no_favorite_store_message" language ="${pageContext.request.locale.language}"/></p>
						</c:if>

					  <c:choose> 
                        <c:when test="${!empty favouriteStoreId}">
	                        <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
	                            <dsp:param name="storeId" value="${favouriteStoreId}"/>
	                            <dsp:param name="searchType" value="2"/>
	                            <dsp:oparam name="output">
	                                <dsp:getvalueof var="storeDetails" param="StoreDetails"/>
	                            </dsp:oparam>
	                            <dsp:oparam name="empty">
			                        <%-- Add the "hidden" class to "noStoreInfo" Tag if there is any fav store previously selected by the user or vise versa --%>
			                        <p class="noStoreInfo <c:if test="${!empty favouriteStoreId}">hidden</c:if>">
				                        <bbbl:label key="lbl_registry_store_no_favorite_store" language ="${pageContext.request.locale.language}"/>
									</p>                            
	                            </dsp:oparam>
	                            <dsp:oparam name="error">
	                            
			                        <%-- Add the "hidden" class to "noStoreInfo" Tag if there is any fav store previously selected by the user or vise versa --%>
			                        <p class="noStoreInfo <c:if test="${!empty favouriteStoreId}">hidden</c:if>">
				                        <bbbl:label key="lbl_registry_store_no_favorite_store" language ="${pageContext.request.locale.language}"/>
									</p>                            
	                            </dsp:oparam>
	                        </dsp:droplet>
						</c:when>
						<c:otherwise>
							<c:if test="${!empty zipFromshipping}">
		                        <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
		                            <dsp:param name="searchString" param="${zipFromshipping}"/>
		                            <dsp:oparam name="output">
		                                <dsp:getvalueof var="storeDetails" param="StoreDetailsRegistry"/>
		                            </dsp:oparam>
		                            <dsp:oparam name="empty">
		       	                         <%-- Add the "hidden" class to "noStoreInfo" Tag if there is any fav store previously selected by the user or vise versa --%>
				                        <p class="noStoreInfo <c:if test="${!empty favouriteStoreId}">hidden</c:if>">
					                        <bbbl:label key="lbl_registry_store_no_favorite_store" language ="${pageContext.request.locale.language}"/>
										</p>
		                            </dsp:oparam>
		                            <dsp:oparam name="error">
				                        <%-- Add the "hidden" class to "noStoreInfo" Tag if there is any fav store previously selected by the user or vise versa --%>
				                        <p class="noStoreInfo <c:if test="${!empty favouriteStoreId}">hidden</c:if>">
					                        <bbbl:label key="lbl_registry_store_no_favorite_store" language ="${pageContext.request.locale.language}"/>
										</p>                            
		                            
		                            </dsp:oparam>
		                        </dsp:droplet>
	                        </c:if>
						</c:otherwise>
                      </c:choose>
                        <%-- do not show this div "registryForm" if there is no fav store currently selected by the user --%>
                        <div class="registryForm grid_8 alpha omega clearfix padTop_10 <c:if test="${ (empty storeDetails)}">hidden</c:if>">
                            <div class="storeDataForPreview clearfix">
                                <div class="grid_4 alpha omega location">
                                    <h3 class="formTitle storeName"><dsp:valueof value="${storeDetails.storeName}"/></h3>
                                    <div class="address">
                                        <div class="street"><dsp:valueof value="${storeDetails.address}"/></div>
                                        <div>
                                            <span class="city"><dsp:valueof value="${storeDetails.city}"/>,</span>
                                            <span class="state"><dsp:valueof value="${storeDetails.state}"/></span>
                                            <span class="zip"><dsp:valueof value="${storeDetails.postalCode}"/></span>
                                            <c:set var="contactFlag" value="${storeDetails.contactFlag}"/>
                                        </div>
                                    </div>
                                    <div class="actionLink">
                                        <a tabindex="1" href="#viewDirections"  data-storeid="${favouriteStoreId}" class="viewDirectionsNew" title="Get Map & Directions">Get Map & Directions</a>
                                    </div>
                                    <%--<div class="actionLink">
                                        <a tabindex="2" href="#viewMap" class="viewMapModal" title='<bbbl:label key="lbl_registry_store_view_on_map" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_registry_store_view_on_map" language ="${pageContext.request.locale.language}"/></a>
                                    </div>--%>
                                    <%-- Add class "hidden" to this div "btnChangeStore1" if the contactFlag is false --%>
                                    <c:if test="${empty regVO}">
                                    <div class="btnChangeStore input submit small fl pushDown">
                                        <div class="button">
	                                        <input tabindex="3" type="button" name="changeStore" value="<bbbl:label key="lbl_fav_store_change" language="${language}"/>" class="changeStore" role="button" aria-pressed="false" />
                                        </div>
                                    </div>
                                    </c:if>
                                    <div class="clear"></div>
                                </div>
                                <div class="grid_4 omega clearfix storePickupBenifitsWrap">
                                    <p class="grid_3 suffix_1 pickupTimimngs noMar clearfix">
										<c:forTokens items="${storeDetails.weekdaysStoreTimings}" delims="," var="item">${item}</c:forTokens>
                                        <br/>
										<c:forTokens items="${storeDetails.satStoreTimings}" delims="," var="item">${item}</c:forTokens>
                                        <br/>
										<c:forTokens items="${storeDetails.sunStoreTimings}" delims="," var="item">${item}</c:forTokens>
                                        <br/>
										<c:forTokens items="${storeDetails.otherTimings1}" delims="," var="item">${item}</c:forTokens>
                                        <br/>
										<c:forTokens items="${storeDetails.otherTimings2}" delims="," var="item">${item}</c:forTokens>
                                        <br/>
										<c:forTokens items="${storeDetails.storeDescription}" delims="," var="item">${item}</c:forTokens>
                                        <br/>
                                        <strong><dsp:valueof value="${storeDetails.storePhone}"/></strong>
                                    </p>
                                    <c:if test="${!empty code && code != null}" >
                                        <div class="formRow clearfix">
                                            <dsp:getvalueof param="StoreDetails.specialtyShopsCd" id="code"/>
                                            <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
                                                <dsp:param name="code" value="${code}"/>
                                                <dsp:param name="queryRQL" value="specialityShopCd=:code"/>
                                                <dsp:param name="repository" value="/com/bbb/selfservice/repository/StoreRepository"/>
                                                <dsp:param name="itemDescriptor" value="specialityCodeMap"/>
                                                <dsp:param name="elementName" value="item"/>
                                                <dsp:oparam name="output">
                                                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                        <dsp:param name="array" param="item.specialityCd"/>
                                                        <dsp:param name="elementName" value="list"/>
                                                        <dsp:oparam name="output">
                                                            <dsp:getvalueof param="list.codeImage" id="image"/>
                                                            <dsp:getvalueof param="list.storeListTitleText" id="storeListTitleText"/>
                                                            <dsp:getvalueof param="list.storeListAltText" id="storeListAltText"/>
                                                            <div class="benefitsItem"><img src="${imagePath}${image}" width="32" height="31" alt="${storeListAltText}" /><span>${storeListTitleText}</span></div>
                                                        </dsp:oparam>    
                                                    </dsp:droplet>
                                                </dsp:oparam>
                                            </dsp:droplet>
                                            <div class="clear"></div>
                                        </div>
                                    </c:if>
                                    <div class="clear"></div>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>

							
							<c:choose>
								<c:when test="${currentSiteId eq BedBathUSSite}">
									<c:set var="perfectBabyRegistry"><tpsw:switch tagName="PerfectBabyRegistry_US"/></c:set>
								</c:when>
								<c:when test="${currentSiteId eq BuyBuyBabySite}">
									<c:set var="perfectBabyRegistry"><tpsw:switch tagName="PerfectBabyRegistry_baby"/></c:set>
								</c:when>
								<c:when test="${currentSiteId eq BedBathCanadaSite}">
									<c:set var="perfectBabyRegistry"><tpsw:switch tagName="PerfectBabyRegistry_CA"/></c:set>
								</c:when>
							</c:choose>
							<c:if  test="${perfectBabyRegistry eq true}">
                            	<c:if  test="${regVO eq null}">
                                	<div class="contactFlag grid_4 alpha omega <c:if test="${empty contactFlag || contactFlag eq false}">hidden</c:if>">
						                <bbbt:textArea key="txt_store_contact_option" language="${pageContext.request.locale.language}"/>
						                <div class="contactMethodContainer radioItem input clearfix">
						                    <div class="grid_4 alpha">
								                    	<div class="radio">
								                        	<dsp:input tabindex="4" type="radio" iclass="required" name="contactMethod" id="contactPhone" value="P" bean="GiftRegistryFormHandler.registryVO.refStoreContactMethod">
			                                                	<dsp:tagAttribute name="aria-checked" value="false"/>
			                                                	<dsp:tagAttribute name="aria-labelledby" value="lblcontactPhone errorcontactMethod"/>
			                                                </dsp:input>
														</div>
								                        <div class="label">
								                        	<label id="lblcontactPhone" for="contactPhone"> <span><bbbl:label key="lbl_registry_store_contact_phone" language ="${pageContext.request.locale.language}"/></span> </label>
														</div>
													</div>
								                    <div class="grid_4 alpha">
								                    	<div class="radio">
								                        	<dsp:input tabindex="5" type="radio" iclass="required" value="E" name="contactMethod" id="contactEmail"    bean="GiftRegistryFormHandler.registryVO.refStoreContactMethod">
			                                                	<dsp:tagAttribute name="aria-checked" value="false"/>
			                                                    <dsp:tagAttribute name="aria-labelledby" value="lblcontactEmail errorcontactMethod"/>
															</dsp:input>
														</div>
														<div class="label">
								                        	<label id="lblcontactEmail" for="contactEmail"> <span><bbbl:label key="lbl_registry_store_contact_email" language ="${pageContext.request.locale.language}"/></span> </label>
								                        </div>
								                    </div>
								                    <div class="grid_4 alpha">
														<div class="radio">
								                        	<dsp:input tabindex="6" type="radio" iclass="required"  value="N" name="contactMethod" id="contactSelf" bean="GiftRegistryFormHandler.registryVO.refStoreContactMethod">
			                                                	<dsp:tagAttribute name="aria-checked" value="false"/>
			                                                    <dsp:tagAttribute name="aria-labelledby" value="lblcontactSelf errorcontactMethod"/>
															</dsp:input>
														</div>
														<div class="label">
															<label id="lblcontactSelf" for="contactSelf"> <span><bbbl:label key="lbl_registry_store_contact_wish" language ="${pageContext.request.locale.language}"/></span> </label>
														</div>
													</div>
											<div class="cb">
												<label id="errorcontactMethod" for="contactMethod" generated="true" class="error"></label>
                                                <label for="contactPhone" class="offScreen"> <span><bbbl:label key="lbl_registry_store_contact_phone" language ="${pageContext.request.locale.language}"/></span> </label>
												<label for="contactEmail" class="offScreen"> <span><bbbl:label key="lbl_registry_store_contact_email" language ="${pageContext.request.locale.language}"/></span> </label>
                                                <label for="contactSelf" class="offScreen"> <span><bbbl:label key="lbl_registry_store_contact_wish" language ="${pageContext.request.locale.language}"/></span> </label>
											</div>
										</div>
									</div>
								</c:if>
							</c:if>    

                           
                           
                            <div class="clear"></div>
                            
                            
                                <%--  CR3 WeddingChannel & TheBump OptIn --%>
                                <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>
                                
                                <c:set var="weddingChannelOptIn">
                                    <bbbc:config key="WeddingChannelOptIn" configName="FlagDrivenFunctions" />
                                </c:set>
                                
                                <c:set var="bumpOptIn">
                                    <bbbc:config key="BumpOptIn" configName="FlagDrivenFunctions" />
                                </c:set>
                                
                                 <c:choose>
                                    <c:when test="${null eq regVO}">
                                        <c:set var="optInChecked" value="false"/>
                                    </c:when>
                                    <c:when test="${empty regVO}">
                                        <c:set var="optInChecked" value="false"/>
                                    </c:when>
                                    <c:when test="${fn:toLowerCase(regVO.optInWeddingOrBump) eq 'true'}">
                                        <c:set var="optInChecked" value="true"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="optInChecked" value="false"/>
                                    </c:otherwise>
                                </c:choose> 
                                <c:if test="${regVO == null}">
                                    <div class="grid_5 alpha marTop_10 marBottom_10">
                                        <c:if test="${(currentSiteId ne BedBathCanadaSite) && weddingChannelOptIn && (event == 'BRD'||event == 'COM')}">
                                            <div class="checkbox fl">
                                                <dsp:input tabindex="7" id="emailPromotions" type="checkbox"
                                                    name="emailPromotions" checked="${optInChecked}" value="optInWeddingOrBumpChecked"
                                                    bean="GiftRegistryFormHandler.registryVO.optInWeddingOrBump">
                                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="emailPromotions"/>
                                                </dsp:input>
                                            </div>
                                            <bbbt:textArea key="txt_wed_optin_registry" language="${pageContext.request.locale.language}"/>
                                        </c:if>
                                        <c:if test="${bumpOptIn && event == 'BA1'}">
                                            <div class="checkbox fl">
                                                <dsp:input tabindex="8" id="emailPromotions" type="checkbox"
                                                    name="emailPromotions" checked="${optInChecked}" value="optInWeddingOrBumpChecked"
                                                    bean="GiftRegistryFormHandler.registryVO.optInWeddingOrBump">
                                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="emailPromotions"/>
                                                </dsp:input>
                                            </div>
                                            <bbbt:textArea key="txt_bump_optin_registry" language="${pageContext.request.locale.language}"/>
                                        </c:if>
                                    </div>
                                    <div class="clear"></div>
                                </c:if>
                                <%--  CR3 WeddingChannel & TheBump OptIn --%>
                        </div>
                       <%-- Condition to check if the selected favorite store flag is disabled from business --%>
                       <c:if test='${empty storeDetails && not empty favouriteStoreId}'>
                      <bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}"/>
					   </c:if>
                        <div class="clear"></div>
                        
                        <div class="grid_8 alpha omega clearfix">
                            <%-- Add class "hidden" to this div "btnChangeStore2" if the contactFlag is true --%>
                            <div class="btnChangeStore2 input submit small fl marRight_5">                               
                                    <c:choose>
                                        <c:when  test="${ (empty storeDetails)}">
											<div class="button">
                                            <input tabindex="9" type="button" name="changeStore" value="Find a Store" class="changeStore" role="button" aria-pressed="false" />
											</div>
										</c:when>                                        
                                    </c:choose>                               
                            </div>
                            <%--Client DOM XSRF | Part -1
                             dsp:input bean="GiftRegistryFormHandler.registryCreationSuccessURL" type="hidden" value="${contextPath}/giftregistry/registry_creation_confirmation.jsp"/>
							<dsp:input bean="GiftRegistryFormHandler.registryCreationErrorURL" type="hidden" value="${contextPath}/giftregistry/simpleReg_creation_form.jsp"/> --%> 
							<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registryCreationConfirm"/> 
                            <c:choose>
                                <c:when test="${siteId eq BuyBuyBabySite}">
								 <c:if test="${regVO == null}">
                                    <div class="input submit small fl">
                                        <div class="button button_active button_active_orange">
                                                <dsp:input tabindex="10" bean="GiftRegistryFormHandler.createRegistry" type="button" id="submitBabyRegistry" value="Create Registry" iclass="step4FocusField" >
                                                </dsp:input>
                                                <dsp:input tabindex="10" bean="GiftRegistryFormHandler.createRegistry"
                                                    type="submit" id="step4FormPostButton" iclass="step4FocusField createRegistrySubmit" value="Create Registry" priority="-9999">
                                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="step4FormPostButton"/>
                                                    <dsp:tagAttribute name="role" value="button"/>
                                                </dsp:input>
                                        </div>
                                    </div>
									  </c:if>
                                </c:when>
                                <c:otherwise>
								 <c:if test="${regVO == null}">
                                    <div class="input submit small fl">
                                     
                                        <div class="button button_active button_active_orange">
                                             <dsp:input tabindex="11" bean="GiftRegistryFormHandler.createRegistry" type="button" id="submitBabyRegistry" value="Create Registry" iclass="step4FocusField" >          
                                                </dsp:input>
                                                <dsp:input tabindex="11" bean="GiftRegistryFormHandler.createRegistry"
                                                    type="submit" id="step4FormPostButton" value="Create Registry" iclass="step4FocusField createRegistrySubmit" onclick="_gaq.push(['_trackEvent', 'bridal', 'click go', 'create']);" priority="-9999">
                                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="step4FormPostButton"/>
                                                    <dsp:tagAttribute name="role" value="button"/>
                                                </dsp:input>
                                        </div>
                                        
                                    </div>
									</c:if>
                                </c:otherwise>
                            </c:choose>
                            <div class="clear"></div>
                        </div>
                        <div class="clear"></div>
                    </fieldset>
                    <div class="grid_4 omega clearfix padTop_20">
                        <div class="registryNotice">
                        	<bbbt:textArea key="txt_reg_create_store_form" language ="${pageContext.request.locale.language}"/>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
  
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
</dsp:page>