<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<c:set var="section" value="selfService" scope="request" />
	
<%-- 	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:if test="${currentSiteId eq BedBathCanadaSite}">
		<c:redirect url="/"/>
	</c:if>	
--%>
<%--
	<bbb:pageContainer titleKey="lbl_find_store_title">

		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
		
		<jsp:body>
--%>		
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
    <dsp:getvalueof param="frmCollege" var="frmCollege"/>
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" bean="Profile.userSiteItems"/>
		<dsp:param name="elementName" value="sites"/>
		<dsp:oparam name="output">
			<dsp:getvalueof id="key" param="key"/>
			<c:if test="${currentSiteId eq key}">
				<dsp:getvalueof id="favouriteStoreId" param="sites.favouriteStoreId"/>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
	
	<c:set var="localStoreLinkToYext">
        <bbbc:config key="LOCAL_STORE_LINK_TO_YEXT" configName="FlagDrivenFunctions" />
    </c:set>
	<c:set var="hostNameForYEXTLink"><bbbl:label key="lbl_host_name_for_yext" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="linkDisplayNameForYEXTLink"><bbbl:label key="lbl_link_display_name_for_yext" language="${pageContext.request.locale.language}" /></c:set>

<%--Jsp body starts from here--%>

    <%-- default radius depends on store concept--%>  
 	<c:set var="radius_default_us"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set> 
	<c:set var="radius_default_baby"><bbbc:config key="radius_default_baby" configName="MapQuestStoreType" /></c:set> 
	<c:set var="radius_range_us"><bbbc:config key="radius_range_us" configName="MapQuestStoreType" /></c:set> 
	<c:set var="radius_range_baby"><bbbc:config key="radius_range_baby" configName="MapQuestStoreType" /></c:set> 
	<c:set var="radius_range_type"><bbbc:config key="radius_range_type" configName="MapQuestStoreType" /></c:set> 
	<c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite}">
			<c:set var="radius_default_selected">${radius_default_us}</c:set> 
			<c:set var="radius_range">${radius_range_us}</c:set> 
		</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="radius_default_selected">${radius_default_baby}</c:set> 
			<c:set var="radius_range">${radius_range_baby}</c:set> 
		</c:when>
		<c:otherwise>
			<c:set var="radius_default_selected">${radius_default_us}</c:set> 
			<c:set var="radius_range">${radius_range_us}</c:set> 
		</c:otherwise>
	</c:choose>
 	
 	<%-- only logged in users should see make favorite store buttons --%>
 	
 	<c:set var="enableFavoriteStores" value="false" />
 	<dsp:getvalueof var="isAnnonymousUser" bean="Profile.transient"/>
 	<c:if test="${!isAnnonymousUser}">
 		<c:set var="enableFavoriteStores" value="true" />
 	</c:if>
    							   
		<dsp:a iclass="makeFavorite" href="/store/selfservice/store/find_store.jsp?favouriteStoreId=dummyId"  id="storeLocatorHiddenLink" style="display:none;">
			<bbbl:label key="lbl_find_store_make_fav_store"	language="${pageContext.request.locale.language}" />
			<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
		</dsp:a>
        
        <div id="GetDirectionsV2Wrapper" class="clearfix  grid_12  hidden">	
                
	 		<div id="storeLocatorPrintAddresses" class="container_12 printOnly" >
            	<div id="storeLocatorPrintAddressesOrigin" class="grid_6 alpha "></div>                    
                <div id="storeLocatorPrintAddressesDestination" class="grid_6 omega "></div>
			</div> 
	
            
                
            <div id="storeLocatorResults" class="" style="">
                
                    
                <div id="storeLocatorMapResults" class="grid_7 omega storeLocatorMapResultsFS" <c:if test="${frmCollege == 'true'}">style="height: 481px;"</c:if>></div>
                
                <div id="storeLocatorDirections" class="grid_3 alpha omega ">
                    
                    <div id="storeLocatorDirectionsFormWrapper" class="storeLocatorDirectionsFormWrapperFS">
                                          

                        <h2>Map &amp; Directions</h2>

                        <form id="storeLocatorDirectionsForm">
                            <input type="hidden" name="storeLocatorDirectionsSearchingOnPoiKey" id="storeLocatorDirectionsSearchingOnPoiKey" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLat" id="storeLocatorDirectionsStartingLat" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLng" id="storeLocatorDirectionsStartingLng" value="" />
                            <fieldset>                           
                                <div class="input" id="storeLocatorDirectionsRouteTypeWrapper">
                                    <div class="label">
                                        <bbbl:label key="lbl_findstore_select_route" language="${pageContext.request.locale.language}" />
                                    </div>
                                    <div id="storeLocatorDirectionsRouteTypeRadioGroup">
                                        <input type="radio" name="routeType" id="storeLocatorDirectionsFastest" checked="checked" value="fastest" />
                                        <label for="storeLocatorDirectionsFastest"><bbbl:label key="lbl_findstore_driving" language="${pageContext.request.locale.language}" /></label>

                                        <input type="radio" name="routeType" id="storeLocatorDirectionsTransit" value="multimodal" />
                                        <label for="storeLocatorDirectionsTransit"><bbbl:label key="lbl_findstore_public_transit" language="${pageContext.request.locale.language}" /></label>

                                        <input type="radio" name="routeType" id="storeLocatorDirectionsWalking" value="pedestrian" />
                                        <label for="storeLocatorDirectionsWalking"><bbbl:label key="lbl_findstore_walking" language="${pageContext.request.locale.language}" /></label>
                                    </div>
                                </div>                                
                                <div class="input" id="storeLocatorDirectionsOmniStartWrapper" >
                                    <div class="label">
                                        <label id="lblstoreLocatorDirectionsOmniStartInput" for="storeLocatorDirectionsOmniStartInput"><bbbl:label key="lbl_findstore_from" language="${pageContext.request.locale.language}" /></label>
                                    </div>
                                    <div class="text">
                                    <c:set var="lbl_start_direction_placeholder">
										<bbbl:label key="lbl_start_direction_placeholder" language="${pageContext.request.locale.language}" />
									</c:set>
                                        <input type="text" placeholder="${lbl_start_direction_placeholder}" name="storeLocatorDirectionsOmniStartInput" id="storeLocatorDirectionsOmniStartInput" aria-required="true" aria-labelledby="lblstoreLocatorDirectionsOmniStartInput errorstoreLocatorDirectionsOmniStartInput" />
                                    </div>
                                </div>
                                <div class="input" id="storeLocatorDirectionsOmniEndWrapper" >
                                    <div class="label">
                                        <label id="lblstoreLocatorDirectionsOmniEndInput" for="storeLocatorDirectionsOmniEndInput"><bbbl:label key="lbl_findstore_to" language="${pageContext.request.locale.language}" /></label>
                                    </div>
                                    <div class="text">
                                        <input type="text" placeholder="" name="storeLocatorDirectionsOmniEndInput" id="storeLocatorDirectionsOmniEndInput" aria-required="true" aria-labelledby="lblstoreLocatorDirectionsOmniEndInput errorstoreLocatorDirectionsOmniEndInput" />
                                    </div>
                                </div>
                                
                                <div class="checkboxItem input clearfix">
                                    <div class="checkbox">
                                        <input id="storeLocatorDirectionsAvoidSeasonalRoads" name="storeLocatorDirectionsAvoidSeasonalRoads" value="true" type="checkbox" >                                        
                                    </div>
                                    <div class="label">
                                        <label for="storeLocatorDirectionsAvoidSeasonalRoads"><bbbl:label key="lbl_findstore_avoid_ssonal_roads" language="${pageContext.request.locale.language}" /></label>	
                                    </div>	
                                </div>
                                <div class="checkboxItem input clearfix">
                                    <div class="checkbox">
                                        <input id="storeLocatorDirectionsAvoidHighways" name="storeLocatorDirectionsAvoidHighways" value="true" type="checkbox" >
                                    </div>
                                    <div class="label">
                                        <label for="storeLocatorDirectionsAvoidHighways"><bbbl:label key="lbl_findstore_avoid_highways" language="${pageContext.request.locale.language}" /></label>	
                                    </div>	
                                </div>
                                <div class="checkboxItem input clearfix">
                                    <div class="checkbox">
                                        <input id="storeLocatorDirectionsAvoidTolls" name="storeLocatorDirectionsAvoidTolls" value="true" type="checkbox" >
                                    </div>
                                    <div class="label">
                                        <label for="storeLocatorDirectionsAvoidTolls"><bbbl:label key="lbl_findstore_avoid_toll_road" language="${pageContext.request.locale.language}" /></label>	
                                    </div>	
                                </div>


                                <div id="storeLocatorDirectionsDestination"></div>

                                <div class="button button_active"> 
                                    <input type="submit" value="Get Directions" />
                                </div>                             

                            </fieldset>
                        </form>
                    </div>
                   
                    		<div id="storeLocatorTurnByTurnRouteInfo"></div>
                    		<div id="storeLocatorTurnByTurnDirections"></div>
                   	</div>
            </div>
		</div>
<input id="localStoreLinkToYext" type="hidden" value="${localStoreLinkToYext}"/>
<input id="hostNameForYEXTLink" type="hidden" value="${hostNameForYEXTLink}"/>
<input id="linkDisplayNameForYEXTLink" type="hidden" value="${linkDisplayNameForYEXTLink}"/>
<%--			
	</jsp:body>	
</bbb:pageContainer>
--%>
</dsp:page>
