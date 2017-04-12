
<c:set var="section" value="browse" scope="request" />
<c:set var="pageWrapper" value="college useCertonaAjax useFB" scope="request" />

<c:set var="pageVariation" value="bc" scope="request" />
<dsp:page>
 <dsp:importbean bean="/atg/multisite/Site"/>
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
          
              <div id="content" class="container_12 clearfix college" role="main">
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
                     
                     <div class="catIcons small-12 medium-2 columns right">
                    </div>
 					<!-- <h3 class="divider college"></h3> -->
                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                        <dsp:param param="LandingTemplateVO.promoTierLayout1" name="array"/>
                        <dsp:oparam name="output">
                        <div class="categoryContent small-12 medium-10 columns">
                            <div id="findYourStyle" class="small-12 medium-8 columns">
                                 <dsp:include page="/giftregistry/promo_tier_layout_50.jsp">
                                    <dsp:param name="promoTierLayout1" param="LandingTemplateVO.collegePromoBox1"/>
                                </dsp:include>
                               <MAP NAME="${imageMapName}">${imageMapContent}</MAP>
                            </div>
                             
                            
                            <div class="contentDivs columns small-12 medium-4">
                                <dsp:include page="/giftregistry/promo_tier_layout_25.jsp">
                                  <dsp:param name="promoTierLayout1" param="LandingTemplateVO.collegePromoBox2"/>
                                </dsp:include>
                                <MAP NAME="${imageMapName}">${imageMapContent}</MAP>
                            </div>
                            
                        </div>
                        </dsp:oparam>
                    </dsp:droplet>
                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                        <dsp:param param="LandingTemplateVO.promoTierLayout2" name="array"/>
                        <dsp:oparam name="output">
                        <dsp:getvalueof var="firstPromoContent1" param="element.promoBoxFirstVOList.promoBoxContent" />
                        <dsp:getvalueof var="secondPromoContent1" param="element.promoBoxSecondVOList.promoBoxContent" />
                        <dsp:getvalueof var="thirdPromoContent1" param="element.promoBoxThirdVOList.promoBoxContent" />
                        <div class="categoryPromoImages row">
                           	  <c:choose>
                                  <c:when test="${not empty firstPromoContent1}">
                                      <dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/>
                                  </c:when>
                                  <c:otherwise>
                                      <dsp:getvalueof var="imgSrc" param="element.promoBoxFirstVOList.imageURL" />
                                      <dsp:getvalueof var="imgAltText" param="element.promoBoxFirstVOList.imageAltText" />
                                      <dsp:getvalueof var="imageLink" vartype="java.lang.String" param="element.promoBoxFirstVOList.imageLink"/>
                                      <a href="${imageLink}" title="${imgAltText}">
                                          <img src="${imgSrc}" alt="${imgAltText}" class="stretch" />
                                      </a>
                                  </c:otherwise>
                              </c:choose>
							
			                                
                                <c:choose>
                                    <c:when test="${not empty secondPromoContent1}">
                                        <dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/>
                                    </c:when>
                                    <c:otherwise>
                                    <dsp:getvalueof var="imgSrc" param="element.promoBoxSecondVOList.imageURL" />
                                    <dsp:getvalueof var="imgAltText" param="element.promoBoxSecondVOList.imageAltText" />
                                    <dsp:getvalueof var="imageLink" vartype="java.lang.String" param="element.promoBoxSecondVOList.imageLink"/>
                                    <a href="${imageLink}" title="${imgAltText}">
                                        <img src="${imgSrc}" alt="${imgAltText}" class="stretch" />
                                    </a>
                                    </c:otherwise>
                                </c:choose> 
                              
                                <c:choose>
                                    <c:when test="${not empty thirdPromoContent1}">
                                        <dsp:valueof param="element.promoBoxThirdVOList.promoBoxContent" valueishtml="true"/>
                                    </c:when>
                                    <c:otherwise>
                                        <dsp:getvalueof var="imgSrc" param="element.promoBoxThirdVOList.imageURL" />
                                        <dsp:getvalueof var="imgAltText" param="element.promoBoxThirdVOList.imageAltText" />
                                        <dsp:getvalueof var="imageLink" vartype="java.lang.String" param="element.promoBoxThirdVOList.imageLink"/>
                                        <a href="${imageLink}" title="${imgAltText}">
                                            <img src="${imgSrc}" alt="${imgAltText}" class="stretch" />
                                        </a>
                                    </c:otherwise>
                                </c:choose> 
                                </div> 
                                </div>                             
                        </dsp:oparam>
                    </dsp:droplet>
                    
                    
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
                          
                      
                    
                    <div class="small-12 columns blurbTxt">
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