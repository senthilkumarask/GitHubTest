<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />		
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	
	<c:set var="pageWrapper" value="useStoreLocator estorePage" scope="request" />

	<bbb:pageContainer headerRenderer=" "  titleKey="lbl_find_store_print_title" index="false" follow="false">

	
	<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

	<jsp:body>
<%--Jsp body starts from here--%>
 <style>               
            /* search results styles - layout */
            body {min-width: 700px;}
            #content, #storeLocatorResults {width: 748px;}
            #storeLocatorOriginFieldset {width: auto; padding:0 10px;}
            #storeLocatorMapResults {width: 515px;}
            #storeLocatorResults, 
            #storeLocatorMapResults, 
            #storeLocatorAddressResults, 
            #storeLocatorDirections {  height:600px; }
            #storeLocatorDirectionsFormWrapper {height: 390px; /* 240 */}
            #storeLocatorTurnByTurnRouteInfo {height: 20px;}
            #storeLocatorTurnByTurnDirections {height: 190px; /* 340 */ overflow-y: auto;}
            
            /* custom estore hiding of elements */
            #storeLocatorDirectionsRouteTypeRadioGroup,
            #storeLocatorDirectionsForm div.checkboxItem {/* display: none; */}              
            #footer, #footerPs { display: none; }
    </style>
    

												   
												   
    <div id="content" class="container_12 clearfix">        
                <form id="storeLocatorSearchForm" class="clearfix ">

                    <fieldset class="width_9" id="storeLocatorOriginFieldset">
                        <legend><h5><bbbl:label key="lbl_findstore_store_locator" language="${pageContext.request.locale.language}" /></h5></legend>
                        
                        <div class="input" id="storeLocatorOmnibarContainer">
                            <div class="label">
                                <label>
                                    <bbbl:label key="lbl_findstore_location" language="${pageContext.request.locale.language}" />
                                    <span id="storeLocatorOmnibarInputWarning" class="warning" style="display:none"><bbbl:label key="lbl_findstore_location_required" language="${pageContext.request.locale.language}" /></span>
                                </label>
                            </div>
                            <div class="text">
                                <input type="text" placeholder="City, State or Zip Code" id="storeLocatorOmnibarInput" />
                                
                            </div>
                        </div>
                        
                        <div class="input" id="storeLocatorRadiusContainer">                                
                            <div class="label">
                                <label><bbbl:label key="lbl_findstore_search_within" language="${pageContext.request.locale.language}" /></label>
                            </div>
                            <div class="text">
                                <select id="storeLocatorRadiusInput" >
                                    <option>5</option>
                                    <option>10</option>
                                    <option>15</option>
                                    <option>20</option>
                                    <option>25</option>
                                    <option>50</option>
                                    <option>100</option>
                                    <option>200</option>
                                </select>
                            </div>
                        </div>
                        <div class="input" id="storeLocatorSubmitContainer">                             
                            <div class="button button_active"> 
                                <input type="submit" value="Find a Store" />
                            </div> 
                        </div>
                        
                        
                        <div class="input" id="storeLocatorUseMyLocationContainer"> 
                            <div class="button button_secondary "> 
                                <input type="button" id="storeLocatorUseMyLocationButton" value="Use My Location" />
                            </div> 
                        </div>
                        
                        
                        <div class="input" id="storeLocatorStoreConceptsContainer">
                            <div class="label">
                                <label><bbbl:label key="lbl_findstore_store" language="${pageContext.request.locale.language}" /></label>
                            </div>
				
                            <input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptBBBY" class="storeLocatorStoreConcept" value="10" /><label for="storeLocatorStoreConceptBBBY"><bbbl:label key="lbl_findstore_bed_beyond" language="${pageContext.request.locale.language}" /></label>
                            <input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptBaby" class="storeLocatorStoreConcept" value="40" /><label for="storeLocatorStoreConceptBaby"><bbbl:label key="lbl_findstore_buybuy_Baby" language="${pageContext.request.locale.language}" /></label>
                            <input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptHarmon" class="storeLocatorStoreConcept" value="30" /><label for="storeLocatorStoreConceptHarmon"><bbbl:label key="lbl_findstore_harmon" language="${pageContext.request.locale.language}" /></label>
                           	
						</div>
                        
                    </fieldset>	
	
                    
                    <fieldset class="grid_9"  id="storeLocatorFiltersFieldset">
						<legend><bbbl:label key="lbl_findstore_filters" language="${pageContext.request.locale.language}" /></legend>
			
						<div class="input" id="storeLocatorSpecialtyContainer">
                            <div class="label">
                                <label><bbbl:label key="lbl_findstore_specialty_dprmt" language="${pageContext.request.locale.language}" /></label>
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorFTG" id="storeLocatorFTG" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorFTG">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_ftg26.png" />
                                    <bbbl:label key="lbl_findstore_tabletop" language="${pageContext.request.locale.language}" />
                                </label>
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorWB" id="storeLocatorWB" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorWB">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_wb26.png" />                                    
                                   <bbbl:label key="lbl_findstore_winebeer" language="${pageContext.request.locale.language}" />
                                </label>                                
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorHBC" id="storeLocatorHBC" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorHBC">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_hbc26.png" />
                                    <bbbl:label key="lbl_findstore_health_beauty" language="${pageContext.request.locale.language}" />
                                </label>                                
                            </div>      
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorPhoto" id="storeLocatorPhoto" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorPhoto">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_photo26.png" />
                                   <bbbl:label key="lbl_findstore_photo_studio" language="${pageContext.request.locale.language}" />
                                </label>                                
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorWM" id="storeLocatorWM" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorWM">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_wm26.png" />
                                    <bbbl:label key="lbl_findstore_specialty_foods" language="${pageContext.request.locale.language}" />
                                </label>                                
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorHD" id="storeLocatorHD" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorHD">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_hd26.png" />
                                    <bbbl:label key="lbl_findstore_home_delivery" language="${pageContext.request.locale.language}" />
                                </label>
                                
                            </div>
						</div>
                    </fieldset>                    
            </form>
	
            <h5 id="storeLocatorResultsMsg" style="display: none;"></h5>
            
            <p id="storeLocatorInfo" style="display: none;"><bbbl:label key="lbl_findstore_store_hours" language="${pageContext.request.locale.language}" /></p>
                
            <div id="storeLocatorResults" class="" style="display: none;">
                <div id="storeLocatorAddressResults" class="grid_3 alpha omega "></div>
                    
                <div id="storeLocatorMapResults" class="grid_6 alpha omega "></div>
                
                <div id="storeLocatorDirections" class="grid_3 alpha omega ">
                    
                    <div id="storeLocatorDirectionsFormWrapper">
                        <a id="storeLocatorDirectionsBackToReultsLink" href=""><bbbl:label key="lbl_findstore_back_list" language="${pageContext.request.locale.language}" /></a>                  

                        <h2><bbbl:label key="lbl_findstore_directions" language="${pageContext.request.locale.language}" /></h2>

                        <form id="storeLocatorDirectionsForm">
                            <input type="hidden" name="storeLocatorDirectionsSearchingOnPoiKey" id="storeLocatorDirectionsSearchingOnPoiKey" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLat" id="storeLocatorDirectionsStartingLat" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLng" id="storeLocatorDirectionsStartingLng" value="" />
                            <fieldset>     
								<legend class="offScreen"><bbbl:label key="lbl_storeLocator" language="${pageContext.request.locale.language}" /></legend>                      
                                                
                                <div class="input" id="storeLocatorDirectionsRouteTypeWrapper">
                                    <div class="label">
                                        <label ><bbbl:label key="lbl_findstore_select_route" language="${pageContext.request.locale.language}" /></label>
                                    </div>
                                    <div id="storeLocatorDirectionsRouteTypeRadioGroup">
                                        <input type="radio" name="routeType" id="storeLocatorDirectionsFastest" checked="checked" value="fastest" />
                                        <label for="storeLocatorDirectionsFastest"><bbbl:label key="lbl_findstore_car" language="${pageContext.request.locale.language}" /></label>

                                        <input type="radio" name="routeType" id="storeLocatorDirectionsTransit" value="multimodal" />
                                        <label for="storeLocatorDirectionsTransit"><bbbl:label key="lbl_findstore_pub" language="${pageContext.request.locale.language}" /></label>

                                        <input type="radio" name="routeType" id="storeLocatorDirectionsWalking" value="pedestrian" />
                                        <label for="storeLocatorDirectionsWalking"><bbbl:label key="lbl_findstore_walk" language="${pageContext.request.locale.language}" /></label>
                                    </div>
                                </div>                                
                                <div class="input" id="storeLocatorDirectionsOmniStartWrapper" >
                                    <div class="label">
                                        <label><bbbl:label key="lbl_findstore_from" language="${pageContext.request.locale.language}" /></label>
                                    </div>
                                    <div class="text">
                                        <input type="text" placeholder="street, city, state, zip" name="storeLocatorDirectionsOmniStartInput" id="storeLocatorDirectionsOmniStartInput" />
                                    </div>
                                </div>
                                <div class="input" id="storeLocatorDirectionsOmniEndWrapper" >
                                    <div class="label">
                                        <label><bbbl:label key="lbl_findstore_to" language="${pageContext.request.locale.language}" /></label>
                                    </div>
                                    <div class="text">
                                        <input type="text" placeholder="" name="storeLocatorDirectionsOmniEndInput" id="storeLocatorDirectionsOmniEndInput" />
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
	<%--Jsp body Ends here--%>	
	</jsp:body>
	</bbb:pageContainer>
</dsp:page>