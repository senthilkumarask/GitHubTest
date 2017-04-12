<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/EverLivingPDPDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/EverLivingDetailsDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>
<dsp:getvalueof var="CategoryId" param="categoryId"/>
<dsp:getvalueof var="Keyword" param="Keyword"/>
<dsp:getvalueof var="productId" param="productId"/>
<dsp:getvalueof var="brandId" param="brandId"/>
<c:if test="${not empty param.poc }">
	<dsp:getvalueof var="poc" param="poc"/>
</c:if>
<c:set var="categoryPath" value="" scope="request" />
<c:set var="rootCategory" value="" scope="request" />
<c:set var="count" value="1" />
<c:set var="countOmni" value="1" />



<c:if test="${not empty poc }">
<dsp:droplet name="EverLivingPDPDroplet">
			<dsp:param name="id" value="${poc}" />
				<dsp:param name="siteId" param="siteId"/>
				<dsp:oparam name="output">
				<dsp:getvalueof var="everLivingProduct" param="everLivingProduct" />
				</dsp:oparam>
</dsp:droplet>
<c:choose>
<c:when test="${everLivingProduct}" >
<dsp:droplet name="EverLivingDetailsDroplet">
<dsp:param name="id" value="${poc}" />
<dsp:param name="siteId" param="siteId"/>
<dsp:param name="isMainProduct" value="true"/>
	<dsp:oparam name="output">	  
		<dsp:getvalueof var="collectionProductName" param="productVO.name" scope="request"/>
		<dsp:getvalueof var="collectionSmallImage" param="productVO.productImages.smallImage" scope="request"/>
	</dsp:oparam>
</dsp:droplet>
</c:when>
<c:otherwise>
<dsp:droplet name="ProductDetailDroplet">
<dsp:param name="id" value="${poc}" />
<dsp:param name="isMainProduct" value="true"/>
<dsp:param name="siteId" param="siteId"/>
	<dsp:oparam name="output">	  
		<dsp:getvalueof var="collectionProductName" param="productVO.name" scope="request"/>
		<dsp:getvalueof var="collectionSmallImage" param="productVO.productImages.smallImage" scope="request"/>
	</dsp:oparam>
</dsp:droplet>
</c:otherwise>
</c:choose>
</c:if>							
<c:choose>
	<c:when test="${!(CategoryId eq null)}">	  
		<dsp:droplet name="BreadcrumbDroplet">
		<dsp:param name="categoryId" param="categoryId" />
		<dsp:param name="siteId" param="siteId"/>
		<dsp:param name="isFromBreadcrumb" value="true" />
			<dsp:oparam name="output">
				<c:set var="fromCollege">false</c:set>
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" param="breadCrumb" />
					<dsp:oparam name="outputStart">
					<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
					<a href="${scheme}://${servername}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
					<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
					<span class="rightCarrot">&gt;</span>
					</dsp:oparam>
					<dsp:oparam name="output">
						<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
							<dsp:param name="id" param="element.categoryId" />
							<dsp:param name="itemDescriptorName" value="category" />
							<dsp:param name="repositoryName"
								value="/atg/commerce/catalog/ProductCatalog" />
							<dsp:oparam name="output">
								<c:set var="isCollege">false</c:set>
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
									param="url" />
								<dsp:getvalueof var="catName" param="element.categoryName" />
								<c:if test="${fn:toLowerCase(catName) eq fn:toLowerCase('College') || fn:toLowerCase(catName) eq fn:toLowerCase('University')}">
									<c:set var="isCollege">true</c:set>
									<c:set var="fromCollege">true</c:set>
								</c:if>
								<c:if test="${isCollege == true}">
									<c:set var="finalUrl">/page/College</c:set>
								</c:if>
								<c:if test="${isCollege == false && fromCollege == true}">
									<c:set var="finalUrl">${finalUrl}?fromCollege=true</c:set>
								</c:if>
								<dsp:a page="${finalUrl}" title="${catName}">
										<dsp:valueof param="element.categoryName" />
									</dsp:a>
							<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>
							<c:set var="phantomCategory" scope="request"><dsp:valueof param="element.phantomCategory" /></c:set>		
							<c:if test="${count eq '1'}">
								<c:set var="rootCategory" scope="request"><dsp:valueof param="element.categoryId" /></c:set>
								<c:set var="rootCategoryName" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
								<c:set var="count" value="${count+1}" />
							</c:if>
							<c:if test="${countOmni eq '2'}">
								<c:set var="categoryNameL1" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
							</c:if>
							<c:if test="${countOmni eq '3'}">
								<c:set var="categoryNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
							</c:if>
							<c:set var="countOmni" value="${countOmni+1}" />
								
							</dsp:oparam>
						</dsp:droplet>
						<span class="rightCarrot">&gt;</span>
					</dsp:oparam>
					<dsp:oparam name="outputEnd">	
					  <%--Added for child and accessory products the link of parent product ..part of release 2.1 scope#29 --%>				  
					  <c:if test="${not empty poc }">					            
					      			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="poc" />
										<dsp:param name="itemDescriptorName" value="product" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
												param="url" />								
										</dsp:oparam>
									</dsp:droplet>
									<dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																									
											<c:out value="${collectionProductName}" escapeXml="false"/>																				
									</dsp:a>					    					   
					   <span class="rightCarrot">&gt;</span>
					  </c:if>
					<span class="bold"><dsp:valueof param="productVO.name" valueishtml="true"/></span>
					<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
					<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
					</dsp:oparam>
					<dsp:oparam name="empty">
						<a href="${scheme}://${servername}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
						<span class="rightCarrot">&gt;</span>
						<span class="bold"><dsp:valueof param="productVO.name" valueishtml="true"/></span>
					</dsp:oparam>
				</dsp:droplet>
				
			</dsp:oparam>
		</dsp:droplet>	
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${!(Keyword eq null)}">
			  <%-- added as part of release 2.1 scope#29 --%>	
			   <dsp:droplet name="BreadcrumbDroplet">
					<dsp:param name="productId" param="productId" />
					<dsp:param name="poc" param="poc" />
					<dsp:param name="siteId" param="siteId"/>
					<dsp:param name="Keyword" param="Keyword"/>
					<dsp:param name="isFromBreadcrumb" value="true" />
						<dsp:oparam name="output">
						 <dsp:getvalueof var="isPrimaryCat" param="isPrimaryCat"/>
                          <c:set var="isPrimaryCatValue" value="${isPrimaryCat}" scope="request"/>						
						 <%--  <c:choose> 
							<c:when test="${(isPrimaryCatValue)}">							 
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="outputStart">
								<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
								<a href="${scheme}://${servername}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
								<span class="rightCarrot">&gt;</span>
								</dsp:oparam>
								<dsp:oparam name="output">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
												param="url" />
											<dsp:getvalueof var="catName" param="element.categoryName" />
											<dsp:a page="${finalUrl}" title="${catName}">
													<dsp:valueof param="element.categoryName" />
												</dsp:a>
											<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>	
											<c:if test="${count eq '1'}">
												<c:set var="rootCategory" scope="request"><dsp:valueof param="element.categoryId" /></c:set>
												<c:set var="rootCategoryName" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
												<c:set var="count" value="${count+1}" />
											</c:if>
											<c:if test="${countOmni eq '2'}">
												<c:set var="categoryNameL1" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>			
											
											<c:if test="${countOmni eq '3'}">
												<c:set var="categoryNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>
											<c:set var="countOmni" value="${countOmni+1}" />
										</dsp:oparam>
									</dsp:droplet>
									<span class="rightCarrot">&gt;</span>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
								    added as part of release 2.1 scope#29								  
									<c:if test="${not empty poc }">									  
						      			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
													param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>																					
										</dsp:a>					    					   
									   <span class="rightCarrot">&gt;</span>
									</c:if>
								<span class="bold"><dsp:valueof param="productVO.name" valueishtml="true"/></span>
								<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>		
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								</dsp:oparam>
							</dsp:droplet>
							</c:when>
							<c:otherwise> --%>			
							<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
							<a href="${scheme}://${servername}" title="<bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}"/>"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
							<span class="rightCarrot">&gt;</span>	
							<c:set var="title"><bbbl:label key='lbl_pdp_breadcrumb_back_to_search' language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="referer"><c:out value="${header.referer}" escapeXml="true"/></c:set>
							<a href="${referer}" title="${title}">${title}</a>
							<c:set var="categoryPath" scope="request">${categoryPath} > <bbbl:label key='lbl_pdp_breadcrumb_back_to_search' language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
							<span class="rightCarrot">&gt;</span>
							    <%-- added as part of release 2.1 scope#29 --%>							   
								<c:if test="${not empty poc }">								   
						      			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
													param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>																					
										</dsp:a>					    					   
							   <span class="rightCarrot">&gt;</span>
							  </c:if>
							<span class="bold"><dsp:valueof param="productVO.name" valueishtml="true"/></span>
							<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>	
							<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
							<dsp:droplet name="BreadcrumbDroplet">
							<dsp:param name="productId" param="productId" />
							<dsp:param name="poc" param="poc" />
							<dsp:param name="siteId" param="siteId"/>
							<dsp:param name="isFromBreadcrumb" value="true" />
								<dsp:oparam name="output">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" param="breadCrumb" />
										<dsp:oparam name="output">
											<c:if test="${count eq '1'}">
												<c:set var="rootCategory" scope="request"><dsp:valueof param="element.categoryId" /></c:set>
												<c:set var="rootCategoryName" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
												<c:set var="count" value="${count+1}" />
											</c:if>	
											<c:if test="${countOmni eq '2'}">
											<c:set var="categoryNameL1" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
										</c:if>
										<c:if test="${countOmni eq '3'}">
											<c:set var="categoryNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
										</c:if>
										<c:set var="countOmni" value="${countOmni+1}" />	
										</dsp:oparam>
								</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>							
						<%-- 	</c:otherwise>							
                           </c:choose>	 --%>																																									
						</dsp:oparam>
					</dsp:droplet>						
			</c:when>
			<c:when test="${not empty brandId}">
		
			   <dsp:droplet name="BreadcrumbDroplet">
					<dsp:param name="productId" param="productId" />
					<dsp:param name="siteId" param="siteId"/>
					<dsp:param name="isFromBreadcrumb" value="true" />
						<dsp:oparam name="output">
						 <dsp:getvalueof var="isPrimaryCat" param="isPrimaryCat"/>
                          <c:set var="isPrimaryCatValue" value="${isPrimaryCat}" scope="request"/>						
						  <c:choose>
							<c:when test="${(isPrimaryCatValue)}">							 
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="outputStart">
								<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
								<a href="${scheme}://${servername}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
								<span class="rightCarrot">&gt;</span>
								</dsp:oparam>
								<dsp:oparam name="output">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
												param="url" />
											<dsp:getvalueof var="catName" param="element.categoryName" />
											<dsp:a page="${finalUrl}" title="${catName}">
													<dsp:valueof param="element.categoryName" />
												</dsp:a>
											<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>	
											<c:if test="${count eq '1'}">
												<c:set var="rootCategory" scope="request"><dsp:valueof param="element.categoryId" /></c:set>
												<c:set var="rootCategoryName" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
												<c:set var="count" value="${count+1}" />
											</c:if>
											<c:if test="${countOmni eq '2'}">
												<c:set var="categoryNameL1" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>			
											
											<c:if test="${countOmni eq '3'}">
												<c:set var="categoryNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>
											<c:set var="countOmni" value="${countOmni+1}" />
										</dsp:oparam>
									</dsp:droplet>
									<span class="rightCarrot">&gt;</span>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
								   						  
									<c:if test="${not empty poc }">									  
						      			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id"  param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
													param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>																					
										</dsp:a>					    					   
									   <span class="rightCarrot">&gt;</span>
									</c:if>
								<span class="bold"><dsp:valueof param="productVO.name" valueishtml="true"/></span>
								<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>		
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								</dsp:oparam>
							</dsp:droplet>
							</c:when>
							
							<c:otherwise>							
							<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
							<a href="${scheme}://${servername}" title="<bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}"/>"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
							<span class="rightCarrot">&gt;</span>	
								<dsp:droplet name="BrandDetailDroplet">
									<dsp:param name="keywordName" value="${brandId}"/>
									<dsp:oparam name="seooutput">
									<dsp:getvalueof var="brandUrl" param="seoUrl" />
									<dsp:getvalueof var="brandName" param="brandName" />
									</dsp:oparam>
								</dsp:droplet>
							<a href="${contextroot}${brandUrl}" title="${brandName}">${brandName}</a>
							<span class="rightCarrot">&gt;</span>
							<c:set var="categoryPath" scope="request">${categoryPath} > ${brandName}</c:set>
								<c:if test="${not empty poc }">								   
						      			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String" param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>																					
										</dsp:a>					    					   
							   <span class="rightCarrot">&gt;</span>
							  </c:if>
							<span class="bold"><dsp:valueof param="productVO.name" valueishtml="true"/></span>
							<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>	
							<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
							<dsp:droplet name="BreadcrumbDroplet">
							<dsp:param name="productId" param="productId" />
							<dsp:param name="siteId" param="siteId"/>
							<dsp:param name="isFromBreadcrumb" value="true" />
								<dsp:oparam name="output">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" param="breadCrumb" />
										<dsp:oparam name="output">
											<c:if test="${count eq '1'}">
												<c:set var="rootCategory" scope="request"><dsp:valueof param="element.categoryId" /></c:set>
												<c:set var="rootCategoryName" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
												<c:set var="count" value="${count+1}" />
											</c:if>	
											<c:if test="${countOmni eq '2'}">
											<c:set var="categoryNameL1" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
										</c:if>
										<c:if test="${countOmni eq '3'}">
											<c:set var="categoryNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
										</c:if>
										<c:set var="countOmni" value="${countOmni+1}" />	
										</dsp:oparam>
								</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>							
							</c:otherwise>							
                           </c:choose>																																										
						</dsp:oparam>
					</dsp:droplet>						
			</c:when>
			<c:otherwise>
					<dsp:droplet name="BreadcrumbDroplet">
					<dsp:param name="productId" param="productId" />
					<dsp:param name="poc" param="poc" />
					<dsp:param name="siteId" param="siteId"/>
					<dsp:param name="isFromBreadcrumb" value="true" />
						<dsp:oparam name="output">
							<dsp:getvalueof param="isPrimaryCat" var="isPrimaryCat"/>
							<dsp:getvalueof param="isOrphanProduct" var="isOrphanProduct"/>
							<c:choose>
							<c:when test="${isPrimaryCat eq 'true'}">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="outputStart">
								<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								<a href="${scheme}://${servername}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
								<span class="rightCarrot">&gt;</span>
								</dsp:oparam>
								<dsp:oparam name="output">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
												param="url" />
											<dsp:getvalueof var="catName" param="element.categoryName" />
											<dsp:a page="${finalUrl}" title="${catName}">
													<dsp:valueof param="element.categoryName" />
												</dsp:a>
											<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>	
											<c:set var="phantomCategory" scope="request"><dsp:valueof param="element.phantomCategory" /></c:set>
											<c:if test="${count eq '1'}">
												<c:set var="rootCategory" scope="request"><dsp:valueof param="element.categoryId" /></c:set>
												<c:set var="rootCategoryName" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
												<c:set var="count" value="${count+1}" />
											</c:if>
										
											<c:if test="${countOmni eq '2'}">
												<c:set var="categoryNameL1" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>			
											
											<c:if test="${countOmni eq '3'}">
												<c:set var="categoryNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>
											<c:set var="countOmni" value="${countOmni+1}" />
										</dsp:oparam>
									</dsp:droplet>
									<span class="rightCarrot">&gt;</span>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
								<%-- added as part of release 2.1 scope#29 --%>								  
								<c:if test="${not empty poc }">								   
						      			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
													param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>																						
										</dsp:a>					    					   
							   <span class="rightCarrot">&gt;</span>
							  </c:if>								       
								<span class="bold"><dsp:valueof param="productVO.name" valueishtml="true"/></span>
								<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>		
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								</dsp:oparam>
							</dsp:droplet>
							</c:when>
							<c:when test="${isOrphanProduct}">
								<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
								<a href="${scheme}://${servername}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
								<span class="rightCarrot">&gt;</span>
								<span class="bold"><dsp:valueof param="productVO.name" valueishtml="true"/>	</span>
								<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
							</c:when>
							<c:otherwise>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="outputStart">
								<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
								<a href="${scheme}://${servername}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
								<span class="rightCarrot">&gt;</span>
								</dsp:oparam>
								<dsp:oparam name="output">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
												param="url" />
											<dsp:getvalueof var="catName" param="element.categoryName" />
											<dsp:a page="${finalUrl}" title="${catName}">
													<dsp:valueof param="element.categoryName" />
												</dsp:a>
											<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>	
											<c:set var="phantomCategory" scope="request"><dsp:valueof param="element.phantomCategory" /></c:set>
											<c:if test="${count eq '1'}">
												<c:set var="rootCategory" scope="request"><dsp:valueof param="element.categoryId" /></c:set>
												<c:set var="rootCategoryName" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
												<c:set var="count" value="${count+1}" />
											</c:if>
											
											<c:if test="${countOmni eq '2'}">
												<c:set var="categoryNameL1" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>			
											
											<c:if test="${countOmni eq '3'}">
												<c:set var="categoryNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>
											<c:set var="countOmni" value="${countOmni+1}" />
										</dsp:oparam>
									</dsp:droplet>
									<span class="rightCarrot">&gt;</span>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
								<%-- added as part of release 2.1 scope#29 --%>								  
								<c:if test="${not empty poc }">								   
						      			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
													param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>																						
										</dsp:a>					    					   
							   <span class="rightCarrot">&gt;</span>
							  </c:if>
								<span class="bold"><dsp:valueof param="productVO.name" valueishtml="true"/>	</span>
								<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								</dsp:oparam>
							</dsp:droplet>
							</c:otherwise>
							</c:choose>
							
						</dsp:oparam>
					</dsp:droplet>
			</c:otherwise>
		</c:choose>
	</c:otherwise>							
</c:choose>

</dsp:page>