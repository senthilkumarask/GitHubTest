<dsp:page>
<dsp:importbean bean="com/bbb/selfservice/ScheduleAppointmentManager" />
<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
<dsp:getvalueof var="registryId" bean="ScheduleAppointmentManager.registryId"/>
<dsp:getvalueof var="eventDate" bean="ScheduleAppointmentManager.eventDate"/>
<dsp:getvalueof var="coregFN" bean="ScheduleAppointmentManager.coregFN"/>
<dsp:getvalueof var="coregLN" bean="ScheduleAppointmentManager.coregLN"/>
<dsp:getvalueof var="preselectedServiceRef" bean="SearchStoreFormHandler.preSelectedServiceRef"/>
	<dsp:getvalueof var="itemcount" param="itemcount" />
	<ul class="clearfix">
		<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>
			<dsp:getvalueof var="storeId" param="StoreDetails.storeId" />
			<dsp:getvalueof var="appointmentType" param="appointmentType" />
			<dsp:getvalueof var="productChkFlg"
				param="appointmentAvailableMap.${storeId}" vartype="java.lang.Integer"
				scope="page" />
			<dsp:getvalueof var="favStore" param="favstoreoutput" />
			
			<li class="col1">
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
			     
				
				<li class="col2">
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
					<p class="marTop_5">
					<c:forTokens items="${StoreDetailsVar.otherTimings2}" delims="," var="item">
						${item}
					</c:forTokens>
					</p>
				</li>	
				
				<c:if test="${siteId eq 'BedBathCanada'}">
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
				<li class="col4"><p>
				<c:choose>
				<c:when test="${productChkFlg==true}">			
					<dsp:include page="/scheduleappointment/scheduleAppointment.jsp">					
					<dsp:param name="appointmentType" value="${appointmentType}"/>
					<dsp:param name="storeId" value="${storeId}"/>
					<dsp:param name="registryId" value="${registryId}"/>
					<dsp:param name="eventDate" value="${eventDate}"/>
					<dsp:param name="coregFN" value="${coregFN}"/>
					<dsp:param name="coregLN" value="${coregLN}"/>
					</dsp:include>	
				</c:when>				
				<c:otherwise>
								<bbbl:label
								key="lbl_appointment_not_available"
								language="${pageContext.request.locale.language}" />
				</c:otherwise>
			</c:choose>
			</p>
			</li>
           </li>
	</ul>

</dsp:page>