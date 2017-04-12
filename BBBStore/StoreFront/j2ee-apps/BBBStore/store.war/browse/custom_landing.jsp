<dsp:page>
	<dsp:importbean bean="/com/bbb/cms/droplet/CustomLandingTemplateDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	
	<c:set var="omnitureFlag" value="false"/>
	<c:set var="clpCacheTimeOut">
		<bbbc:config key="ClpCacheTimeOut" configName="HTMLCacheKeys" />
	</c:set>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="categoryId" param="categoryId"/>
	<%-- Retrieve CLP Template --%>
			<dsp:droplet name="CustomLandingTemplateDroplet">
				<dsp:param param="categoryId" name="categoryId" />
				<dsp:param param="name" name="name" />
				<dsp:param name="templateName" value="customLandingTemplate" />
				<dsp:param name="alternateURLRequired" value="true" />
				<dsp:param name="omnitureFlag" value="true" />
			
				<%-- If CLP data is not available then redirect to related category --%>
				<dsp:oparam name="empty">
				 	 <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
	                       <dsp:param name="id" param="categoryId" />
	                       <dsp:param name="itemDescriptorName" value="category" />
	                       <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
	                      		<dsp:oparam name="output">
			                          <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
	                         		   <c:redirect url="${finalUrl}">${catName}></c:redirect>
	                    	     </dsp:oparam>
	                 </dsp:droplet>
				</dsp:oparam>
				<dsp:oparam name="error">
				 	 <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
	                       <dsp:param name="id" param="categoryId" />
	                       <dsp:param name="itemDescriptorName" value="category" />
	                       <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
	                      		<dsp:oparam name="output">
			                          <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
	                         		   <c:redirect url="${finalUrl}">${catName}></c:redirect>
	                    	     </dsp:oparam>
	                 </dsp:droplet>
				</dsp:oparam>
			<dsp:oparam name="output">
				<dsp:getvalueof var="alternateURL"  param="alternateURL"/>
				<dsp:getvalueof var="canadaAlternateURL" param="canadaAlternateURL"/>
				<dsp:getvalueof var="usAlternateURL" param="usAlternateURL"/>
				<dsp:getvalueof var="id"  param="cmsResponseVO.responseItems[0].id"/>
				<dsp:getvalueof var="clpTitle" param="clpTitle"/>
				<dsp:getvalueof var="categoryL1" param="categoryL1" />
				<dsp:getvalueof var="categoryL2" param="categoryL2" />
				<dsp:getvalueof var="categoryL3" param="categoryL3" />
				<dsp:getvalueof var="clpName"  param="clpName"/>
			
				<%-- Setting variable to set the clp attribute for back to clp navigation for search --%>
				<c:set var="fromClpQs" value="?fromClp=true"/>
				<c:set var="fromClpAmp" value="&fromClp=true"/>
					
				<%-- Set CLP Canonical URL --%>
				<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
					<dsp:param name="id" value="${id}" />
					<dsp:param name="itemDescriptorName" value="customLandingTemplate" />
					<dsp:param name="repositoryName" value="/com/bbb/cms/repository/CustomLandingTemplate" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="clpCanonicalUrl" vartype="java.lang.String" param="url" />
					</dsp:oparam>
				</dsp:droplet>
		
				<bbb:pageContainer>
					<jsp:attribute name="clpTitle">${clpTitle }</jsp:attribute>
					<jsp:attribute name="section">browse</jsp:attribute>
					<jsp:attribute name="pageWrapper">custom useCertonaJs useLiveClicker</jsp:attribute>
					<jsp:attribute name="alternateURL">${alternateURL}</jsp:attribute>
					<jsp:attribute name="canonicalURL">${clpCanonicalUrl}</jsp:attribute>
					<jsp:attribute name="canadaAlternateURL">${canadaAlternateURL}</jsp:attribute>
					<jsp:attribute name="usAlternateURL">${usAlternateURL}</jsp:attribute>
					
					<jsp:body>
				<%--Adding Cache Droplet to improve performance --%>
					<dsp:droplet name="/atg/dynamo/droplet/Cache">
					   	<dsp:param name="key" value="${currentSiteId}_CLP_${categoryId}" />
					   	<dsp:param name="cacheCheckSeconds" value="${clpCacheTimeOut}"/>
					 		 <dsp:oparam name="output">
								<div id="content" class="container_12 clearfix clpSection">
									<dsp:getvalueof var="resposneElements" param="cmsResponseVO.responseItems" />	
									<dsp:param name="cmsTemplateElement" param="cmsResponseVO.responseItems[0]"/>
									<dsp:getvalueof var="clpName"  param="cmsTemplateElement.clpName"/>
									
									<h1 class="grid_12"><dsp:valueof  param="cmsTemplateElement.clpTitle"/></h1>
									
									<%-- Display Hero Images  --%>
									<dsp:droplet name="ForEach">
											<dsp:param name="array"  param="cmsTemplateElement.heroImagePromoBox"/>
											<dsp:param name="elementName"  value="heroImage"/>
												<dsp:oparam name="output">
												<div id="mainBanner" role="banner" class="grid_12 marBottom_20">
												<h3><dsp:valueof  param="heroImage.heroBoxTitle"/></h3>
													<dsp:getvalueof var="promoBoxContent" param="heroImage.promoBoxContent" />
													<dsp:getvalueof var="imageMapName" param="heroImage.imageMapName" />
													<dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="heroImage.imageMapContent"/>
			                                     	<dsp:getvalueof var="altText" param="heroImage.imageAltText" />
													<dsp:getvalueof var="imageLink" param="heroImage.imageLink" />
													<dsp:getvalueof var="imageUrl" param="heroImage.imageUrl" />	
																															
													<c:choose>
														<c:when test="${not empty promoBoxContent}">
															<dsp:valueof value="${promoBoxContent}" valueishtml="true"/>
														</c:when>
			
														<c:when test="${not empty imageMapContent}">
													  
															<%-- Display Image Map --%>
															<div style="background: url(&quot;${imageUrl}&quot;) no-repeat scroll center top transparent; width: 976px; margin:0">
																<c:set var="iMapName">#${imageMapName}</c:set>
																<img width="976" height="405" class="heroImages" style="width: 976px;margin-right: 0px;" 
																		src="/_assets/global/images/spacer.gif" title="${altText}" alt="${altText}" usemap="${imageMapName}">
																<MAP NAME="${imageMapName}">${imageMapContent}</MAP>
															</div>
														</c:when>
														<c:otherwise>
						                                    <dsp:getvalueof var="altText" param="heroImage.imageAltText" />
															<dsp:getvalueof var="imageLink" param="heroImage.imageLink" />
															<dsp:getvalueof var="imageUrl" param="heroImage.imageUrl" />
																<a title="${altText}" href="${imageLink}">
																	<img alt="${altText}" src="${imageUrl}"/>
																</a>																			
														</c:otherwise>
													</c:choose>
												</div>
											</dsp:oparam>
										 </dsp:droplet>
			
										 <%-- Display 3X Containers --%>
										
										 <div class="grid_12  marBottom_20">
										  
											<%-- Setting counter to achieve multiple layouts--%>
												<c:set var="counter" value="0"/>
												
												
												<%-- Iterate Over 3XPromoTierLayOut --%>	
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array"  param="cmsTemplateElement.3XPromoTierLayOut"/>
													<dsp:param name="elementName"  value="threeCrossModule"/>
														<dsp:oparam name="output">
														<div class="grid_12 alpha omega marBottom_20">
														<h3><dsp:valueof  param="threeCrossModule.threeXPromoTitle"/></h3>
														<c:forEach var="promoCounter" begin="1" end="3">
																<dsp:param name="promoSlot"  param="threeCrossModule.promoContainer${promoCounter}"/>
																<c:set var="counter" value="${counter + 1}"/>
																<%--Promo Slot 1,2,3  --%>	
																<dsp:droplet name="IsEmpty">
																	<dsp:param name="value"  param="promoSlot"/>
																		<dsp:oparam name="false">
																		
																		<dsp:getvalueof var="promoSlotTitle" param="promoSlot.promoElementName"/>
																		<dsp:getvalueof var="promoSlotPromoAltText" param="promoSlot.promoAltText"/>
																		<dsp:getvalueof var="promoSlotImgLink" param="promoSlot.promoImageLink"/>
																		<dsp:getvalueof var="promoSlotImgType" param="promoSlot.imageDestinationURLType"/>
																			<dsp:droplet name="Switch">
																				<dsp:param name="value" param="promoSlot.type"/>
																				<dsp:oparam name="Image">
																					<c:choose>
																						<c:when test="${promoCounter==1}">
																							<div class="grid_4 alpha contentBox">
																						</c:when>
																						<c:when test="${promoCounter==2}">
																							<div class="grid_4 contentBox">
																						</c:when>
																						<c:otherwise>
																							<div class="grid_4 omega contentBox">
																						</c:otherwise>
																					</c:choose>
																						<%--Calling the imageFragment jsp --%>
																					<dsp:include page="customLanding_imageFrag.jsp" flush="true">
																						<dsp:param name="fromClpQs" value="${fromClpQs}"/>
																						<dsp:param name="fromClpAmp" value="${fromClpAmp}"/>
																					</dsp:include>	
																					</div>
																				</dsp:oparam>
																				<dsp:oparam name="Video">
																					<c:choose>
																						<c:when test="${promoCounter==1}">
																							<div class="grid_4 videoItem alpha contentBox">
																						</c:when>
																						<c:when test="${promoCounter==2}">
																							<div class="grid_4 videoItem contentBox">
																						</c:when>
																						<c:otherwise>
																							<div class="grid_4 videoItem omega contentBox">
																						</c:otherwise>
																					</c:choose>
																						<%--Calling the videoFragment jsp --%>
																					<dsp:include page="customLanding_videoFrag.jsp" flush="true">
																						<dsp:param name="counter" value="${counter}"/>
																					</dsp:include>	
																					</div>
																				</dsp:oparam>
																				
																				<dsp:oparam name="Static">
																					<c:choose>
																						<c:when test="${promoCounter==1}">
																							<div class="grid_4 alpha contentBox">
																						</c:when>
																						<c:when test="${promoCounter==2}">
																							<div class="grid_4 contentBox">
																						</c:when>
																						<c:otherwise>
																							<div class="grid_4 omega contentBox">
																						</c:otherwise>
																					</c:choose>
																						<%--Calling the staticFragment jsp --%>
																					<dsp:include page="customLanding_staticFrag.jsp" flush="true"/>	
																					</div>	
																				</dsp:oparam>
																			</dsp:droplet><%-- End Switch --%>
																	</dsp:oparam>
																	<dsp:oparam name="true">
																	</dsp:oparam>
																</dsp:droplet><%-- End IsEmpty --%>
														</c:forEach>
														</div>
										</dsp:oparam>
									</dsp:droplet>
									</div>
								<%-- End 3X Module --%>
								
								<%-- Begin 4X Module --%>
								<div class="grid_12  marBottom_20">
			
								<%-- Iterate Over 4XPromoTierLayOut --%>	
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array"  param="cmsTemplateElement.4XPromoTierLayOut"/>
									<dsp:param name="elementName"  value="fourCrossModule"/>
									<dsp:oparam name="output">
										<h3><dsp:valueof  param="fourCrossModule.fourXPromoTitle"/></h3>
											<div class="grid_12 alpha omega marBottom_20">
																	
												<div class="grid_12 alpha omega">
													<c:forEach var="promoCounter" begin="1" end="4">
														<dsp:param name="promoSlot"  param="fourCrossModule.promoContainer${promoCounter}"/>
														<c:set var="counter" value="${counter + 1}"/>			
												<%--PromoSlot 4,5,6,7  --%>			
													<dsp:droplet name="IsEmpty">
														<dsp:param name="value"  param="promoSlot"/>
														<dsp:oparam name="false">
															<dsp:getvalueof var="promoSlotTitle" param="promoSlot.promoElementName"/>
															<dsp:getvalueof var="promoSlotPromoAltText" param="promoSlot.promoAltText"/>
															<dsp:getvalueof var="promoSlotImgLink" param="promoSlot.promoImageLink"/>
															<dsp:getvalueof var="promoSlotImgType" param="promoSlot.imageDestinationURLType"/>	
																<dsp:droplet name="Switch">
																	<dsp:param name="value" param="promoSlot.type"/>
																		<dsp:oparam name="Image">
																			<c:choose>
																				<c:when test="${promoCounter==1}">
																					<div class="grid_3 alpha contentBox">
																				</c:when>
																				<c:when test="${promoCounter==2}">
																					<div class="grid_3 contentBox">
																				</c:when>
																				<c:when test="${promoCounter==3}">
																					<div class="grid_3 contentBox">
																				</c:when>																	
																				<c:otherwise>
																					<div class="grid_3 omega contentBox">
																				</c:otherwise>
																			</c:choose>
																			<%--Calling the imageFragment jsp --%>	
																			<dsp:include page="customLanding_imageFrag.jsp" flush="true">
																					<dsp:param name="fromClpQs" value="${fromClpQs}"/>
																					<dsp:param name="fromClpAmp" value="${fromClpAmp}"/>
																			</dsp:include>		
																				</div>
																		</dsp:oparam>
																		<dsp:oparam name="Video">
																				<c:choose>
																					<c:when test="${promoCounter==1}">
																						<div class="grid_3 videoItem alpha contentBox">
																					</c:when>
																					<c:when test="${promoCounter==2}">
																						<div class="grid_3 videoItem contentBox">
																					</c:when>
																					<c:when test="${promoCounter==3}">
																						<div class="grid_3 videoItem contentBox">
																					</c:when>																			
																					<c:otherwise>
																						<div class="grid_3 videoItem omega contentBox">
																					</c:otherwise>
																				</c:choose>
																				<%--Calling the videoFragment jsp --%>
																				<dsp:include page="customLanding_videoFrag.jsp" flush="true">
																					<dsp:param name="counter" value="${counter}"/>
																				</dsp:include>	
																			</div>
																		</dsp:oparam>
																		<dsp:oparam name="Static">
																				<c:choose>
																					<c:when test="${promoCounter==1}">
																						<div class="grid_3 alpha contentBox">
																					</c:when>
																					<c:when test="${promoCounter==2}">
																						<div class="grid_3 contentBox">
																					</c:when>
																					<c:when test="${promoCounter==3}">
																						<div class="grid_3 contentBox">
																					</c:when>																			
																					<c:otherwise>
																						<div class="grid_3 omega contentBox">
																					</c:otherwise>
																				</c:choose>
																							<%--Calling the staticFragment jsp --%>
																				<dsp:include page="customLanding_staticFrag.jsp" flush="true"/>	
																			</div>
																		</dsp:oparam>
																</dsp:droplet>
															</dsp:oparam>
															<dsp:oparam name="true">
															</dsp:oparam>
												</dsp:droplet>
											</c:forEach>
													</div> 
												</div> 
											</dsp:oparam>
										</dsp:droplet>
										
									</div>
								<%-- End 4X Module --%>
							
								<%-- Show SEO Static Text --%>
								<dsp:getvalueof var="seoStaticText"  param="cmsTemplateElement.seoStaticText"/>	
								<c:if test="${not empty seoStaticText}">
									<div class="grid_12 clearfix catSEOTxt">
										<c:out value="${seoStaticText}" escapeXml="false"/>
									</div>
								</c:if>
								<%--DoubleClick Floodlight START
								<c:if test="${DoubleClickOn}">
									<c:if test="${(currentSiteId eq BedBathUSSite)}">
									   <c:set var="cat"><bbbc:config key="cat_category_bedBathUS" configName="RKGKeys" /></c:set>
									   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
									   <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
									</c:if>
									 <c:if test="${(currentSiteId eq BuyBuyBabySite)}">
									   <c:set var="cat"><bbbc:config key="cat_category_baby" configName="RKGKeys" /></c:set>
									   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
									   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
									 </c:if>
									  <c:if test="${(currentSiteId eq BedBathCanadaSite)}">
					    		  		 <c:set var="cat"><bbbc:config key="cat_category_bedbathcanada" configName="RKGKeys" /></c:set>
					    		   		 <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
					    		  		 <c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
					    		 </c:if>
									<dsp:include page="/_includes/double_click_tag.jsp">
										<dsp:param name="doubleClickParam"
													value="src=${src};type=${type};cat=${cat};u10=${categoryId};u11=${categoryL1}" />
									</dsp:include>
								</c:if>
								DoubleClick Floodlight END --%>
							</div>
						
							<script type="text/javascript">
							var resx = new Object();
							</script>
							</dsp:oparam>
						</dsp:droplet>
						</jsp:body>
						<%--Omniture changes for Custom landing page start/Band 905(Clp omniture changes for evar5 & evar6) start --%>
					 	<jsp:attribute name="footerContent">
					 	<script type="text/javascript">
			            if(typeof s !=='undefined') {
			                <dsp:getvalueof var="pageName" param="categoryL1"/>
			                <dsp:getvalueof var="omniCategoryL2" param="categoryL2"/>
			                <dsp:getvalueof var="omniCategoryL3" param="categoryL3"/>
			                <dsp:getvalueof var="omniClpName" param="clpName"/>
			                
			                var omniCategoryL1 = '${fn:replace(fn:replace(pageName,'\'',''),'"','')}';
			                var omniCategoryL2 = '${fn:replace(fn:replace(omniCategoryL2,'\'',''),'"','')}';
			                var omniCategoryL3 = '${fn:replace(fn:replace(omniCategoryL3,'\'',''),'"','')}';
			                var omniClpName = 	'${fn:replace(fn:replace(omniClpName,'\'',''),'"','')}';
			                var customClpValue = " Landing Page";
			                omniClpName = omniClpName.concat(customClpValue);
			                s.pageName = omniCategoryL1 + '>' +  omniClpName + '>All';
			                s.channel= omniCategoryL1;
			                s.prop1='Category Page';  
			         		s.prop2= omniCategoryL1 + '>' + omniClpName; 
			         		s.prop3=omniCategoryL1 + '>' + omniClpName + '>All';   
			                s.prop4='';  
			                s.prop5='';  
			                s.eVar4= omniCategoryL1;  
			          		s.eVar5= omniCategoryL1 + '>' + omniClpName;  
			                s.eVar6= omniCategoryL1 + '>' + omniClpName + '>All';  
			                s.prop6='${pageContext.request.serverName}';
			                s.eVar9='${pageContext.request.serverName}';
			                fixOmniSpacing();
			                var s_code=s.t();
			                if(s_code)document.write(s_code);
			            }
			        </script>

		 	</jsp:attribute>
		 	<%--Omniture/Band 905(Clp omniture changes for evar5 & evar6) changes for Custom landing page end --%>
		 	
		</bbb:pageContainer>
		</dsp:oparam> <%--CustomLandingDroplet --%>
	</dsp:droplet>
	<%--Fetching appId for Certona Tagging --%>
	<dsp:droplet name="Switch">
	 <dsp:param name="value" bean="Site.id"/>
	 	<dsp:oparam name="BedBathUS">
	 		<c:set var="appIdCertona" scope="request"><bbbc:config key="BedBathUS" configName="CertonaKeys"/></c:set>
	 	</dsp:oparam>
	 	<dsp:oparam name="BedBathCanada">
	 		<c:set var="appIdCertona" scope="request"><bbbc:config key="BedBathCanada" configName="CertonaKeys"/></c:set> 
	 	</dsp:oparam>
	 	<dsp:oparam name="BuyBuyBaby">
	 		<c:set var="appIdCertona" scope="request"><bbbc:config key="BuyBuyBaby" configName="CertonaKeys"/></c:set>
	 	</dsp:oparam>
	 </dsp:droplet>
	 <dsp:droplet name="Switch">
        <dsp:param name="value" bean="Profile.transient" />
        <dsp:oparam name="false">
            <dsp:getvalueof var="userId" bean="Profile.id" />
        </dsp:oparam>
        <dsp:oparam name="true">
            <dsp:getvalueof var="userId" value="" />
        </dsp:oparam>
    </dsp:droplet>
	 	<%--Certona Tagging changes start --%>
    	<script type="text/javascript">
   	       var resx = new Object();
            resx.appid = "${appIdCertona}";
            resx.customerid ="${userId}";
 		</script>
     <%--Certona Tagging changes start --%>	

</dsp:page>

