<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryItemsDisplayDroplet" />
	<dsp:importbean	bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />					
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>	
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${currentSiteId}" />
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
    
	<dsp:getvalueof var="sortSeq" value="${fn:escapeXml(param.sortSeq)}"/>
	<dsp:getvalueof var="view" value="${fn:escapeXml(param.view)}" />
	<dsp:getvalueof var="pwsurl" param="pwsurl" />
	<dsp:getvalueof var="guestUrl" value="${contextPath}/giftregistry/view_registry_guest.jsp?registryId=${registryId}&eventType=${eventType}&pwsurl=${pwsurl}" />

	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	
	<dsp:getvalueof var="isChecklistDisabled" value="${param.isChecklistDisabled}"/>
	<c:set var="myItemsFlag">
		<bbbc:config key="My_Items_Checklist_Flag" configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="myItemsGiftGiver">
		<bbbc:config key="GiftGiver_Items_Checklist_Flag" configName="FlagDrivenFunctions" />
	</c:set>
	<c:choose>
		<c:when test="${isChecklistDisabled or null eq isChecklistDisabled or empty isChecklistDisabled or !myItemsFlag or !myItemsGiftGiver}">
			<c:set var="isMyItemsCheckList">false</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="isMyItemsCheckList">true</c:set>
		</c:otherwise>
	</c:choose>
	<c:set var="myItemsCheckListFlag" value="${isMyItemsCheckList}"/>
		<c:if test="${sortSeq eq 2}">
			<c:set var="myItemsCheckListFlag" value="false"/>
		</c:if>
		
	<input type="hidden" name="isMyItemsCheckListHiddenFlagGuest" id="isMyItemsCheckListHiddenFlagGuest" value="${isMyItemsCheckList}" />
	
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
			<dsp:param name="eventDate" value="${param.eventDate}"/>	
			<dsp:param name="isMyItemsCheckList" value="${myItemsCheckListFlag}"/>
			<%--RegistryItemsDisplayDroplet output parameter starts --%>
			<dsp:oparam name="output">
			<dsp:getvalueof var="omnitureRegistryProd" param="omniProductList"/>
			<dsp:getvalueof var="showStartBrowsing" param="showStartBrowsing"/>
		
			<script>
			var omniString = '${omnitureRegistryProd}';
            var registryId = '${registryId}';
			var eventType = '${eventType}';
			</script>
				<dsp:getvalueof var="emptyList" param="emptyList" />
				
				
				
				
				
				<div class="grid_12 clearfix marTop_20 marBottom_20">
				
					<%-- 
					<dsp:include page="registry_sort_controls.jsp">
						<dsp:param name="sortSeq" param="sorting"/>
						<dsp:param name="view" param="view"/>
					</dsp:include>
					--%>
					
					
					<div class="grid_7 alpha clearfix space sorting giftViewSortingControls">
					
						<c:set var="showMeCss" value="style='width:53px;'" />
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
							<option value="1" <c:if test="${(empty view) || view ==1}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></option>
						 	<option value="3" <c:if test="${(not empty view) && view ==3}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_purchased_sort_owner' language="${pageContext.request.locale.language}" /></option>								  
							<option value="2" <c:if test="${(not empty view) && view ==2}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></option>
						<select>
						 
						 
						<c:set var="sortCss" value="style='width:105px;'" />
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
						<select  id="sortRegistryBy" class="sorting" aria-label="Sort the registry by category or price. Your page will be refreshed once you have selected an option" name="sortRegistryBy" ${sortCss }>
							<option value="1" <c:if test="${(empty sortSeq) || sortSeq ==1}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /></option>
							<option value="2" <c:if test="${(not empty sortSeq) && sortSeq ==2}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></option>								  
						</select>
					</div>
					
				</div>
				
				<%-- Copy Registry changes start --%>
				<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
				
				<%-- Debugging  
				registryId: ${registryId} // 
				sessionRegId: ${sessionRegId }
				--%>
		        <c:if test="${registryId!=sessionRegId}">
					<dsp:getvalueof var="totalToCopy" param="totalToCopy"/>
		      	
		      		 
		      		<dsp:droplet name="AddItemToGiftRegistryDroplet">
						<dsp:param name="siteId" value="${appid}"/>
					</dsp:droplet>
					
					
					<%-- need this at all?
					<dsp:droplet name="AddItemToGiftRegistryDroplet">
						<dsp:param name="siteId" value="${appid}"/>
							<dsp:oparam name="output">
							<c:set var="sizeValue">
								<dsp:valueof param="size" />
							</c:set>
						</dsp:oparam>
					</dsp:droplet>
					--%>
					
					
		    		<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>		    		
		    		<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
					<dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
		    	
		    		<%-- Debugging 
		    		registrySkinnyVOList: ${registrySkinnyVOList } // 
					totalToCopy: ${totalToCopy}  //  
					isTransient: ${isTransient} // 
					sizeValue : ${sizeValue}
					--%>  
		    	
		  			<c:if test="${totalToCopy>=1 && !isTransient && sizeValue>=1}">		  			
                 		<div id="copyRegistrySection" class="kickstarterSection grid_12 noMarBot noMarTop">
							<div class="kickstarterSectionHeader grid_12">
								<div class="grid_3 alpha  ">           		
				           			<h2><bbbt:textArea key="txt_copy_this_registry_header" language ="${pageContext.request.locale.language}"/></h2>
			           			</div>
			           			<div class="grid_9 omega clearfix">
				           			<p class="clearfix">
				           				<span class="grid_6 alpha clearfix">
				           					<bbbt:textArea key="txt_copy_this_registry_description" language ="${pageContext.request.locale.language}" />
				           				</span>
				           				
				           				<span class="grid_2 omega clearfix">
					           				
											
			           						<%-- --%>
		  									<span class="">
			           							<input type="button" class="btnCopyRegistry button-Large btnPrimary <c:if test='${isInternationalCustomer}'>disabled</c:if>"  <c:if test="${isInternationalCustomer}">disabled="disabled"</c:if>value="Copy This Registry" />
											</span>
										</span>
				           			</p>
			           			</div>
			           			<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
			           			
								
                      		</div>
                      	</div>
								
					</c:if>
				</c:if>
				
				<%-- Copy Registry changes end --%>
				
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
									<c:choose>
										<c:when test="${isMyItemsCheckList}">
											<dsp:include page="registry_items_guest_category_checklist.jsp" flush="true" >
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
												
						</c:otherwise>						
					</c:choose>
					<c:if test="${showStartBrowsing eq 'true'}">
						<dsp:form id="startBrowseForm" method="get" action="/store/giftregistry/startBrowsing.jsp"> 
							<dsp:input bean="GiftRegistryFormHandler.registryId" type ='hidden' value="${registryId}"  id= "registryId" name="registryId"/> 										
							<bbbt:textArea key="txt_buyoff_start_browsing" language="${pageContext.request.locale.language}"/>
						</dsp:form>
					</c:if>  
					<c:if test="${eventTypeCode ne 'BA1' }">
						<div class="clearfix marTop_5">
							<div class="grid_2 suffix_1 alpha"> 
								<bbbt:textArea key="txt_regitem_store_image" language="${pageContext.request.locale.language}" />
							</div>
							<div class="grid_9 alpha"> 
							<bbbt:textArea key="txt_mng_regitem_chkfirst" language="${pageContext.request.locale.language}" />								
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
