<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/RegistryItemsDisplayDroplet" />
	<dsp:importbean bean="/com/bbb/email/EmailHolder"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean
		bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />	
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="appid" bean="Site.id" />
	
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sortSeq"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
	<dsp:oparam name="output">
		 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	</dsp:oparam>
    </dsp:droplet>
	<c:if test="${not empty registryId}">
		<dsp:droplet name="RegistryItemsDisplayDroplet">
			<dsp:param name="registryId" value="${registryId}" />
			<dsp:param name="startIdx" param="startIdx" />
			<dsp:param name="isGiftGiver" param="isGiftGiver" />
			<dsp:param name="eventTypeCode" param="eventTypeCode" />
			<dsp:param name="blkSize" param="blkSize" />
			<dsp:param name="isAvailForWebPurchaseFlag" param="isAvailForWebPurchaseFlag" />
			<dsp:param name="sortSeq" value="${sortSeq}" />
			<dsp:param name="view" param="view" />
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:param name="profile" bean="Profile"/>
			<%--RegistryItemsDisplayDroplet output parameter starts --%>
			<dsp:oparam name="output">
				<dsp:getvalueof var="skuList" param="skuList" scope="request"/>
				<dsp:getvalueof var="certonaSkuList" param="certonaSkuList" scope="request"/>
						<dsp:form method="post" action="index.jsp" id="frmRegistryProduct">
						<dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="" />
							<dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registryId}" />
							<div class="row sorting giftViewSortingControls registrySorting">
								<div class="small-12 large-3 columns">
									<bbbl:label key='lbl_mng_regitem_sortby' language="${pageContext.request.locale.language}" />:
									<c:if test="${(empty sortSeq) || sortSeq ==1}">
										<label id="lblsortSeqCat" for="sortSeqCat" class="tiny button show-me"><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqCat" name="sorting" type="radio"  style="display:none"
												checked="true" value="1">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqCat"/>
										</dsp:input>
										<bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /></label>
										<label id="lblsortSeqPrice" for="sortSeqPrice" class="tiny button notActive tiny button"><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqPrice" name="sorting" type="radio" style="display:none"
											value="2">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqPrice"/>
										</dsp:input>
										<bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></label>
									</c:if>
									<c:if test="${(not empty sortSeq) && sortSeq ==2}">
										<label id="lblsortSeqCat" for="sortSeqCat" class="tiny button notActive"><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqCat" name="sorting" type="radio" style="display:none"
											value="1">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqCat"/>
										</dsp:input>
										<bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /></label>
										<label id="lblsortSeqPrice" for="sortSeqPrice" class="tiny button"><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqPrice" name="sorting" type="radio" style="display:none"
											checked="true" value="2">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqPrice"/>
										</dsp:input>
										<bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></label>
									</c:if>
                                </div>
								<div class="small-12 large-5 columns txt-alg-sty">
										<bbbl:label key='lbl_mng_regitem_view' language="${pageContext.request.locale.language}" />:
										<c:if test="${(empty view) || view ==1}">
											<label id="lblall" for="all" class="tiny button show-me">
                                            <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio" style="display:none"
												checked="true" value="1">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                                            </dsp:input>
											<bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></label>
                                            <label id="lblpurchased" for="purchased" class="tiny button notActive ">
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio" style="display:none"
												value="3">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                                            </dsp:input>
											<bbbl:label key='lbl_mng_regitem_purchased_sort_owner' language="${pageContext.request.locale.language}" /></label>
                                            <label id="lblRemaining" for="Remaining" class="tiny button notActive show-me"><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio" style="display:none"
												value="2">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                                            </dsp:input>
											<bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></label>
										</c:if>
										<c:if test="${(not empty view) && view ==3}">
                                            <label id="lblall" for="all" class="tiny button notActive show-me"><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio" style="display:none"
												value="1">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                                            </dsp:input>
											<bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></label>
                                            <label id="lblpurchased" for="purchased" class="tiny button "><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio" style="display:none"
												checked="true" value="3">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                                            </dsp:input>
											<bbbl:label key='lbl_mng_regitem_purchased_sort_owner' language="${pageContext.request.locale.language}" /></label>
                                            <label id="lblRemaining" for="Remaining" class="tiny button notActive"><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio" style="display:none"
												value="2">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                                            </dsp:input>
											<bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></label>
										</c:if>
										<c:if test="${(not empty view) && view ==2}">
											<label id="lblall" for="all" class="tiny button notActive"><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio"  style="display:none"
												value="1">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                                            </dsp:input>
											<bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></label>
                                            <label id="lblpurchased" for="purchased" class="tiny button notActive"><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio" style="display:none"
												value="3">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                                            </dsp:input>
											<bbbl:label key='lbl_mng_regitem_purchased_sort_owner' language="${pageContext.request.locale.language}" /></label>
                                            <label id="lblRemaining" for="Remaining" class="tiny button "><dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio" style="display:none"
												checked="true" value="2">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                                            </dsp:input>
											<bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></label>
										</c:if>
								</div>
							
						
						<!-- <div class="small-12 large-4 columns omega expandCollapse right">
								<div class="expandAll right"><a href="#">Expand All</a></div>
								<div class="sep right">|</div>
								<div class="collapseAll right"><a href="#">Collapse All</a></div>
						</div> -->
                        </div><br/><br/><br/><br/>
						</dsp:form>
						<c:set var="maxBulkSize" scope="request">
			  				<bbbc:config key="MaxSizeRegistryItems" configName="ContentCatalogKeys" />
		    			</c:set>
						<%-- ForEach for categoryBuckets map --%>
						<c:choose>
							<c:when test="${sortSeq eq 2 }">
								<dsp:include page="registry_items_owner_price.jsp" flush="true" >
									<dsp:param name="registryId" value="${registryId}"/>
									<dsp:param name="startIdx" value="0"/>
									<dsp:param name="isGiftGiver" value="false"/>
									<dsp:param name="blkSize" value="${maxBulkSize}"/>
									<dsp:param name="isAvailForWebPurchaseFlag" value="true"/>
									<dsp:param name="sortSeq" value="${sortSeq}" />
									<dsp:param name="view" value="${view}" />
									<dsp:param name="eventType" value="${eventType}"/>
									<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
									<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
								</dsp:include>
							</c:when>
							<c:otherwise>
								<dsp:include page="registry_items_owner_category.jsp" flush="true" >
									<dsp:param name="registryId" value="${registryId}"/>
									<dsp:param name="startIdx" value="0"/>
									<dsp:param name="isGiftGiver" value="false"/>
									<dsp:param name="blkSize" value="${maxBulkSize}"/>
									<dsp:param name="isAvailForWebPurchaseFlag" value="true"/>
									<dsp:param name="sortSeq" value="${sortSeq}" />
									<dsp:param name="view" value="${view}" />
									<dsp:param name="eventType" value="${eventType}"/>
									<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
									<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
								</dsp:include>
							</c:otherwise>
						</c:choose>
						<%-- End ForEach for categoryBuckets map --%>
                   <!--  <div class="small-4 columns alpha omega expandCollapse">
                            <div class="expandAll"><a href="#">Expand All</a></div>
                            <div class="sep">|</div>
                            <div class="collapseAll"><a href="#">Collapse All</a></div>
                    </div> -->
				<%-- Droplet Placeholder  output RegistryItemsDisplayDroplet ends --%>
			</dsp:oparam>
			<dsp:oparam name="error">
				<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
				<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
	<dsp:getvalueof var="userId" bean="Profile.id"/>
		
	<script type="text/javascript">
	var resx = new Object();
	resx.appid ="${appIdCertona}";
	resx.links = "${itemList}";
	resx.pageid = "${pageIdCertona}";
	resx.customerid = "${userId}";
	resx.event="registry+"+"${eventType}";
	resx.itemid="${itemList}";
	resx.transactionid = "${registryId}";
	 if (typeof certonaResx === 'object') { 
			certonaResx.run();  
		};
    function callCertonaResxRun(certonaString) {
	       var resx = window.resx || new Object();
           resx.itemid = certonaString;
		   resx.links = "";
	       resx.event="registry+"+"${eventType}";
           if (typeof certonaResx === 'object') { certonaResx.run();  }
           
    }
   </script>
	
</dsp:page> 