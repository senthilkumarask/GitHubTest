<dsp:page>
<div class="grid_6 alpha clearfix">							
	<div class="grid_2 alpha">
		<h6><dsp:valueof param="storeDetails.storeName"/> <span><dsp:valueof param="storeDetails.distance"/></span></h6>
		<p><dsp:valueof param="storeDetails.address"/></p>
		<dsp:getvalueof id="linkViewMap" idtype="java.lang.String"	param="linkViewMap" />
		<dsp:getvalueof id="linkgetDirections" idtype="java.lang.String"	param="linkgetDirections" />
		<dsp:getvalueof id="linkFavorite" idtype="java.lang.String"	param="linkFavorite" />
		<c:if test="${linkViewMap == '1'}">
			<a href=""><bbbl:label key="lbl_route_info_view_map" language="${pageContext.request.locale.language}"/></a><br />
		</c:if>
		<c:if test="${linkgetDirections == '1'}">
			<a href=""><bbbl:label key="lbl_route_info_get_directions" language="${pageContext.request.locale.language}"/></a><br />
		</c:if>
		<c:if test="${linkFavorite == '1'}">
			<a href=""><bbbl:label key="lbl_route_info_make_favourite_store" language="${pageContext.request.locale.language}"/></a><br />
		</c:if>
		<br />
	</div>
	<div class="grid_3 push_1">
		<p>
			<bbbl:label key="lbl_route_info_weekdays" language="${pageContext.request.locale.language}"/>: <dsp:valueof param="StoreDetails.weekdaysStoreTimings"/><br />
			<bbbl:label key="lbl_route_info_saturday" language="${pageContext.request.locale.language}"/>: <dsp:valueof param="StoreDetails.satStoreTimings"/><br>
			<bbbl:label key="lbl_route_info_sunday" language="${pageContext.request.locale.language}"/>: <dsp:valueof param="StoreDetails.sunStoreTimings"/><br />
			<bbbl:label key="lbl_route_info_sunday" language="${pageContext.request.locale.language}"/>: <dsp:valueof param="StoreDetails.otherTimings1"/><br />
			<bbbl:label key="lbl_route_info_sunday" language="${pageContext.request.locale.language}"/>: <dsp:valueof param="StoreDetails.otherTimings2"/><br />
			<dsp:valueof param="StoreDetails.storePhone"/><br/>
			<strong><dsp:valueof param="StoreDetails.storePhone"/></strong>
		</p>
	</div>
</div>
</dsp:page>
