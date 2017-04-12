<dsp:page>
<dsp:getvalueof var="favStoreStockStatus" param="favStoreStockStatus" />
<dsp:getvalueof var="commerceItemId" param="commerceItemId" />
<dsp:getvalueof var="storeDetails" param="storeDetails" />

	<ul class="clearfix favStoreDetails">
		<dsp:getvalueof var="StoreDetailsVar" param="storeDetails" scope="page"/>
		<dsp:getvalueof var="storeId" param="StoreDetailsVar.storeId" />
		<label for="radioStoreAddress" class="storeAdd">
			<c:choose>
				<c:when test="${favStoreStockStatus[commerceItemId] == 0}">
				<%--PS-62372 | Changing label name as per business requirement --%>
					<span class="availability"><bbbl:label key="lbl_available_store" language="${pageContext.request.locale.language}" /></span> <bbbl:label key="lbl_at_your_store" language="${pageContext.request.locale.language}" />
				</c:when>
				<c:when test="${favStoreStockStatus[commerceItemId] == 2}">
					<span class="availability"> <bbbl:label key="lbl_find_in_store_lowstock" language="${pageContext.request.locale.language}" /></span> <bbbl:label key="lbl_at_your_store" language="${pageContext.request.locale.language}" />
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