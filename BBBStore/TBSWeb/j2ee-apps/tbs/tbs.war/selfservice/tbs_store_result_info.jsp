<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>

	<dsp:getvalueof var="counter" param="counter" />
	<dsp:getvalueof var="pageFrom" param="pageFrom" />
	<dsp:getvalueof var="Zipcode" param="Zipcode" />
	<dsp:getvalueof var="storeCount" param="NumberOfStores" idtype="java.lang.Integer"/>
	<dsp:getvalueof var="storeId" param="StoreDetail.storeId"/>
	<dsp:getvalueof var="inStock" param="productAvailStatus.${storeId }"/>
	<dsp:getvalueof var="inStockValue" param="nearbyStoresInventory.${storeId }"/>
	<dsp:getvalueof var="itemQuantity" param="itemQuantity"/>
	<dsp:getvalueof var="commerceId" param="commerceId"/>
	<dsp:getvalueof var="shipid" param="shipid"/>
	<dsp:getvalueof var="siteId" bean="Site.id" />
	
	<c:choose>
		<c:when test="${siteId eq 'TBS_BedBathUS'}">
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_us"/></c:set>
		</c:when>
		<c:when test="${siteId eq 'TBS_BuyBuyBaby'}">
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_baby"/></c:set>
		</c:when>
		<c:when test="${siteId eq 'TBS_BedBathCanada'}">
			<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_ca"/></c:set>
		</c:when>
	</c:choose>
	
	<c:if test="${storeId eq favouriteStoreId}">
		<strong>Favorite Store</strong>
	</c:if>
	<c:choose>
		<c:when test="${not empty commerceId}">
			<c:set var="btnClass" value="btnPickInStore"></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="btnClass" value="btnAddToCart"></c:set>
		</c:otherwise>
	</c:choose>
	<c:if test="${not empty inStock or pageFrom eq 'storeResults'}">
		<li>
			<dsp:getvalueof var="StoreDetailsVar" param="StoreDetail" />
			<c:if test="${counter eq 1}">
				<p>We found this product in <dsp:valueof value="${storeCount}"/>  
				<c:choose>
				    <c:when test="${not empty Zipcode}">
				      stores within <dsp:valueof param="miles"></dsp:valueof> ${StoreDetailsVar.distanceUnit} of <dsp:valueof value="${Zipcode}"/>
				    </c:when>
				    <c:otherwise>
				        store for store <dsp:valueof value="${sessionScope.storenumber}"/> at ${StoreDetailsVar.postalCode}.
				    </c:otherwise>
				</c:choose>
			</c:if>
			<div class="small-12 medium-4 columns no-padding-left">
				<span class="storeName upperCase"><strong>${StoreDetailsVar.storeName}</strong></span>
				<span class=" strong miles">(<dsp:valueof value="${StoreDetailsVar.distance}" format="number" number="##.##"></dsp:valueof>&nbsp;
					<dsp:valueof value="${StoreDetailsVar.distanceUnit}"/>)
				</span>
				<br/>
				<span class="street">${StoreDetailsVar.address}</span>
				<br/>
				<span class="city">${StoreDetailsVar.city}</span> 
				<span class="state">${StoreDetailsVar.state}</span>
				<span class="zip">${StoreDetailsVar.postalCode}</span>
				<br/>
				<span class="phone">${StoreDetailsVar.postalCode}</span>
				<p>${StoreDetailsVar.storePhone}</p>
				<div class="actionLinks">
					<c:set var="lbl_find_store_get_directions"><bbbl:label key="lbl_find_store_get_directions" language="${pageContext.request.locale.language}" /></c:set>
					<%--  New version of view map/get directions --%>	
					<p class="marTop_5">
						<a class="viewDirectionsNew" href="#"  data-storeid="${storeId}" onclick="javascript:externalLinks('mapquest: view maps')"; title="Get Map & Directions">Get Map & Directions</a>
					</p>
                </div>
			</div>
			
			<div class="small-12 medium-4 columns no-padding-left">
				<c:forTokens items="${StoreDetailsVar.weekdaysStoreTimings}" delims="," var="item">
					${item}<br/>
				</c:forTokens>
				
				<c:forTokens items="${StoreDetailsVar.satStoreTimings}" delims="," var="item">
					${item}<br/>
				</c:forTokens>
				<c:forTokens items="${StoreDetailsVar.sunStoreTimings}" delims="," var="item">
					${item}<br/>
				</c:forTokens>
				<c:forTokens items="${StoreDetailsVar.otherTimings1}" delims="," var="item">
					${item}<br/>
				</c:forTokens>
				<c:forTokens items="${StoreDetailsVar.otherTimings2}" delims="," var="item">
					${item}<br/>
				</c:forTokens>
			</div>
			<c:if test="${empty pageFrom}">
			<div class="small-12 medium-4 columns no-padding">
				
				<div class="small-12 medium-4 columns">
					<c:choose>
					<c:when test="${inStock==0 || inStock==2}">
						<span class="inStock"><c:out value="${inStockValue}"/>
							<bbbl:label key="lbl_find_in_store_instock" language="${pageContext.request.locale.language}" />
						</span>
					</c:when>
					<c:when test="${inStock==1 || inStock==101}">
						<span class="outOfStock dd">
							<bbbl:label key="lbl_find_in_store_unavailable" language="${pageContext.request.locale.language}" />
						</span>
					</c:when>
					<c:when test="${inStock==100 || inStock==102}">
						<%-- Available but Store pickup not available in this state store --%>
						<span class="inStock">
							<c:out value="${inStockValue}"/> <bbbl:label key="lbl_find_in_store_instock" language="${pageContext.request.locale.language}" />
						</span>
						<span class="reserveOnline"><bbbl:label key="lbl_findinstore_reserveonline" language="${pageContext.request.locale.language}" /> </span>
					</c:when>
					<c:otherwise>
						<span class="inStock"><c:out value="${inStockValue}"/>
							<bbbl:label key="lbl_find_in_store_instock" language="${pageContext.request.locale.language}" />
						</span>
						<span class="reserveOnline"><bbbl:label key="lbl_findinstore_reserveonline" language="${pageContext.request.locale.language}" /></span>
					</c:otherwise>
					</c:choose>
				</div>
				<div class="small-12 medium-8 columns">
			 		<c:choose>
			 		<c:when test="${!MapQuestOn}">
			 			<div class="button_active_orange button_enabled addToCart button_disabled">
							<input type="hidden" name="storeId" value="${storeId}" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
							<input disabled="disabled" class="tiny button expand transactional revealModal" type="submit" value="Add to Cart" name="${btnClass}" aria-pressed="false" role="button" aria-labelledby="btnAddToCart"
							data-commerceid="${commerceId}" data-qty="${itemQuantity}" data-storeid="${storeId}" data-shipid="${shipid}"/>
						</div>
			 		</c:when>
					<c:when test="${inStock==0 && MapQuestOn}">
						<dsp:droplet name="TBSItemExclusionDroplet">
							<dsp:param name="siteId" param="siteId"/>
							<dsp:param name="skuId" param="sku"/>
							<dsp:oparam name="output">
							<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
							<dsp:getvalueof var="caDisabled" param="caDisabled"/>
							<dsp:getvalueof var="reasonCode" param="reasonCode"/>
							<c:choose>
								<c:when test="${isItemExcluded || caDisabled}">
									<div class="small-12 columns product-other-links availability" style="color:red;font-weight:bold;">
								<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
									<dsp:param name="reasonCode" value="${reasonCode}"/>
								</dsp:include>
							    </div>    
									<div class="button_active_orange button_enabled addToCart button_disabled">
										<input type="hidden" name="storeId" value="${storeId}" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
										<input disabled="disabled" class="tiny button expand transactional revealModal" type="submit" value="Add to Cart" name="${btnClass}" aria-pressed="false" role="button" aria-labelledby="btnAddToCart"
										data-commerceid="${commerceId}" data-qty="${itemQuantity}" data-storeid="${storeId}" data-shipid="${shipid}"/>
									</div>
								</c:when>
								<c:otherwise>
									<div class="button_active button_active_orange button_enabled addToCart">
										<input type="hidden" name="storeId" value="${storeId}" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
										<input class="tiny button expand transactional revealModal" type="submit" value="Add to Cart" name="${btnClass}" aria-pressed="false" role="button" aria-labelledby="btnAddToCart"
										data-commerceid="${commerceId}" data-qty="${itemQuantity}" data-storeid="${storeId}" data-shipid="${shipid}"/>
									</div>
								</c:otherwise>
							</c:choose>
							</dsp:oparam>
						</dsp:droplet>
					</c:when>
					<c:when test="${inStock==2 && MapQuestOn}">
						<dsp:droplet name="TBSItemExclusionDroplet">
							<dsp:param name="siteId" param="siteId"/>
							<dsp:param name="skuId" param="sku"/>
							<dsp:oparam name="output">
							<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
							<dsp:getvalueof var="caDisabled" param="caDisabled"/>
							<dsp:getvalueof var="reasonCode" param="reasonCode"/>
							<c:choose>
								<c:when test="${isItemExcluded || caDisabled}">
									<div class="small-12 columns product-other-links availability" style="color:red;font-weight:bold;">
							 		<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
										<dsp:param name="reasonCode" value="${reasonCode}"/>
									</dsp:include>
							    </div>    
									<div class=" button_active_orange button_enabled addToCart button_disabled">
										<input type="hidden" name="storeId" value="${storeId}" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
										<input disabled="disabled" class="tiny button expand transactional" type="submit" value="Add to Cart" name="${btnClass}" aria-pressed="false" role="button" aria-labelledby="btnAddToCart"
										data-commerceid="${commerceId}" data-qty="${itemQuantity}" data-storeid="${storeId}" data-shipid="${shipid}"/>
									</div>
								</c:when>
								<c:otherwise>
									<div class="button_active button_active_orange button_enabled addToCart">
										<input type="hidden" name="storeId" value="${storeId}" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
										<input class="tiny button expand transactional" type="submit" value="Add to Cart" name="${btnClass}" aria-pressed="false" role="button" aria-labelledby="btnAddToCart"
										data-commerceid="${commerceId}" data-qty="${itemQuantity}" data-storeid="${storeId}" data-shipid="${shipid}"/>
									</div>
								</c:otherwise>
							</c:choose>
							</dsp:oparam>
						</dsp:droplet>
					</c:when>
					<c:otherwise>
						<c:if test="${MapQuestOn}">
							<dsp:droplet name="TBSItemExclusionDroplet">
								<dsp:param name="siteId" param="siteId"/>
								<dsp:param name="skuId" param="sku"/>
								<dsp:oparam name="output">
								<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
								<dsp:getvalueof var="caDisabled" param="caDisabled"/>
								<dsp:getvalueof var="reasonCode" param="reasonCode"/>
								<c:choose>
									<c:when test="${isItemExcluded || caDisabled}">
									<div class="small-12 columns product-other-links availability" style="color:red;font-weight:bold;">
							 		<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
										<dsp:param name="reasonCode" value="${reasonCode}"/>
									</dsp:include>
							    </div>  
										<div class="button_active_orange button_enabled addToCart button_disabled">
											<input type="hidden" name="storeId" value="${storeId}" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
											<input disabled="disabled" class="tiny button expand transactional" type="submit" value="Add to Cart" name="${btnClass}" aria-pressed="false" role="button" aria-labelledby="btnAddToCart"
											data-commerceid="${commerceId}" data-qty="${itemQuantity}" data-storeid="${storeId}" data-shipid="${shipid}"/>
										</div>
									</c:when>
									<c:otherwise>
										<div class="button_active button_active_orange button_disabled addToCart">
											<input type="hidden" name="storeId" value="${storeId}" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
											<input disabled="disabled" class="tiny button expand transactional" type="submit" value="Add to Cart" name="${btnClass}" aria-pressed="false" role="button" aria-labelledby="btnAddToCart"
											data-commerceid="${commerceId}" data-qty="${itemQuantity}" data-storeid="${storeId}" data-shipid="${shipid}"/>
										</div>
									</c:otherwise>
								</c:choose>
								</dsp:oparam>
							</dsp:droplet>
						</c:if>
					</c:otherwise>
					</c:choose>
				</div>
			</div>
			</c:if>
		</li>
	</c:if>
</dsp:page>