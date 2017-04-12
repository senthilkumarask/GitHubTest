<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/RegistryItemsDisplayDroplet" />
	<dsp:importbean
		bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
					
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	
	<%-- Droplet Placeholder --%>

    <c:set var="registryId"><dsp:valueof value="${fn:escapeXml(param.registryId)}"/></c:set>
    <c:set var="eventType"><dsp:valueof value="${fn:escapeXml(param.eventType)}"/></c:set>
    
   	<%-- Validate external parameters --%>
	<dsp:droplet name="ValidateParametersDroplet">
	    <dsp:param value="registryId;eventType" name="paramArray" />
	    <dsp:param value="${registryId};${eventType}" name="paramsValuesArray" />
	    <dsp:oparam name="error">
	      <dsp:droplet name="RedirectDroplet">
	        <dsp:param name="url" value="/404.jsp" />
	      </dsp:droplet>
	    </dsp:oparam>
    </dsp:droplet> 
    
	<dsp:getvalueof var="sortSeq" param="sortSeq"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="pwsurl" param="pwsurl" />
	<dsp:getvalueof var="guestUrl" value="${contextPath}/giftregistry/view_registry_guest.jsp?registryId=${registryId}&eventType=${eventType}&pwsurl=${pwsurl}" />

	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	
	<c:if test="${not empty registryId}">
		<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
				<dsp:oparam name="output">
					 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
				</dsp:oparam>
		</dsp:droplet>
		<dsp:droplet name="RegistryItemsDisplayDroplet">
			<dsp:param name="registryId" value="${registryId}" />
			<dsp:param name="startIdx" param="startIdx" />
			<dsp:param name="isGiftGiver" param="isGiftGiver" />
			<dsp:param name="blkSize" param="blkSize" />
			<dsp:param name="isAvailForWebPurchaseFlag"
				param="isAvailForWebPurchaseFlag" />
			<dsp:param name="userToken" param="userToken" />
			<dsp:param name="sortSeq" param="sortSeq" />
			<dsp:param name="view" param="view" />
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:param name="profile" bean="Profile"/>

			<%--RegistryItemsDisplayDroplet output parameter starts --%>
			<dsp:oparam name="output">
			<dsp:getvalueof var="emptyList" param="emptyList" />
			<div class="small-12 columns">
						<dsp:include page="registry_sort_controls.jsp">
							<dsp:param name="sortSeq" param="sortSeq"/>
							<dsp:param name="view" param="view"/>
							<dsp:param name="pwsurl" param="pwsurl"/>
						</dsp:include>
						</div>
						<div class="small-12 columns">
						
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
								<bbbe:error key="err_no_reg_item_details" language="${pageContext.request.locale.language}"/>
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
									<dsp:param name="isGiftGiver" value="true"/>
									<dsp:param name="blkSize" value="${maxBulkSize}"/>	
									<dsp:param name="isAvailForWebPurchaseFlag" value="false"/>
									<dsp:param name="sortSeq" value="${sortSeq}" />
									<dsp:param name="view" value="${view}" />
									<dsp:param name="eventType" value="${eventType}"/>
									<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
									<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
								</dsp:include>
							</c:when>
							<c:otherwise>
								<dsp:include page="registry_items_guest_category.jsp" flush="true" >
									<dsp:param name="registryId" value="${registryId}"/>
									<dsp:param name="startIdx" value="0"/>
									<dsp:param name="isGiftGiver" value="true"/>
									<dsp:param name="blkSize" value="${maxBulkSize}"/>	
									<dsp:param name="isAvailForWebPurchaseFlag" value="false"/>
									<dsp:param name="sortSeq" value="${sortSeq}" />
									<dsp:param name="view" value="${view}" />
									<dsp:param name="eventType" value="${eventType}"/>
									<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
									<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
								</dsp:include>
							</c:otherwise>
						</c:choose>
												
						</c:otherwise>
						</c:choose>
						
						<c:if test="${eventTypeCode ne 'BA1' }">
							<div class="row">
								<div class="small-3 columns"> 
									<bbbt:textArea key="txt_regitem_store_image" language="${pageContext.request.locale.language}" />
								</div>
								<div class="small-9 columns"> 
								<bbbt:textArea key="txt_mng_regitem_chkfirst" language="${pageContext.request.locale.language}" />								
                                <c:choose>
                                    <c:when test="${appid == TBS_BedBathCanadaSite}">
                                        <a href="${contextPath}/selfservice/CanadaStoreLocator" title='<bbbl:label key="lbl_mng_regitem_clickhere" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_mng_regitem_clickhere" language="${pageContext.request.locale.language}" /></a>&nbsp;
                                    </c:when>
                                    <c:otherwise>
                                      <c:if test="${MapQuestOn}">
                                        <a href="${contextPath}/selfservice/FindStore" title='<bbbl:label key="lbl_mng_regitem_clickhere" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_mng_regitem_clickhere" language="${pageContext.request.locale.language}" /></a>&nbsp;
                                      </c:if>
                                    </c:otherwise>
                                </c:choose>
								<c:choose>
									<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
										<bbbt:textArea key="txt_mng_regitem_callbbby" language="${pageContext.request.locale.language}" />
									</c:when>
									<c:otherwise>
										<bbbt:textArea key="txt_mng_regitem_callbbb" language="${pageContext.request.locale.language}" />
									</c:otherwise>
							    </c:choose>
							    <p class="alpha space"><bbbt:textArea key="txt_mng_regitem_ask_updcopy" language="${pageContext.request.locale.language}" /></p>
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
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
<%--BBBSL-6813 | Adding certona tagging on guest registry view --%>
  	<script type="text/javascript">
			var links = "${link}";
			if(links.length >1){
				links = links.substring(0, links.length - 1);
			}
			var resx = new Object();
			resx.appid ="${appIdCertona}";
			resx.links = links;
			resx.pageid = "${pageIdCertona}";
			 if (typeof certonaResx === 'object') { 
					certonaResx.run();  
				};
		function callCertonaResxRun(certonaString) {
	       resx = new Object();
	        resx.appid = "${appIdCertona}";
			resx.event = "addtocart_op";
	          resx.itemid = certonaString;
			resx.pageid = "";
	        resx.links = "";
	        if (typeof certonaResx === 'object') { 
	        	certonaResx.run();  
	   }
	   }
    </script>
</dsp:page>