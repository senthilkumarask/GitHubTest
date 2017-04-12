<%--
  This page included by page container tag
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="atg.servlet.ServletUtil" %>
<%-- 
  JSP 2.1 parameter. With trimDirectiveWhitespaces enabled,
  template text containing only blank lines, or white space,
  is removed from the response output.
  
  trimDirectiveWhitespaces doesn't remove all white spaces in a
  HTML page, it is only supposed to remove the blank lines left behind
  by JSP directives (as described here 
  http://java.sun.com/developer/technicalArticles/J2EE/jsp_21/ ) 
  when the HTML is rendered. 
 --%>
<%@page trimDirectiveWhitespaces="true"%>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
<dsp:importbean bean="/com/bbb/cms/droplet/GuidesLongDescDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
<dsp:page>
	<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
    <dsp:getvalueof var="section" param="section"/>
    <dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
    <dsp:getvalueof var="themeFolder" param="themeFolder"/>
    <dsp:getvalueof var="pageName" param="pageName"/>
    <dsp:getvalueof var="PageType" param="PageType"/>
    <dsp:getvalueof var="pageFBType" value="website"/>	
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="language" bean="/OriginatingRequest.requestLocale.locale.language"/>
    <dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
    <dsp:getvalueof var="schemeName" bean="/OriginatingRequest.scheme"/>
    <dsp:getvalueof var="headTagContent" param="headTagContent"/>
    <dsp:getvalueof var="profile" bean="/atg/userprofiling/Profile"/>
	<c:choose>
    	<c:when test="${!fn:contains(profile, 'null')}">
    		<c:set var="profileId"><dsp:valueof bean="/atg/userprofiling/Profile.id"/></c:set>
    	</c:when>
    	<c:otherwise>
    		<c:set var="profileId" value=""/>
   		</c:otherwise>
   </c:choose>
    <c:set var="pageSecured" value="${pageContext.request.secure}" scope="request" />
    <c:set var="REQURLPROTOCOL" value="http" scope="request" />
    <c:if test="${pageSecured}">
        <c:set var="REQURLPROTOCOL" value="https" scope="request" />
    </c:if>
	<dsp:getvalueof var="isInternationalCustomer" bean="SessionBean.internationalShippingContext" />
	<dsp:getvalueof var="personalizationData" bean="SessionBean.edwDataVO.edwDataJsonObject" />
	<dsp:getvalueof var="maxRetryEDwRepo" bean="SessionBean.edwDataVO.maxRepoRetryFlag" />
	
	<c:set var="isInternationalCustomer" value= "${isInternationalCustomer}" scope="session"/>
	<dsp:getvalueof var="valueMap" bean="SessionBean.values" />
    
	<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
		<dsp:oparam name="output">
		<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM"/>
		 <c:set var="buildRevisionNumber" value="${buildRevisionNumber}" scope="request" />
				</dsp:oparam>	
	</dsp:droplet>
	<dsp:getvalueof var="hoorayModal" param="hoorayModal"/>
    <%--
    FIX FOR CSS FILES GETTING DOWNLOADED TWICE ON IE7 & IE8 
    http://harinderseera.blogspot.in/2011/06/ie8-quirk-css-file-requested-twice-when.html
    http://makandracards.com/makandra/12183-internet-explorer-will-download-css-files-twice-if-referenced-via-scheme-less-urls
    --%>
    <c:if test="${fn:indexOf(cssPath, '//') == 0}">
        <c:set var="cssPath" value="${REQURLPROTOCOL}:${cssPath}" scope="request" />
    </c:if>
    
    <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
        <dsp:param name="configKey" value="QASKeys"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="configMap" param="configMap"/>
        </dsp:oparam>
    </dsp:droplet>
    
	<c:if test="${not empty param.enableLog}">
        <c:set var="enableLog" scope="session" value="${param.enableLog}"/>
    </c:if>

	
	
    <c:if test="${not empty param.minifyDebug}">
        <c:set var="notMinifyJSCSS" scope="session" value="${param.minifyDebug}"/>
    </c:if>
    
    <c:if test="${empty notMinifyJSCSS}">
        <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
        <dsp:param name="configKey" value="FlagDrivenFunctions"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="minifiedJSFlag" param='${"configMap.minifiedJSFlag_"}${currentSiteId}' scope="request"/>
            <dsp:getvalueof var="minifiedCSSFlag" param='${"configMap.minifiedCSSFlag_"}${currentSiteId}' scope="request"/>
        </dsp:oparam>
        </dsp:droplet>
    </c:if>
    
    <c:if test="${(not empty notMinifyJSCSS) && (notMinifyJSCSS == 'false')}">
        <c:set var="minifiedJSFlag" value="true" scope="request"/>
        <c:set var="minifiedCSSFlag" value="true" scope="request"/>
    </c:if>

    <dsp:getvalueof id="cssCurrentSiteId" bean="/atg/multisite/Site.id" />
    <c:set var="cssBedBathUSSiteCode"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="cssBuyBuyBabySiteCode"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="cssBedBathCanadaSiteCode"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="scene7ClientID"><bbbc:config key="s7ClientID" configName="ThirdPartyURLs" /></c:set>
    <c:choose>
        <c:when test="${cssCurrentSiteId eq cssBuyBuyBabySiteCode}">
            <c:set var="cssCurrentSiteCode" value="buyBuyBaby" />
        </c:when>
        <c:when test="${cssCurrentSiteId eq cssBedBathCanadaSiteCode}">
            <c:set var="cssCurrentSiteCode" value="bedBathCA" />
        </c:when>
        <c:otherwise>
            <c:set var="cssCurrentSiteCode" value="bedBathUS" />
        </c:otherwise>
    </c:choose>
	<dsp:getvalueof var="catLevelCount" bean="/atg/multisite/Site.siteL3Count" />
    <c:set var="vbvStyleTag" value="" />
    <c:set var="vbvCFrameFix" value="" />
    <c:set var="vbvBodyOnLoad" value="" />
    <c:if test="${fn:indexOf(pageWrapper, 'cclaunchmodal') > -1}">
        <c:set var="vbvStyleTag" value="style='background: url(\"/_assets/global/images/widgets/small_loader.gif\") no-repeat scroll center center #fff !important;overflow:hidden !important;width:380px;height:380px;min-width:380px;min-height:380px;'" />
        <c:set var="vbvBodyOnLoad" value="onload=\"cCOnLoadHandler();\"" />
    </c:if>
    <c:if test="${fn:indexOf(pageWrapper, 'cCFrame') > -1}">
        <c:set var="vbvCFrameFix" value="overlayTrans" />
    </c:if>
<!doctype html>
<%-- <!--[if IE 7]>    <html class="${cssCurrentSiteCode} ie no-js ie7 oldie" ${vbvStyleTag} lang="en" xmlns:fb="${REQURLPROTOCOL}://graph.facebook.com/schema/og" xml:lang="en"> <![endif]--> --%>
<!--[if IE 8]>    <html class="${cssCurrentSiteCode} ie no-js ie8 oldie" ${vbvStyleTag} lang="en" xmlns:fb="${REQURLPROTOCOL}://graph.facebook.com/schema/og" xml:lang="en"> <![endif]-->
<!--[if IE 9]>    <html class="${cssCurrentSiteCode} ie no-js ie9 newie" ${vbvStyleTag} lang="en" xmlns:fb="${REQURLPROTOCOL}://graph.facebook.com/schema/og" xml:lang="en"> <![endif]-->
<!--[if gt IE 9]> <html class="${cssCurrentSiteCode} ie no-js newie" ${vbvStyleTag} lang="en" xmlns:fb="${REQURLPROTOCOL}://graph.facebook.com/schema/og" xml:lang="en">     <![endif]-->
<!--[if !IE]><html class="${cssCurrentSiteCode} not-ie no-js" ${vbvStyleTag} lang="en" xmlns:fb="${REQURLPROTOCOL}://graph.facebook.com/schema/og" xml:lang="en">  <![endif]-->
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta name="msapplication-config" content="none"/>
<c:if test="${fn:indexOf(pageWrapper, 'billing') > -1 && fn:indexOf(pageWrapper, 'singleShipping') == -1}">
   <meta name = "viewport" content = "initial-scale=1.0, user-scalable=no, width=device-width">
</c:if>

<c:set var="BedBathUSSite"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" /></c:set>
<c:set var="BuyBuyBabySite"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
<c:set var="BedBathCanadaSite"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>

<c:if test="${fn:indexOf(pageWrapper, 'chekoutConfirm') > -1}">
	<script type="text/javascript">
		if (window.self !== window.top) {
			window.top.location = window.self.location;
		}
	</script>
</c:if>
<c:if test="${fn:indexOf(pageWrapper, 'cCAuthenticate') > -1}">
	<c:set var="vbvBodyOnLoad" value="onload=\"cCOnLoadHandler();\"" />
	<script type="text/javascript">
		var selfLoc = window.self.location.href;
		if (window.self !== window.top) {
			function cCOnLoadHandler(){ /* just a blank function to prevent js error during redirection (edge-case) */ }
			window.top.location = (selfLoc.indexOf('?') > -1)? (selfLoc + '&pr=true'): (selfLoc + '?pr=true');
		}
	</script>
</c:if>
<dsp:include page="/_includes/third_party_on_of_tags.jsp"/>

<meta http-equiv="content-type" content="text/html; charset=utf-8" />

<c:if test="${pageContext.request.secure}">
   <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
  </c:if>


<dsp:getvalueof param="amigoMeta" var="amigoMeta"/>
<c:out value="${amigoMeta}" escapeXml="false"></c:out>

<c:if test="${minifiedCSSFlag == 'true'}"><c:set var="min" value=".min" /></c:if>

<%--mPulse file --%>
<dsp:include page="/_includes/mPulse.jsp">
	<dsp:param name="BedBathUSSite" param="BedBathUSSite"/>
	<dsp:param name="BuyBuyBabySite" param="BuyBuyBabySite"/>
	<dsp:param name="BedBathCanadaSite" param="BedBathCanadaSite"/>
	<dsp:param name="PageType" param="PageType"/>
</dsp:include>

<c:if test="${fn:indexOf(pageWrapper, 'category') > -1 }">
    <style>
        .by .category #hero, .by .category #hero .carousel, .by .category #hero .caroufredsel_wrapper, .bc .category #hero .carousel, .bc .category #hero .caroufredsel_wrapper,  .by .category #hero .heroImageContainer .heroImages{height:475px !important;}        
        .category #popularCategory{padding-bottom:15px;}
        .category #popularCategory li{display:list-item !important;}
        .category .viewMoreCat, .category .viewLessCat{display:none !important;}
        .category .popularCategoryRow{display: block !important;}
        .category #expandCollapseCategories{display: none;}
        .category #popularCategory h2, .category #someFunNewProducts h2{color:#444;font-family:'FuturaStdHeavy';}      
        .bb .category #popularCategory h2, .bb .category #someFunNewProducts h2{color: #f75e9f; font-family: 'AmericanTypewriter';  font-size: 25px;}
        .by .category #hero .heroImageContainer div{background-position: center top !important;}
    </style>
</c:if>


<%-- GLOBAL CSS FILES --%>
<c:choose>
    <c:when test="${minifiedCSSFlag == 'true'}">
		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/bbb_combined.css?v=${buildRevisionNumber}" />
		<!--[if lte IE 9]>
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/global.min.css?v=${buildRevisionNumber}" />
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/bbb_combined_IE.css?v=${buildRevisionNumber}" />
        <![endif]-->
		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/print.min.css?v=${buildRevisionNumber}" media="print" />
    </c:when>
    <c:otherwise>    	
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/12colgrid.css?v=${buildRevisionNumber}" />
		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/main.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/global.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/ic_oops_page.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/forms.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/jquery-ui-1.8.16.custom.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/uniform/uniform_default.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/main_header.css?v=${buildRevisionNumber}" />
     	<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/touchcarousel.css?v=${buildRevisionNumber}" />
    	<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/bus/fonts/style.css?v=${buildRevisionNumber}"  />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/store_locator${min}.css" />         
		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/print.css?v=${buildRevisionNumber}" media="print" />		
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/contact_store.css?v=${buildRevisionNumber}"  />
        <!-- <c:if test="${fn:indexOf(pageWrapper, 'webPopOverCSS') > -1}">
        </c:if> -->
        <link href="${cssPath}/_assets/global/css/jquery.webui-popover.min.css?v=${buildRevisionNumber}" rel="stylesheet" type="text/css" />
    </c:otherwise>
</c:choose>

<c:if test="${fn:indexOf(pageWrapper, 'personalStorePage') > -1}">
    <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/jquery.mCustomScrollbar${min}.css?v=${buildRevisionNumber}" /> 
</c:if>

<%-- BBBI-3814|Add vendor JS and CSS file in type ahead (tech story) --%>
<%-- UNBXD AUTOSUGGEST start --%>
<c:if test="${not empty vendorParam && 'e1' ne vendorParam}">
	<c:set var="vNameBccKey">${vendorParam}<bbbl:label key="lbl_search_vendor_sitespect" language="${pageContext.request.locale.language}" /></c:set>
  	<c:set var="vName"><bbbc:config key="${vNameBccKey}" configName="VendorKeys"/></c:set>
  	<c:set var="vendorCSSHrefBccKey">${vName}<bbbl:label key="lbl_css_href" language="${pageContext.request.locale.language}" /></c:set>
   	<link rel="stylesheet" type="text/css" href='<bbbc:config key="${vendorCSSHrefBccKey}" configName="VendorKeys"/>'>
</c:if>
<%-- UNBXD AUTOSUGGEST end --%>	

 <c:if test="${fn:indexOf(pageWrapper, 'productDetails') > -1 || fn:indexOf(pageWrapper, 'searchResults') > -1 || fn:indexOf(pageWrapper, 'subCategory') > -1 || fn:indexOf(pageWrapper, 'category') > -1}">
 <%--Webcollage Script--%>		
		 <c:choose>
			<c:when test="${currentSiteId eq BedBathUSSite}">
				<script type="text/javascript">collageSiteId = "bedbathandbeyond";</script>
			</c:when>
			<c:when test="${currentSiteId eq BuyBuyBabySite}">
				<script type="text/javascript">collageSiteId = "buybuybaby";</script>
			</c:when>
			<c:otherwise>
				<script type="text/javascript">collageSiteId = "bedbathbeyond-ca";</script>
			</c:otherwise>
		</c:choose>
		
		<script type="text/javascript">
		window._acssitecode = collageSiteId;
		// Answers Cloud Services Embed Script v1.02
		// DO NOT MODIFY BELOW THIS LINE *****************************************
		;(function (g) {
		  var d = document, i, am = d.createElement('script'), h = d.head || d.getElementsByTagName("head")[0],
		  aex = {"src": "//gateway.answerscloud.com/"+window._acssitecode+"/production/gateway.min.js", "type": "text/javascript", "async": "true", "data-vendor": "acs","data-role": "gateway"};
		  for (var attr in aex) { am.setAttribute(attr,aex[attr]); }
		  h.appendChild(am);
		  g['acsReady'] = function () {var aT = '__acsReady__', args = Array.prototype.slice.call(arguments, 0),k = setInterval(function () {if (typeof g[aT] === 'function') {clearInterval(k);for (i = 0; i < args.length; i++) {g[aT].call(g, function(fn) { return function() { setTimeout(fn, 1) };}(args[i]));}}}, 50);};
		})(window);
		// DO NOT MODIFY ABOVE THIS LINE *****************************************
		</script>
		<%--End Webcollage Script--%>
</c:if>

<%-- baby CA mode triggered by url paramater babyCAMode=true
	themeName = bc = baby canada
	
	in babyCA mode, we will load both the beyond theme as well as the babyCA theme
	the babyCA theme will override any specific styles required
	
	
	will need specific logic as to when to execute baby ca mode
	scenarios:
	viewing baby L1 node
	viewing baby registry on bedbath
	url = baby
	
--%>
<%-- first get babyCA session/url param values, may be overridden by active registry --%>
<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA"/>
<c:if test="${sessionBabyCA eq 'true' && currentSiteId eq 'BedBathCanada'}">
	<c:set var="babyCAMode" scope="request" value="true"/>
</c:if>
<%-- can set url param to manually set babyCA session --%>
<dsp:getvalueof var="babyCA" param="babyCA"/>
<c:if test="${babyCA eq 'true' && currentSiteId eq 'BedBathCanada'}">
	<c:set var="babyCAMode" scope="request" value="true"/>
	<dsp:setvalue bean="SessionBean.babyCA" value="true"/>
</c:if>
<c:if test="${babyCA eq 'false' && currentSiteId eq 'BedBathCanada'}">
   	<dsp:setvalue bean="SessionBean.babyCA" value="false"/>
   	<c:set var="babyCAMode" scope="request" value="false"/>
</c:if>

<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="sessionRegistry"/>
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
<c:if test="${isTransient eq false and empty sessionRegistry}">
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:droplet name="GiftRegistryFlyoutDroplet">
<dsp:param name="profile" bean="Profile"/>
<dsp:oparam name="output">
<%-- Just to set the values in SessionBean.values.userRegistrySummaryVO which will be used to determine the babyCAMode variable responsible for themes in the Canada site. --%>
</dsp:oparam>
</dsp:droplet>
</c:if>


<%-- get active registry in session - registry type can override SessionBean.babyCA value--%>
<c:if test="${not empty sessionRegistry}">
	<c:set var="regType" value="${sessionRegistry.registryType.registryTypeDesc}" />
	<%-- if viewing baby registry on Bedbath Canada show baby style --%>
	<c:if test="${(currentSiteId eq 'BedBathCanada') and (regType eq 'Baby')}" >
		<c:set var="babyCAMode" scope="request" value="true"/>
	</c:if>
	<c:if test="${(currentSiteId eq 'BedBathCanada') and (regType ne 'Baby')}" >
		<c:set var="babyCAMode" scope="request" value="false"/>
	</c:if>
</c:if>
<c:set var="babyCanada" scope="session" value="${babyCAMode }"></c:set>
<%-- if viewing non-baby registry on Bedbath Canada, make sure to NOT load babyca style (due to registry session error)
<dsp:getvalueof var="regEventType" param="eventType"/>
<c:if test="${not empty regEventType && currentSiteId eq 'BedBathCanada'}">
	<c:if test="${regEventType ne 'Baby'}">
		<c:set var="babyCAMode" scope="request" value="false"/>
	</c:if>
	<c:if test="${regEventType eq 'Baby'}">
		<c:set var="babyCAMode" scope="request" value="true"/>
	</c:if>
</c:if> 
--%>

<c:if test="${babyCAMode eq 'true' && currentSiteId eq 'BedBathCanada'}">
    <c:set var="BabyCaClass" value=" bca" scope="request" />
</c:if>


<%-- SITE SPECIFIC CSS --%>
<c:choose>
    <c:when test="${minifiedCSSFlag == 'true'}">
		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/theme.min.css?v=${buildRevisionNumber}" />
    </c:when>
    <c:otherwise>    	
       <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/theme.css?v=${buildRevisionNumber}" />
		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/ic_oops_page.css?v=${buildRevisionNumber}" />
    </c:otherwise>
</c:choose>

<%-- in the case of baby canada, we will load both the beyond theme as well as the babyCA theme
	the babyCA theme will override any specific styles required  --%>
<c:if test="${babyCAMode == 'true' && currentSiteId eq 'BedBathCanada'}">
	<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/bbbabyca/css/theme${min}.css?v=${buildRevisionNumber}" />
</c:if>

<%-- Changed it for iPad optimization --%>
<c:if test="${fn:indexOf(pageWrapper, 'useBazaarVoice') > -1 && bExternalJSCSS && bPlugins && BazaarVoiceOn}">
    <c:choose>
        <c:when test="${currentSiteId eq BedBathUSSite}">
            <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_ratings_us' configName='ThirdPartyURLs' />" />
            </c:when>
        <c:when test="${currentSiteId eq BuyBuyBabySite}">
            <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_ratings_baby' configName='ThirdPartyURLs' />" />
          </c:when>
        <c:otherwise>
            <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_ratings_ca' configName='ThirdPartyURLs' />" />
        </c:otherwise>
    </c:choose>  
</c:if>
<%-- CSS for new store locator maps 
<c:if test="${fn:indexOf(pageWrapper, 'noStoreLocatorCss') == -1 && bExternalJSCSS && bPlugins }">
	<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/store_locator${min}.css" />
</c:if> --%>

<dsp:getvalueof bean="/com/bbb/dafpipeline/SessionSecurityBean.secureTokenstatus" var="secureTokenFlag" />

<c:choose>
	<c:when test="${secureTokenFlag}">
		<c:set var="fireAjax" value="false"/>
	</c:when>
	<c:otherwise>
		<c:set var="fireAjax" value="true"/>
	</c:otherwise>
</c:choose>
<c:set var="siteTourFlag">
	<bbbc:config key="SiteTourFlag" configName="ContentCatalogKeys" />
</c:set>
<c:set var="siteTourCookie">
	<bbbl:label key="lbl_site_tour_cookie" language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="randMoreCategories"><bbbt:textArea key="txt_more_categories"  language ="${pageContext.request.locale.language}"/></c:set>

<script type="text/javascript">
    <!--
    var BBB = BBB || {};
    BBB.randMoreCategories = [<c:out value="${randMoreCategories}" escapeXml="false" />];
    BBB.currentSiteName = "<c:out value="${currentSiteId}" />";
    BBB.fireAjaxCall= "<c:out value="${fireAjax}" />";
    BBB.cssCurrentSiteCode = "<c:out value="${cssCurrentSiteCode}" />";
    BBB.pageName = "<c:out value="${pageNameFB}" />";
    BBB.hideBopusSearchForm = "${sessionScope.status}";
    BBB.siteTourEnabled = "<c:out value="${siteTourFlag}" />";
    BBB.siteTourCookieName = "<c:out value="${siteTourCookie}" />";

    BBB.config = BBB.config || {};
    BBB.config.s7ClientID = "<c:out value="${scene7ClientID}" />";
    BBB.config.catLevelCount = "${catLevelCount}";

    <c:choose>
        <c:when test="${currentSiteId eq BedBathCanadaSite}">
            BBB.config.country = {
                shortCode: 'CA',
                longCode: 'CAN',
                name: 'Canada'
            };
        </c:when>
        <c:otherwise>
            BBB.config.country = {
                shortCode: 'US',
                longCode: 'USA',
                name: 'United States of America'
            };
        </c:otherwise>
    </c:choose>
    
    <jsp:useBean id="dateTimeNow" class="java.util.Date" />
    BBB.config.datePickerDateToday = new Date('<fmt:formatDate value="${dateTimeNow}" pattern="MM.dd.yyyy" />'.replace(/\./g,'/'));
	BBB.config.datePickerDateTomorrow=new Date(BBB.config.datePickerDateToday.getFullYear(), BBB.config.datePickerDateToday.getMonth(), BBB.config.datePickerDateToday.getDate()+1);
    BBB.config.datePickerMinDate = new Date(BBB.config.datePickerDateToday.getFullYear()-2, BBB.config.datePickerDateToday.getMonth(), BBB.config.datePickerDateToday.getDate());
    BBB.config.datePickerMaxDate = new Date(BBB.config.datePickerDateToday.getFullYear()+5, BBB.config.datePickerDateToday.getMonth(), BBB.config.datePickerDateToday.getDate());
    
    /*** bbbLazyLoader ***/
    (function(doc) {
        var bbbLazyLoader = "bbbLazyLoader",
            api = window[bbbLazyLoader] = (window[bbbLazyLoader] || function() { api.ready.apply(null, arguments); }),
            head = doc.documentElement,
            isDomReady,
            queue = [],
            handlers = {},
            scripts = {},
            PRELOADED = 1,
            PRELOADING = 2,
            LOADING = 3,
            LOADED = 4;

        api.js = function() {
            var args = arguments,
                rest = [].slice.call(args, 1),
                next = rest[0],
                allargs = [].slice.call(arguments);

            if (!isDomReady) {
                each(args, function(arg) {
                    if (!isFunc(arg)) {
                        msgbox('info', 'Queued [ ' + toLabel(arg) + ' ]');
                    }
                });
                
                queue.push(function()  {
                    api.js.apply(null, args);
                });

                return api;
            }

            if (next) {
                load(getScript(args[0]), isFunc(next) ? next : function() {
                    api.js.apply(null, rest);
                });
            } else {
                load(getScript(args[0]));
            }

            return api;
        };

        function msgbox(type, msg) {
            /*if (window.console) {
                if (window.console[type]) {
                    <c:if test="${minifiedJSFlag == 'true'}">
                        window.console[type](msg);
                    </c:if>
                }
            }*/
        };
        
        api.ready = function(key, fn) {
            if (isFunc(key)) {
                fn = key;
                key = "ALL";
            }    

            if (typeof key != 'string' || !isFunc(fn)) { return api; }

            var script = scripts[key];

            if (script && script.state == LOADED || key == 'ALL' && allLoaded() && isDomReady) {
                one(fn);
                return api;
            }

            var arr = handlers[key];

            if (!arr) {
                arr = handlers[key] = [fn];
            } else {
                arr.push(fn);
            }

            return api;
        };
        
        api.fireReady = function() {
            if (!isDomReady) {
                msgbox('info', '>>> LAZY SCRIPT LOAD STARTED <<<');
                msgbox('time', 'TOTAL_TIME_TAKEN_BY_ALL_SCRIPTS');
                
                isDomReady = true;
                
                each(queue, function(fn) {
                    fn();
                });

                each(handlers.ALL, function(fn) {
                    one(fn);
                });
            }
        };

        /*** private functions ***/
        function one(fn) {
            if (fn._done) {
                return;
            }
            
            fn();
            fn._done = 1;
        }

        function toLabel(url) {
            var els = url.split("/"),
                name = els[els.length -1],
                i = name.indexOf("?");

            return i != -1 ? name.substring(0, i) : name;
        }

        function getScript(url) {
            var script;

            if (typeof url == 'object') {
                for (var key in url) {
                    if (url[key]) {
                        script = { name: key, url: url[key] };
                    }
                }
            } else {
                script = { name: toLabel(url),  url: url };
            }

            var existing = scripts[script.name];
            if (existing && existing.url === script.url) {
                return existing;
            }
            
            scripts[script.name] = script;

            return script;
        }

        function each(arr, fn) {
            if (!arr) { return; }

            if (typeof arr == 'object') {
                arr = [].slice.call(arr);
            }

            for (var i = 0; i < arr.length; i++) {
                fn.call(arr, arr[i], i);
            }
        }

        function isFunc(el) {
            return Object.prototype.toString.call(el) == '[object Function]';
        }

        function allLoaded(els) {
            els = els || scripts;

            var loaded;

            for (var name in els) {
                if (els.hasOwnProperty(name) && els[name].state != LOADED) {
                    return false;
                }
                
                loaded = true;
            }
            
            msgbox('info', '>>> LAZY SCRIPT LOAD FINISHED <<<');
            msgbox('timeEnd', 'TOTAL_TIME_TAKEN_BY_ALL_SCRIPTS');

            return loaded;
        }

        function onPreload(script) {
            msgbox('info', 'Preloaded [ ' + script.name + ' ]');
            msgbox('time', 'pre_' + script.name);

            script.state = PRELOADED;

            each(script.onpreload, function(el) {
                el.call();
            });
        }

        function preload(script, callback) {
            if (script.state === undefined) {
                msgbox('info', 'Preloading [ ' + script.name + ' ]');
                msgbox('time', 'pre_' + script.name);
            
                script.state = PRELOADING;
                script.onpreload = [];
                scriptTag({ src: script.url, type: 'cache'}, function()  {
                    onPreload(script);
                });
            }
        }

        function load(script, callback) {
            if (script.state == LOADED) {
                msgbox('info', 'Loaded [ ' + script.name + ' ]');
                msgbox('timeEnd', script.name);
                
                return callback && callback();
            }

            if (script.state == LOADING) {
                msgbox('info', 'Loading [ ' + script.name + ' ]');
                msgbox('time', script.name);
                return api.ready(script.name, callback);
            }

            if (script.state == PRELOADING) {
                return script.onpreload.push(function() {
                    load(script, callback);
                });
            }

            script.state = LOADING;
            msgbox('info', 'Loading [ ' + script.name + ' ]');
            msgbox('time', script.name);
                
            scriptTag(script.url, function() {
                script.state = LOADED;
                
                msgbox('info', 'Loaded [ ' + script.name + ' ]');
                msgbox('timeEnd', script.name);
                
                if (callback) {
                    callback();
                }

                each(handlers[script.name], function(fn) {
                    one(fn);
                });

                if (allLoaded() && isDomReady) {
                    each(handlers.ALL, function(fn) {
                        one(fn);
                    });
                }
            });
        }

        function scriptTag(src, callback) {
            var s = doc.createElement('script');

            s.type = 'text/' + (src.type || 'javascript');
            s.src = src.src || src;
            s.async = false;

            s.onreadystatechange = s.onload = function() {
                var state = s.readyState;

                if (!callback.done && (!state || /loaded|complete/.test(state))) {
                    callback.done = true;
                    callback();
                }
            };

            (doc.body || head).appendChild(s);
        }
    })(document);
    // -->
</script>

<%--BBBSL-8716 | PLP Fixes for BOTs --%>
<c:set var="isRobotOn" value="false" scope="request"/> 
<dsp:droplet name="/atg/repository/seo/BrowserTyperDroplet">
 <dsp:oparam name="output">
   <dsp:getvalueof var="browserType" param="browserType"/>
     <c:if test="${PageType eq 'SubCategoryDetails' || PageType eq 'Search'}">
         <c:set var="isRobotOn"  value="${browserType eq 'robot'}" scope="request"/>                       
         <c:set var="TeaLeafOn" value="${browserType ne 'robot'}" scope="request"/>
     </c:if>
 </dsp:oparam>
</dsp:droplet> 

  <%-- <c:import url ="/_includes/end_script.jsp" /> --%>
<script type="text/javascript" src="/store/_includes/end_script.jsp">

</script>
<bbbt:textArea key="txt_l1nodes_styling" language ="${pageContext.request.locale.language}"/>

<c:choose>
    <c:when test="${minifiedJSFlag == 'true'}">
        <script type="text/javascript" src="${jsPath}/_assets/global/js/mobileRedirect.min.js?v=${buildRevisionNumber}"></script>
    </c:when>
    <c:otherwise>       
        <script type="text/javascript" src="${jsPath}/_assets/global/js/mobileRedirect.js?v=${buildRevisionNumber}"></script>
    </c:otherwise>
</c:choose>

<%-- TeaLeaf needs to be first JS to be loaded as per reccomendation --%>
<c:if test="${bExternalJSCSS && bAnalytics && TeaLeafOn}">
            <%-- <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/tealeaf/TealeafSDKConfig.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/tealeaf/TealeafSDK.js?v=${buildRevisionNumber}"></script>--%>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/tealeaf/tealeaf-w3c-prod-min.js?v=${buildRevisionNumber}"></script> 
</c:if>

        <script type="text/javascript" src="${jsPath}/_assets/global/js/jquery-1.7.1.min.js?v=${buildRevisionNumber}"></script>


<%-- moved section/page type specific css includes to a separate file --%>
<c:import url="/_includes/head/section_head.jsp" />

<%--
    <dsp:getvalueof var="siteId" bean="Site.id" />
    <dsp:getvalueof var="siteCssFile" bean="Site.cssFile" />
    <c:if test="${not empty siteCssFile}">
      <link rel="stylesheet" href="${contextPath}/${siteCssFile}.css"
            type="text/css" media="screen" title="no title" charset="utf-8" />
            
 
    <%-- Possible alternative print style sheet modify the css style file --%>
    <%-- <link rel="stylesheet" href="${contextPath}/css/common_print.css"
          type="text/css" media="print" title="no title" charset="utf-8" />
    --%>
    <%--
    <script type="text/javascript" charset="utf-8"> --%>
      <%-- Javascript on css style --%>
      <%--
      document.write('<link rel="stylesheet" href="${contextPath}/css/javascript.css" type="text/css" charset="utf-8" />');

      <%-- Get rid of the IE rollover flicker. --%><%--
      try {
        document.execCommand('BackgroundImageCache', false, true);
      } catch(e) {}
    </script>
--%>
    <%-- Robots meta tag --%>
    <dsp:getvalueof var="index" param="index"/>
    <dsp:getvalueof var="follow" param="follow"/>
    
    <c:set var="indexValue" value="${(index eq 'false') ? 'noindex' : 'index'}"/>
    <c:set var="followValue" value="${(follow eq 'false') ? 'nofollow' : 'follow'}"/>
    <%-- BBBSL-2934 indexing only on Prod site not on Staging sites --%>
    <c:set var="serverDomainName" value="${pageContext.request.serverName}"/>
    <c:set var="productionDomainName"><bbbc:config key="productionDomainName" configName="ContentCatalogKeys"/></c:set>
	<c:choose>
		 <c:when test="${fn:contains(productionDomainName, serverDomainName)}">
			 <meta name="robots" content="${indexValue},${followValue}"/>
		</c:when>
		<c:otherwise>
			 <meta name="robots" content="${indexValue},${followValue}"/>
		</c:otherwise>	
	</c:choose>
	
	<%-- R2.2 : Created common JSP for pagination link for SEO of category, search and Brand pages : Start--%>
	<dsp:include page="/global/gadgets/seoPaginationURL.jsp">
		<dsp:param name="PageType" param="PageType"/>
	</dsp:include>
	<%-- R2.2 : Created common JSP for pagination link for SEO of category, search and Brand pages : End--%>
	
    <%-- Include content from SEO tag renderer --%>
    <dsp:getvalueof var="SEOTagRendererContent" param="SEOTagRendererContent"/>

     <%-- Rendering the clpTitle --%>
    <dsp:getvalueof var="clpTitle" param="clpTitle"/>
     <%-- BBBSL-9359 |Rendering the regTitle --%>
    <dsp:getvalueof var="regTitle" param="regTitle"/>

    <c:choose>
      <c:when test="${not empty SEOTagRendererContent }">
        <c:out value="${SEOTagRendererContent}" escapeXml="false"/>
      </c:when>
      <c:otherwise>
        <%-- Use default SEO tag renderer --%>
        <dsp:include page="/global/gadgets/metaDetails.jsp">
          <dsp:param name="titleString" param="titleString"/>
          <dsp:param name="contentKey" param="contentKey"/>
          <dsp:param name="categoryId" param="categoryId"/>
          <dsp:param name="productId" param="productId"/>
          <dsp:param name="guideId" param="guideId" />
          <dsp:param name="pageName" param="pageName" />
          <dsp:param name="brandId" param="brandId" />
          <dsp:param name="clpTitle" value="${clpTitle}" />
          <dsp:param name="regTitle" value="${regTitle}" />
          
        </dsp:include>
      </c:otherwise>
    </c:choose>

	<dsp:getvalueof var="canonicalURL" param="canonicalURL"/>
	<dsp:getvalueof var="alternateURL" param="alternateURL"/>
	<dsp:getvalueof var="canadaAlternateURL" param="canadaAlternateURL"/>
	<dsp:getvalueof var="usAlternateURL" param="usAlternateURL"/>
				
    <%-- Renders "canonical" tag --%>
    <dsp:include page="/global/gadgets/canonicalTag.jsp">
      <dsp:param name="categoryId" param="categoryId"/>
      <dsp:param name="productId" param="productId"/>
      <dsp:param name="guideId" param="guideId" />
      <dsp:param name="pageName" param="PageType"/>
      <dsp:param name="canonicalURL" value="${canonicalURL}"/>
      <dsp:param name="alternateURL" value="${alternateURL}"/>
      <dsp:param name="currentSiteId" value="${currentSiteId}"/>
      <dsp:param name="narrowDown" value="${narrowDown}"/>
    </dsp:include>

	<dsp:getvalueof var="canonicalURL" param="canonicalURL"/>
	<c:if test="${FBOn}">
	<c:if test="${not empty categoryId && empty productId}">
      <c:set var="pageFBType" value="category" />
    </c:if>
    <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
        <dsp:param name="configKey" value="FBAppIdKeys"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="fbConfigMapHEAD" param="configMap"/>
        </dsp:oparam>
    </dsp:droplet>
   
	<c:set var="prodId"><dsp:valueof value="${fn:escapeXml(param.productId)}"/></c:set>
    <c:set var="regId"><dsp:valueof value="${fn:escapeXml(param.registryId)}"/></c:set>
    <c:set var="regType"><dsp:valueof value="${fn:escapeXml(param.eventType)}"/></c:set>
    <c:set var="guideId"><dsp:valueof value="${fn:escapeXml(param.guideId)}"/></c:set>    
  
    <dsp:getvalueof var="appid" bean="Site.id" />
	
    <c:choose>
    <c:when test="${not empty prodId}">
    	<dsp:droplet name="/com/bbb/commerce/browse/droplet/FacebookImageDroplet">
			<dsp:param name="id" param="productId" />
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:oparam name="output">			
				 <dsp:getvalueof var="imageUrl" param="productImageLarge"/>
				
                        <c:set var="fbImage">${pageContext.request.scheme}:${scene7Path}/${imageUrl}</c:set>
				
			
			</dsp:oparam>
		</dsp:droplet>	
		<dsp:getvalueof var="pageFBType" value="product"/>
    </c:when>
   
   
    	<c:when test="${not empty regId && not empty regType}">
                <dsp:getvalueof var="fbPageTitle" value="${regType} Registry"/>
                <dsp:getvalueof var="fbDesc" value="${regType} Registry Number: ${regId}"/>
                <dsp:getvalueof var="fbUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}${contextPath}/giftregistry/view_registry_guest.jsp?registryId=${regId}&eventType=${regType}" />
		<dsp:getvalueof var="pageFBType" value="registry"/>
    	</c:when>
	<c:when test="${not empty guideId}">
			<dsp:droplet name="GuidesLongDescDroplet">
				<dsp:param name="guideId" value="${guideId}" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="fbPageTitle" param="guidesLongDesc.title" />
					<dsp:getvalueof var="fbDesc" param="guidesLongDesc.shortDescription" />
					<dsp:getvalueof var="imageUrl" param="guidesLongDesc.imageUrl" />
					<dsp:getvalueof var="fbImage" value="${pageContext.request.scheme}:${imageUrl}" />
				</dsp:oparam>
			</dsp:droplet>
			<dsp:getvalueof var="pageFBType" value="guide"/>
		</c:when>
    	
    </c:choose>
    
    <c:if test="${empty fbImage}">
		<c:choose>
			<c:when test="${param.eventType eq 'Wedding'}">          
               <dsp:getvalueof var="fbImage"
                    value="${pageContext.request.scheme}://${pageContext.request.serverName}/_assets/global/images/faceBookShare/RegistryAnnouncement_v4.jpg" />                     
            </c:when>
			<c:when test="${cssCurrentSiteId eq cssBuyBuyBabySiteCode}">
				<dsp:getvalueof var="fbImage"
					value="${pageContext.request.scheme}://${pageContext.request.serverName}/_assets/global/images/logo/logo_bb_circle.png" />
			</c:when>
			<c:when test="${cssCurrentSiteId eq cssBedBathCanadaSiteCode}">
				<dsp:getvalueof var="fbImage"
					value="${pageContext.request.scheme}://${pageContext.request.serverName}/_assets/global/images/logo/logo_bbb_ca.png" />
			</c:when>           
			<c:otherwise>
				<dsp:getvalueof var="fbImage"
					value="${pageContext.request.scheme}://${pageContext.request.serverName}/_assets/global/images/logo/logo_bbb.png" />
			</c:otherwise>
		</c:choose>
	</c:if>
	
        <meta property="og:title" content="<c:out value="${fbPageTitle}"/>"/>
        <meta property="og:image" content="<c:out value="${fbImage}"/>"/>
		<c:if test="${empty fbDesc}">
				<c:set var="fbDesc" value="${fbPageTitle}" />
			</c:if>
        <meta property="og:description" content="<c:out value="${fbDesc}"/>"/>
    <meta property="og:url" content="<c:out value="${fbURL}"/>"/>
    <meta property="fb:app_id" content="${fbConfigMapHEAD[currentSiteId]}" />
    <meta property="og:type" content="${pageFBType}"/>
</c:if>

    <%--
    NOTE: it is a must that four icon files are provided..
        two "favicon.ico & favicon.png" for bed bath & beyond (us and canada)
        and two "favicon_bb.ico & favicon_bb.png" for buy buy baby
    --%>
    <dsp:getvalueof var="faviconUrl" vartype="java.lang.String" bean="/atg/multisite/Site.favicon"/>
    <c:if test="${currentSiteId eq BuyBuyBabySite}">
        <c:set var="iconTheme" value="_bb" scope="request" />
    </c:if>
    <c:if test="${empty faviconUrl}">
        <c:set var="faviconUrl" scope="session">/favicon${iconTheme}.ico</c:set>
    </c:if>

    <link rel="apple-touch-icon" href="${fn:replace(faviconUrl, '.ico', '.png')}" />
    <link rel="shortcut icon" type="image/ico" href="${fn:replace(faviconUrl, '.png', '.ico')}" />
    <link rel="icon" type="image/ico" href="${fn:replace(faviconUrl, '.png', '.ico')}">
    
	<c:if test="${fn:indexOf(pageWrapper, 'printCardsAtHomeEditor') > -1 }">
			<link href='//fonts.googleapis.com/css?family=Libre+Baskerville|Tangerine|Playball|Open+Sans+Condensed:300|Julius+Sans+One|Raleway' rel='stylesheet' type='text/css'>
	</c:if>
    <dsp:include page="/includes/pageStartScript.jsp"/>
	<%-- Begin TagMan --%>

	<c:if test="${TagManOn}">
	<c:choose>
		<c:when test="${empty profileId && ((fn:indexOf(pageWrapper, 'errorPages') > -1) || (fn:indexOf(pageWrapper, 'serverErrorPages') > -1) || (fn:indexOf(pageWrapper, '500') > -1))}">			
		</c:when>
		<c:otherwise>
		<dsp:include page="/tagman/header.jsp" >
			<dsp:param name="countryCode" value="${valueMap.defaultUserCountryCode}"/>
			<dsp:param name="currencyCode" value="${valueMap.defaultUserCurrencyCode}"/>
		</dsp:include>
		<c:if test="${(PageType eq 'SingleShipping') || (PageType eq 'MultiShipping')}">
			<dsp:include page="/tagman/includes/shipping.jsp" />
		</c:if>
		<c:if test="${PageType eq 'Billing' }">
			<dsp:include page="/tagman/includes/billing.jsp" />
		</c:if>
		<c:if test="${PageType eq 'Payment' }">
			<dsp:include page="/tagman/includes/billing_payment.jsp" />
		</c:if>
		<c:if test="${PageType eq 'Gifting' }">
			<dsp:include page="/tagman/includes/gifting.jsp" />
		</c:if>
		<c:if test="${PageType eq 'Login' }">
			<dsp:include page="/tagman/includes/login.jsp" />
		</c:if>
		<c:if test="${PageType eq 'CartDetails' }">
			<dsp:include page="/tagman/includes/cart.jsp" />
		</c:if>
		<c:if test="${PageType eq 'CategoryLandingDetails' }">			
			<dsp:include page="/tagman/includes/categoryLanding.jsp" />
		</c:if>
		<c:if test="${PageType eq 'CheckoutConfirmation' }">
			<dsp:include page="/tagman/includes/checkoutConfirmation.jsp" />
		</c:if>
		<c:if test="${PageType eq 'MyAccountDetails' }">
			<dsp:include page="/tagman/includes/myAccount.jsp" />
		</c:if>
		<c:if test="${PageType eq 'ProductDetails' }">
			<dsp:include page="/tagman/includes/productDetails.jsp" />
		</c:if>
		<c:if test="${PageType eq 'FindStore' }">
			<dsp:include page="/tagman/includes/storeLocator.jsp" />
		</c:if>
		<c:if test="${PageType eq 'SubCategoryDetails' }">
			<c:choose>
				<c:when test="${not empty frmBrandPage && frmBrandPage eq true}">
					<dsp:include page="/tagman/includes/search.jsp" />
				</c:when>
				<c:otherwise>
					<dsp:include page="/tagman/includes/subcategory.jsp" />
				</c:otherwise>
			</c:choose>
		</c:if>
		<c:if test="${not empty hoorayModal}">
			<dsp:include page="/tagman/includes/registryConfirmation.jsp" />
		</c:if>
		<c:if test="${PageType eq 'Search' }">
			<dsp:include page="/tagman/includes/search.jsp" />
		</c:if>
		<c:if test="${PageType eq 'ContactUs' }">
			<dsp:include page="/tagman/includes/contactus.jsp" />
		</c:if>
		<c:if test="${PageType eq 'HomePage' }">
			<dsp:include page="/tagman/includes/homepage.jsp" />
		</c:if>
		<c:if test="${PageType eq 'StaticPage' }">
			<dsp:include page="/tagman/includes/staticpage.jsp" />
		</c:if>
		<c:if test="${PageType eq 'WalletRegistration' }">
			<dsp:include page="/tagman/includes/walletRegistration.jsp" />
		</c:if>
		<c:if test="${PageType eq 'WalletLanding' }">
			<dsp:include page="/tagman/includes/walletLanding.jsp" />
		</c:if>
		<c:if test="${PageType eq 'accountCoupons' }">
			<dsp:include page="/tagman/includes/coupons.jsp" />
		</c:if>
		<dsp:include src="/tagman/frag/tagman_bottom_script.jsp"/>
	</c:otherwise>
	</c:choose>
	</c:if>	
	
	<%-- End TagMan --%>
	<c:if test="${YourAmigoON}">
    <%-- Start YourAmigo Web Analytics --%>
        <script type="text/javascript">
        function loadYawaJS() {
        	//<![CDATA[
        	var yawa_cmds = yawa_cmds || [];
	        var yourAmigoSiteId = ${yourAmigoSiteId};
	        (function() {
	        var _yawa = document.createElement("script"); _yawa.type ="text/javascript";
	        _yawa.async = true;     
	        _yawa.src = "//d3oq49z2nwxwn1.cloudfront.net/yawa/"+ yourAmigoSiteId +"/yawa.min.js";
	        var _yawa_s = document.getElementsByTagName("script")[0];
	        _yawa_s.parentNode.insertBefore(_yawa, _yawa_s);
	        }());
	        //]]>
        }
        
        if (window.attachEvent) {
	        window.attachEvent('onload', loadYawaJS);
	    }
	    else {
	        window.addEventListener('load', loadYawaJS, false);
	    }
        </script>
    <%-- End YourAmigo Web Analytics Code --%>
    </c:if>
<%-- Head Tag Content of StaticTemplate/RegistryTemplate --%>
${headTagContent}

<%-- Script added to set performance marks before JS loads --%>
<script type="text/javascript">
var BBB = BBB || {};
BBB.addPerfMark = function (markName) {
    if (window.performance && window.performance.mark) {
        window.performance.clearMarks(markName);
        window.performance.mark(markName);
    }
}
BBB.addPerfMeasure = function (measureName, beginningMarkName, endMarkName) {
    if (window.performance && window.performance.measure) {
        window.performance.clearMeasures(measureName);
        window.performance.measure(measureName, beginningMarkName, endMarkName);
    }
}
</script>
<c:choose>
        <c:when test="${currentSiteId == BuyBuyBabySite or currentSiteId == BedBathUSSite}">
         <style>
           #ssOurBrands-textLinks li a,.bb #ssOurBrands-textLinks li a{color:#666!important;font-size:11px!important;padding:5px 5px 0!important;font-weight:400!important;font-family:FuturaStdMedium!important;text-align:center;line-height:26px!important}.bb #headerWrapper #topNavMenu>ul>li a{background:0 0;color:#02689d!important;font-size:12px!important;font-family:Arial;font-weight:700}#headerWrapper #topNavMenu ul li:first-child,.bb #headerWrapper #topNavMenu ul li:first-child{min-width:90.19px}.bb #headerWrapper #promoArea.promoMargin{margin:12px 0 0 -32px!important}.bb #ssOurBrands-textLinks li a{color:#02689d!important;font-family:Helvetica,Arial,sans-serif!important}
        </style> 
        </c:when>
         <c:otherwise>
        <style>
        #headerWrapper #siteLogo a.logoBabyCA{width:auto}#topNavMenu #ssOurBrands-textLinks ul li a{font-weight:700}#headerWrapper #promoArea.promoMargin{margin:9px -15px 0 0!important}#headerWrapper #siteLogo a:first-child{width:190px}
        </style>
        </c:otherwise>
</c:choose>   
 </head>
    
  <dsp:getvalueof var="bodyClass" param="bodyClass"/>
  <dsp:getvalueof var="noBGClass" param="noBGClass"/>
  <body id="themeWrapper" class="${themeName}  ${noBGClass}  ${BabyCaClass}    ${vbvCFrameFix}"    ${vbvStyleTag}    ${vbvBodyOnLoad} >
	<input type="hidden" value="${PageType}" id="PageType"/>
    <c:if test="${(PageType ne 'SingleShipping') and (PageType ne 'MultiShipping') and (PageType ne 'Billing') and (PageType ne 'Payment') and (PageType ne 'Gifting') and (PageType ne 'CheckoutConfirmation') and (PageType ne 'Preview')}">
        <a class="skip" href="#hero">Skip to Primary Content </a>
        <c:choose>
            <c:when test="${currentSiteId == BedBathCanadaSite}">
                <a class="skip" href="#department-navigationCA">Skip to Navigation </a>
            </c:when>
            <c:otherwise>
                <a class="skip" href="#department-navigation">Skip to Navigation </a>
            </c:otherwise>
        </c:choose>
    </c:if>
    <noscript>
        <bbbt:textArea key="txt_js_disabled_msg"  language ="${pageContext.request.locale.language}"/>
    </noscript>
    
   <c:if test="${OutdatedBrowserMsgOn}">
    <dsp:getvalueof var="isPopup" param="isPopup"/>
    <c:if test="${!isPopup}">
	    <bbbt:textArea key="txt_outdated_browser_msg1"  language ="${pageContext.request.locale.language}"/>
	    <bbbt:textArea key="txt_outdated_browser_msg2"  language ="${pageContext.request.locale.language}"/>
    </c:if>
   </c:if>

  <dsp:droplet name="/com/bbb/logging/NetworkInfoDroplet">
    <dsp:oparam name="output">
		<!--googleoff: all-->
		<%--BBBSL-1822 Start added usernetworkinfo inside html comments to avoid being read by search engines--%>
		<!-- 
        <div id="userNetworkInfo" class="hidden"> 
		<ul> 
			<li id="session_id"><dsp:valueof param="SESSION_ID" /></li> 
			<li id="jvm_name"><dsp:valueof bean="/atg/dynamo/service/IdGenerator.dcPrefix"/>-<dsp:valueof param="JVM_NAME" /></li>
			<li id="time"><dsp:valueof param="TIME" /></li> 
			</ul> 
		</div>
		 -->
		 <%--BBBSL-1822 end --%>
		 
		<!--googleon: all-->
    </dsp:oparam>
</dsp:droplet>
<input type='hidden' id='hiddenCssPath' value="${cssPath}"/>

<!-- Code is for getting exact browser url from request Starts-->
<dsp:getvalueof var="requestHost" bean="/OriginatingRequest.host"/>
<c:set var="includeContextPath"> <%=ServletUtil.getCurrentRequest().getAttribute("javax.servlet.include.context_path")%></c:set>
<c:set var="forwardRequestURI"> <%=ServletUtil.getCurrentRequest().getAttribute("javax.servlet.forward.request_uri")%></c:set>
<c:set var="getRequestURI"> <%=ServletUtil.getCurrentRequest().getRequestURI()%></c:set>
<c:set var="forwardContextPath"> <%=ServletUtil.getCurrentRequest().getAttribute("javax.servlet.forward.context_path")%></c:set>
<c:set var="forwardQueryString"> <%=ServletUtil.getCurrentRequest().getAttribute("javax.servlet.forward.query_string")%></c:set>
<c:set var="QueryString"> <%=ServletUtil.getCurrentRequest().getQueryString()%></c:set>
<c:set var="currentUrl" value="${schemeName}://${requestHost}"/> 
<c:choose>
<c:when test="${!empty forwardRequestURI && forwardRequestURI ne 'null'}">
	<c:set var="currentUrl" value="${currentUrl}${forwardRequestURI}"/>
	<c:if test="${!empty forwardQueryString && forwardQueryString ne 'null'}">
		<c:set var="currentUrl" value="${currentUrl}?${forwardQueryString}"/>
	</c:if>
</c:when>
<c:when test="${!empty getRequestURI && getRequestURI != null && getRequestURI != 'null' }">
	<c:set var="currentUrl" value="${currentUrl}${getRequestURI}"/>
	<c:if test="${!empty QueryString && QueryString ne 'null'}">
		<c:set var="currentUrl" value="${currentUrl}?${QueryString}"/>
	</c:if>
</c:when>
<c:otherwise>
	<c:if test="${!empty includeContextPath && includeContextPath ne 'null'}">
		<c:set var="currentUrl" value="${currentUrl}${includeContextPath}"/>
	</c:if>
</c:otherwise>
</c:choose>
<input type='hidden' id='latestUrl' value="${fn:escapeXml(currentUrl)}" />
<!-- Code is for getting exact browser url from request Ends-->
<script type="text/javascript">
var isTransient = '${isTransient}';
var maxRetryEDWRepo = '${maxRetryEDwRepo}';
var isDynUserCookieExists = '${isDynUserCookieExists}';
</script>

<%
String siteSpectHeader = request.getHeader("X-personalize");
if (siteSpectHeader != null && siteSpectHeader.length() > 0 && siteSpectHeader.equalsIgnoreCase("true")) {
%>

<script type="text/javascript">
var profile_data = '${personalizationData}';
</script>
<%
}
%>

<%-- Retrieving Vendor Parameter for Vendor Story --%>
<dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
<dsp:getvalueof var="catId" param="categoryId"/>
<c:set var="vendorFlag"><bbbc:config key="VendorFlagOn" configName="VendorKeys"/></c:set>
<!-- <script>
	// adblock
	var test = document.createElement('div');
	test.innerHTML = '&nbsp;';
	test.className = 'adsbox';
	document.body.appendChild(test);
	window.setTimeout(function() {
		  if (test.offsetHeight === 0) {
		    //document.body.classList.add('adblock');
		    alert("ad blocker present");
		  }
		  test.remove();
		}, 100);
</script> -->
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/includes/pageStart.jsp#2 $$Change: 635969 $--%>
