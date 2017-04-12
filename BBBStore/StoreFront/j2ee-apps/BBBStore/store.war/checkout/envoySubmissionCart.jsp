<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/includes/taglibs.jspf" %>

<dsp:page>


    <dsp:importbean bean="/com/bbb/internationalshipping/droplet/InternationalOrderEnvoyDroplet" />
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
    <dsp:droplet name="InternationalOrderEnvoyDroplet">
        <dsp:param name="countryCode" param="countryCode" />
        <dsp:param name="currencyCode" param="currencyCode" />
        <dsp:param name="fraudState" param ="fraudState"/>
        <dsp:param name="orderId" param="orderId" />
        <dsp:param name="merchantOrderId" param="merchantOrderId" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="omnitureProductString" param="omnitureProductString" scope="request"/>
			<dsp:getvalueof var="orderSubmitVO" param="orderSubmitVO" />
		</dsp:oparam>
    </dsp:droplet>
    <dsp:getvalueof var="countryCode" param="countryCode"/>
	<c:set var="userId" value=""/>

   
    <dsp:droplet name="/com/bbb/commerce/browse/droplet/ConfigURLDroplet">
        <dsp:param value="ThirdPartyURLs" name="configType" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="jsPath" param="jsPath" scope="request"/>
        </dsp:oparam>
    </dsp:droplet>
    <script type="text/javascript" src="${jsPath}<bbbc:config key="js_certona" configName="ThirdPartyURLs" /> "></script>
    <!-- BBBSL-5166- Code changes for RKG Pixels || Start -->
	    <c:if test="${countryCode ne 'MX'}" >
			<c:set var="rkgSaleUrl"><bbbc:config key="rkg_referral_sale_url" configName="ThirdPartyURLs" /></c:set>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" value="${orderSubmitVO.rkgItemURLList}" />
				<dsp:param name="elementName" value="rkgItemUrl" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="itemRKG" param="rkgItemUrl" />
					<dsp:getvalueof var="rkgRefUrl" value="${rkgSaleUrl}${itemRKG}" />
					<img src="${rkgRefUrl}" height="1" width="1" alt="rkgRefUrl" />
				</dsp:oparam>
			</dsp:droplet>
	  	<!-- BBBSL-5166- Code changes for RKG Pixels || End-->
	  	<!-- BBBSL-5166- Code changes for Commission Junction Pixels || Start -->
			<c:set var="currencyId">USD</c:set>			
	    	<img src="${orderSubmitVO.cjParamMap.cj_base_url}cid=${orderSubmitVO.cjParamMap.cj_cid}&oid=${orderSubmitVO.cjParamMap.genOrderCode}&${orderSubmitVO.cjParamMap.cj_item_appended_url}&currency=${currencyId}&type=${orderSubmitVO.cjParamMap.cj_type}&method=img" height="1" width="20">
	    </c:if>
  	<!-- BBBSL-5166- Code changes for Commission Junction Pixels || End -->
    <dsp:getvalueof id="currentSiteId" bean="Site.id" />
    <dsp:getvalueof var="merchantId" param="merchantOrderId"/>
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
            <title><bbbl:label key="lbl_metadetails_title" language="${pageContext.request.locale.language}"/></title>
            <script type="text/javascript">
				s = parent.s
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
                        s.products = "${omnitureProductString}";
                        s.purchaseID = "${merchantId}";
                        s.eVar14 = "BorderFree";
                        s.eVar16 = "non registered user";
                        s.eVar52 = "${countryCode}";
                        var s_code = s.t();
                        if(s_code) document.write(s_code);
                    }
                }
            </script>
			<c:if test="${countryCode ne 'MX'}" >
            <script type="text/javascript">
		            var resx = new Object();
					resx.appid = "${appIdCertona}";
					resx.event = "${orderSubmitVO.certonaMap.resxEventType}";
					resx.itemid = "${orderSubmitVO.certonaMap.productIds}";
					resx.qty = "${orderSubmitVO.certonaMap.itemQtys}";
					resx.price = "${orderSubmitVO.certonaMap.itemAmounts}";
					resx.total = "${orderSubmitVO.certonaMap.grandTotal}";
					resx.transactionid = "${orderSubmitVO.certonaMap.genOrderCode}";
				    resx.customerid = "${userId}"; 
				    if (typeof certonaResx === 'object') {
						certonaResx.run();
                }
            </script>
    		<!-- Google Code for Conversions Conversion Page --> 
    		
				<script type="text/javascript"> 
				/* <![CDATA[ */ 
					var google_conversion_id = 1032449598; 
					var google_conversion_language = "en"; 
					var google_conversion_format = "3"; 
					var google_conversion_color = "ffffff"; 
					var google_conversion_label = "YNpKCI7_wgkQvtyn7AM"; 
					var google_remarketing_only = false; 
				/* ]]> */ 
				</script> 
				<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js"> 
				</script> 
				<noscript> 
					<div style="display:inline;"> 
						<img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/1032449598/?label=YNpKCI7_wgkQvtyn7AM&amp;guid=ON&amp;script=0"/> 
					</div> 
				</noscript>   
            </c:if >
        </head>
        <body onload="runOmniOnLoad();">
			&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;
        </body>
    </html>
</dsp:page>