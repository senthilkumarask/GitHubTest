	<%-- This page renders canonical tag --%>
	<dsp:page>
		 <dsp:importbean bean="/atg/multisite/Site"/>
		<dsp:getvalueof var="PageType" param="PageType"/>
		 <dsp:getvalueof var="categoryId" param="categoryId" />
		 <dsp:getvalueof var="productId" param="productId" />
		 <dsp:getvalueof var="pageName" param="pageName" />
		 <dsp:getvalueof var="guideId" param="guideId" />
		 <dsp:getvalueof var="pagNum" param="pagNum" />
		 <dsp:getvalueof var="storeID" param="storeID" />
		 <dsp:getvalueof var="categoryLevel" param="categoryLevel" />
		 <c:set var="storeIdParam" value="" />
		 <c:if test="${not empty storeID && categoryLevel eq 'L2'}">
			<c:set var="storeIdParam" value="store-${storeID}/" />
		 </c:if>
		 <%-- Eval base part of link --%>
		 <dsp:getvalueof var="serverName" vartype="java.lang.String" bean="/atg/dynamo/Configuration.siteHttpServerName" />
		 <dsp:getvalueof var="serverPort" vartype="java.lang.String" bean="/atg/dynamo/Configuration.siteHttpServerPort" />
		 <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="${pageContext.request.scheme}://${serverName}:${serverPort}" />
		 <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath" />
		 <dsp:getvalueof var="requestPath" vartype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
		 <dsp:getvalueof var="httpLink" vartype="java.lang.String" bean="Site.productionURL" />
		 <c:set var="domainName"><bbbc:config key="requestDomainName" configName="MobileWebConfig" /></c:set> 
         <c:set var="media_mobile_value"><bbbc:config key="media_value_mobile" configName="HTTPConnectionAttributes" /></c:set>
         <c:set var="mobileDomainName"><bbbc:config key="alternateDomainName" configName="MobileWebConfig" /></c:set>
         <c:set var="pageSize" value="${currentPage}-${size}" /> 
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
	 
		<c:set var="http" value="https://"/>
		<c:choose>
			<c:when test="${fn:containsIgnoreCase(httpLink, http)}">

			</c:when>
			<c:otherwise>
				<dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${http}${httpLink}" />
			</c:otherwise>
		</c:choose>
		 
		<dsp:getvalueof var="canonicalURL" param="canonicalURL"/>
		<dsp:getvalueof var="alternateURL" param="alternateURL"/>
		<dsp:getvalueof var="canadaAlternateURL" param="canadaAlternateURL"/>
		<dsp:getvalueof var="usAlternateURL" param="usAlternateURL"/>
		   <c:set var="narrowDownURL" value=""></c:set>
		<c:if test="${not empty param.narrowDown}">
                 <c:set var="narrowDownURL" value="${param.narrowDown}/"></c:set>
                 <c:set var="narrowDownURL1" value="/${param.narrowDown}"></c:set>
                 
           </c:if>
	
	<c:choose>
		<c:when test="${not empty canonicalURL}">
			<link rel="canonical" href="${http}${domainName}${contextPath}${canonicalURL}${narrowDownURL}"/>
			  <c:if test="${not empty alternateURL}">
					<link rel="alternate" media="only screen and (max-width: 640px)"  href ="https://${mobileDomainName}${alternateURL}${narrowDownURL}" />

			 </c:if>
			 <c:set var="canadaDomain"><bbbc:config key="referer_url_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
			 <c:set var="usDomain"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
			  
			<c:choose>
				<c:when test="${currentSiteId eq 'BedBathCanada'}">
					<c:choose>
						<c:when test="${not empty canadaAlternateURL}">  
							<link rel="alternate" hreflang="en-ca" href="${canadaDomain}${contextPath}${canadaAlternateURL}${narrowDownURL}"/> 
						</c:when>
						<c:otherwise>
							<link rel="alternate" hreflang="en-ca" href="${canadaDomain}${contextPath}${canonicalURL}${narrowDownURL}"/> 
						</c:otherwise>
					</c:choose>
					<c:if test="${not empty usAlternateURL}">  
						<link rel="alternate" hreflang="x-default" href="${usDomain}${contextPath}${usAlternateURL}${narrowDownURL}"/> 
					</c:if>
				</c:when>
				<c:otherwise>

						<c:if test="${not empty canadaAlternateURL}">  
							<link rel="alternate" hreflang="en-ca" href="${canadaDomain}${contextPath}${canadaAlternateURL}${narrowDownURL}"/> 
						</c:if>
						<c:choose>
						<c:when test="${not empty usAlternateURL}">  
							<link rel="alternate" hreflang="x-default" href="${usDomain}${contextPath}${usAlternateURL}${narrowDownURL}"/> 
						</c:when>
						<c:otherwise>
							<link rel="alternate" hreflang="x-default" href="${usDomain}${contextPath}${canonicalURL}${narrowDownURL}"/> 
						</c:otherwise>
						</c:choose>
				</c:otherwise>			
			</c:choose>

		</c:when>
		
	<c:otherwise>
		<c:choose>
		  
		  <c:when test="${not empty categoryId && empty productId}">
			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
			  <dsp:param name="id" param="categoryId" />
			  <dsp:param name="itemDescriptorName" value="category" />
			  <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
			  <dsp:oparam name="output">
			  <%-- R2.2 Story - SEO Friendly URL changes --%>
				<c:if test="${not empty currentPage && pageName ne 'CategoryLandingDetails'}">
				<dsp:getvalueof var="pageUrl" param="url" vartype="java.lang.String" />
				
	      		<c:choose>
	  				<c:when test="${isFilterApplied eq 'false' || isSWSFilterApplied}">
	  				
	  				<c:choose>
	  				<c:when test="${currentPage eq 1}">
						<link rel="canonical" href="${http}${domainName}${contextPath}${seoUrl}${narrowDownURL}${storeIdParam}" />
	  				</c:when>
	  				<c:otherwise>
						<link rel="canonical" href="${http}${domainName}${contextPath}${seoUrl}${narrowDownURL}${pageSize}/${storeIdParam}" />
	      			</c:otherwise>
	      			</c:choose>
	      				<%-- BPSI-115 Placed the alternate tags here as the requirement is to have canonical URL as alternate URL --%>
	      				<c:if test="${currentSiteId eq 'BedBathUS'}">
	      					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
	      					<c:if test="${PageType ne 'HomePage'}">
								<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
							</c:if>
							<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}${seoUrl}${narrowDownURL}"/>
	      				</c:if>
	      				<c:if test="${currentSiteId eq 'BedBathCanada'}">
	      					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set>
	      					<c:if test="${PageType ne 'HomePage'}">
								<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
							</c:if>
							<link rel="alternate" hreflang="en-ca" href="${http}${domainName}${contextPath}${seoUrl}${narrowDownURL}"/>
	      				</c:if>
	      				<c:if test="${currentSiteId eq 'BuyBuyBaby'}">
	      					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BuyBuyBaby" configName="HTTPConnectionAttributes" /></c:set>
							<c:if test="${PageType ne 'HomePage'}">
								<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
							</c:if>
	      				</c:if>
	      				<link rel="alternate" media="${media_mobile_value}" href="${alternateDomain_mobile}${seoUrl}${narrowDownURL}"/>
	      			</c:when>
	      			<c:otherwise>
	      			<c:choose>
		  				<c:when test="${currentPage eq 1}">
		  					<link rel="canonical" href="${http}${domainName}${contextPath}${seoUrl}${narrowDownURL}${canonicalFilterApplied}/${storeIdParam}" />
		  				</c:when>
		  				<c:otherwise>
		  					<link rel="canonical" href="${http}${domainName}${contextPath}${seoUrl}${narrowDownURL}${canonicalFilterApplied}/${pageSize}/${storeIdParam}" />
		  				</c:otherwise>
	  				</c:choose>
	      				
	      				<%-- BPSI-115 Placed the alternate tags here as the requirement is to have canonical URL as alternate URL --%>
	      				<c:if test="${currentSiteId eq 'BedBathUS'}">
	      					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
	      					<c:if test="${PageType ne 'HomePage'}">
								<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
							</c:if>
							<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}${seoUrl}${narrowDownURL}${canonicalFilterApplied}"/>
	      				</c:if>
	      				<c:if test="${currentSiteId eq 'BedBathCanada'}">
	      					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set>
	      					<c:if test="${PageType ne 'HomePage'}">
								<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
							</c:if>
							<link rel="alternate" hreflang="en-ca" href="${http}${domainName}${contextPath}${seoUrl}${narrowDownURL}${canonicalFilterApplied}"/>
	      				</c:if>
	      				<c:if test="${currentSiteId eq 'BuyBuyBaby'}">
	      					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BuyBuyBaby" configName="HTTPConnectionAttributes" /></c:set>
							<c:if test="${PageType ne 'HomePage'}">
								<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
							</c:if>
						</c:if>
	      				<link rel="alternate" media="${media_mobile_value}" href="${alternateDomain_mobile}${seoUrl}${narrowDownURL}${canonicalFilterApplied}"/>
	      			</c:otherwise>
	      		</c:choose>
	      	    </c:if>
				
				<%--Added for Story 79 B |@psin52 --%>
				<c:if test="${pageName eq 'CategoryLandingDetails'}">
				<dsp:getvalueof var="pageUrl" param="url" vartype="java.lang.String" />
				<c:choose>
						<c:when test="${isFilterApplied eq 'false'}">
							<link rel="canonical" href="${http}${domainName}${contextPath}${pageUrl}${narrowDownURL}" />
							<%-- Placed the alternate tags here as the requirement is to have canonical URL as alternate URL --%>
		      				<c:if test="${currentSiteId eq 'BedBathUS'}">
		      					<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}${pageUrl}${narrowDownURL}"/>
		      				</c:if>
		      				<c:if test="${currentSiteId eq 'BedBathCanada'}">
		      					<link rel="alternate" hreflang="en-ca" href="${http}${domainName}${contextPath}${pageUrl}${narrowDownURL}"/>
		      				</c:if>
							</c:when>
						<c:otherwise>
						<%--BBBSL-8296 | removing back slash from the end of URL --%>
							<link rel="canonical" href="${http}${domainName}${contextPath}${pageUrl}${narrowDownURL}${canonicalFilterApplied}" />
							<%-- Placed the alternate tags here as the requirement is to have canonical URL as alternate URL --%>
		      				<c:if test="${currentSiteId eq 'BedBathUS'}">
		      					<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}${pageUrl}${narrowDownURL}${canonicalFilterApplied}"/>
		      				</c:if>
		      				<c:if test="${currentSiteId eq 'BedBathCanada'}">
		      					<link rel="alternate" hreflang="en-ca" href="${http}${domainName}${contextPath}${pageUrl}${narrowDownURL}${canonicalFilterApplied}"/>
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
				<c:if test="${not empty currentPage}">
					<c:choose>
						<c:when test="${isFilterApplied eq 'false'}">
						<c:choose>
								<c:when test="${currentPage eq 1}">
									<link rel="canonical" href="${http}${domainName}${contextPath}/s/${searchTerm}${narrowDownURL1}/" />
								</c:when>
								<c:otherwise>
									<link rel="canonical" href="${http}${domainName}${contextPath}/s/${searchTerm}${narrowDownURL1}/${pageSize}/" />
								</c:otherwise>
							</c:choose>
							
						</c:when>
						<c:otherwise>
						<c:choose>
								<c:when test="${currentPage eq 1}">
									<link rel="canonical" href="${http}${domainName}${contextPath}/s/${searchTerm}${narrowDownURL1}/${canonicalFilterApplied}/" />
								</c:when>
								<c:otherwise>
									<link rel="canonical" href="${http}${domainName}${contextPath}/s/${searchTerm}${narrowDownURL1}/${canonicalFilterApplied}/${pageSize}/" />
								</c:otherwise>
							</c:choose>
							
						</c:otherwise>
					</c:choose>	
				</c:if>
		   </c:when>
		   <c:when test="${pageTypeForCanonicalURL eq 'ShopTheLook' or pageTypeForCanonicalURL eq 'TopConsultant' or pageTypeForCanonicalURL eq 'KickStarter'}">
		   	<%--BBBSL-8296 | removing back slash from the end of URL --%>
		    	 <link rel="canonical" href="${http}${domainName}<%= request.getAttribute("javax.servlet.forward.request_uri") %>" />
		     </c:when>
		   <c:when test="${pageTypeForCanonicalURL eq 'Brand'}">
				<c:if test="${not empty currentPage }">
					<c:set var="brandCanonicalWithListView">
						 <bbbc:config key="brandCanonicalWithListView" configName="FlagDrivenFunctions" />
					</c:set> 
					<c:set var="viewList">
					<c:if test="${brandCanonicalWithListView}">
						?view=list
					</c:if>
					</c:set>
					<c:choose>					
						<c:when test="${isFilterApplied eq 'false'}">		
						<c:choose>
							<c:when test="${currentPage eq 1}">
									<link rel="canonical" href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}" />
							</c:when>
							<c:otherwise>
									<link rel="canonical" href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}/${pageSize}/" />
							</c:otherwise>
						</c:choose>				
						
								<%--Added for Story BPSI114--%>
							<c:if test="${currentSiteId eq 'BedBathUS'}">
								<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}"/>	
								<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>								
								<c:if test="${PageType ne 'HomePage'}">
									<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
								</c:if>
								<link rel="alternate" media="${media_mobile_value}" href="${alternateDomain_mobile}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}"/>
							</c:if>
							<c:if test="${currentSiteId eq 'BedBathCanada'}">
								<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set>								
								<c:if test="${PageType ne 'HomePage'}">
									<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
								</c:if>
								<link rel="alternate" media="${media_mobile_value}" href="${alternateDomain_mobile}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}"/>
							</c:if>
							<c:if test="${currentSiteId eq 'BuyBuyBaby'}">								
								<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BuyBuyBaby" configName="HTTPConnectionAttributes" /></c:set>									
								<c:if test="${PageType ne 'HomePage'}">
									<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
								</c:if>
								<link rel="alternate" media="${media_mobile_value}" href="${alternateDomain_mobile}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}"/>
							</c:if>							
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${currentPage eq 1}">
									<link rel="canonical" href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}/${canonicalFilterApplied}" />
								</c:when>
								<c:otherwise>
									<link rel="canonical" href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}/${canonicalFilterApplied}/${pageSize}/" />
								</c:otherwise>
							</c:choose>
							
								<%--Added for Story BPSI114--%>
							<c:if test="${currentSiteId eq 'BedBathUS'}">
								<link rel="alternate" hreflang="x-default" href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}/${canonicalFilterApplied}"/>		
								<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>							
								<c:if test="${PageType ne 'HomePage'}">								
                                	<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
								</c:if>
								
								<link rel="alternate" media="${media_mobile_value}" href="${alternateDomain_mobile}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}/${canonicalFilterApplied}"/>
							</c:if>
							
							<c:if test="${currentSiteId eq 'BedBathCanada'}">
								<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
								<c:if test="${PageType ne 'HomePage'}">								
									<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
								</c:if>
							    <link rel="alternate" media="${media_mobile_value}" href="${alternateDomain_mobile}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}/${canonicalFilterApplied}"/>
							</c:if>
							<c:if test="${currentSiteId eq 'BuyBuyBaby'}">
								<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BuyBuyBaby" configName="HTTPConnectionAttributes" /></c:set>
								<c:if test="${PageType ne 'HomePage'}">								
									<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
								</c:if>
								<link rel="alternate" media="${media_mobile_value}" href="${alternateDomain_mobile}/brand/${brandNameForUrl}/${searchTerm}${narrowDownURL1}/${canonicalFilterApplied}"/>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>
		   </c:when>
		   <%-- R2.2 Story - SEO Friendly URL changes : End --%>
		   <c:when test="${requestPath eq '/store/cms/homepage.jsp'}">
			 <c:if test="${fn:containsIgnoreCase(httpLink, contextPath)}">
				<c:set var="httpLink" value="${fn:replace(httpLink, contextPath,'')}" />
			 </c:if>
			 <link rel="canonical" href="${httpLink}"/>
			 <c:set var="fbURL" value="${httpLink}" scope="request" />
			 <c:set var="googleURL" value="${httpLink}" scope="request" />
			 <c:set var="postURL" value="${pageUrl}"/>
			</c:when>
		<c:when test="${requestPath eq '/store/cms/guide_detail.jsp' && not empty guideId}">
		
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
			<c:when test="${currentSiteId eq 'BedBathCanada'}">	
				<c:set var="presentDomian"><bbbc:config key="referer_url_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
				<c:set var="alternateDomain"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set> 
				<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set>
				<c:if test="${PageType ne 'HomePage'}">
					<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
				</c:if>
				<c:set var="defaultURL"></c:set>
				<c:set var="alternateURL"></c:set>
				<c:if test="${requestPath eq '/store/cms/homepage.jsp'}">
					 <c:set var="googleURL"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
					 <c:set var="defaultURL"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set> 
					 <c:set var="contextPath" value=""/>
					 <c:set var="alternateURL">${presentDomian}${contextPath}${pageUrl}</c:set>
				</c:if>
		
				<c:if test="${fn:containsIgnoreCase(googleURL,'/store/selfservice/CanadaStoreLocator')}">
					<c:set var="postURL">/selfservice/FindStore</c:set>
					<c:set var="mobilepostURL">/storeLocator</c:set>
					<c:set var="defaultURL">${alternateDomain}${contextPath}${postURL}</c:set>
					<c:set var="alternateURL">${googleURL}</c:set>
				</c:if>
				
				<c:if test="${empty googleURL}">
					<c:set var="postURL">${requestPath}</c:set>
				</c:if>
				
				<c:if test="${empty defaultURL and empty alternateURL}">
					<c:set var="defaultURL">${alternateDomain}${contextPath}${postURL}</c:set>
					<c:set var="alternateURL">${presentDomian}${postURL}</c:set>
				</c:if>
				
			<c:choose>
			    <c:when test="${ !empty mobilepostURL}">
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}${mobilepostURL}</c:set>
				</c:when>
				<c:otherwise>
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
				</c:otherwise>
			</c:choose>
				
				<c:if test="${PageType eq 'CollegeLandingPage'}">
					<c:set var="postURL_mobile"><bbbc:config key="canonicalUrl_collegeLanding" configName="MobileWebConfig" /></c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL_mobile}</c:set>
					<c:set var="alternateURL">${presentDomian}${postURL_mobile}</c:set>
				</c:if>
				
				<c:if test="${PageType eq 'RegistryLanding'}">
					<c:set var="postURL_mobile"><bbbc:config key="canonicalUrl_registryLanding" configName="MobileWebConfig" /></c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL_mobile}</c:set>
					<c:set var="alternateURL">${presentDomian}${postURL_mobile}</c:set>
				</c:if>
				
				<c:if test="${PageType eq 'kickstarter'}">
					<c:set var="kickstarter_mobile"><bbbc:config key="canonicalUrl_kickstarter" configName="MobileWebConfig" /></c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}/${kickstarter_mobile}</c:set>
				</c:if>
				<link rel="alternate" hreflang="x-default" href="${defaultURL}/"/>
				<link rel="alternate" hreflang="en-ca" href="${alternateURL}/"/>
				
				<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}/"/>
			</c:when>
			<%--Added for Story 79 B |@psin52 --%>
			<c:when test="${currentSiteId eq 'BuyBuyBaby'}">
				<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BuyBuyBaby" configName="HTTPConnectionAttributes" /></c:set>
				<c:if test="${PageType ne 'HomePage'}">
					<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
				</c:if>
				<c:if test="${fn:containsIgnoreCase(googleURL,'store/selfservice/FindStore')}">
					
					<c:set var="mobilepostURL_bby">${alternateDomain_mobile}/storeLocator</c:set>
				</c:if>
				<c:if test="${fn:containsIgnoreCase(googleURL,'store/selfservice/CanadaStoreLocator')}">
					
					<c:set var="mobilepostURL_bby">${alternateDomain_mobile}/storeLocator</c:set>
				</c:if>
				
				<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
				
				<c:if test="${PageType eq 'RegistryLandingBaby'}">
					<c:set var="postURL_mobile"><bbbc:config key="canonicalUrl_registryLanding" configName="MobileWebConfig" /></c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL_mobile}</c:set>
				</c:if>
				
				<c:if test="${PageType eq 'kickstarter'}">	
					<c:set var="kickstarter_mobile"><bbbc:config key="canonicalUrl_kickstarter" configName="MobileWebConfig" /></c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}/${kickstarter_mobile}</c:set>
				</c:if>
				<c:choose>
			<c:when test="${ !empty mobilepostURL_bby}">
				
					<link rel="alternate" media="${media_mobile_value}" href="${mobilepostURL_bby}/"/>
				</c:when>
				<c:otherwise>
					<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}/"/>
				</c:otherwise>
				
				</c:choose>
			</c:when>
			
			<c:otherwise>
				<c:set var="alternateDomain"><bbbc:config key="referer_url_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
				<%--Added for Story 79 B |@psin52 --%>
				<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
				<c:if test="${PageType ne 'HomePage'}">
					<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
				</c:if>
				<c:set var="presentDomain"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
				<c:if test="${requestPath eq '/store/cms/homepage.jsp'}">
					<c:set var="googleURL"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set> 		
					<c:set var="contextPath" value=""/>	    
				</c:if>
				
				<c:if test="${fn:containsIgnoreCase(googleURL,'store/selfservice/FindStore')}">
					<c:set var="postURL">/selfservice/CanadaStoreLocator</c:set>
					<c:set var="mobilepostURL">/storeLocator</c:set>
				</c:if>
				<c:if test="${empty googleURL}">
					<c:set var="googleURL">${presentDomain}${requestPath}</c:set>
					<c:set var="postURL">${requestPath}</c:set>
					<c:set var="contextPath"></c:set>
				</c:if>
				<c:set var="alternateURL">${alternateDomain}${contextPath}${postURL}</c:set>
		<c:choose>
			<c:when test="${ !empty mobilepostURL}">
				<c:set var="alternateMobileURL">${alternateDomain_mobile}${mobilepostURL}</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
			</c:otherwise>
		</c:choose>
				<c:if test="${PageType eq 'kickstarter'}">
					<c:set var="kickstarter_mobile"><bbbc:config key="canonicalUrl_kickstarter" configName="MobileWebConfig" /></c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}/${kickstarter_mobile}</c:set>
				</c:if>
				
				<c:if test="${PageType eq 'CollegeLandingPage'}">
					<c:set var="postURL_mobile"><bbbc:config key="canonicalUrl_collegeLanding" configName="MobileWebConfig" /></c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL_mobile}</c:set>
					<c:set var="alternateURL">${alternateDomain}${postURL_mobile}</c:set>
				</c:if>
				
				<c:if test="${PageType eq 'RegistryLanding'}">
					<c:set var="postURL_mobile"><bbbc:config key="canonicalUrl_registryLanding" configName="MobileWebConfig" /></c:set>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL_mobile}</c:set>
					<c:set var="alternateURL">${alternateDomain}${postURL_mobile}</c:set>
				</c:if>
				<c:if test="${PageType eq 'StaticPage'}">
					<c:if test="${fn:contains(postURL,'/static')}">
						<c:set var="postURL_mobile">${fn:replace(postURL,'/static','/static/content')}</c:set>
					</c:if>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL_mobile}</c:set>
					<c:set var="alternateURL">${alternateDomain}${postURL_mobile}</c:set>
				</c:if>
				
				<c:if test="${PageType eq 'ContactUs'}">
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}/contactUs</c:set>
				</c:if>
				<c:if test="${PageType eq 'Login'}">
				  <link rel="canonical" href="${googleURL}"/>
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}/loginPage</c:set>
				</c:if>
				<c:if test="${PageType eq 'WalletRegistration'}">
				  <link rel="canonical" href="${googleURL}"/>
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}/couponWalletRegistration</c:set>
				</c:if>
					<c:if test="${fn:containsIgnoreCase(alternateURL,'GiftCardHomePage')}">
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}/static/content/gifts</c:set>
				</c:if>	
				<c:if test="${fn:containsIgnoreCase(alternateURL,'brands')}">
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}/brands</c:set>
				</c:if>	
				<c:if test="${fn:containsIgnoreCase(alternateURL,'MovingSolution')}">
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}/static/content/ShopHerePickup</c:set>
				</c:if>
				<c:if test="${fn:containsIgnoreCase(alternateURL,'TrackOrder')}">
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}/trackOrder</c:set>
				</c:if>
				<c:if test="${fn:containsIgnoreCase(alternateURL,'RegistryAnnouncementCards')}">
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}/registryLanding</c:set>
				</c:if>
				<c:if test="${fn:containsIgnoreCase(alternateURL,'simpleReg_creation_form.jsp')}">
				  <link rel="canonical" href="${googleURL}"/>
				  <c:set var="alternateMobileURL">${alternateDomain_mobile}/registryLanding</c:set>
				</c:if>
											
				<c:choose>
					<c:when test="${fn:containsIgnoreCase(alternateURL,'account/favoritestore.jsp')}">
						<link rel="alternate" hreflang="x-default" href="${googleURL}"/>
					</c:when>
					<c:otherwise>
					<c:if test="${pageName ne 'Search'}">
						<link rel="alternate" hreflang="x-default" href="${googleURL}/"/>
						<link rel="alternate" hreflang="en-ca" href="${alternateURL}/"/>
						<c:choose>
						<c:when test="${ fn:containsIgnoreCase(alternateURL,'RegistryFeatures') || fn:containsIgnoreCase(alternateURL,'RegistryIncentives')|| fn:containsIgnoreCase(alternateURL,'BridalBook')|| fn:containsIgnoreCase(alternateURL,'GuidesAndAdviceLandingPage')|| fn:containsIgnoreCase(alternateURL,'PersonalizedInvitations')|| fn:containsIgnoreCase(alternateURL,'RegistryChecklist')}">
						
				         <link rel="alternate" media="${media_mobile_value}" href=""/>
				       </c:when>
					   <c:otherwise>
						<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}/"/>
						</c:otherwise>
						</c:choose>
					</c:if>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
			</c:choose>	
		</c:if>
		<%--Start: Added for Story 79 C |@ngup50 --%>
		<c:if test="${pageName eq 'CategoryLandingDetails' or pageName eq 'SubCategoryDetails'}">
			<c:choose>
				<c:when test="${currentSiteId eq 'BedBathUS'}">
					<c:set var="alternateDomain"><bbbc:config key="referer_url_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
					<c:set var="presentDomain"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set>
					<c:if test="${PageType ne 'HomePage'}">								
                    	<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
					</c:if>
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
				<c:when test="${currentSiteId eq 'BuyBuyBaby'}">
					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BuyBuyBaby" configName="HTTPConnectionAttributes" /></c:set>
					<c:if test="${PageType ne 'HomePage'}">								
						<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
					</c:if>
					<c:set var="alternateMobileURL">${alternateDomain_mobile}${postURL}</c:set>
					<c:if test="${pageName eq 'CategoryLandingDetails'}">
						<link rel="alternate" media="${media_mobile_value}" href="${alternateMobileURL}"/>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:set var="alternateDomain"><bbbc:config key="referer_url_BedBathUS" configName="HTTPConnectionAttributes" /></c:set> 
					<c:set var="presentDomain"><bbbc:config key="referer_url_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set> 
					<c:set var="alternateDomain_mobile"><bbbc:config key="referer_url_mobile_BedBathCanada" configName="HTTPConnectionAttributes" /></c:set>
					<c:if test="${PageType ne 'HomePage'}">								
						<c:set var="alternateDomain_mobile">${alternateDomain_mobile}/m</c:set>
					</c:if>
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
	
	</c:otherwise>
	</c:choose>
	
	</dsp:page>
