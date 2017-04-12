<dsp:page>
	<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
       	<dsp:param name="storeId" param="shippingGroup.storeId" />
       	<dsp:oparam name="output">
			<ul class="marTop_10">
			
			<c:if test="${not empty showLinks}">
				<h1 class="SPCSectionHeading marTop_20"><bbbl:label key="lbl_spc_shipping_store_pickup" language="<c:out param='${language}'/>"/></h1>
			</c:if>	
								
				<li class="clearfix shippingItemAddDetails">
					<div class="grid_4 alpha">
						<p class="padTop_5"><strong><bbbl:label key="lbl_spc_preview_storelocation" language="<c:out param='${language}'/>"/></strong></p>
						<p class="marTop_5"><strong><dsp:valueof param="StoreDetails.storeName"/></strong></p>
						<div class="storeAdd">
							<p><span class="street"><dsp:valueof param="StoreDetails.address"/></span></p>
							<p><span class="city"><dsp:valueof param="StoreDetails.city"/></span></p>
							<p><span class="state"><dsp:valueof param="StoreDetails.state"/></span>&nbsp;<span class="zip"><dsp:valueof param="StoreDetails.postalCode"/></span></p>
						</div>
						<p><span class="phone"><dsp:valueof param="StoreDetails.storePhone"/></span></p>
<%-- 						<c:if test="${not empty showLinks}">
							<p class="marTop_10"><a class="upperCase" href="${pageContext.request.contextPath}/checkout/shipping/shipping.jsp" title="<bbbl:label key="lbl_spc_preview_changestore" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_spc_preview_changestore" language="<c:out param='${language}'/>"/></strong></a></p>
						</c:if>
--%>
					</div>
					<div class="grid_4 omega">
					<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>
						<p class="padTop_5"><strong><bbbl:label key="lbl_spc_preview_storehours" language="<c:out param='${language}'/>"/></strong></p>
						<dl class="pickupTimimngs noMar marTop_5">
							
							<c:forTokens items="${StoreDetailsVar.weekdaysStoreTimings}" delims="," var="item" varStatus="status">
								<c:choose>
									<c:when test="${status.count == 1}">
										<dt>${item}</dt>	
									</c:when>
									<c:otherwise>
										<dd class="upperCase">${item}</dd>			
									</c:otherwise>				
								</c:choose>
							</c:forTokens>
							
							<c:forTokens items="${StoreDetailsVar.satStoreTimings}" delims="," var="item" varStatus="status">
								<c:choose>
									<c:when test="${status.count == 1}">
										<dt>${item}</dt>	
									</c:when>
									<c:otherwise>
										<dd class="upperCase">${item}</dd>			
									</c:otherwise>				
								</c:choose>
							</c:forTokens>
							
							<c:forTokens items="${StoreDetailsVar.sunStoreTimings}" delims="," var="item" varStatus="status">
								<c:choose>
									<c:when test="${status.count == 1}">
										<dt>${item}</dt>	
									</c:when>
									<c:otherwise>
										<dd class="upperCase">${item}</dd>			
									</c:otherwise>				
								</c:choose>
							</c:forTokens>
							<c:forTokens items="${StoreDetailsVar.otherTimings1}" delims="," var="item" varStatus="status">
								<c:choose>
									<c:when test="${status.count == 1}">
										<dt>${item}</dt>	
									</c:when>
									<c:otherwise>
										<dd class="upperCase">${item}</dd>			
									</c:otherwise>				
								</c:choose>
							</c:forTokens>
							<c:forTokens items="${StoreDetailsVar.otherTimings2}" delims="," var="item" varStatus="status">
								<c:choose>
									<c:when test="${status.count == 1}">
										<dt>${item}</dt>	
									</c:when>
									<c:otherwise>
										<dd class="upperCase">${item}</dd>			
									</c:otherwise>				
								</c:choose>
							</c:forTokens>
							<dsp:valueof param="StoreDetails.storeDescription" />
						</dl>
<%--						<c:if test="${not empty showLinks}">
							<p class="marTop_10">
								<a class="viewDirectionsNew upperCase" data-storeid="<dsp:valueof param="StoreDetails.storeId"/>" href="#" id="getDirections" title="<bbbl:label key="lbl_spc_find_store_get_directions" language="<c:out param='${language}'/>"/>">
									<strong><bbbl:label key="lbl_spc_find_store_get_directions" language="<c:out param='${language}'/>"/></strong>
								</a>
							</p>
						</c:if>	
--%>
 					</div>
				</li>
			</ul>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
