
<%-- ====================== Description===================
/**
* This page is used to do the mapping store search results so that it would be available in ajax call.
* @author Seema
**/
--%>
<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/P2PDirectionsFormHandler" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.postalCode"
		value="${param.storeMultiZip}" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.street"
		value="${param.storeMultiStreet}" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.city"
		value="${param.storeMultiCity}" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.state"
		value="${param.storeMultiState}" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.showDirectionsWithMaps"
		value="${param.dirShowMap}" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.routeEndPoint"
		value="${param.routeEndPoint}" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.destStreet"
		paramvalue="dialogDestStreet" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.destCity"
		paramvalue="dialogDestCity" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.destState"
		paramvalue="dialogDestState" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.destPostalCode"
		paramvalue="dialogDestZip" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.routeType"
		value="${param.selRouteOptionName}" />
	<c:choose>
		<c:when test="${not empty param.cbAvoidSeasonalRoadsName}">
			<dsp:setvalue bean="P2PDirectionsFormHandler.seasonalRoad"
				value="${param.cbAvoidSeasonalRoadsName}" />
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="P2PDirectionsFormHandler.seasonalRoad"
				value="false" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${not empty param.cbAvoidHighwaysName}">
			<dsp:setvalue bean="P2PDirectionsFormHandler.highways"
				value="${param.cbAvoidHighwaysName}" />
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="P2PDirectionsFormHandler.highways" value="false" />
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${not empty param.cbAvoidTollsName}">
			<dsp:setvalue bean="P2PDirectionsFormHandler.tollRoad"
				value="${param.cbAvoidTollsName}" />
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="P2PDirectionsFormHandler.tollRoad" value="false" />
		</c:otherwise>
	</c:choose>

	<dsp:setvalue bean="P2PDirectionsFormHandler.successURL"
		value="p2p_directions.jsp" />
	<dsp:setvalue bean="P2PDirectionsFormHandler.errorURL"
		value="p2p_directions.jsp" />

	<dsp:setvalue bean="P2PDirectionsFormHandler.p2PDirections" />
	<dsp:getvalueof bean="P2PDirectionsFormHandler.formError" var="flag" />

</dsp:page>
