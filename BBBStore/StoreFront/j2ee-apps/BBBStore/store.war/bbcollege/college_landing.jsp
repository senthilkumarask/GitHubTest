
<c:set var="section" value="browse" scope="request" />
<c:set var="pageWrapper" value="college useCertonaAjax useFB" scope="request" />
<c:set var="pageVariation" value="bc" scope="request" />
<dsp:page>
 <dsp:importbean bean="/atg/multisite/Site"/>
 <dsp:importbean bean="/atg/userprofiling/Profile" />
 <dsp:importbean bean="/com/bbb/commerce/catalog/droplet/SchoolLookupDroplet" />
 <dsp:getvalueof var="schoolId" bean="Profile.schoolIds"/>
 <c:set var="isCollegeSelected" scope="request">${param.stop_mobi}</c:set> 
 <dsp:getvalueof var="siteId" bean="Site.id" />
 <c:choose>
 	<c:when test="${siteId eq 'BedBathCanada' }">
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
<c:if test="${not empty omniSchoolId}"> 
<dsp:droplet name="SchoolLookupDroplet">
	  <dsp:param name="schoolId" value="${omniSchoolId}"/>
	  <dsp:oparam name="output"> 
	  <dsp:getvalueof var="omniSchoolName" param="SchoolVO.schoolName" />
	  <input type='hidden' value='${omniSchoolName}' name='omniSchoolName'/>
	  </dsp:oparam>
</dsp:droplet>
</c:if>
    <bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" titleString="${titleString}" pageVariation="${pageVariation}">
			 <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
             <dsp:importbean bean="/com/bbb/cms/droplet/LandingTemplateDroplet" />
          	 <dsp:importbean bean="/atg/userprofiling/Profile" />
			 <dsp:importbean bean="/com/bbb/cms/droplet/PageTabsOrderingDroplet"/>
			 <dsp:importbean bean="/atg/userprofiling/Profile" />
           
	<dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />	
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
					<c:if test="${findACollegeLinks_us eq false && currentSiteId eq BedBathUSSite}">
					<c:set var="bdrBottomThick">bdrBottomThick</c:set>
					</c:if>
					<c:if test="${findACollegeLinks_ca eq false && currentSiteId eq BedBathCanadaSite}">
					<c:set var="bdrBottomThick">bdrBottomThick</c:set>
					</c:if>
					<dsp:getvalueof var="collegeCategories" param="collegeCategories.subCategories"/>
					 
                     <div class="catIcons grid_12 clearfix marBottom_20">
                        <c:choose>
                            <c:when test="${not empty collegeCategories}">
							<h2><bbbl:label key="lbl_collegelanding_category" language ="${pageContext.request.locale.language}"/></h2>
                                <div class="catIconsList ${bdrBottomThick} <c:if test='${currentSiteId eq BedBathUSSite}'> marBottom_10</c:if>">
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
													<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="83" width="83" alt="image of ${categoryName}" />
													<span>${categoryName}</span>
												</c:when>
												<c:otherwise>
													<img src="${categoryImage}" class="noImageFound" alt="image of ${categoryName}" width="83" height="83"/>
													<span>${categoryName}</span>
											     </c:otherwise>
										     </c:choose>
											</dsp:a>
										</dsp:oparam>
										</dsp:droplet>
								   </dsp:oparam>
		                       </dsp:droplet>
							</div>
                <%-- BBBSL-4343 DoubleClick Floodlight START 
    			<c:if test="${DoubleClickOn}">
					<c:if test="${(currentSiteId eq BedBathUSSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_college_bedBathUS" configName="RKGKeys" /></c:set>
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
			 			<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};u4=null;u5=null;u11=${categoryName}"/>
			 		</dsp:include>

		 		</c:if>
	 		 DoubleClick Floodlight END --%>
                                <c:if test="${findACollegeLinks_us eq true && currentSiteId eq BedBathUSSite}">
                                <bbbt:textArea key="txt_collegelanding_collegelinkswithcategories" language="${pageContext.request.locale.language}"></bbbt:textArea> 
                                </c:if>  
                                <c:if test="${findACollegeLinks_ca eq true && currentSiteId eq BedBathCanadaSite}">
                                 <bbbt:textArea key="txt_collegelanding_collegelinkswithcategories" language="${pageContext.request.locale.language}"></bbbt:textArea> 
                                </c:if>
                            </c:when>
                            <c:otherwise>
                            <c:if test="${findACollegeLinks_us eq true && currentSiteId eq BedBathUSSite}">
                                <bbbt:textArea key="txt_collegelanding_collegelinkswithoutcategories" language="${pageContext.request.locale.language}"></bbbt:textArea>   
                                </c:if>  
                                <c:if test="${findACollegeLinks_ca eq true && currentSiteId eq BedBathCanadaSite}">
                                 <bbbt:textArea key="txt_collegelanding_collegelinkswithoutcategories" language="${pageContext.request.locale.language}"></bbbt:textArea>   
                                </c:if>    
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <div id="openContainer">
                     <dsp:getvalueof var="promoBoxContent" param="LandingTemplateVO.promoBoxContentVO.promoBoxContent" />
					
					   <c:if test="${!(empty promoBoxContent)}">
							
							<dsp:getvalueof var="catCSSFile" param="LandingTemplateVO.promoBoxContentVO.promoBoxCssFilePath" />
					    	<dsp:getvalueof var="catJSFile" param="LandingTemplateVO.promoBoxContentVO.promoBoxJsFilePath" />
							<script type="text/javascript" src="${catJSFile}"></script>
							<link rel="stylesheet" type="text/css" href="${catCSSFile}"/>
							<div id="promoBoxContent" class="marBottom_10 marLeft_10 clearfix">
								${promoBoxContent}
							</div>
						</c:if>
					</div>
					
					<dsp:getvalueof id="appid" bean="Site.id" />
					  
					 <c:set var="CollegeCategory">
						<bbbc:config key="rootCollegeId" configName="ContentCatalogKeys" />
					 </c:set> 
					  
					  <dsp:droplet name="Switch">
					     <dsp:param name="value" bean="Profile.transient"/>
						   <dsp:oparam name="false">
							<dsp:getvalueof var="userId" bean="Profile.id"/>
							</dsp:oparam>
							
							<dsp:oparam name="true">
								<dsp:getvalueof var="userId" value=""/>
							</dsp:oparam>
					  </dsp:droplet>
					  	  
					  
		             <%-- Ajax Call --%>
		          
		            <c:set var="cert_categoryId" scope="request"><bbbc:config key="rootCollegeId" configName="ContentCatalogKeys" /></c:set>    
		            <c:set var="cert_topCollegeItemsMax" scope="request"><bbbc:config key="CollegeTopItemsProdMax" configName="CertonaKeys" /></c:set>
					<c:set var="cert_alsoCheckProdMax" scope="request"><bbbc:config key="CollegeAlsoCheckProdMax" configName="CertonaKeys" /></c:set> 
					<c:set var="cert_alsoCheckoutTabLbl"><bbbl:label key="lbl_collegelanding_also_check_out" language ="${pageContext.request.locale.language}"/></c:set>
					<c:set var="cert_lastViewedTabLbl"><bbbl:label key="lbl_collegelanding_lastitems" language ="${pageContext.request.locale.language}"/></c:set>
					<c:set var="cert_topCollegeItemsTabLbl"><bbbl:label key="lbl_collegelanding_top_college_items"  language="${pageContext.request.locale.language}"/></c:set>
		            <c:set var="cert_scheme" scope="request">clp_tci;clp_aco</c:set>
                   
                    <c:set var="cert_number" scope="request">${cert_topCollegeItemsMax};${cert_alsoCheckProdMax}</c:set>
                    <c:set var="cert_pageName" scope="request">College Landing Page</c:set>
                    <c:set var="cert_omniCrossSellPageName" scope="request">(college page)</c:set>
                    <c:set var="cert_alsoCheckOutFlag" scope="request"><dsp:valueof param="LandingTemplateVO.alsoCheckOutFlag" /></c:set>
                    <c:set var="cert_topCollegeItemsFlag" scope="request"><dsp:valueof param="LandingTemplateVO.topCollegeItemsFlag" /></c:set>
		            <c:set var="cert_flagFunNewProducts" scope="request">false</c:set> 
		            <c:set var="cert_bottomTabs" scope="request">true</c:set>
		            <c:set var="cert_linksCertonaNonRecomm" scope="request"><dsp:valueof param="linkString" /></c:set>
		             <div id="certonaBottomTabs" class="clearfix loadAjaxContent" 
                        data-ajax-url="${contextPath}/common/certona_slots.jsp" 
                        data-ajax-target-divs="#certonaBottomTabs,#requestResponse" 
                        data-ajax-params-count="14" 
                        data-ajax-param1-name="categoryId" data-ajax-param1-value="${cert_categoryId}" 
                        data-ajax-param2-name="scheme" data-ajax-param2-value="${cert_scheme}" 
                        data-ajax-param3-name="number" data-ajax-param3-value="${cert_number}" 
                        data-ajax-param4-name="alsoCheckoutTabLbl" data-ajax-param4-value="${cert_alsoCheckoutTabLbl}" 
                        data-ajax-param5-name="lastViewedTabLbl" data-ajax-param5-value="${cert_lastViewedTabLbl}" 
                        data-ajax-param6-name="topCollegeItemsTabLbl" data-ajax-param6-value="${cert_topCollegeItemsTabLbl}"
                        data-ajax-param7-name="alsoCheckOutFlag" data-ajax-param7-value="${cert_alsoCheckOutFlag}" 
                        data-ajax-param8-name="topCollegeItemsFlag" data-ajax-param8-value="${cert_topCollegeItemsFlag}" 
                        data-ajax-param9-name="omniCrossSellPageName" data-ajax-param9-value="${cert_omniCrossSellPageName}" 
                        data-ajax-param10-name="certonaPageName" data-ajax-param10-value="${cert_pageName}" 
                        data-ajax-param11-name="funNewProductsFlagParam" data-ajax-param11-value="${cert_flagFunNewProducts}" 
                        data-ajax-param12-name="certonaBottomTabsFlagParam" data-ajax-param12-value="${cert_bottomTabs}" 
                        data-ajax-param13-name="linksCertonaNonRecomm" data-ajax-param13-value="${cert_linksCertonaNonRecomm}"
                        data-ajax-param14-name="certonaSwitch" data-ajax-param14-value="${CertonaOn}"  
                    role="complementary">
                        <div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                    </div>
                    
                    <%--BBBSL-6574 | Printing Certona WS call on source --%>
                    <div id="requestResponse" class="hidden">
                    </div>
					
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
		var omniSchoolName = '${omniSchoolName}';
		var isCollegeSelected = '${isCollegeSelected}';
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