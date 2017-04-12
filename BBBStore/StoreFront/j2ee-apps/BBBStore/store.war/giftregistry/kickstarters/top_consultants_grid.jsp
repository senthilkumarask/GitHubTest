<dsp:page>
<dsp:importbean bean="/com/bbb/kickstarters/droplet/TopConsultantsDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
 <c:set var="kickStartersCacheTimeout"><bbbc:config key="kickStartersCacheTimeout" configName="HTMLCacheKeys" /></c:set>
   <dsp:droplet name="/atg/dynamo/droplet/Cache">
		                  <dsp:param name="key" value="TopConsultantGrid_${currentSiteId}_${param.eventType}_${isTransient}" />
		                  <dsp:param name="cacheCheckSeconds" value="${kickStartersCacheTimeout}"/>
		                     <dsp:oparam name="output">
	<dsp:droplet name="TopConsultantsDroplet">
		<dsp:param name="registryType" value="${param.eventType}" />
		<dsp:param name="site_id" value="${currentSiteId}" />
		<dsp:param name="isTransient" value="${isTransient}" />
		<dsp:oparam name="output">
		<dsp:getvalueof var="kickStarterDataItemsList" param="kickStarterDataItemsList" />
		</dsp:oparam>
	</dsp:droplet>
	</dsp:oparam>
	</dsp:droplet>
			<dsp:droplet name="ForEach">	
				<dsp:param name="array" value="${kickStarterDataItemsList}" />                                            
           		<dsp:param name="elementName" value="kickStarterDataItem" />
           		
           		<dsp:oparam name="empty">
				    <%-- <bbbl:label key="err_no_consultants_found" language="${pageContext.request.locale.language}" /> --%>
				</dsp:oparam>
           		
           		<dsp:oparam name="outputStart">

					<div id="topConsultantsSection" class="kickstarterSection grid_12">
           		
						<div class="kickstarterSectionHeader grid_12">
							<div class="grid_5 alpha ">           		
			           			<%--<h2><bbbl:label key="lbl_top_consultants_picks" language="${pageContext.request.locale.language}" /></h2>--%>
			           			<h2><bbbt:textArea key="txt_top_consultants_grid_header" language ="${pageContext.request.locale.language}"/></h2>
		           			</div>
		           			<div class="grid_7 omega">
			           			<p>
			           				<bbbt:textArea key="txt_top_consultants_grid_description" language ="${pageContext.request.locale.language}"/>				           			
			           			</p>
		           			</div>
	           			</div>
           		
				    <div class="topConsultantRow">
						<ul class="topConsultantsList">
				</dsp:oparam>
           		
           		<dsp:oparam name="output">
	           		<dsp:getvalueof var="kickStarterItem" param="kickStarterDataItem"/>
	           		<dsp:getvalueof var="imagePath" param="kickStarterDataItem.imageUrl"/>
	           		<dsp:getvalueof var="id" param="kickStarterDataItem.id"/>
	           		<dsp:getvalueof var="name" param="kickStarterDataItem.heading1"/>
	           		<dsp:getvalueof var="shortDescription" param="kickStarterDataItem.heading2"/>
	           		<dsp:getvalueof var="longDescription" param="kickStarterDataItem.description"/>
           			
           			<dsp:getvalueof var="totalSize"  param="size"/>
           			<dsp:getvalueof var="currentCount" param="count"/>
           			<dsp:getvalueof var="currentIndex" param="index"/>

           			<c:set var="imagePath" value="${imagePath}?wid=209&hei=209" />
           			
           			

					<%-- 4 items per row, close the list and start a new one --%>           		           		
           		    <c:if test="${currentIndex % 4 == 0 && currentIndex > 0}">
           		    	</ul>	
					</div>
					<div class="topConsultantRow">
						<ul class="topConsultantsList">
           		    </c:if>
           		     
           		           		
					<li class="topConsultantItem  ${currentCount % 4 == 0 ? "last" : ""}">						 
						
						<c:set var="altViewCollection"><bbbl:label key="lbl_view_collection_alt_msg" language="${pageContext.request.locale.language}" /></c:set>
						<c:choose>
							<c:when test="${not empty param.eventType && not empty param.registryId }">
								<a href="${contextPath}/topconsultant/${id}/${param.registryId}/${param.eventType}" title="${name}" ><img class="topConsultantImage" src="${imagePath}" alt="${name}-View Collection" width="209" height="209" ></a>
							</c:when>
							<c:when test="${not empty param.eventType && empty param.registryId }">
								<a href="${contextPath}/topconsultant/${id}/${param.eventType}" title="${name}" ><img class="topConsultantImage" src="${imagePath}" alt="${name}-View Collection" width="209" height="209" ></a>
							</c:when>
							<c:when test="${empty param.eventType && not empty param.registryId }">
								<a href="${contextPath}/topconsultant/${id}/${param.registryId}" title="${name}" ><img class="topConsultantImage" src="${imagePath}" alt="${name}-View Collection"width="209" height="209" ></a>
							</c:when>
							<c:otherwise>
								<a href="${contextPath}/topconsultant/${id}" title="${name}" ><img class="topConsultantImage" src="${imagePath}" alt="${name}-View Collection" width="209" height="209" ></a>
							</c:otherwise>
						</c:choose>
													
						<h5 class="topConsultantName">${name}</h5>
						<span class="consultantStyle">${shortDescription}</span>					
						<span class="consultantDescription">
							<c:choose>
								<c:when test="${fn:length(longDescription)>110}">
							       ${fn:substring(longDescription, 0, 110)}&hellip;
							    </c:when>						    
							    <c:otherwise>
							        ${longDescription}
							    </c:otherwise>
							</c:choose>
						</span>
						
						<div class="button button_active viewPicksButton">
							<c:set var="altViewCollection"><bbbl:label key="lbl_view_collection_alt_msg" language="${pageContext.request.locale.language}" /></c:set>
							<c:choose>
							<c:when test="${not empty param.eventType && not empty param.registryId }">
							<a href="${contextPath}/topconsultant/${id}/${param.registryId}/${param.eventType}" title="${altViewCollection}" ><bbbl:label key="lbl_view_collection_button" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:when test="${not empty param.eventType && empty param.registryId }">
							<a href="${contextPath}/topconsultant/${id}/${param.eventType}" title="${altViewCollection}" ><bbbl:label key="lbl_view_collection_button" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:when test="${empty param.eventType && not empty param.registryId }">
							<a href="${contextPath}/topconsultant/${id}/${param.registryId}" title="${altViewCollection}" ><bbbl:label key="lbl_view_collection_button" language="${pageContext.request.locale.language}" /></a>
							</c:when>
							<c:otherwise>
							<a href="${contextPath}/topconsultant/${id}" title="${altViewCollection}" ><bbbl:label key="lbl_view_collection_button" language="${pageContext.request.locale.language}" /></a>
							</c:otherwise>
							</c:choose>
						</div>
					</li>
					
           		</dsp:oparam>
           		
           		<dsp:oparam name="outputEnd">
			           	</ul>	
					</div>
					
					<div class="clearfix"></div>
					
				</div>
				</dsp:oparam>
           	</dsp:droplet>
		
</dsp:page>