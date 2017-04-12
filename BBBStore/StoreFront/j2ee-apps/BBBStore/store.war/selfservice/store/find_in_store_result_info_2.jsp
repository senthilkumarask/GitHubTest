<dsp:page>		
	<dsp:getvalueof var="itemcount" param="itemcount" />
	<ul class="clearfix">
		<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>		
			<dsp:getvalueof var="storeId" param="StoreDetails.storeId" />
			<li class="width_7 fl clearfix">
				<div class="width_4 fl">
					<div class="radioItem clearfix">
						<div class="label">
							<label for="radioStoreAddress_${itemcount}" class="storeAdd">
								<span>${StoreDetailsVar.storeName} (<dsp:valueof value="${StoreDetailsVar.distance}" format="number"></dsp:valueof>&nbsp;<bbbl:label key="lbl_store_route_miles" language="${pageContext.request.locale.language}" />) </span> 
								<span class="street">${StoreDetailsVar.address}</span> 
								<span class="city">${StoreDetailsVar.city}</span> 
								<span class="state">${StoreDetailsVar.state}</span>
								<span class="zip">${StoreDetailsVar.postalCode}</span> 
							</label>
						</div>
						<dsp:getvalueof var="address" param="StoreDetails.address" />
						<dsp:getvalueof var="city" param="StoreDetails.city" />
						<dsp:getvalueof var="state" param="StoreDetails.state" />
						<dsp:getvalueof var="postalCode" param="StoreDetails.postalCode" />
						<div class="actionLinks">
						<c:set var="lbl_find_store_get_directions"><bbbl:label key="lbl_find_store_get_directions" language="${pageContext.request.locale.language}" /></c:set>
							<p>
                                <dsp:getvalueof var="storeId" param="StoreDetails.storeId" />
                                <a class="viewDirectionsNew" href="#" data-storeid="${storeId}" title="Get Map & Directions">Get Map & Directions</a>
                                <%--
								<dsp:a iclass="viewDirections" href="#viewDirections" title="${lbl_find_store_get_directions}">
									<bbbl:label key="lbl_find_store_get_directions" language="${pageContext.request.locale.language}" />
									<dsp:param name="routeEndPoint" value="${address}, ${city}, ${state} ${postalCode}" />
									<dsp:param name="destStreet" value="${address}" />
									<dsp:param name="destCity" value="${city}" />
									<dsp:param name="destState" value="${state}" />
									<dsp:param name="destPostalCode" value="${postalCode}"/>
								</dsp:a>
                                --%>
							</p>
							<%--<p><c:set var="lbl_find_store_view_map"><bbbl:label	key="lbl_find_store_view_map" language="${pageContext.request.locale.language}" /> </c:set>
								<a class="viewOnMap" href="#" title="${lbl_find_store_view_map}">
									<bbbl:label	key="lbl_find_store_view_map" language="${pageContext.request.locale.language}" /> 
								</a>
							</p>--%>
						</div>
					</div>
					</div>
			
				<div class="width_3 fl">	
				<%-- <dsp:getvalueof id="storeTimings" idtype="java.lang.String" param="StoreDetails.weekdaysStoreTimings" /><dsp:valueof value="${StoreDetailsVar.weekdaysStoreTimings}"/> --%>
				<p class="marTop_5">
				${StoreDetailsVar.weekdaysStoreTimings}
				</p>
				<p class="marTop_5">
				${StoreDetailsVar.satStoreTimings}
				</p>
				<p class="marTop_5">
				${StoreDetailsVar.sunStoreTimings}
				</p>
				<p class="marTop_5">
				${StoreDetailsVar.otherTimings1}
				</p>
				<p class="marTop_5">
				${StoreDetailsVar.otherTimings2}
				</p>
					<p class="marTop_5">
						${StoreDetailsVar.storePhone}
						<%-- <dsp:valueof param="StoreDetails.storePhone" /> --%>
					</p>
					<p class="marTop_5">
						${StoreDetailsVar.storeDescription}
						<%-- <dsp:valueof param="StoreDetails.storeDescription" /> --%>
					</p>
					<div class="benefitItem marTop_10">
						<img src="/_assets/global/images/icons/health_beauty.png" />
						<p>Health & Beauty Department.</p>
					</div>
				</div></li>
			
				
	</ul>
</dsp:page>