<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/MxRegistryItemsDisplayDroplet" />
	<dsp:importbean
		bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
				
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="sortSeq" param="sortSeq"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="pwsurl" param="pwsurl" />
	<dsp:getvalueof var="guestUrl" value="${contextPath}/mx/view_registry_guest.jsp?registryId=${registryId}&eventType=${eventType}&pwsurl=${pwsurl}" />

	<c:set var="mxConversionValue" scope="request"><bbbc:config key="MexicoConversionValue" configName="ContentCatalogKeys" /></c:set>

	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:if test="${not empty registryId}">
		<dsp:droplet name="MxRegistryItemsDisplayDroplet">
			<dsp:param name="registryId" value="${registryId}" />
			<dsp:param name="startIdx" param="startIdx" />
			<dsp:param name="isGiftGiver" param="isGiftGiver" />
			<dsp:param name="blkSize" param="blkSize" />
			<dsp:param name="isAvailForWebPurchaseFlag" param="isAvailForWebPurchaseFlag" />
			<dsp:param name="userToken" param="userToken" />
			<dsp:param name="sortSeq" param="sortSeq" />
			<dsp:param name="view" param="view" />
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:param name="profile" bean="Profile"/>
			<dsp:param name="isMxGiftGiver" value="true"/>
			<dsp:param name="mxConversionValue" value="${mxConversionValue}"/>

			<%--RegistryItemsDisplayDroplet output parameter starts --%>
			<dsp:oparam name="output">
			<dsp:getvalueof var="emptyList" param="emptyList" />
			<div class="grid_12 clearfix">
						<dsp:include page="registry_sort_controls.jsp">
							<dsp:param name="sortSeq" param="sorting"/>
							<dsp:param name="view" param="view"/>
						</dsp:include>
						</div>
						<div class="grid_12 clearfix">
						
						<c:choose>
						<c:when test="${emptyList eq 'true'}">
							<c:choose>
							<c:when test="${view eq '3'}">
								<bbbe:error key="err_no_pur_reg_item_details" language="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${view eq '2'}">
								<bbbe:error key="err_no_rem_reg_item_details" language="${pageContext.request.locale.language}"/>
							</c:when>
							<c:otherwise>
								<bbbe:error key="err_no_mxreg_item_detail" language="${pageContext.request.locale.language}"/>
							</c:otherwise>							
							</c:choose>
						</c:when>
						<c:otherwise>
						<c:set var="maxBulkSize" scope="request">
			  				<bbbc:config key="MaxSizeRegistryItems" configName="ContentCatalogKeys" />
		    			</c:set>
						<c:choose>
							<c:when test="${sortSeq eq 2 }">
								<dsp:include page="registry_items_guest_price.jsp" flush="true" >
									<dsp:param name="registryId" value="${registryId}"/>
									<dsp:param name="startIdx" value="0"/>
									<dsp:param name="isGiftGiver" value="false"/>
									<dsp:param name="blkSize" value="${maxBulkSize}"/>	
									<dsp:param name="isAvailForWebPurchaseFlag" value="false"/>
									<dsp:param name="sortSeq" value="${sortSeq}" />
									<dsp:param name="view" value="${view}" />
									<dsp:param name="eventType" value="${eventType}"/>
									<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
								</dsp:include>
							</c:when>
							<c:otherwise>
								<dsp:include page="registry_items_guest_category.jsp" flush="true" >
									<dsp:param name="registryId" value="${registryId}"/>
									<dsp:param name="startIdx" value="0"/>
									<dsp:param name="isGiftGiver" value="false"/>
									<dsp:param name="blkSize" value="${maxBulkSize}"/>	
									<dsp:param name="isAvailForWebPurchaseFlag" value="false"/>
									<dsp:param name="sortSeq" value="${sortSeq}" />
									<dsp:param name="view" value="${view}" />
									<dsp:param name="eventType" value="${eventType}"/>
									<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
								</dsp:include>
							</c:otherwise>
						</c:choose>
												
						</c:otherwise>
						</c:choose>
						
						<c:if test="${eventTypeCode ne 'BA1' }">
							<div class="clearfix marTop_5">
								<div class="grid_2 suffix_1 alpha"> 
									<bbbt:textArea key="txt_regitem_store_image" language="${pageContext.request.locale.language}" />
								</div>
								<div class="grid_9 alpha"> 
								<bbbt:textArea key="txt_mng_mxregitem_chkfirst" language="${pageContext.request.locale.language}" />								
                                <c:choose>
                                    <c:when test="${appid == BedBathCanadaSite}">
                                        <a href="${contextPath}/selfservice/CanadaStoreLocator" title='<bbbl:label key="lbl_mng_regitem_clickhere" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_mng_regitem_clickhere" language="${pageContext.request.locale.language}" /></a>&nbsp;
                                    </c:when>
                                    <c:otherwise>
                                      <c:if test="${MapQuestOn}">
                                        <a href="${contextPath}/selfservice/FindStore" title='<bbbl:label key="lbl_mng_regitem_clickhere" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_mng_regitem_clickhere" language="${pageContext.request.locale.language}" /></a>&nbsp;
                                      </c:if>
                                    </c:otherwise>
                                </c:choose>
								<c:choose>
									<c:when test="${currentSiteId eq BuyBuyBabySite}">
										<bbbt:textArea key="txt_mng_regitem_callbbby" language="${pageContext.request.locale.language}" />
									</c:when>
									<c:otherwise>
										<bbbt:textArea key="txt_mng_mxregitem_callbbb" language="${pageContext.request.locale.language}" />
									</c:otherwise>
							    </c:choose>
							    <p class="alpha space"><bbbt:textArea key="txt_mng_mxregitem_ask_updcopy" language="${pageContext.request.locale.language}" /></p>
							    </div>
							</div>
							
						</c:if> 
					</div>
									
				<%-- Droplet Placeholder  output RegistryItemsDisplayDroplet ends --%>
			</dsp:oparam>
			<dsp:oparam name="error">
				<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
				<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	<c:import url="/selfservice/find_in_store.jsp" >
		<c:param name="enableStoreSelection" value="true"/>
	</c:import>
	
	<c:import url="/_includes/modules/change_store_form.jsp" >
		<c:param name="action" value="${contextPath}${finalUrl}"/>
	</c:import>

  	<script type="text/javascript">
		function callCertonaResxRun(certonaString) {
	       var resx = new Object();
		   resx.event = "shopping+cart";
	          resx.itemid = certonaString;
	          if (typeof certonaResx === 'object') { certonaResx.run();  }
	   }
    </script>
</dsp:page>