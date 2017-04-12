<%-- ====================== Description===================
/**
* This page is used to display store locator functionality in which customer inputs his address, city and state or zip code
* and details of his nearby location are displayed with the option of viewing map and directions.
* @author Seema
**/
--%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<%-- Variables --%>
	<c:set var="section" value="selfService" scope="request" />
	<c:set var="pageWrapper" value="useStoreLocator" scope="request" />
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:if test="${currentSiteId eq TBS_BedBathCanadaSite}">
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

			<%-- default radius depends on store concept--%>
			<c:set var="radius_default_us"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set>
			<c:set var="radius_default_baby"><bbbc:config key="radius_default_baby" configName="MapQuestStoreType" /></c:set>
			<c:set var="radius_range_us"><bbbc:config key="radius_range_us" configName="MapQuestStoreType" /></c:set>
			<c:set var="radius_range_baby"><bbbc:config key="radius_range_baby" configName="MapQuestStoreType" /></c:set>
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
					<c:set var="radius_default_selected">${radius_default_us}</c:set>
					<c:set var="radius_range">${radius_range_us}</c:set>
				</c:otherwise>
			</c:choose>

			<%-- only logged in users should see make favorite store buttons --%>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="false">
					<c:set var="enableFavoriteStores" value="true" />
				</dsp:oparam>
				<dsp:oparam name="true">
					<c:set var="enableFavoriteStores" value="false" />
				</dsp:oparam>
			</dsp:droplet>

			<%--
			<link rel="stylesheet" type="text/css" href="/store/selfservice/store/map.css" />
			<script src="http://www.mapquestapi.com/sdk/js/v7.0.s/mqa.toolkit.js?key=Gmjtd%7Cluubnuu8nq%2Can%3Do5-lz15h"></script>
			<script src="/store/selfservice/store/map.js"></script>
			--%>
			<script type="text/javascript">
				$(document).ready(function() {
					var options = {
							radius:'${radius_default_selected}'
							,favoriteStoreID : '${favouriteStoreId}'
							,conceptID: '${currentSiteId}'
							,enableFavoriteStores : ${enableFavoriteStores}
							,debug: false
							,useAlerts: true
							, usePrintPage:true
							, useScrollShadows: true
						};

					BBB.BBBStoreLocator.init(options);
					BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden
				});
			</script>

			<div id="content" class="row">

				<%-- title --%>
				<div class="small-12 columns">
					<h1><bbbl:label key="lbl_find_store_heading" language="${pageContext.request.locale.language}" /></h1>
					<dsp:a iclass="makeFavorite" href="/tbs/selfservice/store/find_store.jsp?favouriteStoreId=dummyId"  id="storeLocatorHiddenLink" style="display:none;">
						<bbbl:label key="lbl_find_store_make_fav_store"	language="${pageContext.request.locale.language}" />
						<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
					</dsp:a>
				</div>

				<%-- store locator search form / filters --%>
				<div class="small-12 columns store-locator-form">
					<div class="row">
						<form id="storeLocatorSearchForm">

							<%-- search form --%>
							<div class="small-12 large-7 columns">
								<h2>Store Locator</h2>
								<div class="row">
									<div class="small-6 columns">
										<label>
											<h3>Location</h3>
												<input type="text" placeholder='<bbbl:label key="lbl_storeLocatorInput" language="${pageContext.request.locale.language}" />' id="storeLocatorOmnibarInput" aria-required="true" />										
										</label>
									</div>
									<div class="small-6 columns" id="storeLocatorRadiusContainer">
										<label>
											<h3>Search Within</h3>
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
										</label>
									</div>
								</div>
								<div class="row">
									<div class="small-6 columns" id="storeLocatorUseMyLocationContainer">
										<input type="button" class="tiny button expand secondary" id="storeLocatorUseMyLocationButton" value="Use My Location" title="This allows us to find the stores closest to you, by automatically detecting your location." />
									</div>
									<div class="small-6 columns" id="storeLocatorSubmitContainer">
										<input type="submit" value="Find a Store" class="tiny button expand transactional"/>
									</div>
								</div>
								<div class="row">
									<div class="small-12 columns" id="storeLocatorStoreConceptsContainer">
										<h3>Store</h3>
										<label class="inline-rc checkbox" for="storeLocatorStoreConceptBBBY">										 
										  <c:choose>
										    <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
				  								<input type="checkbox" name="storeLocatorStoreConcept" checked="checked" id="storeLocatorStoreConceptBBBY" class="storeLocatorStoreConcept" value="10" />
										    </c:when>
										    <c:otherwise>
												<input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptBBBY" class="storeLocatorStoreConcept" value="10" />											  										  	
										    </c:otherwise>
										  </c:choose>
											<span></span>
											Bed Bath &amp; Beyond
										</label>
										<label class="inline-rc checkbox" for="storeLocatorStoreConceptBaby">
										  <c:choose>
											<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
												<input type="checkbox" name="storeLocatorStoreConcept" checked="checked" id="storeLocatorStoreConceptBaby" class="storeLocatorStoreConcept" value="40" />
											</c:when>
											<c:otherwise>
												<input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptBaby" class="storeLocatorStoreConcept" value="40" />
											</c:otherwise>
										  </c:choose>
											<span></span>
											buybuy Baby
										</label>
										<label class="inline-rc checkbox" for="storeLocatorStoreConceptHarmon">
											<input type="checkbox" name="storeLocatorStoreConcept" id="storeLocatorStoreConceptHarmon" class="storeLocatorStoreConcept" value="30" />
											<span></span>
											Harmon
										</label>
									</div>
								</div>
							</div>

							<%-- filters --%>
							<div class="small-12 large-5 columns">
								<h2>Filters</h2>
								<div id="storeLocatorSpecialtyContainer">
									<h3>Specialty Departments and Services</h3>
									<div class="storeLocatorSpecialtyItemContainer">
										<label class="inline-rc checkbox" for="storeLocatorFTG">
											<input type="checkbox" name="storeLocatorFTG" id="storeLocatorFTG" class="storeLocatorSpecialtyDept" value="1" />
											<span></span>
											<img src="/_assets/global/images/storeLocator/storeLocator_ftg26.png" />
											Fine Tabletop &amp; Giftware
										</label>
									</div>
									<div class="storeLocatorSpecialtyItemContainer">
										<label class="inline-rc checkbox" for="storeLocatorWB">
											<input type="checkbox" name="storeLocatorWB" id="storeLocatorWB" class="storeLocatorSpecialtyDept" value="1" />
											<span></span>
											<img src="/_assets/global/images/storeLocator/storeLocator_wb26.png" />
											Wine &amp; Beer
										</label>
									</div>
									<div class="storeLocatorSpecialtyItemContainer">
										<label class="inline-rc checkbox" for="storeLocatorHBC">
											<input type="checkbox" name="storeLocatorHBC" id="storeLocatorHBC" class="storeLocatorSpecialtyDept" value="1" />
											<span></span>
											<img src="/_assets/global/images/storeLocator/storeLocator_hbc26.png" />
											Health &amp; Beauty
										</label>
									</div>
									<div class="storeLocatorSpecialtyItemContainer">
										<label class="inline-rc checkbox" for="storeLocatorPhoto">
											<input type="checkbox" name="storeLocatorPhoto" id="storeLocatorPhoto" class="storeLocatorSpecialtyDept" value="1" />
											<span></span>
											<img src="/_assets/global/images/storeLocator/storeLocator_photo26.png" />
											Photo Studio
										</label>
									</div>
									<div class="storeLocatorSpecialtyItemContainer">
										<label class="inline-rc checkbox" for="storeLocatorWM">
											<input type="checkbox" name="storeLocatorWM" id="storeLocatorWM" class="storeLocatorSpecialtyDept" value="1" />
											<span></span>
											<img src="/_assets/global/images/storeLocator/storeLocator_wm26.png" />
											Specialty Foods
										</label>
									</div>
									<div class="storeLocatorSpecialtyItemContainer">
										<label class="inline-rc checkbox" for="storeLocatorHD">
											<input type="checkbox" name="storeLocatorHD" id="storeLocatorHD" class="storeLocatorSpecialtyDept" value="1" />
											<span></span>
											<img src="/_assets/global/images/storeLocator/storeLocator_hd26.png" />
											Home Delivery
										</label>
									</div>
									<div class="storeLocatorSpecialtyItemContainer">
										<label class="inline-rc checkbox" for="storeLocatorBaby">
											<input type="checkbox" name="storeLocatorBaby" id="storeLocatorBaby" class="storeLocatorSpecialtyDept" value="1" />
											<span></span>
											<img src="/_assets/global/images/storeLocator/storeLocator_babystroller26.png" />
											Baby Department
										</label>
									</div>
								</div>
							</div>

						</form>
					</div>
				</div>

				<%-- print only addresses --%>
				<div class="small-12 columns printOnly">
					<div id="storeLocatorPrintAddresses" class="row">
						<div id="storeLocatorPrintAddressesOrigin" class="small-12 large-6 columns"></div>
						<div id="storeLocatorPrintAddressesDestination" class="small-12 large-6 columns"></div>
					</div>
				</div>

				<div class="small-12 columns">
					<h3 id="storeLocatorResultsMsg"></h3>
					<p class="p-secondary" id="storeLocatorInfo" >
						Store hours may vary during Holiday Season and Back to College. Please
						contact the store location for more detailed information.
					</p>
				</div>
				<div id="storeLocatorResults" class="small-12 columns">
					<div class="row">
						<div id="storeLocatorAddressResults" class="small-12 large-4 columns"></div>
						<div id="storeLocatorMapResults" class="small-12 large-8 columns"></div>
						<div class="hide-for-medium-down">
							<div id="storeLocatorDirections" class="small-12 large-4 columns">
								<div id="storeLocatorDirectionsFormWrapper">
									<a id="storeLocatorDirectionsBackToReultsLink" href="">&lt; Back to List</a>
									<h2>Map &amp; Directions</h2>
									<form id="storeLocatorDirectionsForm">
										<input type="hidden" name="storeLocatorDirectionsSearchingOnPoiKey" id="storeLocatorDirectionsSearchingOnPoiKey" value="" />
										<input type="hidden" name="storeLocatorDirectionsStartingLat" id="storeLocatorDirectionsStartingLat" value="" />
										<input type="hidden" name="storeLocatorDirectionsStartingLng" id="storeLocatorDirectionsStartingLng" value="" />
<%-- 										<bbbl:label key="lbl_storeLocator" language="${pageContext.request.locale.language}" /> --%>
										<div id="storeLocatorDirectionsRouteTypeWrapper">
											<h3>Select A Route Option</h3>
											<div id="storeLocatorDirectionsRouteTypeRadioGroup">
												<label class="inline-rc radio" for="storeLocatorDirectionsFastest">
													<input type="radio" name="routeType" id="storeLocatorDirectionsFastest" checked="checked" value="fastest" />
													<span></span>
													Driving
												</label>
												<label class="inline-rc radio" for="storeLocatorDirectionsTransit">
													<input type="radio" name="routeType" id="storeLocatorDirectionsTransit" value="multimodal" />
													<span></span>
													Public Transit
												</label>
												<label class="inline-rc radio" for="storeLocatorDirectionsWalking">
													<input type="radio" name="routeType" id="storeLocatorDirectionsWalking" value="pedestrian" />
													<span></span>
													Walking
												</label>
											</div>
										</div>
										<div id="storeLocatorDirectionsOmniStartWrapper">
											<label id="lblstoreLocatorDirectionsOmniStartInput" for="storeLocatorDirectionsOmniStartInput">From</label>
											<input type="text" placeholder="street, city, state, zip" name="storeLocatorDirectionsOmniStartInput" id="storeLocatorDirectionsOmniStartInput" aria-required="true" aria-labelledby="lblstoreLocatorDirectionsOmniStartInput errorstoreLocatorDirectionsOmniStartInput" />
										</div>
										<div id="storeLocatorDirectionsOmniEndWrapper" >
											<label id="lblstoreLocatorDirectionsOmniEndInput" for="storeLocatorDirectionsOmniEndInput">To</label>
											<input type="text" placeholder="" name="storeLocatorDirectionsOmniEndInput" id="storeLocatorDirectionsOmniEndInput" aria-required="true" aria-labelledby="lblstoreLocatorDirectionsOmniEndInput errorstoreLocatorDirectionsOmniEndInput" />
										</div>
										<label class="inline-rc checkbox" for="storeLocatorDirectionsAvoidSeasonalRoads">
											<input id="storeLocatorDirectionsAvoidSeasonalRoads" name="storeLocatorDirectionsAvoidSeasonalRoads" value="true" type="checkbox">
											<span></span>
											Avoid Seasonal Roads
										</label>
										<label class="inline-rc checkbox" for="storeLocatorDirectionsAvoidHighways">
											<input id="storeLocatorDirectionsAvoidHighways" name="storeLocatorDirectionsAvoidHighways" value="true" type="checkbox">
											<span></span>
											Avoid Highways
										</label>
										<label class="inline-rc checkbox" for="storeLocatorDirectionsAvoidTolls">
											<input id="storeLocatorDirectionsAvoidTolls" name="storeLocatorDirectionsAvoidTolls" value="true" type="checkbox">
											<span></span>
											Avoid Toll Road
										</label>
										<div id="storeLocatorDirectionsDestination"></div>
										<input type="submit" value="Get Directions"  class="tiny button expand transactional"/>
									</form>
								</div>
								<div id="storeLocatorTurnByTurnRouteInfo"></div>
								<div id="storeLocatorTurnByTurnDirections"></div>
							</div>
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
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
