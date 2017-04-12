<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>
<compress:html removeComments="false">
<dsp:page>
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="currentSiteId" param="currentSiteId"/>
<dsp:importbean bean="/com/bbb/redirectURLs/CategoryRedirectURLLoader"/>
<dsp:getvalueof var="categoryRedirectURLMap" bean="CategoryRedirectURLLoader.categoryRedirectURLMap"/>

<c:set var="rootCatValue">
		<bbbc:config key="${currentSiteId}RootCategory" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BedBathUSSite">
	<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BuyBuyBabySite">
	<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BedBathCanadaSite">
	<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
 <c:choose>
     <c:when test="${currentSiteId eq BedBathCanadaSite}">
	  
     <c:set var="clearanceCategory" scope="request">
		<bbbc:config key="BedBathCanada_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="NodeStylingON" scope="request"><tpsw:switch tagName="NodeStyling_ca"/></c:set>
	</c:when>
	<c:when test="${currentSiteId eq BedBathUSSite}">
	
     <c:set var="clearanceCategory" scope="request">
		<bbbc:config key="BedBathUS_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="NodeStylingON" scope="request"><tpsw:switch tagName="NodeStyling_us"/></c:set>
	</c:when>
	<c:when test="${currentSiteId eq BuyBuyBabySite}">
	
     <c:set var="clearanceCategory" scope="request">
		<bbbc:config key="BuyBuyBaby_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="NodeStylingON" scope="request"><tpsw:switch tagName="NodeStyling_baby"/></c:set>
	</c:when>
</c:choose>

<dsp:droplet name="SearchDroplet">
         <dsp:param name="CatalogId" value="0"/>
         <dsp:param name="CatalogRefId" value="10000"/>
         <dsp:param name="isHeader" value="Y"/>
         <dsp:oparam name="output">
			<c:set var="GiftImageL1Category">
			<bbbc:config key="GiftImageL1Category" configName="ContentCatalogKeys" />
			</c:set>
			<c:set var="GiftImageL1CategoryList" value="${fn:split(GiftImageL1Category, ';')}" />
			<c:if test="${NodeStylingON}">
            <%-- Get all special nodes for which we have different styels --%>
           	<c:set var="SpecialL1Nodes">
			<bbbc:config key="SpecialL1Nodes" configName="ContentCatalogKeys" />
			</c:set>
			<c:set var="SpecialL1NodesList" value="${fn:split(SpecialL1Nodes, ';')}" />
			</c:if>
			<c:set var="babyCAL1Cat"><bbbc:config key="BabyCanada_L1_Category" configName="ContentCatalogKeys" /></c:set>
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
                     <dsp:param param="browseSearchVO.facets" name="array" />
                      <dsp:oparam name="output">
                          <dsp:getvalueof var="facetName" param="element.name" />
                          <c:if test="${facetName == 'DEPARTMENT'}">
                              <c:set var="isFirst" value="${true}" />
                              <dsp:getvalueof var="refinementVO" param="element.facetRefinement" scope="page"/>
                              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                  <dsp:param param="element.facetRefinement" name="array" />
                                  <dsp:getvalueof var="counter" param="count" scope="page"/>
                                  <dsp:getvalueof var="size" param="size"/>
                                  <dsp:oparam name="output">
                                      <dsp:getvalueof var="query" param="element.query"/>
                                      <dsp:getvalueof var="catName" param="element.name"/>
                                      <dsp:getvalueof var="categoryId" param="element.catalogId"/>
                                      <c:set var="IsGiftImageCat" value="false"/>
									  <c:forEach var="GiftImageCat" items="${GiftImageL1CategoryList}">
										  <c:if test="${GiftImageCat eq categoryId}">
										   <c:set var="IsGiftImageCat" value="true"/>
										  </c:if>
									  </c:forEach>
                                      <c:set var="customMenuItem" value="" />

										<c:if test="${NodeStylingON}">
										<%-- Find style class if this is special node --%>
										<c:forEach var="SpecialNode" items="${SpecialL1NodesList}">
												<c:if test="${SpecialNode eq categoryId}">
													<c:set var="customMenuItem">
														<bbbc:config key="${categoryId}StyleClass" configName="ContentCatalogKeys" />
													</c:set>
											   </c:if>
										</c:forEach>
										</c:if>
								<dsp:getvalueof var="sourceCategoryId" param="element.catalogId" />

								<c:if test="${not empty categoryRedirectURLMap and categoryRedirectURLMap.containsKey(sourceCategoryId)}">
									<c:set var="doItemLookUp" value="${false}" />
								</c:if>
                          <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                              <dsp:param name="id" param="element.catalogId" />
                              <dsp:param name="doItemLookUp" value="${doItemLookUp}"/>
                              <dsp:param name="itemDescriptorName" value="category" />
                              <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                               <dsp:oparam name="output">
                               	<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
				            	<li class="${customMenuItem} <c:if test="${IsGiftImageCat}">iconGiftBG</c:if>" data-clicked="false" data-panel="shopLinkPanelCat_${categoryId}">
							 	<c:if test="${IsGiftImageCat}">
								<span class="icon icon-gift" aria-hidden="true"></span>
                                         <span class="icon-text">Gift</span>
								</c:if>
								<c:choose>
									<c:when test="${categoryId eq '22904'}">
										<a class="NavBaby" href="${contextPath}${finalUrl}"
											data-contentid="tabData_${categoryId}" class=""> <dsp:valueof
												param="element.name" /></a>
									</c:when>
									<c:otherwise>
										<a href="${contextPath}${finalUrl}"
											data-contentid="tabData_${categoryId}" class=""> <dsp:valueof
												param="element.name" /><span class="txtOffScreen">&nbsp;opens a flyout</span></a>
									</c:otherwise>
								</c:choose>

			                      <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
		                                 <dsp:param name="id" param="element.catalogId" />
		                                 <dsp:param name="itemDescriptorName" value="category" />
		                                 <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
			                                 <dsp:oparam name="output">
			                                 	<dsp:getvalueof var="finalUrl" param="url" />
						                        <dsp:include page="/_includes/header/elements/menu_contents.jsp">
													<dsp:param name="CatalogId" value="${categoryId}" />
													<dsp:param name="catalogRefId" value="${categoryId}" />
													<dsp:param name="rootCategory" value="${rootCatValue}"/>
													<dsp:param name="catName" value="${catName}"/>
													<dsp:param name="CategoryURL" value="${finalUrl}"/>
												</dsp:include>
												  </dsp:oparam>
			 						 </dsp:droplet>
							  </li>
			  </dsp:oparam>
			  </dsp:droplet>
				  </dsp:oparam>
				  </dsp:droplet>
				  <li><hr></li>
				  <%-- Removed the code to fetch the below the line L1 categories and moved the content to text area. --%>
			      <bbbt:textArea key="txt_globalnav_flyout_content" language="${pageContext.request.locale.language}" />
			    				  </c:if>
				  </dsp:oparam>
				  </dsp:droplet>
				  </dsp:oparam>
				  </dsp:droplet>
</dsp:page>
</compress:html>
