<dsp:page>

	<dsp:getvalueof var="itemcount" param="itemcount" />
	<ul class="clearfix">
		<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>
			<dsp:getvalueof var="storeId" param="StoreDetails.storeId" />
			<dsp:getvalueof var="productChkFlg"
				param="productAvailable.${storeId}" vartype="java.lang.Integer"
				scope="page" />
			<dsp:getvalueof var="favStore" param="favstoreoutput" />
			<li class="column1">
				<div class="fl">
					<div class="clearfix">
							<label for="radioStoreAddress_${itemcount}" class="storeAdd">
							<span class="storeName upperCase"><strong>${StoreDetailsVar.storeName}</strong></span>
							<c:if test="${empty favStore}">
									<span class="miles">(<dsp:valueof
										value="${StoreDetailsVar.distance}" format="number" number="##.##"></dsp:valueof>&nbsp;<bbbl:label
										key="lbl_store_route_miles"
										language="${pageContext.request.locale.language}" />)
									</span>
								</c:if>
								<span
								class="street">${StoreDetailsVar.address}</span> <span
								class="city">${StoreDetailsVar.city}</span> <span class="state">${StoreDetailsVar.state}</span>
								<span class="zip">${StoreDetailsVar.postalCode}</span> <%--<span><dsp:valueof param="StoreDetails.storeName" /> (<dsp:valueof
																	param="StoreDetails.distance" converter="number" /> <bbbl:label
																	key="lbl_store_route_miles"
																	language="${pageContext.request.locale.language}" />)</span>
														<span class="street"><dsp:valueof param="StoreDetails.address"/> </span>
														<span class="city"><dsp:valueof param="StoreDetails.city" /></span>
														<span class="state"><dsp:valueof param="StoreDetails.state" /> </span>
														<span class="zip"><dsp:valueof param="StoreDetails.postalCode" /> </span>--%>
							</label>
							<p>
								${StoreDetailsVar.storePhone}
								<%-- <dsp:valueof param="StoreDetails.storePhone" /> --%>
							</p>

							<dsp:getvalueof var="address" param="StoreDetails.address" />
							<dsp:getvalueof var="babyCanada" param="StoreDetails.babyCanadaFlag"/>
							<dsp:getvalueof var="city" param="StoreDetails.city" />
							<dsp:getvalueof var="state" param="StoreDetails.state" />
							<dsp:getvalueof var="postalCode" param="StoreDetails.postalCode" />
							<div class="actionLinks">
							<c:set var="lbl_find_store_get_directions"><bbbl:label key="lbl_find_store_get_directions"
											language="${pageContext.request.locale.language}" /></c:set>
								<%--  New version of view map/get directions --%>	
								<p class="marTop_5">
									<a class="viewDirectionsNew" href="#"  data-storeid="${storeId}"  title="Get Map & Directions">Get Map & Directions</a>
								</p>
								<%--			
								<p class="marTop_5">
									<dsp:a iclass="viewDirections" href="#viewDirections" title="${lbl_find_store_get_directions}">
										<bbbl:label key="lbl_find_store_get_directions"
											language="${pageContext.request.locale.language}" />
										<dsp:param name="routeEndPoint"
											value="${address}, ${city}, ${state} ${postalCode}" />
										<dsp:param name="destStreet" value="${address}" />
										<dsp:param name="destCity" value="${city}" />
										<dsp:param name="destState" value="${state}" />
										<dsp:param name="destPostalCode" value="${postalCode}" />
									</dsp:a>
								</p>
								<p class="marTop_10">
								<c:set var="lbl_find_store_view_map"><bbbl:label
											key="lbl_find_store_view_map"
											language="${pageContext.request.locale.language}" /></c:set>
									<a class="viewOnMap" href="#" title="${lbl_find_store_view_map}"><bbbl:label
											key="lbl_find_store_view_map"
											language="${pageContext.request.locale.language}" /> </a>
								</p>
                                --%>
							</div>
						</div>
					</div>
				</li>
			     
				
				<li class="column2">
					<%-- <dsp:getvalueof id="storeTimings" idtype="java.lang.String" param="StoreDetails.weekdaysStoreTimings" /><dsp:valueof value="${StoreDetailsVar.weekdaysStoreTimings}"/> --%>

					<c:forTokens items="${StoreDetailsVar.weekdaysStoreTimings}" delims="," var="item">
					${item}
					</c:forTokens>
					<p class="marTop_5">
					<c:forTokens items="${StoreDetailsVar.satStoreTimings}" delims="," var="item">
						${item}
					</c:forTokens></p>
					<p class="marTop_5">
					<c:forTokens items="${StoreDetailsVar.sunStoreTimings}" delims="," var="item">
						${item}
					</c:forTokens></p>
					<p class="marTop_5">
					<c:forTokens items="${StoreDetailsVar.otherTimings1}" delims="," var="item">
						${item}
					</c:forTokens></p>
					<p class="specialMsg marTop_5">
					<c:forTokens items="${StoreDetailsVar.otherTimings2}" delims="," var="item">
						${item}
					</c:forTokens>
					</p>
				</li>	
				<c:if test="${siteId eq 'TBS_BedBathCanada'}">
				<li class="columnLogo">
				  <c:choose>
				   <c:when test="${babyCanada}">
				   <img src="${imagePath}/_assets/global/images/logo/logo_baby_ca_67x30.png" alt="Buy Buy Baby" />
				   </c:when>
				   <c:otherwise>
				   <img src="${imagePath}/_assets/global/images/logo/logo_bbby_ca_120x32.png" alt="Bed Bath and Beyond" />
				   </c:otherwise>
				 </c:choose>
				</li>
				</c:if>
				<c:choose>
				<c:when test="${productChkFlg==0 || productChkFlg==2}">
					<li class="column3 itemAvailability"><span class="inStock"><bbbl:label
								key="lbl_find_in_store_instock"
								language="${pageContext.request.locale.language}" /> </span></li>

				</c:when>
				<%-- Coded to remove this state at SearchInStoreDroplet:320
				<c:when test="${productChkFlg==2}">
					<li class="column3 itemAvailability"><span class="lowStock"><bbbl:label
								key="lbl_find_in_store_lowstock"
								language="${pageContext.request.locale.language}" /> </span></li>

				</c:when>
				 --%>
				<c:when test="${productChkFlg==1 || productChkFlg==101}">
					<li class="column3 itemAvailability"><span
						class="outOfStock"><bbbl:label
								key="lbl_find_in_store_unavailable"
								language="${pageContext.request.locale.language}" /> </span></li>

				</c:when>
				<c:when test="${productChkFlg==100 || productChkFlg==102}">
					<%-- Available but Store pickup not available in this state store --%>
					<li class="column3 itemAvailability"><span
						class="inStock"><bbbl:label
								key="lbl_find_in_store_instock"
								language="${pageContext.request.locale.language}" /> </span>
					<span class="reserveOnline"><bbbl:label key="lbl_findinstore_reserveonline"
								language="${pageContext.request.locale.language}" /> </span></li>
				</c:when>
				<c:otherwise>
					<li class="column3 itemAvailability"><span
						class="inStock"><bbbl:label
								key="lbl_find_in_store_instock"
								language="${pageContext.request.locale.language}" /> </span>
					<span class="reserveOnline"><bbbl:label key="lbl_findinstore_reserveonline"
								language="${pageContext.request.locale.language}" /> </span></li>
				</c:otherwise>
			</c:choose>

			 <li class="column4 noPadRight">
			 <c:choose>
				<c:when test="${productChkFlg==0}">
					<div class="button button_active button_active_orange button_enabled">
						<input type="hidden" name="storeId" value="${storeId}" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
						<input type="submit" value="Add to Cart" name="changeStoreBtn" id="changeStoreBtn" aria-pressed="false" role="button" aria-labelledby="changeStoreBtn"/>
					</div>
				</c:when>
				<c:when test="${productChkFlg==2}">
					<div class="button button_active button_active_orange button_enabled">
						<input type="hidden" name="storeId" value="${storeId}" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
						<input type="submit" value="Add to Cart" name="changeStoreBtn" id="changeStoreBtn" aria-pressed="false" role="button" aria-labelledby="changeStoreBtn"/>
					</div>
				</c:when>
				<c:otherwise>

				</c:otherwise>
			</c:choose>
           </li>
	</ul>

</dsp:page>