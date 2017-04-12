<dsp:page>
<dsp:importbean bean="/com/bbb/cms/droplet/StaticTemplateDroplet" />
<dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:getvalueof id="currentSiteId" bean="Site.id" />

<dsp:getvalueof var="pageName" param="pageName" scope="page"/>
<c:set var="search" value="'"/>
<dsp:getvalueof var="pageType" param="pageType" scope="page"/>
<dsp:getvalueof var="contentKey" param="contentKey" scope="page"/>
<dsp:getvalueof var="showAsPopup" param="showAsPopup" scope="page"/>
<c:set var="section" value="cms" scope="request" />
<c:set var="pageWrapper" value="${pageName}" scope="request" />
<c:if test="${pageName == 'GlossaryPage' }">
    <c:set var="bodyClass" value="glossary-page" scope="request" />
</c:if>
<%-- <c:set var="contentKey" vlaue="/static/${pageName}"/> --%>
<dsp:droplet name="StaticTemplateDroplet">
		<dsp:param name="pageName" param="pageName" />
		<dsp:param name="siteId" value="${currentSiteId}" />
		<dsp:oparam name="output">
		<dsp:getvalueof var ="pageTitle" param="staticTemplateData.pageTitle"/>
		<dsp:getvalueof var ="breadCrumb" param="staticPageBreadCrumb"/>
		<c:set var="pageTitleNew" value="${fn:replace(pageTitle, search,'')}" />
		<dsp:getvalueof var="pageTypeCode" param="staticTemplateData.pageType" />
		<dsp:getvalueof var="omnitureData" param="staticTemplateData.omnitureData" />
		<dsp:getvalueof var="headTagContent" param="staticTemplateData.headTagContent" />
		<dsp:getvalueof var="bodyEndTagContent" param="staticTemplateData.bodyEndTagContent" />
 		<c:if test="${not empty omnitureData}">
			<c:set var="omniPageName">${omnitureData['pageName'] }</c:set>
			<c:set var="channel">${omnitureData['channel'] }</c:set>
			<c:set var="prop1">${omnitureData['prop1'] }</c:set>
			<c:set var="prop2">${omnitureData['prop2'] }</c:set>
			<c:set var="prop3">${omnitureData['prop3'] }</c:set>
			<c:set var="prop4">${omnitureData['prop4'] }</c:set>
			<c:set var="prop5">${omnitureData['prop5'] }</c:set>
			<c:set var="evar1">${omnitureData['eVar1'] }</c:set>
			<c:set var="evar2">${omnitureData['eVar2'] }</c:set>
			<c:set var="evar3">${omnitureData['eVar3'] }</c:set>
			<c:set var="evar4">${omnitureData['eVar4'] }</c:set>
			<c:set var="evar5">${omnitureData['eVar5'] }</c:set>
			<c:set var="evar6">${omnitureData['eVar6'] }</c:set>
			<c:set var="evar7">${omnitureData['eVar7'] }</c:set>
			<c:set var="evar8">${omnitureData['eVar8'] }</c:set>
		</c:if>
		<c:set var="popup" value="false"/>
		<c:choose>
			<c:when test="${pageTypeCode eq '202' || showAsPopup eq 'true'}">
				<dsp:include page="/cms/static/staticpopup.jsp"/>
				<c:set var="popup" value="true"/>
			</c:when>
			<c:otherwise>
			<%-- <bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" titleString="${titleString}" contentKey="${contentKey}">
			--%>
				<bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}">
					<jsp:attribute name="PageType">StaticPage</jsp:attribute>
					<jsp:attribute name="headTagContent">${headTagContent}</jsp:attribute>
					<jsp:attribute name="bodyEndTagContent">${bodyEndTagContent}</jsp:attribute>
					<dsp:include page="/cms/static/staticfullpage.jsp">
						<dsp:param name="pageName" param="pageName"/>
						<dsp:param name="breadCrumb" param="staticPageBreadCrumb"/>
					</dsp:include>
					</bbb:pageContainer>
			</c:otherwise>
		</c:choose>
	</dsp:oparam>
	<dsp:oparam name="empty">
		<dsp:droplet name="/atg/dynamo/droplet/Redirect">
			<dsp:param name="url" value="/"/>
		</dsp:droplet>
	</dsp:oparam>
	</dsp:droplet>
	<c:if test="${popup eq false}">
	</c:if>
<c:if test="${TellApartOn}">
	<bbb:tellApart actionType="pv" />
</c:if>
<script type="text/javascript">
    function placeOmnitureCode(){
        s.pageType='';
        s.channel='Company Info';
        s.prop4='Company Info';
    }
    if (typeof s !== 'undefined') {
        var omni_omniPageName='${fn:replace(fn:replace(omniPageName,'\'',''),'"','')}';
        var omni_channel='${fn:replace(fn:replace(channel,'\'',''),'"','')}';
        var omni_prop1='${fn:replace(fn:replace(prop1,'\'',''),'"','')}';
        var omni_prop2='${fn:replace(fn:replace(prop2,'\'',''),'"','')}';
        var omni_prop3='${fn:replace(fn:replace(prop3,'\'',''),'"','')}';
        var omni_prop4='${fn:replace(fn:replace(prop4,'\'',''),'"','')}';
        var omni_prop5='${fn:replace(fn:replace(prop5,'\'',''),'"','')}';
        var omni_evar1='${fn:replace(fn:replace(evar1,'\'',''),'"','')}';
        var omni_evar2='${fn:replace(fn:replace(evar2,'\'',''),'"','')}';
        var omni_evar3='${fn:replace(fn:replace(evar3,'\'',''),'"','')}';
        var omni_evar4='${fn:replace(fn:replace(evar4,'\'',''),'"','')}';
        var omni_evar5='${fn:replace(fn:replace(evar5,'\'',''),'"','')}';
        var omni_evar6='${fn:replace(fn:replace(evar6,'\'',''),'"','')}';
        var omni_evar7='${fn:replace(fn:replace(evar7,'\'',''),'"','')}';
        var omni_evar8='${fn:replace(fn:replace(evar8,'\'',''),'"','')}';
        if(omni_omniPageName.length>0){
            s.pageName=omni_omniPageName; // pageName
            s.channel=omni_channel;
            s.prop1=omni_prop1;
            s.prop2=omni_prop2;
            s.prop3=omni_prop3;
            s.prop4=omni_prop4;
            s.prop5=omni_prop5;
            s.eVar1=omni_evar1;
            s.eVar2=omni_evar2;
            s.eVar3=omni_evar3;
            s.eVar4=omni_evar4;
            s.eVar5=omni_evar5;
            s.eVar6=omni_evar6;
            s.eVar7=omni_evar7;
            s.eVar8=omni_evar8; 
            s.prop6='${pageContext.request.serverName}';
            s.eVar9='${pageContext.request.serverName}';
            s.events = '';
            s.products = '';
            s.server='${pageContext.request.serverName}';
        }else{
            var omni_pageTitleNew='${fn:replace(fn:replace(pageTitleNew,'\'',''),'"','')}';
            var omni_pageTitle='${fn:replace(fn:replace(pageTitle,'\'',''),'"','')}';
            var pageName = '${pageName}';
            var pageTitle = '${pageTitleNew}';
            s.pageName='Content Page>'+omni_pageTitleNew; // pageName
            s.channel='Guides & Advice';
            s.prop1='Content Page';
            s.prop2='Content Page';
            s.prop3='Content Page';
            s.prop4='Guides & Advice';
            s.prop6='${pageContext.request.serverName}';
            s.eVar9='${pageContext.request.serverName}';
            s.events = '';
            s.products = '';
            s.server='${pageContext.request.serverName}';
            if(pageName=='GiftCardHomePage'){
                s.pageType='';
                s.pageName='Gift Card Homepage'; // pageName
                s.channel='Gift Cards';
                s.prop1='Gift Cards';
                s.prop2='Gift Cards';
                s.prop3='Gift Cards';
                s.prop4='';
            }else if(pageName=='PrivacyPolicy' || pageName=='AboutUs' || pageName=='BabyAboutUs' || pageName=='MediaRelations' || pageName=='Careers' || pageName=='TermsOfUse' || pageName=='GlossaryPage' || pageName=='GlossaryPage' || pageName=='EasyReturns' || pageName=='ShippingPolicies' || pageName=='GiftPackagingPopUp' || pageName=='FAQ' || pageName=='SafetyAndRecalls' || pageName=='GiftCardPolicy' || pageName=='CorporateResponsibilityReport' || pageName=='CorporateSalesReport' || pageName=='CareersEOD'){
                placeOmnitureCode();
            }else if(pageName=='BabyShippingPolicies'){
                s.pageName= 'Content Page > Registry Policies';
                s.channel='Company Info';
                s.prop4='Company Info';
                s.pageType='';
            }else if (pageName=='HealthyPregnancyGuide' || pageName=='PrenatalCare' || pageName=='TopTips' || pageName=='TipsSleep'){
                s.pageType='';
            }else if (pageName=='DestinationMaternityPage'){
                s.pageName= 'Destination Maternity Welcome Page';
                s.channel='Partnership Page';
                s.prop4='Partnership Page';
            }else if (pageName == 'SmartSleepHabits' || pageName == 'YouAreWhatYouEat' || pageName == 'SkinHealthWellness' || pageName == 'CollegeMedicineCabinetChecklist' ){
                s.pageName= 'College > Insider Guides > The Guides > College Health Guide > ' + omni_pageTitle;
                s.channel='College';
                s.prop4='College';
            }else if (pageName == 'CollegeHealthGuide'){
                s.pageName= 'College > Insider Guides > The Guides > College Health Guide';
                s.channel='College';
                s.prop4='College';
            }else if(pageName == 'WinterWellnessGuide' ){
                s.pageName= 'Content Page > Healthy Women > Winter Wellness Guide';
            }else if(pageName == 'HealthyTravelGuide'){
                s.pageName= 'Content Page > Healthy Women > Healthy Travel Guide';
            }else if(pageName == 'PlanAhead'){
                s.pageName= 'Content Page > Healthy Women > Healthy Travel Guide > Plan Ahead';
            }else if (pageTitle=='Catalogs'){
                s.pageName= 'Circular Page>'+omni_pageTitleNew;
                s.channel='Circular';
                s.prop1='Circular';
                s.prop2='Circular';
                s.prop3='Circular';
                s.prop4='Circular';
                s.eVar1=omni_evar1;
                s.eVar2=omni_evar2;
                s.eVar3=omni_evar3;
                s.eVar4=omni_evar4;
                s.eVar5=omni_evar5;
                s.eVar6=omni_evar6;
                s.eVar7=omni_evar7;
                s.eVar8=omni_evar8; 
            }else{
            }
        }
        fixOmniSpacing();
        var s_code = s.t();
        if (s_code) document.write(s_code);
    }
</script>
</dsp:page>