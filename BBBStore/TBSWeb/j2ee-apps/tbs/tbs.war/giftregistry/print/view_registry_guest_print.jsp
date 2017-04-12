<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryItemsDisplayDroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
<dsp:importbean var="CurrentDate" bean="/atg/dynamo/service/CurrentDate"/>
<dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:getvalueof id="currentSiteId" bean="Site.id" scope="session"/>
<dsp:getvalueof var="eventTypeParam" param="eventType"/>
<dsp:getvalueof var="registryId" param="registryId"/>
<dsp:getvalueof var="view" param="view"/>
<c:if test="${view eq null || (empty view) }"><c:set var="view">1</c:set></c:if>
<dsp:getvalueof var="sortSeq" param="sortSeq"/>
<c:if test="${sortSeq eq null || (empty sortSeq) }"><c:set var="sortSeq">1</c:set></c:if>
<dsp:getvalueof var="eventTypeCode" param="eventTypeCode"/>
<dsp:getvalueof var="startIdx" param="startIdx"/>
<dsp:getvalueof var="isGiftGiver" param="isGiftGiver"/>
<dsp:getvalueof var="blkSize" param="blkSize"/>
<dsp:getvalueof var="isAvailForWebPurchaseFlag" param="isAvailForWebPurchaseFlag"/>
<dsp:getvalueof var="userToken" param="userToken"/>
<c:set var="maxBulkSize" scope="request"><bbbc:config key="MaxSizeRegistryItems" configName="ContentCatalogKeys" /></c:set>
<c:set var="othersCat"><bbbc:config key="DefaultCategoryForRegistry" configName="ContentCatalogKeys" /></c:set>
<dsp:getvalueof var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<dsp:droplet name="/com/bbb/commerce/browse/droplet/ConfigURLDroplet">
	<dsp:param value="ThirdPartyURLs" name="configType" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="imagePath" param="imagePath" scope="request"/>
		<dsp:getvalueof var="cssPath" param="cssPath" scope="request"/>
		<dsp:getvalueof var="jsPath" param="jsPath" scope="request"/>
		<dsp:getvalueof var="scene7Path" param="scene7Path" scope="request"/>
	</dsp:oparam>
</dsp:droplet>

<!doctype html>
<!--[if lt IE 7]> <html class="ie no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>	<html class="ie no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>	<html class="ie no-js ie8 oldie" lang="en"> <![endif]-->
<!--[if IE 9]>	<html class="ie no-js ie9 newie" lang="en"> <![endif]-->
<!--[if gt IE 9]> <html class="ie no-js newie" lang="en">	 <![endif]-->
<!--[if !IE]><!--><html class="not-ie no-js" lang="en">	  <!--<![endif]-->

<head>
	<title><bbbl:label key="lbl_metadetails_title" language ="${pageContext.request.locale.language}"/></title>
	<meta charset="utf-8" />
	<META http-equiv="imagetoolbar" content="no">
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<meta name="viewport" content="width=device-width,initial-scale=1" />

	<dsp:include page="/_includes/third_party_on_of_tags.jsp"/>

	<c:if test="${pageContext.request.secure}">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
	</c:if>
	<dsp:getvalueof var="faviconUrl" vartype="java.lang.String" bean="/atg/multisite/Site.favicon"/>
	<c:if test="${currentSiteId eq TBS_BuyBuyBabySite}">
		<c:set var="iconTheme" value="_bb" scope="request" />
	</c:if>
	<c:if test="${empty faviconUrl}">
		<c:set var="faviconUrl" scope="session">/favicon${iconTheme}.ico</c:set>
	</c:if>

	<link rel="apple-touch-icon" href="${fn:replace(faviconUrl, '.ico', '.png')}" />
	<link rel="shortcut icon" type="image/ico" href="${fn:replace(faviconUrl, '.png', '.ico')}" />
	<link rel="icon" type="image/ico" href="${fn:replace(faviconUrl, '.png', '.ico')}">

	<c:set var="section" value="registry" scope="request" />

	<c:choose>
		<c:when test="${currentSiteId == TBS_BedBathUSSite}">
			<c:set var="themeName" value="by" scope="session" />
		</c:when>
		<c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
			<c:set var="themeName" value="by" scope="session" />
		</c:when>
		<c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
			<c:set var="themeName" value="bb" scope="session" />
		</c:when>
		<c:otherwise>
			<c:set var="themeName" value="bb" scope="session" />
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${not empty currentSiteId && currentSiteId eq TBS_BuyBuyBabySite}">
			<c:set var="pageVariation" value="" scope="request" />
		</c:when>
		<c:when test="${not empty eventTypeParam && eventTypeParam eq 'Baby'}">
			<c:set var="pageVariation" value="" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="pageVariation" value="br" scope="request" />
		</c:otherwise>
	</c:choose>
		<c:choose>
		<c:when test="${not empty currentSiteId && currentSiteId eq TBS_BuyBuyBabySite}">
			<c:set var="weddingTheme" value="" scope="request" />
		</c:when>
		<c:when test="${not empty eventTypeParam && eventTypeParam eq 'Baby'}">
			<c:set var="weddingTheme" value="" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="weddingTheme" value="weddingTheme" scope="request" />
		</c:otherwise>
	</c:choose>
	<c:set var="pageWrapper" value="viewRegistry guestView printerFriendly" scope="request" />
	<link rel="stylesheet" type="text/css" href="/_assets/global/css/global.css"/> 
	<link rel="stylesheet" type="text/css" href="/_assets/global/css/12colgrid.css"/> 
	<script type="text/javascript" src="${assetDomainName}${contextPath}/resources/js/src/legacy/jquery-1.7.1.min.js"></script>
	<script type="text/javascript">
		function errNoImageFound(obj) {
			var $container = $('body');

			if (typeof obj !== 'undefined' && $(obj)[0]) {
				$container = $(obj);
			}

			var loopObj = $container.find('img.noImageFound'),
				noImgURL = '${imagePath}/_assets/global/images/no_image_available.jpg';

			if (loopObj[0]) {
				loopObj.each(function (i, e) {
					var $this = $(this);

					$this.unbind('error.errNoImageFound abort.errNoImageFound').bind('error.errNoImageFound abort.errNoImageFound', function () {
						var $this = $(this);

						// catch if even the no_image_available fails to load
						if ($this.attr('src').indexOf(noImgURL) > -1) {
							$this.addClass('errBBBNoImageFound').unbind('.errNoImageFound');
						} else {
							$this.data('errImageURL', $this.attr('src')).attr('src', noImgURL);
						}
					}).unbind('load.errNoImageFound').bind('load.errNoImageFound', function () {
						var $this = $(this);

						if ($this.attr('src').indexOf(noImgURL) > -1) {
							$this.addClass('errBBBNoImageFound');
						} else {
							$this.removeClass('errBBBNoImageFound');
						}
					});

					try {
						$this.attr('src', $this.attr('src'));
					} catch (e) {
						$this.attr('src', noImgURL);
					}
				});
			}
		};
		$(function () {
			errNoImageFound();
			$('td.breakWord').removeClass('wordBreak');
			onLoadCall();
		});
		function onLoadCall(){
			window.print(); 
			return false;
		}
	</script>
	<style type="text/css">
		.clear { clear: both; display: block; overflow: hidden; visibility: hidden; width: 0; height: 0; }
		.clearfix:before, .clearfix:after { content: '\0020'; display: block; overflow: hidden; visibility: hidden; width: 0; height: 0; }
		.clearfix:after { clear: both; }
		.clearfix { zoom: 1; }
		.bold { font-weight: bold !important; }
		.normal { font-weight: normal !important;}
		.italic, .italics { font-style: italic !important; }
		.noline { text-decoration: none !important; }
		.underline { text-decoration: underline !important; }
		.overline { text-decoration: overline !important; }
		.strike { text-decoration: line-through !important; }
		.textLeft { text-align: left !important; }
		.textRight { text-align: right !important; }
		.textCenter { text-align: center !important; }
		.upperCase, .allCaps { text-transform: uppercase !important; }
		.capitalize, .wordCaps { text-transform: capitalize !important; }
		.lowerCase, .noCaps { text-transform: lowercase !important; }
		.noTransform {text-transform: none !important; }
		.marAuto { display: block !important; margin: 0 auto !important; }
		.noMar { margin: 0 !important; }
		.noMarLeft { margin-left: 0 !important; }
		.noMarRight { margin-right: 0 !important; }
		.noMarBot { margin-bottom: 0 !important; }
		.noMarTop { margin-top: 0 !important; }
		.marLeft_5 { margin-left: 5px !important; }
		.marLeft_10 { margin-left: 10px !important; }
		.marLeft_20 { margin-left: 20px !important; }
		.marLeft_25 { margin-left: 25px !important; }
		.marTop_5 { margin-top: 5px !important; }
		.marTop_10 { margin-top: 10px !important; }
		.marTop_20 { margin-top: 20px !important; }
		.marTop_25 { margin-top: 25px !important; }
		.marRight_5 { margin-right: 5px !important; }
		.marRight_10 { margin-right: 10px !important; }
		.marRight_20 { margin-right: 20px !important; }
		.marRight_25 { margin-right: 25px !important; }
		.marBottom_5 { margin-bottom: 5px !important; }
		.marBottom_10 { margin-bottom: 10px !important; }
		.marBottom_20 { margin-bottom: 20px !important; }
		.marBottom_25 { margin-bottom: 25px !important; }
		.noPad { padding: 0 !important; }
		.noPadLeft { padding-left: 0 !important; }
		.noPadRight { padding-right: 0 !important; }
		.noPadBot { padding-bottom: 0 !important; }
		.noPadTop { padding-top: 0 !important; }
		.padLeft_5 { padding-left: 5px !important; }
		.padLeft_10 { padding-left: 10px !important; }
		.padLeft_20 { padding-left: 20px !important; }
		.padLeft_25 { padding-left: 25px !important; }
		.padLeft_169 { padding-left: 169px !important; }
		.padTop_5 { padding-top: 5px !important; }
		.padTop_10 { padding-top: 10px !important; }
		.padTop_20 { padding-top: 20px !important; }
		.padTop_25 { padding-top: 25px !important; }
		.padRight_5 { padding-right: 5px !important; }
		.padRight_10 { padding-right: 10px !important; }
		.padRight_20 { padding-right: 20px !important; }
		.padRight_25 { padding-right: 25px !important; }
		.padBottom_5 { padding-bottom: 5px !important; }
		.padBottom_10 { padding-bottom: 10px !important; }
		.padBottom_20 { padding-bottom: 20px !important; }
		.padBottom_25 { padding-bottom: 25px !important; }
		.noBorder { border: 0 !important; border-collapse: collapse !important; }
		.noBorderLeft { border-left: 0 !important; }
		.noBorderRight { border-right: 0 !important; }
		.noBorderBot { border-bottom: 0 !important; }
		.noBorderTop { border-top: 0 !important; }
		.noBorderRadius { -webkit-border-radius: 0 !important; -moz-border-radius: 0 !important; border-radius: 0 !important; }
		.noBG { background: transparent !important; }
		.noOverflow { overflow: hidden !important; }
		.overVis, .overflowVisible { overflow: visible !important; }
		.topMost { z-index: 2147483647 !important; }
		.botMost { z-index: 0 !important; }
		.posRel { position: relative !important; }
		.posFix { position: fixed !important; }
		.posAbs { position: absolute !important; }
		.posLeft { left: 0 !important; }
		.posRight { right: 0 !important; }
		.posBot { bottom: 0 !important; }
		.posTop { top: 0 !important; }
		.posBotLeft { bottom: 0 !important; left: 0 !important; }
		.posBotRight { bottom: 0 !important; right: 0 !important; }
		.posTopLeft { top: 0 !important; left: 0 !important; }
		.posTopRight { top: 0 !important; right: 0 !important; }
		.fl { float: left !important; }
		.fr { float: right !important; }
		.cl { clear: left !important; }
		.cr { clear: right !important; }
		.cb { clear: both !important; }
		.cf { float: none !important; }
		.block { display: block !important; }
		.inline { display: inline !important; }
		.inlineBlock { display: inline-block !important; }
		.hidden { display: none !important; }
		.invisible, #themeWrapper .invisible, body .invisible, .invisible *, #themeWrapper .invisible *, body .invisible * { visibility: hidden !important; }
		.visible, #themeWrapper .visible, body .visible, .visible *, #themeWrapper .visible *, body .visible * { visibility: visible !important; }
		.smallText { font-size: 10px !important; color: #666; }
		.itemNotAvailable{font-size:11px !important;color: #444;}
		.disableText { color: #999 !important }
		.offScreen { position: absolute !important; top: -10000000px !important; left: -10000000px !important; }
		.widAuto { width: auto !important; }
		.capitalize { text-transform: capitalize; }
		.seeThrough { -ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=40)"; filter:alpha(opacity=40); opacity:0.4; }
		.zoom { zoom: 1 !important; }
		.breakWord { word-wrap: break-word !important; word-break: break-word !important; }
		.error span, .error { display: block !important; color: #ff0000 !important; }
		.fontSize12 {
			font-size: 12px;
		}
		.fontSize12All,
		fontSize12All * {
			font-size: 12px !important;
		}
		* {
			border: 0;
			margin: 0;
			padding: 0;
			font-family: 'Arial';
			font-weight: normal;
			border-collapse: collapse;
			border-spacing: 0;
			list-style-type: none;
		}
		#themeWrapper {
			padding-left: 10px;
			padding-right: 10px;
		}
		#pageWrapper {
			width: 976px;
		}
		h1,
		td.reg-prod-name {
			color: #273691;
			font-size: 30px;
		}
		h2 {
			font-size: 25px;
			color: #273691;
		}
		h3 {
			font-size: 21px;
			color: #444444;
		}
		table, th, td {
			vertical-align: top;
			font-size: 12px;
		}
		.reg-number, .reg-msg-oos {
			font-size: 12px;
		}
		.reg-header {
			background: #00aef0;
		}
		.reg-header,
		.reg-content,
		.reg-content-wrapper {
			width: 100%;
		}
		.reg-header h3 {
			color: #fff !important;
			font-size: 15px !important;
			line-height: 21px !important;
			padding: 7px !important;
		}
		.registryTableBaby table,
		.registryTableBridal table,
		.reg-content {
			background: #fff;
			border: 1px solid #d4d3d8;
		}
		.reg-spacer {
			font-size: 11px;
			height: 15px;
		}
		.reg-prod-image {
			width: 120px;
			font-size: 0;
			padding: 10px 0;
			text-align: center;
			border-right: 1px solid #d4d3d8;
		}
		.reg-prod-header th {
			background-color: #f1f1f1;
			border-bottom: 1px solid #d4d3d8;
			text-align: left;
		}
		.reg-prod-header th,
		.reg-prod-content td,
		td.reg-prod-footer {
			padding-top: 10px;
			padding-bottom: 10px;
			padding-left: 10px;
		}
		.reg-prod-name {
			width: 320px;
		}
		.reg-prod-price,
		.reg-prod-purchased,
		.reg-prod-requested,
		.reg-prod-qty {
			width: 70px;
		}
		.reg-prod-color {
			width: 130px;
		}
		.reg-prod-content td {
			font-size: 14px;
			font-weight: bold;
		}
		td.reg-prod-qty input {
			width: 35px;
			height: 35px;
			line-height: 35px;
			font-weight: bold;
			text-align: center;
			border: 1px solid #d4d3d8;
		}
		td.reg-prod-content-extrapad {
			padding-top: 20px;
		}
		.registryTableBaby table th,
		.registryTableBaby table td,
		.registryTableBridal table th,
		.registryTableBridal table td {
			border-right: 1px solid #d4d3d8;
			padding: 7px 10px;
		}
		.registryTableBaby table th,
		.registryTableBridal table th {
			background-color: #f8f8f8;
		}
		.registryTableBaby table td,
		.registryTableBridal table td {
			font-size: 13px;
		}
		#footer {
			font-size: 11px;
			padding: 10px;
			background-color: #273691;
			color: #fff;
			margin:0
		}
		#header div.img {
			padding: 10px;
		}
		#header {
			border-bottom: 3px solid #00aef0;
		}
		.wideColumn {
			width: 300px;
		}
		.purchasedConfirmation h2 {
			background: url("//s7d9.scene7.com/is/image/BedBathandBeyond/images/product_guides/other_20130304_listcheck.jpg") no-repeat scroll 0 7px transparent;
			font: 18px/22px Arial;
			padding: 0 0 0 20px;
		}
		.bb h2 {
			font-size: 25px;
			color:#F75E9F;
			text-transform: lowercase;
		}
		.bb h3 {
			font-size: 20px;
			color: #444444;
			text-transform: lowercase;
		}
		.bb .purchasedConfirmation h2 {
			background: url("//s7d9.scene7.com/is/image/BedBathandBeyond/images/product_guides/other_20130304_listcheck.jpg") no-repeat scroll 0 7px transparent;
			font: 18px/22px Arial;
			padding: 0 0 0 20px;
		}
		.bb #header div.img {
			padding: 0 10px;
		}
		.bb h1 {
			color: #F75E9F;
			text-transform: lowercase;
		}
		.bb .registryTableBaby table,
		.bb .registryTableBridal table,
		.bb .reg-content,
		.bb .reg-prod-image,
		.bb .reg-prod-header th,
		.bb td.reg-prod-qty input,
		.bb .registryTableBaby table th,
		.bb .registryTableBaby table td,
		.bb .registryTableBridal table th,
		.bb .registryTableBridal table td {
			border-color: #c4e4f3;
		}
		.bb .registryTableBaby table th,
		.bb .registryTableBridal table th {
			background-color: #f1f9fc;
		}
		.bb #header {
			background: #f0fbff;
			border-color: #f16c7d;
		}
		.bb .reg-prod-header th {
			background-color: #d9effa;
		}
		.bb #footer {
			background-color: #f16c7d;
		}
		.bb td.reg-prod-name {
			color: #3d97cb;
		}
		.br h1 {
			font-size: 40px;
			color:#666666;
		}
		.br h2 {
			font-size: 24px;
			color:#666666;
		}
		.br h3 {
			font-size:20px;
			color: #444444;
		}
		.br .purchasedConfirmation h2 {
			background: url("//s7d9.scene7.com/is/image/BedBathandBeyond/images/product_guides/other_20130304_listcheck.jpg") no-repeat scroll 0 7px transparent;
			font: 18px/22px Arial;
			padding: 0 0 0 20px;
			margin: 0 0 5px;
		}
		.br .registryTableBaby table,
		.br .registryTableBridal table,
		.br .reg-content,
		.br .reg-prod-image,
		.br .reg-prod-header th,
		.br td.reg-prod-qty input,
		.br .registryTableBaby table th,
		.br .registryTableBaby table td,
		.br .registryTableBridal table th,
		.br .registryTableBridal table td {
			border-color: #C28DED;
		}
		.br .registryTableBaby table th,
		.br .registryTableBridal table th {
			background-color: #f8f8f8;
		}
		.br #header {
			border-color: #49176d;
		}
		.br .reg-header {
			background: #49176D;
		}
		.br .reg-prod-header th {
			background-color: #ddd;
		}
		.br #footer {
			background-color: #49176d;
		}
		.br td.reg-prod-name {
			color: #40195c;
		}

		.prodAttrPrimary_Blue{font-weight:bold; font-size:11px; color: #273691 !important;}
		.prodAttrPrimary_Red{font-weight:bold; font-size:11px; color: #ff0000 !important;}
		.prodAttrPrimary_Green{font-weight:bold; font-size:11px; color: #5CB801 !important;}
		.prodAttrPrimary_LightBlue{font-weight:bold; font-size:11px; color: #00AEF0 !important;}
		.prodAttrPrimary_Grey{font-weight:bold; font-size:11px; color: #666 !important;}
		.prodAttrPrimary_Black{font-weight:bold; font-size:11px; color: #111 !important;}
		.prodAttrPrimary_Orange{font-weight:bold; font-size:11px; color: #FF9018 !important;}
		.prodAttrPrimary_DarkOrange{font-weight:bold; font-size:11px; color: #FF6A23 !important;}
		.prodAttrPrimary_Pink{font-weight:bold; font-size:11px; color: #F75E9F !important;}
		.prodAttrPrimary_LightPink{font-weight:bold; font-size:11px; color: #F49AC1 !important;}
		.prodAttrPrimary_CrimsonRed{font-weight:bold; font-size:11px; color: #e30540 !important;}
		.prodAttrPrimary_GreenOrange{font-weight:bold; font-size:11px; color: #5CB801 !important;}
		.prod-attrib {font:700 11px/1 Helvetica, Arial, Verdana, sans-serif;}
		.prod-attrib-calphalon,
		.prod-attrib-circulon,
		.prod-attrib-giftcard,
		.prod-attrib-villeroy,
		.prod-attrib-year,
		.prod-attrib-giftcard-mfg {
			color:#00aef0;
		}
		.prod-attrib-save-children {
			color:#1f5d04;
		}
		.prod-attrib-exclusive {
			color:#273691;
		}
		.prod-attrib-zoom-img,
		.prod-attrib-nogift,
		.prod-attrib-great-value,
		.prod-attrib-zoom,
		.prod-attrib-beyond-value,
		.prod-attrib-size-chart,
		.prod-attrib-web-only {
			color:#293a8e;
		}
		.prod-attrib-exclusive-color {
			color:#ff5090;
			text-transform:uppercase;
		}
		.prod-attrib-bhg {
			color:#2d7a15;
		}
		.prod-attrib-eco-fee {
			color:#2f7238;
		}
		.prod-attrib-giftcard-para-1,
		.prod-attrib-giftcard-para-2 {
			color:#444444;
		}
		.prod-attrib-bridal {
			color:#512698;
		}
		.prod-attrib-25,
		.prod-attrib-50,
		.prod-attrib-rebate,
		.prod-attrib-dorm,
		.prod-attrib-percent-off,
		.prod-attrib-dollars-off {
			color:#5ab646;
		}
		.prod-attrib-gift-with-purchase,

		.prod-attrib-bpa,
		.prod-attrib-organic,
		.prod-attrib-natural,
		.prod-attrib-energy-guide,
		.prod-attrib-energy-guide-lights {
			color:#5cb801;
		}
		.prod-attrib-pacific-coast-feather,
		.prod-attrib-olympic-queen,
		.prod-attrib-leaded,
		.prod-attrib-length,
		.prod-attrib-special-size,
		.prod-attrib-room-rugs,
		.prod-attrib-additional-colors,
		.prod-attrib-additional-colors-lengths,
		.prod-attrib-toddler,
		.prod-attrib-banquet,
		.prod-attrib-choking,
		.prod-attrib-more-teams,
		.prod-attrib-bleach,
		.prod-attrib-foreman,
		.prod-attrib-twin {
			color:#666666;
		}
		.prod-attrib-mom,
		.prod-attrib-genie {
			color:#e30450;
		}
		.prod-attrib-cure,
		.prod-attrib-cancer {
			color:#ea77af;
		}
		.prod-attrib-sure-fit,
		.prod-attrib-manufacturer-offer,
		.prod-attrib-aerobed-raised,
		.prod-attrib-aerobed-deluxe,
		.prod-attrib-nwhrc,
		.prod-attrib-room-view,
		.prod-attrib-furniture,
		.prod-attrib-back,
		.prod-attrib-mural,
		.prod-attrib-allergy,
		.prod-attrib-special,
		.prod-attrib-limited,
		.prod-attrib-circular,
		.prod-attrib-umbrella,
		.prod-attrib-reversible,
		.prod-attrib-real-simple,
		.prod-attrib-tools,
		.prod-attrib-dehp,
		.prod-attrib-new,
		.prod-attrib-tiny,
		.prod-attrib-super-value,
		.prod-attrib-grill-daddy,
		.prod-attrib-kirsh,
		.prod-attrib-tdcpp {
			color:#f16a2a;
		}
		.prod-attrib-clearance,
		.prod-attrib-tv,
		.prod-attrib-surcharge,
		.prod-attrib-bogo,
		.prod-attrib-free-ship,
		.prod-attrib-vendor-ship,
		.prod-attrib-vendor-delay,
		.prod-attrib-ca-lighting,
		.prod-attrib-beer,
		.prod-attrib-fly,
		.prod-attrib-nyc {
			color:#ff0000;
		}
		.prod-attrib-fragrance,
		.prod-attrib-seasonal {
			color:#ff6a23;
		}
		.prod-attrib-green-guard {
			width:57px;
			height:25px;
			display:inline-block;
			text-indent:-999px;
			overflow:hidden;
			background:url(//s7d9.scene7.com/is/image/BedBathandBeyond/global/images/other/other_20130220_greenguardlogosmall.jpg) no-repeat 0 0;
		}
		.prod-attrib-view-energy-guide {
			background:url("${imagePath}/_assets/global/images/icons/pdf_icon_small.gif") no-repeat 0 0;
			padding-left: 18px;
		}
		.bb .prodAttrPrimary_Blue{font-weight:bold; font-size:11px; color: #F12D81 !important;}	 overridden the blue from global
		.bb .prodAttrPrimary_LightBlue{font-weight:bold; font-size:11px; color: #512698 !important;}  overridden the light blue from global
		.bb .prodAttrPrimary_Grey{font-weight:bold; font-size:11px; color: #594D43 !important;}	 overridden the grey from global
		.bb .prodAttrPrimary_Black{font-weight:bold; font-size:11px; color: #111 !important;}  Same as global no black color in baby
		.bb .prodAttrPrimary_Pink{font-weight:bold; font-size:11px; color: #F75E9F !important;}	 Pink color specific to baby
		.bb .prodAttrPrimary_Green{font-weight:bold; font-size:11px; color: #66BC29 !important;}
		.bb .prodAttrPrimary_Orange{font-weight:bold; font-size:11px; color: #FFC82E !important;}
		.bb .prodAttrPrimary_DarkOrange{font-weight:bold; font-size:11px; color: #EA7125 !important;}
		.bb .prodAttrPrimary_GreenOrange{font-weight:bold; font-size:11px; color: #EA7125 !important;}
		.bb .prod-attrib-circular {
			  color:#5bb746;
		}
		.bb .prod-attrib-giftcard-mfg {
		  color:#ea7125;
		}
		.bb .prod-attrib-new,
		.bb .prod-attrib-surcharge {
		  color:#f12d81;
		}
		.bc .prodAttrPrimary_Blue{font-weight:bold; font-size:11px; color: #273691 !important;}
		.bc .prodAttrPrimary_LightBlue{font-weight:bold; font-size:11px; color: #00AEF0 !important;}
		.bc .prodAttrPrimary_Grey{font-weight:bold; font-size:11px; color: #666 !important;}
		.bc .prodPrimary_LightGreen{font-weight:bold; font-size:11px; color: #8DC63F !important; }
		.bc .prodPrimary_Green{font-weight:bold; font-size:11px; color: #4F8801 !important; }
		.br .prodAttrPrimary_Blue{font-weight:bold; font-size:11px; color: #43165E !important;}
		.br .prodAttrPrimary_LightBlue{font-weight:bold; font-size:11px; color: #00AEF0 !important;}
		.br .prodAttrPrimary_Grey{font-weight:bold; font-size:11px; color: #999 !important;}
		.br .prodAttrPrimary_Black{font-weight:bold; font-size:11px; color: #444 !important;}
		.divIMG {
			width: 146px;
			padding-right: 83px;
		}
		.divTXT {
			width: 727px;
		}
		.weddingTheme .registryTableGifts td, .weddingTheme #footer, .weddingTheme .reg-header {  background-color: #49176D;}
		.weddingTheme td.reg-prod-name {color: #40195C;}
		.weddingTheme h1{ font-family:'ITCNewBaskervilleRoman'; font-size:36px; font-weight:200; color: #656565}
		.registryTableBaby table tr td .wordBreak{float:left; max-width:300px; word-wrap:break-word !important;}
		.registryTableBaby table th, .registryTableBaby table td{text-align:left}
		.registryTableBaby table td span{font-weight:bold}
		p, dl, hr, h1, h2, h3, h4, h5, h6, ol, ul, pre, table, address, fieldset, figure{margin:0}
		.registryTableBaby table td span {
			display: block;
			float: left;
			font-weight: bold;
			max-width: 140px;
			word-wrap: break-word !important;
		}
		.reg-prod-footer .prodAttrPrimary_Blue{
		color: #666666 !important;
		font-size: 11px;
		font-weight: bold;
		}
	</style>
</head>

<body id="themeWrapper" class="${themeName} ${weddingTheme}">
	<dsp:droplet name="/com/bbb/logging/NetworkInfoDroplet">
		<dsp:oparam name="output">
			<!--googleoff: all-->
			<div id="userNetworkInfo" class="hidden">
				<ul>
					<li id="session_id"><dsp:valueof param="SESSION_ID" /></li>
					<li id="jvm_name"><dsp:valueof bean="/atg/dynamo/service/IdGenerator.dcPrefix"/>-<dsp:valueof param="JVM_NAME" /></li>
					<li id="time"><dsp:valueof param="TIME" /></li>
				</ul>
			</div>
			<!--googleon: all-->
		</dsp:oparam>
	</dsp:droplet>
	<div id="pageWrapper" class="${pageWrapper}">
		<div id="header" class="cb">
			<div class="img">
				<c:set var="logoWidth" value="173" />
				<c:if test="${currentSiteId == TBS_BedBathCanadaSite}">
					<c:set var="logoCountry" value="_ca" />
					<c:set var="logoWidth" value="200" />
				</c:if>
				<c:choose>
					<c:when test="${currentSiteId == TBS_BedBathCanadaSite || currentSiteId == TBS_BedBathUSSite}">
						<c:choose>
							<c:when test="${themeName == 'br'}">
								<img src="${imagePath}/_assets/global/images/logo/logo_br${logoCountry}.png" height="50" width="${logoWidth}" alt="Bed Bath &amp; Beyond - Wedding Invitations &amp; Accessories" />
							</c:when>
							<c:when test="${themeName == 'bc'}">
								<c:choose>
									<c:when test="${currentSiteId == TBS_BedBathCanadaSite}">
										<img src="${imagePath}/_assets/global/images/logo/logo_bbb${logoCountry}.png" height="50" width="${logoWidth}" alt="Bed Bath &amp; Beyond - We Love University" />
									</c:when>
									<c:otherwise>
										<img src="${imagePath}/_assets/global/images/logo/logo_bbb${logoCountry}.png" height="50" width="${logoWidth}" alt="Bed Bath &amp; Beyond - We Love College" />
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<img src="${imagePath}/_assets/global/images/logo/logo_bbb${logoCountry}.png" height="50" width="${logoWidth}" alt="Bed Bath &amp; Beyond" />
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<img src="${imagePath}/_assets/global/images/logo/logo_bb_circle.png" height="122" width="127" alt="buybuy BABY" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div id="content" class="cb">
			<c:if test="${not empty registryId}">
				<dsp:droplet name="RegistryInfoDisplayDroplet">
					<dsp:param value="${registryId}" name="registryId" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="eventType" param="registrySummaryVO.eventType"/>
						<dsp:getvalueof var="primaryRegistrantFirstName" param="registrySummaryVO.primaryRegistrantFirstName"/>
						<dsp:getvalueof var="primaryRegistrantLastName" param="registrySummaryVO.primaryRegistrantLastName"/>
						<dsp:getvalueof var="coRegistrantFirstName" param="registrySummaryVO.coRegistrantFirstName"/>
						<dsp:getvalueof var="coRegistrantLastName" param="registrySummaryVO.coRegistrantLastName"/>
						<dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>
						<dsp:getvalueof var="eventDate" param="registrySummaryVO.eventDate" />
						<dsp:getvalueof var="babyName" param="registrySummaryVO.eventVO.babyName"/>
						<dsp:getvalueof var="babyShowerDate" param="registrySummaryVO.eventVO.showerDateObject"/>
						<dsp:getvalueof var="babyGender" param="registrySummaryVO.eventVO.babyGender"/>
						<dsp:getvalueof var="babyNurseryTheme" param="registrySummaryVO.eventVO.babyNurseryTheme"/>
						<c:choose>
							<c:when test="${eventType eq  eventTypeParam}">
								<h1 class="padTop_10 padBottom_10 bold">
									<dsp:droplet name="IsEmpty">
										<dsp:param value="${primaryRegistrantFirstName}" name="value"/>
										<dsp:oparam name="false">
											 ${primaryRegistrantFirstName}&nbsp;${primaryRegistrantLastName}
										 </dsp:oparam>
										 <dsp:oparam name="true">
											 &nbsp;
										 </dsp:oparam>
									</dsp:droplet>
									<dsp:droplet name="IsEmpty">
										<dsp:param value="${coRegistrantFirstName}" name="value"/>
										<dsp:oparam name="false">&amp;&nbsp;${coRegistrantFirstName}&nbsp;${coRegistrantLastName}
										</dsp:oparam>
									</dsp:droplet>
								</h1>

								<c:if test="${eventTypeCode eq 'BA1' }">
									<p class="reg-number padTop_10 padBottom_10">Registry #: <span class="bold">${registryId}</span></p>
								</c:if>

								<c:choose>
									<c:when test="${eventTypeCode ne 'BA1' }">
										<div class="registryTableBridal padBottom_10">
											<table width="100%" cellpadding="0" cellspacing="0" border="0">
												<tr>
													 <th class="textLeft"><span>Registry Type</span></th>
													 <th class="textLeft"><span>Event Date</span></th>
													 <th class="textLeft"><span>Registry No.</span></th>
												</tr>
												<tr>
													<td class="textLeft bold">${eventType}</td>
													<td class="textLeft bold">${eventDate}</td>
													<td class="textLeft bold">${registryId}</td>
												</tr>
											</table>
										</div>
									</c:when>
									<c:otherwise>
										<div class="registryTableBaby padBottom_10">
									
                            			<table class="width_12">
											<thead>
												<tr>
													 <th class="width_2"><span>Registry Type</span></th>
													 <th class="width_2"><span>Baby's Expected Arrival Date</span></th>
													<c:if test="${not empty babyShowerDate}">
														 <th class="width_2"><span>Shower Date</span></th>
													</c:if>
													<c:if test="${not empty babyName}">
														 <th class="width_2"><span><bbbl:label key='lbl_mng_regitem_baby_name' language="${pageContext.request.locale.language}" /></span></th>
													</c:if>
													<c:if test="${not empty babyGender}">
														 <th class="width_2"><span>Baby Gender</span></th>
													</c:if>
													<c:if test="${not empty babyNurseryTheme}">
														 <th class="width_2"><span>
														<bbbl:label key='lbl_mng_regitem_nursery_theme' language="${pageContext.request.locale.language}" /></span></th>
													</c:if>
									
												</tr>
											</thead>

											<tbody>
												<tr>
													<td class="width_2"><span>${eventType}</span></td>
													<td class="width_2"><span>${eventDate}</span></td>
													<c:if test="${not empty babyShowerDate}">
														<td class="width_2"><span><dsp:valueof param="registrySummaryVO.eventVO.showerDateObject" converter="date" date="MM/dd/yyyy"/></span></td>
													</c:if>
													<c:if test="${not empty babyName}">
														<td class="width_2 breakWord"><span>${babyName}</span></td>
													</c:if>
													<c:if test="${not empty babyGender}">
														<td class="width_2"><span>
															<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/GetGenderKeyDroplet">
																<dsp:param name="genderKey" value="${babyGender}"/>
																<dsp:param name="inverseflag" value="true"/>
																<dsp:oparam name="output">
																	<dsp:getvalueof var="genderValue" param="genderCode" />
																</dsp:oparam>
															</dsp:droplet>
														${genderValue}
														</span></td>
													</c:if>
													<c:if test="${not empty babyNurseryTheme}">
														<td class="width_2 f1 breakWord"><span>${babyNurseryTheme}</span></td>
													</c:if>
												</tr>
											</tbody>
											</table>
										</div>
									</c:otherwise>
								</c:choose>

								<div class="giftViewSortingControls padBottom_10">
									<table width="100%" cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td class="textLeft">
												<bbbl:label key='lbl_mng_regitem_sortby' language="${pageContext.request.locale.language}" />:
												<c:if test="${(empty sortSeq) || sortSeq ==1}">
													<span class="bold"><bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /></span>
												</c:if>
												<c:if test="${(not empty sortSeq) && sortSeq ==2}">
													<span class="bold"><bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></span>
												</c:if>
											</td>
											<td class="textRight">
												<bbbl:label key='lbl_mng_regitem_view' language="${pageContext.request.locale.language}" />:
												<c:if test="${(empty view) || view ==1}">
													<span class="bold"><bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></span>
												</c:if>
												<c:if test="${(not empty view) && view ==2}">
													<span class="bold"><bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></span>
												</c:if>
												<c:if test="${(not empty view) && view ==3}">
													<span class="bold"><bbbl:label key='lbl_mng_regitem_purchased_sort' language="${pageContext.request.locale.language}" /></span>
												</c:if>
											</td>
										</tr>
									</table>
								</div>

								<dsp:droplet name="RegistryItemsDisplayDroplet">
									<dsp:param name="registryId" value="${registryId}" />
									<dsp:param name="startIdx" value="0" />
									<dsp:param name="isGiftGiver" value="false" />
									<dsp:param name="blkSize" value="${maxBulkSize}" />
									<dsp:param name="isAvailForWebPurchaseFlag" value="false" />
									<dsp:param name="userToken" value="UT1021" />
									<dsp:param name="sortSeq" value="${sortSeq}" />
									<dsp:param name="view" value="${view}" />
									<dsp:param name="siteId" value="${currentSiteId}"/>
									<dsp:param name="profile" bean="Profile"/>
									<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
									<dsp:param name="eventType" value="${eventType}"/>
									<dsp:getvalueof var="emptyList" param="emptyList" />

									<%--RegistryItemsDisplayDroplet output parameter starts --%>
									<dsp:oparam name="output">
										<c:choose>
											<c:when test="${emptyList eq 'true'}">
												<div class="padTop_10 padBottom_10">
													<p class="error fontSize12">
														<c:choose>
															<c:when test="${view eq '3'}">
																<bbbe:error key="err_no_pur_reg_item_details" language="${pageContext.request.locale.language}"/>
															</c:when>
															<c:when test="${view eq '2'}">
																<bbbe:error key="err_no_rem_reg_item_details" language="${pageContext.request.locale.language}"/>
															</c:when>
															<c:otherwise>
																<bbbe:error key="err_no_reg_item_details" language="${pageContext.request.locale.language}"/>
															</c:otherwise>
														</c:choose>
													</p>
												</div>
											</c:when>
											<c:otherwise>
												<c:if test="${(empty sortSeq) || sortSeq ==1}">
													<%-- For Each Droplet for InStock normal Categories --%>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="inStockCategoryBuckets" />
														
														<dsp:oparam name="output">
															<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
															<dsp:getvalueof var="bucket" param="inStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
															<c:if test="${bucketName ne othersCat}">
																<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
																<dsp:getvalueof var="categoryName" param="key"/>
																<table width="100%" cellpadding="0" cellspacing="0" border="0">
																	<c:if test="${count ne 0}">
																		<tr>
																			<td class="reg-header"><h3>${categoryName}&nbsp;(${count})</h3></td>
																		</tr>
																	</c:if>
																	<dsp:droplet name="ForEach">
																		<dsp:param name="array" param="inStockCategoryBuckets.${bucketName}" />
																		<dsp:param name="elementName" value="regItem" />
																		<dsp:oparam name="output">
																			<c:if test="${count ne 0}">
																				<dsp:getvalueof var="skuID" vartype="java.lang.String"	param="regItem.sku" />
																				<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
																				<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
																				<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
																				<c:set var="productName"><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:set>
																				<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.smallImage" />
																				<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
																				<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
																				<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
																				<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
																				<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
																				<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
																					<dsp:param name="skuId" value="${skuID}" />
																					<dsp:oparam name="output">
																						<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
																						<dsp:getvalueof var="inStoreSku" param="inStore" />
																					</dsp:oparam>
																				</dsp:droplet>
																				<tr>
																					<td class="reg-content-wrapper">
																						<table width="100%" cellpadding="0" cellspacing="0" border="0">
																								<tr>
																									<td class="reg-content">
																										<table width="100%" cellpadding="0" cellspacing="0" border="0">
																											<tr>
																												<td class="reg-prod-image">
																												<c:choose>
																													<c:when test="${empty imageURL}">
																														<img class="prodImage noImageFound" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" title="${productName}" height="106" width="78">
																													</c:when>
																													<c:otherwise>
																														<img class="prodImage noImageFound" src="${scene7Path}/${imageURL}" alt="${productName}" title="${productName}" height="106" width="78">
																													</c:otherwise>
																												</c:choose>
																												</td>
																												<td class="reg-prod-info">
																													<table width="100%" cellpadding="0" cellspacing="0" border="0">
																														<tr>
																															<td class="reg-prod-info-inner">
																																<table width="100%" cellpadding="0" cellspacing="0" border="0">
																																	<tr class="reg-prod-header">
																																		<th class="reg-prod-name reg-prod-firstcol breakWord">
																																			<c:if test="${upc ne null}">
																																				<div class="fl upc">
																																					<span class="bold"><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></span>
																																					<span class="padLeft_5">${upc}</span>
																																				</div>
																																			</c:if>
																																			<c:if test="${(not empty upc) && (not empty color)}">
																																				<div class="fl padLeft_5 padRight_5">|</div>
																																			</c:if>
																																			<c:if test="${color ne null}">
																																				<div class="fl bold"><bbbl:label key='lbl_mng_regitem_color' language="${pageContext.request.locale.language}" /></div>
																																				<div class="fl reg-prod-color padLeft_5 breakWord">${color}</div>
																																			</c:if>
																																			<div class="clear"></div>
																																		</th>
																																		<th class="reg-prod-price textCenter"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></th>
																																		<th class="reg-prod-requested textCenter"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></th>
																																		<th class="reg-prod-purchased textCenter"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></th>
																																		<%-- <th class="reg-prod-qty textCenter"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></th> --%>
																																		<th class="reg-prod-lastcol">&nbsp;</th>
																																	</tr>
																																	<tr class="reg-prod-content">
																																		<td class="reg-prod-name reg-prod-firstcol reg-prod-content-extrapad breakWord">${productName}</td>
																																		<td class="reg-prod-price textCenter reg-prod-content-extrapad">
																																			<dsp:droplet name="PriceDroplet">
																																				<dsp:param name="product" value="${productId}" />
																																				<dsp:param name="sku" value="${skuID}"/>
																																				<dsp:oparam name="output">
																																					<dsp:setvalue param="theListPrice" paramvalue="price"/>
																																					<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
																																					<c:choose>
																																						<c:when test="${not empty profileSalePriceList}">
																																							<dsp:droplet name="PriceDroplet">
																																								<dsp:param name="priceList" bean="Profile.salePriceList"/>
																																								<dsp:oparam name="output">
																																									<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																									<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
																																										<c:if test="${listPrice gt 0.10}">
																																										<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																											<dsp:param name="price" value="${listPrice }"/>
																																										</dsp:include>
																																				                        </c:if>
																																								</dsp:oparam>
																																								<dsp:oparam name="empty">
																																									<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																									<c:if test="${price gt 0.10}">
																																										<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																											<dsp:param name="price" value="${price }"/>
																																										</dsp:include>
																																									</c:if>
																																								</dsp:oparam>
																																							</dsp:droplet><%-- End price droplet on sale price --%>
																																						</c:when>
																																						<c:otherwise>
																																							<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																							<c:if test="${price gt 0.10}">
																																								<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																									<dsp:param name="price" value="${price }"/>
																																								</dsp:include>
																																							</c:if>
																																						</c:otherwise>
																																					</c:choose>
																																				</dsp:oparam>
																																			</dsp:droplet>
																																		</td>
																																		<td class="reg-prod-requested textCenter reg-prod-content-extrapad">${requestedQuantity}</td>
																																		<td class="reg-prod-purchased textCenter reg-prod-content-extrapad">${purchasedQuantity}</td>
																																		<%-- <td class="reg-prod-qty textCenter"><input readonly="readonly" name="qty" type="text" value="1" class="" /></td> --%>
																																		<td class="reg-prod-lastcol reg-prod-content-extrapad">
																																			<c:if test="${purchasedQuantity >= requestedQuantity}">
																																				<div class="purchasedConfirmation">
																																					<h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
																																				</div>
																																			</c:if>
																																		</td>
																																	</tr>
																																</table>
																															</td>
																														</tr>
																														<tr>
																															<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
																																<dsp:param name="elementName" value="attributeVOList"/>
																																<dsp:oparam name="outputStart">
																																	<td class="reg-prod-footer">
																																</dsp:oparam>
																																<dsp:oparam name="output">
																																	<dsp:getvalueof var="pageName" param="key" />
																																	<c:if test="${pageName eq 'RLP'}">
																																		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																		<dsp:param param="attributeVOList" name="array" />
																																		<dsp:param name="elementName" value="attributeVO"/>
																																		<dsp:param name="sortProperties" value="+priority"/>
																																			<dsp:oparam name="output">
																																				<dsp:getvalueof var="size" param="size"/>
																																				<dsp:getvalueof var="count" param="count"/>
																																				<c:set var="attributeDescrip"><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></c:set>
																																					<div class="fl bold">${attributeDescrip}</div><!-- this should come in Bold, check with site Dev -->
																																					<c:if test="${count < size}">
																																						<div class="fl padLeft_5 padRight_5">|</div>
																																					</c:if>
																																			</dsp:oparam>
																																		</dsp:droplet>
																																	</c:if>
																																</dsp:oparam>
																																<dsp:oparam name="outputEnd">
																																	<div class="clear"></div>
																																	</td>
																																</dsp:oparam>
																															</dsp:droplet>
																														</tr>
																													</table>
																												</td>
																											</tr>
																										</table>
																									</td>
																								</tr>
																								<tr>
																									<td class="reg-spacer">&nbsp;</td>
																								</tr>
																						</table>
																					</td>
																				</tr>
																			</c:if>
																		</dsp:oparam>
																	</dsp:droplet>
																</table>
															</c:if>
														</dsp:oparam>
													</dsp:droplet>
													<%-- For Each Droplet for InStock other Categorie --%>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="inStockCategoryBuckets" />
														
														<dsp:oparam name="output">
															<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
															<dsp:getvalueof var="bucket" param="inStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
															<c:if test="${bucketName eq othersCat}">
																<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
																<dsp:getvalueof var="categoryName" param="key"/>
																<table width="100%" cellpadding="0" cellspacing="0" border="0">
																	<c:if test="${count ne 0}">
																		<tr>
																			<td class="reg-header"><h3>${categoryName}&nbsp;(${count})</h3></td>
																		</tr>
																	</c:if>
																	<dsp:droplet name="ForEach">
																		<dsp:param name="array" param="inStockCategoryBuckets.${bucketName}" />
																		<dsp:param name="elementName" value="regItem" />
																		<dsp:oparam name="output">
																			<c:if test="${count ne 0}">
																				<dsp:getvalueof var="skuID" vartype="java.lang.String"	param="regItem.sku" />
																				<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
																				<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
																				<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
																				<c:set var="productName"><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:set>
																				<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.smallImage" />
																				<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
																				<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
																				<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
																				<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
																				<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
																				<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
																					<dsp:param name="skuId" value="${skuID}" />
																					<dsp:oparam name="output">
																						<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
																						<dsp:getvalueof var="inStoreSku" param="inStore" />
																					</dsp:oparam>
																				</dsp:droplet>
																				<tr>
																					<td class="reg-content-wrapper">
																						<table width="100%" cellpadding="0" cellspacing="0" border="0">
																								<tr>
																									<td class="reg-content">
																										<table width="100%" cellpadding="0" cellspacing="0" border="0">
																											<tr>
																												<td class="reg-prod-image">
																												<c:choose>
																													<c:when test="${empty imageURL}">
																														<img class="prodImage noImageFound" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" title="${productName}" height="106" width="78">
																													</c:when>
																													<c:otherwise>
																														<img class="prodImage noImageFound" src="${scene7Path}/${imageURL}" alt="${productName}" title="${productName}" height="106" width="78">
																													</c:otherwise>
																												</c:choose>
																												</td>
																												<td class="reg-prod-info">
																													<table width="100%" cellpadding="0" cellspacing="0" border="0">
																														<tr>
																															<td class="reg-prod-info-inner">
																																<table width="100%" cellpadding="0" cellspacing="0" border="0">
																																	<tr class="reg-prod-header">
																																		<th class="reg-prod-name reg-prod-firstcol breakWord">
																																			<c:if test="${upc ne null}">
																																				<div class="fl upc">
																																					<span class="bold"><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></span>
																																					<span class="padLeft_5">${upc}</span>
																																				</div>
																																			</c:if>
																																			<c:if test="${(not empty upc) && (not empty color)}">
																																				<div class="fl padLeft_5 padRight_5">|</div>
																																			</c:if>
																																			<c:if test="${color ne null}">
																																				<div class="fl bold"><bbbl:label key='lbl_mng_regitem_color' language="${pageContext.request.locale.language}" /></div>
																																				<div class="fl reg-prod-color padLeft_5 breakWord">${color}</div>
																																			</c:if>
																																			<div class="clear"></div>
																																		</th>
																																		<th class="reg-prod-price textCenter"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></th>
																																		<th class="reg-prod-requested textCenter"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></th>
																																		<th class="reg-prod-purchased textCenter"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></th>
																																		<%-- <th class="reg-prod-qty textCenter"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></th> --%>
																																		<th class="reg-prod-lastcol">&nbsp;</th>
																																	</tr>
																																	<tr class="reg-prod-content">
																																		<td class="reg-prod-name reg-prod-firstcol reg-prod-content-extrapad breakWord">${productName}</td>
																																		<td class="reg-prod-price textCenter reg-prod-content-extrapad">
																																			<dsp:droplet name="PriceDroplet">
																																				<dsp:param name="product" value="${productId}" />
																																				<dsp:param name="sku" value="${skuID}"/>
																																				<dsp:oparam name="output">
																																					<dsp:setvalue param="theListPrice" paramvalue="price"/>
																																					<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
																																					<c:choose>
																																						<c:when test="${not empty profileSalePriceList}">
																																							<dsp:droplet name="PriceDroplet">
																																								<dsp:param name="priceList" bean="Profile.salePriceList"/>
																																								<dsp:oparam name="output">
																																									<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																									<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
																																										<c:if test="${listPrice gt 0.10}">
																																										<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																											<dsp:param name="price" value="${listPrice }"/>
																																										</dsp:include>
																																				                        </c:if>
																																								</dsp:oparam>
																																								<dsp:oparam name="empty">
																																									<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																									<c:if test="${price gt 0.10}">
																																										<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																											<dsp:param name="price" value="${price }"/>
																																										</dsp:include>
																																									</c:if>
																																								</dsp:oparam>
																																							</dsp:droplet><%-- End price droplet on sale price --%>
																																						</c:when>
																																						<c:otherwise>
																																							<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																								<c:if test="${price gt 0.10}">
																																								<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																									<dsp:param name="price" value="${price }"/>
																																								</dsp:include>
																																									</c:if>
																																						</c:otherwise>
																																					</c:choose>
																																				</dsp:oparam>
																																			</dsp:droplet>
																																		</td>
																																		<td class="reg-prod-requested textCenter reg-prod-content-extrapad">${requestedQuantity}</td>
																																		<td class="reg-prod-purchased textCenter reg-prod-content-extrapad">${purchasedQuantity}</td>
																																		<%-- <td class="reg-prod-qty textCenter"><input readonly="readonly" name="qty" type="text" value="1" class="" /></td> --%>
																																		<td class="reg-prod-lastcol reg-prod-content-extrapad">
																																			<c:if test="${purchasedQuantity >= requestedQuantity}">
																																				<div class="purchasedConfirmation">
																																					<h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
																																				</div>
																																			</c:if>
																																		</td>
																																	</tr>
																																</table>
																															</td>
																														</tr>
																														<tr>
																															<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
																																<dsp:param name="elementName" value="attributeVOList"/>
																																<dsp:oparam name="outputStart">
																																	<td class="reg-prod-footer">
																																</dsp:oparam>
																																<dsp:oparam name="output">
																																	<dsp:getvalueof var="pageName" param="key" />
																																	<c:if test="${pageName eq 'RLP'}">
																																		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																		<dsp:param param="attributeVOList" name="array" />
																																		<dsp:param name="elementName" value="attributeVO"/>
																																		<dsp:param name="sortProperties" value="+priority"/>
																																			<dsp:oparam name="output">
																																				<dsp:getvalueof var="size" param="size"/>
																																				<dsp:getvalueof var="count" param="count"/>
																																				<c:set var="attributeDescrip"><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></c:set>
																																					<div class="fl bold">${attributeDescrip}</div><!-- this should come in Bold, check with site Dev -->
																																					<c:if test="${count < size}">
																																						<div class="fl padLeft_5 padRight_5">|</div>
																																					</c:if>
																																			</dsp:oparam>
																																		</dsp:droplet>
																																	</c:if>
																																</dsp:oparam>
																																<dsp:oparam name="outputEnd">
																																	<div class="clear"></div>
																																	</td>
																																</dsp:oparam>
																															</dsp:droplet>
																														</tr>
																													</table>
																												</td>
																											</tr>
																										</table>
																									</td>
																								</tr>
																								<tr>
																									<td class="reg-spacer">&nbsp;</td>
																								</tr>
																						</table>
																					</td>
																				</tr>
																			</c:if>
																		</dsp:oparam>
																	</dsp:droplet>
																</table>
															</c:if>
														</dsp:oparam>
													</dsp:droplet>
													<dsp:getvalueof var="emptyOutOfStockListFlag" param="emptyOutOfStockListFlag"/>
													<c:if test="${emptyOutOfStockListFlag ne 'true' }">
														<p class="reg-msg-oos padBottom_10 padTop_10"><bbbt:textArea key="txt_mng_regitem_items_unavail" language="${pageContext.request.locale.language}" /></p>
														<%-- For Each Droplet for NotInStock normal Categories --%>
														<dsp:droplet name="ForEach">
															<dsp:param name="array" param="notInStockCategoryBuckets" />
															
															<dsp:oparam name="output">
																<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
																<dsp:getvalueof var="bucket" param="notInStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
																<c:if test="${bucketName ne othersCat}">
																	<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
																	<dsp:getvalueof var="categoryName" param="key"/>
																	<table width="100%" cellpadding="0" cellspacing="0" border="0">
																		<c:if test="${count ne 0}">
																			<tr>
																				<td class="reg-header"><h3>${categoryName}&nbsp;(${count})</h3></td>
																			</tr>
																		</c:if>
																		<dsp:droplet name="ForEach">
																			<dsp:param name="array" param="notInStockCategoryBuckets.${bucketName}" />
																			<dsp:param name="elementName" value="regItem" />
																			<dsp:oparam name="output">
																				<c:if test="${count ne 0}">
																					<dsp:getvalueof var="skuID" vartype="java.lang.String"	param="regItem.sku" />
																					<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
																					<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
																					<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
																					<c:set var="productName"><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:set>
																					<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.smallImage" />
																					<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
																					<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
																					<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
																					<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
																					<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
																					<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
																						<dsp:param name="skuId" value="${skuID}" />
																						<dsp:oparam name="output">
																							<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
																							<dsp:getvalueof var="inStoreSku" param="inStore" />
																						</dsp:oparam>
																					</dsp:droplet>
																					<tr>
																						<td class="reg-content-wrapper">
																							<table width="100%" cellpadding="0" cellspacing="0" border="0">
																									<tr>
																										<td class="reg-content">
																											<table width="100%" cellpadding="0" cellspacing="0" border="0">
																												<tr>
																													<td class="reg-prod-image">
																													<c:choose>
																														<c:when test="${empty imageURL}">
																															<img class="prodImage noImageFound" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" title="${productName}" height="106" width="78">
																														</c:when>
																														<c:otherwise>
																															<img class="prodImage noImageFound" src="${scene7Path}/${imageURL}" alt="${productName}" title="${productName}" height="106" width="78">
																														</c:otherwise>
																													</c:choose>
																													</td>
																													<td class="reg-prod-info">
																														<table width="100%" cellpadding="0" cellspacing="0" border="0">
																															<tr>
																																<td class="reg-prod-info-inner">
																																	<table width="100%" cellpadding="0" cellspacing="0" border="0">
																																		<tr class="reg-prod-header">
																																			<th class="reg-prod-name reg-prod-firstcol breakWord">
																																				<c:if test="${upc ne null}">
																																					<div class="fl upc">
																																						<span class="bold"><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></span>
																																						<span class="padLeft_5">${upc}</span>
																																					</div>
																																				</c:if>
																																				<c:if test="${(not empty upc) && (not empty color)}">
																																					<div class="fl padLeft_5 padRight_5">|</div>
																																				</c:if>
																																				<c:if test="${color ne null}">
																																					<div class="fl bold"><bbbl:label key='lbl_mng_regitem_color' language="${pageContext.request.locale.language}" /></div>
																																					<div class="fl reg-prod-color padLeft_5 breakWord">${color}</div>
																																				</c:if>
																																				<div class="clear"></div>
																																			</th>
																																			<th class="reg-prod-price textCenter"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></th>
																																			<th class="reg-prod-requested textCenter"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></th>
																																			<th class="reg-prod-purchased textCenter"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></th>
																																			<%-- <th class="reg-prod-qty textCenter"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></th> --%>
																																			<th class="reg-prod-lastcol">&nbsp;</th>
																																		</tr>
																																		<tr class="reg-prod-content">
																																			<td class="reg-prod-name reg-prod-firstcol reg-prod-content-extrapad breakWord">${productName}</td>
																																			<td class="reg-prod-price textCenter reg-prod-content-extrapad">
																																				<dsp:droplet name="PriceDroplet">
																																					<dsp:param name="product" value="${productId}" />
																																					<dsp:param name="sku" value="${skuID}"/>
																																					<dsp:oparam name="output">
																																						<dsp:setvalue param="theListPrice" paramvalue="price"/>
																																						<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
																																						<c:choose>
																																							<c:when test="${not empty profileSalePriceList}">
																																								<dsp:droplet name="PriceDroplet">
																																									<dsp:param name="priceList" bean="Profile.salePriceList"/>
																																									<dsp:oparam name="output">
																																										<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																										<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
																																										<c:if test="${listPrice gt 0.10}">
																																											<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																												<dsp:param name="price" value="${listPrice }"/>
																																											</dsp:include>
																																										</c:if>
																																									</dsp:oparam>
																																									<dsp:oparam name="empty">
																																										<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																											<c:if test="${price gt 0.10}">
																																											<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																												<dsp:param name="price" value="${price }"/>
																																											</dsp:include>
																																											</c:if>
																																									</dsp:oparam>
																																								</dsp:droplet><%-- End price droplet on sale price --%>
																																							</c:when>
																																							<c:otherwise>
																																								<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																									<c:if test="${price gt 0.10}">
																																									<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																										<dsp:param name="price" value="${price }"/>
																																									</dsp:include>
																																									</c:if>
																																							</c:otherwise>
																																						</c:choose>
																																					</dsp:oparam>
																																				</dsp:droplet>
																																			</td>
																																			<td class="reg-prod-requested textCenter reg-prod-content-extrapad">${requestedQuantity}</td>
																																			<td class="reg-prod-purchased textCenter reg-prod-content-extrapad">${purchasedQuantity}</td>
																																			<%-- <td class="reg-prod-qty textCenter"><input readonly="readonly" name="qty" type="text" value="1" class="" /></td> --%>
																																			<td class="reg-prod-lastcol reg-prod-content-extrapad">
																																				<c:if test="${purchasedQuantity >= requestedQuantity}">
																																					<div class="purchasedConfirmation">
																																						<h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
																																					</div>
																																				</c:if>
																																			</td>
																																		</tr>
																																	</table>
																																</td>
																															</tr>
																															<tr>
																																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																	<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
																																	<dsp:param name="elementName" value="attributeVOList"/>
																																	<dsp:oparam name="outputStart">
																																		<td class="reg-prod-footer">
																																	</dsp:oparam>
																																	<dsp:oparam name="output">
																																		<dsp:getvalueof var="pageName" param="key" />
																																		<c:if test="${pageName eq 'RLP'}">
																																			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																			<dsp:param param="attributeVOList" name="array" />
																																			<dsp:param name="elementName" value="attributeVO"/>
																																			<dsp:param name="sortProperties" value="+priority"/>
																																				<dsp:oparam name="output">
																																					<dsp:getvalueof var="size" param="size"/>
																																					<dsp:getvalueof var="count" param="count"/>
																																					<c:set var="attributeDescrip"><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></c:set>
																																						<div class="fl bold">${attributeDescrip}</div><!-- this should come in Bold, check with site Dev -->
																																						<c:if test="${count < size}">
																																							<div class="fl padLeft_5 padRight_5">|</div>
																																						</c:if>
																																				</dsp:oparam>
																																			</dsp:droplet>
																																		</c:if>
																																	</dsp:oparam>
																																	<dsp:oparam name="outputEnd">
																																		<div class="clear"></div>
																																		</td>
																																	</dsp:oparam>
																																</dsp:droplet>
																															</tr>
																														</table>
																													</td>
																												</tr>
																											</table>
																										</td>
																									</tr>
																									<tr>
																										<td class="reg-spacer">&nbsp;</td>
																									</tr>
																							</table>
																						</td>
																					</tr>
																				</c:if>
																			</dsp:oparam>
																		</dsp:droplet>
																	</table>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
														<%-- For Each Droplet for NotInStock other Categories --%>
														<dsp:droplet name="ForEach">
															<dsp:param name="array" param="notInStockCategoryBuckets" />
															
															<dsp:oparam name="output">
																<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
																<dsp:getvalueof var="bucket" param="notInStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
																<c:if test="${bucketName eq othersCat}">
																	<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
																	<dsp:getvalueof var="categoryName" param="key"/>
																	<table width="100%" cellpadding="0" cellspacing="0" border="0">
																		<c:if test="${count ne 0}">
																			<tr>
																				<td class="reg-header"><h3>${categoryName}&nbsp;(${count})</h3></td>
																			</tr>
																		</c:if>
																		<dsp:droplet name="ForEach">
																			<dsp:param name="array" param="notInStockCategoryBuckets.${bucketName}" />
																			<dsp:param name="elementName" value="regItem" />
																			<dsp:oparam name="output">
																				<c:if test="${count ne 0}">
																					<dsp:getvalueof var="skuID" vartype="java.lang.String"	param="regItem.sku" />
																					<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
																					<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
																					<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
																					<c:set var="productName"><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:set>
																					<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.smallImage" />
																					<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
																					<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
																					<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
																					<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
																					<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
																					<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
																						<dsp:param name="skuId" value="${skuID}" />
																						<dsp:oparam name="output">
																							<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
																							<dsp:getvalueof var="inStoreSku" param="inStore" />
																						</dsp:oparam>
																					</dsp:droplet>
																					<tr>
																						<td class="reg-content-wrapper">
																							<table width="100%" cellpadding="0" cellspacing="0" border="0">
																									<tr>
																										<td class="reg-content">
																											<table width="100%" cellpadding="0" cellspacing="0" border="0">
																												<tr>
																													<td class="reg-prod-image">
																													<c:choose>
																														<c:when test="${empty imageURL}">
																															<img class="prodImage noImageFound" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" title="${productName}" height="106" width="78">
																														</c:when>
																														<c:otherwise>
																															<img class="prodImage noImageFound" src="${scene7Path}/${imageURL}" alt="${productName}" title="${productName}" height="106" width="78">
																														</c:otherwise>
																													</c:choose>
																													</td>
																													<td class="reg-prod-info">
																														<table width="100%" cellpadding="0" cellspacing="0" border="0">
																															<tr>
																																<td class="reg-prod-info-inner">
																																	<table width="100%" cellpadding="0" cellspacing="0" border="0">
																																		<tr class="reg-prod-header">
																																			<th class="reg-prod-name reg-prod-firstcol breakWord">
																																				<c:if test="${upc ne null}">
																																					<div class="fl upc">
																																						<span class="bold"><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></span>
																																						<span class="padLeft_5">${upc}</span>
																																					</div>
																																				</c:if>
																																				<c:if test="${(not empty upc) && (not empty color)}">
																																					<div class="fl padLeft_5 padRight_5">|</div>
																																				</c:if>
																																				<c:if test="${color ne null}">
																																					<div class="fl bold"><bbbl:label key='lbl_mng_regitem_color' language="${pageContext.request.locale.language}" /></div>
																																					<div class="fl reg-prod-color padLeft_5 breakWord">${color}</div>
																																				</c:if>
																																				<div class="clear"></div>
																																			</th>
																																			<th class="reg-prod-price textCenter"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></th>
																																			<th class="reg-prod-requested textCenter"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></th>
																																			<th class="reg-prod-purchased textCenter"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></th>
																																			<%-- <th class="reg-prod-qty textCenter"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></th> --%>
																																			<th class="reg-prod-lastcol">&nbsp;</th>
																																		</tr>
																																		<tr class="reg-prod-content">
																																			<td class="reg-prod-name reg-prod-firstcol reg-prod-content-extrapad breakWord">${productName}</td>
																																			<td class="reg-prod-price textCenter reg-prod-content-extrapad">
																																				<dsp:droplet name="PriceDroplet">
																																					<dsp:param name="product" value="${productId}" />
																																					<dsp:param name="sku" value="${skuID}"/>
																																					<dsp:oparam name="output">
																																						<dsp:setvalue param="theListPrice" paramvalue="price"/>
																																						<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
																																						<c:choose>
																																							<c:when test="${not empty profileSalePriceList}">
																																								<dsp:droplet name="PriceDroplet">
																																									<dsp:param name="priceList" bean="Profile.salePriceList"/>
																																									<dsp:oparam name="output">
																																										<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																										<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
																																											<c:if test="${listPrice gt 0.10}">
																																											<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																												<dsp:param name="price" value="${listPrice }"/>
																																											</dsp:include>
																																					                        </c:if>
																																									</dsp:oparam>
																																									<dsp:oparam name="empty">
																																										<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																										<c:if test="${price gt 0.10}">
																																											<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																												<dsp:param name="price" value="${price }"/>
																																											</dsp:include>
																																										</c:if>
																																									</dsp:oparam>
																																								</dsp:droplet><%-- End price droplet on sale price --%>
																																							</c:when>
																																							<c:otherwise>
																																								<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																									<c:if test="${price gt 0.10}">
																																									<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																										<dsp:param name="price" value="${price }"/>
																																									</dsp:include>
																																									</c:if>
																																							</c:otherwise>
																																						</c:choose>
																																					</dsp:oparam>
																																				</dsp:droplet>
																																			</td>
																																			<td class="reg-prod-requested textCenter reg-prod-content-extrapad">${requestedQuantity}</td>
																																			<td class="reg-prod-purchased textCenter reg-prod-content-extrapad">${purchasedQuantity}</td>
																																			<%-- <td class="reg-prod-qty textCenter"><input readonly="readonly" name="qty" type="text" value="1" class="" /></td> --%>
																																			<td class="reg-prod-lastcol reg-prod-content-extrapad">
																																				<c:if test="${purchasedQuantity >= requestedQuantity}">
																																					<div class="purchasedConfirmation">
																																						<h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
																																					</div>
																																				</c:if>
																																			</td>
																																		</tr>
																																	</table>
																																</td>
																															</tr>
																															<tr>
																																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																	<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
																																	<dsp:param name="elementName" value="attributeVOList"/>
																																	<dsp:oparam name="outputStart">
																																		<td class="reg-prod-footer">
																																	</dsp:oparam>
																																	<dsp:oparam name="output">
																																		<dsp:getvalueof var="pageName" param="key" />
																																		<c:if test="${pageName eq 'RLP'}">
																																			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																			<dsp:param param="attributeVOList" name="array" />
																																			<dsp:param name="elementName" value="attributeVO"/>
																																			<dsp:param name="sortProperties" value="+priority"/>
																																				<dsp:oparam name="output">
																																					<dsp:getvalueof var="size" param="size"/>
																																					<dsp:getvalueof var="count" param="count"/>
																																					<c:set var="attributeDescrip"><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></c:set>
																																						<div class="fl bold">${attributeDescrip}</div><!-- this should come in Bold, check with site Dev -->
																																						<c:if test="${count < size}">
																																							<div class="fl padLeft_5 padRight_5">|</div>
																																						</c:if>
																																				</dsp:oparam>
																																			</dsp:droplet>
																																		</c:if>
																																	</dsp:oparam>
																																	<dsp:oparam name="outputEnd">
																																		<div class="clear"></div>
																																		</td>
																																	</dsp:oparam>
																																</dsp:droplet>
																															</tr>
																														</table>
																													</td>
																												</tr>
																											</table>
																										</td>
																									</tr>
																									<tr>
																										<td class="reg-spacer">&nbsp;</td>
																									</tr>
																							</table>
																						</td>
																					</tr>
																				</c:if>
																			</dsp:oparam>
																		</dsp:droplet>
																	</table>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
												</c:if>
												<c:if test="${(not empty sortSeq) && sortSeq ==2}">
													<%-- For Each Droplet for InStock price range list --%>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="priceRangeList" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="priceRange" param="element"/>
															<dsp:droplet name="ForEach">
																<dsp:param name="array" param="inStockCategoryBuckets" />
																
																<dsp:oparam name="output">
																	<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
																	<dsp:getvalueof var="bucket" param="inStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
																	<c:if test="${priceRange eq bucketName}">
																		<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
																		<dsp:getvalueof var="categoryName" param="key"/>
																		<table width="100%" cellpadding="0" cellspacing="0" border="0">
																			<c:if test="${count ne 0}">
																				<tr>
																					<td class="reg-header"><h3>${categoryName}&nbsp;(${count})</h3></td>
																				</tr>
																			</c:if>
																			<dsp:droplet name="ForEach">
																				<dsp:param name="array" param="inStockCategoryBuckets.${bucketName}" />
																				<dsp:param name="elementName" value="regItem" />
																				<dsp:oparam name="output">
																					<c:if test="${count ne 0}">
																						<dsp:getvalueof var="skuID" vartype="java.lang.String"	param="regItem.sku" />
																						<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
																						<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
																						<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
																						<c:set var="productName"><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:set>
																						<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.smallImage" />
																						<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
																						<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
																						<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
																						<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
																						<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
																						<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
																							<dsp:param name="skuId" value="${skuID}" />
																							<dsp:oparam name="output">
																								<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
																								<dsp:getvalueof var="inStoreSku" param="inStore" />
																							</dsp:oparam>
																						</dsp:droplet>
																						<tr>
																							<td class="reg-content-wrapper">
																								<table width="100%" cellpadding="0" cellspacing="0" border="0">
																										<tr>
																											<td class="reg-content">
																												<table width="100%" cellpadding="0" cellspacing="0" border="0">
																													<tr>
																														<td class="reg-prod-image">
																														<c:choose>
																															<c:when test="${empty imageURL}">
																																<img class="prodImage noImageFound" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" title="${productName}" height="106" width="78">
																															</c:when>
																															<c:otherwise>
																																<img class="prodImage noImageFound" src="${scene7Path}/${imageURL}" alt="${productName}" title="${productName}" height="106" width="78">
																															</c:otherwise>
																														</c:choose>
																														</td>
																														<td class="reg-prod-info">
																															<table width="100%" cellpadding="0" cellspacing="0" border="0">
																																<tr>
																																	<td class="reg-prod-info-inner">
																																		<table width="100%" cellpadding="0" cellspacing="0" border="0">
																																			<tr class="reg-prod-header">
																																				<th class="reg-prod-name reg-prod-firstcol breakWord">
																																					<c:if test="${upc ne null}">
																																						<div class="fl upc">
																																							<span class="bold"><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></span>
																																							<span class="padLeft_5">${upc}</span>
																																						</div>
																																					</c:if>
																																					<c:if test="${(not empty upc) && (not empty color)}">
																																						<div class="fl padLeft_5 padRight_5">|</div>
																																					</c:if>
																																					<c:if test="${color ne null}">
																																						<div class="fl bold"><bbbl:label key='lbl_mng_regitem_color' language="${pageContext.request.locale.language}" /></div>
																																						<div class="fl reg-prod-color padLeft_5 breakWord">${color}</div>
																																					</c:if>
																																					<div class="clear"></div>
																																				</th>
																																				<th class="reg-prod-price textCenter"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></th>
																																				<th class="reg-prod-requested textCenter"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></th>
																																				<th class="reg-prod-purchased textCenter"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></th>
																																				<%-- <th class="reg-prod-qty textCenter"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></th> --%>
																																				<th class="reg-prod-lastcol">&nbsp;</th>
																																			</tr>
																																			<tr class="reg-prod-content">
																																				<td class="reg-prod-name reg-prod-firstcol reg-prod-content-extrapad breakWord">${productName}</td>
																																				<td class="reg-prod-price textCenter reg-prod-content-extrapad">
																																					<dsp:droplet name="PriceDroplet">
																																						<dsp:param name="product" value="${productId}" />
																																						<dsp:param name="sku" value="${skuID}"/>
																																						<dsp:oparam name="output">
																																							<dsp:setvalue param="theListPrice" paramvalue="price"/>
																																							<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
																																							<c:choose>
																																								<c:when test="${not empty profileSalePriceList}">
																																									<dsp:droplet name="PriceDroplet">
																																										<dsp:param name="priceList" bean="Profile.salePriceList"/>
																																										<dsp:oparam name="output">
																																											<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																											<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
																																												<c:if test="${listPrice gt 0.10}">
																																												<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																													<dsp:param name="price" value="${listPrice }"/>
																																												</dsp:include>
																																													</c:if>
																																										</dsp:oparam>
																																										<dsp:oparam name="empty">
																																											<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																											<c:if test="${price gt 0.10}">
																																												<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																													<dsp:param name="price" value="${price }"/>
																																												</dsp:include>
																																											</c:if>
																																										</dsp:oparam>
																																									</dsp:droplet><%-- End price droplet on sale price --%>
																																								</c:when>
																																								<c:otherwise>
																																									<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																										<c:if test="${price gt 0.10}">
																																										<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																											<dsp:param name="price" value="${price }"/>
																																										</dsp:include>
																																										</c:if>
																																								</c:otherwise>
																																							</c:choose>
																																						</dsp:oparam>
																																					</dsp:droplet>
																																				</td>
																																				<td class="reg-prod-requested textCenter reg-prod-content-extrapad">${requestedQuantity}</td>
																																				<td class="reg-prod-purchased textCenter reg-prod-content-extrapad">${purchasedQuantity}</td>
																																				<%-- <td class="reg-prod-qty textCenter"><input readonly="readonly" name="qty" type="text" value="1" class="" /></td> --%>
																																				<td class="reg-prod-lastcol reg-prod-content-extrapad">
																																					<c:if test="${purchasedQuantity >= requestedQuantity}">
																																						<div class="purchasedConfirmation">
																																							<h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
																																						</div>
																																					</c:if>
																																				</td>
																																			</tr>
																																		</table>
																																	</td>
																																</tr>
																																<tr>
																																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																		<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
																																		<dsp:param name="elementName" value="attributeVOList"/>
																																		<dsp:oparam name="outputStart">
																																			<td class="reg-prod-footer">
																																		</dsp:oparam>
																																		<dsp:oparam name="output">
																																			<dsp:getvalueof var="pageName" param="key" />
																																			<c:if test="${pageName eq 'RLP'}">
																																				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																				<dsp:param param="attributeVOList" name="array" />
																																				<dsp:param name="elementName" value="attributeVO"/>
																																				<dsp:param name="sortProperties" value="+priority"/>
																																					<dsp:oparam name="output">
																																						<dsp:getvalueof var="size" param="size"/>
																																						<dsp:getvalueof var="count" param="count"/>
																																						<c:set var="attributeDescrip"><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></c:set>
																																							<div class="fl bold">${attributeDescrip}</div><!-- this should come in Bold, check with site Dev -->
																																							<c:if test="${count < size}">
																																								<div class="fl padLeft_5 padRight_5">|</div>
																																							</c:if>
																																					</dsp:oparam>
																																				</dsp:droplet>
																																			</c:if>
																																		</dsp:oparam>
																																		<dsp:oparam name="outputEnd">
																																			<div class="clear"></div>
																																			</td>
																																		</dsp:oparam>
																																	</dsp:droplet>
																																</tr>
																															</table>
																														</td>
																													</tr>
																												</table>
																											</td>
																										</tr>
																										<tr>
																											<td class="reg-spacer">&nbsp;</td>
																										</tr>
																								</table>
																							</td>
																						</tr>
																					</c:if>
																				</dsp:oparam>
																			</dsp:droplet>
																		</table>
																	</c:if>
																</dsp:oparam>
															</dsp:droplet>
														</dsp:oparam>
													</dsp:droplet>
													<dsp:getvalueof var="emptyOutOfStockListFlag" param="emptyOutOfStockListFlag"/>
													<c:if test="${emptyOutOfStockListFlag ne 'true' }">
														<p class="reg-msg-oos padBottom_10 padTop_10"><bbbt:textArea key="txt_mng_regitem_items_unavail" language="${pageContext.request.locale.language}" /></p>
														<%-- For Each Droplet for NotInStock price range list --%>
														<dsp:droplet name="ForEach">
															<dsp:param name="array" param="priceRangeList" />
															<dsp:oparam name="output">
																<dsp:getvalueof var="priceRange" param="element"/>
																<dsp:droplet name="ForEach">
																	<dsp:param name="array" param="notInStockCategoryBuckets" />
																	
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
																		<dsp:getvalueof var="bucket" param="notInStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
																		<c:if test="${priceRange eq bucketName}">
																			<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
																			<dsp:getvalueof var="categoryName" param="key"/>
																			<table width="100%" cellpadding="0" cellspacing="0" border="0">
																				<c:if test="${count ne 0}">
																					<tr>
																						<td class="reg-header"><h3>${categoryName}&nbsp;(${count})</h3></td>
																					</tr>
																				</c:if>
																				<dsp:droplet name="ForEach">
																					<dsp:param name="array" param="notInStockCategoryBuckets.${bucketName}" />
																					<dsp:param name="elementName" value="regItem" />
																					<dsp:oparam name="output">
																						<c:if test="${count ne 0}">
																							<dsp:getvalueof var="skuID" vartype="java.lang.String"	param="regItem.sku" />
																							<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
																							<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
																							<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
																							<c:set var="productName"><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:set>
																							<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.smallImage" />
																							<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
																							<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
																							<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
																							<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
																							<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
																							<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
																								<dsp:param name="skuId" value="${skuID}" />
																								<dsp:oparam name="output">
																									<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
																									<dsp:getvalueof var="inStoreSku" param="inStore" />
																								</dsp:oparam>
																							</dsp:droplet>
																							<tr>
																								<td class="reg-content-wrapper">
																									<table width="100%" cellpadding="0" cellspacing="0" border="0">
																											<tr>
																												<td class="reg-content">
																													<table width="100%" cellpadding="0" cellspacing="0" border="0">
																														<tr>
																															<td class="reg-prod-image">
																															<c:choose>
																																<c:when test="${empty imageURL}">
																																	<img class="prodImage noImageFound" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" title="${productName}" height="106" width="78">
																																</c:when>
																																<c:otherwise>
																																	<img class="prodImage noImageFound" src="${scene7Path}/${imageURL}" alt="${productName}" title="${productName}" height="106" width="78">
																																</c:otherwise>
																															</c:choose>
																															</td>
																															<td class="reg-prod-info">
																																<table width="100%" cellpadding="0" cellspacing="0" border="0">
																																	<tr>
																																		<td class="reg-prod-info-inner">
																																			<table width="100%" cellpadding="0" cellspacing="0" border="0">
																																				<tr class="reg-prod-header">
																																					<th class="reg-prod-name reg-prod-firstcol breakWord">
																																						<c:if test="${upc ne null}">
																																							<div class="fl upc">
																																								<span class="bold"><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></span>
																																								<span class="padLeft_5">${upc}</span>
																																							</div>
																																						</c:if>
																																						<c:if test="${(not empty upc) && (not empty color)}">
																																							<div class="fl padLeft_5 padRight_5">|</div>
																																						</c:if>
																																						<c:if test="${color ne null}">
																																							<div class="fl bold"><bbbl:label key='lbl_mng_regitem_color' language="${pageContext.request.locale.language}" /></div>
																																							<div class="fl reg-prod-color padLeft_5 breakWord">${color}</div>
																																						</c:if>
																																						<div class="clear"></div>
																																					</th>
																																					<th class="reg-prod-price textCenter"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></th>
																																					<th class="reg-prod-requested textCenter"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></th>
																																					<th class="reg-prod-purchased textCenter"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></th>
																																					<%-- <th class="reg-prod-qty textCenter"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></th> --%>
																																					<th class="reg-prod-lastcol">&nbsp;</th>
																																				</tr>
																																				<tr class="reg-prod-content">
																																					<td class="reg-prod-name reg-prod-firstcol reg-prod-content-extrapad breakWord">${productName}</td>
																																					<td class="reg-prod-price textCenter reg-prod-content-extrapad">
																																						<dsp:droplet name="PriceDroplet">
																																							<dsp:param name="product" value="${productId}" />
																																							<dsp:param name="sku" value="${skuID}"/>
																																							<dsp:oparam name="output">
																																								<dsp:setvalue param="theListPrice" paramvalue="price"/>
																																								<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
																																								<c:choose>
																																									<c:when test="${not empty profileSalePriceList}">
																																										<dsp:droplet name="PriceDroplet">
																																											<dsp:param name="priceList" bean="Profile.salePriceList"/>
																																											<dsp:oparam name="output">
																																												<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																												<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
																																												<c:if test="${listPrice gt 0.10}">
																																													<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																														<dsp:param name="price" value="${listPrice }"/>
																																													</dsp:include>
																																													</c:if>
																																											</dsp:oparam>
																																											<dsp:oparam name="empty">
																																												<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																													<c:if test="${price gt 0.10}">
																																													<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																														<dsp:param name="price" value="${price }"/>
																																													</dsp:include>
																																													</c:if>
																																											</dsp:oparam>
																																										</dsp:droplet><%-- End price droplet on sale price --%>
																																									</c:when>
																																									<c:otherwise>
																																										<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																																											<c:if test="${price gt 0.10}">
																																											<dsp:include page="/global/gadgets/formattedPrice.jsp">
																																												<dsp:param name="price" value="${price }"/>
																																											</dsp:include>
																																											</c:if>
																																									</c:otherwise>
																																								</c:choose>
																																							</dsp:oparam>
																																						</dsp:droplet>
																																					</td>
																																					<td class="reg-prod-requested textCenter reg-prod-content-extrapad">${requestedQuantity}</td>
																																					<td class="reg-prod-purchased textCenter reg-prod-content-extrapad">${purchasedQuantity}</td>
																																					<%-- <td class="reg-prod-qty textCenter"><input readonly="readonly" name="qty" type="text" value="1" class="" /></td> --%>
																																					<td class="reg-prod-lastcol reg-prod-content-extrapad">
																																						<c:if test="${purchasedQuantity >= requestedQuantity}">
																																							<div class="purchasedConfirmation">
																																								<h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
																																							</div>
																																						</c:if>
																																					</td>
																																				</tr>
																																			</table>
																																		</td>
																																	</tr>
																																	<tr>
																																		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																			<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
																																			<dsp:param name="elementName" value="attributeVOList"/>
																																			<dsp:oparam name="outputStart">
																																				<td class="reg-prod-footer">
																																			</dsp:oparam>
																																			<dsp:oparam name="output">
																																				<dsp:getvalueof var="pageName" param="key" />
																																				<c:if test="${pageName eq 'RLP'}">
																																					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																																					<dsp:param param="attributeVOList" name="array" />
																																					<dsp:param name="elementName" value="attributeVO"/>
																																					<dsp:param name="sortProperties" value="+priority"/>
																																						<dsp:oparam name="output">
																																							<dsp:getvalueof var="size" param="size"/>
																																							<dsp:getvalueof var="count" param="count"/>
																																							<c:set var="attributeDescrip"><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></c:set>
																																								<div class="fl bold">${attributeDescrip}</div><!-- this should come in Bold, check with site Dev -->
																																								<c:if test="${count < size}">
																																									<div class="fl padLeft_5 padRight_5">|</div>
																																								</c:if>
																																						</dsp:oparam>
																																					</dsp:droplet>
																																				</c:if>
																																			</dsp:oparam>
																																			<dsp:oparam name="outputEnd">
																																				<div class="clear"></div>
																																				</td>
																																			</dsp:oparam>
																																		</dsp:droplet>
																																	</tr>
																																</table>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											<tr>
																												<td class="reg-spacer">&nbsp;</td>
																											</tr>
																									</table>
																								</td>
																							</tr>
																						</c:if>
																					</dsp:oparam>
																				</dsp:droplet>
																			</table>
																		</c:if>
																	</dsp:oparam>
																</dsp:droplet>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
												</c:if>
											</c:otherwise>
										</c:choose>
										<div class="padTop_10 padBottom_10 clearfix">
											<div class="divIMG fl">
												<bbbt:textArea key="txt_regitem_store_image" language="${pageContext.request.locale.language}" />
											</div>
											<div class="divTXT fl fontSize12All">
												<bbbt:textArea key="txt_mng_regitem_chkfirst" language="${pageContext.request.locale.language}" />&nbsp;
												<c:choose>
													<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
														<bbbt:textArea key="txt_mng_regitem_callbbby" language="${pageContext.request.locale.language}" />
													</c:when>
													<c:otherwise>
														<bbbt:textArea key="txt_mng_regitem_callbbb" language="${pageContext.request.locale.language}" />
													</c:otherwise>
												</c:choose>
												<div class="padTop_10 padBottom_10 fontSize12"><bbbt:textArea key="txt_mng_regitem_ask_updcopy" language="${pageContext.request.locale.language}" /></div>
											</div>
											<div class="clear"></div>
										</div>
									</dsp:oparam>
									<dsp:oparam name="error">
										<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
										<div class="padTop_10 padBottom_10">
											<p class="error fontSize12"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></p>
										</div>
									</dsp:oparam>
								</dsp:droplet>
							</c:when>
							<c:otherwise>
								<div class="padTop_10 padBottom_10">
									<h3><span class="error">Access Denied!!!</span></h3>
								</div>
							</c:otherwise>
						</c:choose>
					</dsp:oparam>
					<dsp:oparam name="error">
						<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
						<div class="padTop_10 padBottom_10">
							<p class="error fontSize12"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></p>
						</div>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
		</div>

		<div id="footer" class="textCenter cb">
			<dsp:getvalueof var="currentYear" bean="CurrentDate.year"/>
			<jsp:useBean id="placeHolderMapCopyRight" class="java.util.HashMap" scope="request"/>
			<c:set target="${placeHolderMapCopyRight}" property="currentYear" value="${currentYear}"/>
			<c:choose>
				<c:when test="${currentSiteId eq TBS_BedBathUSSite }">
					<bbbt:textArea key="txt_footer_bedbathbeyond_all_rights_reserved" placeHolderMap="${placeHolderMapCopyRight}" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
					<bbbt:textArea key="txt_footer_buybuybaby_all_rights_reserved" placeHolderMap="${placeHolderMapCopyRight}" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
					<bbbt:textArea key="txt_footer_bedbathbeyondca_all_rights_reserved" placeHolderMap="${placeHolderMapCopyRight}" language ="${pageContext.request.locale.language}"/>
				</c:when>
			</c:choose>
		</div>
	</div>
	<dsp:getvalueof var="errorList" bean="SystemErrorInfo.errorList"/>
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
    	<dsp:param bean="SystemErrorInfo.errorList" name="array" />
		<dsp:param name="elementName" value="ErrorInfoVO" />
		<dsp:oparam name="outputStart"><div id="error" class="hidden"><ul></dsp:oparam>
    	<dsp:oparam name="output">
			<li id="tl_atg_err_code"><dsp:valueof param="ErrorInfoVO.errorCode"/></li>
			<li id="tl_atg_err_value"><dsp:valueof param="ErrorInfoVO.errorDescription"/></li>
    	</dsp:oparam>
		<dsp:oparam name="outputEnd"></ul></div></dsp:oparam>
    </dsp:droplet>
</body>
</html>
</dsp:page>