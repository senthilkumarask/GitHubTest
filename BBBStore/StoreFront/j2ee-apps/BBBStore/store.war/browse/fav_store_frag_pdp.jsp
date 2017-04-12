<dsp:page>
<dsp:getvalueof var="isMultiSku" param="isMultiSku" />
<dsp:getvalueof var="favStoreStockStatus" param="favStoreStockStatus" />
<dsp:getvalueof var="skuId" param="skuId" />
<dsp:getvalueof var="skuIdfromURL" param="skuIdfromURL" />
<dsp:getvalueof var="storeDetails" param="storeDetails" />
	<ul class="clearfix favStoreDetails <c:if test="${(isMultiSku==true && empty skuIdfromURL) or (empty favStoreStockStatus or favStoreStockStatus=='') or 
	(not empty skuIdfromURL && favStoreStockStatus[skuId] == 1)}">hidden</c:if>">
		<dsp:getvalueof var="StoreDetailsVar" param="storeDetails" scope="page"/>
		<dsp:getvalueof var="storeId" param="StoreDetailsVar.storeId" />
		<label for="radioStoreAddress" class="storeAdd">
			<c:choose>
				<c:when test="${favStoreStockStatus[skuId] == 0 || (isMultiSku && empty skuIdfromURL)}">
						<span class="availability"><bbbl:label key="lbl_available1" language="${pageContext.request.locale.language}" /></span> <bbbl:label key="lbl_at_your_store" language="${pageContext.request.locale.language}" />
				</c:when>
				<c:when test="${favStoreStockStatus[skuId] == 2 && empty skuIdfromURL}">
					<span class="availability"> Limited Availability</span> at your favorite store
				</c:when>
				<c:when test="${not empty skuIdfromURL}">
						<c:choose>
							<c:when test="${favStoreStockStatus[skuId] == 0 || favStoreStockStatus[skuId] == 1}">
									<span class="availability"><bbbl:label key="lbl_available1" language="${pageContext.request.locale.language}" /></span> <bbbl:label key="lbl_at_your_store" language="${pageContext.request.locale.language}" />
							</c:when>
							<c:otherwise>
									 <span class="availability"><bbbl:label key="lbl_find_in_store_lowstock" language="${pageContext.request.locale.language}" /></span> <bbbl:label key="lbl_at_your_store" language="${pageContext.request.locale.language}" />
							</c:otherwise>
						</c:choose>
				</c:when>
			</c:choose>
				<span class="storeName upperCase"><strong>${StoreDetailsVar.storeName}</strong></span>
				<span class="street">${StoreDetailsVar.address}</span> 
				<span class="city">${StoreDetailsVar.city}</span> <span class="state">${StoreDetailsVar.state}</span>
				<span class="zip">${StoreDetailsVar.postalCode}</span> 
		</label>
		<p>
			Contact no: ${StoreDetailsVar.storePhone}
		</p>
	</ul>
</dsp:page>