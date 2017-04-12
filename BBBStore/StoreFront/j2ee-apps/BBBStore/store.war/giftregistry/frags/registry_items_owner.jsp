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
	<dsp:importbean bean="/com/bbb/simplifyRegistry/droplet/RegistryStatusDroplet" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="appid" bean="Site.id" />
    <dsp:getvalueof var="isMyItemsCheckList" param="isMyItemsCheckList"/>
	
	<input type="hidden" name="isMyItemsCheckListHiddenValue" id="isMyItemsCheckListHiddenValue" value="${isMyItemsCheckList}" />
	<c:set var="myItemsFlag">
			<bbbc:config key="My_Items_Checklist_Flag" configName="FlagDrivenFunctions" />
	</c:set>
	<dsp:getvalueof var="isChecklistDisabled" param="isChecklistDisabled"/>
	
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="c1name" param="c1name" />
	<dsp:getvalueof var="c2name" param="c2name" />
	<dsp:getvalueof var="c3name" param="c3name" />
	<dsp:getvalueof var="qty" param="qty" />
	<dsp:getvalueof var="c1id" param="c1id" />
	<dsp:getvalueof var="c2id" param="c2id" />
	<dsp:getvalueof var="c3id" param="c3id" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sortSeq"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="regEventDate" param="regEventDate" />
	<c:if test="${not empty registryId}">
		<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
				<dsp:oparam name="output">
					 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
				</dsp:oparam>
		</dsp:droplet>

		<dsp:droplet name="RegistryStatusDroplet">
			<dsp:param value="${registryId}" name="registryId" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="regPublic" param="regPublic"/>
			</dsp:oparam>
		</dsp:droplet>

		<c:set var="myItemsCheckListFlag" value="${isMyItemsCheckList}"/>
		<c:if test="${sortSeq eq 2}">
			<c:set var="myItemsCheckListFlag" value="false"/>
		</c:if>

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
			<dsp:param name="c1name" value="${c1name}"/>
			<dsp:param name="c2name" value="${c2name}"/>
			<dsp:param name="c3name" value="${c3name}"/>
			<dsp:param name="qty" value="${qty}"/>
			<dsp:param name="c1id" value="${c1id}"/>
			<dsp:param name="c2id" value="${c2id}"/>
			<dsp:param name="c3id" value="${c3id}"/>
			<dsp:param name="profile" bean="Profile"/>
			<dsp:param name="eventDate" value="${regEventDate}"/>
			<dsp:param name="isMyItemsCheckList" value="${myItemsCheckListFlag}"/>
			<%--RegistryItemsDisplayDroplet output parameter starts --%>
			<dsp:oparam name="output">
				<dsp:getvalueof var="skuList" param="skuList" scope="request"/>
				<dsp:getvalueof var="certonaSkuList" param="certonaSkuList" scope="request"/>
				<dsp:getvalueof var="totEntries" param="totEntries"/>
				
				<c:choose>
					<c:when test="${empty ephCategoryBuckets && totEntries == 0 && giftsRegistered gt 0 && view ==2}">
						<c:set var="showForRemainingFilter" value="true" scope="session"/>
					</c:when>
					<c:otherwise>
						<c:set var="showForRemainingFilter" value="false" scope="session"/>
					</c:otherwise>
				</c:choose>
				
					<div class="grid_12 alpha omega marTop_10">
						<dsp:getvalueof var="addedQty" param="qtyof" scope="request"/>
						<dsp:getvalueof var="addedCount" param="addedCount" scope="request"/>
						<dsp:form method="post" action="index.jsp" id="frmRegistryProduct">
						<dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="" />
							<dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registryId}" />
							<c:choose>
								<c:when test = "${isMyItemsCheckList eq 'true' && ( not empty c1name )}"> 
							<div class="clearfix showCategory seeAllItemsBtn" >
									<div class="grid_9 alpha clearfix space">
										<ul>
											<dsp:getvalueof var="other" param="other" />
											<c:choose>
												<c:when test="${!other}">
													<li data-category="c1" class="categoryName">${c1name}</li>
													<li data-category="c2"
														class="categoryName <c:if test="${empty c3name}">selected </c:if> ">${c2name}
														<c:choose>
															<c:when test="${empty c3name}">
																<span class="c3count_myitems" data-id = c3Count_${c2id} > (${addedQty})</span>
													</li>
												</c:when>
												<c:otherwise>
													</li>
													<li data-category="c2" class="categoryName selected">
														${c3name} <span class="c3count_myitems" data-id = c3Count_${c3id}> (${addedQty})</span>
													</li>
												</c:otherwise>
											</c:choose>
											</c:when>
											<c:otherwise>
												<li data-category="c1" class="categoryName">Others</li>
												<c:choose>
													<c:when test="${empty c3name}">
														<li data-category="c2" class="categoryName selected">
																${c2name}<span class="c3count_myitems" data-id = c3Count_${c2id}> (${addedQty})</span></li>
													</c:when>
													<c:otherwise>
														<li data-category="c2" class="categoryName">${c2name}</li>
														<li data-category="c2" class="categoryName selected">
															${c3name} <span class="c3count_myitems" data-id = c3Count_${c3id}> (${addedQty})</span>
														</li>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
				
										</c:choose>

										</ul>
									</div>
									<div class="grid_3 omega ">
												<c:set var="capitalizeTextClass" value="" />
												<c:if test="${appid ne 'BuyBuyBaby'}">
													<c:set var="capitalizeTextClass" value="allCaps" />
												</c:if>
												<dsp:a href="view_registry_owner.jsp" bean="GiftRegistryFormHandler.viewEditRegistry" value=""  iclass="${capitalizeTextClass} button-Small btnSecondary fr" requiresSessionConfirmation="false">
												<dsp:param name="registryId" value="${registryId}" />
												<dsp:param name="eventType" value="${eventType}" />
                                               <span class="txtOffScreen">Edit my ${eventType} registry .   </span>
                                               <c:choose>                                               
                                               <c:when test="${appid eq 'BuyBuyBaby'}">
                                               		<span><bbbl:label key='lbl_ic_see_all_items' language="${pageContext.request.locale.language}" /></span>
                                               </c:when>
                                               <c:otherwise>
                                               		<strong><bbbl:label key='lbl_ic_see_all_items' language="${pageContext.request.locale.language}" /></strong>
                                               </c:otherwise>
                                               </c:choose>
                                               
												
											</dsp:a>
								</div>
								<div class="clear"></div>
							</div>   
							</c:when>
							<c:otherwise>
							<div class="grid_7 alpha clearfix space sorting giftViewSortingControls">
							
								<c:set var="showMeCss" value="style='width:115px;'" />
								<c:choose>
									<%-- ALL --%>
									<c:when test="${(empty view) || view ==1}">
										<c:set var="showMeCss" value="style='width:53px;'" />  
									</c:when>
									<%-- purchased --%>
									<c:when test="${(not empty view) && view ==3}">
										<c:set var="showMeCss" value="style='width:115px;'" />  
									</c:when>
									<%-- remaining --%>
									<c:when test="${(empty view) || view ==2}">
										<c:set var="showMeCss" value="style='width:115px;'" />  
									</c:when>
								</c:choose>
								
								<label for="filterRegistryView"><bbbl:label key='lbl_mng_regitem_view' language="${pageContext.request.locale.language}" /></label>								
								<select class="sorting" id="filterRegistryView" aria-label="Show me the purchased or remaining products from this registry. Your page will be refreshed once you have selected an option" name="filterRegistryView" ${showMeCss}>
								  <option value="1" <c:if test="${(empty view) || view ==1}">selected="selected" </c:if>><bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /><span class="icon-chevron-down" aria-hidden="true"></span></option>
								  <option value="3" <c:if test="${(not empty view) && view ==3}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_purchased_sort_owner' language="${pageContext.request.locale.language}" /><span class="icon-chevron-down" aria-hidden="true"></span></option>								  
								  <option value="2" <c:if test="${(not empty view) && view ==2}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /><span class="icon-chevron-down" aria-hidden="true"></span></option>
								<select>
							
								
									
								<c:set var="sortCss" value="style='width:75px;'" />
								<c:choose>
									<%-- category --%>
									<c:when test="${(empty sortSeq) || sortSeq ==1}">
										<c:set var="sortCss" value="style='width:105px;'" />  
									</c:when>
									<%-- price --%>
									<c:when test="${(not empty sortSeq) && sortSeq ==2}">
										<c:set var="sortCss" value="style='width:75px;'" />  
									</c:when>									
								</c:choose>
								
								
								<label for="sortRegistryBy"><bbbl:label key='lbl_mng_regitem_sortby' language="${pageContext.request.locale.language}" /></label>								 
								<select id="sortRegistryBy" class="sorting" aria-label="Sort the registry by category or price. Your page will be refreshed once you have selected an option" name="sortRegistryBy" ${sortCss }>
								  <option value="1" <c:if test="${(empty sortSeq) || sortSeq ==1}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /><span class="icon-chevron-down" aria-hidden="true"></span></option>
								  <option value="2" <c:if test="${(not empty sortSeq) && sortSeq ==2}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /><span class="icon-chevron-down" aria-hidden="true"></span></option>								  
								<select>
							
							
								<%-- 
								<ul class="grid_3 suffix_3 marLeft_10">
									<li class="bold"><bbbl:label key='lbl_mng_regitem_sortby' language="${pageContext.request.locale.language}" /></li>
									<c:if test="${(empty sortSeq) || sortSeq ==1}">
									<li>
										<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqCat" name="sorting" type="radio"
												checked="true" value="1">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqCat"/>
										</dsp:input>
										<label id="lblsortSeqCat" for="sortSeqCat"><bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /></label>
									</li>
									<li>
										<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqPrice" name="sorting" type="radio"
											value="2">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqPrice"/>
										</dsp:input>
										<label id="lblsortSeqPrice" for="sortSeqPrice"><bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></label>
									</li>
									</c:if>
									<c:if test="${(not empty sortSeq) && sortSeq ==2}">
									<li>
										<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqCat" name="sorting" type="radio"
											value="1">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqCat"/>
										</dsp:input>
										<label id="lblsortSeqCat" for="sortSeqCat"><bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /></label>
									</li>
									<li>
										<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqPrice" name="sorting" type="radio"
											checked="true" value="2">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqPrice"/>
										</dsp:input>
										<label id="lblsortSeqPrice" for="sortSeqPrice"><bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></label>
									</li>
									</c:if>
								</ul>
								--%>
								
								
								
								<%-- 
								<div class="grid_4 alpha">	
									<ul class="fr">
										<li class="bold"><bbbl:label key='lbl_mng_regitem_view' language="${pageContext.request.locale.language}" /></li>
										<c:if test="${(empty view) || view ==1}">
										<li>
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio" 
												checked="true" value="1">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                                            </dsp:input>
											<label id="lblall" for="all"><bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></label>
										</li>
										<li>
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio"
												value="3">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                                            </dsp:input>
											<label id="lblpurchased" for="purchased"><bbbl:label key='lbl_mng_regitem_purchased_sort_owner' language="${pageContext.request.locale.language}" /></label>
										</li>
										<li>
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio"
												value="2">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                                            </dsp:input>
											<label id="lblRemaining" for="Remaining"><bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></label>
										</li>
										</c:if>
										<c:if test="${(not empty view) && view ==3}">
										<li>
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio" 
												value="1">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                                            </dsp:input>
											<label id="lblall" for="all"><bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></label>
										</li>
										<li>
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio"
												checked="true" value="3">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                                            </dsp:input>
											<label id="lblpurchased" for="purchased"><bbbl:label key='lbl_mng_regitem_purchased_sort_owner' language="${pageContext.request.locale.language}" /></label>
										</li>
										<li>
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio"
												value="2">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                                            </dsp:input>
											<label id="lblRemaining" for="Remaining"><bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></label>
										</li>
										</c:if>
										<c:if test="${(not empty view) && view ==2}">
										<li>
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio" 
												value="1">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                                            </dsp:input>
											<label id="lblall" for="all"><bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></label>
										</li>
										<li>
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio"
												value="3">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                                            </dsp:input>
											<label id="lblpurchased" for="purchased"><bbbl:label key='lbl_mng_regitem_purchased_sort_owner' language="${pageContext.request.locale.language}" /></label>
										</li>
										<li>
											<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio"
												checked="true" value="2">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                                            </dsp:input>
											<label id="lblRemaining" for="Remaining"><bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></label>
										</li>
										</c:if>
									</ul>								
								</div>
									--%>
							</div>
							
						
						<c:choose>
							<c:when test="${isMyItemsCheckList eq 'true'}">
								<c:choose>
									<c:when test="${totEntries eq 0}">
										<c:set var="showExpandCollapse" value="false"/>																			
									</c:when>
									<c:otherwise>
										<c:set var="showExpandCollapse" value="true"/>
						</c:otherwise>
						</c:choose>
							</c:when>
							<c:otherwise>
								<c:set var="showExpandCollapse" value="true"/>
							</c:otherwise>
						</c:choose>
						<c:if test="${showExpandCollapse eq 'true'}"> 
						<div class="grid_3 omega expandCollapse">
							<ul>
								<li class="expandAll"><a href="#" class="button-Small btnSecondary"><bbbl:label key="lbl_Expand_All" language="${pageContext.request.locale.language}"/></a></li>								
								<li class="collapseAll"><a href="#" class="button-Small btnSecondary"><bbbl:label key="lbl_Collapse_All" language="${pageContext.request.locale.language}"/></a></li>
							</ul>
						</div>
						</c:if>
						<div class="clear"></div>
						</c:otherwise>
						</c:choose>
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
									<dsp:param name="regPublic" value="${regPublic}"/>								
									<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}"/>
									<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
								</dsp:include>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${isMyItemsCheckList}">
										<dsp:include page="registry_items_owner_category_checklist.jsp" flush="true" >
											<dsp:param name="registryId" value="${registryId}"/>
											<dsp:param name="startIdx" value="0"/>
											<dsp:param name="isGiftGiver" value="false"/>
											<dsp:param name="blkSize" value="${maxBulkSize}"/>
											<dsp:param name="isAvailForWebPurchaseFlag" value="true"/>
											<dsp:param name="sortSeq" value="${sortSeq}" />
											<dsp:param name="view" value="${view}" />
											<dsp:param name="eventType" value="${eventType}"/>
											<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
											<dsp:param name="regPublic" value="${regPublic}"/>
											<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}"/>
											<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
											<dsp:param name="c1id" value="${c1id}"/>
											<dsp:param name="addedCount" value="${addedCount}" />
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
											<dsp:param name="regPublic" value="${regPublic}"/>
											<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}"/>
											<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
										</dsp:include>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						<%-- End ForEach for categoryBuckets map --%>
					</div>
                    <div class="clear"></div>
					<c:if test="${showExpandCollapse eq 'true'}"> 
                    <div class="grid_10 alpha omega expandCollapse clearfix">
                        <ul>
                           <li class="expandAll"><a href="#" class="button-Small btnSecondary"><bbbl:label key="lbl_Expand_All" language="${pageContext.request.locale.language}"/></a></li>								
								<li class="collapseAll"><a href="#" class="button-Small btnSecondary"><bbbl:label key="lbl_Collapse_All" language="${pageContext.request.locale.language}"/></a></li>
                        </ul>
                    </div>
                    </c:if>
				<%-- Droplet Placeholder  output RegistryItemsDisplayDroplet ends --%>
			</dsp:oparam>
			<dsp:oparam name="error">
				<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
				<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	<script type="text/javascript">
    function callCertonaResxRun(certonaString) {
	       var resx = window.resx || new Object();
		   resx.event = "addtocart_op";
           resx.itemid = certonaString;
		   resx.links = "";
           if (typeof certonaResx === 'object') { certonaResx.run();  }
           
    }
   </script>
	
</dsp:page> 
