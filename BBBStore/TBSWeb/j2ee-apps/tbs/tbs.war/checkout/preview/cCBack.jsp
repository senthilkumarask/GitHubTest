<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/includes/taglibs.jspf" %>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>

<dsp:page>
    <dsp:importbean bean="com/bbb/commerce/common/BBBVBVSessionBean" />

    <dsp:getvalueof var="token" bean="BBBVBVSessionBean.bBBVerifiedByVisaVO.token" scope="request" />
    <c:set var="error_message" scope="request"><bbbc:error key="err_vbv_refresh_back_error" language="${pageContext.request.locale.language}"></bbbc:error></c:set>

    <c:choose>
        <c:when test="${token eq 'cCFrame'}">
            <dsp:setvalue bean="com/bbb/commerce/common/BBBVBVSessionBean.bBBVerifiedByVisaVO.token" value="cCBack" />

            <dsp:getvalueof var="aCSUrl" bean="BBBVBVSessionBean.bBBVerifiedByVisaVO.aCSUrl" scope="request" />
            <dsp:getvalueof var="payload" bean="BBBVBVSessionBean.bBBVerifiedByVisaVO.payload" scope="request" />
            <dsp:getvalueof var="TermUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}/tbs/checkout/preview/cCResponse.jsp" scope="request" />

			<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
				<dsp:oparam name="output">
					<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM" />
				</dsp:oparam>
			</dsp:droplet>
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
                <html lang="${pageContext.request.locale.language}" xml:lang="${pageContext.request.locale.language}" style="background: url('/_assets/global/images/widgets/small_loader.gif') no-repeat scroll center center #fff; overflow:hidden; width:400px; height:400px; min-width:400px; min-height:400px;">
                    <head>
                        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
                        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
                        <meta http-equiv="pragma" content="no-cache">
                        <meta http-equiv="cache-control" content="no-cache">
                        <meta name="robots" content="noindex, nofollow" />
                        <title><bbbl:label key="lbl_metadetails_title" language="${pageContext.request.locale.language}"/></title>
                        <script type="text/javascript">
                            function ccLoad(){ document.frmLaunchACS.submit(); }
                        </script>
                        <%-- <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/global/js/thirdparty/tealeaf/tealeaf-w3c-prod-min.js?v=${buildRevisionNumber}"></script> --%>
                    </head>
                    <body onload="ccLoad();" style="overflow:hidden; width:380px; height:380px; min-width:380px; min-height:380px;">
                        <div style="visibility: hidden;">
                            <form name="frmLaunchACS" method="post" action="${aCSUrl}">
                                <input type="hidden" name="PaReq" value="${payload}" />
                                <input type="hidden" name="TermUrl" value="${TermUrl}" />
                                <input type="submit" name="lookupSubmit" value="Submit" id="lookupSubmit" />
                            </form>

                            <dsp:droplet name="/com/bbb/logging/NetworkInfoDroplet">
                                <dsp:oparam name="output">
                                    <!--googleoff: all-->
                                    <div id="userNetworkInfo" style="display: none; visibility: hidden;"><ul>
                                        <li id="session_id"><dsp:valueof param="SESSION_ID" /></li>
                                        <li id="jvm_name"><dsp:valueof bean="/atg/dynamo/service/IdGenerator.dcPrefix"/>-<dsp:valueof param="JVM_NAME" /></li>
                                        <li id="time"><dsp:valueof param="TIME" /></li>
                                    </ul></div>
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
                <dsp:param name="url" value="/checkout/payment/billing_payment.jsp" />
            </dsp:droplet>
        </c:otherwise>
    </c:choose>
</dsp:page>