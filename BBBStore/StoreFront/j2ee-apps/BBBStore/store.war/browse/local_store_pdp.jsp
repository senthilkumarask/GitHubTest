<dsp:page>
  
   <dsp:getvalueof var="isInternationalCustomer" bean="/com/bbb/profile/session/SessionBean.internationalShippingContext" />
   <dsp:getvalueof var="sddEligibleLandingZip" bean="/com/bbb/profile/session/SessionBean.landingZipcodeVO.sddEligibility" />
   <dsp:getvalueof var="sddEligibleCurrentZip" bean="/com/bbb/profile/session/SessionBean.currentZipcodeVO.sddEligibility" />
   <dsp:getvalueof var="sddAttribs" param="sddAttribs" />
   <dsp:getvalueof var="isMultiSku" param="isMultiSku" />
   <dsp:getvalueof var="isSkuSelected" param="isSkuSelected" />
   <dsp:getvalueof var="isLeadSKU" param="isLeadSKU" />
   <dsp:getvalueof var="collection" param="collection" />
   <dsp:getvalueof var="vdcSku" param="vdcSku" />
   
   <%-- sddAttribs is passed only for single and multi sku product and not when we click on Find in Store link on PDP --%>
   <c:choose>
     <c:when test="${((sddEligibleLandingZip eq 'true') || (sddEligibleCurrentZip eq 'true')) && isInternationalCustomer ne true}">
      <c:set var="sddEligible" value="true"/>
     </c:when>
     <c:otherwise>
      <c:set var="sddEligible" value="false"/>
     </c:otherwise>
   </c:choose>
   <c:choose>
      <c:when test="${isInternationalCustomer ne true}">
          <c:set var="supplyBalance">
            <bbbc:config key="SUPPLY_BALANCE_ON" configName="FlagDrivenFunctions" />
         </c:set>
         
         <c:set var="localStorePDP">
              <bbbc:config key="LOCAL_STORE_PDP_FLAG" configName="FlagDrivenFunctions" />
         </c:set>
          
          <dsp:getvalueof param="productId" var="productId"/>
         <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
         <dsp:getvalueof param="bopusNotAllowed" var="bopusNotAllowed"/>
         <dsp:getvalueof param="ltlItem" var="ltlItem"/>
         
         <dsp:getvalueof param="customizationOffered"
            var="customizationOffered"/>
         <dsp:getvalueof param="itemAlreadyPersonalized"
            var="itemAlreadyPersonalized"/>
         <dsp:getvalueof param="customizableRequired"
            var="customizableRequired"/>
         <div class="storeInfoSection marTop_10">
            <div class="txtOrderHeading">
              <bbbl:textArea key="txt_sdd_how_you_want_order"
              language="${pageContext.request.locale.language}" />
            </div>
           
       <c:choose>
         <c:when test="${sddEligible eq 'true'}">
          <div class="shipTime col33">
         </c:when>
         <c:otherwise>
          <div class="shipTime col50">
         </c:otherwise>
       </c:choose>
               <div></div>
               <strong>Shipping</strong>
               <h6>
                <bbbl:label key="lbl_usually_ships_in_hours" language="${pageContext.request.locale.language}" />
                </h6>
               <a href="${contextPath}/browse/shipping_policies.jsp" class="popupShipping"><bbbl:label key="lbl_shipping_details_costs" language="${pageContext.request.locale.language}" /></a>
               <!-- <div class="separator"></div> -->
            </div>
            <%--<c:when
                  test="${(localStorePDP eq false && supplyBalance eq false) || bopusNotAllowed eq true || ltlItem eq true|| (customizationOffered eq true && 
                  itemAlreadyPersonalized eq true) || (customizableRequired eq true)}"> --%>
                  <c:if test="${vdcSku eq 'true' && ltlItem ne true && customizationOffered ne true && itemAlreadyPersonalized ne true && customizableRequired ne true}">
                  <c:set var="vdcPassed" value="true"/>
                  </c:if>
            <c:choose>
               <c:when
                  test="${(localStorePDP eq false && supplyBalance eq false) || (bopusNotAllowed eq true || ltlItem eq true|| (customizationOffered eq true && 
                  itemAlreadyPersonalized eq true) || (customizableRequired eq true)) && !(vdcPassed eq 'true') }">
                  
          <c:choose>
             <c:when test="${sddEligible eq 'true'}">
              <div class="storeInfoDetails col33">
             </c:when>
             <c:otherwise>
              <div class="storeInfoDetails col50">
             </c:otherwise>
           </c:choose>
          
                     <div></div>
                     <strong>Store Pickup</strong>
                     <div class='favouriteStoreDetails'>
                        
                        <h6>
                           <bbbl:label key="lbl_pick_up_not_available" language="${pageContext.request.locale.language}" />
                        </h6>

                     </div>
                  </div>
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
    
                  
           <c:choose>
             <c:when test="${sddEligible eq 'true'}">
              <div class="storeInfoDetails col33">
             </c:when>
             <c:otherwise>
              <div class="storeInfoDetails col50">
             </c:otherwise>
           </c:choose>
          

                     <div></div>
                  
                     <c:if test="${localStorePDP eq true&&(!isMultiSku||(isMultiSku&&isSkuSelected))}">
                     <div class="optionalStore"><strong>Store Pickup</strong></div>
                     </c:if>
                     
                     
                     <div class='favouriteStoreDetails'>
                     
             <c:choose>
			    
                <c:when test="${localStorePDP eq false ||(isMultiSku&&!isSkuSelected)}">
                  <h6>
                     <bbbl:label key="lbl_pick_up_in_your_local_store" language="${pageContext.request.locale.language}" />
                  </h6>   
                  <input type="button"  class="button-Small btnRegistrySecondary babyColor findStores" value="<bbbl:label key='lbl_pdp_product_find_store' language='${pageContext.request.locale.language}'/>">
                   <dsp:getvalueof param="latitude" var="lat"/>
                  <dsp:getvalueof param="longitude" var="lng"/>
              
                  <c:set var="latLngCookie" value="${cookie['latLngCookie']}"/>
                   <c:if test="${(latLngCookie eq null || empty latLngCookie) && (favStoreId eq null || empty favStoreId) && (lat eq null || empty lat) &&(lng eq null || empty lng)}">
                      <input type="hidden" name="emptyInput" value="true"/>
                   </c:if>
                </c:when>
                  <c:otherwise>
                  <dsp:getvalueof param="favouriteStore" var="favouriteStore"/>
                  <c:if test="${favouriteStore eq true }">
                    <dsp:droplet name="BBBFindStoreOnPDPDroplet">
                     <dsp:param name="skuId" param="skuId" />
                     <dsp:param name="searchString" param="searchStore" />
                     <dsp:param name="radius" param="radius" />
                     <dsp:param name="favouriteStore" param="favouriteStore" />
                     <dsp:param name="storeId" value="${favStoreId}" />
                     <dsp:param name="favStoreId" value="${favStoreId}" />
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
                                <a class="viewStoreDetails" onclick="javascript:findInStore('${fn:escapeXml(productId)}')">
                                    <bbbl:label key="lbl_see_more_stores" language="${pageContext.request.locale.language}" />
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
                                 <bbbl:label key="lbl_find_in_your_store" language="${pageContext.request.locale.language}" />
                              </h6>
                              <a href="#" class="findStores" data-field-storeid='${storeId}'>Find Stores</a>
                              <!--<input type="hidden" data-field-storeid='${storeId}' class="button-Small btnRegistrySecondary findStores" value="Find Stores ">-->
                            
                           </c:when>
                           <c:otherwise>
                              <c:if test="${localStorePDP eq true && empty emptyInput}">
                                 <h6>
                                    <span>
                                       ${storeDetailsFavStore[0].storeName} 
                                       <bbbl:label key="lbl_store_header" language="${pageContext.request.locale.language}" />
                                    </span>
                                 </h6>
                              </c:if>
                              <c:if test="${(supplyBalance eq true && localStorePDP eq true && vdcPassed ne 'true')}">
                 <a href="#" class="reserveNow" data-field-storeid='${storeId}'>
                  <bbbl:label key="lbl_storepickup_reserve_now" language="${pageContext.request.locale.language}" />
                 </a>
                                 <!--<input type="hidden" data-field-storeid='${storeId}' class="button-Small btnRegistrySecondary reserveNow" value="Reserve Now ">-->
                               </c:if>
                               <c:if test="${(supplyBalance eq true && localStorePDP eq true && vdcPassed eq 'true')}">
                               <h6 class="availability-label"><bbbl:label key="lbl_pdp_please_call_for_store_availability" language="${pageContext.request.locale.language}" /></h6>
                               </c:if>
                              <c:if test="${(supplyBalance eq false && localStorePDP eq true)}">
                                 <h6>
                                    <bbbl:label key="lbl_call_store_for_availability" language="${pageContext.request.locale.language}" />
                                 </h6>
                              </c:if>
                              <c:if test="${!(supplyBalance eq false && localStorePDP eq false)}">
                                 <a class="viewStoreDetails" onclick="javascript:findInStore('${fn:escapeXml(productId)}')">
                                    <bbbl:label key="lbl_see_more_stores" language="${pageContext.request.locale.language}" />
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
          <c:if test="${sddEligible eq 'true'}">
          <dsp:droplet name="/com/bbb/commerce/browse/droplet/BBBDisplaySDDEligibilityOnPDPDroplet">
           <dsp:param name="skuId" param="skuId" />
           <dsp:param name="productId" param="productId" />
           <dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id" />
           <dsp:param name="inputZip" value="inputZip" />
           <dsp:param name="sddAttribs" value="${fn:escapeXml(sddAttribs)}" />
           <dsp:oparam name="message">
            <dsp:getvalueof param="message" var="message" />
           </dsp:oparam>
           <dsp:oparam name="availableStatus">
            <dsp:getvalueof param="availableStatus" var="availableStatus" />
           </dsp:oparam>
          </dsp:droplet>
			<div class="sameDayDelivery col33">
			    <span class="sdd-icon"></span>
	        <div class="ssdMsgBox">
			        <strong><bbbl:label key="lbl_sdd_heading" language="${pageContext.request.locale.language}" /></strong>
			        <div class="visuallyhidden"></div>
			        <c:choose>
			            <c:when test="${availableStatus == 'available'}">
			                <span class="availableMsg" aria-hidden="true">${message}</span>
			                <a href="javascript:;" class="sameDayDeliveryTooltip"><bbbl:label key="lbl_sdd_change_zip" language="${pageContext.request.locale.language}" /></a>
			            </c:when>
			            <c:when test="${availableStatus == 'ineligible'}">
			                <span class="unavailableMsg" aria-hidden="true">${message}</span>
			                <c:set var="modeDetailsLabel">
			                    <bbbl:label key="lbl_sdd_more_details" language="${pageContext.request.locale.language}" />
			                </c:set>
			                <c:if test="${!empty modeDetailsLabel }">
			                	<a href="javascript:;" class="sameDayDeliveryInfoTooltip" aria-describedby="sddExclusion">${modeDetailsLabel}</a>
			            	</c:if>
			            	<a href="javascript:;" class="sameDayDeliveryTooltip"><bbbl:label key="lbl_sdd_change_zip" language="${pageContext.request.locale.language}" /></a>
			            </c:when>
			            <c:when test="${availableStatus eq 'product_sdd_eligible' || availableStatus eq 'product_sdd_ineligible' || availableStatus eq 'product_sdd_market_ineligible'}">
			                ${message}
			            </c:when>
			            <c:otherwise>
			                <span class="unavailableMsg" aria-hidden="true">${message}</span>
			                <a href="javascript:;" class="sameDayDeliveryTooltip"><bbbl:label key="lbl_sdd_change_zip" language="${pageContext.request.locale.language}" /></a>
			            </c:otherwise>
			        </c:choose>
			    </div>
			</div>


         </div>
         </c:if>
                 
         </div>

         <div class="toolTipStoreDetails">
          <dsp:getvalueof param="favouriteStore" var="favouriteStore"/>
         <c:if test="${favouriteStore eq false }">
                      <dsp:droplet name="BBBFindStoreOnPDPDroplet">
                     <dsp:param name="skuId" param="skuId" />
                     <dsp:param name="searchString" param="searchStore" />
                     <dsp:param name="radius" param="radius" />
                     <dsp:param name="favouriteStore" param="favouriteStore" />
                     <dsp:param name="storeId" value="${favStoreId}" />
                     <dsp:param name="favStoreId" value="${favStoreId}" />
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
          <c:choose>
          <c:when test="${emptyInput ne null && not empty emptyInput }">
            <h6 class="noLatLong" style="margin: 10px 0px 0px 35%;"></h6>
            <div class="foundResults hidden"> <span class="foundText"><bbbl:label key="lbl_we_have_found" language="${pageContext.request.locale.language}" /> <span id="stores">${fn:length
         (storeDetailsViewStore)}</span> <bbbl:label key="lbl_stores_within" language="${pageContext.request.locale.language}" /> <span class='selectedMiles'>${finalRadius}
         </span><bbbl:label
            key="lbl_store_route_miles" language="${pageContext.request.locale.language}"/></span>
         <div class="storeDropDown" style="display:inline;">
         <a href="javascript:void(0);" class="ajaxDisable">
         <span class="dropDownArrow"><span></a>
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
         <div class="foundResults"> <span class="foundText"><bbbl:label key="lbl_we_have_found" language="${pageContext.request.locale.language}" /> <span id="stores">${fn:length
         (storeDetailsViewStore)}</span> <bbbl:label key="lbl_stores_within" language="${pageContext.request.locale.language}" /> <span class='selectedMiles'>${finalRadius}
         </span><bbbl:label
            key="lbl_store_route_miles" language="${pageContext.request.locale.language}"/></span>
         <div class="storeDropDown" style="display:inline;">
         <a href="javascript:void(0);" class="ajaxDisable">
         <span class="dropDownArrow"><span></a>
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
         <input type="hidden" id="numberOfStores" value="${fn:length(storeDetailsViewStore)}" name="numberOfStores" />
         <li class="column1">
         <bbbl:label key="lbl_store_header" language="${pageContext.request.locale.language}" />
         </li>
         <li class="column2">
         <bbbl:label key="lbl_store_hour" language="${pageContext.request.locale.language}" />
         </li>
         <li class="column3">
         <bbbl:label key="lbl_store_availability" language="${pageContext.request.locale.language}" />
         </li>
         <li class="column4">
         <bbbl:label key="lbl_store_reserve" language="${pageContext.request.locale.language}" />
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
         <dsp:getvalueof param="viewDetails" var="storeDetail" />
         <dsp:getvalueof value="${storeDetail.storeId}" var="storeId" />
         <dsp:getvalueof var="productChkFlg"
            value="${productAvailableViewStore[storeId]}" vartype="java.lang.Integer"
            scope="page" />
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
        <c:choose> 
        <c:when test="${callStore eq true || (emptyDomResponse ne null && not empty emptyDomResponse)}">
          <span style="font-weight: bold;"><bbbl:label key="lbl_call_store_for_availability" 
            language="${pageContext.request.locale.language}" /></span>
        </c:when>
        <c:otherwise>
         <c:choose>
         <c:when test="${productChkFlg==0}">
         <li class="column3 itemAvailability"><span class="inStock"><bbbl:label
            key="lbl_find_in_store_instock"
            language="${pageContext.request.locale.language}" /> </span></li>
         </c:when>
         <%-- Coded to remove this state at SearchInStoreDroplet:320--%>
         <c:when test="${productChkFlg==2}">
         <li class="column3 itemAvailability"><span class="lowStock"><bbbl:label
            key="lbl_find_in_store_lowstock"
            language="${pageContext.request.locale.language}" /> </span></li>
         </c:when>
         <c:when test="${productChkFlg==1 || productChkFlg==101}">
         <li class="column3 itemAvailability"><span
            class="outOfStock"><bbbl:label
            key="lbl_find_in_store_unavailable"
            language="${pageContext.request.locale.language}" /> </span></li>
         </c:when>
         <c:when test="${productChkFlg==100 || productChkFlg==102}">
         <%-- Available but Store pickup not available in this state store --%>
         <li class="column3 itemAvailability"><span
            class="inStock"><bbbl:label
            key="lbl_find_in_store_instock"
            language="${pageContext.request.locale.language}" /> </span>
         <span class="reserveOnline"><bbbl:label key="lbl_findinstore_reserveonline"
            language="${pageContext.request.locale.language}" /> </span></li>
         </c:when>
         <c:when test="${productChkFlg==-7}">
          <li class="column3 itemAvailability"><span
            style="font-weight: bold;"><bbbl:label
                key="lbl_call_store_for_availability"
                language="${pageContext.request.locale.language}" /> </span></li>

    </c:when>
         <c:otherwise>
         <li class="column3 itemAvailability"><span
            class="inStock"><bbbl:label
            key="lbl_find_in_store_instock"
            language="${pageContext.request.locale.language}" /> </span>
         <span class="reserveOnline"><bbbl:label key="lbl_findinstore_reserveonline"
            language="${pageContext.request.locale.language}" /> </span></li>
         </c:otherwise>
         </c:choose>
         </c:otherwise>
         </c:choose>
         
         
        <c:if test="${(supplyBalance eq true)}">
          <c:if test="${productChkFlg==0 || productChkFlg==2}">
            <li class="column4 noPadRight resultStoreWrapper">
              <c:choose>
                <c:when test="${vdcPassed ne 'true'}">
                  <input type="button" data-field-storeid='${storeId}' class="button-Med btnPrimary storeAddtoCart reserveNow" value="Reserve Now" />
                </c:when>
                <c:otherwise>
                  <span class="outOfStock"><bbbl:label key="lbl_pdp_find_in_store_call_for_availability" language="${pageContext.request.locale.language}" /></span>
                </c:otherwise>
              </c:choose>
            </li>
          </c:if>
        </c:if>
         </ul>
         </li>
         </dsp:oparam>
         </dsp:droplet>
         </ul>
         </div>
         </div>
         </div>
         </div>
       
         <section class="searchMorelistings">
         <div class="searchMoreText">
         <bbbl:label key="lbl_search_for_more_listing" language="${pageContext.request.locale.language}" />
         </div>
         <div class="findStoreSectn">
         <form id="searchStoreToolTip">
             <input type="searchStoreField" name="searchStoreField" placeholder="<bbbl:label key="lbl_findstore_loc" language="${pageContext.request.locale.language}" />">     
         </form>
         <div class="findStoresBtn">
         <button class="btnPrimary btn-block" id="findStores">
         <bbbl:label key="lbl_find_stores" language="${pageContext.request.locale.language}" />
         </button>
         </div>
         <div class="errorMessage hidden">*&nbsp;<bbbl:label key="lbl_field_empty" language="${pageContext.request.locale.language}" /></div>
         </div>
         </section>
         <p><bbbl:textArea key="txt_disclaimer_pdp"
            language="${pageContext.request.locale.language}" /></p>
         </div>
        </c:otherwise>
         </c:choose>
         </c:otherwise>
         </c:choose>
      </c:when>
      <c:otherwise>
         <div class="storeInfoSection marTop_10">
         <div class="txtOrderHeading">
              <bbbl:textArea key="txt_sdd_how_you_want_order"
              language="${pageContext.request.locale.language}" />
            </div>
      <c:choose>
         <c:when test="${sddEligible eq 'true'}">
          <div class="shipTime col33">
         </c:when>
         <c:otherwise>
          <div class="shipTime col50">
         </c:otherwise>
       </c:choose>
  
            
               <div></div>
               <strong>Shipping</strong>
               <h6>
                  <bbbl:label key="lbl_usually_ships_in_hours" language="${pageContext.request.locale.language}" />
               </h6>
               <a href="${contextPath}/browse/shipping_policies.jsp" class="popupShipping"><bbbl:label key="lbl_shipping_details_costs" language="${pageContext.request.locale.language}" /></a>
               <!-- <div class="separator"></div> -->
            </div>
            
      <c:choose>
         <c:when test="${sddEligible eq 'true'}">
          <div class="storeInfoDetails col33">
         </c:when>
         <c:otherwise>
          <div class="storeInfoDetails col50">
         </c:otherwise>
       </c:choose>
               <div></div>
               <strong>Store Pickup</strong> 
               <div class='favouriteStoreDetails'>
                  
                  <h6>
                     <bbbl:label key="lbl_pick_up_not_available" language="${pageContext.request.locale.language}" />
                  </h6>
               </div>
            </div>
            
      </c:otherwise>
   </c:choose>
   <c:if test="${sddEligible eq 'true'}">
	  <dsp:droplet name="/com/bbb/commerce/browse/droplet/BBBDisplaySDDEligibilityOnPDPDroplet">
	     <dsp:param name="skuId" param="skuId" />
	     <dsp:param name="productId" param="productId" />
	    <dsp:param name="sddAttribs" value="${fn:escapeXml(sddAttribs)}" />
	     <dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id" />
	     <dsp:param name="inputZip" value="inputZip" />
	     <dsp:oparam name="message">
	      <dsp:getvalueof param="message" var="message" />
	     </dsp:oparam>
	     <dsp:oparam name="availableStatus">
	      <dsp:getvalueof param="availableStatus" var="availableStatus" />
	     </dsp:oparam>
	    </dsp:droplet>
	    <div class="sameDayDelivery col33">
		    <span class="sdd-icon"></span>
		    <div class="ssdMsgBox">
		        <strong><bbbl:label key="lbl_sdd_heading" language="${pageContext.request.locale.language}" /></strong>
		        <div class="visuallyhidden"></div>
		        <c:choose>
		            <c:when test="${availableStatus == 'available'}">
		                <span class="availableMsg" aria-hidden="true">${message}</span>
		                <a href="javascript:;" class="sameDayDeliveryTooltip"><bbbl:label key="lbl_sdd_change_zip" language="${pageContext.request.locale.language}" /></a>
		            </c:when>
		            <c:when test="${availableStatus == 'ineligible'}">
		                <span class="unavailableMsg" aria-hidden="true">${message}</span>
		                <c:set var="modeDetailsLabel">
		                    <bbbl:label key="lbl_sdd_more_details" language="${pageContext.request.locale.language}" />
		                </c:set>
		                <c:if test="${!empty modeDetailsLabel}">
		                	<a href="javascript:;" class="sameDayDeliveryInfoTooltip" aria-describedby="sddExclusion">${modeDetailsLabel}</a>
		                </c:if>
		                <a href="javascript:;" class="sameDayDeliveryTooltip"><bbbl:label key="lbl_sdd_change_zip" language="${pageContext.request.locale.language}" /></a>
		            </c:when>
		            <c:when test="${availableStatus eq 'product_sdd_eligible' || availableStatus eq 'product_sdd_ineligible' || availableStatus eq 'product_sdd_market_ineligible'}">
		                ${message}
		            </c:when>
		            <c:otherwise>
		                <span class="unavailableMsg" aria-hidden="true">${message}</span>
		                <a href="javascript:;" class="sameDayDeliveryTooltip"><bbbl:label key="lbl_sdd_change_zip" language="${pageContext.request.locale.language}" /></a>
		            </c:otherwise>
		        </c:choose>
		    </div>
		</div>
		
		</div>
   </c:if>

   
</dsp:page>
