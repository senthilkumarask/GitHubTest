	<%-- This page renders canonical tag --%>
	<dsp:page>
		 <dsp:importbean bean="/atg/multisite/Site"/>
		
		 <dsp:getvalueof var="categoryId" param="categoryId" />
		 <dsp:getvalueof var="productId" param="productId" />
		 <dsp:getvalueof var="pageName" param="pageName" />
		 <dsp:getvalueof var="guideId" param="guideId" />
		 <dsp:getvalueof var="pagNum" param="pagNum" />
		 <%-- Eval base part of link --%>
		 <dsp:getvalueof var="serverName" vartype="java.lang.String" bean="/atg/dynamo/Configuration.siteHttpServerName" />
		 <dsp:getvalueof var="serverPort" vartype="java.lang.String" bean="/atg/dynamo/Configuration.siteHttpServerPort" />
		 <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="${pageContext.request.scheme}://${serverName}:${serverPort}" />
		 <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath" />
		 <dsp:getvalueof var="requestPath" vartype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
		 <dsp:getvalueof var="httpLink" vartype="java.lang.String" bean="Site.productionURL" />
		 <c:set var="domainName"><bbbc:config key="requestDomainName" configName="MobileWebConfig" /></c:set> 
         <c:set var="media_mobile_value"><bbbc:config key="media_value_mobile" configName="HTTPConnectionAttributes" /></c:set>
		<c:choose>
		<c:when test="${empty httpLink}">
			<dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
			</c:when>
		<c:otherwise>
			<c:if test="${not fn:endsWith(httpLink, contextPath)}">
				<dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpLink}${contextPath}" />
			</c:if>
		  </c:otherwise>
		</c:choose>
	 
		<c:set var="http" value="//"/>
		<c:choose>
			<c:when test="${fn:containsIgnoreCase(httpLink, http)}">
			</c:when>
			<c:otherwise>
				<dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${http}${httpLink}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
		  <c:when test="${not empty categoryId && empty productId}">
			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
			  <dsp:param name="id" param="categoryId" />
			  <dsp:param name="itemDescriptorName" value="category" />
			  <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
			  <dsp:oparam name="output">
			  <%-- R2.2 Story - SEO Friendly URL changes --%>
				<c:if test="${not empty currentPage && currentPage eq 1 && pageName ne 'CategoryLandingDetails'}">
				<dsp:getvalueof var="pageUrl" param="url" vartype="java.lang.String" />
	      		<c:choose>
	  				<c:when test="${isFilterApplied eq 'false'}">
	      				<link rel="canonical" href="${http}${domainName}${contextPath}${seoUrl}" />
	      				<%-- Placed the alternate tags here as the requirement is to have canonical URL as alternate URL --%>
	      				<c:if test="${currentSiteId eq 'TBS_BedBathUS'}">
	      					<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}${seoUrl}"/>
	      				</c:if>
	      				<c:if test="${currentSiteId eq 'TBS_BedBathCanada'}">
	      					<link rel="alternate" hreflang="en-ca" href="${http}${domainName}${contextPath}${seoUrl}"/>
	      				</c:if>
	      			</c:when>
	      			<c:otherwise>
	      				<link rel="canonical" href="${http}${domainName}${contextPath}${seoUrl}${filterApplied}" />
	      				<%-- Placed the alternate tags here as the requirement is to have canonical URL as alternate URL --%>
	      				<c:if test="${currentSiteId eq 'TBS_BedBathUS'}">
	      					<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}${seoUrl}${filterApplied}"/>
	      				</c:if>
	      				<c:if test="${currentSiteId eq 'TBS_BedBathCanada'}">
	      					<link rel="alternate" hreflang="en-ca" href="${http}${domainName}${contextPath}${seoUrl}${filterApplied}"/>
	      				</c:if>
	      			</c:otherwise>
	      		</c:choose>
	      	    </c:if>
				
				<%--Added for Story 79 B |@psin52 --%>
				<c:if test="${pageName eq 'CategoryLandingDetails'}">
				<dsp:getvalueof var="pageUrl" param="url" vartype="java.lang.String" />
				<c:choose>
						<c:when test="${isFilterApplied eq 'false'}">
							<link rel="canonical" href="${http}${domainName}${contextPath}${pageUrl}" />
							<%-- Placed the alternate tags here as the requirement is to have canonical URL as alternate URL --%>
		      				<c:if test="${currentSiteId eq 'TBS_BedBathUS'}">
		      					<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}${pageUrl}"/>
		      				</c:if>
		      				<c:if test="${currentSiteId eq 'TBS_BedBathCanada'}">
		      					<link rel="alternate" hreflang="en-ca" href="${http}${domainName}${contextPath}${pageUrl}"/>
		      				</c:if>
							</c:when>
						<c:otherwise>
							<link rel="canonical" href="${http}${domainName}${contextPath}${pageUrl}${filterApplied}" />
							<%-- Placed the alternate tags here as the requirement is to have canonical URL as alternate URL --%>
		      				<c:if test="${currentSiteId eq 'TBS_BedBathUS'}">
		      					<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}${pageUrl}${filterApplied}"/>
		      				</c:if>
		      				<c:if test="${currentSiteId eq 'TBS_BedBathCanada'}">
		      					<link rel="alternate" hreflang="en-ca" href="${http}${domainName}${contextPath}${pageUrl}${filterApplied}"/>
		      				</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>
			  </dsp:oparam>
			</dsp:droplet>
			<c:set var="fbURL" value="${httpLink}${pageUrl}" scope="request" />
			<c:set var="googleURL" value="${httpLink}${pageUrl}" scope="request" />
			<c:set var="postURL" value="${pageUrl}"/>
		  </c:when>
		  <c:when test="${not empty productId}">
			 <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
			   <dsp:param name="id" param="productId" />
			   <dsp:param name="itemDescriptorName" value="product" />
			   <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
			   <dsp:oparam name="output">
				 <dsp:getvalueof var="pageUrl" param="url" vartype="java.lang.String" />
				 <link rel="canonical" href="${httpLink}${pageUrl}" />
			   </dsp:oparam>
			 </dsp:droplet>
			 <c:set var="fbURL" value="${httpLink}${pageUrl}" scope="request" />
			 <c:set var="googleURL" value="${httpLink}${pageUrl}" scope="request" />
			 <c:set var="postURL" value="${pageUrl}"/>
		   </c:when>
		   <%-- R2.2 Story - SEO Friendly URL changes : start--%>
		   <c:when test="${pageTypeForCanonicalURL eq 'Search'}">
				<c:if test="${not empty currentPage && currentPage eq 1}">
					<c:choose>
						<c:when test="${isFilterApplied eq 'false'}">
							<link rel="canonical" href="${http}${domainName}${contextPath}/s/${searchTerm}" />
						</c:when>
						<c:otherwise>
							<link rel="canonical" href="${http}${domainName}${contextPath}/s/${searchTerm}/${filterApplied}" />
						</c:otherwise>
					</c:choose>	
				</c:if>
		   </c:when>
		   
		   <c:when test="${pageTypeForCanonicalURL eq 'Brand'}">
				<c:if test="${not empty currentPage && currentPage eq 1}">
					<c:choose>
						<c:when test="${isFilterApplied eq 'false'}">
							<link rel="canonical" href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}" />
						</c:when>
						<c:otherwise>
							<link rel="canonical" href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}/${filterApplied}" />
						</c:otherwise>
					</c:choose>
				</c:if>
		   </c:when>
		   <%-- R2.2 Story - SEO Friendly URL changes : End --%>
		   <c:when test="${requestPath eq '/tbs/cms/homepage.jsp'}">
			 <c:if test="${fn:containsIgnoreCase(httpLink, contextPath)}">
				<c:set var="httpLink" value="${fn:replace(httpLink, contextPath,'')}" />
			 </c:if>
			 <link rel="canonical" href="${httpLink}"/>
			 <c:set var="fbURL" value="${httpLink}" scope="request" />
			 <c:set var="googleURL" value="${httpLink}" scope="request" />
			 <c:set var="postURL" value="${pageUrl}"/>
			</c:when>
		<c:when test="${requestPath eq '/tbs/cms/guide_detail.jsp' && not empty guideId}">
		
		 <c:set var="uppercaseGuideID" value="${fn:toUpperCase(guideId)}"/>
		 <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
			<dsp:param name="id" value="${uppercaseGuideID}" />
			<dsp:param name="itemDescriptorName" value="guides" />
			<dsp:param name="repositoryName" value="/com/bbb/cms/repository/GuidesTemplate" />
			<dsp:oparam name="output">
			  <dsp:getvalueof var="pageUrl" param="url" vartype="java.lang.String" />
			  <c:set var="googleURL" value="${httpLink}${pageUrl}" scope="request" />
			  <c:set var="postURL" value="${pageUrl}"/>
			  <link rel="canonical" href="${httpLink}${pageUrl}" />
			</dsp:oparam>
		  </dsp:droplet>
		</c:when>
		   <c:otherwise>
			 <c:if test="${not empty requestScope.originalUrl}">
		 <link rel="canonical" href="${httpLink}${requestScope.originalUrl}"/>
			  <c:set var="fbURL" value="${httpLink}${requestScope.originalUrl}" scope="request" />
			  <c:set var="googleURL" value="${httpLink}${requestScope.originalUrl}" scope="request" />
			  <c:set var="postURL" value="${requestScope.originalUrl}"/>
			 </c:if>
		   </c:otherwise>
		</c:choose>
		
		<%-- start scope 79 R2.1.1 --%>
		<%--Updated for Story 79 B |@psin52 --%>
		<c:if test="${pageName ne 'CategoryLandingDetails' && pageName ne 'SubCategoryDetails'}">
		   <c:choose>
			<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
				<c:set var="presentDomian"><bbbc:config key="referer_url_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
				<c:set var="alternateDomain"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set> 
				<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set>
				<c:set var="defaultURL"></c:set>
				<c:set var="alternateURL"></c:set>
				<c:if test="${requestPath eq '/tbs/cms/homepage.jsp'}">
					 <c:set var="googleURL"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
					 <c:set var="defaultURL"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set> 
					 <c:set var="contextPath" value=""/>
					 <c:set var="alternateURL">${presentDomian}${contextPath}${pageUrl}</c:set>
				</c:if>
				
				<c:if test="${fn:containsIgnoreCase(googleURL,'/tbs/selfservice/CanadaStoreLocator')}">
					<c:set var="postURL">/selfservice/FindStore</c:set>
					<c:set var="defaultURL">${alternateDomain}${contextPath}${postURL}</c:set>
					<c:set var="alternateURL">${googleURL}</c:set>
				</c:if>
				
				<c:if test="${empty googleURL}">
					<c:set var="postURL">${requestPath}</c:set>
				</c:if>
				
				<c:if test="${empty defaultURL and empty alternateURL}">
					<c:set var="defaultURL">${alternateDomain}${contextPath}${pageUrl}</c:set>
					<c:set var="alternateURL">${presentDomian}${contextPath}${pageUrl}</c:set>
				</c:if>
				
				<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
				<link rel="alternate" hreflang="x-default" href="${defaultURL}"/>
				<link rel="alternate" hreflang="en-ca" href="${alternateURL}"/>
				<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}"/>
			</c:when>
			<%--Added for Story 79 B |@psin52 --%>
			<c:when test="${currentSiteId eq 'TBS_BuyBuyBaby'}">
				<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BuyBuyBaby" configName="HTTPConnectionAttributes" /></c:set>
				<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
				<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}"/>
			</c:when>
			
			<c:otherwise>
				<c:set var="alternateDomain"><bbbc:config key="referer_url_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
				<%--Added for Story 79 B |@psin52 --%>
				<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
				
				<c:set var="presentDomain"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
				<c:if test="${requestPath eq '/tbs/cms/homepage.jsp'}">
					<c:set var="googleURL"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set> 		
					<c:set var="contextPath" value=""/>	    
				</c:if>
				
				<c:if test="${fn:containsIgnoreCase(googleURL,'store/selfservice/FindStore')}">
					<c:set var="postURL">/selfservice/CanadaStoreLocator</c:set>
				</c:if>
				<c:if test="${empty googleURL}">
					<c:set var="googleURL">${presentDomain}${requestPath}</c:set>
					<c:set var="postURL">${requestPath}</c:set>
					<c:set var="contextPath"></c:set>
				</c:if>
				<c:set var="alternateURL">${alternateDomain}${contextPath}${postURL}</c:set>
				<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
				<c:choose>
					<c:when test="${fn:containsIgnoreCase(alternateURL,'account/favoritestore.jsp')}">
						<link rel="alternate" hreflang="x-default" href="${googleURL}"/>
					</c:when>
					<c:otherwise>
					<c:if test="${pageName ne 'Search'}">
						<link rel="alternate" hreflang="x-default" href="${googleURL}"/>
						<link rel="alternate" hreflang="en-ca" href="${alternateURL}"/>
						<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}"/>
					</c:if>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
			</c:choose>	
		</c:if>
		<%--Start: Added for Story 79 C |@ngup50 --%>
		<c:if test="${pageName eq 'CategoryLandingDetails' or pageName eq 'SubCategoryDetails'}">
			<c:choose>
				<c:when test="${currentSiteId eq 'TBS_BedBathUS'}">
					<c:set var="alternateDomain"><bbbc:config key="referer_url_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
					<c:set var="presentDomain"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
										
					<c:if test="${empty googleURL}">
						<c:set var="googleURL">${presentDomain}${requestPath}</c:set>
						<c:set var="postURL">${requestPath}</c:set>
						<c:set var="contextPath"></c:set>
					</c:if>
					
					<dsp:droplet name="/com/bbb/commerce/catalog/droplet/BBBUSCanadaCatMappingDroplet">
						<dsp:param name="categoryId" param="categoryId" />
						<dsp:param name="postURL" value="${postURL}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="returnPostURL" param="returnPostURL" />
							<c:set var="returnPostURL">${returnPostURL}</c:set>
						</dsp:oparam>
					</dsp:droplet>
					
					<c:set var="alternateURL">${alternateDomain}${contextPath}${returnPostURL}</c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
					<c:if test="${returnPostURL ne ''}">
						<c:if test="${(pageName eq 'SubCategoryDetails' && currentPage eq 1) || pageName eq 'CategoryLandingDetails'}">
							<link rel="alternate" hreflang="en-ca" href="${alternateURL}"/>
						</c:if>
					</c:if>
					<c:if test="${pageName eq 'CategoryLandingDetails'}">
						<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}"/>
					</c:if>
				</c:when>
				<%--Added for Story 79 B |@psin52 --%>
				<c:when test="${currentSiteId eq 'TBS_BuyBuyBaby'}">
					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BuyBuyBaby" configName="HTTPConnectionAttributes" /></c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
					<c:if test="${pageName eq 'CategoryLandingDetails'}">
						<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}"/>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:set var="alternateDomain"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set> 
					<c:set var="presentDomain"><bbbc:config key="referer_url_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set>
					<c:set var="defaultURL"></c:set>
					<c:set var="alternateURL"></c:set>
					
					<dsp:droplet name="/com/bbb/commerce/catalog/droplet/BBBUSCanadaCatMappingDroplet">
						<dsp:param name="categoryId" param="categoryId" />
						<dsp:param name="postURL" value="${pageUrl}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="returnPostURL" param="returnPostURL" />
							<c:set var="returnPostURL">${returnPostURL}</c:set>
						</dsp:oparam>
					</dsp:droplet>
					
					<c:if test="${empty googleURL}">
						<c:set var="postURL">${requestPath}</c:set>
					</c:if>
					
					<c:if test="${empty defaultURL and empty alternateURL}">
						<c:set var="defaultURL">${alternateDomain}${contextPath}${returnPostURL}</c:set>
						<c:set var="alternateURL">${presentDomain}${contextPath}${pageUrl}</c:set>
					</c:if>
					
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
					<c:if test="${returnPostURL ne ''}">
							<c:if test="${(pageName eq 'SubCategoryDetails' && currentPage eq 1) || pageName eq 'CategoryLandingDetails'}">
								<link rel="alternate" hreflang="x-default" href="${defaultURL}"/>
							</c:if>
					</c:if>
					<c:if test="${pageName eq 'CategoryLandingDetails'}">
						<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}"/>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:if>
		<%--End: Added for Story 79 C |@ngup50 --%>
	<%-- stop scope 497 R2.1.1 --%>
	</dsp:page>