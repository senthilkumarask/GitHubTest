<dsp:page> 
<%@ include file="/includes/taglibs.jspf" %>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/multisite/Site" />

<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<c:set var="BedBathUSSite">
    <bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BuyBuyBabySite">
    <bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BedBathCanadaSite">
    <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
	<dsp:oparam name="output">
		<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM" />
	</dsp:oparam>
</dsp:droplet>
	<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content=" initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <link type="text/css" rel="stylesheet" href="/_assets/global/css/large.css?v=${buildRevisionNumber}" />
    <script type="text/javascript">
        if (top.frames.length != 0)
            top.location = self.document.location;
    </script>

<c:if test="${currentSiteId == BuyBuyBabySite}">
    <link type="text/css" rel="stylesheet" href="/_assets/global/css/large.css?v=${buildRevisionNumber}" />
    <script type="text/javascript">
        //per device change the css 
        var baseCssPath = "/_assets/global/css";
        if (navigator.userAgent.indexOf("BlackBerry") >= 0) {
            if (navigator.userAgent.indexOf("VendorID/378") > 0) {
                var link = "<link href='" + baseCssPath + "/bb50.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";

            } else if (navigator.userAgent.indexOf("VendorID/102") > 0) {
                var link = "<link href='" + baseCssPath + "/bb102.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";

            } else {
                var link = "<link href='" + baseCssPath + "/bb.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";
            }
        }
        if (navigator.userAgent.indexOf("Windows") >= 0) {
            var link = "<link href='" + baseCssPath + "/winphone.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";
        }
        if (navigator.userAgent.indexOf("Android") >= 0 || navigator.platform.indexOf("Android") >= 0) {
            var link = "<link href='" + baseCssPath + "/android.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";
        }
        if (link != null && link != '') {
            document.write(link);
        }
    </script>
    <title><bbbl:label key="lbl_change_password" language="${pageContext.request.locale.language}" /> - <bbbl:label key="lbl_buy_buy_Baby" language="${pageContext.request.locale.language}" /></title>
</head>
<body>

    <div id="header">
        <div class="logo">
            <a href="https://m.buybuybaby.com/buybuybaby/index.do">
                <img alt="Buy Buy Baby" src="/_assets/global/images/logo.gif"></a>
        </div>
    </div>
    <div id="nav">
        <ul><li><a href="https://m.buybuybaby.com/buybuybaby/index.do" class="n1"><bbbl:label key="lbl_breadcrumb_home_link" language ="${pageContext.request.locale.language}"/></a></li>
        <li><a href="https://m.buybuybaby.com/buybuybaby/store/locator.do" class="n2">Stores</a></li>
        <li><a href="https://m.buybuybaby.com/buybuybaby/registry/registryForward.do" class="n3"><bbbl:label key="lbl_reg_feature_reg" language ="${pageContext.request.locale.language}"/></a></li>
        <li><a href="https://m.buybuybaby.com/buybuybaby/cart/list.do" class="n4"><bbbl:label key="lbl_checkout_cart" language ="${pageContext.request.locale.language}"/></a></li></ul>
    </div>
</c:if>

<c:if test="${currentSiteId == BedBathUSSite || currentSiteId == BedBathCanadaSite}">
    <link type="text/css" rel="stylesheet" href="//www.bedbathandbeyond.com/assets/product_images/ls/css/large.css?v=${buildRevisionNumber}" />
    <script type="text/javascript">
        //per device change the css 
        var baseCssPath = "//www.bedbathandbeyond.com/assets/product_images/ls/css";
        if (navigator.userAgent.indexOf("BlackBerry") >= 0) {
            if (navigator.userAgent.indexOf("VendorID/378") > 0) {
                var link = "<link href='" + baseCssPath + "/bb50.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";

            } else if (navigator.userAgent.indexOf("VendorID/102") > 0) {
                var link = "<link href='" + baseCssPath + "/bb102.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";

            } else {
                var link = "<link href='" + baseCssPath + "/bb.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";
            }
        }
        if (navigator.userAgent.indexOf("Windows") >= 0) {
            var link = "<link href='" + baseCssPath + "/winphone.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";
        }
        if (navigator.userAgent.indexOf("Android") >= 0 || navigator.platform.indexOf("Android") >= 0) {
            var link = "<link href='" + baseCssPath + "/android.css?v=${buildRevisionNumber}' rel='stylesheet' type='text/css' />";
        }
        if (link != null && link != '') {
            document.write(link);
        }
    </script>
    <title><bbbl:label key="lbl_change_password" language="${pageContext.request.locale.language}" /> - <bbbl:label key="lbl_bbb_desktop" language="${pageContext.request.locale.language}" /></title>
</head>
<body>

    <div id="header">
        <div class="logo">
            <a href="https://m.bedbathandbeyond.com/bedbathbeyond/index.do">
                <img alt="Bed Bath & Beyond" src="//www.bedbathandbeyond.com/assets/product_images/ls/logo.png"></a>
        </div>
    </div>
    <div id="nav">
        <ul><li><a href="https://m.bedbathandbeyond.com/bedbathbeyond/index.do" class="n1"><bbbl:label key="lbl_breadcrumb_home_link" language ="${pageContext.request.locale.language}"/></a></li>
        <li><a href="https://m.bedbathandbeyond.com/bedbathbeyond/store/locator.do" class="n2">Stores</a></li>
        <li><a href="https://m.bedbathandbeyond.com/bedbathbeyond/registry/registryForward.do" class="n3"><bbbl:label key="lbl_mng_regitem_registry_label" language ="${pageContext.request.locale.language}"/></a></li>
        <li><a href="https://m.bedbathandbeyond.com/bedbathbeyond/cart/list.do" class="n4"><bbbl:label key="lbl_checkout_cart" language ="${pageContext.request.locale.language}"/></a></li></ul>
    </div>
</c:if>    

    <div class="pagetitle"><bbbl:label key="lbl_forgot_password__submit" language ="${pageContext.request.locale.language}"/></div>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
			<c:choose>
            	 <c:when test="${ProfileFormHandler.changePasswordSuccessMessage == true}" >
             		 	<p class="bold welcomeMsg noMarTop"><bbbt:textArea key="txtarea_changepassword_successmessage" language ="${pageContext.request.locale.language}"/></p>
             		 	<dsp:setvalue bean="ProfileFormHandler.changePasswordSuccessMessage" value="false"/>
             	</c:when>
             </c:choose>	
			
<dsp:form method="post" iclass="mt10" id="updatePass" action="changepasswordMobile.jsp">
				
				<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
				  <dsp:getvalueof param="formhandler" var="formhandler"/>
				  <%-- formhandler Errors --%>
				  <dsp:droplet name="ErrorMessageForEach">
					<c:if test="${null ne formhandler}">
							<dsp:param param="formhandler.formExceptions" name="exceptions"/>
				    </c:if>
				
				    <dsp:oparam name="outputStart">
				      <div id="error" class="special_mesaage"><div class="error_info"><p><ul class="error">
				    </dsp:oparam>
				    <dsp:oparam name="output">
				      <li class="error">
				        <dsp:getvalueof param="message" var="err_msg_key" />
						<c:set var="under_score" value="_"/>
						<c:choose>
						<c:when test="${fn:contains(err_msg_key, under_score)}">
				        	<c:set var="err_msg" scope="page">
				        		<bbbe:error key="${err_msg_key}" language="${pageContext.request.locale.language}"/>
							</c:set>
				        </c:when>
				        </c:choose>
						<c:choose>
						<c:when test="${empty err_msg}"><dsp:valueof param="message" valueishtml="true" /></c:when>
						<c:otherwise>${err_msg}</c:otherwise>
						</c:choose>  
						<c:set var="err_msg" scope="page"></c:set>
				      </li>
				    </dsp:oparam>
				    <dsp:oparam name="outputEnd">
				      </p></div></div>
				    </dsp:oparam>
				  </dsp:droplet>
				
				<c:choose>
            	 <c:when test="${ProfileFormHandler.changePasswordSuccessMessage == true}" >
             		 	<p class="bold welcomeMsg noMarTop"><bbbt:textArea key="txtarea_changepassword_successmessage" language ="${pageContext.request.locale.language}"/></p>
             		 	<dsp:setvalue bean="ProfileFormHandler.changePasswordSuccessMessage" value="false"/>
             	</c:when>
            	</c:choose>
            	 <dsp:input bean="ProfileFormHandler.fromPage" type="hidden" value="changePasswordMobile" />
				<%-- Client DOM XSRF
				<dsp:input bean="ProfileFormHandler.changePasswordSuccessURL" type="hidden" value="changepasswordMobilecfm.jsp"/>
				<dsp:input bean="ProfileFormHandler.changePasswordErrorURL" type="hidden" value="changepasswordMobile.jsp"/>
				<dsp:input bean="ProfileFormHandler.fromMobile" name="fromMobile" type="hidden" value="true"/> --%>
							
             <table class="form_table">
    <tr>
    <td><b><bbbl:label key="lbl_chat_email" language="${pageContext.request.locale.language}" />*</b></td>
    </tr>
    <tr>
    <td height="8"></td>
    </tr>
    <tr>
    <td>
    <dsp:input bean="ProfileFormHandler.value.login"  id="cemail" iclass="intxt" name="email" type="text"/></td>
    </tr>
	<tr>
    <td height="8"></td>
    </tr>
    <tr>
    <td><b><bbbl:label key="lbl_temp_password" language="${pageContext.request.locale.language}" />*</b></td>
    </tr>
    <tr>
    <td height="8"></td>
    </tr>
    <tr>
    <td>
    <dsp:input bean="ProfileFormHandler.value.oldpassword"  id="cpassword" iclass="intxt" name="currentpassword" value="" type="password" autocomplete="off"/></td>
    </tr>
    <tr>
    <td height="8"></td>
    </tr>
    <tr>
    <td><b><bbbl:label key="lbl_change_pass_model_new" language="${pageContext.request.locale.language}" />*</b></td>
    </tr>
      <tr>
    <td height="8"></td>
    </tr>
    <tr>
    <td>
    <dsp:input bean="ProfileFormHandler.value.password" id="password" name="password" iclass="intxt" value="" type="password" autocomplete="off"/></td>
    </tr>
    <tr>
    <td height="8"></td>
    </tr>
    <tr>
    <td><b><bbbl:label key="lbl_personalinfo_confirmnewpassword" language="${pageContext.request.locale.language}" />*</b></td>
    </tr>
      <tr>
    <td height="8"></td>
    </tr>
    <tr>
    <td>
    <dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" autocomplete="off" iclass="intxt" name="confirmPassword" id="confirmPassword" value="" /></td>
    </tr>
    <tr>
    <td height="8"></td>
    </tr>
    </table>
    <div class="clear"></div>
    <dsp:input bean="ProfileFormHandler.changePassword" type="hidden" value="submit" /> 
	<div class="btnarea">
	   <c:set var="submitKey">
				<bbbl:label key='lbl_personalinfo_update' language='${pageContext.request.locale.language}' />
	   </c:set>
	   <dsp:input bean="ProfileFormHandler.changePassword" id="updatePassBtn" iclass="btn_submit"  type="Submit" value=""/>
       </div>    
</dsp:form>
<div class="clear"></div>

<c:if test="${currentSiteId == BuyBuyBabySite}">
<div id="footer">
        <p>
            <a href="https://m.buybuybaby.com/buybuybaby/foot/contactus.do"><bbbl:label key="lbl_contactus_contactus" language="${pageContext.request.locale.language}" /></a><a href="https://m.buybuybaby.com/buybuybaby/foot/footReturns.do"><bbbl:label key="lbl_easy_return_form_heading_1" language="${pageContext.request.locale.language}" /></a><a href="https://m.buybuybaby.com/buybuybaby/foot/trackorder.do"><bbbl:label key="lbl_header_trackorder" language="${pageContext.request.locale.language}" /></a><a href="https://m.buybuybaby.com/buybuybaby/foot/footPrivacy.do"><bbbl:label key="lbl_subscribe_privacy_policy" language="${pageContext.request.locale.language}" /></a><br>
            <a href="https://m.buybuybaby.com/buybuybaby/foot/footShipping.do"><bbbl:label key="lbl_spc_bread_crumb_shipping" language="${pageContext.request.locale.language}" /> &amp; <bbbl:label key="lbl_policies" language="${pageContext.request.locale.language}" /></a><a href="https://m.buybuybaby.com/buybuybaby/foot/termsofuse.do"><bbbl:label key="lbl_terms_of_use" language="${pageContext.request.locale.language}" /></a><a href="https://www.buybuybaby.com/default.asp?stop_mobi=yes"><bbbl:label key="lbl_visit_full_site" language="${pageContext.request.locale.language}" /></a>
        </p>
        <div class="copyright">
            <bbbl:label key="lbl_help_avail_at" language="${pageContext.request.locale.language}" />
            <br>
            <bbbl:label key="lbl_help_num" language="${pageContext.request.locale.language}" /><br>
            &copy; <bbbl:label key="lbl_help_num2" language="${pageContext.request.locale.language}" />, <bbbl:label key="lbl_rights_reserved" language="${pageContext.request.locale.language}" />.<br>
        </div>
    </div>
    <br>
	<script type="text/javascript">
		var _gaq = _gaq || [];
		_gaq.push(['_setAccount', 'UA-1966617-4']);
		_gaq.push(['_trackPageview']);

		(function() {
			var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
			ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
			var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		})();
	</script>
</c:if>

<c:if test="${currentSiteId == BedBathUSSite || currentSiteId == BedBathCanadaSite}">
    <div id="footer">
        <p>
            <a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/contractus.do">Contact Us</a><a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/footReturns.do"><bbbl:label key="lbl_easy_return_form_heading_1" language="${pageContext.request.locale.language}" /></a><a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/trackorder.do"><bbbl:label key="lbl_header_trackorder" language="${pageContext.request.locale.language}" /></a><a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/footPrivacy.do"><bbbl:label key="lbl_subscribe_privacy_policy" language="${pageContext.request.locale.language}" /></a><br>
            <a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/footShipping.do"><bbbl:label key="lbl_bread_crumb_shipping" language="${pageContext.request.locale.language}" /> &amp; <bbbl:label key="lbl_policies" language="${pageContext.request.locale.language}" /></a><a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/termsofuse.do"><bbbl:label key="lbl_terms_of_use" language="${pageContext.request.locale.language}" /></a><a href="https://www.bedbathandbeyond.com/default.asp?stop_mobi=yes"><bbbl:label key="lbl_visit_full_site" language="${pageContext.request.locale.language}" /></a>
        </p>
        <div class="copyright">
            <bbbl:label key="lbl_query_assistance" language="${pageContext.request.locale.language}" />
            <br>
           <bbbl:label key="lbl_help_bbb" language="${pageContext.request.locale.language}" />&reg; <bbbl:label key="lbl_help_num_bbb" language="${pageContext.request.locale.language}" /><br>
            &copy;<bbbl:label key="lbl_help_num2_bbb" language="${pageContext.request.locale.language}" /> &amp; <bbbl:label key="lbl_inc_subsidiaries" language="${pageContext.request.locale.language}" /><br>
            <bbbl:label key="lbl_all_right_reserved" language="${pageContext.request.locale.language}" />
        </div>
    </div>
    <br>
    <script type="text/javascript">
        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-1966617-1']);
        _gaq.push(['_trackPageview']);

        (function() {
            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
        })();
    </script>
</c:if>

    <script type="text/javascript">
    function inputFocus(id){
     if(id.value=="Enter message here..."&&id.type=="textarea")
     {
        id.value='';
        id.style.color ="#333";
     }else{
        id.style.color ="#333";
     }
     if(id.value=="(required)"&&id.type=="text"){
        id.value='';
        id.style.color="#333";
     }else{
        id.style.color="#333";
     }
    }

    function inputBlur(id){
     if(id.value==''&&id.type=="textarea"){
      id.value='Enter message here...';
      id.style.color ="#999";
      id.className='text-color';
      }
     if(id.value==''&&id.type=="text"){
      id.value="(required)";
      id.style.color ="#999";
      id.className='intxt';
      }
    }
    </script>
</dsp:page>
