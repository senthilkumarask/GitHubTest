
<%-- ====================== Description===================
/**
* This page is used to display directions to reach out to store from inputed starting address on printer friendly page 
* @author Seema
**/
--%>
<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/P2PDirectionsFormHandler" />
	<c:set var="section" value="browse" scope="request" />
	<c:set var="pageWrapper" value="productDetails useMapQuest useBazaarVoice useStoreLocator useLiveClicker printerFriendly" scope="request" />

<bbb:pageContainer titleKey="lbl_find_store_print_title" index="false" follow="false">

		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

		<jsp:body>		
<%--Jsp body starts from here--%>

		<div id="content" class="container_12 clearfix" role="main">

			<div class="grid_12 marTop_20">
				<div class="clearfix">
			<div class="grid_3 alpha">
				
				<h3>
					<bbbl:label key="lbl_store_route_start"
						language="${pageContext.request.locale.language}" />
				</h3>
				<p>
					<dsp:valueof bean="P2PDirectionsFormHandler.routeStartPoint" />
				</p>
			</div>

			<div class="width_3 fl prefix_1 marLeft_10">
				<h3>
					<bbbl:label key="lbl_store_route_destination"
						language="${pageContext.request.locale.language}" />
				</h3>
				<div id="destinationLocation">
					<div class="address">
						<%--Displays Store's phone number and address which includes street, city, state and Zip code  --%>
						<div class="street">
							<dsp:valueof bean="P2PDirectionsFormHandler.destStreet" />
						</div>

						<div>
							<span class="city"> <dsp:valueof
									bean="P2PDirectionsFormHandler.destCity" />, </span> <span
								class="state"><dsp:valueof
									bean="P2PDirectionsFormHandler.destState" /> </span> <span
								class="zip"><dsp:valueof
									bean="P2PDirectionsFormHandler.destPostalCode" /> </span>
						</div>
						
					</div>
				</div>
			</div>
		</div>
		<%--This displays the MAP. --%>
		<div class="dialogDirectionOtions clearfix">
			<div class="grid_4 alpha">
				<div id="destinationMap">
					<dsp:getvalueof var="mapURL"
						bean='P2PDirectionsFormHandler.p2PRouteDetails.routeMap' />
					<img alt="Directions map" src="${mapURL}" />
				</div>
			</div>

			<div class="grid_3 omega">
				<div id="directionsRefineForm">
                    <div class="clearfix">
					<%-- This method will call the p2p_directions.jsp to get the directions --%>
					<dsp:form action="p2p_directions.jsp" method="post"
						name="refineDirectionsForm" id="refineDirectionsForm">
							<%--START ROUTE TYPE (QUICK,SHORT,PEDESTRIAN) SELECT INPUT FIELD --%>
							<div class="input width_2">
								<div class="label">
									<label id="lblselRouteOption" for="selRouteOption"><bbbl:label
											key="lbl_store_route_option"
											language="${pageContext.request.locale.language}" /> </label>
								</div>

								<div class="select">
									<dsp:select bean="P2PDirectionsFormHandler.routeType"
										id="selRouteOption" name="selRouteOptionName" iclass="uniform">
										<dsp:option value="quick">
											<bbbl:label key="lbl_store_route_type_option_quick"
												language="${pageContext.request.locale.language}" />
										</dsp:option>
										<dsp:option value="shortest">
											<bbbl:label key="lbl_store_route_type_option_short"
												language="${pageContext.request.locale.language}" />
										</dsp:option>
										<dsp:option value="pedestrian">
											<bbbl:label key="lbl_store_route_type_option_pedestrian"
												language="${pageContext.request.locale.language}" />
										</dsp:option>
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblselRouteOption errorselRouteOption"/>
									</dsp:select>
								</div>
							</div>
							<%--END ROUTE TYPE (QUICK, SHORT,PEDESTRIAN) SELECT INPUT FIELD --%>
							<%--START AVOID SEASONAL ROADS CHECHBOX --%>
							<div class="checkboxItem input clearfix">
								<div class="checkbox">
									<dsp:input bean="P2PDirectionsFormHandler.seasonalRoad"
										type="checkbox" value="true" name="cbAvoidSeasonalRoadsName"
										id="cbAvoidSeasonalRoads" >
                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcbAvoidSeasonalRoads"/>
                                    </dsp:input>
								</div>

								<div class="label">
									<label id="lblcbAvoidSeasonalRoads" for="cbAvoidSeasonalRoads"><bbbl:label
											key="lbl_store_route_avoid_seasonal_roads"
											language="${pageContext.request.locale.language}" /> </label>
								</div>
							</div>
							<%--END AVOID SEASONAL ROADS CHECHBOX --%>
							<%--START AVOID HIGHWAYS CHECHBOX --%>
							<div class="checkboxItem input clearfix">
								<div class="checkbox">
									<dsp:input bean="P2PDirectionsFormHandler.highways"
										type="checkbox" value="true" name="cbAvoidHighwaysName"
										id="cbAvoidHighways" >
                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcbAvoidHighways"/>
                                    </dsp:input>
								</div>

								<div class="label">
									<label id="lblcbAvoidHighways" for="cbAvoidHighways"><bbbl:label
											key="lbl_store_route_avoid_highways"
											language="${pageContext.request.locale.language}" /> </label>
								</div>
							</div>
							<%--END AVOID HIGHWAYS CHECHBOX --%>
							<%--START AVOID TOLL CHECHBOX --%>
							<div class="checkboxItem input clearfix">
								<div class="checkbox">
									<dsp:input bean="P2PDirectionsFormHandler.tollRoad"
										type="checkbox" value="true" name="cbAvoidTollsName"
										id="cbAvoidTolls" >
                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcbAvoidTolls"/>
                                    </dsp:input>
								</div>

								<div class="label">
									<label id="lblcbAvoidTolls" for="cbAvoidTolls"><bbbl:label
											key="lbl_store_route_avoid_toll_road"
											language="${pageContext.request.locale.language}" /> </label>
								</div>
							</div>
						
							<%--END AVOID TOLL CHECHBOX --%>
							<%--GO Button --%>
							<div class="button button_active">
								<input	type="button" id="refineDirections" value="go" role="button" aria-pressed="false" aria-labelledby="refineDirections" />
							</div>


						<dsp:input bean="P2PDirectionsFormHandler.successURL"
							type="hidden" value="p2p_directions.jsp" />
						<dsp:input bean="P2PDirectionsFormHandler.errorURL" type="hidden"
							value="p2p_directions.jsp" />
						<dsp:input type="hidden"
							bean="P2PDirectionsFormHandler.routeEndPoint" />
						<dsp:input type="hidden"
							bean="P2PDirectionsFormHandler.routeStartPoint" />
						<dsp:input type="hidden"
							bean="P2PDirectionsFormHandler.showDirectionsWithMaps" />
					</dsp:form>
					<%-- Start Driving time and distance estimation --%>
					</div>
					<div class="noMar width_3  marTop_10">
						<p class="padBottom_5"><span class="bold"><bbbl:label key="lbl_store_route_driving_time"
					language="${pageContext.request.locale.language}" /> </span><span class="block cb"><dsp:valueof
								bean='P2PDirectionsFormHandler.p2PRouteDetails.formattedTime' />
						</span></p>

						<p class="padBottom_5"><span class="bold"><bbbl:label key="lbl_store_route_driving_distance"
							language="${pageContext.request.locale.language}" /> </span><span class="block cb"><dsp:valueof
								bean='P2PDirectionsFormHandler.p2PRouteDetails.totalDistance' />
								<bbbl:label key="lbl_store_route_miles"
										language="${pageContext.request.locale.language}" />
						</span></p>
					</div>
					<%-- Start Driving time and distance estimation --%>
						
				</div>
			</div>
		</div>

	<div class="clear"></div>
	<%-- directionsTable will display the Driving directions from Start location to Destination with and without Map option.  --%>
	<div id="directionsTable" class="directionsTableWrapper print">
		<table>
			<caption>
				<bbbl:label key="lbl_store_route_print_driving _directions"
					language="${pageContext.request.locale.language}" />
			</caption>
			<dsp:getvalueof var="mapOption"
				bean="P2PDirectionsFormHandler.showDirectionsWithMaps" />
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array"
					bean="P2PDirectionsFormHandler.p2PRouteDetails.routeLegs" />
				<dsp:param name="elementName" value="routeLegs" />
				<dsp:oparam name="output">
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param="routeLegs.maneuvers" />
						<dsp:param name="elementName" value="maneuvers" />
						<dsp:oparam name="outputStart">
							<thead>
								<tr>
									<th>#</th>
									<th><bbbl:label key="lbl_store_route_maneuvers"
											language="${pageContext.request.locale.language}" />
									</th>
									<th><bbbl:label key="lbl_store_route_distance_traveled"
											language="${pageContext.request.locale.language}" />
									</th>
									<c:if test="${mapOption=='true'}">
										<th><bbbl:label key="lbl_store_route_map"
												language="${pageContext.request.locale.language}" /></th>
									</c:if>
								</tr>
							</thead>

						</dsp:oparam>
						<dsp:oparam name="output">
							<dsp:getvalueof id="distance" param="maneuvers.distance"/>
							<dsp:getvalueof id="mapUrl" param="maneuvers.mapUrl" />
							<tr>
								<td><dsp:valueof param="count" />
								</td>
								<td><dsp:valueof param="maneuvers.narrative" /></td>
								<td><dsp:valueof param="maneuvers.distance"
										converter="number" /> <bbbl:label key="lbl_store_route_miles"
										language="${pageContext.request.locale.language}" />
								</td>
								<c:choose>
									<c:when
										test="${mapOption=='true' and distance==0 and  empty mapUrl}">
										<td><dsp:img
												page="<bbbl:label key='lbl_store_route_BBB_logo'
											language='${pageContext.request.locale.language}'/>" />
										</td>
									</c:when>
									<c:when test="${mapOption=='true' and distance!=0}">
										<td><dsp:img page="${mapUrl}" /></td>
									</c:when>
								</c:choose>
							</tr>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</table>
	</div>
	</div>
    </div>
	<%--Jsp body Ends here--%>	
	</jsp:body>
	</bbb:pageContainer>
</dsp:page>