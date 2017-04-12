<%--
Tag that acts as a container for all top level pages, including all relevant
header, footer and nav elements.
The body of this tag should include any required gadgets.

If any of the divId, titleKey or textKey attributes are set, then the
pageIntro gadget will be included.  If none of these attributes are
specified, then the pageIntro gadget will not be included.

This tag accepts the following attributes
divId (optional) - id for the containing div. Will be passed to the pageIntro
					gadget.
copyrightDivId (optional) - id for the containing div. Will be passed to the
							copyright gadget. If value is not specified then
							default value will be used.
bodyClass (optional) - class name that will be used in the page's <body> tag.
contentClass (optional) - class name that will be used for the page's 'content' <div> tag.
titleKey (optional)- resource bundle key for the title. Will be passed to the
					pageIntro gadget.
textKey (optional) - resource bundle key for the intro text. Will be passed
					to the pageIntro gadget.
titleString (optional) - Title String  that will be passed to the pageIntro gadget
textString (optional) - Intro text string that will be passed to the pageIntro gadget
index (optional) - indexing instruction passed to robot <meta> tag, takes "true|false"
					value and specifies whether a page should be indexed by search robots,
					"true" is default
follow (optional) - indexing instruction passed to robot <meta> tag, takes "true|false"
					value and specifies whether a page should be followed by search robots,
					"true" is default

The tag accepts the following fragments
navigationAddition - define a fragment that will be included at the end
					of the left nav gadgets.  If required, use as
						<jsp:attribute name="leftNavigationAddition">
						....
						</jsp:attribute>
subNavigation - define a fragment that will be contain sub navigation gadgets.
				If required, use as
					<jsp:attribute name="subNavigation">
					....
					</jsp:attribute>
SEOTagRenderer - define a fragment that will render SEO meta tags.
				If required, use as
					<jsp:attribute name="SEOTagRenderer">
					....
					</jsp:attribute>

levelNeeded
	string representation of level required for displaying the current page. All levels defined in the
	/atg/store/states/CheckoutProgressStates.checkoutProgressLevels property

redirectURL
	if current page is not allowed, request will be redirected to this URL
--%>

<%@ include file="/includes/taglibs.jspf" %>
<%@ include file="/includes/context.jspf" %>

<%@ tag import="java.util.*, java.lang.*" %>
<%@ tag language="java" %>

<%@ attribute name="divId" %>
<%@ attribute name="copyrightDivId" %>
<%@ attribute name="bodyClass" %>
<%@ attribute name="contentClass" %>
<%@ attribute name="titleKey" %>
<%@ attribute name="textKey" %>
<%@ attribute name="titleString" %>
<%@ attribute name="textString" %>
<%@ attribute name="index" %>
<%@ attribute name="follow" %>
<%@ attribute name="nextHref" %>
<%@ attribute name="prevHref" %>
<%@ attribute name="navigationAddition" fragment="true" %>
<%@ attribute name="subNavigation" fragment="true" %>
<%@ attribute name="SEOTagRenderer" fragment="true"%>
<%@ attribute name="headerRenderer" fragment="true"%>
<%@ attribute name="footerContent" fragment="true"%>
<%@ attribute name="levelNeeded" %>
<%@ attribute name="redirectURL" %>
<%@ attribute name="section" %>
<%@ attribute name="homepage" %>
<%@ attribute name="themeWrapper" %>
<%@ attribute name="pageWrapper" %>
<%@ attribute name="themeFolder" %>
<%@ attribute name="pageVariation" %>
<%@ attribute name="PageType" %>
<%@ attribute name="headTagContent" %>
<%@ attribute name="bodyEndTagContent" %>
<%@ attribute name="amigoMeta" fragment="true"%>

<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>

<jsp:invoke fragment="amigoMeta" var="amigoMetaContent"/>
<jsp:invoke fragment="SEOTagRenderer" var="SEOTagRendererContent"/>
<jsp:invoke fragment="headerRenderer" var="headerContent"/>
<jsp:invoke fragment="footerContent" var="footerLastContent"/>

<dsp:importbean bean="/atg/multisite/Site"/>

<dsp:getvalueof id="currentSiteId" bean="Site.id" scope="session"/>
<dsp:getvalueof id="cssFile" bean="Site.cssFile"/>
<dsp:getvalueof id="siteURL" bean="Site.productionURL"/>
<dsp:getvalueof var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<dsp:getvalueof var="contentKey" param="contentKey"/>
<c:set var="compressHTMLFlag" scope="request"><bbbc:config key="compressHTMLFlag" configName="FlagDrivenFunctions" /></c:set>
<compress:html enabled="${compressHTMLFlag}" removeComments="false">

	<dsp:droplet name="/com/bbb/commerce/browse/droplet/ConfigURLDroplet">
		<dsp:param value="ThirdPartyURLs" name="configType" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="imagePath" param="imagePath" scope="request"/>
			<dsp:getvalueof var="cssPath" param="cssPath" scope="request"/>
			<dsp:getvalueof var="jsPath" param="jsPath" scope="request"/>
			<dsp:getvalueof var="scene7Path" param="scene7Path" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>

	<c:choose>
		<c:when test="${currentSiteId == 'BedBathUS'}">
			<c:set var="themeFolder" value="${cssFile}" scope="session" />
			<c:set var="themeName" value="by" scope="session" />
		</c:when>
		<c:when test="${currentSiteId == 'BedBathCanada'}">
			<c:set var="themeFolder" value="${cssFile}" scope="session" />
			<c:set var="themeName" value="by" scope="session" />
		</c:when>
		<c:when test="${currentSiteId == 'BuyBuyBaby'}">
			<c:set var="themeFolder" value="${cssFile}" scope="session" />
			<c:set var="themeName" value="bb" scope="session" />
		</c:when>
		<c:otherwise>
			<c:set var="themeFolder" value="bbbaby" scope="session" />
			<c:set var="themeName" value="bb" scope="session" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${pageVariation == 'br'}">
			<c:set var="themeFolder" value="bbregistry" scope="session" />
			<c:set var="themeName" value="br" scope="session" />
		</c:when>
		<c:when test="${pageVariation == 'bc'}">
			<c:set var="themeFolder" value="bbcollege" scope="session" />
			<c:set var="themeName" value="bc" scope="session" />
		</c:when>
	</c:choose>

	<dsp:include page="/includes/pageStart.jsp">
		<dsp:param name="bodyClass" value="${bodyClass}"/>
		<dsp:param name="titleString" value="${titleString}"/>
		<dsp:param name="section" value="${section}"/>
		<dsp:param name="index" value="${index}"/>
		<dsp:param name="prevHref" value="${prevHref}"/>
		<dsp:param name="nextHref" value="${nextHref}"/>
		<dsp:param name="follow" value="${follow}"/>
		<dsp:param name="SEOTagRendererContent" value="${SEOTagRendererContent}" />
		<dsp:param name="pageWrapper" value="${pageWrapper}"/>
		<dsp:param name="contentKey" value="${contentKey}"/>
		<dsp:param name="amigoMeta" value="${amigoMetaContent}"/>
		<dsp:param name="PageType" value="${PageType}"/>
		<dsp:param name="headTagContent" value="${headTagContent}"/>
	</dsp:include>

	<%-- <div id="pageWrapper" class="${pageWrapper}"> --%>
		<c:set var="sessionTimeout">
				<bbbc:config key="SessionTimeoutTbs" configName="HTMLCacheKeys" />
		</c:set>
		<c:set var="sessionTimeoutStart">
			<bbbc:config key="SessionRemainingTimeMessageTbs" configName="HTMLCacheKeys" />
		</c:set> 
		<c:choose>
			<c:when test="${(sessionTimeout > sessionTimeoutStart) && ((sessionTimeout - sessionTimeoutStart) >= 5)}">
				<c:set var="sessionTimeoutMessageStart" value="${sessionTimeoutStart}"/>      
			</c:when>
			<c:otherwise>
				<c:set var="sessionTimeoutMessageStart" value="${((sessionTimeout - 5) > 0)?(sessionTimeout - 5):0}"/>
			</c:otherwise>
		</c:choose>  
		<input type="hidden" id="timeOutTime" value=" ${sessionTimeout}">
		<input type="hidden" id="sessionTimeoutMessageStart" value="${sessionTimeoutMessageStart}">
			<div id="sessionTimeOut" class="reveal-modal small" data-reveal>
                                <p class="beforeSessionExpire"><bbbl:label key='lbl_session_ramaining_time_to_expire' language="${pageContext.request.locale.language}" /></p>
                                <p class="afterSessionExpire none"><bbbl:label key='lbl_session_expired_message' language="${pageContext.request.locale.language}" /></p>
                                <button class="button continueSession"><bbbl:label key='lbl_removeccmodal_ok' language="${pageContext.request.locale.language}" /></button>
                                <a class="close-reveal-modal">&#215;</a>
			</div>
		<c:choose>
			<c:when test="${not empty headerContent}">
				<c:out value="${headerContent}" escapeXml="false"/>
			</c:when>
			<c:otherwise>
				<%-- Use default header tag renderer --%>
				<dsp:include page="/_includes/header/header.jsp">
					<dsp:param name="homepage" value="${homepage}"/>
					<dsp:param name="PageType" value="${PageType}"/>
					<dsp:param name="pageWrapper" value="${pageWrapper}"/>
				</dsp:include>
			</c:otherwise>
		</c:choose>

		<%--
		<dsp:include page="/navigation/gadgets/search.jsp" flush="true"/>

		<dsp:include page="/navigation/gadgets/welcome.jsp" flush="true"/>
		<dsp:getvalueof id="repriceOrder" param="repriceOrder"/>

		<c:if test="${repriceOrder}">
			<dsp:include page="/global/gadgets/orderReprice.jsp"/>
		</c:if>
		--%>

		<jsp:doBody/>

		<%-- jsp:invoke fragment="navigationAddition"/>
		<jsp:invoke fragment="subNavigation"/--%>

		<%-- start footer gadgets --%>
		<%-- footer --%>

		<%-- KP COMMENT START: Dropping old footer --%>
		<%--
		<c:import url="/_includes/footer/footer.jsp" />
		--%>
		<%-- KP COMMENT END --%>

		<%-- end footer --%>

	<%-- </div> --%>

	<c:choose>
		<c:when test="${pageVariation == 'br'}">
			<c:set var="themeFolder" value="bbregistry" scope="session" />
			<c:set var="themeName" value="br" scope="session" />
		</c:when>
		<c:when test="${pageVariation == 'bc'}">
			<c:set var="themeFolder" value="bbcollege" scope="session" />
			<c:set var="themeName" value="bc" scope="session" />
		</c:when>
	</c:choose>

	<dsp:include page="/includes/pageEnd.jsp">
		<dsp:param name="section" value="${section}"/>
		<dsp:param name="pageWrapper" value="${pageWrapper}"/>
		<dsp:param name="PageType" value="${PageType}"/>
		<dsp:param name="themeFolder" value="${themeFolder}"/>
		<dsp:param name="footerLastContent" value="${footerLastContent}"/>
		<dsp:param name="bodyEndTagContent" value="${bodyEndTagContent}"/>
	</dsp:include>

</compress:html>
<%-- @version $Id:  $$Change:  $ --%>
