<dsp:page>
  
	<dsp:getvalueof var="isInternationalCustomer" bean="/com/bbb/profile/session/SessionBean.internationalShippingContext" />
   
	<c:set var="supplyBalance">
		<bbbc:config key="SUPPLY_BALANCE_ON" configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="localStorePLP">
		<bbbc:config key="LOCAL_STORE_PLP_FLAG" configName="FlagDrivenFunctions" />
	</c:set>  
	<c:set var="lblChangeStore">
		<bbbl:label key="lbl_Change_Store_On_PLP" language="${pageContext.request.locale.language}"/>
	</c:set>
	<c:set var="noStoresFound">
		<bbbl:label key="lbl_no_store_found_on_PLP" language="${pageContext.request.locale.language}" />
	</c:set>
          
	<dsp:getvalueof param="productId" var="productId"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof param="bopusNotAllowed" var="bopusNotAllowed"/>
	<dsp:getvalueof param="ltlItem" var="ltlItem"/>
	<dsp:getvalueof param="customizationOffered" var="customizationOffered"/>
	<dsp:getvalueof param="itemAlreadyPersonalized" var="itemAlreadyPersonalized"/>
	<dsp:getvalueof param="customizableRequired" var="customizableRequired"/>
	<dsp:getvalueof param="selectedStoreForAjax" id="selectedStoreIDForAjax"/>
	<div class="storeInfoSection marTop_10">
	<c:set var="localStorePLP" value="${true}" />
	<c:choose>
		<c:when test="${(localStorePLP eq false && supplyBalance eq false) || bopusNotAllowed eq true || ltlItem eq true|| (customizationOffered eq true && itemAlreadyPersonalized eq true) || (customizableRequired eq true)}">
			<div class="storeInfoDetails col50">
			<div class='favouriteStoreDetails'>
                        
				<h6>
					<bbbl:label key="lbl_pick_up_not_available" language="${pageContext.request.locale.language}" />
				</h6>

			</div>
			</div>
		</c:when>
		<c:otherwise>
			<dsp:importbean bean="com/bbb/commerce/browse/droplet/BBBFindStoreOnPDPDroplet" />
			<dsp:importbean bean="/atg/userprofiling/Profile" />
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
                     <dsp:param name="array" bean="Profile.userSiteItems" />
                     <dsp:param name="elementName" value="sites" />
                     <dsp:oparam name="output">
                        <dsp:getvalueof id="key" param="key" />
                        <dsp:getvalueof id="favouriteStoreId" param="sites.favouriteStoreId" />
                        <c:if test="${currentSiteId eq key}">
                           <c:set var="favStoreId" value="${favouriteStoreId}"></c:set>
                        </c:if>
                     </dsp:oparam>
				</dsp:droplet>
    
			<div class="storeInfoDetails col50">
			<div></div>
			<div class='favouriteStoreDetails'>
			<c:choose>
				<c:when test="${localStorePLP eq false}">
                  <a href="javascript:void(0)" class="viewStoreDetails" role="link" tabindex="0" aria-label="Select your local store">
                     ${lblChangeStore}
                  </a>
                   <dsp:getvalueof param="latitude" var="lat"/>
                  <dsp:getvalueof param="longitude" var="lng"/>
              
                  <c:set var="latLngCookie" value="${cookie['latLngCookie']}"/>
                   <c:if test="${(latLngCookie eq null || empty latLngCookie) && (favStoreId eq null || empty favStoreId) && (lat eq null || empty lat) &&(lng eq null || empty lng)}">
                      <input type="hidden" name="emptyInput" value="true"/>
                   </c:if>
                </c:when>
				<c:otherwise>
				
					<c:set var="radius_range_us">
                     <bbbc:config key="radius_range_us" configName="MapQuestStoreType" />
					</c:set>
					<c:set var="radius_range_baby">
                     <bbbc:config key="radius_range_baby" configName="MapQuestStoreType" />
					</c:set>
					<c:set var="radius_range_ca">
                     <bbbc:config key="radius_range_ca" configName="MapQuestStoreType" />
					</c:set>
					<c:set var="radius_range_type">
                     <bbbc:config key="radius_range_type" configName="MapQuestStoreType" />
					</c:set>
					<c:choose>
                     <c:when test="${currentSiteId eq 'BedBathUS'}">
                        <c:set var="radius_range">${radius_range_us}</c:set>
                     </c:when>
                     <c:when test="${currentSiteId eq 'BuyBuyBaby'}">
                        <c:set var="radius_range">${radius_range_baby}</c:set>
                     </c:when>
                     <c:when test="${currentSiteId eq 'BedBathCanada'}">
                        <c:set var="radius_range_type">
                           <bbbc:config key="radius_range_type_ca" configName="MapQuestStoreType" />
                        </c:set>
                        <c:set var="radius_range">${radius_range_ca}</c:set>
                     </c:when>
                     <c:otherwise>
                        <c:set var="radius_range">${radius_range_us}</c:set>
                     </c:otherwise>
					</c:choose>
				
					<dsp:getvalueof param="favouriteStore" var="favouriteStore"/>
					<%--<c:set var="favouriteStore" value="${true}" />--%>
					<c:if test="${favouriteStore eq true }">
                    <dsp:droplet name="BBBFindStoreOnPDPDroplet">
                     <dsp:param name="skuId" param="skuId" />
                     <dsp:param name="searchString" param="searchStore" />
                     <dsp:param name="radius" param="radius" />
                     <dsp:param name="latitude" param="latitude" />
                     <dsp:param name="longitude" param="longitude" />
                     <dsp:param name="favouriteStore" value="${favouriteStore }" />
                     <dsp:param name="storeId" value="${favStoreId}" />
                     <dsp:param name="favStoreId" value="${favStoreId}" />
					 <dsp:param name="localStoreCallFromPLP" value="true" />
					 <dsp:param name="latLngFromPLP" value="${latLngFromPLP}" />
                     <dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id" />
                     <dsp:oparam name="outputViewStore">
                        <dsp:getvalueof var="storeDetailsViewStore" param="storeDetails" />
                        <dsp:getvalueof var="storeId" param="storeDetailsViewStore.storeId" />
                        <dsp:getvalueof var="productAvailableViewStore" param="productAvailableViewStore" scope="page" />
                        <dsp:getvalueof var="finalRadius" param="radius" scope="page" />
                         <dsp:getvalueof var="callStore" param="callStore" scope="page" />
                     </dsp:oparam>
                     <dsp:oparam name="viewStoreEmpty">
                        <dsp:getvalueof var="finalRadius" param="radius" scope="page" />
                     </dsp:oparam>
                     <dsp:oparam name="errorinViewFavStores">
                        <dsp:getvalueof param="errorinViewFavStores" var="errorinViewFavStores" />
                     </dsp:oparam>
                     <dsp:oparam name="outputFavStore">
                        <dsp:getvalueof var="storeId" param="storeDetailsFavStore[0].storeId" />
                        <dsp:getvalueof var="storeDetailsFavStore" param="storeDetailsFavStore" />
                        <dsp:getvalueof var="localStoreOff" param="localStoreOff" />
                     </dsp:oparam>
                     <dsp:oparam name="favStoreEmpty">
                        <c:set value="true" var="favstoreempty"/>
                     </dsp:oparam>
                     <dsp:oparam name="outputInventoryNotAvailable">
                        <dsp:getvalueof param="inventoryNotAvailableInFavStore" var="inventoryNotAvailable" />
                        <dsp:getvalueof param="radius" var="radius" />
                     </dsp:oparam>
                     <dsp:oparam name="errorinViewFavStores">
                        <dsp:getvalueof param="errorinViewFavStores" var="errorinViewFavStores" />
                     </dsp:oparam>
                      <dsp:oparam name="empty">
                        <dsp:getvalueof var="emptyInput" param="emptyInput" scope="page" />
                  
                     </dsp:oparam>
					</dsp:droplet>
					</c:if>
                        <c:choose>
                           <c:when
                              test="${errorinViewFavStores ne null && not empty errorinViewFavStores }">
                              <h6>${errorinViewFavStores}</h6>
                                <a href="javascript:void(0)" class="viewStoreDetails" role="link" tabindex="0" aria-label="${errorinViewFavStores}. Try Selecting another local store">
                                    ${lblChangeStore}
                               </a>
                           </c:when>
                           <c:when test="${inventoryNotAvailable ne null && not empty inventoryNotAvailable}">
                              <h6>
                                 ${inventoryNotAvailable} "${fn:escapeXml(radius)}
                                 <bbbl:label key="lbl_store_route_miles" 
                                    language="${pageContext.request.locale.language}"/>"
                              </h6>
                              <a href="#" class="findOtherStores">Find Other Stores</a>
                           </c:when>
                           <c:when test="${favstoreempty eq true || not empty emptyInput}">
                              <h6>
                                 ${noStoresFound}
                              </h6>
                              <a href="javascript:void(0)" class="findStores" data-field-storeid='${storeId}' role="link" tabindex="0" aria-label="${noStoresFound}!! Select your local store">${lblChangeStore}</a>
                              <!--<input type="hidden" data-field-storeid='${storeId}' class="button-Small btnRegistrySecondary findStores" value="Find Stores ">-->
							  
                           </c:when>
                           <c:otherwise>
                              <c:if test="${localStorePLP eq true && empty emptyInput}">
                                 <h6>
                                    <span id="defaultSelectedStoreDetails">
                                       ${fn:trim(storeDetailsFavStore[0].storeName)}, ${storeDetailsFavStore[0].state} ${storeDetailsFavStore[0].postalCode}
                                    </span>
                                 </h6>
								 <input type="hidden" id="defaultSelectedStoreIdForJS" value="${storeDetailsFavStore[0].storeId}">
                              </c:if>
                              <%--<c:if test="${(supplyBalance eq true && localStorePLP eq true)}">
                 <a href="#" class="reserveNow" data-field-storeid='${storeId}'>
                  <bbbl:label key="lbl_storepickup_reserve_now" language="${pageContext.request.locale.language}" />
                 </a>
                                 <!--<input type="hidden" data-field-storeid='${storeId}' class="button-Small btnRegistrySecondary reserveNow" value="Reserve Now ">-->
                               </c:if>
                              <c:if test="${(supplyBalance eq false && localStorePLP eq true)}">
                                 <h6>
                                    <bbbl:label key="lbl_call_store_for_availability" language="${pageContext.request.locale.language}" />
                                 </h6>
                              </c:if>--%>
                              <c:if test="${!(supplyBalance eq false && localStorePLP eq false)}">
                                 <a href="javascript:void(0)" class="viewStoreDetails" role="link" tabindex="0" aria-label="Change your local store from ${fn:trim(storeDetailsFavStore[0].storeName)}">
                                    ${lblChangeStore}
                                 </a>
                              </c:if>
                              <input type="hidden" value="${storeDetailsFavStore[0].state}" name="favStoreState" id="favStoreState"/>
                              ${emptyInput}
                                 </c:otherwise>
			</c:choose>
			</c:otherwise>
			</c:choose>
			<c:if test="${not empty emptyInput}">
                     <input type="hidden" name="emptyInput" value="${emptyInput}"/>
            </c:if>
                     </div>
                  </div>
			</div>

         <div class="toolTipStoreDetails">
          <dsp:getvalueof param="favouriteStore" var="favouriteStore"/>
         <c:if test="${favouriteStore eq false }">
                      <dsp:droplet name="BBBFindStoreOnPDPDroplet">
                     <dsp:param name="skuId" param="skuId" />
                     <dsp:param name="searchString" param="searchStore" />
                     <dsp:param name="radius" param="radius" />
                     <dsp:param name="latitude" param="latitude" />
                     <dsp:param name="longitude" param="longitude" />
                     <dsp:param name="favouriteStore" param="favouriteStore" />
                     <dsp:param name="storeId" value="${favStoreId}" />
                     <dsp:param name="favStoreId" value="${favStoreId}" />
					 <dsp:param name="localStoreCallFromPLP" value="true" />
					 <dsp:param name="latLngFromPLP" value="${latLngFromPLP}" />
                     <dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id" />
                     <dsp:oparam name="outputViewStore">
                        <dsp:getvalueof var="storeDetailsViewStore" param="storeDetails" />
                        <dsp:getvalueof var="storeId" param="storeDetailsViewStore.storeId" />
                        <dsp:getvalueof var="productAvailableViewStore" param="productAvailableViewStore" scope="page" />
                        <dsp:getvalueof var="finalRadius" param="radius" scope="page" />
                        <dsp:getvalueof var="callStore" param="callStore" scope="page" />
                         <dsp:getvalueof var="emptyDomResponse" param="emptyDomResponse" scope="page" />
                     </dsp:oparam>
                     <dsp:oparam name="viewStoreEmpty">
                        <dsp:getvalueof var="finalRadius" param="radius" scope="page" />
                     </dsp:oparam>
                     <dsp:oparam name="errorinViewFavStores">
                        <dsp:getvalueof param="errorinViewFavStores" var="errorinViewFavStores" />
                     </dsp:oparam>
                     <dsp:oparam name="outputFavStore">
                        <dsp:getvalueof var="storeId" param="storeDetailsFavStore[0].storeId" />
                        <dsp:getvalueof var="storeDetailsFavStore" param="storeDetailsFavStore" />
                        <dsp:getvalueof var="localStoreOff" param="localStoreOff" />
                     </dsp:oparam>
                     <dsp:oparam name="favStoreEmpty">
                        <c:set value="true" var="favstoreempty"/>
                     </dsp:oparam>
                     <dsp:oparam name="outputInventoryNotAvailable">
                        <dsp:getvalueof param="inventoryNotAvailableInFavStore" var="inventoryNotAvailable" />
                        <dsp:getvalueof param="radius" var="radius" />
                     </dsp:oparam>
                     <dsp:oparam name="errorinViewAllStores">
                        <dsp:getvalueof param="errorinViewAllStores" var="errorinViewAllStores" />
                     </dsp:oparam>
                      <dsp:oparam name="empty">
                        <dsp:getvalueof var="emptyInput" param="emptyInput" scope="page" />
                     </dsp:oparam>
                  </dsp:droplet>
                 </c:if>
                 <c:if test="${not empty emptyInput}">
                     <input type="hidden" name="emptyInput" value="${emptyInput}"/>
                 </c:if>
         <c:choose>
         <c:when test="${errorinViewAllStores ne null && not empty errorinViewAllStores }">
         <div class="serverUnavailable ">${errorinViewAllStores}</div>
         </c:when>
         <c:otherwise>
		 
		 <c:if test="${storeDetailsViewStore[0] != null}">
		 <div id="selectedStoreDetails">
		 <div id="selectedStoreLabel" tabindex="0"><bbbl:label key="lbl_selected_store_on_PLP" language="${pageContext.request.locale.language}" /></div>
		 <dsp:getvalueof value="${storeDetailsViewStore[0]}" var="storeDetail" />
         <dsp:getvalueof value="${storeDetail.storeId}" var="storeId" />
		 <li class="storeInformation">
         <ul class="clearfix">
         <li class="column1">
         <div class="fl">
         <div class="clearfix">
         <label for="radioStoreAddress_33" class="storeAdd">
         <div class="storeName upperCase"><strong>${storeDetail.storeName}</strong>
         </div>
         <div class="miles">(
         <dsp:valueof value="${storeDetail.distance}" converter="number" />
         <bbbl:label key="lbl_store_route_miles" language="${pageContext.request.locale.language}" />) </div>
         <div class="street">${storeDetail.address}</div>
         <div class="city">${storeDetail.city}</div>
         <div class="state">${storeDetail.state}&nbsp;${storeDetail.postalCode}</div>
         <div class="phoneNo">${storeDetail.storePhone}</div>
         </label>
         <!-- <p> 
            </p> -->
         <c:if test="${favStoreId == storeId}">
         <div class="actionLinkForFavouriteStore">
         <p class="marTop_5">
         <c:choose>
         <c:when test="${currentSiteId eq 
            'BedBathCanada'}">
         <a class="editFavouriteStore"
            href="${contextPath}/selfservice/store/canada_store_locator.jsp?favStoreId=${storeDetail.postalCode}">
         <bbbl:label 
            key="lbl_edit_favorite_store" language="${pageContext.request.locale.language}" />
         </a>
         </c:when>
         <c:otherwise>
         <a class="editFavouriteStore"
            href="${contextPath}/selfservice/store/find_store.jsp?favStoreId=${storeDetail.postalCode}">
         <bbbl:label 
            key="lbl_edit_favorite_store" language="${pageContext.request.locale.language}" />
         </a>
         </c:otherwise>
         </c:choose>
         </p>
         </div>
         </c:if>
         </div>
         </div>
         </li>
         <li class="column2 resultStoreWrapper">
         ${storeDetail.weekdaysStoreTimings}
         <div>
         ${storeDetail.satStoreTimings}
         </div>
         <div>
         ${storeDetail.sunStoreTimings}
         </div>
         <div>
         ${storeDetail.otherTimings1}
         </div>
         <div>
         ${storeDetail.otherTimings2}
         </div>
         </li>
              
         </ul>
         </li>
		 </div>
		 </c:if>
		 
		  <c:choose>
		  <c:when test="${fn:length(storeDetailsViewStore) gt 0}">
		  	<c:choose>
		  	<c:when test="${selectedStoreIDForAjax eq storeDetailsViewStore[0].storeId}">
				<dsp:getvalueof var="foundResultsCount" value="${fn:length(storeDetailsViewStore)-1}" />
			</c:when>
			<c:otherwise>
				<dsp:getvalueof var="foundResultsCount" value="${fn:length(storeDetailsViewStore)}" />
			</c:otherwise> 
			</c:choose>			
		  </c:when>
		  <c:otherwise>
		  	<dsp:getvalueof var="foundResultsCount" value="0" />
		  </c:otherwise>
		  </c:choose>
          <c:choose>
          <c:when test="${emptyInput ne null && not empty emptyInput }">
            <h6 class="noLatLong"></h6>
            <div class="foundResults hidden"> <span class="foundText"><bbbl:label key="lbl_we_have_found" language="${pageContext.request.locale.language}" /> <span id="stores">0</span>&nbsp;<bbbl:label key="lbl_stores_within" language="${pageContext.request.locale.language}" /> <span class='selectedMiles'>${finalRadius}
         </span><bbbl:label
            key="lbl_store_route_miles" language="${pageContext.request.locale.language}"/></span>
         <div class="storeDropDown" style="display:inline;">
         <a href="javascript:void(0);" class="ajaxDisable" aria-hidden="true">
         <span class="dropDownArrow" aria-hidden="true"><span></a>
         <ul class="dropDownOptions" style="display: none;">
         <c:forTokens items="${radius_range}" delims="," var="item">                                                                                
         <li class="clearfix" data-value="${item}" data-index="0"><span>${item} ${radius_range_type}</span>
         </li>
         </c:forTokens>
         </ul>

         <span class="zipCodeSearch hidden"><c:if test="${!(empty param.searchString)}"> of <span style="font-weight:bold">${fn:escapeXml(param.searchString)}</c:if><span>
         </div>
         <bbbl:label key="lbl_in_your_area" language="${pageContext.request.locale.language}" />.
         </div>

          </c:when>
          <c:otherwise>
         <div class="foundResults" id="foundResults"> <span class="foundText"><bbbl:label key="lbl_we_have_found" language="${pageContext.request.locale.language}" />
		 <span id="stores">${foundResultsCount}</span>&nbsp;<bbbl:label key="lbl_stores_within" language="${pageContext.request.locale.language}" /> <span class='selectedMiles'>${finalRadius}
         </span><bbbl:label
            key="lbl_store_route_miles" language="${pageContext.request.locale.language}"/></span>
         <div class="storeDropDown" style="display:inline;">
         <a href="javascript:void(0);" class="ajaxDisable" aria-hidden="true">
         <span class="dropDownArrow" aria-hidden="true"><span></a>
         <ul class="dropDownOptions" style="display: none;">
         <c:forTokens items="${radius_range}" delims="," var="item">                                                                                
         <li class="clearfix" data-value="${item}" data-index="0"><span>${item} ${radius_range_type}</span>
         </li>
         </c:forTokens>
         </ul>

         <span class="zipCodeSearch hidden"><c:if test="${!(empty param.searchString)}"> of <span style="font-weight:bold">${fn:escapeXml(param.searchString)}</c:if><span>
         </div><bbbl:label key="lbl_in_your_area" language="${pageContext.request.locale.language}" />.
         </div>
         </c:otherwise>
		 </c:choose>

         <div class="toolTipStoreData">
         <ul class="findStoreResultHeader clearfix">
		 
         <input type="hidden" id="numberOfStores" value="${foundResultsCount}" name="numberOfStores" />
         <li class="column1">
         <bbbl:label key="lbl_store_header" language="${pageContext.request.locale.language}" />
         </li>
         <li class="column2">
         <bbbl:label key="lbl_store_hour" language="${pageContext.request.locale.language}" />
         </li>
         <li class="column3">
         <%--<bbbl:label key="lbl_store_availability" language="${pageContext.request.locale.language}" />--%>
         </li>
         <li class="column4">
         <%--<bbbl:label key="lbl_store_reserve" language="${pageContext.request.locale.language}" />--%>
         </li>
         </ul>
         <div class="findStoreResultWrapper clearfix">
         <div class="wrapper1">
         <div class="wrapper2 viewport clearfix">
         <ul class="findStoreResult noMarTop overview">
         <dsp:droplet name="/atg/dynamo/droplet/ForEach">
         <dsp:param name="array" value="${storeDetailsViewStore}" />
         <dsp:setvalue param="viewDetails" paramvalue="element" />
         <dsp:oparam name="output">
		 <dsp:getvalueof param="index" var="index" />
         <dsp:getvalueof param="viewDetails" var="storeDetail" />
         <dsp:getvalueof value="${storeDetail.storeId}" var="storeId" />
         <dsp:getvalueof var="productChkFlg"
            value="${productAvailableViewStore[storeId]}" vartype="java.lang.Integer"
            scope="page" />
		 <c:choose>
			<c:when test="${storeDetail.storeId == selectedStoreIDForAjax}"></c:when>
			<c:otherwise>
				<%@ include file="/browse/store_list_from_ajax.jspf" %>
			</c:otherwise>
		 </c:choose>
         </dsp:oparam>
         </dsp:droplet>
         </ul>
         </div>
         </div>
         </div>
         </div>
       
         <section class="searchMorelistings">
		 <label for="searchStoreField">
         <div class="searchMoreText">
         <bbbl:label key="lbl_search_for_more_listing" language="${pageContext.request.locale.language}" />
         </div>
		 </label>
         <div class="findStoreSectn">
         <form id="searchStoreToolTip">
             <input id="searchStoreField" type="searchStoreField" name="searchStoreField" placeholder="<bbbl:label key="lbl_findstore_loc" language="${pageContext.request.locale.language}" />">     
         </form>
         <div class="findStoresBtn">
         <button class="btnPrimary btn-block" id="findStores">
         <bbbl:label key="lbl_find_stores" language="${pageContext.request.locale.language}" />
         </button>
         </div>
         <div class="errorMessage hidden">*&nbsp;<bbbl:label key="lbl_field_empty" language="${pageContext.request.locale.language}" /></div>
         </div>
         </section>
         </div>
         </c:otherwise>
         </c:choose>
         </c:otherwise>
         </c:choose>

</dsp:page>
