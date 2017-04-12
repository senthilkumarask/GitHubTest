
<c:set var="section" value="browse" scope="request" />
<c:set var="pageWrapper" value="college useCertonaJs useFB" scope="request" />

<c:set var="pageVariation" value="bc" scope="request" />
<dsp:page>
 <dsp:importbean bean="/atg/multisite/Site"/>
   <dsp:getvalueof var="siteId" bean="Site.id" />
  <c:choose>
 	<c:when test="${siteId eq 'TBS_BedBathCanada' }">
 	<c:set var="titleString" value="University Landing Page" scope="request" />
 	</c:when>
 	<c:otherwise> 
 	<c:set var="titleString" value="College Landing Page" scope="request" />
 	</c:otherwise>
 </c:choose>
 <c:set var="findACollegeLinks_us">
		<bbbc:config key="findACollegeLinks_BedBathUS" configName="ContentCatalogKeys" />
 </c:set>
 <c:set var="findACollegeLinks_ca">
		<bbbc:config key="findACollegeLinks_BedBathCanada" configName="ContentCatalogKeys" />
 </c:set>
    <bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" titleString="${titleString}" pageVariation="${pageVariation}">
			 <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
             <dsp:importbean bean="/com/bbb/cms/droplet/LandingTemplateDroplet" />
             <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
			 <dsp:importbean bean="/atg/userprofiling/Profile" />
			 <dsp:importbean bean="/com/bbb/cms/droplet/PageTabsOrderingDroplet"/>
			 <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
			 <dsp:importbean bean="/atg/userprofiling/Profile" />
             <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />	
             <dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>
	<jsp:attribute name="PageType">CollegeLandingPage</jsp:attribute>
	<c:set var="displayFlag" value="true"/>
    <jsp:body>
		  <dsp:getvalueof var="schoolId" bean="Profile.schoolIds"/>   
		  <dsp:droplet name="LandingTemplateDroplet">
	         <dsp:param name="pageName" value="CollegeLandingPage"/>
	         <dsp:oparam name="output">       
	          <c:if test="${(empty schoolId)}">
		          <dsp:include page="/browse/hero_rotator.jsp" >
	                   <dsp:param name="landingTemplateVO" param="LandingTemplateVO"/>
	               </dsp:include>
	          </c:if>
          
              <div id="content" class="container_12 clearfix" role="main">
                  <dsp:getvalueof var="schoolPromotions" bean="Profile.schoolPromotions"/>
				  <dsp:getvalueof var="heroImage"  vartype="java.util.List" param="LandingTemplateVO.heroImages" scope="request"/>
				  <c:set var="alsoChkOutFlag" value="false"/>
				  <c:set var="topRegistryFlag" value="false"/>
				  
				 	<c:if test="${(not empty schoolId)}">
		          	   <dsp:include page="college_weblink.jsp">
						  <dsp:param name="schoolId" value="${schoolId}"/>
						</dsp:include>
					</c:if>
					<c:if test="${findACollegeLinks_us eq false && currentSiteId eq TBS_BedBathUSSite}">
					<c:set var="bdrBottomThick">bdrBottomThick</c:set>
					</c:if>
					<c:if test="${findACollegeLinks_ca eq false && currentSiteId eq TBS_BedBathCanadaSite}">
					<c:set var="bdrBottomThick">bdrBottomThick</c:set>
					</c:if>
					<dsp:getvalueof var="collegeCategories" param="collegeCategories.subCategories"/>
					 
                     <div class="catIcons grid_12 clearfix marBottom_20">
                        <c:choose>
                            <c:when test="${not empty collegeCategories}">
							<h2><bbbl:label key="lbl_collegelanding_category" language ="${pageContext.request.locale.language}"/></h2>
                                <div class="catIconsList ${bdrBottomThick} <c:if test='${currentSiteId eq TBS_BedBathUSSite}'> marBottom_10</c:if>">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="collegeCategories.subCategories" name="array"/>
			                     	<dsp:oparam name="output">
										<dsp:getvalueof var="categoryName" param="element.categoryName"/>
										<dsp:getvalueof var="categoryImage" param="element.categoryImage"/>
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											<dsp:a iclass="catIconsA" page="${finalUrl}?fromCollege=true" title="${categoryName}">
											<c:choose>
												<c:when test="${empty categoryImage}">
													<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="83" width="83" alt="${categoryName}" />
													<span>${categoryName}</span>
												</c:when>
												<c:otherwise>
													<img src="${categoryImage}" class="noImageFound" alt="${categoryName}" width="83" height="83"/>
													<span>${categoryName}</span>
											     </c:otherwise>
										     </c:choose>
											</dsp:a>
										</dsp:oparam>
										</dsp:droplet>
								   </dsp:oparam>
		                       </dsp:droplet>
							</div>
                                <%--DoubleClick Floodlight START --%>
                                <%-- Commenting out DoubleClick as part of 34473
    		<c:if test="${DoubleClickOn}">
					<c:if test="${(currentSiteId eq TBS_BedBathUSSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_college_bedBathUS" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
                        </c:if>
		    		 <c:if test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_category_baby" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
                        </c:if>
			 		<dsp:include page="/_includes/double_click_tag.jsp">
			 			<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};u4=null;u5=null;u11=${categoryName}"/>
			 		</dsp:include>

		 		</c:if>
		 		--%>
	 		 <%--DoubleClick Floodlight END --%>
                                <c:if test="${findACollegeLinks_us eq true && currentSiteId eq TBS_BedBathUSSite}">
                                <bbbt:textArea key="txt_collegelanding_collegelinkswithcategories" language="${pageContext.request.locale.language}"></bbbt:textArea> 
                                </c:if>  
                                <c:if test="${findACollegeLinks_ca eq true && currentSiteId eq TBS_BedBathCanadaSite}">
                                 <bbbt:textArea key="txt_collegelanding_collegelinkswithcategories" language="${pageContext.request.locale.language}"></bbbt:textArea> 
                                </c:if>
                            </c:when>
                            <c:otherwise>
                            <c:if test="${findACollegeLinks_us eq true && currentSiteId eq TBS_BedBathUSSite}">
                                <bbbt:textArea key="txt_collegelanding_collegelinkswithoutcategories" language="${pageContext.request.locale.language}"></bbbt:textArea>   
                                </c:if>  
                                <c:if test="${findACollegeLinks_ca eq true && currentSiteId eq TBS_BedBathCanadaSite}">
                                 <bbbt:textArea key="txt_collegelanding_collegelinkswithoutcategories" language="${pageContext.request.locale.language}"></bbbt:textArea>   
                                </c:if>    
                            </c:otherwise>
                        </c:choose>
                    </div>

					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="LandingTemplateVO.promoTierLayout1" name="array"/>
						<dsp:oparam name="output">
						<div class="categoryContent grid_12 clearfix">
							<div id="findYourStyle" class="grid_6 alpha">
							     <dsp:include page="/bbregistry/promo_tier_layout_50.jsp">
							    	<dsp:param name="promoTierLayout1" param="LandingTemplateVO.collegePromoBox1"/>
							  	</dsp:include>
							   <MAP NAME="${imageMapName}">${imageMapContent}</MAP>
					  		</div>
							 
							
							<div class="contentDivs grid_3">
							    <dsp:include page="/bbregistry/promo_tier_layout_25.jsp">
								  <dsp:param name="promoTierLayout1" param="LandingTemplateVO.collegePromoBox2"/>
							    </dsp:include>
					            <MAP NAME="${imageMapName}">${imageMapContent}</MAP>
						    </div>
                             
                            
							<c:if test="${FBOn}">
								<div id="fbActivity" class="grid_3 marTop_10 clearfix omega">
								    <c:set var="fbFeedSite"><bbbc:config key="FBLikeCollegeSiteUrl" configName="ThirdPartyURLs" /></c:set>
								    <div class="fb-like-box" data-href="${fbFeedSite}" data-width="229" data-height="348" data-show-faces="false" data-border-color="#cccccc" data-stream="true" data-header="false"></div>
								</div>	                	
		                	</c:if>
						</div>
						</dsp:oparam>
					</dsp:droplet>
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="LandingTemplateVO.promoTierLayout2" name="array"/>
						<dsp:oparam name="output">
						<dsp:getvalueof var="firstPromoContent1" param="element.promoBoxFirstVOList.promoBoxContent" />
						<dsp:getvalueof var="secondPromoContent1" param="element.promoBoxSecondVOList.promoBoxContent" />
						<dsp:getvalueof var="thirdPromoContent1" param="element.promoBoxThirdVOList.promoBoxContent" />
						<div class="categoryPromoImages grid_12 clearfix marBottom_20">
							<div class="promoRow">
								<div class="grid_6 alpha">
								<c:choose>
									<c:when test="${not empty firstPromoContent1}">
										<dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/>
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="imgSrc" param="element.promoBoxFirstVOList.imageURL" />
										<dsp:getvalueof var="imgAltText" param="element.promoBoxFirstVOList.imageAltText" />
										<dsp:getvalueof var="imageLink" vartype="java.lang.String" param="element.promoBoxFirstVOList.imageLink"/>
										<a href="${imageLink}" title="${imgAltText}">
											<img src="${imgSrc}" alt="${imgAltText}" width="478" height="183" />
										</a>
									</c:otherwise>
								</c:choose>

								</div>
								<div class="grid_3">
								<c:choose>
									<c:when test="${not empty secondPromoContent1}">
										<dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/>
									</c:when>
									<c:otherwise>
									<dsp:getvalueof var="imgSrc" param="element.promoBoxSecondVOList.imageURL" />
									<dsp:getvalueof var="imgAltText" param="element.promoBoxSecondVOList.imageAltText" />
									<dsp:getvalueof var="imageLink" vartype="java.lang.String" param="element.promoBoxSecondVOList.imageLink"/>
									<a href="${imageLink}" title="${imgAltText}">
										<img src="${imgSrc}" alt="${imgAltText}" width="229" height="183" />
									</a>
									</c:otherwise>
								</c:choose>	
								</div>
								<div class="grid_3 omega clearfix">
								<c:choose>
									<c:when test="${not empty thirdPromoContent1}">
										<dsp:valueof param="element.promoBoxThirdVOList.promoBoxContent" valueishtml="true"/>
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="imgSrc" param="element.promoBoxThirdVOList.imageURL" />
										<dsp:getvalueof var="imgAltText" param="element.promoBoxThirdVOList.imageAltText" />
										<dsp:getvalueof var="imageLink" vartype="java.lang.String" param="element.promoBoxThirdVOList.imageLink"/>
										<a href="${imageLink}" title="${imgAltText}">
											<img src="${imgSrc}" alt="${imgAltText}" width="229" height="183" />
										</a>
									</c:otherwise>
								</c:choose>	
								</div>								
							</div>
						</div>
						</dsp:oparam>
					</dsp:droplet>
					
					
					<dsp:getvalueof id="appid" bean="Site.id" />
					  
					 <c:set var="CollegeCategory">
						<bbbc:config key="rootCollegeId" configName="ContentCatalogKeys" />
					 </c:set> 
					  
					<dsp:droplet name="ProdToutDroplet">
						<dsp:param value="lastviewed" name="tabList" />
						<dsp:param name="id" value="${CollegeCategory}"/>
						<dsp:param param="siteId" name="siteId" />
						<dsp:oparam name="output">
							 
 	                     <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
							  <dsp:param name="value" param="lastviewedProductsList" />
							   <dsp:oparam name="false">
							  
						  	    <dsp:getvalueof var="lastviewedProductsList"  vartype="java.util.List" param="lastviewedProductsList" /> 
						  	    <dsp:droplet name="ExitemIdDroplet">
						          <dsp:param value="${lastviewedProductsList}" name="lastviewedProductsList" />
						          <dsp:oparam name="output">
								    <dsp:getvalueof var="productList"  vartype="java.util.List" param="productList" />
                                  </dsp:oparam>
						        </dsp:droplet>
						             
						     </dsp:oparam>
						  </dsp:droplet>
                  	</dsp:oparam>
					</dsp:droplet>
					
					  <dsp:droplet name="Switch">
					     <dsp:param name="value" bean="Profile.transient"/>
						   <dsp:oparam name="false">
							<dsp:getvalueof var="userId" bean="Profile.id"/>
							</dsp:oparam>
							
							<dsp:oparam name="true">
								<dsp:getvalueof var="userId" value=""/>
							</dsp:oparam>
					  </dsp:droplet>
					  	  
					 <c:if test= "${CertonaOn}">
					 
					 	<c:set var="topCollegeItemsMax" scope="request">
			  				<bbbc:config key="CollegeTopItemsProdMax" configName="CertonaKeys" />
		    			</c:set>
						<c:set var="alsoCheckProdMax" scope="request">
			  				<bbbc:config key="CollegeAlsoCheckProdMax" configName="CertonaKeys" />
		    			</c:set> 
						<dsp:droplet name="CertonaDroplet">
							<dsp:param name="scheme" value="clp_tci;clp_aco"/>
							<dsp:param name="exitemid" value="${productList}"/>
							<dsp:param name="userid" value="${userId}"/>
							<dsp:param name="siteId" value="${applicationId}"/>
							<dsp:param name="number" value="${topCollegeItemsMax};${alsoCheckProdMax}"/>
							<dsp:oparam name="output">
		                       <dsp:getvalueof var="topRegistryProductsVOsList" param="certonaResponseVO.resonanceMap.${'clp_tci'}.productsVOsList"/>
		                       <dsp:getvalueof var="alsoChkOutProductsVOsList" param="certonaResponseVO.resonanceMap.${'clp_aco'}.productsVOsList"/>
		                       <dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
		                       <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
		                     </dsp:oparam>
		                     <dsp:oparam name="error">
								<c:set var="displayFlag" value="false"/>
							  </dsp:oparam>
											
							  <dsp:oparam name="empty">
								<c:set var="displayFlag" value="false"/>
							   </dsp:oparam>
		                </dsp:droplet>  
		             </c:if>
                  
					<c:if test="${(not empty alsoChkOutProductsVOsList) || (not empty lastviewedProductsList) || (not empty topRegistryProductsVOsList) }">
						<div id="catPTabs" class="categoryProductTabs grid_12 clearfix">
						<c:set var="alsoCheckoutTab"><bbbl:label key="lbl_collegelanding_also_check_out" language ="${pageContext.request.locale.language}"/></c:set>
						<c:set var="lastViewedTab"><bbbl:label key="lbl_collegelanding_lastitems" language ="${pageContext.request.locale.language}"/></c:set>
						<c:set var="topCollegeItemsTab"><bbbl:label key="lbl_collegelanding_top_college_items" language ="${pageContext.request.locale.language}"/></c:set>
					 		<ul class="categoryProductTabsLinks">
							 	<dsp:droplet name="PageTabsOrderingDroplet">
									<dsp:param value="College Landing Page" name="pageName" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="tabList"  vartype="java.util.List" param="pageTab" />
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param value="${tabList}" name="array" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="tabName" param="element" />
												<c:choose>
													<c:when test="${tabName eq alsoCheckoutTab}">
														<c:if test="${displayFlag eq true}">
													    	<dsp:droplet name="Switch">
																<dsp:param param="LandingTemplateVO.alsoCheckOutFlag" name="value"/>
															    <dsp:oparam name="true">
																<c:if test="${not empty alsoChkOutProductsVOsList }">
																    <c:set var="alsoChkOutFlag" value="true"/>
																    <li><div class="arrowSouth"></div><a title="${alsoCheckoutTab}" href="#categoryProductTabs-tabs1">${alsoCheckoutTab}</a></li>
																</c:if>
															</dsp:oparam>
														   </dsp:droplet>
													    </c:if>					
													</c:when>
													<c:when test="${tabName eq lastViewedTab}">
														<c:if test="${not empty lastviewedProductsList}">
												      		<li><div class="arrowSouth"></div><a title="${lastViewedTab}" href="#categoryProductTabs-tabs2">${lastViewedTab}</a></li>
													 	</c:if>
													</c:when>
													<c:when test="${tabName eq topCollegeItemsTab}">
														<c:if test="${displayFlag eq true}">
														 	<dsp:droplet name="Switch">
															 	<dsp:param param="LandingTemplateVO.topCollegeItemsFlag" name="value"/>
																<dsp:oparam name="true">
																   <c:if test="${not empty topRegistryProductsVOsList }">
																   <c:set var="topRegistryFlag" value="true"/>
																	 <li><div class="arrowSouth"></div><a title="${topCollegeItemsTab}" href="#categoryProductTabs-tabs3">${topCollegeItemsTab}</a></li>
																   </c:if>
																</dsp:oparam>
															</dsp:droplet>
														</c:if>
													</c:when>
												</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</ul>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param value="${tabList}" name="array" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="tabName" param="element"/>
									<c:choose>
										<c:when test="${tabName eq alsoCheckoutTab}">
											<c:if test="${alsoChkOutFlag eq true }">
												<div id="categoryProductTabs-tabs1" class="categoryProductTabsData">
												    <dsp:include page="/browse/certona_prod_carousel.jsp">
													  	<dsp:param name="productsVOsList" value="${alsoChkOutProductsVOsList}"/>								  	
														<dsp:param name="desc" value="Also Checkout (college)"/>
													  	<dsp:param name="crossSellFlag" value="true"/> 
													</dsp:include>
										        </div>
									        </c:if>				
										</c:when>
										<c:when test="${tabName eq lastViewedTab}">
											<c:if test="${not empty lastviewedProductsList}">
										        <div id="categoryProductTabs-tabs2" class="categoryProductTabsData">
									               <dsp:include page="/browse/certona_prod_carousel.jsp">
														<dsp:param name="productsVOsList" value="${lastviewedProductsList}"/>
														<dsp:param name="desc" value="Last Viewed (college)"/>
														<dsp:param name="crossSellFlag" value="true"/> 
												   </dsp:include>
											    </div>
											</c:if>  
										</c:when>
										<c:when test="${tabName eq topCollegeItemsTab}">
											<c:if test="${topRegistryFlag eq true }">
												 <div id="categoryProductTabs-tabs3" class="categoryProductTabsData">
												    <dsp:include page="/browse/certona_prod_carousel.jsp">
													  	<dsp:param name="productsVOsList" value="${topRegistryProductsVOsList}"/>
													  	<dsp:param name="desc" value="Top College Items (college)"/>
													  	<dsp:param name="crossSellFlag" value="true"/> 
													</dsp:include>
											     </div>
											</c:if>
										</c:when>
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
						</div>
					</c:if>
					<div class="grid_12 alpha omega clearfix blurbTxt">
	                <bbbt:textArea key="txt_seo_content_college" language="${pageContext.request.locale.language}" /> 
	                </div>
				 </div>
			</dsp:oparam>
	      	</dsp:droplet>
		<script type="text/javascript">
			var resx = new Object();
			resx.appid = "${appIdCertona}";
			resx.links = '${linksCertona}'+'${productList}';
			resx.pageid = "${pageIdCertona}";
			resx.customerid = "${userId}";
		</script>	
	</jsp:body>
	<jsp:attribute name="footerContent">
		<script type="text/javascript">   
			if(typeof s !=='undefined') {
				s.channel='College';
				s.pageName='College>${titleString}';// pagename
				s.prop1='College';
				s.prop2='College'; 
	  			s.prop3='College';
	  			s.prop6='${pageContext.request.serverName}'; 
	  			s.eVar9='${pageContext.request.serverName}';
	  			var s_code=s.t();
	  			if(s_code)document.write(s_code);		
			}
		</script>
	</jsp:attribute>
	</bbb:pageContainer>		
</dsp:page>