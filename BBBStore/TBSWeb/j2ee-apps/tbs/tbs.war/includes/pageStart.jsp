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

<dsp:page>

    <%-- Imports --%>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
    <dsp:importbean bean="/com/bbb/cms/droplet/GuidesLongDescDroplet"/>

    <%-- Page Variables --%>
    <dsp:getvalueof var="section" param="section"/>
    <dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
    <dsp:getvalueof var="themeFolder" param="themeFolder"/>
    <dsp:getvalueof var="pageName" param="pageName"/>
    <dsp:getvalueof var="PageType" param="PageType"/>
    <dsp:getvalueof var="pageFBType" value="website"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="language" bean="/OriginatingRequest.requestLocale.locale.language"/>
    <dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
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

    <dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
		<dsp:oparam name="output">
		<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM"/>
				</dsp:oparam>	
	</dsp:droplet>

	<c:set var="buildRevisionNumber" value= "${buildRevisionNumber}" scope="request"/>
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






    <%-- KP COMMENT START: this is for development ONLY. We're gutting a lot of JS files, so we don't
        want the minified versions of files...too hard to read.
    <c:set var="minifiedJSFlag" value="false" />
    <c:set var="minifiedCSSFlag" value="false" /> --%>
    <%-- KP COMMENT END --%>







    <dsp:getvalueof id="cssCurrentSiteId" bean="/atg/multisite/Site.id" />
    <c:set var="cssTBS_BedBathUSSiteCode"><bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="cssTBS_BuyBuyBabySiteCode"><bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="cssTBS_BedBathCanadaSiteCode"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="scene7ClientID"><bbbc:config key="s7ClientID" configName="ThirdPartyURLs" /></c:set>
    <c:choose>
        <c:when test="${cssCurrentSiteId eq cssTBS_BuyBuyBabySiteCode}">
            <c:set var="cssCurrentSiteCode" value="buyBuyBaby" />
        </c:when>
        <c:when test="${cssCurrentSiteId eq cssTBS_BedBathCanadaSiteCode}">
            <c:set var="cssCurrentSiteCode" value="bedBathCA" />
        </c:when>
        <c:otherwise>
            <c:set var="cssCurrentSiteCode" value="bedBathUS" />
        </c:otherwise>
    </c:choose>
    <dsp:getvalueof var="catLevelCount" bean="/atg/multisite/Site.siteL3Count" />
    <c:set var="vbvStyleTag" value="" />
    <c:set var="vbvCFrameFix" value="" />
    <c:if test="${fn:indexOf(pageWrapper, 'cclaunchmodal') > -1}">
        <c:set var="vbvStyleTag" value="style='background: url(\"/_assets/global/images/widgets/small_loader.gif\") no-repeat scroll center center #fff !important;overflow:hidden !important;width:380px;height:380px;min-width:380px;min-height:380px;'" />
    </c:if>
    <c:if test="${fn:indexOf(pageWrapper, 'cCFrame') > -1}">
        <c:set var="vbvCFrameFix" value="overlayTrans" />
    </c:if>

    <!doctype html>
    <!--[if IE 9]>    <html class="${cssCurrentSiteCode} ie no-js ie9 newie" ${vbvStyleTag} lang="en" xmlns:fb="${REQURLPROTOCOL}://graph.facebook.com/schema/og" xml:lang="en"> <![endif]-->
    <!--[if gt IE 9]> <html class="${cssCurrentSiteCode} ie no-js newie" ${vbvStyleTag} lang="en" xmlns:fb="${REQURLPROTOCOL}://graph.facebook.com/schema/og" xml:lang="en">     <![endif]-->
    <!--[if !IE]><!-->
    <html class="${cssCurrentSiteCode} no-js" ${vbvStyleTag} lang="en" xmlns:fb="${REQURLPROTOCOL}://graph.facebook.com/schema/og" xml:lang="en">   <!--<![endif]-->
    <head>
        <meta charset="UTF-8" />
        <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
        <meta http-equiv="imagetoolbar" content="no">

        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <%-- KP COMMENT START: Disabling this to trim down css before build --%>
        <%--
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/mobileRedirect.js?v=${buildRevisionNumber}"></script>
        --%>
        <%-- KP COMMENT END --%>

       <%-- KP COMMENT START: javascript constants --%>
        <%-- Replaced jsp call with inline js --%>
       

        <c:choose>
            <c:when test="${minifiedCSSFlag == 'true'}">
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/bbb_combined.css?v=${buildRevisionNumber}" />
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbregistry/css/theme.min.css?v=${buildRevisionNumber}" />
            </c:when>
            <c:otherwise>
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/foundation-daterangepicker.css?v=${buildRevisionNumber}" />
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/foundation-datepicker.css?v=${buildRevisionNumber}" />
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/entypo.css?v=${buildRevisionNumber}" />
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/global.css?ver=${buildRevisionNumber}" />
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/font-awesome.css?v=${buildRevisionNumber}" />
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbregistry/css/theme.css?v=${buildRevisionNumber}" />
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/custom_scene7.css?v=${buildRevisionNumber}" />
            </c:otherwise>
        </c:choose>

        <script type="text/javascript">
            var CONSTANTS = {

            contextPath : '/tbs'
            },
            BBB = BBB || {};
            BBB.loginStatus = {};
        </script>
        <%-- KP COMMENT END --%>

        <c:if test="${fn:indexOf(pageWrapper, 'chekoutConfirm') > -1}">
            <script type="text/javascript">
                if (window.self !== window.top) {
                    window.top.location = window.self.location;
                }
            </script>
        </c:if>
        <c:if test="${fn:indexOf(pageWrapper, 'cCAuthenticate') > -1}">
            <script type="text/javascript">
                var selfLoc = window.self.location.href;
                if (window.self !== window.top) {
                    window.top.location = (selfLoc.indexOf('?') > -1) ? (selfLoc + '&pr=true') : (selfLoc + '?pr=true');
                }
            </script>
        </c:if>

        <dsp:include page="/_includes/third_party_on_of_tags.jsp"/>

        <c:if test="${pageContext.request.secure}">
            <meta http-equiv="pragma" content="no-cache">
            <meta http-equiv="cache-control" content="no-cache">
        </c:if>

        <dsp:getvalueof param="amigoMeta" var="amigoMeta"/>
        <c:out value="${amigoMeta}" escapeXml="false"></c:out>

        <%--mPulse file --%>
        <dsp:include page="/_includes/mPulse.jsp"> 
        	<dsp:param name="PageType" param="PageType"/>
        </dsp:include>

        <%-- first get babyCA session/url param values, may be overridden by active registry --%>
        
        <dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA"/>
        <c:if test="${sessionBabyCA eq 'true' && currentSiteId eq 'TBS_BedBathCanada'}">
            <c:set var="babyCAMode" scope="request" value="true"/>
        </c:if>
        <%-- can set url param to manually set babyCA session --%>
        <dsp:getvalueof var="babyCA" param="babyCA"/>
        <c:if test="${babyCA eq 'true' && currentSiteId eq 'TBS_BedBathCanada'}">
            <c:set var="babyCAMode" scope="request" value="true"/>
            <dsp:setvalue bean="SessionBean.babyCA" value="true"/>
        </c:if>
        <c:if test="${babyCA eq 'false' && currentSiteId eq 'TBS_BedBathCanada'}">
            <dsp:setvalue bean="SessionBean.babyCA" value="false"/>
            <c:set var="babyCAMode" scope="request" value="false"/>
        </c:if>

        <%-- get active registry in session - registry type can override SessionBean.babyCA value--%>
        <dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="sessionRegistry"/>
        <c:if test="${not empty sessionRegistry}">
            <c:set var="regType" value="${sessionRegistry.registryType.registryTypeDesc}" />
            <%-- if viewing baby registry on Bedbath Canada show baby style --%>
            <c:if test="${(currentSiteId eq 'TBS_BedBathCanada') and (regType eq 'Baby')}" >
                <c:set var="babyCAMode" scope="request" value="true"/>
            </c:if>
            <c:if test="${(currentSiteId eq 'TBS_BedBathCanada') and (regType ne 'Baby')}" >
                <c:set var="babyCAMode" scope="request" value="false"/>
            </c:if>
        </c:if>

        <%-- if viewing non-baby registry on Bedbath Canada, make sure to NOT load babyca style (due to registry session error)
        <dsp:getvalueof var="regEventType" param="eventType"/>
        <c:if test="${not empty regEventType && currentSiteId eq 'TBS_BedBathCanada'}">
            <c:if test="${regEventType ne 'Baby'}">
                <c:set var="babyCAMode" scope="request" value="false"/>
            </c:if>
            <c:if test="${regEventType eq 'Baby'}">
                <c:set var="babyCAMode" scope="request" value="true"/>
            </c:if>
        </c:if>
        --%>
        <c:set var="currentSiteContext" value="${currentSiteId}" scope="request" />
        <c:if test="${babyCAMode eq 'true' && currentSiteId eq 'TBS_BedBathCanada'}">
            <c:set var="BabyCaClass" value=" bca" scope="request" />
            <c:set var="currentSiteContext" value="babyCanada" scope="request" />
        </c:if>

        <%-- SITE SPECIFIC CSS --%>
        <%-- KP COMMENT START: Disabling this to trim down css before build --%>
                    
            
        <%-- KP COMMENT END --%>

        <%-- KP COMMENT START: Disabling this to trim down css before build --%>
            <%-- <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/jquery-ui.css?v=${buildRevisionNumber}" /> --%>
        <%-- KP COMMENT END --%>

        <%-- KP COMMENT START: Disabling this to trim down css before build --%>
            <%-- <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/${themeFolder}/css/forms.css?v=${buildRevisionNumber}" /> --%>
        <%-- KP COMMENT END --%>

        <%-- in the case of baby canada, we will load both the beyond theme as well as the babyCA theme
            the babyCA theme will override any specific styles required  --%>
        <%-- KP COMMENT START: Disabling this to trim down css before build --%>
            
            <c:if test="${babyCAMode == 'true' && currentSiteId eq 'TBS_BedBathCanada'}">
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbbabyca/css/theme.css?v=${buildRevisionNumber}" />
            </c:if>
            
        <%-- KP COMMENT END --%>

        <%-- KP COMMENT START: Disabling this to trim down css before build --%>
            <%-- Changed it for iPad optimization --%>
            <%-- <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/touchcarousel.css?v=${buildRevisionNumber}" /> --%>
        <%-- KP COMMENT END --%>

        <%-- KP COMMENT START: Disabling this to trim down css before build --%>
            
            
        <%-- KP COMMENT END --%>

        <%-- CSS for new store locator maps --%>
            <%-- <c:if test="${fn:indexOf(pageWrapper, 'useStoreLocator') > -1 &&  fn:indexOf(pageWrapper, 'noStoreLocatorCss') == -1 && bExternalJSCSS && bPlugins }"> --%>

            <%-- COMMENTED AS IT IS MOVED TO global.css
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/store_locator.css?v=${buildRevisionNumber}" />
            --%>
           <%--  </c:if> --%>
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
                <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
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
                    if (window.console) {
                        if (window.console[type]) {
                            <c:if test="${minifiedJSFlag == 'true'}">
                                window.console[type](msg);
                            </c:if>
                        }
                    }
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
            
        </script>

       <%-- <c:import url ="/_includes/end_script.jsp" /> --%>
<script type="text/javascript" src="/tbs/_includes/end_script.jsp">

</script>
<bbbt:textArea key="txt_l1nodes_styling" language ="${pageContext.request.locale.language}"/>

        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/libraries/jquery.js"></script>

        <%-- Robots meta tag --%>
        <dsp:getvalueof var="index" param="index"/>
        <dsp:getvalueof var="follow" param="follow"/>

        <c:set var="indexValue" value="${(index eq 'false') ? 'noindex' : 'index'}"/>
        <c:set var="followValue" value="${(follow eq 'false') ? 'nofollow' : 'follow'}"/>

        <meta name="robots" content="${indexValue},${followValue}"/>

        <%-- R2.2 : Created common JSP for pagination link for SEO of category, search and Brand pages : Start--%>
        <dsp:include page="/global/gadgets/seoPaginationURL.jsp">
            <dsp:param name="PageType" param="PageType"/>
        </dsp:include>
        <%-- R2.2 : Created common JSP for pagination link for SEO of category, search and Brand pages : End--%>

        <%-- Include content from SEO tag renderer --%>
        <dsp:getvalueof var="SEOTagRendererContent" param="SEOTagRendererContent"/>
        <c:choose>
            <c:when test="${not empty SEOTagRendererContent }">
                <c:out value="${SEOTagRendererContent}" escapeXml="false"/>
            </c:when>
            <c:otherwise>
                <%-- Use default SEO tag renderer
                <dsp:include page="/global/gadgets/metaDetails.jsp">
                <dsp:param name="titleString" param="titleString"/>
                <dsp:param name="contentKey" param="contentKey"/>
                <dsp:param name="categoryId" param="categoryId"/>
                <dsp:param name="productId" param="productId"/>
                <dsp:param name="guideId" param="guideId" />
                <dsp:param name="pageName" param="pageName" />
                <dsp:param name="brandId" param="brandId" />
                </dsp:include> --%>
            </c:otherwise>
        </c:choose>

        <%-- Renders "canonical" tag
        <dsp:include page="/global/gadgets/canonicalTag.jsp">
            <dsp:param name="categoryId" param="categoryId"/>
            <dsp:param name="productId" param="productId"/>
            <dsp:param name="guideId" param="guideId" />
            <dsp:param name="pageName" param="PageType"/>
            <dsp:param name="currentSiteId" value="${currentSiteId}"/>
        </dsp:include> --%>

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
                <c:when test="${not empty regId && not empty regType}">
                    <dsp:getvalueof var="fbPageTitle" value="${regType} Registry"/>
                    <dsp:getvalueof var="fbDesc" value="${regType} Registry Number: ${regId}"/>
                    <dsp:getvalueof var="fbUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}${contextPath}/giftregistry/view_registry_guest.jsp?registryId=${regId}&eventType=${regType}" />
                    <dsp:getvalueof var="pageFBType" value="registry"/>
                </c:when>
                <c:when test="${not empty guideId}">
                    <dsp:droplet name="GuidesLongDescDroplet">
                        <dsp:param name="guideId" value="${guideId}" />
                        <dsp:oparam name="empty">
                            <dsp:include page="../404.jsp" flush="true"/>
                        </dsp:oparam>
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
                    <c:when test="${cssCurrentSiteId eq cssTBS_BuyBuyBabySiteCode}">
                        <dsp:getvalueof var="fbImage"
                            value="${pageContext.request.scheme}://${pageContext.request.serverName}/_assets/global/images/logo/logo_bb_circle.png" />
                    </c:when>
                    <c:when test="${cssCurrentSiteId eq cssTBS_BedBathCanadaSiteCode}">
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
            <meta property="og:url" content="<c:out value="${fbUrl}"/>"/>
            <meta property="fb:app_id" content="${fbConfigMapHEAD[currentSiteId]}" />
            <meta property="og:type" content="${pageFBType}"/>
        </c:if>

        <%--
        NOTE: it is a must that four icon files are provided..
            two "favicon.ico & favicon.png" for bed bath & beyond (us and canada)
            and two "favicon_bb.ico & favicon_bb.png" for buy buy baby
        --%>
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

        <%-- Begin TagMan --%>
        <c:if test="${TagManOn}">
			<c:choose>
		<c:when test="${empty profileId && ((fn:indexOf(pageWrapper, 'errorPages') > -1) || (fn:indexOf(pageWrapper, 'serverErrorPages') > -1) || (fn:indexOf(pageWrapper, '500') > -1))}">			
		</c:when>
		<c:otherwise>
            <dsp:include page="/tagman/header.jsp" />
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
            <c:if test="${PageType eq 'RegistryConfirmation' }">
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
            <dsp:include src="/tagman/frag/tagman_bottom_script.jsp"/>
            </c:otherwise>
	</c:choose>
        </c:if>
        <%-- End TagMan --%>

        <%-- KP COMMENT START --%>
        <%-- TBS PROJECT CSS SWITCH TO ENABLE OLD VS NEW STYLES --%>

        <%-- OLD COMPILED CSS -
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/bbb_combined.css?v=${buildRevisionNumber}" />
        -%>
        <%-- NEW TBS RWD CSS --%>
        
        <%-- UNBXD AUTOSUGGEST start --%>
        <%-- BBBI-3814|Add vendor JS and CSS file in type ahead (tech story) --%>
        <c:if test="${not empty vendorParam && 'e1' ne vendorParam}">
        	<c:set var="vNameBccKey">${vendorParam}<bbbl:label key="lbl_search_vendor_sitespect" language="${pageContext.request.locale.language}" /></c:set>
		  	<c:set var="vName"><bbbc:config key="${vNameBccKey}" configName="VendorKeys"/></c:set>
		  	<c:set var="vendorCSSHrefBccKey">${vName}<bbbl:label key="lbl_css_href" language="${pageContext.request.locale.language}" /></c:set>
		   	<link rel="stylesheet" type="text/css" href='<bbbc:config key="${vendorCSSHrefBccKey}" configName="VendorKeys"/>'>
    	</c:if>
    	<%-- UNBXD AUTOSUGGEST end --%>
        
        <c:if test="${cssCurrentSiteId eq cssTBS_BuyBuyBabySiteCode}">
            <link rel="stylesheet" href="${cssPath}/_assets/tbs_assets/css/buybuyBaby.css?ver=${buildRevisionNumber}" type="text/css" />
        </c:if>
        <%-- KP COMMENT END --%>
        <%-- Head Tag Content of StaticTemplate/RegistryTemplate --%>
            ${headTagContent}
    </head>

    <dsp:getvalueof var="bodyClass" param="bodyClass"/>
    <dsp:getvalueof var="noBGClass" param="noBGClass"/>
    
    <body id="themeWrapper" data-sitId="${currentSiteContext}" class="${themeName} ${noBGClass} ${BabyCaClass} ${vbvCFrameFix} kp ${bodyClass}" ${vbvStyleTag}>
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
        
		<!-- Code is for getting exact browser url from request Starts-->
		<dsp:getvalueof var="requestHost" bean="/OriginatingRequest.host"/>
		<dsp:getvalueof var="schemeName" bean="/OriginatingRequest.scheme"/>
		<c:set var="includeContextPath"> <%=ServletUtil.getCurrentRequest().getAttribute("javax.servlet.include.context_path")%></c:set>
		<c:set var="forwardRequestURI"> <%=ServletUtil.getCurrentRequest().getAttribute("javax.servlet.forward.request_uri")%></c:set>
		<c:set var="forwardContextPath"> <%=ServletUtil.getCurrentRequest().getAttribute("javax.servlet.forward.context_path")%></c:set>
		<c:set var="forwardQueryString"> <%=ServletUtil.getCurrentRequest().getAttribute("javax.servlet.forward.query_string")%></c:set>
		<c:set var="currentUrl" value="${schemeName}://${requestHost}"/>
			
		<c:choose>
		<c:when test="${!empty forwardRequestURI && forwardRequestURI ne 'null'}">
			<c:set var="currentUrl" value="${currentUrl}${forwardRequestURI}"/>
			<c:if test="${!empty forwardQueryString && forwardQueryString ne 'null'}">
				<c:set var="currentUrl" value="${currentUrl}?${forwardQueryString}"/>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${!empty includeContextPath && includeContextPath ne 'null'}">
				<c:set var="currentUrl" value="${currentUrl}${includeContextPath}"/>
			</c:if>
		</c:otherwise>
		</c:choose>
		<input type='hidden' id='latestUrl' value="${fn:escapeXml(currentUrl)}"/>
		<!-- Code is for getting exact browser url from request Ends-->
		
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
        <dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>

        <script type="text/javascript">
            var isTransient = '${isTransient}';
        </script>

        <%-- KP COMMENT START: New RWD page structure --%>

        <!-- screen size detection -->
        <span id="smallTest" class="show-for-small"></span>
        <span id="mediumTest" class="show-for-medium"></span>
        <span id="largeTest" class="show-for-large-up"></span>

        <div class="off-canvas-wrap" data-offcanvas>
            <div class="inner-wrap">

        <%-- KP COMMENT END --%>
        
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/includes/pageStart.jsp#2 $$Change: 635969 $--%>
