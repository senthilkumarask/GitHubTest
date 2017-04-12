<%-- SECTION/PAGE-TYPE SPECIFIC CSS --%>
<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
    <dsp:getvalueof var="section" param="section"/>
    <dsp:getvalueof var="pageWrapper" param="pageWrapper"/>

    <%--    <dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
		<dsp:oparam name="output">
		<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM"/>
				</dsp:oparam>	
	</dsp:droplet>--%>

    
    <dsp:getvalueof var="buildRevisionNumber" param="buildRevisionNumber"/>
	<c:if test="${minifiedCSSFlag == 'true'}"><c:set var="min" value=".min" /></c:if>

    <c:if test="${section == 'browse' || section == 'compare' || section == 'search'}">
		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/browse${min}.css?v=${buildRevisionNumber}" />
        <c:if test="${fn:indexOf(pageWrapper, 'productDetails') > -1 || fn:indexOf(pageWrapper, 'compareProducts') > -1 || fn:indexOf(pageWrapper, 'searchResults') > -1 || fn:indexOf(pageWrapper, 'subCategory') > -1 || fn:indexOf(pageWrapper, 'personalStorePage') > -1}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/product_details${min}.css?v=${buildRevisionNumber}" />
            <!-- <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/contact_store${min}.css?v=${buildRevisionNumber}" /> -->
        </c:if>
        <c:if test="${fn:indexOf(pageWrapper, 'bridalBook') > -1 || fn:indexOf(pageWrapper, 'babyBook') > -1}">
            <style type="text/css">
                body {
                    min-width: 100%;
                }
            </style>
        </c:if>
    </c:if>

    <%-- PORCH css: Porch css is required on PDP, checkout-multishipping, order-preview, order summary, cart. account-orders 
        ultimately want to combine the porch css into minified files with juicer
    --%>
    <c:if test="${section == 'browse' || section == 'checkout' || section == 'cartDetail' || section == 'accounts' || section == 'singleCheckout' }">
        
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/porch.css?v=${buildRevisionNumber}" /> 
        
    </c:if>


    <%-- <c:if test="${section == 'cms' || section == 'selfService'}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/cms.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/cms.css?v=${buildRevisionNumber}" />
    </c:if> 
    <c:if test="${section == 'search'}">
      <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/search.css?v=${buildRevisionNumber}" /> 
    </c:if> --%>
    <c:if test="${section == 'cms'}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/cms${min}.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/cms${min}.css?v=${buildRevisionNumber}" />
    </c:if>
    <c:if test="${section == 'accounts'}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/account${min}.css?v=${buildRevisionNumber}" />
        <%-- <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/qas.css?v=${buildRevisionNumber}"/>
        </c:if> --%>
        <!-- <c:if test="${fn:indexOf(pageWrapper, 'overviewPage') > -1 || fn:indexOf(pageWrapper, 'favoriteStore') > -1}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/contact_store${min}.css?v=${buildRevisionNumber}" />
        </c:if> -->
    </c:if>
    <c:if test="${section == 'cartDetail'}">
		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/cart${min}.css?v=${buildRevisionNumber}" />
        <!-- <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/contact_store${min}.css?v=${buildRevisionNumber}" /> -->
    </c:if>
    <c:if test="${section == 'checkout'}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/checkout${min}.css?v=${buildRevisionNumber}" />
        <%-- <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/qas.css?v=${buildRevisionNumber}"/>
        </c:if> --%>
        <!-- <c:if test="${fn:indexOf(pageWrapper, 'chekoutReview') > -1 || fn:indexOf(pageWrapper, 'shippingWrapper') > -1}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/contact_store${min}.css?v=${buildRevisionNumber}" />
        </c:if> -->
    </c:if>
    <c:if test="${section == 'singleCheckout'}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/singleCheckout${min}.css?v=${buildRevisionNumber}" />
        <%-- <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/qas.css?v=${buildRevisionNumber}"/>
        </c:if> 
        <c:if test="${fn:indexOf(pageWrapper, 'chekoutReview') > -1 || fn:indexOf(pageWrapper, 'shippingWrapper') > -1}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/contact_store${min}.css?v=${buildRevisionNumber}" />
        </c:if>
        --%>
    </c:if>

    
    <c:if test="${section == 'selfService'}">
        <c:if test="${fn:indexOf(pageWrapper, 'findStore') > -1}">
            <!-- <link href="${cssPath}/_assets/global/css/contact_store${min}.css?v=${buildRevisionNumber}" rel="stylesheet" type="text/css" /> -->
            <%-- flash store locator --%>
            <%-- <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/flash_assets/history/history.css?v=${buildRevisionNumber}" /> --%>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/AC_OETags.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/flash_assets/history/history.js?v=${buildRevisionNumber}"></script>
        </c:if>
    </c:if>
    <!-- <c:if test="${section == 'college'}">
        <c:if test="${fn:indexOf(pageWrapper, 'findACollege') > -1}">
            <link href="${cssPath}/_assets/global/css/contact_store${min}.css?v=${buildRevisionNumber}" rel="stylesheet" type="text/css" />
        </c:if>
    </c:if> -->
    <%-- <c:if test="${section == 'createRegistry' || section == 'registry' || fn:indexOf(pageWrapper, 'bridalLanding') > -1 || fn:indexOf(pageWrapper, 'babyRegistry') > -1 || fn:indexOf(pageWrapper, 'registry') > -1 || fn:indexOf(pageWrapper, 'manageRegistry') > -1 || fn:indexOf(pageWrapper, 'registryConfirm') > -1}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/cms.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/cms.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/registry.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/registry.css?v=${buildRevisionNumber}" />
        <link href="${cssPath}/_assets/global/css/contact_store.css?v=${buildRevisionNumber}" rel="stylesheet" type="text/css" />
        <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/qas.css?v=${buildRevisionNumber}"/>
        </c:if>
    </c:if> --%>
	
	<c:if test="${section == 'registry'}">
	<c:if test="${fn:indexOf(pageWrapper, 'updateFormPopup') > -1}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/updateRegistry.css?v=${buildRevisionNumber}" />
    </c:if>
	</c:if>
    
	<c:if test="${section == 'createRegistry'}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/registry.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/registry.css?v=${buildRevisionNumber}" />
        <!-- <link href="${cssPath}/_assets/global/css/contact_store${min}.css?v=${buildRevisionNumber}" rel="stylesheet" type="text/css" /> -->
        <%-- <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/qas.css?v=${buildRevisionNumber}"/>
        </c:if> --%>
    </c:if>
       <c:if test="${section == 'createSimpleRegistry'}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/simpleRegistry.css?v=${buildRevisionNumber}" />
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/registry.css?v=${buildRevisionNumber}" />
       <!--  <link href="${cssPath}/_assets/global/css/contact_store${min}.css?v=${buildRevisionNumber}" rel="stylesheet" type="text/css" /> -->
        <%-- <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
            <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/qas.css?v=${buildRevisionNumber}"/>
        </c:if> --%>
    </c:if>
    <c:if test="${fn:indexOf(pageWrapper, 'printerFriendly') > -1 || (not empty param.printerFriendly && param.printerFriendly eq 'true')}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/print_page.css?v=${buildRevisionNumber}" media="all" />
    </c:if>
      <!-- <link href="${cssPath}/_assets/global/css/jquery.webui-popover.css?v=${buildRevisionNumber}" rel="stylesheet" type="text/css" /> -->

    <c:if test="${fn:indexOf(pageWrapper, 'useAdobeActiveContent') > -1 && bExternalJSCSS && bPlugins && bAdobeActiveContent}">
        <script type="text/javascript">
            var AC_FL_RunContent = 0,
                DetectFlashVer = 0;
        </script>
        <%-- must NOT be loaded async as it will break the baby-book, bridal-book and circular pages, because this lib uses document.write[deprecated] and it does not work on async loads. --%>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/AC_RunActiveContent.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/windowControl.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript">
            // overriding global values for flash content
            var requiredMajorVersion = 9,
                requiredMinorVersion = 0,
                requiredRevision = 115;
        </script>
    </c:if>
	<c:set var="banner_css_url">
		<bbbc:config key="banner_css_${appid}" configName="BBBLiveClicker" />
	</c:set>
    <c:if test="${fn:indexOf(pageWrapper, 'useLiveClicker') > -1 && bExternalJSCSS && bPlugins && LiveClickerOn}">
        <link rel="stylesheet" type="text/css" href="${banner_css_url}" />
    </c:if>
</dsp:page>