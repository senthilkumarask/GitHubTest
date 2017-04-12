<dsp:page>
<dsp:importbean bean="/com/bbb/kickstarters/droplet/ShopTheLookDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>

	

			<dsp:droplet name="ShopTheLookDroplet">
				<dsp:param name="registryType" value="${param.eventType}" />
				<dsp:param name="site_id" value="${fn:replace(currentSiteId, 'TBS_', '')}" />
				<dsp:param name="isTransient" value="${isTransient}" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="kickStarterDataItemsList"	param="kickStarterDataItemsList" />
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="kickStarterDataItemsList" />
						<dsp:param name="elementName" value="kickStarterDataItem" />
						
						<dsp:oparam name="outputStart">
	
							<div id="shopLooksSection" class="kickstarterSection grid_12">
		
							<%-- <h2><bbbl:label key="lbl_shopthelook" language="${pageContext.request.locale.language}" /></h2>--%>
							
							<div class="kickstarterSectionHeader grid_12">
								<div class="grid_5 alpha ">           		
				           			<h2><bbbl:label key="lbl_shop_look_main_header" language="${pageContext.request.locale.language}" /></h2>
			           			</div>
			           			<div class="grid_7 omega">
				           			<p>
				           				<bbbt:textArea key="txt_shop_looks_grid_description" language ="${pageContext.request.locale.language}"/>					           			
				           			</p>
			           			</div>
		           			</div>
							
							
							
							<ul class="shopLooksList small-block-grid-1 medium-block-grid-2">
						</dsp:oparam>
						
						
						<dsp:oparam name="output">
							<dsp:getvalueof var="kickStarterItem" param="kickStarterDataItem" />
							<dsp:getvalueof var="imagePath" param="kickStarterDataItem.imageUrl" />
							<dsp:getvalueof var="id" param="kickStarterDataItem.id" />
							<dsp:getvalueof var="name" param="kickStarterDataItem.heading1" />
							<dsp:getvalueof var="shortDescription"	param="kickStarterDataItem.heading2" />
							<dsp:getvalueof var="longDescription"	param="kickStarterDataItem.description" />

							<dsp:getvalueof var="totalSize" param="size" />
							<dsp:getvalueof var="currentCount" param="count" />
							<dsp:getvalueof var="currentIndex" param="index" />
							
							<%-- looking for a scene7 url, no params or protocol, ie: s7d9.scene7.com/is/image/BedBathandBeyond/flatware --%>
							<c:set var="imagePath" value="${imagePath}?wid=458&hei=210" />
							
							<c:choose>
								<c:when test="${currentIndex % 2 == 0}">
									<li class="shopLooksItem odd">
								</c:when>
								<c:otherwise>
									<li class="shopLooksItem even">
								</c:otherwise>
							</c:choose>
					           		<c:choose>
										<c:when test="${not empty param.eventType && not empty param.registryId }">
										<a href="${contextPath}/shopthislook/${id}/${param.registryId}/${param.eventType}" title="${altViewCollection}" ><img class="shopLooksItemMainImage stretch" src="${imagePath}" width="458" height="210" /></a>
					           			</c:when>
										<c:when test="${not empty param.eventType && empty param.registryId }">
										<a href="${contextPath}/shopthislook/${id}/${param.eventType}" title="${altViewCollection}" ><img class="shopLooksItemMainImage stretch" src="${imagePath}"  width="458" height="210" /></a>
					           			</c:when>
										<c:when test="${empty param.eventType && not empty param.registryId }">
										<a href="${contextPath}/shopthislook/${id}/${param.registryId}" title="${altViewCollection}" ><img class="shopLooksItemMainImage stretch" src="${imagePath}"  width="458" height="210" /></a>
					           			</c:when>
										<c:otherwise>
										<a href="${contextPath}/shopthislook/${id}" title="${altViewCollection}" ><img class="shopLooksItemMainImage stretch" src="${imagePath}"  width="458" height="210" /></a>
					           			</c:otherwise>
									</c:choose>
		           					</li>
						</dsp:oparam>
						
						<dsp:oparam name="outputEnd">
								</ul>
							</div>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
				
</dsp:page>