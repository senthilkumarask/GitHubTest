<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<dsp:page>	
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
 <dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id"/>
<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="eventTypeCode" param="eventTypeCode"/>

		<div class="grid_12 clearfix marTop_20 marBottom_20">
					<div id = "giftViewSortingControlsDropdown" class="grid_7 alpha clearfix space sorting giftViewSortingControls hidden">
					
						<c:set var="showMeCss" value="style='width:53px;'" />
						<c:choose>
							<%-- ALL --%>
							<c:when test="${(empty param.view) || param.view ==1}">
								<c:set var="showMeCss" value="style='width:53px;'" />  
							</c:when>
							<%-- purchased --%>
							<c:when test="${(not empty param.view) && param.view ==3}">
								<c:set var="showMeCss" value="style='width:115px;'" />  
							</c:when>
							<%-- remaining --%>
							<c:when test="${(empty param.view) || param.view ==2}">
								<c:set var="showMeCss" value="style='width:115px;'" />  
							</c:when>
						</c:choose>
					
						<label for="filterRegistryView"><bbbl:label key='lbl_mng_regitem_view' language="${pageContext.request.locale.language}" /></label>						 
						<select class="sorting" id="filterRegistryView" aria-label="Show me the purchased or remaining products from this registry. Your page will be refreshed once you have selected an option" name="filterRegistryView" ${showMeCss}>
							<option value="1" <c:if test="${(empty param.view) || param.view ==1}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></option>
						 	<option value="3" <c:if test="${(not empty param.view) && param.view ==3}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_purchased_sort_owner' language="${pageContext.request.locale.language}" /></option>								  
							<option value="2" <c:if test="${(not empty param.view) && param.view ==2}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></option>
						<select>
						 
						 
						<c:set var="sortCss" value="style='width:105px;'" />
						
						<label for="sortRegistryBy"><bbbl:label key='lbl_mng_regitem_sortby' language="${pageContext.request.locale.language}" /></label>
						<select  id="sortRegistryBy" class="sorting" aria-label="Sort the registry by category or price. Your page will be refreshed once you have selected an option" name="sortRegistryBy" ${sortCss }>
							<option value="1" <c:if test="${(empty param.sortSeq) || param.sortSeq ==1}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /></option>
							<option value="2" <c:if test="${(not empty param.sortSeq) && param.sortSeq ==2}">selected="selected"</c:if>><bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></option>								  
						</select>
					</div>
					
				</div>
						
<%-- Copy Registry changes start --%>
				<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
				   <c:if test="${param.registryId!=sessionRegId}">
		      	
		      		 
		      		<dsp:droplet name="AddItemToGiftRegistryDroplet">
						<dsp:param name="siteId" value="${currentSiteId}"/>
					</dsp:droplet>
					
					
					
		    		<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>		    		
		    		<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
					<dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
		    	
		    			<c:if test="${!isTransient && sizeValue>=1}">		  			
                 		<div id="copyRegistrySection" class="kickstarterSection grid_12 noMarBot noMarTop hidden">
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
				<c:import url="/selfservice/find_in_store.jsp" >
					<c:param name="enableStoreSelection" value="true"/>
				</c:import>
	
	<c:import url="/_includes/modules/change_store_form.jsp" >
		<c:param name="action" value="${contextPath}${finalUrl}"/>
	</c:import>
				<%-- Copy Registry changes end --%>
            <dsp:droplet name="/atg/dynamo/droplet/Cache">
              <dsp:param name="key" value="RegistryC1Category_${eventTypeCode}_${currentSiteId}" />
                <dsp:oparam name="output">
                 <dsp:include page="/giftregistry/frags/registry_items_guest_category_accordian.jsp">
                 		<dsp:param name="eventTypeCode" value="${eventTypeCode}" />
				</dsp:include>
              </dsp:oparam>
          </dsp:droplet>
          <div id = "outOfStockContainer"></div>
          <dsp:form id="startBrowseForm" method="get" action="/store/giftregistry/startBrowsing.jsp" iclass="hidden"> 
							<dsp:input bean="GiftRegistryFormHandler.registryId" type ='hidden' value="${param.registryId}"  id= "registryId" name="registryId"/> 										
							<bbbt:textArea key="txt_buyoff_start_browsing" language="${pageContext.request.locale.language}"/>
						</dsp:form>
	
</dsp:page>          