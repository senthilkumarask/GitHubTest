<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/includes/taglibs.jspf" %>

<dsp:page>


    <dsp:importbean bean="/com/bbb/internationalshipping/droplet/InternationalOrderEnvoyDroplet" />
    <dsp:importbean bean="/atg/multisite/Site"/>

    <dsp:droplet name="InternationalOrderEnvoyDroplet">
        <dsp:param name="countryCode" param="countryCode" />
        <dsp:param name="currencyCode" param="currencyCode" />
        <dsp:param name="fraudState" param ="fraudState"/>
        <dsp:param name="orderId" param="orderId" />
        <dsp:param name="merchantOrderId" param="merchantOrderId" />
    </dsp:droplet>

	<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
		<dsp:oparam name="output">
			<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM" />
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="/com/bbb/commerce/browse/droplet/ConfigURLDroplet">
        <dsp:param value="ThirdPartyURLs" name="configType" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="jsPath" param="jsPath" scope="request"/>
        </dsp:oparam>
    </dsp:droplet>

    <dsp:getvalueof id="currentSiteId" bean="Site.id" />
    <dsp:getvalueof var="merchantId" param="merchantOrderId"/>
    <dsp:getvalueof var="countryCode" param="countryCode"/>
    <dsp:getvalueof var="orderTotal" param="orderTotal" /> 
	<dsp:getvalueof var="omniOrderTotal" value="${orderTotal}" vartype="java.lang.Double"/>
	
    <!doctype html>
    <html>
        <head>
            <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
            <meta http-equiv="content-type" content="text/html; charset=utf-8" />
            <meta http-equiv="pragma" content="no-cache">
            <meta http-equiv="cache-control" content="no-cache">
            <meta name="robots" content="noindex, nofollow" />
            <title><bbbl:label key="lbl_spc_metadetails_title" language="${pageContext.request.locale.language}"/></title>
            <c:choose>
                <c:when test="${currentSiteId == 'BuyBuyBaby'}">
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/omniture/buybuybaby_s_code.js?v=${buildRevisionNumber}"></script>
                </c:when>
                <c:when test="${currentSiteId == 'BedBathUS'}">
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/omniture/bbb_s_code.js?v=${buildRevisionNumber}"></script>
                </c:when>
                <c:otherwise>
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/omniture/bbbca_s_code.js?v=${buildRevisionNumber}"></script>
                </c:otherwise>
            </c:choose>
            <script type="text/javascript">
                function runOmniOnLoad() {
                    if (typeof s != "undefined") {
                        s.pageName = 'Check Out>International Shipping Details';
                        s.channel = 'Check Out';
                        s.prop1 = 'Check Out';
                        s.prop2 = 'Check Out';
                        s.prop3 = 'Check Out';
                        s.prop16 = "non registered user";
                        s.events = "purchase,event67=${omniOrderTotal}";
                        s.eVar11="${merchantId}";
                        s.purchaseID = "${merchantId}";
                        s.eVar14 = "BorderFree";
                        s.eVar16 = "non registered user";
                        s.eVar52 = "${countryCode}";
                        var s_code = s.t();
                        if(s_code) document.write(s_code);
                    }
                }
            </script>
        </head>
        <body onload="runOmniOnLoad();">
            &nbsp;&nbsp;&nbsp;
            <%--
            <script type="text/javascript">
                if (typeof s != "undefined") {
                    s.pageName = 'Check Out>International shipping details';
                    s.channel = 'Check Out';
                    s.prop1 = 'Check Out';
                    s.prop2 = 'Check Out';
                    s.prop3 = 'Check Out';
                    s.prop16 = "non registered user";
                    s.events = "purchase,event67=${orderTotal}";
                    s.purchaseID = "${merchantId}";
                    s.eVar14 = "BorderFree";
                    s.eVar16 = "non registered user";
                    s.eVar52 = "${countryCode}";
                    var s_code = s.t();
                    if (s_code) { document.write(s_code); }
                }
            </script>
            --%>
            &nbsp;&nbsp;&nbsp;
        </body>
    </html>
</dsp:page>