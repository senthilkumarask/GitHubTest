<c:set var="section" value="cms" scope="request" />
<c:set var="pageWrapper" value="guideAdvice useFB useLiveClicker" scope="request" />
<c:set var="titleString" value="Bed Bath &amp; Beyond - CMS Pages - Guides & Advice" scope="request" />
 <c:set var="pageVariation" value="br" scope="request" />
 <c:set var="pageTitle"><bbbl:label key="lbl_guides_title" language ="${pageContext.request.locale.language}"/></c:set>
  
<dsp:page>
	<bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" titleString="${titleString}" pageVariation="${pageVariation}">
	<jsp:body>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/com/bbb/cms/droplet/GuidesTemplateDroplet" />
	<dsp:importbean bean="/com/bbb/cms/droplet/PaginationDroplet" />
	<dsp:getvalueof var="contentType" param="contentType"/>
	<dsp:getvalueof var="contentVideo" param="contentVideo"/>
	
	<dsp:getvalueof id="siteURL" bean="Site.productionURL"/>
	<dsp:getvalueof var="siteId" bean="Site.id" />
	

	<div id="content" class="container_12 clearfix" role="main">
		
		<bbbt:textArea key="txt_registry_pagehead" language="${pageContext.request.locale.language}"></bbbt:textArea>
		
		<div class="breadcrumbs grid_12">
			<a href="${contextPath}"><bbbl:label key="lbl_reg_feature_home" language ="${pageContext.request.locale.language}"/></a>
			<span class="rightCarrot">&gt;</span>
			
			<c:choose>
		    <c:when test="${siteId eq 'BedBathUS' || siteId eq 'BedBathCanada'}">
		      <a href="${contextPath}/page/Registry"><bbbl:label key="lbl_reg_feature_bridal_reg" language ="${pageContext.request.locale.language}"/></a>
		    </c:when>
		    <c:otherwise>
		      <a href="${contextPath}/page/BabyRegistry"><bbbl:label key="lbl_reg_feature_baby_reg" language ="${pageContext.request.locale.language}"/></a>
		    </c:otherwise>
		    </c:choose>
			<span class="rightCarrot">&gt;</span>
			<a href="${contextPath}/registry/GuidesAndAdviceLandingPage"><bbbl:label key="lbl_guides_advice" language ="${pageContext.request.locale.language}"/></a>
			<span class="rightCarrot">&gt;</span>
			<span class="bold">
			<c:choose>
			<c:when test="${not empty contentVideo}"> 
			  <bbbl:label key="lbl_guides_video" language ="${pageContext.request.locale.language}"/>
			</c:when>
			<c:otherwise>
			 ${fn:toUpperCase(fn:substring(contentType, 0, 1))}${fn:toLowerCase(fn:substring(contentType, 1, -1))}
			</c:otherwise>
			</c:choose>
			
			
			</span>
		</div>
		
		<dsp:include page="left_navigation.jsp" />
				
		<div id="cmsPageHead" class="grid_9">
			<h1><bbbl:label key="lbl_guides_title" language ="${pageContext.request.locale.language}"/></h1>
				 
			<div class="txtCaption">
				<bbbt:textArea key="txt_guides_description" language ="${pageContext.request.locale.language}"/> 
			</div>
				
            <%-- <div class="btnCaption">
				<span class="bold"><bbbl:label key="lbl_guides_registry" language ="${pageContext.request.locale.language}"/></span>
				<bbbt:textArea key="txt_guides_registry" language ="${pageContext.request.locale.language}"/>
			</div> --%>
				
		<%-- <bbbt:textArea key="txt_create_wedding_registry" language ="${pageContext.request.locale.language}"/> --%>
			
		</div> 
		
		
		<c:set var="cssClass" value="active"></c:set>
		<div id="cmsPageContent" class="grid_9 clearfix">
			<div class="grid_9 alpha omega">
				<dl class="guidesNav noMarTop">					
				<dd class="clearfix">
				<a href="guides_advice.jsp?contentType=articles&guidesCategory=bridal guide" title="<bbbl:label key="lbl_guides_article" language ="${pageContext.request.locale.language}"/>" class="first<c:if test="${ (empty contentVideo) and  (contentType==null or contentType=='articles')}"> active</c:if>"><bbbl:label key="lbl_guides_article" language ="${pageContext.request.locale.language}"/></a>
                    <%-- commented the below code as per the content change request --%>
                    <%-- <a href="guides_advice.jsp?contentType=guides&guidesCategory=bridal guide" title="<bbbl:label key="lbl_guides_home" language ="${pageContext.request.locale.language}"/>" class="first<c:if test="${ (empty contentVideo) and  (contentType==null or contentType=='guides')}"> active</c:if>"><bbbl:label key="lbl_guides_home" language ="${pageContext.request.locale.language}"/></a><span>|</span>
					<a href="guides_advice.jsp?contentType=tips&guidesCategory=bridal guide" title="<bbbl:label key="lbl_guides_Tips" language ="${pageContext.request.locale.language}"/>" class="<c:if test="${contentType=='tips'}">active</c:if>"><bbbl:label key="lbl_guides_Tips" language ="${pageContext.request.locale.language}"/></a><span>|</span>
					<a href="guides_advice.jsp?contentVideo=video" title="<bbbl:label key="lbl_guides_video" language ="${pageContext.request.locale.language}"/>" class="<c:if test="${not empty contentVideo}">active</c:if>"><bbbl:label key="lbl_guides_video" language ="${pageContext.request.locale.language}"/></a><span>|</span>
					<a href="guides_advice.jsp?contentType=articles&guidesCategory=bridal guide" title="<bbbl:label key="lbl_guides_article" language ="${pageContext.request.locale.language}"/>" class="<c:if test="${contentType=='articles'}">active</c:if>"><bbbl:label key="lbl_guides_article" language ="${pageContext.request.locale.language}"/></a>		
			 --%>	</dd>
				</dl>
			</div>
			 
            <dsp:getvalueof var="perPage" param="pagFilterOpt" scope="request"/>
			<dsp:getvalueof var="pagNum" param="pagNum" scope="request"/>
			<dsp:getvalueof var="seeAll" param="seeAll" scope="request"/>
	  	    
	  	    <c:if test="${empty contentVideo}">
 		    <dsp:droplet name="GuidesTemplateDroplet">
		    	<dsp:param name="contentType" param="contentType"/>
		    	<dsp:param name="guidesCategory" param="guidesCategory"/>
				<dsp:oparam name="output">
			    	<dsp:getvalueof var="lstGuidesTemplate" param="lstGuidesTemplate" />
					<dsp:getvalueof var="lstDropDown" param="lstDropDown" scope="request"/>
					<dsp:getvalueof var="firstElement" param="FirstElement"/>
					<c:choose>
				 		<c:when test="${empty perPage}">
				 		  <c:set var="perPage" value="${firstElement}" scope="request"/>
				 		</c:when>
				 		<c:otherwise>
				 		  <c:set var="seeAll" value="false"/>
				 		</c:otherwise>
			 	   </c:choose>
			  
					<c:if test="${empty pagNum}">
					   <c:set var="pagNum" value="1" scope="request"/>
			        </c:if>
		    		<dsp:getvalueof var="lstGuidesTemplateSize" value="${fn:length(lstGuidesTemplate)}" scope="request"/>
					<input type="hidden" id="lstGuidesTemplateSize" value="${lstGuidesTemplateSize}">					
			 		<dsp:getvalueof var="tabNo" param="tabNo" />
					 <c:if test="${empty tabNo}">
			            <c:set var="tabNo" value="1" />
	                </c:if>
			 		<dsp:getvalueof var="tabNo" param="tabNo" />
					<dsp:droplet name="PaginationDroplet">
				 		<dsp:param name="pageNo" value="${tabNo}"/>
				 		<dsp:param name="perPage" value="${perPage}"/>
				 		<dsp:param name="seeAll" value="${seeAll}"/>
				 		<dsp:param name="guideList" value="${lstGuidesTemplate}"/>
				   		<dsp:oparam name="output">
				     		<dsp:getvalueof var="guideReturnList" param="guideReturnList" />
				     		<dsp:getvalueof var="guideReturnListSize" value="${fn:length(guideReturnList)}" scope="request"/>
				     		<dsp:getvalueof var="pageCount" param="pageCount"/>
							<c:set var="totalTab" value="${pageCount}" scope="request"/>
				   		</dsp:oparam>
					</dsp:droplet> 
						<div class="grid_9 alpha omega">
                    <dsp:getvalueof var="currentPage" value="${pagNum}" scope="request"/>
					<dsp:include page="pagination.jsp" >
					  <dsp:param name="guideReturnListSize" value="${guideReturnListSize}"/>
					  <dsp:param name="lstGuidesTemplateSize" value="${lstGuidesTemplateSize}"/>
					  <dsp:param name="lstDropDown" value="${lstDropDown}"/>
					  <dsp:param name="perPage" value="${perPage}"/>
					  <dsp:param name="totalTab" value="${totalTab}"/>
					  <dsp:param name="contentType" value="${contentType}"/>
					</dsp:include>
					</div>
					<dsp:getvalueof var="tabNo" param="tabNo" />
					<dsp:droplet name="PaginationDroplet">
						<dsp:param name="pageNo" value="${pagNum}"/>
						<dsp:param name="perPage" value="${perPage}"/>
						<dsp:param name="seeAll" value="${seeAll}"/>
						<dsp:param name="guideList" value="${lstGuidesTemplate}"/>
	           			<dsp:oparam name="output">
				 			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		                    <dsp:param param="guideReturnList" name="array"/>
							<dsp:oparam name="outputStart">
								
							</dsp:oparam>
                    		<dsp:oparam name="output">
								<dsp:getvalueof var="count" param="count" />
								<dsp:getvalueof var="title" param="element.title" />
								<dsp:getvalueof var="imageUrl" param="element.imageUrl" />
								<dsp:getvalueof var="imageAltText" vartype="java.lang.String" param="element.imageAltText"/>
								<dsp:getvalueof var="shortDescription" param="element.shortDescription" />
								<dsp:getvalueof var="guideId" param="element.guideTemplateId" scope="request" />
						
								<dsp:droplet name="/atg/repository/seo/GuideItemLink">
									<dsp:param name="repositoryName" value="/com/bbb/cms/repository/GuidesTemplate" />
									<dsp:param name="itemDescriptor" value="guides" />
									<dsp:param name="id" param="element.guideTemplateId"/>
									<dsp:oparam name="output">
								        <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url"/>
								        <c:set var="finalUrl" value="${finalUrl}?frmReg=true"/>
								    </dsp:oparam>
							    </dsp:droplet>  	 				
								
								<c:if test="${count==1}">
									<div class="grid_9 containerBorder ">
								</c:if> 
								<div class="grid_9 alpha omega infoBox">  
									   <div class="grid_1">
                                        <c:choose>
                                            <c:when test="${(fn:indexOf(imageUrl, 'http') == 0) || (fn:indexOf(imageUrl, '//') == 0)}">
                                                <dsp:a page="${finalUrl}" title="${title}"><img src="${imageUrl}" height="63" width="63" alt="${imageAltText}" title="${imageAltText}" /></dsp:a>
                                            </c:when>
                                            <c:otherwise>
                                                <dsp:a page="${finalUrl}" title="${title}"><img src="${imagePath}${imageUrl}" height="63" width="63" alt="${imageAltText}" title="${imageAltText}" /></dsp:a>
                                            </c:otherwise>
                                       </c:choose>
										</div>		
										 <div class="grid_8 alpha omega clearfix">
											<h6 class="grid_6 alpha omega noMar marBottom_10"><dsp:a page="${finalUrl}" title="${title}">${title}</dsp:a></h6>
                                            <c:if test="${FBOn}">
                                            <!--[if IE 7]>
                                                <dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
                                                    <dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
                                                    <dsp:oparam name="output">
                                                        <dsp:getvalueof id="encodedURL" param="encodedURL"/>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                                <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
                                                    <dsp:param name="configKey" value="FBAppIdKeys"/>
                                                    <dsp:oparam name="output">
                                                        <dsp:getvalueof var="fbAppIDConfigMap" param="configMap"/>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                                <div class="fb-like grid_2 alpha omega">
                                                    <iframe type="some_value_to_prevent_js_error_on_ie7" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=button_count&amp;width=90&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:90px; height:24px;" allowTransparency="true"></iframe>
                                                </div>
                                            <![endif]-->
                                            <!--[if !IE 7]><!-->
                                                <div class="fb-like grid_2 alpha omega" data-href="${finalUrl}" data-layout="button_count" data-send="false" data-width="90" data-show-faces="false"></div>
                                            <!--<![endif]-->
                                            </c:if>
										   <div class="grid_8 noMar boxContent">
											${shortDescription}  
										   </div>
										</div>
								   </div>
 
							    
								<c:if test="${count==2}">
									</div>  
								</c:if>
				 
								 
							</dsp:oparam> 
				  			</dsp:droplet>
			    		</dsp:oparam>
			  		</dsp:droplet>
                  <div class="grid_9 alpha omega marTop_10">					
                    <dsp:getvalueof var="currentPage" value="${pagNum}" scope="request"/>
					<dsp:include page="pagination.jsp" >
						<dsp:param name="guideReturnListSize" value="${guideReturnListSize}"/>
						<dsp:param name="lstGuidesTemplateSize" value="${lstGuidesTemplateSize}"/>
						<dsp:param name="lstDropDown" value="${lstDropDown}"/>
						<dsp:param name="perPage" value="${perPage}"/>
						<dsp:param name="totalTab" value="${totalTab}"/>
						<dsp:param name="contentType" value="${contentType}"/>
					</dsp:include>
					</div>
				</dsp:oparam>
				<dsp:oparam name="empty">
					No ${contentType} Found
					 
				  </div>
				</dsp:oparam>
			</dsp:droplet>
			</c:if>
			<c:if test="${not empty contentVideo}">
			<div class="grid_9 alpha omega">
                            <div id="iframe"></div>
                        </div>
			</c:if>
			
	 	  </div>
    </div>		
    </jsp:body>
     <jsp:attribute name="footerContent">
      <script type="text/javascript">   if(typeof s !=='undefined') {
   			var omni_pageTitle='${fn:replace(fn:replace(pageTitle,'\'',''),'"','')}';
   			s.channel='Registry';
   			s.pageName='Registry Guides and Advice>' + omni_pageTitle;// pagename
   			s.prop1='Content Page';// page title
   			s.prop2='Content Page';// category level 1 
   			s.prop3='Content Page';// category level 2
   			s.prop4='Registry';// page type = page title
   			s.prop5=omni_pageTitle;// page type = page title
   			s.prop6='${pageContext.request.serverName}'; // category level 1
   			s.eVar9='${pageContext.request.serverName}';
   			fixOmniSpacing();
   			var s_code=s.t();
   			if(s_code)document.write(s_code);		
   }
	</script>
	</jsp:attribute>
   </bbb:pageContainer>
  
</dsp:page>