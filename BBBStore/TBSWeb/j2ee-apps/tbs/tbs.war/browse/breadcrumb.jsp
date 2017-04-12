<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/EverLivingPDPDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/EverLivingDetailsDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>

<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="CategoryId" param="categoryId"/>
<dsp:getvalueof var="Keyword" param="Keyword"/>
<dsp:getvalueof var="productId" param="productId"/>
<dsp:getvalueof var="poc" param="poc"/>
<c:set var="categoryPath" value="" scope="request" />
<c:set var="rootCategory" value="" scope="request" />
<c:set var="count" value="1" />
<c:set var="countOmni" value="1" />
<c:if test="${not empty poc }">
<dsp:droplet name="EverLivingPDPDroplet">
			<dsp:param name="id" param="poc" />
				<dsp:param name="siteId" param="siteId"/>
				<dsp:oparam name="output">
				<dsp:getvalueof var="everLivingProduct" param="everLivingProduct" />
				</dsp:oparam>
</dsp:droplet>
<c:choose>
<c:when test="${everLivingProduct}" >
<dsp:droplet name="EverLivingDetailsDroplet">
<dsp:param name="id" param="poc" />
<dsp:param name="siteId" param="siteId"/>
	<dsp:oparam name="output">	  
		<dsp:getvalueof var="collectionProductName" param="productVO.name" scope="request"/>
		<dsp:getvalueof var="collectionSmallImage" param="productVO.productImages.smallImage" scope="request"/>
	</dsp:oparam>
</dsp:droplet>
</c:when>
<c:otherwise>
<dsp:droplet name="ProductDetailDroplet">
<dsp:param name="id" param="poc" />
<dsp:param name="siteId" param="siteId"/>
<dsp:param name="isDefaultSku" value="true"/>
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
			<dsp:oparam name="output">
				<c:set var="fromCollege">false</c:set>
				<dsp:droplet name="ForEach">
				<dsp:param name="array" param="breadCrumb" />
					<dsp:oparam name="outputStart">
					<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
					<li><a href="${contextroot}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a></li>
					<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
					</dsp:oparam>
					<dsp:oparam name="output">
						<dsp:droplet name="CanonicalItemLink">
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
								<li><dsp:a page="${finalUrl}" title="${catName}">
										<dsp:valueof param="element.categoryName" />
									</dsp:a></li>
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
					</dsp:oparam>
					<dsp:oparam name="outputEnd">	
					  <%--Added for child and accessory products the link of parent product ..part of release 2.1 scope#29 --%>				  
					  <c:if test="${not empty poc }">					            
					      			<dsp:droplet name="CanonicalItemLink">
										<dsp:param name="id" param="poc" />
										<dsp:param name="itemDescriptorName" value="product" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
												param="url" />								
										</dsp:oparam>
									</dsp:droplet>
									<li><dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																									
											<c:out value="${collectionProductName}" escapeXml="false"/>																				
									</dsp:a></li>					    					   
					  </c:if>
					<li><a><dsp:valueof param="productVO.name" valueishtml="true"/></a></li>
					<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
					<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
					</dsp:oparam>
					<dsp:oparam name="empty">
						<li><a href="${contextroot}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a></li>
						<li><a><dsp:valueof param="productVO.name" valueishtml="true"/></a></li>
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
					<dsp:param name="siteId" param="siteId"/>
					<dsp:param name="Keyword" param="Keyword"/>
						<dsp:oparam name="output">
						 <dsp:getvalueof var="isPrimaryCat" param="isPrimaryCat"/>
                          <c:set var="isPrimaryCatValue" value="${isPrimaryCat}" scope="request"/>						
						  <c:choose>
							<c:when test="${(isPrimaryCatValue)}">							 
							<dsp:droplet name="ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="outputStart">
								<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
								<li><a href="${contextroot}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a></li>
								</dsp:oparam>
								<dsp:oparam name="output">
									<dsp:droplet name="CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
												param="url" />
											<dsp:getvalueof var="catName" param="element.categoryName" />
											<li><dsp:a page="${finalUrl}" title="${catName}">
													<dsp:valueof param="element.categoryName" />
												</dsp:a></li>
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
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
								    <%-- added as part of release 2.1 scope#29 --%>								  
									<c:if test="${not empty poc }">									  
						      			<dsp:droplet name="CanonicalItemLink">
											<dsp:param name="id" param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
													param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<li><dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>																					
										</dsp:a></li>					    					   
									</c:if>
								<li><a><dsp:valueof param="productVO.name" valueishtml="true"/></a></li>
								<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>		
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								</dsp:oparam>
							</dsp:droplet>
							</c:when>
							<c:otherwise>							
							<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
							<li><a href="${contextroot}" title="<bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}"/>"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a></li>
							<c:set var="title"><bbbl:label key='lbl_pdp_breadcrumb_back_to_search' language="${pageContext.request.locale.language}" /></c:set>
							<li><a href="${header.referer}" title="${title}">${title}</a></li>
							<c:set var="categoryPath" scope="request">${categoryPath} > <bbbl:label key='lbl_pdp_breadcrumb_back_to_search' language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
							    <%-- added as part of release 2.1 scope#29 --%>							   
								<c:if test="${not empty poc }">								   
						      			<dsp:droplet name="CanonicalItemLink">
											<dsp:param name="id" param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
													param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<li><dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>																					
										</dsp:a></li>					    					   
							  </c:if>
							<li><a><dsp:valueof param="productVO.name" valueishtml="true"/></a></li>
							<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>	
							<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
							<dsp:droplet name="BreadcrumbDroplet">
							<dsp:param name="productId" param="productId" />
							<dsp:param name="siteId" param="siteId"/>
								<dsp:oparam name="output">
								<dsp:droplet name="ForEach">
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
					<c:choose>
						<c:when test="${not empty poc}">
							<dsp:param name="productId" param="poc" />
						</c:when>
						<c:otherwise>
							<dsp:param name="productId" param="productId" />
						</c:otherwise>
					</c:choose>
					<dsp:param name="siteId" param="siteId"/>
						<dsp:oparam name="output">
							<dsp:getvalueof param="isPrimaryCat" var="isPrimaryCat"/>
							<dsp:getvalueof param="isOrphanProduct" var="isOrphanProduct"/>
							<c:choose>
							<c:when test="${isPrimaryCat eq 'true'}">
							<dsp:droplet name="ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="outputStart">
								<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								<li><a href="${contextroot}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a></li>
								</dsp:oparam>
								<dsp:oparam name="output">
									<dsp:droplet name="CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
												param="url" />
											<dsp:getvalueof var="catName" param="element.categoryName" />
											<li><dsp:a page="${finalUrl}" title="${catName}">
													<dsp:valueof param="element.categoryName" />
												</dsp:a></li>
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
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
								<%-- added as part of release 2.1 scope#29 --%>								  
								<c:if test="${not empty poc }">								   
						      			<dsp:droplet name="CanonicalItemLink">
											<dsp:param name="id" param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
													param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<li><dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>																						
										</dsp:a></li>					    					   
							  </c:if>								       
								<li><a><dsp:valueof param="productVO.name" valueishtml="true"/></a></li>
								<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>		
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								</dsp:oparam>
							</dsp:droplet>
							</c:when>
							<c:when test="${isOrphanProduct}">
								<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
								<li><a href="${contextroot}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a></li>
								<li><a><dsp:valueof param="productVO.name" valueishtml="true"/>	</a></li>
								<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
								<c:set var="omniProdName" scope="request"><dsp:valueof param="productVO.name" valueishtml="true"/></c:set>
							</c:when>
							<c:otherwise>
							<dsp:droplet name="ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="outputStart">
								<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
								<li><a href="${contextroot}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a></li>
								</dsp:oparam>
								<dsp:oparam name="output">
									<dsp:droplet name="CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
												param="url" />
											<dsp:getvalueof var="catName" param="element.categoryName" />
											<li><dsp:a page="${finalUrl}" title="${catName}">
													<dsp:valueof param="element.categoryName" />
												</dsp:a></li>
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
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
								<%-- added as part of release 2.1 scope#29 --%>								  
								<c:if test="${not empty poc }">								   
						      			<dsp:droplet name="CanonicalItemLink">
											<dsp:param name="id" param="poc" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
													param="url" />
											</dsp:oparam>
										</dsp:droplet>
										<li><dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">																																										
												<c:out value="${collectionProductName}" escapeXml="false"/>
										</dsp:a></li>					    					   
							  </c:if>
								<li><a><dsp:valueof param="productVO.name" valueishtml="true"/>	</a></li>
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