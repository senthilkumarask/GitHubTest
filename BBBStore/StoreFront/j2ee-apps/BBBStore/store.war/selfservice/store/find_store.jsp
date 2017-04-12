<%-- ====================== Description===================
/**
* This page is used to display store locator functionality in which customer inputs his address, city and state or zip code 
* and details of his nearby location are displayed with the option of viewing map and directions.
**/
--%>

<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<c:set var="section" value="selfService" scope="request" />
	<c:set var="pageWrapper" value="findAStorePage useStoreLocator" scope="request" />
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="localStoreLinkToYext">
		<bbbc:config key="LOCAL_STORE_LINK_TO_YEXT" configName="FlagDrivenFunctions" />
    </c:set>
	<c:set var="hostNameForYEXTLink"><bbbl:label key="lbl_host_name_for_yext" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="linkDisplayNameForYEXTLink"><bbbl:label key="lbl_link_display_name_for_yext" language="${pageContext.request.locale.language}" /></c:set>
	<c:if test="${currentSiteId eq BedBathCanadaSite}">
		<c:redirect url="/"/>
	</c:if>	
	<bbb:pageContainer titleKey="lbl_find_store_title">

		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
		
		<jsp:body>
                    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
                    <dsp:getvalueof param="flashEnabled" id="flashEnabled"/>
			<c:if test="${TellApartOn}">
			<bbb:tellApart actionType="pv" pageViewType="Other"/>
			</c:if>
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
<%--Jsp body starts from here--%>

    <%-- default radius depends on store concept--%>  
 	<c:set var="radius_default_us"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set> 
	<c:set var="radius_default_baby"><bbbc:config key="radius_default_baby" configName="MapQuestStoreType" /></c:set> 
	<c:set var="radius_range_us"><bbbc:config key="radius_range_us" configName="MapQuestStoreType" /></c:set> 
	<c:set var="radius_range_baby"><bbbc:config key="radius_range_baby" configName="MapQuestStoreType" /></c:set> 
	<c:set var="range_type"><bbbc:config key="radius_range_type" configName="MapQuestStoreType" /></c:set>	
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
 	
 	<c:set var="enableFavoriteStores" value="false" scope="session"/>
 	<dsp:getvalueof var="isAnnonymousUser" bean="Profile.transient"/>
 	<c:if test="${!isAnnonymousUser}">
 		<c:set var="enableFavoriteStores" value="true" scope="session"/>
 	</c:if>

  <div id="content" class="container_12 clearfix">
         <div id="findStorePageTitle" class="grid_12 clearfix">
			<h1>
				<bbbl:label key="lbl_find_store_heading"
							language="${pageContext.request.locale.language}" />
			</h1>
			<div class="clear"></div>
		</div>	
		
		<dsp:a iclass="makeFavorite" href="/store/selfservice/store/find_store.jsp?favouriteStoreId=dummyId"  id="storeLocatorHiddenLink" style="display:none;">
			<bbbl:label key="lbl_find_store_make_fav_store"	language="${pageContext.request.locale.language}" />
			<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
		</dsp:a>
        
        <div class="clearfix  grid_12 ">	
                
               
                
                <form id="storeLocatorSearchForm" class="clearfix ">

                    <fieldset class="grid_7" id="storeLocatorOriginFieldset">
					
						<input id="localStoreLinkToYext" type="hidden" value="${localStoreLinkToYext}"/>
						<input id="hostNameForYEXTLink" type="hidden" value="${hostNameForYEXTLink}"/>
						<input id="linkDisplayNameForYEXTLink" type="hidden" value="${linkDisplayNameForYEXTLink}"/>
					
                        <legend><h5> <bbbl:label key="lbl_findstore_store_locator" language="${pageContext.request.locale.language}"/></h5></legend>
                        
                        <div class="input" id="storeLocatorOmnibarContainer">
                            <div class="label">
                                <label id="lblstoreLocatorOmnibarInput" for="storeLocatorOmnibarInput">
                                     <bbbl:label key="lbl_findstore_location" language="${pageContext.request.locale.language}"/>
              							<span id="storeLocatorOmnibarInputWarning" class="warning" style="display:none"> <bbbl:label key="lbl_findstore_location_required" language="${pageContext.request.locale.language}" /></span>
                                </label>
                            </div>
                            <div class="text" style="speak: digits;">
                                <input type="text" placeholder="City and State, or Zip Code" id="storeLocatorOmnibarInput" aria-required="true" aria-labelledby="lblstoreLocatorOmnibarInput storeLocatorResultsMsg errorstoreLocatorOmnibarInput" />
                                
                            </div>
                        </div>
                        
                        <div class="input" id="storeLocatorRadiusContainer">                                
                            <div class="label">
                                <label> <bbbl:label key="lbl_findstore_search_within" language="${pageContext.request.locale.language}" /></label>
                            </div>
                            <div class="select">
                                <select class="uniform" id="storeLocatorRadiusInput" >
                                    <c:forEach var="radius" items="${fn:split(radius_range, ',')}">
                                    	<c:choose> 
								  			<c:when test="${radius_default_selected eq radius}">
								  				<option value="${radius}" selected="selected">${radius} ${range_type}</option>
								  			</c:when>
											<c:otherwise>
												<option value="${radius}">${radius} ${range_type}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="input" id="storeLocatorSubmitContainer">                             
                            <div class="button disabled" id="FindStoreBTN"> 
                                <input type="submit" disabled="disabled"  id="findStoreSubmitButton" value="Find a Store" />
                            </div> 
                        </div>
                        
                        <div class="input" id="storeLocatorUseMyLocationContainer"> 
                            <div class="button button_secondary "> 
                                <input type="button" id="storeLocatorUseMyLocationButton" value="Use My Location" title="This allows us to find the stores closest to you, by automatically detecting your location." />
                            </div> 
                        </div>
                        
                        
                        <div class="input" id="storeLocatorStoreConceptsContainer">
                            <div class="label">
                                <label> <bbbl:label key="lbl_findstore_store" language="${pageContext.request.locale.language}" /></label>
                            </div>
				
                            <input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptBBBY" class="storeLocatorStoreConcept" value="10" /><label for="storeLocatorStoreConceptBBBY">
                            <bbbl:label key="lbl_findstore_bed_beyond" language="${pageContext.request.locale.language}" /></label>
                            <input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptBaby" class="storeLocatorStoreConcept" value="40" /><label for="storeLocatorStoreConceptBaby"> 
                            <bbbl:label key="lbl_findstore_buybuy_Baby" language="${pageContext.request.locale.language}" /> </label>
                            <input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptHarmon" class="storeLocatorStoreConcept" value="30" /><label for="storeLocatorStoreConceptHarmon"> 
                            <bbbl:label key="lbl_findstore_harmon" language="${pageContext.request.locale.language}" /></label>
			</div>
                        
                    </fieldset>	
	
                    <fieldset class="grid_5"  id="storeLocatorFiltersFieldset">
			<legend> <bbbl:label key="lbl_findstore_specialty_dprmt" language="${pageContext.request.locale.language}" /></legend>
			<div class="input" id="storeLocatorSpecialtyContainer">
                            <div class="label">
                                <label> <bbbl:label key="lbl_findstore_specialty_dprmt" language="${pageContext.request.locale.language}" /></label>
                            </div>
                            <span id="iconOf" class="hidden">icon of</span>
                            <div class="storeLocatorSpecialtyItemContainer first">
                                <input aria-labelledby="iconOf label1" type="checkbox" name="storeLocatorFTG" id="storeLocatorFTG" class="storeLocatorSpecialtyDept" value="1" />
                                <label id="label1" for="storeLocatorFTG" title="Fine Tabletop &amp; Giftware">
                                   <span class="storeIcon store_ftg"></span>
                                    <bbbl:label key="lbl_store_gift" language="${pageContext.request.locale.language}" />
                                </label>
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer second">
                                <input aria-labelledby="iconOf label2" type="checkbox" name="storeLocatorWB" id="storeLocatorWB" class="storeLocatorSpecialtyDept" value="1" />
                                <label id="label2" for="storeLocatorWB" title="Wine &amp; Beer">
                                   <span class="storeIcon store_wb"></span> 
                                   <bbbl:label key="lbl_store_beer_wine" language="${pageContext.request.locale.language}" />
                                </label>                                
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer first">
                                <input aria-labelledby="iconOf label3" type="checkbox" name="storeLocatorHBC" id="storeLocatorHBC" class="storeLocatorSpecialtyDept" value="1" />
                                <label id="label3" for="storeLocatorHBC" title="Health &amp; Beauty">
                                     <span class="storeIcon store_hbc"></span> 
                                     <bbbl:label key="lbl_store_health_beauty" language="${pageContext.request.locale.language}" />
                                </label>                                
                            </div>      
                            <div class="storeLocatorSpecialtyItemContainer second">
                                <input aria-labelledby="iconOf label4" type="checkbox" name="storeLocatorPhoto" id="storeLocatorPhoto" class="storeLocatorSpecialtyDept" value="1" />
                                <label id="label4" for="storeLocatorPhoto" title="Photo Studio">
                                    <span class="storeIcon store_photo"></span> 
                                      <bbbl:label key="lbl_findstore_photo_studio" language="${pageContext.request.locale.language}" />
                                </label>                                
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer first">
                                <input aria-labelledby="iconOf label5" type="checkbox" name="storeLocatorWM" id="storeLocatorWM" class="storeLocatorSpecialtyDept" value="1" />
                                <label id="label5" for="storeLocatorWM" title="Specialty Foods">
                                     <span class="storeIcon store_wm"></span> 
                                    <bbbl:label key="lbl_findstore_specialty_foods" language="${pageContext.request.locale.language}" />
                                </label>                                
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer second">
                                <input aria-labelledby="iconOf label6" type="checkbox" name="storeLocatorHD" id="storeLocatorHD" class="storeLocatorSpecialtyDept" value="1" />
                                <label id="label6" for="storeLocatorHD" title="Home Delivery">
                                     <span class="storeIcon store_hd"></span> 
                                   <bbbl:label key="lbl_findstore_home_delivery" language="${pageContext.request.locale.language}" />
                                </label>
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer first">
                                <input aria-labelledby="iconOf label7" type="checkbox" name="storeLocatorBaby" id="storeLocatorBaby" class="storeLocatorSpecialtyDept" value="1" />
                                <label id="label7" for="storeLocatorBaby" title="Baby Department">
                                     <span class="storeIcon store_baby"></span> 
                                     <bbbl:label key="lbl_store_baby_department" language="${pageContext.request.locale.language}" />
                                </label>                                
                            </div>
                            
                            
			</div>
                    </fieldset>		
	
	
                    
            </form>
	
	
	 		<div id="storeLocatorPrintAddresses" class="container_12 printOnly" >
            	<div id="storeLocatorPrintAddressesOrigin" class="grid_6 alpha "></div>                    
                <div id="storeLocatorPrintAddressesDestination" class="grid_6 omega "></div>
			</div> 
	
            <h5 id="storeLocatorResultsMsg" style="display: none;"></h5>
            
            <p id="storeLocatorInfo" style="display: none;"> 
            <bbbl:label key="lbl_findstore_store_hours" language="${pageContext.request.locale.language}" /></p>
            <div id="storeLocatorResults" class="container_12" style="display: none;">
                <div id="storeLocatorAddressResults" class="grid_3 alpha omega "></div>
                    
                <div id="storeLocatorMapResults" class="grid_9 alpha omega " aria-hidden="true"></div>
                
                <div id="storeLocatorDirections" class="grid_3 alpha omega ">
                    
                    <div id="storeLocatorDirectionsFormWrapper">
                        <a id="storeLocatorDirectionsBackToReultsLink" href=""> 
                        <bbbl:label key="lbl_findstore_back_list" language="${pageContext.request.locale.language}" /></a>                  
                        <h2><bbbl:label key="lbl_findstore_directions" language="${pageContext.request.locale.language}" /></h2>
                        <form id="storeLocatorDirectionsForm">
                            <input type="hidden" name="storeLocatorDirectionsSearchingOnPoiKey" id="storeLocatorDirectionsSearchingOnPoiKey" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLat" id="storeLocatorDirectionsStartingLat" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLng" id="storeLocatorDirectionsStartingLng" value="" />
                            <fieldset>           
								<legend class="offScreen"><bbbl:label key="lbl_storeLocator" language="${pageContext.request.locale.language}" /></legend>                
                                      
                                <div class="input" id="storeLocatorDirectionsRouteTypeWrapper">
                                    <div class="label">
                                        <label ><bbbl:label key="lbl_findstore_select_route" language="${pageContext.request.locale.language}" /> </label>
                                    </div>
                                    <div id="storeLocatorDirectionsRouteTypeRadioGroup">
                                        <input type="radio" name="routeType" id="storeLocatorDirectionsFastest" checked="checked" value="fastest" />
                                        <label for="storeLocatorDirectionsFastest"><bbbl:label key="lbl_findstore_driving" language="${pageContext.request.locale.language}" /></label>
                                        <input type="radio" name="routeType" id="storeLocatorDirectionsTransit" value="multimodal" />
                                        <label for="storeLocatorDirectionsTransit"><bbbl:label key="lbl_findStore_PublicTransit" language="${pageContext.request.locale.language}"/></label>

                                        <input type="radio" name="routeType" id="storeLocatorDirectionsWalking" value="pedestrian" />
                                        <label for="storeLocatorDirectionsWalking"> <bbbl:label key="lbl_findstore_walking" language="${pageContext.request.locale.language}" /></label>
                                    </div>
                                </div>                                
                                <div class="input" id="storeLocatorDirectionsOmniStartWrapper" >
                                    <div class="label">
                                        <label id="lblstoreLocatorDirectionsOmniStartInput" for="storeLocatorDirectionsOmniStartInput"> <bbbl:label key="lbl_findstore_from" language="${pageContext.request.locale.language}" /></label>
                                    </div>
                                    <div class="text">
                                        <input type="text" placeholder="street, city, state, zip" name="storeLocatorDirectionsOmniStartInput" id="storeLocatorDirectionsOmniStartInput" aria-required="true" aria-labelledby="lblstoreLocatorDirectionsOmniStartInput errorstoreLocatorDirectionsOmniStartInput" />
                                    </div>
                                </div>
                                <div class="input" id="storeLocatorDirectionsOmniEndWrapper" >
                                    <div class="label">
                                        <label id="lblstoreLocatorDirectionsOmniEndInput" for="storeLocatorDirectionsOmniEndInput"> <bbbl:label key="lbl_findstore_to" language="${pageContext.request.locale.language}" /></label>
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
                                        <label for="storeLocatorDirectionsAvoidSeasonalRoads"> <bbbl:label key="lbl_findstore_avoid_ssonal_roads" language="${pageContext.request.locale.language}" /> </label>	
                                    </div>	
                                </div>
                                <div class="checkboxItem input clearfix">
                                    <div class="checkbox">
                                        <input id="storeLocatorDirectionsAvoidHighways" name="storeLocatorDirectionsAvoidHighways" value="true" type="checkbox" >
                                    </div>
                                    <div class="label">
                                        <label for="storeLocatorDirectionsAvoidHighways"> <bbbl:label key="lbl_findstore_avoid_highways" language="${pageContext.request.locale.language}" /> </label>	
                                    </div>	
                                </div>
                                <div class="checkboxItem input clearfix">
                                    <div class="checkbox">
                                        <input id="storeLocatorDirectionsAvoidTolls" name="storeLocatorDirectionsAvoidTolls" value="true" type="checkbox" >
                                    </div>
                                    <div class="label">
                                        <label for="storeLocatorDirectionsAvoidTolls"> <bbbl:label key="lbl_findstore_avoid_toll_road" language="${pageContext.request.locale.language}" /> </label>	
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


    </div>
			
  
	<c:if test="${RKGOn}">
	<script type="text/javascript">
	var size= '${size}';
	if(size.length > 0){
		//rkg micro pixel
		var appid = '${currentSiteId}';
		var type = 'storeloc';
		$(function () {
		rkg_micropixel(appid,type);
		});
	}
        </script>
        </c:if>

<%--Jsp body Ends here--%>	
	</jsp:body>
	<jsp:attribute name="footerContent">
	<script>
		if (typeof s !== 'undefined') {
			s.pageName ='My Account>Find a Store';
			s.channel = 'My Account';
			s.prop1 = 'My Account';
			s.prop2 = 'My Account';
			s.prop3 = 'My Account';
			s.prop6='${pageContext.request.serverName}'; 
			s.eVar9='${pageContext.request.serverName}';
			var s_code = s.t();
			if (s_code)
			document.write(s_code);
		}
		 if(BBB.oQueryData.favStoreId){
            $("input[id=storeLocatorOmnibarInput]").val(BBB.oQueryData.favStoreId);
            setTimeout(function(){ 
              $("form#storeLocatorSearchForm").trigger("submit");
            }, 1000);
            setTimeout(function(){ 
               $("input[id=storeLocatorOmnibarInput]").val(BBB.oQueryData.favStoreId);
            }, 3500);
        }
	</script>
	</jsp:attribute>
</bbb:pageContainer>
</dsp:page>
