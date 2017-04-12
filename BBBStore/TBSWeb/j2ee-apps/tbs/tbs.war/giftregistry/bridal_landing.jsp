 <dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile" />
     <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
     <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>    
     <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
     <dsp:importbean bean="/atg/multisite/Site" />
     <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
     <dsp:importbean bean="/com/bbb/cms/droplet/LandingTemplateDroplet" />   
     <dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
     <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />
     <dsp:getvalueof var="appid" bean="Site.id" />
     <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/> 
     <dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
     
     <c:set var="section" value="browse" scope="request" />
     <c:set var="pageWrapper" value="bridalLanding useCertonaAjax useFB useAdobeActiveContent" scope="request" />
     <c:set var="titleString" value="Bed Bath &amp; Beyond - Bridal Landing" scope="request" />
     <c:set var="pageVariation" value="br" scope="request" />
     <c:set var="topRegistryItemFlag" value="false"/>
     <c:set var="displayFlag" value="true"/>
     
        <bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" titleString="${titleString}" pageVariation="${pageVariation}">
        <jsp:attribute name="PageType">RegistryLanding</jsp:attribute>
        <jsp:attribute name="bodyClass">bridalLanding useCertonaAjax useFB useAdobeActiveContent</jsp:attribute>
        <jsp:body>
        <c:choose>
            <c:when test="${minifiedCSSFlag == 'true'}">
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbregistry/css/theme.min.css?v=${buildRevisionNumber}" />
            </c:when>
            <c:otherwise>
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbregistry/css/theme.css?v=${buildRevisionNumber}" />
            </c:otherwise>
        </c:choose>
      <dsp:getvalueof id="currentSiteId" bean="Site.id" />
      <dsp:getvalueof var="transient" bean="Profile.transient"></dsp:getvalueof>
      <c:if test='${!transient}'><c:set var="loggedStatusCss">loggedInNew</c:set></c:if>    
      <dsp:droplet name="GiftRegistryFlyoutDroplet">
            <dsp:param name="profile" bean="Profile" />
                <dsp:oparam name="output">
                   <dsp:getvalueof var="userStatus" param="userStatus"></dsp:getvalueof>	 
                   <dsp:getvalueof var="registrySummaryVO" param="registrySummaryVO"></dsp:getvalueof>	  
            </dsp:oparam>
      </dsp:droplet>
      
      <dsp:droplet name="Switch">
       		<dsp:param name="value" value="${userStatus}" />
			<dsp:oparam name="1">
				<c:set var="loggedStatusCss">loggedOut</c:set>
			</dsp:oparam>  
			<dsp:oparam name="2">
                 <c:set var="loggedStatusCss">loggedInNew</c:set>  
       		</dsp:oparam>   
            <dsp:oparam name="4">
                 <c:set var="loggedStatusCss">loggedInMultiple</c:set> 
            </dsp:oparam>
            <dsp:oparam name="3">
                 <c:set var="loggedStatusCss">loggedInSingle</c:set> 
            </dsp:oparam>	
       </dsp:droplet>
       
        
        
        <%-- <dsp:include page="bridal_sub_header_us_canada.jsp"/>   --%>
       
<div id="heroContent" class="${loggedStatusCss} row">
         <%-- <dsp:getvalueof var="regbridal" param="regbridal" />
         <dsp:getvalueof var="regType" param="regType" />
         <c:choose>
              <c:when test="${regbridal eq true }">
              <bbbt:textArea key="txt_${regType}_registry_landing" language ="${pageContext.request.locale.language}"/>
               </c:when>
              <c:otherwise>
                <dsp:include page="bridal_landing_registry.jsp"/>
              </c:otherwise>
            </c:choose>--%>
             <dsp:include page="bridal_landing_registry.jsp">
             	<dsp:param name="userStatus" value="${userStatus}"/>
             	<dsp:param name="registrySummaryVO" value="${registrySummaryVO}"/>
             </dsp:include>
        
        <%-- <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
            <dsp:param name="value" param="regbridal"/>
            <dsp:oparam name="false">
                <bbbt:textArea key="txt_${regType}_registry_FPO_landing" language ="${pageContext.request.locale.language}"/>
            </dsp:oparam>
         </dsp:droplet>
        --%>
        
         
         <dsp:droplet name="LandingTemplateDroplet">
          <dsp:param name="pageName" value="RegistryLandingPage"/>
          <dsp:oparam name="output"> 
          <dsp:getvalueof var="LandingTemplateVO" param="LandingTemplateVO"/>
                <c:choose>
                   <c:when test="${regbaby eq true }">
                       <dsp:include page="mul_registry_logged.jsp">
                        <dsp:param name="registryCategoryImage" param="LandingTemplateVO.registryCategoryImage"/>
                       </dsp:include>
                   </c:when>
                   <c:otherwise>
                   
                    <dsp:droplet name="Switch">
		    <dsp:param name="value" param="${userStatus}"/>
		    <dsp:oparam name="3">
		    	<dsp:include page="mul_registry_logged_bridal.jsp">	
				<dsp:param name="registryCategoryImage" param="LandingTemplateVO.registryCategoryImage"/>
			</dsp:include>
		    </dsp:oparam>
		    <dsp:oparam name="4">
		    	<dsp:include page="mul_registry_logged_bridal.jsp">	
				<dsp:param name="registryCategoryImage" param="LandingTemplateVO.registryCategoryImage"/>
			</dsp:include>
		    </dsp:oparam>
		    </dsp:droplet>
                   </c:otherwise>
               </c:choose>  
                  
            </div>     
            <dsp:getvalueof param="LandingTemplateVO.bridalTool" var="bridalTool"/>
                 <c:if test="${not empty bridalTool}">
                 <div class="row cb">
                    <div class="catTabsData prodIconsCarousal">
                        <div class="carousel clearfix">
                        <div class="carouselBody">
                        
                        <dsp:getvalueof var="bridalToolList" param="LandingTemplateVO.bridalTool"/>
                                                        
                            <c:choose>
                             <c:when test="${fn:length(bridalToolList)<=6}"> 
                            <c:choose>
                                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                    <c:set var="bridalCA">bridalCA</c:set>
                                </c:when>
                            </c:choose>
                            <div class="bridalCarousalCA">
                                <ul class="${bridalCA} small-12 columns no-padding">
                            <dsp:droplet name="ForEach">
                             <dsp:param param="LandingTemplateVO.bridalTool" name="array"/>
                             <dsp:oparam name="output">
                                 
                             <c:set var="title"><dsp:valueof param="element.linkLabel" valueishtml="true"/></c:set>
                                <dsp:getvalueof var="iconImageURL" param="element.imageUrl"/>
                                <c:choose>
                                <c:when test="${title eq 'Bridal Toolkit'}">
                                    <li class="noCarousel small-4 large-2 columns no-padding"><a title="<dsp:valueof param="element.imageAltText"/>" href="${contextPath}/giftregistry/registry_features.jsp?pageName=BridalToolkit">
                                        <c:choose>
                                            <c:when test="${(fn:indexOf(iconImageURL, 'http') == 0) || (fn:indexOf(iconImageURL, '//') == 0) || (fn:indexOf(iconImageURL, '/_assets/') == 0)}">
                                                <img class="small-2 columns no-padding" width="36" height="38" src="${iconImageURL}" />
                                            </c:when>
                                            <c:otherwise>
                                                <img class="small-2 columns no-padding" width="36" height="38" src="${scene7Path}/${iconImageURL}" />
                                            </c:otherwise>
                                        </c:choose>
                                        
                                        <p class="small-10 columns left no-padding">${title}</p>
                                        </a> 
                                    </li>                                   
                                </c:when>
                                <c:otherwise>
                                    <li class="noCarousel small-4 large-2 columns no-padding" ><a title="<dsp:valueof param="element.imageAltText"/>" href="<dsp:valueof param="element.linkUrl"/>">
                                        <c:choose>
                                            <c:when test="${(fn:indexOf(iconImageURL, 'http') == 0) || (fn:indexOf(iconImageURL, '//') == 0) || (fn:indexOf(iconImageURL, '/_assets/') == 0)}">
                                                <img class="small-2 columns no-padding" src="${iconImageURL}"  />
                                            </c:when>
                                            <c:otherwise>
                                                <img class="small-2 columns no-padding" src="${scene7Path}/${iconImageURL}" />
                                            </c:otherwise>
                                        </c:choose>
                                        <p class="small-10 columns left prodIcons no-padding">${title}</p>
                                        </a> 
                                    </li>
                                </c:otherwise>
                                </c:choose>
                                
                                </dsp:oparam>
                            </dsp:droplet>
                            </ul>
                            </div>
                            </c:when>
                            
                            <c:otherwise>
                            <div class="bridalCarousalActive">
                            <div class="grid_1 carouselArrow omega carouselArrowPrevious clearfix ">
                                &nbsp;
                                <a class="carouselScrollPrevious" title="Previous" href="#"><bbbl:label key="lbl_blanding_previous" language ="${pageContext.request.locale.language}"/></a>
                            </div>
                            
                            
                            <div class="carouselContent clearfix ">
                                <ul class="prodGridRow">
                            <dsp:droplet name="ForEach">
                             <dsp:param param="LandingTemplateVO.bridalTool" name="array"/>
                             <dsp:oparam name="output">
                                <dsp:getvalueof var="title" param="element.linkLabel"/>
                                <c:choose>
                                <c:when test="${title eq 'Bridal Toolkit'}">
                                    <li><a title="${title}" href="${contextPath}/giftregistry/registry_features.jsp?pageName=BridalToolkit">
                                        <img width="36" height="38" src="${imagePath}<dsp:valueof param="element.imageUrl"/>" alt="<dsp:valueof param="element.imageAltText"/>" />
                                        <p class="grid_1"><dsp:valueof param="element.linkLabel"/></p>
                                        </a> 
                                    </li>                                   
                                </c:when>
                                <c:otherwise>
                                    <li ><a title="${title}" href="<dsp:valueof param="element.linkUrl"/>">
                                        <img src="${imagePath}<dsp:valueof param="element.imageUrl"/>" alt="<dsp:valueof param="element.imageAltText"/>" />
                                        <p class="grid_1 prodIcons"><dsp:valueof param="element.linkLabel"/></p>
                                        </a> 
                                    </li>
                                </c:otherwise>
                                </c:choose>
                                
                                </dsp:oparam>
                            </dsp:droplet>
                            </ul>
                            </div>
                            
                            
                            
                            <div class="grid_1 carouselArrow alpha carouselArrowNext clearfix ">
                                &nbsp;
                                <a class="carouselScrollNext" title="Next" href="#"><bbbl:label key="lbl_blanding_next" language ="${pageContext.request.locale.language}"/></a>
                            </div>
                            </div>
                            </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="carouselPages hidden">
                            <div class="carouselPageLinks clearfix">
                                <a title="Page 1" class="selected" href="#">1</a>
                                <a title="Page 2" href="#">2</a>
                                <a title="Page 3" href="#">3</a>
                                <a title="Page 4" href="#">4</a>
                            </div>
                        </div>
                    </div>
                    </div>
                </div>
                </c:if>
             <div id="content" class="row bridalLanding" role="main">   
                <div class="small-12 columns flashRow no-padding">
                    <div class="small-12 large-6 columns">
                      
                          <div id="flashBannerWrapper">
                         <c:choose>
                           <c:when test="${LandingTemplateVO.marketingBannerUrl eq null}">
                            <img src="${imagePath}/_assets/bbregistry/images/waterford.jpg" alt="Waterford" width="478" height="302" />
                           </c:when>
                           <c:otherwise>
                            <%-- TODO: empty string check required 
                            if ... <dsp:valueof param='LandingTemplateVO.marketingBannerUrl'/>
                            is empty then the following script and noscript tag should not be rendered 
                            and instead the following div should be rendered
                            <div class="noFlashImage">
                                <img src="${imagePath}/_assets/bbregistry/images/waterford.jpg" alt="Waterford" width="478" height="302" />
                            </div>
                            
                            ALSO ... the url for the flash movie SHOULD NOT CONTAIN ".swf" at the end
                            --%>
                            <script type="text/javascript">
                                <!--
                                if (AC_FL_RunContent == 0 || DetectFlashVer == 0) {
                                    alert("This page requires AC_RunActiveContent.js.");
                                } else {
                                    // Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
                                    var hasProductInstall = DetectFlashVer(6, 0, 65);

                                    // Version check based upon the values defined in globals
                                    var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);

                                    if ( hasProductInstall && !hasRequestedVersion ) {
                                        // DO NOT MODIFY THE FOLLOWING FOUR LINES
                                        // Location visited after installation is complete if installation is required
                                        var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
                                        var MMredirectURL = window.location;
                                        document.title = document.title.slice(0, 47) + " - Flash Player Installation";
                                        var MMdoctitle = document.title;

                                        AC_FL_RunContent(
                                            "src", "${imagePath}/_assets/global/flash_assets/playerProductInstall",
                                            "FlashVars", "MMredirectURL="+MMredirectURL+'&MMplayerType='+MMPlayerType+'&MMdoctitle='+MMdoctitle+"",
                                            "width", "478",
                                            "height", "302",
                                            "align", "middle",
                                            "id", "flashBanner",
                                            "quality", "high",
                                            "bgcolor", "#869ca7",
                                            "name", "flashBanner",
                                            "allowScriptAccess","sameDomain",
                                            "wmode","transparent",
                                            "type", "application/x-shockwave-flash",
                                            "pluginspage", "http://www.adobe.com/go/getflashplayer"
                                        );
                                    } else if ( hasRequestedVersion ) {
                                        // if we've detected an acceptable version
                                        // embed the Flash Content SWF when all tests are passed
                                        AC_FL_RunContent(
                                                "src", "<dsp:valueof param='LandingTemplateVO.marketingBannerUrl'/>",
                                                "width", "478",
                                                "height", "302",
                                                "align", "middle",
                                                "id", "flashBanner",
                                                "quality", "high",
                                                "bgcolor", "#869ca7",
                                                "name", "flashBanner",
                                                "allowScriptAccess","sameDomain",
                                                "wmode","transparent",
                                                "type", "application/x-shockwave-flash",
                                                "pluginspage", "http://www.adobe.com/go/getflashplayer"
                                        );
                                    } else {  // flash is too old or we can't detect the plugin
                                         $('#flashBannerWrapper').append('<a href="http://get.adobe.com/flashplayer"><img src="${imagePath}/_assets/global/images/upgradeFlashPlayer.jpg" alt="Upgrade Your Flash Player" width="478" height="302" /></a>');
                                    }
                                }
                                // -->
                            </script>
                            <noscript>
                                <a href="http://get.adobe.com/flashplayer"><img src="/_assets/global/images/upgradeFlashPlayer.jpg" alt="Upgrade Your Flash Player" width="478" height="302" /></a>
                            </noscript>
                            </c:otherwise>
                            </c:choose>
                          
                        </div>
                     
                    </div>
                    <c:if test="${FBOn}">
                        <div id="fbActivity" class="small-12 large-3 columns omega fbActivity right">
                            <c:set var="fbFeedSite"><bbbc:config key="FBLikeBridalSiteUrl" configName="ThirdPartyURLs" /></c:set>
                            <div class="fb-like-box" data-href="${fbFeedSite}" class="stretch" data-width="229" data-height="302" data-show-faces="false" data-border-color="#cccccc" data-stream="true" data-header="false"></div>
                        </div>
                    </c:if>
                    <div class="small-12 large-3 columns">
                         <dsp:valueof param="LandingTemplateVO.promoSmallContent" valueishtml="true"/>
                    </div>
                 </div>
                
            <dsp:droplet name="ForEach">
              <dsp:param param="LandingTemplateVO.promoTierLayout1" name="array"/>
               <dsp:oparam name="output">
                 <div class="row">
                        <div class="small-12 large-6 columns">
                           <dsp:include page="promo_tier_layout_50.jsp">
                              <dsp:param name="promoTierLayout1" param="element.promoBoxFirstVOList"/>
                            </dsp:include>
                        </div>
                        <MAP NAME="${imageMapName}">
                          ${imageMapContent} 
                        </MAP>
                        <div class="small-12 large-3 columns">
                           <dsp:include page="promo_tier_layout_25.jsp">
                               <dsp:param name="promoTierLayout1" param="element.promoBoxSecondVOList"/>
                           </dsp:include>
                        </div>
                        <MAP NAME="${imageMapName}">
                         ${imageMapContent} 
                         </MAP>
                       <div class="small-12 large-3 columns"> 
                           <dsp:include page="promo_tier_layout_25.jsp">
                              <dsp:param name="promoTierLayout1" param="element.promoBoxThirdVOList"/>
                           </dsp:include>
                        </div>
                       <MAP NAME="${imageMapName}">
                         ${imageMapContent} 
                      </MAP>
                </div>
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
            
            <dsp:getvalueof var="keepsakeShopList" vartype="java.util.List" param="LandingTemplateVO.keepsakeShop" />
            <dsp:getvalueof var="brandList" vartype="java.util.List" param="LandingTemplateVO.brands" />
            <dsp:getvalueof var="topRegistryItemsFlag" param="LandingTemplateVO.topRegistryItemsFlag"/> 
            
            <dsp:droplet name="ExitemIdDroplet">
            <dsp:param value="${keepsakeShopList}" name="lastviewedProductsList" />
            <dsp:oparam name="output">
               <dsp:getvalueof var="productList" param="productList" />
            </dsp:oparam>
            </dsp:droplet>
            
            <c:if test="${not empty keepsakeShopList}" >
                <input name="iskeepsakeShopList" id="iskeepsakeShopList" type="hidden" value="true"></input> 
            </c:if>
            
            <c:if test="${not empty brandList}" >
                <input name="isbrandList" id="isbrandList" type="hidden" value="true"></input>
            </c:if>
            
            <c:set var="cert_topRegistryItemsFlag" scope="request"><dsp:valueof param="LandingTemplateVO.topRegistryItemsFlag" /></c:set>
            <c:set var="topRegistryProductMax" scope="request"><bbbc:config key="RegistryLandingTopRegMax" configName="CertonaKeys" /></c:set> 
            <c:set var="cert_scheme" scope="request">grl_tri</c:set>
            <c:set var="cert_topreg" scope="request">grl_tri</c:set>
            <c:set var="cert_topreg_bridal" scope="request">Top Registry Items(bridal)</c:set>
            <c:set var="cert_number" scope="request">${topRegistryProductMax}</c:set>
            <c:set var="cert_BazarVoiceOn" scope="request" value="${BazaarVoiceOn}"></c:set>
             
            <%-- AJAX Call to get the response from Certona 
             <div id="certonaBottomTabs" class="clearfix loadAjaxContent" 
                            data-ajax-url="${contextPath}/giftregistry/bridal_landing_certona_slots.jsp" 
                            data-ajax-target-divs="#certonaBottomTabs" 
                            data-ajax-params-count="8"
                            data-ajax-param1-name="scheme" data-ajax-param1-value="${cert_scheme}" 
                            data-ajax-param2-name="number" data-ajax-param2-value="${cert_number}"  
                            data-ajax-param3-name="topreg" data-ajax-param3-value="${cert_topreg}" 
                            data-ajax-param4-name="topRegistryItemsFlagParam" data-ajax-param4-value="${cert_topRegistryItemsFlag}" 
                            data-ajax-param5-name="certonaSwitch" data-ajax-param5-value="${CertonaOn}" 
                            data-ajax-param6-name="productList" data-ajax-param6-value="${productList}"
                            data-ajax-param7-name="tpb" data-ajax-param7-value="${cert_topreg_bridal}"
                            data-ajax-param8-name="BazaarVoiceOn" data-ajax-param8-value="${cert_BazarVoiceOn}"
                            role="complementary">
                            <div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
            </div> --%> 
            
            <c:set var="cert_BazarVoiceOn" scope="request"></c:set>
            <c:set var="cert_BazarVoiceOn" scope="request"></c:set>
            
            <%-- Display data from Certona got from AJAX Call and BCC --%>
                <div id="tabWrapper">
                    <div class="categoryProductTabs grid_12 clearfix hidden" id="brdlProdTab">
                            <ul class="categoryProductTabsLinks">
                                <li id="topRegCertonaLI"><div class="arrowSouth"></div><a title='<bbbl:label key="lbl_blanding_topreg" language="${pageContext.request.locale.language}"/>' href="#tabs-1"><bbbl:label key="lbl_blanding_topreg" language="${pageContext.request.locale.language}"/></a></li>
                                <c:if test="${not empty keepsakeShopList}" >
                                   <li><div class="arrowSouth"></div><a title='<bbbl:label key="lbl_blanding_keepsake" language="${pageContext.request.locale.language}"/>' href="#tabs-2"><bbbl:label key="lbl_blanding_keepsake" language="${pageContext.request.locale.language}"/></a></li>
                                </c:if>
                                <c:if test="${not empty brandList}" >
                                    <li><div class="arrowSouth"></div><a title='<bbbl:label key="lbl_blanding_brands" language="${pageContext.request.locale.language}"/>' href="#tabs-3"><bbbl:label key="lbl_blanding_brands" language="${pageContext.request.locale.language}"/></a></li>
                                </c:if>
                            </ul>
                            
                            <div id="tabs-1" class="catTabsData">
                                <div class="carousel clearfix">
                                </div>
                             </div>
                            
                            <c:if test="${not empty keepsakeShopList}" >
                            <div id="tabs-2" class="catTabsData">
                              <div class="carousel clearfix">
                                  <dsp:include page="/browse/prod_carousel.jsp" >
                                        <dsp:param name="clearanceProductsList" value="${keepsakeShopList}" />
                                        <dsp:param name="invoke" value="clearance"/>
                                        <dsp:param name="crossSellFlag" value="true"/>
                                        <dsp:param name="desc" value="Keepsake Shop(bridal)"/> 
                                  </dsp:include>
                              </div>
                            </div>
                            </c:if>
                            
                            <c:if test="${not empty brandList}" >
                                <div id="tabs-3" class="catTabsData carosalBrands">
                                    <div class="carousel clearfix">
                                        <dsp:include page="show_brand.jsp" >
                                         <dsp:param name="brandList" value="${brandList}"/>
                                        </dsp:include>
                                    </div>
                                </div>
                            </c:if>
                    </div>
                </div>
                <div class="grid_12 alpha omega clearfix blurbTxt">
                    <bbbt:textArea key="txt_seo_content_registry" language="${pageContext.request.locale.language}" /> 
                </div>
            </div>

            </dsp:oparam>
          </dsp:droplet>
          <script type="text/javascript">
             var resx = new Object();
        </script>

        </jsp:body>
          <jsp:attribute name="footerContent">
           <script type="text/javascript">
           if(typeof s !=='undefined') {
            s.channel='Registry';
            s.pageName='Registry Landing Page';// pagename
            s.prop1='Registry';// page title
            s.prop2='Registry';// category level 1 
            s.prop3='Registry';// category level 2
            s.prop4='';// page type = page title
            s.prop5='';// page type = page title
            s.prop6='${pageContext.request.serverName}'; 
            s.eVar9='${pageContext.request.serverName}';
            var s_code=s.t();
            if(s_code)document.write(s_code);       
           }
        </script>
    </jsp:attribute>
      <%--DoubleClick Floodlight START  --%>
        <%-- Commenting out DoubleClick as part of 34473
    <c:if test="${DoubleClickOn}">
             <c:if test="${(currentSiteId eq BedBathUSSite)}">
                       <c:set var="cat"><bbbc:config key="cat_registry_bedBathUS" configName="RKGKeys" /></c:set>
                       <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
                       <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
             </c:if>
             <c:if test="${(currentSiteId eq BedBathCanadaSite)}">
                       <c:set var="cat"><bbbc:config key="cat_registry_bedbathcanada" configName="RKGKeys" /></c:set>
                       <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
                       <c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
             </c:if>
            <dsp:include page="/_includes/double_click_tag.jsp">
                <dsp:param name="doubleClickParam" 
                value="src=${src};type=${type};cat=${cat};u10=null;u11=null"/>
            </dsp:include>
        </c:if>
        --%>
        <%--DoubleClick Floodlight END --%>
    
    </bbb:pageContainer>
 </dsp:page>