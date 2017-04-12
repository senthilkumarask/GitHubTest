<dsp:page>
	<dsp:importbean bean="/com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	
	<c:set var="section" value="selfService" scope="request" />
	
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<%-- <c:if test="${currentSiteId eq TBS_BedBathCanadaSite}">
		<c:redirect url="/"/>
	</c:if> --%>	

<%--
	<bbb:pageContainer titleKey="lbl_find_store_title">

		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
		
		<jsp:body>
--%>		
    <dsp:getvalueof id="currentSiteId" bean="Site.id" />

	<dsp:droplet name="ForEach">
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
	<c:set var="radius_default_ca"><bbbc:config key="radius_default_ca" configName="MapQuestStoreType" /></c:set>
	<c:set var="radius_range_us"><bbbc:config key="radius_range_us" configName="MapQuestStoreType" /></c:set> 
	<c:set var="radius_range_baby"><bbbc:config key="radius_range_baby" configName="MapQuestStoreType" /></c:set> 
	<c:set var="radius_range_ca"><bbbc:config key="radius_range_ca" configName="MapQuestStoreType" /></c:set>
	<c:set var="radius_range_type"><bbbc:config key="radius_range_type" configName="MapQuestStoreType" /></c:set> 
	<c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathUSSite}">
			<c:set var="radius_default_selected">${radius_default_us}</c:set> 
			<c:set var="radius_range">${radius_range_us}</c:set> 
		</c:when>
		<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
			<c:set var="radius_default_selected">${radius_default_baby}</c:set> 
			<c:set var="radius_range">${radius_range_baby}</c:set> 
		</c:when>
		<c:otherwise>
			<c:set var="radius_default_selected">${radius_default_ca}</c:set> 
			<c:set var="radius_range">${radius_range_ca}</c:set> 
		</c:otherwise>
	</c:choose>
 	
 	<%-- only logged in users should see make favorite store buttons --%>
 	<dsp:droplet name="Switch">
	    <dsp:param name="value" bean="Profile.transient"/>
	    <dsp:oparam name="false">
	    	<c:set var="enableFavoriteStores" value="true" />        
	    </dsp:oparam>
	    <dsp:oparam name="true">
			<c:set var="enableFavoriteStores" value="false" />        
		</dsp:oparam>
	</dsp:droplet>                                              

	<%-- <style type="text/css">
		#storeLocatorMapResults {width: 561px !important; margin-left:229px;}
		#storeLocatorDirectionsFormWrapper h2 {margin-top:0;}
	</style> --%>

	<style>               
         /* search results styles - layout */
         #content, #storeLocatorResults {width: auto;}
         #storeLocatorOriginFieldset {width: auto; padding:0 10px;}
         #storeLocatorMapResults {width: 600px;float:right;}
         #storeLocatorResults, 
         #storeLocatorMapResults, 
         #storeLocatorAddressResults, 
         #storeLocatorDirections {  height:600px; }
          #storeLocatorDirections {  width:390px; }
         #storeLocatorDirectionsFormWrapper {height: 390px; /* 240 */}
         #storeLocatorTurnByTurnRouteInfo {height: 20px;}
         #storeLocatorTurnByTurnDirections {height: 190px; /* 340 */ overflow-y: auto;}
         
         /* custom estore hiding of elements */
         #storeLocatorDirectionsRouteTypeRadioGroup,
         #storeLocatorDirectionsForm div.checkboxItem {/* display: none; */}              
         #footer, #footerPs { display: none; }
	
		@media only screen and (min-width: 28.5em) and (max-width: 48em) { /* smartphones, Android phones, landscape iPhone */ 
			#storeLocatorResults{height: auto;width:100%;}
			#storeLocatorMapResults {width: 71vw; height:400px;float:none;margin-top:10px;}
			#storeLocatorDirections {  width: 100%; height:170vh;margin-top:20px;}
			#storeLocatorDirections{position: static;}
		}
		@media only screen and (min-width: 48em) and (max-width: 64em) { /* portrait tablets, portrait iPad, e-readers (Nook/Kindle), landscape 800x480 phones (Android) */
			#storeLocatorResults{height: auto;width:100%;}
			#storeLocatorMapResults {width: 72vw; height:400px;float:none;margin-top:10px;}
			#storeLocatorDirections {  width: 100%; height:100vh;margin-top:20px;}
			#storeLocatorDirections{position: static;}
		}
		@media only screen and (max-width: 28.5em) {/* smartphones, portrait iPhone, portrait 480x320 phones (Android) */
			#storeLocatorResults{height: auto;width:100%;}
			#storeLocatorMapResults {width: 80vw; height:400px;float:none;margin-top:20px;}
			#storeLocatorDirections {  width: 100%; height:100vh;margin-top:20px;}
			#storeLocatorDirections{position: static;}
		}
		
		

		 
    </style>
	<!-- <link rel="stylesheet" type="text/css" href="/store/selfservice/store/map.css" /> 
    <script src="//www.mapquestapi.com/sdk/js/v7.0.s/mqa.toolkit.js?key=Gmjtd%7Clu6120u8nh%2C2w%3Do5-lwt2l"></script>
    <script src="/store/selfservice/store/map.js"></script>-->
   
    <script type="text/javascript">
    
    $(document).ready(function() {
        var options = {
                radius:'${radius_default_selected}',
                //,favoriteStoreID : '${favouriteStoreId}'
                conceptID: '${currentSiteId}',
                enableFavoriteStores : false, 
                debug: false,
                usePrintPage: true
            };
            
        BBB.BBBStoreLocator.init(options);
       // BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden
    });
    
    </script> 
	                                               

												   
		<%-- <dsp:a iclass="button" href="/tbs/selfservice/store/find_store.jsp?favouriteStoreId=dummyId"  id="storeLocatorHiddenLink" style="display:none;">
		   <bbbl:label key="lbl_find_store_make_fav_store" language="${pageContext.request.locale.language}" />
		   <dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
	   </dsp:a> --%>										   
      <a id="storeLocatorDirectionsBackToLink"  class="hidden">&lt; Back to List</a> 
        <div id="GetDirectionsV2Wrapper" class="row">	
                
	 		<div id="storeLocatorPrintAddresses" class="container_12 printOnly" >
            	<div id="storeLocatorPrintAddressesOrigin" class="grid_6 alpha "></div>                    
                <div id="storeLocatorPrintAddressesDestination" class="grid_6 omega "></div>
			</div> 
	
            
                
            <div id="storeLocatorResults" class="" style="">
                
                    
                <div id="storeLocatorMapResults" class="" ></div>
                
                <div id="storeLocatorDirections" class=" ">
                    
                    <div id="storeLocatorDirectionsFormWrapper">
                                          

                        <h2>Map &amp; Directions</h2>

                        <form id="storeLocatorDirectionsForm">
                            <input type="hidden" name="storeLocatorDirectionsSearchingOnPoiKey" id="storeLocatorDirectionsSearchingOnPoiKey" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLat" id="storeLocatorDirectionsStartingLat" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLng" id="storeLocatorDirectionsStartingLng" value="" />
                                
                                                 
                                <div class="input" id="storeLocatorDirectionsRouteTypeWrapper">
                                    <div class="label">
                                        <label >Select A Route Option </label>
                                    </div>
                                    <div id="storeLocatorDirectionsRouteTypeRadioGroup">
                                        <input type="radio" name="routeType" id="storeLocatorDirectionsFastest" checked="checked" value="fastest" />
                                        <label for="storeLocatorDirectionsFastest">Driving</label>

                                        <input type="radio" name="routeType" id="storeLocatorDirectionsTransit" value="multimodal" />
                                        <label for="storeLocatorDirectionsTransit">Public Transit</label>

                                        <input type="radio" name="routeType" id="storeLocatorDirectionsWalking" value="pedestrian" />
                                        <label for="storeLocatorDirectionsWalking">Walking</label>
                                    </div>
                                </div>                                
                                <div class="input" id="storeLocatorDirectionsOmniStartWrapper" >
                                    <div class="label">
                                        <label id="lblstoreLocatorDirectionsOmniStartInput" for="storeLocatorDirectionsOmniStartInput">From</label>
                                    </div>
                                    <div class="text">
                                        <input type="text" placeholder="street, city, state, zip" name="storeLocatorDirectionsOmniStartInput" id="storeLocatorDirectionsOmniStartInput" aria-required="true" aria-labelledby="lblstoreLocatorDirectionsOmniStartInput errorstoreLocatorDirectionsOmniStartInput" />
                                    </div>
                                </div>
                                <div class="input" id="storeLocatorDirectionsOmniEndWrapper" >
                                    <div class="label">
                                        <label id="lblstoreLocatorDirectionsOmniEndInput" for="storeLocatorDirectionsOmniEndInput">To</label>
                                    </div>
                                    <div class="text">
                                        <input type="text" placeholder="street, city, state, zip" name="storeLocatorDirectionsOmniEndInput" id="storeLocatorDirectionsOmniEndInput" aria-required="lblstoreLocatorDirectionsOmniEndInput" aria-labelledby="lblstoreLocatorDirectionsOmniEndInput errorstoreLocatorDirectionsOmniEndInput" />
                                    </div>
                                </div>
                                
                                <div class="checkboxItem input clearfix">
                                    <input id="storeLocatorDirectionsAvoidSeasonalRoads" name="storeLocatorDirectionsAvoidSeasonalRoads" value="true" type="checkbox" > 
                                    <label for="storeLocatorDirectionsAvoidSeasonalRoads">Avoid Seasonal Roads </label>                                       	
                                </div>
                                <div class="checkboxItem input clearfix">
                                    <input id="storeLocatorDirectionsAvoidHighways" name="storeLocatorDirectionsAvoidHighways" value="true" type="checkbox" >
                                    <label for="storeLocatorDirectionsAvoidHighways">Avoid Highways </label>
                                </div>
                                <div class="checkboxItem input clearfix">
                                    <input id="storeLocatorDirectionsAvoidTolls" name="storeLocatorDirectionsAvoidTolls" value="true" type="checkbox" >
                                    <label for="storeLocatorDirectionsAvoidTolls">Avoid Toll Road </label>	
                                </div>


                                <div id="storeLocatorDirectionsDestination"></div>

                                <input type="submit" class="button" value="Get Directions" />
                                                           

                            
                        </form>
                    </div>
                    
                    <div id="storeLocatorTurnByTurnRouteInfo"></div>
                    <div id="storeLocatorTurnByTurnDirections"></div>
                    
                </div>
            </div>
            <dsp:getvalueof param="showpopup" var="showpopup"/>
            <c:if test="${showpopup eq 'true'}">
            	<a class="close-reveal-modal">&times;</a>
            </c:if>
		</div>

<%--			
	</jsp:body>	
</bbb:pageContainer>
--%>
</dsp:page>
