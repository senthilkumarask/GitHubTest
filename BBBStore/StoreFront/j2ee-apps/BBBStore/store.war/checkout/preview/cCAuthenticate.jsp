<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/includes/taglibs.jspf" %>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>

<dsp:page>
    <dsp:importbean bean="com/bbb/commerce/common/BBBVBVSessionBean" />

    <dsp:getvalueof var="token" bean="BBBVBVSessionBean.bBBVerifiedByVisaVO.token" scope="request" />
    <c:set var="error_message" scope="request"><bbbc:error key="err_vbv_refresh_back_error" language="${pageContext.request.locale.language}"></bbbc:error></c:set>

    <c:choose>
        <c:when test="${token eq 'cCResponse'}">
            <dsp:setvalue bean="com/bbb/commerce/common/BBBVBVSessionBean.bBBVerifiedByVisaVO.token" value="cCAuthenticate" />

            <dsp:getvalueof var="currentSiteId" vartype="java.lang.String" bean="/atg/multisite/Site.id" scope="request" />
            <dsp:getvalueof var="faviconUrl" vartype="java.lang.String" bean="/atg/multisite/Site.favicon" scope="request" />

            <c:set var="BuyBuyBabySite" scope="request"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
			<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
				<dsp:oparam name="output">
					<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM" />
				</dsp:oparam>
			</dsp:droplet>

			<c:set var="iconTheme" value="" scope="request" />
            <c:set var="themeName" value="by" scope="request" />
            <c:if test="${currentSiteId eq BuyBuyBabySite}">
                <c:set var="iconTheme" value="_bb" scope="request" />
                <c:set var="themeName" value="bb" scope="request" />
            </c:if>
            <c:if test="${empty faviconUrl}"><c:set var="faviconUrl" scope="request">/favicon${iconTheme}.ico</c:set></c:if>

            <dsp:droplet name="/com/bbb/commerce/browse/droplet/ConfigURLDroplet">
                <dsp:param value="ThirdPartyURLs" name="configType" />
                <dsp:oparam name="output">
                    <dsp:getvalueof var="imagePath" param="imagePath" scope="request" />
                    <dsp:getvalueof var="cssPath" param="cssPath" scope="request" />
                    <dsp:getvalueof var="jsPath" param="jsPath" scope="request" />
                    <dsp:getvalueof var="scene7Path" param="scene7Path" scope="request" />
                </dsp:oparam>
            </dsp:droplet>

            <compress:html removeComments="false">
                <!doctype html>
                <html lang="${pageContext.request.locale.language}" xml:lang="${pageContext.request.locale.language}">
                    <head>
                        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
                        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
                        <meta http-equiv="pragma" content="no-cache">
                        <meta http-equiv="cache-control" content="no-cache">
                        <meta name="robots" content="noindex, nofollow" />

                        <link rel="apple-touch-icon" href="${fn:replace(faviconUrl, '.ico', '.png')}" />
                        <link rel="shortcut icon" type="image/ico" href="${fn:replace(faviconUrl, '.png', '.ico')}" />
                        <link rel="icon" type="image/ico" href="${fn:replace(faviconUrl, '.png', '.ico')}" />

                        <title><bbbl:label key="lbl_metadetails_title" language="${pageContext.request.locale.language}" /></title>

                        <style type="text/css">
                            html{background:#fff;font-size:100%;overflow-y:scroll;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%}
                            .ie7 html,html>body{overflow-x:hidden}
                            body{background:#fff;width:auto;height:auto;margin:0 auto;min-width:996px;font-weight:400;font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#333;-webkit-appearance:none;-webkit-border-radius:0}
                            img{border:0;-ms-interpolation-mode:bicubic;vertical-align:middle}
                            .container_12{margin-left:auto;margin-right:auto;width:996px}
                            .grid_12{display:inline;float:left;position:relative;margin-left:10px;margin-right:10px;width:976px}
                            .clear{clear:both;display:block;overflow:hidden;visibility:hidden;width:0;height:0}
                            .clearfix:after,.clearfix:before{content:'\0020';display:block;overflow:hidden;visibility:hidden;width:0;height:0}
                            .clearfix:after{clear:both}
                            .clearfix{zoom:1}
                            .hidden{display:none;visibility:hidden}
                            .bvbAuthenticate{text-align:center;font-size:20px;margin-top:265px}
                            .bvbAuthenticate p{line-height:36px;margin-top:15px}
                            .bold{font-weight:700!important}
                            #noScriptWarning{background:#FEFBE7;padding:7px 0;border:1px solid #FAD611;margin:0}
                            #noScriptWarning a,#noScriptWarning p,#noScriptWarning p.errHeading{font-weight:400;font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#333;text-transform:none;letter-spacing:0;margin:0;padding:7px 0;word-wrap:break-word;word-break:break-word;background:0 0;border:0}
                            #noScriptWarning p.errHeading{font-weight:700;color:#444;font-size:18px}
                            #noScriptWarning a{text-decoration:none;color:#273691}
                            #noScriptWarning a:hover{text-decoration:underline;color:#273691}
							.pageError {color: #FF0000;font-size:12px;}
							#pageError a {text-decoration: underline;}
                        </style>
                        <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/tealeaf/tealeaf-w3c-prod-min.js?v=${buildRevisionNumber}"></script>
                        <script type="text/javascript">
                            function ccLoad(){ document.frmLaunchACS.btnPlaceOrder.click(); }
							var placeOrderIntervalKey = "<bbbc:config key='placeOrderIntervalKey' configName='FlagDrivenFunctions' />" ;// here we will check the key on/off
							if (placeOrderIntervalKey === true || placeOrderIntervalKey === 'true') {
								var interval = parseInt("<bbbc:config key='placeOrderWaitingTime' configName='CartAndCheckoutKeys' />");
								(function() {
									setTimeout(
										function() {
											var errorDiv = document.getElementById('pageError');
											errorDiv.classList.remove('hidden');
										},
										interval);
								})();
							}
                        </script>
                    </head>
                    <body id="themeWrapper" class="${themeName}" onload="ccLoad();" >
                        <noscript><bbbt:textArea key="txt_js_disabled_msg"  language="${pageContext.request.locale.language}" /></noscript>
                        <div id="pageWrapper" class="cCFrame">
                            <div id="content" class="container_12 clearfix">
                                <div class="grid_12 clearfix">
                                    <div class="bvbAuthenticate">
                                        <bbbl:textArea key="txt_vbv_sitelogo_img" language="${pageContext.request.locale.language}" />
                                        <bbbl:label key="lbl_vbv_wait_temporarily" language="${pageContext.request.locale.language}" />
										<div id="pageError" class="pageError hidden"><bbbt:textArea key="placeOrderIntervalErrorVbvDesktop" language="${pageContext.request.locale.language}" />
										</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div style="visibility: hidden;">
                            <dsp:form name="frmLaunchACS" id="frmLaunchACS" method="post">
                                <dsp:input bean="/atg/commerce/order/purchase/CommitOrderFormHandler.paRes" type="hidden" value="${authResponse}" />
                                <dsp:input id="btnPlaceOrder" name="btnPlaceOrder" bean="/atg/commerce/order/purchase/CommitOrderFormHandler.verifiedByVisaAuth" type="submit" value="Place Order" />
                            </dsp:form>

                            <dsp:droplet name="/com/bbb/logging/NetworkInfoDroplet">
                                <dsp:oparam name="output">
                                    <!--googleoff: all-->
								<%--BBBSL-1822 Start - added usernetworkinfo inside html comments to avoind being read by search engines--%>
                                    <!--
                                    <div id="userNetworkInfo" style="display: none; visibility: hidden;"><ul>
                                        <li id="session_id"><dsp:valueof param="SESSION_ID" /></li>
                                        <li id="jvm_name"><dsp:valueof bean="/atg/dynamo/service/IdGenerator.dcPrefix"/>-<dsp:valueof param="JVM_NAME" /></li>
                                        <li id="time"><dsp:valueof param="TIME" /></li>
                                    </ul></div>
                                    -->
                                    <!--googleon: all-->
                                </dsp:oparam>
                            </dsp:droplet>
                        </div>
                    </body>
                </html>
            </compress:html>
        </c:when>

        <c:otherwise>
            <dsp:setvalue bean="com/bbb/commerce/common/BBBVBVSessionBean.bBBVerifiedByVisaVO.token" value="error_true" />
            <dsp:setvalue bean="com/bbb/commerce/common/BBBVBVSessionBean.bBBVerifiedByVisaVO.message" value="${error_message}" />
            <dsp:droplet name="/atg/dynamo/droplet/Redirect">
                <dsp:param name="url" value="/store/checkout/payment/billing_payment.jsp" />
            </dsp:droplet>
        </c:otherwise>
    </c:choose>
</dsp:page>