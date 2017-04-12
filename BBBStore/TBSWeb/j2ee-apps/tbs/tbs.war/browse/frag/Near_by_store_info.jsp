<dsp:page>
	<c:set var="HolidayMessagingOn" scope="request"><bbbc:config key="isHolidayMessaging" configName="FlagDrivenFunctions" /></c:set>
	<dsp:getvalueof var="skuId" param="skuId" />
	<dsp:getvalueof var="inventoryErrorMessage" param="inventoryErrorMessage" />
	<dsp:getvalueof var="emptyStoreInventory" param="emptyStoreInventory" />
	<c:choose>
		<c:when test="${null ne emptyStoreInventory && emptyStoreInventory}">
			<bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}" />
		</c:when>
		<c:when test="${null ne inventoryErrorMessage}">
		</c:when>
		<c:otherwise>
			<dsp:getvalueof param="timeframe" var="timeframe"></dsp:getvalueof>
			<dsp:getvalueof param="nearbyStore" var="nearbyStore"></dsp:getvalueof>
			<dsp:getvalueof param="nearbyStoreLink" var="nearbyStoreLink"></dsp:getvalueof>
			<dsp:getvalueof param="currentStoreQty" var="currentStoreQty"></dsp:getvalueof>
				<a style="text-decoration: none !important;" class="pdp-sprite in-store <c:if test='${currentStoreQty eq null || empty currentStoreQty || currentStoreQty < 1}'> no-stock</c:if>" <c:if test="${currentStoreQty eq null || empty currentStoreQty || currentStoreQty < 1}">disabled='disabled'</c:if>>${currentStoreQty} In Store</a>
			
			<a href="/tbs/browse/shipping_policies.jsp" class="pdp-sprite online popupShipping <c:if test="${timeframe eq '0004'}"> no-stock</c:if>" <c:if test="${timeframe eq 0004}">disabled='disabled'</c:if>>
				<c:if test="${timeframe eq '0001'}">
					<bbbl:label key="lbl_tbs_ship_time_0001" language="${pageContext.request.locale.language}" />
				</c:if>
				<c:if test="${timeframe eq '0002'}">
					<bbbl:label key="lbl_tbs_ship_time_0002" language="${pageContext.request.locale.language}" />
				</c:if>
				<c:if test="${timeframe eq '0003'}">
					<bbbl:label key="lbl_tbs_ship_time_0003" language="${pageContext.request.locale.language}" />
				</c:if>
				<c:if test="${timeframe eq '0004'}">
					<bbbl:label key="lbl_tbs_ship_time_0004" language="${pageContext.request.locale.language}" />
				</c:if>
				<c:if test="${timeframe eq '0005'}">
					<bbbl:label key="lbl_tbs_ship_time_0005" language="${pageContext.request.locale.language}" />
				</c:if>
			</a>
			
			<c:if test="${HolidayMessagingOn}">
				 	<dsp:include src="/tbs/common/holidayMessaging.jsp">
					<dsp:param name="nearbyStoreLink" value="${nearbyStoreLink}"/>
			 		<dsp:param name="showNearbyStorelink" value="true"/>								 		
			 		<dsp:param name="timeframe" value="${timeframe}"/>
			 	</dsp:include>
			 </c:if>
			<c:choose>
				<c:when test='${nearbyStoreLink eq true}'>
				    <dsp:a iclass="pdp-sprite nearby-stores 
				         in-store" href="/tbs/selfservice/find_tbs_store.jsp">
				         <dsp:param name="skuid" value="${skuId}" />
				         <dsp:param name="itemQuantity" value="1" />
				         <dsp:param name="id" param="productId" />
				         <dsp:param name="siteId" value="${appid}" />
				         <dsp:param name="skuId" value="${skuId}" />
				         <dsp:param name="registryId" param="registryId" />
				         Nearby Stores
				     </dsp:a>
				     <div id="nearbyStore" class="reveal-modal" data-reveal>
				     </div>
				</c:when>
				<c:otherwise>
					<span class="pdp-sprite nearby-stores no-stock" disabled="disabled" >
						Nearby Stores
					</span>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	
	<script type="text/javascript">
		$(document).ready(function(){
			$(".nearby-stores").attr("data-reveal-id","nearbyStore");
			$(".nearby-stores").attr("data-reveal-ajax","true");
			$(".notify-me").attr("data-reveal-id","notifyMeRequest");
			$(".notify-me").attr("data-reveal-ajax","true");
			$( ".bbbS7ContainerTipWrapper" ).wrap( "<a href='#' data-reveal-id='bbbS7ZoomImageDIV'></a>" );
			$(document).foundation('reflow');
			$("body").on("click",'#nearbyStore .close-reveal-modal', function(){
            	$('#nearbyStore').foundation('reveal', 'close');
            });
		});
	</script>
</dsp:page>
