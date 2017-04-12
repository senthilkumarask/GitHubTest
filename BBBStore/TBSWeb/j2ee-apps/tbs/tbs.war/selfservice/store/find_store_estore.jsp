<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />		
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	
	<c:set var="pageWrapper" value="useStoreLocator" scope="request" />

	<bbb:pageContainer headerRenderer=" "  titleKey="lbl_find_store_print_title" index="false" follow="false">

	
	<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

	<jsp:body>
<%--Jsp body starts from here--%>


    <script type="text/javascript">   

    $(document).ready(function() {
        var options = {
                radius:25,
                //favoriteStoreID : '1102',
                conceptID: 'BedBathUS',
                enableFavoriteStores: false,
                debug:false,
                //origin: '90210',
                //runSearchOnLoad: true
                //imgBasePath: '/images/icons/',
                usePrintPage:true
                //printPage: 'map_print.html' 
            };
            
        BBB.BBBStoreLocator.init(options);
        BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden    
    
    });
    
    </script>                                      
	                                               
	                                               
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
                        <legend><h5>Store Locator</h5></legend>
                        
                        <div class="input" id="storeLocatorOmnibarContainer">
                            <div class="label">
                                <label>
                                    Location
                                    <span id="storeLocatorOmnibarInputWarning" class="warning" style="display:none">Location is required</span>
                                </label>
                            </div>
                            <div class="text">
                                <input type="text" placeholder='<bbbl:label key="lbl_storeLocatorInput" language="${pageContext.request.locale.language}" />' id="storeLocatorOmnibarInput" />
                               
                            </div>
                        </div>
                        
                        <div class="input" id="storeLocatorRadiusContainer">                                
                            <div class="label">
                                <label>Search Within</label>
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
                                <label>Store</label>
                            </div>
				
                            <input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptBBBY" class="storeLocatorStoreConcept" value="10" /><label for="storeLocatorStoreConceptBBBY">Bed Bath &amp; Beyond</label>
                            <input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptBaby" class="storeLocatorStoreConcept" value="40" /><label for="storeLocatorStoreConceptBaby">buybuy Baby </label>
                            <input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptHarmon" class="storeLocatorStoreConcept" value="30" /><label for="storeLocatorStoreConceptHarmon">Harmon </label>
                           	
						</div>
                        
                    </fieldset>	
	
                    
                    <fieldset class="grid_9"  id="storeLocatorFiltersFieldset">
						<legend>Filters</legend>
			
						<div class="input" id="storeLocatorSpecialtyContainer">
                            <div class="label">
                                <label>Specialty Departments and Services</label>
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorFTG" id="storeLocatorFTG" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorFTG">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_ftg26.png" />
                                    Tabletop & Fine Dining
                                </label>
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorWB" id="storeLocatorWB" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorWB">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_wb26.png" />                                    
                                    Wine &amp Beer
                                </label>                                
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorHBC" id="storeLocatorHBC" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorHBC">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_hbc26.png" />
                                    Health & Beauty 
                                </label>                                
                            </div>      
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorPhoto" id="storeLocatorPhoto" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorPhoto">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_photo26.png" />
                                    Photo Studio
                                </label>                                
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorWM" id="storeLocatorWM" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorWM">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_wm26.png" />
                                    Specialty Foods
                                </label>                                
                            </div>
                            <div class="storeLocatorSpecialtyItemContainer">
                                <input type="checkbox" name="storeLocatorHD" id="storeLocatorHD" class="storeLocatorSpecialtyDept" value="1" />
                                <label for="storeLocatorHD">
                                    <img src="/_assets/global/images/storeLocator/storeLocator_hd26.png" />
                                    Home Delivery 
                                </label>
                                
                            </div>
						</div>
                    </fieldset>                    
            </form>
	
            <h5 id="storeLocatorResultsMsg" style="display: none;"></h5>
            
            <p id="storeLocatorInfo" style="display: none;">Store hours may vary during Holiday Season and Back to College. Please contact the store location for more detailed information.</p>
                
            <div id="storeLocatorResults" class="" style="display: none;">
                <div id="storeLocatorAddressResults" class="grid_3 alpha omega "></div>
                    
                <div id="storeLocatorMapResults" class="grid_6 alpha omega "></div>
                
                <div id="storeLocatorDirections" class="grid_3 alpha omega ">
                    
                    <div id="storeLocatorDirectionsFormWrapper">
                        <a id="storeLocatorDirectionsBackToReultsLink" href="">&lt; Back to List</a>                  

                        <h2>Map &amp; Directions</h2>

                        <form id="storeLocatorDirectionsForm">
                            <input type="hidden" name="storeLocatorDirectionsSearchingOnPoiKey" id="storeLocatorDirectionsSearchingOnPoiKey" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLat" id="storeLocatorDirectionsStartingLat" value="" />
                            <input type="hidden" name="storeLocatorDirectionsStartingLng" id="storeLocatorDirectionsStartingLng" value="" />
                            <fieldset>     
								<legend class="offScreen"><bbbl:label key="lbl_storeLocator" language="${pageContext.request.locale.language}" /></legend>                      
                                                
                                <div class="input" id="storeLocatorDirectionsRouteTypeWrapper">
                                    <div class="label">
                                        <label >Select A Route Option </label>
                                    </div>
                                    <div id="storeLocatorDirectionsRouteTypeRadioGroup">
                                        <input type="radio" name="routeType" id="storeLocatorDirectionsFastest" checked="checked" value="fastest" />
                                        <label for="storeLocatorDirectionsFastest">Car</label>

                                        <input type="radio" name="routeType" id="storeLocatorDirectionsTransit" value="multimodal" />
                                        <label for="storeLocatorDirectionsTransit">Pub</label>

                                        <input type="radio" name="routeType" id="storeLocatorDirectionsWalking" value="pedestrian" />
                                        <label for="storeLocatorDirectionsWalking">Walk</label>
                                    </div>
                                </div>                                
                                <div class="input" id="storeLocatorDirectionsOmniStartWrapper" >
                                    <div class="label">
                                        <label>From</label>
                                    </div>
                                    <div class="text">
                                        <input type="text" placeholder="street, city, state, zip" name="storeLocatorDirectionsOmniStartInput" id="storeLocatorDirectionsOmniStartInput" />
                                    </div>
                                </div>
                                <div class="input" id="storeLocatorDirectionsOmniEndWrapper" >
                                    <div class="label">
                                        <label>To</label>
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
                                        <label for="storeLocatorDirectionsAvoidSeasonalRoads">Avoid Seasonal Roads </label>	
                                    </div>	
                                </div>
                                <div class="checkboxItem input clearfix">
                                    <div class="checkbox">
                                        <input id="storeLocatorDirectionsAvoidHighways" name="storeLocatorDirectionsAvoidHighways" value="true" type="checkbox" >
                                    </div>
                                    <div class="label">
                                        <label for="storeLocatorDirectionsAvoidHighways">Avoid Highways </label>	
                                    </div>	
                                </div>
                                <div class="checkboxItem input clearfix">
                                    <div class="checkbox">
                                        <input id="storeLocatorDirectionsAvoidTolls" name="storeLocatorDirectionsAvoidTolls" value="true" type="checkbox" >
                                    </div>
                                    <div class="label">
                                        <label for="storeLocatorDirectionsAvoidTolls">Avoid Toll Road </label>	
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