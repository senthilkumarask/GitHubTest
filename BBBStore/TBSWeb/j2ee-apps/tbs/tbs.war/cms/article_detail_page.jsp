<c:set var="section" value="cms" scope="request" />
<c:set var="pageWrapper" value="articleDetails useFB" scope="request" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:page>

     <dsp:getvalueof var="siteId" bean="Site.id" />
     <dsp:getvalueof var="fromCollege" param="fromCollege" scope="page"/>

 <c:choose>
	  <c:when test="${ fromCollege eq 'true' && (siteId eq 'BedBathUS' || siteId eq 'BedBathCanada')}">
	        <c:set var="pageVariation" value="bc" scope="request" />
	     </c:when>
     <c:otherwise>
	     <c:if test="${siteId eq 'BedBathUS' || siteId eq 'BedBathCanada'}">
		   <c:set var="pageVariation" value="br" scope="request" />
	     </c:if>
     </c:otherwise>

    </c:choose>
    <bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" pageVariation="${pageVariation}">
        <jsp:body>
        <dsp:importbean bean="/atg/multisite/Site"/>
        <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof id="siteURL" bean="Site.productionURL"/>
		<dsp:getvalueof var="guideId" param="guideId"/>
	    <dsp:importbean bean="/com/bbb/cms/droplet/GuidesLongDescDroplet"/>
	    <dsp:getvalueof var="siteId" bean="Site.id" />

         <dsp:droplet name="GuidesLongDescDroplet">
		     <dsp:param name="guideId" value="${guideId}"/>

		     <dsp:oparam name="output">
 	 	 <dsp:getvalueof var="title" param="guidesLongDesc.title"/>
 	 	 <dsp:getvalueof var="longDescription" param="guidesLongDesc.longDescription"/>
 	 	 <dsp:getvalueof var="omnitureData" param="guidesLongDesc.omnitureData"/>
 	 	 <c:if test="${not empty omnitureData}">
					<c:set var="omniPageName">${omnitureData['pageName'] }</c:set>
					<c:set var="omniChannel">${omnitureData['channel'] }</c:set>
					<c:set var="prop1">${omnitureData['prop1'] }</c:set>
					<c:set var="prop2">${omnitureData['prop2'] }</c:set>
					<c:set var="prop3">${omnitureData['prop3'] }</c:set>
					<c:set var="prop4">${omnitureData['prop4'] }</c:set>
		</c:if>
 	      <div id="content" class="container_12 clearfix" role="main">
			<bbbt:textArea key="txt_registry_pagehead" language="${pageContext.request.locale.language}"></bbbt:textArea>
			<dsp:getvalueof var="fromCollege" param="fromCollege"/>

			<div class="breadcrumbs grid_12">
				<c:set var="breadcrumbsLevel_0"><bbbl:label key="lbl_reg_feature_home" language ="${pageContext.request.locale.language}"/></c:set>
				<a href="${contextPath}"><bbbl:label key="lbl_reg_feature_home" language ="${pageContext.request.locale.language}"/></a>
				<span class="rightCarrot">&gt;</span>
				<c:choose>
					<c:when test="${fromCollege == true}">
						<c:set var="breadcrumbsLevel_1"><bbbl:label key="lbl_reg_feature_college_reg" language ="${pageContext.request.locale.language}"/></c:set>
						<a href="${contextPath}/page/College"><bbbl:label key="lbl_reg_feature_college_reg" language ="${pageContext.request.locale.language}"/></a>
					</c:when>
					<c:otherwise>
						<c:choose>
						    <c:when test="${siteId eq 'BedBathUS' || siteId eq 'BedBathCanada'}">
								<c:set var="breadcrumbsLevel_1"><bbbl:label key="lbl_reg_feature_bridal_reg" language ="${pageContext.request.locale.language}"/></c:set>
						      <a href="${contextPath}/page/Registry"><bbbl:label key="lbl_reg_feature_bridal_reg" language ="${pageContext.request.locale.language}"/></a>
						    </c:when>
						    <c:otherwise>
								<c:set var="breadcrumbsLevel_1"><bbbl:label key="lbl_reg_feature_baby_reg" language ="${pageContext.request.locale.language}"/></c:set>
							  <a href="${contextPath}/page/BabyRegistry"><bbbl:label key="lbl_reg_feature_baby_reg" language ="${pageContext.request.locale.language}"/></a>
						    </c:otherwise>
					    </c:choose>
						<span class="rightCarrot">&gt;</span>
						<c:set var="breadcrumbsLevel_2"><bbbl:label key="lbl_guides_advice" language ="${pageContext.request.locale.language}"/></c:set>
						<a href="${contextPath}/registry/GuidesAndAdviceLandingPage"><bbbl:label key="lbl_guides_advice" language ="${pageContext.request.locale.language}"/></a>
					</c:otherwise>
				</c:choose>
				<span class="rightCarrot">&gt;</span>
				<span class="bold">${title}</span>
				<c:set var="breadcrumbsLevel_3">${title}</c:set>
			</div>

			<c:choose>
			<c:when test="${fromCollege == true}">
				<dsp:include page="/cms/college_left_navigation.jsp" />
			</c:when>
			<c:otherwise>
				<dsp:include page="left_navigation.jsp" />
			</c:otherwise>
			</c:choose>

			<div class="grid_9 omega">
				<div class="grid_9 alpha omega"><h1>${title}</h1></div>
                <c:if test="${FBOn}">
                    <div class="grid_9 alpha omega">
                        <!--[if !IE 7]><!-->
                            <div class="fb-like" data-send="false" data-width="350" data-show-faces="false"></div>
                        <!--<![endif]-->
                    </div>
                </c:if>
				<div id="cmsPageContent" class="grid_9 alpha omega clearfix">${longDescription}</div>
			</div>
	</div>

	</dsp:oparam>
	<dsp:oparam name="empty">
		<div id="content" class="container_12 clearfix">
            <div id="cmsPageContent">
                <bbbl:textArea key="txt_404_text" language="${language}"/>
            </div>
        </div>
	</dsp:oparam>
		 </dsp:droplet>
       </jsp:body>
        <jsp:attribute name="footerContent">
        </jsp:attribute>
	</bbb:pageContainer>

	<c:choose>
		<c:when test="${fn:contains(breadcrumbsLevel_1, 'College')}">
			<c:set var="channel">College</c:set>
			<c:set var="prop4var">College</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="channel">Registry</c:set>
			<c:set var="prop4var">Registry</c:set>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${empty breadcrumbsLevel_2}">
			<c:set var="pageName">${breadcrumbsLevel_3}</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="pageName">${channel}&nbsp;${breadcrumbsLevel_2}&nbsp;&gt;&nbsp;${breadcrumbsLevel_3}</c:set>
		</c:otherwise>
	</c:choose>
	<script>
        if (typeof s !== 'undefined') {
            var omni_pageName='${fn:replace(fn:replace(pageName,'\'',''),'"','')}';
            var omni_prop4var='${fn:replace(fn:replace(prop4var,'\'',''),'"','')}';
            var omni_omniPageName='${fn:replace(fn:replace(omniPageName,'\'',''),'"','')}';
            var omni_omniChannel='${fn:replace(fn:replace(omniChannel,'\'',''),'"','')}';
            var omni_prop1='${fn:replace(fn:replace(prop1,'\'',''),'"','')}';
            var omni_prop2='${fn:replace(fn:replace(prop2,'\'',''),'"','')}';
            var omni_prop3='${fn:replace(fn:replace(prop3,'\'',''),'"','')}';
            var omni_prop4='${fn:replace(fn:replace(prop4,'\'',''),'"','')}';
            if(omni_omniPageName.length>0){
                s.pageName=omni_omniPageName; // pageName
                s.channel=omni_omniChannel;
                s.prop1=omni_prop1;
                s.prop2=omni_prop2;
                s.prop3=omni_prop3;
                s.prop4=omni_prop4;
                s.prop6='${pageContext.request.serverName}';
                s.eVar9='${pageContext.request.serverName}';
                s.events = '';
                s.products = '';
                s.server='${pageContext.request.serverName}';
            }else{
                s.channel = '${channel}';
                s.pageName = omni_pageName
                s.prop1 = 'Content Page';
                s.prop2 = 'Content Page';
                s.prop3 = 'Content Page';
                s.prop4 = omni_prop4var;
                s.prop6 = '${pageContext.request.serverName}';
                s.eVar9 = '${pageContext.request.serverName}';
            }
            fixOmniSpacing();
            var s_code=s.t();
            if(s_code)document.write(s_code);
        }
	</script>
</dsp:page>