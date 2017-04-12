<dsp:page> 
<%@ include file="/includes/taglibs.jspf" %>
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

<c:if test="${currentSiteId == BuyBuyBabySite}">    
    <link type="text/css" rel="stylesheet" href="/_assets/global/css/large.css?v=${buildRevisionNumber}" />
    <script type="text/javascript">
        if (top.frames.length != 0)
            top.location = self.document.location;
    </script>
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
		<ul><li><a href="https://m.buybuybaby.com/buybuybaby/index.do" class="n1"><bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" /></a></li>
		<li><a href="https://m.buybuybaby.com/buybuybaby/store/locator.do" class="n2"><bbbl:label key="lbl_stores" language="${pageContext.request.locale.language}" /></a></li>
		<li><a href="https://m.buybuybaby.com/buybuybaby/registry/registryForward.do" class="n3"><bbbl:label key="lbl_registry_header_registry" language="${pageContext.request.locale.language}" /></a></li>
		<li><a href="https://m.buybuybaby.com/buybuybaby/cart/list.do" class="n4"><bbbl:label key="lbl_checkout_cart" language="${pageContext.request.locale.language}" /></a></li></ul>
    </div>
    <div class="pagetitle"><bbbl:label key="lbl_forgot_password__submit" language="${pageContext.request.locale.language}" /></div>

    <div style="text-align: center;">
        <br />
        <span class="babySubHeadline"><b><bbbl:label key="lbl_password_change_completed" language="${pageContext.request.locale.language}" /></b></span>
    </div>
    
    <div class="btnarea">
        <p class="mb10">
            <input type="button" id="detail2" onclick="javascropt:location.href='https://m.buybuybaby.com/buybuybaby/index.do'" class="btn_continueshopping" value="">
        </p>
    </div>

<div class="clear"></div>
<div id="footer">
        <p>
            <a href="https://m.buybuybaby.com/buybuybaby/foot/contactus.do"><bbbl:label key="lbl_header_contactus" language="${pageContext.request.locale.language}" /></a><a href="https://m.buybuybaby.com/buybuybaby/foot/footReturns.do"><bbbl:label key="lbl_easy_return_form_heading_1" language="${pageContext.request.locale.language}" /></a><a href="https://m.buybuybaby.com/buybuybaby/foot/trackorder.do"><bbbl:label key="lbl_header_trackorder" language="${pageContext.request.locale.language}" /></a><a href="https://m.buybuybaby.com/buybuybaby/foot/footPrivacy.do"><bbbl:label key="lbl_subscribe_privacy_policy" language="${pageContext.request.locale.language}" /></a><br>
            <a href="https://m.buybuybaby.com/buybuybaby/foot/footShipping.do"><bbbl:label key="lbl_spc_bread_crumb_shipping" language="${pageContext.request.locale.language}" /> &amp; <bbbl:label key="lbl_policies" language="${pageContext.request.locale.language}" /></a><a href="https://m.buybuybaby.com/buybuybaby/foot/termsofuse.do"><bbbl:label key="lbl_terms_of_use" language="${pageContext.request.locale.language}" /></a><a href="//www.buybuybaby.com/default.asp?stop_mobi=yes"><bbbl:label key="lbl_visit_full_site" language="${pageContext.request.locale.language}" /></a>
        </p>
        <div class="copyright">
            <bbbl:label key="lbl_help_avail_at" language="${pageContext.request.locale.language}" />
            <br>
            <bbbl:label key="lbl_help_num" language="${pageContext.request.locale.language}" /><br>
            &copy; <bbbl:label key="lbl_help_num2" language="${pageContext.request.locale.language}" />, <bbbl:label key="lbl_rights_reserved" language="${pageContext.request.locale.language}" /><br>
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
        <ul><li><a href="https://m.bedbathandbeyond.com/bedbathbeyond/index.do" class="n1">Home</a></li>
        <li><a href="https://m.bedbathandbeyond.com/bedbathbeyond/store/locator.do" class="n2">Stores</a></li>
        <li><a href="https://m.bedbathandbeyond.com/bedbathbeyond/registry/registryForward.do" class="n3">Registry</a></li>
        <li><a href="https://m.bedbathandbeyond.com/bedbathbeyond/cart/list.do" class="n4">Cart</a></li></ul>
    </div>
    <div class="pagetitle"><bbbl:label key="lbl_forgot_password__submit" language="${pageContext.request.locale.language}" /></div>

    <div style="text-align: center;">
        <br />
        <span style="color: #0C51AD;font-size: 140%;line-height: 20px;"><b><bbbl:label key="lbl_password_change_completed" language="${pageContext.request.locale.language}" /></b></span>
    </div>
    
    <div class="btnarea">
        <p class="mb10">
            <input type="button" id="detail2" onclick="javascropt:location.href='https://m.bedbathandbeyond.com/bedbathbeyond/index.do'" class="btn_continueshopping" value="">
        </p>
    </div>

<div class="clear"></div>
    <div id="footer">
        <p>
            <a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/contractus.do"><bbbl:label key="lbl_contactus_contactus" language="${pageContext.request.locale.language}" /></a><a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/footReturns.do"><bbbl:label key="lbl_easy_return_form_heading_1" language="${pageContext.request.locale.language}" /></a><a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/trackorder.do"><bbbl:label key="lbl_header_trackorder" language="${pageContext.request.locale.language}" /></a><a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/footPrivacy.do"><bbbl:label key="lbl_subscribe_privacy_policy" language="${pageContext.request.locale.language}" /></a><br>
            <a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/footShipping.do"><bbbl:label key="lbl_spc_bread_crumb_shipping" language="${pageContext.request.locale.language}" /> &amp; <bbbl:label key="lbl_policies" language="${pageContext.request.locale.language}" /></a><a href="https://m.bedbathandbeyond.com/bedbathbeyond/foot/termsofuse.do"><bbbl:label key="lbl_terms_of_use" language="${pageContext.request.locale.language}" /></a><a href="//www.bedbathandbeyond.com/default.asp?stop_mobi=yes"><bbbl:label key="lbl_visit_full_site" language="${pageContext.request.locale.language}" /></a>
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
