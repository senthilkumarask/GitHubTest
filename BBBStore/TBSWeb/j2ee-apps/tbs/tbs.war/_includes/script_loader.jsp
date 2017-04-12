<dsp:page>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:getvalueof var="section" param="section"/>
    <dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
    <dsp:getvalueof var="homepage" param="homepage"/>
    <dsp:getvalueof var="PageType" param="PageType"/>
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${currentSiteId}"/>
    <c:set var="googleAddressSwitch" scope="request"><bbbc:config key="google_address_switch" configName="FlagDrivenFunctions" /></c:set>

    <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
        <dsp:param name="configKey" value="FBAppIdKeys"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="fbConfigMap" param="configMap"/>
        </dsp:oparam>
    </dsp:droplet>

    <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
        <dsp:param name="configKey" value="QASKeys"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="qasConfigMap" param="configMap"/>
        </dsp:oparam>
    </dsp:droplet>

    <c:set var="REQURLPROTOCOL" value="http" scope="request" />
    <c:if test="${pageSecured}">
        <c:set var="REQURLPROTOCOL" value="https" scope="request" />
    </c:if>

   <dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
		<dsp:oparam name="output">
		<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM"/>
				</dsp:oparam>	
	</dsp:droplet>
    <%-- must load/check for this before any other js --%>
    <c:if test="${section == 'browse' || section == 'compare' || section == 'search'}">
        <script type="text/javascript">
            BBB.browse = BBB.browse || {};
        </script>
        <c:if test="${fn:indexOf(pageWrapper, 'productDetails') > -1 || fn:indexOf(pageWrapper, 'compareProducts') > -1 || fn:indexOf(pageWrapper, 'searchResults') > -1 || fn:indexOf(pageWrapper, 'subCategory') > -1}">
            <c:choose>
                <c:when test="${minifiedJSFlag}">
                    <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/min/browse.min.js?v=${buildRevisionNumber}"></script>
                </c:when>
                <c:otherwise>
                    <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/browse.js?v=${buildRevisionNumber}"></script>
                </c:otherwise>
            </c:choose>
        </c:if>
    </c:if>

  <%--### load minified js file(s) depending on the minification status ###--%>
    <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/bbb_combined.js?v=${buildRevisionNumber}"></script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/libraries/modernizr.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/init.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/foundation.min.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/foundation/foundation.tab.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/foundation/foundation.tooltip.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/foundation/foundation.dropdown.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/foundation/foundation.alert.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/foundation/foundation.accordion.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/plugins/jquery.rwdImageMaps.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/plugins/jquery.form.js"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.touchcarousel-1.1.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.zclip.min.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.placeholder.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.lazyload.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.copyEvents.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.tinyscrollbar.min.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.mousewheel.min.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/json.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/bbb_fn.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/bbb_extend.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/jquery_fn_extend.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/js_native_extend.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/jquery.maskedinput.min.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/bootstrap-formhelpers-phone.min.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/form_helpers.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/validation.util.bbb.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/registry.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/global.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/servicemethods.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/stacktrace.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/base.js?v=${buildRevisionNumber}"></script>
			<script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/plugins/easyzoom.js?v=${buildRevisionNumber}"></script>
        </c:otherwise>
    </c:choose>

<%-- KP COMMENT END --%>

    <%--### load js file for the current theme ###--%>
    <%-- <script type="text/javascript" src="${jsPath}/_assets/${themeFolder}/js/base.js?v=${buildRevisionNumber}"></script> --%>

    <%--### load js file for the current section ###--%>
    <c:if test="${section == 'accounts'}">
        <script type="text/javascript">
            BBB.accountManagement = BBB.accountManagement || {};
        </script>
        <c:choose>
            <c:when test="${minifiedJSFlag}">
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/bbb_accounts_min.js?v=${buildRevisionNumber}"></script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/address_book.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/create_fb_connect.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/account.js?v=${buildRevisionNumber}"></script>
            </c:otherwise>
        </c:choose>
        <c:if test="${bExternalJSCSS && bPlugins && TBSQASOn}">
            <c:choose>
                <c:when test="${(bAsync && TBSQASOnAsync != false) || TBSQASOnAsync}">
                    <script type="text/javascript">
                        <c:choose>
                            <c:when test="${minifiedJSFlag}">
                                bbbLazyLoader.js('${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.min.js?v=${buildRevisionNumber}', function(){
                                if (QAS_Variables) {
                                    <c:choose>
                                        <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                            QAS_Variables.DEFAULT_DATA = "CAN";
                                        </c:when>
                                        <c:otherwise>
                                            QAS_Variables.DEFAULT_DATA = "USA";
                                        </c:otherwise>
                                    </c:choose>
                                    BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                                }
                            });
                            </c:when>
                            <c:otherwise>
                                bbbLazyLoader.js('${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.js?v=${buildRevisionNumber}', function(){
                                if (QAS_Variables) {
                                    <c:choose>
                                        <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                            QAS_Variables.DEFAULT_DATA = "CAN";
                                        </c:when>
                                        <c:otherwise>
                                            QAS_Variables.DEFAULT_DATA = "USA";
                                        </c:otherwise>
                                    </c:choose>
                                    BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                                }
                            });
                            </c:otherwise>
                        </c:choose>
                    </script>
                </c:when>
                <c:otherwise>
                <c:choose>
                    <c:when test="${minifiedJSFlag}">
                        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.min.js?v=${buildRevisionNumber}"></script>
                    </c:when>
                    <c:otherwise>
                        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.js?v=${buildRevisionNumber}"></script>
                    </c:otherwise>
                </c:choose>
                <script type="text/javascript">
                    if(QAS_Variables) {
                        <c:choose>
                            <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                QAS_Variables.DEFAULT_DATA = "CAN";
                            </c:when>
                            <c:otherwise>
                                QAS_Variables.DEFAULT_DATA = "USA";
                            </c:otherwise>
                        </c:choose>
                        BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                    }
                </script>
                </c:otherwise>
            </c:choose>
        </c:if>
    </c:if>

    <c:if test="${section == 'registry'}">
        <script type="text/javascript">
            BBB.registry = BBB.registry || {};
        </script>
    </c:if>
   	<c:set var="googleLocationAPIURL"><bbbc:config key="google_api_url" configName="ThirdPartyURLs" /></c:set>	<c:set var="googleLocationAPIkey"><bbbc:config key="google_api_key" configName="ThirdPartyURLs" /></c:set>
	<c:set var="useGoogleAPIkey"><bbbc:config key="google_api_key_flag" configName="ThirdPartyURLs" /></c:set>
	<c:if test="${fn:toLowerCase(useGoogleAPIkey) eq true}">
		<c:set var="googleLocationAPIURL">${googleLocationAPIURL}&key=${googleLocationAPIkey}</c:set>
	</c:if>
	<c:if test="${fn:toLowerCase(googleAddressSwitch) eq true && (section eq 'accounts' || section eq 'checkout' || section eq 'registry' || sectionName eq 'registry')}">
    <!-- <script type="text/javascript">
		  bbbLazyLoader.js(${googleLocationAPIURL});
    </script> -->
	<script type="text/javascript" src="${googleLocationAPIURL}"></script> 
</c:if>

    <c:if test="${section == 'createRegistry'}">
        <c:choose>
            <c:when test="${minifiedJSFlag}">
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/bbb_createRegistry_min.js?v=${buildRevisionNumber}"></script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/rAccord_formValidator.js?v=${buildRevisionNumber}"></script>
            </c:otherwise>
        </c:choose>
        <c:if test="${bExternalJSCSS && bPlugins && TBSQASOn}">
            <c:choose>
                <c:when test="${(bAsync && TBSQASOnAsync != false) || TBSQASOnAsync}">
                    <script type="text/javascript">

                        <c:choose>
                            <c:when test="${minifiedJSFlag}">
                            bbbLazyLoader.js('${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.min.js?v=${buildRevisionNumber}', function(){
                            if (QAS_Variables) {
                                <c:choose>
                                    <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                        QAS_Variables.DEFAULT_DATA = "CAN";
                                    </c:when>
                                    <c:otherwise>
                                        QAS_Variables.DEFAULT_DATA = "USA";
                                    </c:otherwise>
                                </c:choose>
                                BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                            }
                        });
                            </c:when>
                            <c:otherwise>
                            bbbLazyLoader.js('${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.js?v=${buildRevisionNumber}', function(){
                            if (QAS_Variables) {
                                <c:choose>
                                    <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                        QAS_Variables.DEFAULT_DATA = "CAN";
                                    </c:when>
                                    <c:otherwise>
                                        QAS_Variables.DEFAULT_DATA = "USA";
                                    </c:otherwise>
                                </c:choose>
                                BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                            }
                        });
                            </c:otherwise>
                        </c:choose>

                    </script>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${minifiedJSFlag}">
                            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.min.js?v=${buildRevisionNumber}"></script>
                        </c:when>
                        <c:otherwise>
                            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.js?v=${buildRevisionNumber}"></script>
                        </c:otherwise>
                    </c:choose>
                    
                    <script type="text/javascript">
                        if(QAS_Variables) {
                            <c:choose>
                                <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                    QAS_Variables.DEFAULT_DATA = "CAN";
                                </c:when>
                                <c:otherwise>
                                    QAS_Variables.DEFAULT_DATA = "USA";
                                </c:otherwise>
                            </c:choose>
                            BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                        }
                    </script>
                </c:otherwise>
            </c:choose>
        </c:if>
    </c:if>
    <%-- Katori --%>
    <c:if test="${section == 'cart' || section == 'cartDetail' || fn:indexOf(pageWrapper, 'productDetails') > -1 || fn:indexOf(pageWrapper, 'wishlist') > -1}">
    	<div class="katoriModals"></div>
        <div class="katoriModalsSFL"></div>
        <script>
            var NUM_KATORI_ITEMS = $('.editPersonalization, .personalize ,.editPersonalizationSfl').length;
            
            if($("#cartBody")[0]){
                NUM_KATORI_ITEMS = NUM_KATORI_ITEMS + parseInt($('#countSavedItemsList').val());
                
            }
        </script>
        
       <dsp:droplet name="/com/bbb/common/droplet/GetVendorConfigurationDroplet">
       		<dsp:param name="productId" value="${param.productId}"/>
       		<dsp:param name="section" value="${section}"/>
       		<dsp:param name="pagewrapper" value="${pageWrapper}"/>
       		<dsp:oparam name="output">
       			<dsp:getvalueof var="vendorJsList" param="vendorJs"/>
       			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
       				<dsp:param name="array" value="${vendorJsList}"/>
       				<dsp:oparam name="output">
       					<dsp:getvalueof var="vendorJs" param="element"/>
       					 <script type="text/javascript" src="${vendorJs}"></script>
       				</dsp:oparam>
       			</dsp:droplet>
       		</dsp:oparam>
       </dsp:droplet>
      
    </c:if>
    <%-- Katori --%>
    <%-- BBBI-3814|Add vendor JS and CSS file in type ahead (tech story) --%>
    <%-- UNBXD AUTOSUGGEST start --%>
     <c:if test="${not empty vendorParam && 'e1' ne vendorParam}">
     		<c:set var="vNameBccKey">${vendorParam}<bbbl:label key="lbl_search_vendor_sitespect" language="${pageContext.request.locale.language}" /></c:set>
		  	<c:set var="vName"><bbbc:config key="${vNameBccKey}" configName="VendorKeys"/></c:set>
		  	<c:set var="vendorJsSrcBccKey">${vName}<bbbl:label key="lbl_js_src" language="${pageContext.request.locale.language}" /></c:set>
		  	<script type="text/javascript" src='<bbbc:config key="${vendorJsSrcBccKey}" configName="VendorKeys"/>'></script>
     </c:if>
    <%-- UNBXD AUTOSUGGEST end --%>
    <c:if test="${section == 'cart' || section == 'cartDetail'}">
        <script type="text/javascript">
            BBB.browse = BBB.cart || {};
        </script>
    </c:if>

    <c:if test="${section == 'checkout'}">
        <script type="text/javascript">
            BBB.browse = BBB.cart || {};
        </script>
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/social_share.js?v=${buildRevisionNumber}"></script>
        <c:choose>
            <c:when test="${minifiedJSFlag}">
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/bbb_checkout_min.js?v=${buildRevisionNumber}"></script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/address_book.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/checkout.js?v=${buildRevisionNumber}"></script>
            </c:otherwise>
        </c:choose>
        <c:if test="${bExternalJSCSS && bPlugins && TBSQASOn}">
            <c:choose>
                <c:when test="${(bAsync && TBSQASOnAsync != false) || TBSQASOnAsync}">
                    <script type="text/javascript">


                        <c:choose>
                            <c:when test="${minifiedJSFlag}">
                                bbbLazyLoader.js('${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.min.js?v=${buildRevisionNumber}', function(){
                            if (QAS_Variables) {
                                <c:choose>
                                    <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                        QAS_Variables.DEFAULT_DATA = "CAN";
                                    </c:when>
                                    <c:otherwise>
                                        QAS_Variables.DEFAULT_DATA = "USA";
                                    </c:otherwise>
                                </c:choose>
                                BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                            }
                        });
                            </c:when>
                            <c:otherwise>
                                bbbLazyLoader.js('${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.js?v=${buildRevisionNumber}', function(){
                            if (QAS_Variables) {
                                <c:choose>
                                    <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                        QAS_Variables.DEFAULT_DATA = "CAN";
                                    </c:when>
                                    <c:otherwise>
                                        QAS_Variables.DEFAULT_DATA = "USA";
                                    </c:otherwise>
                                </c:choose>
                                BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                            }
                        });
                            </c:otherwise>
                        </c:choose>


                        
                    </script>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${minifiedJSFlag}">
                            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.min.js?v=${buildRevisionNumber}"></script>
                        </c:when>
                        <c:otherwise>
                            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.js?v=${buildRevisionNumber}"></script>
                        </c:otherwise>
                    </c:choose>
                    
                    <script type="text/javascript">
                        if(QAS_Variables) {
                            <c:choose>
                                <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                    QAS_Variables.DEFAULT_DATA = "CAN";
                                </c:when>
                                <c:otherwise>
                                    QAS_Variables.DEFAULT_DATA = "USA";
                                </c:otherwise>
                            </c:choose>
                            BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                        }
                    </script>
                </c:otherwise>
            </c:choose>
        </c:if>
    </c:if>

    <%-- KP COMMENT START: this functionality was updated and is being moved to init.js 
    <c:if test="${section == 'search'}">
        <script type="text/javascript">
            BBB.search = BBB.search || {};
        </script>
        <c:choose>
            <c:when test="${minifiedJSFlag}">
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/min/search.min.js?v=${buildRevisionNumber}"></script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/search.js?v=${buildRevisionNumber}"></script>
            </c:otherwise>
        </c:choose>
    </c:if>
    --%>
    <%-- KP COMMENT END --%>

    <%--### load js file(s) based on applied pageWrapper(s) ###--%>

    <%--### LOCAL FILES ###--%>
    <c:if test="${fn:indexOf(pageWrapper, 'category') > -1}">
        <script type="text/javascript">
            BBB.browse = BBB.browse || {};
            BBB.browse.category = true;
        </script>
    </c:if>

    <c:if test="${fn:indexOf(pageWrapper, 'requestBridalBook') > -1 || fn:indexOf(pageWrapper, 'manageRegistry') > -1 || fn:indexOf(pageWrapper, 'registryConfirm') > -1}">
        <script type="text/javascript" src="${jsPath}/_assets/bbregistry/js/registry.js?v=${buildRevisionNumber}"></script>
    </c:if>
    <c:if test="${fn:indexOf(pageWrapper, 'createSimpleRegistry') > -1}">
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/simpleRegistry.js?v=${buildRevisionNumber}"></script>
    </c:if>
    <c:if test="${fn:indexOf(pageWrapper, 'college') > -1}">
        <script type="text/javascript">
            BBB.browse = BBB.browse || {};
            BBB.browse.category = true;
        </script>
    </c:if>

    <c:if test="${fn:indexOf(pageWrapper, 'bridalLanding') > -1}">
        <script type="text/javascript">
            BBB.browse = BBB.browse || {};
            BBB.browse.category = true;
        </script>
    </c:if>

    <%-- KP COMMENT START: this functionality was updated and is being moved to init.js --%>
    <%--
    <c:if test="${fn:indexOf(pageWrapper, 'subCategory') > -1}">
        <script type="text/javascript">
            BBB.browse = BBB.browse || {};
            BBB.browse.subCategory = true;
            BBB.search = BBB.search || {};
        </script>
        <c:choose>
            <c:when test="${minifiedJSFlag}">
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/min/search.min.js?v=${buildRevisionNumber}"></script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/search.js?v=${buildRevisionNumber}"></script>
            </c:otherwise>
        </c:choose>
    </c:if>
    --%>
    <%-- KP COMMENT END --%>

    <c:if test="${fn:indexOf(pageWrapper, 'login') > -1}">
        <script type="text/javascript">
            BBB.accountManagement.login = true;
        </script>
    </c:if>

    <%--### THIRD PARTY FILES ###--%>
	<%-- Scene7 API removed in favor of custom plugin --%>

<%-- KP COMMENT: I saw this output on the homepage, not sure who added this but if it needs to return please use the new markup below to keep it from messing up the page --%>
<%-- <div class="row">
    <div class="small-12 columns">
        <div class="panel callout radius">
            <h2>Test Output</h2>
            <p>
                <c:if test="${fn:indexOf(pageWrapper, 'useStoreLocator') > -1}">
                pagewrapper true
                </c:if>
                <c:out value="pageWrapper indexOf: ${fn:indexOf(pageWrapper, 'useStoreLocator')}" />
                <c:out value="bExternalJSCSS : ${bExternalJSCSS}" />
                <c:out value="bPlugins : ${bPlugins}" />
                <c:out value="LoadMQALibOn : ${LoadMQALibOn}" />
            </p>
        </div>
    </div>
</div> --%>
 <c:if test="${section != 'checkout' && PageType !='HomePage'}">
    <%-- <c:if test="${fn:indexOf(pageWrapper, 'useStoreLocator') > -1 && bExternalJSCSS && bPlugins && LoadMQALibOn}"> --%>
        <script type="text/javascript" src="<bbbc:config key='js_mapQuest' configName='ThirdPartyURLs' />"></script>
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/store_locator_v2.min.js?v=${buildRevisionNumber}"></script>

       <%--  <script type="text/javascript">
            bbbLazyLoader.js('<bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" />', function(){
                <c:choose>
                    <c:when test="${minifiedJSFlag}">
                        bbbLazyLoader.js('${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/store_locator_v2.min.js?v=${buildRevisionNumber}');
                    </c:when>
                    <c:otherwise>
                        bbbLazyLoader.js('${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/store_locator_v2.js?v=${buildRevisionNumber}');
                    </c:otherwise>
                </c:choose>
            });
        </script> --%>
         
       
        <c:if test="${currentSiteId eq TBS_BedBathCanadaSite}">
            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/store_locator.js?v=${buildRevisionNumber}"></script>
        </c:if>
</c:if> 
    <%-- </c:if> --%>

    <c:if test="${fn:indexOf(pageWrapper, 'useMapQuest') > -1 && bExternalJSCSS && bPlugins && LoadMQALibOn}">
        <c:choose>
            <c:when test="${(bAsync && MapQuestOnAsync != false) || MapQuestOnAsync}">
                <script type="text/javascript">
                    <c:choose>
                        <c:when test="${minifiedJSFlag}">
                            <c:choose>
                                <c:when test="${fn:indexOf(pageWrapper, 'productDetails') > -1}">
                                    bbbLazyLoader.js(
                                        '/_assets/tbs_assets/js/src/legacy/extensions/initiate_map_quest_view_map_dialog.js?v=${buildRevisionNumber}',
                                        '/_assets/tbs_assets/js/src/legacy/extensions/get_map_quest_directions.js?v=${buildRevisionNumber}'
                                    );
                                </c:when>
                                <c:otherwise>
                                    bbbLazyLoader.js('<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:otherwise></c:choose>',
                                        '/_assets/tbs_assets/js/src/legacy/bbb_mapQuest_min.js?v=${buildRevisionNumber}',
                                        function() {
                                            if (typeof BBB.fn.initMQ === 'function') {
                                                BBB.fn.initMQ();
                                            }
                                        }
                                    );
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${fn:indexOf(pageWrapper, 'productDetails') > -1}">
                                    bbbLazyLoader.js(
                                        '/_assets/tbs_assets/js/src/legacy/extensions/initiate_map_quest_view_map_dialog.js?v=${buildRevisionNumber}',
                                        '/_assets/tbs_assets/js/src/legacy/extensions/get_map_quest_directions.js?v=${buildRevisionNumber}'
                                    );
                                </c:when>
                                <c:otherwise>
                                    bbbLazyLoader.js('<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:otherwise></c:choose>',
                                        '/_assets/tbs_assets/js/src/legacy/extensions/initiate_map_quest_view_map_dialog.js?v=${buildRevisionNumber}',
                                        '/_assets/tbs_assets/js/src/legacy/extensions/get_map_quest_directions.js?v=${buildRevisionNumber}',
                                        '/_assets/tbs_assets/js/src/legacy/extensions/store_locator.js?v=${buildRevisionNumber}',
                                        '/_assets/tbs_assets/js/src/legacy/extensions/load_map_quest_map.js?v=${buildRevisionNumber}',
                                        function() {
                                            if (typeof BBB.fn.initMQ === 'function') {
                                                BBB.fn.initMQ();
                                            }
                                        }
                                    );
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src='<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:otherwise></c:choose>'></script>
                <c:choose>
                    <c:when test="${minifiedJSFlag}">
                        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/bbb_mapQuest_min.js?v=${buildRevisionNumber}"></script>
                    </c:when>
                    <c:otherwise>
                        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/store_locator.js?v=${buildRevisionNumber}"></script>
                        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/initiate_map_quest_view_map_dialog.js?v=${buildRevisionNumber}"></script>
                        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/get_map_quest_directions.js?v=${buildRevisionNumber}"></script>
                        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/load_map_quest_map.js?v=${buildRevisionNumber}"></script>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:if test="${fn:indexOf(pageWrapper, 'useRichFX') > -1 && bExternalJSCSS && bPlugins && RichFXOn}">
        <c:choose>
            <c:when test="${(bAsync && RichFXOnAsync != false) || RichFXOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_richFX" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_richFX" configName="ThirdPartyURLs" /></c:otherwise></c:choose>');
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src='<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_richFX" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_richFX" configName="ThirdPartyURLs" /></c:otherwise></c:choose>'></script>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:if test="${fn:indexOf(pageWrapper, 'useBazaarVoice') > -1 && bExternalJSCSS && bPlugins && BazaarVoiceOn}">

        <c:choose>
            <c:when test="${usePieCampaignId}">
                <!-- Using Pie Campaign ${usePieCampaignId}-->
                <dsp:getvalueof var="usePieCampaignId" value="false" scope="session"/>
                <dsp:getvalueof var="campaignId" param="pieCampaignId" />
            </c:when>
            <c:otherwise>
                <!-- Using page Campaign ${usePieCampaignId}-->
                <c:if test="${fn:contains(pageWrapper, 'orderDetailWrapper')}" >
                    <c:set var="campaignId"><bbbc:config key="BV_order_history_campaign_id" configName="ThirdPartyURLs" /></c:set>
                </c:if>
                <c:if test="${fn:contains(pageWrapper, 'trackOrder')}" >
                    <c:set var="campaignId"><bbbc:config key="BV_track_order_campaign_id" configName="ThirdPartyURLs" /></c:set>
                </c:if>
                <c:if test="${fn:contains(pageWrapper, 'ownerView')}" >
                    <c:set var="campaignId"><bbbc:config key="BV_registry_owner_campaign_id" configName="ThirdPartyURLs" /></c:set>
                </c:if>
                <c:if test="${fn:contains(pageWrapper, 'orderSummary')}" >
                    <c:set var="campaignId"><bbbc:config key="BV_order_summary_campaign_id" configName="ThirdPartyURLs" /></c:set>
                </c:if>
            </c:otherwise>
        </c:choose>

        <!-- Campaign ID: ${campaignId}-->

        <c:choose>
            <c:when test="${not empty userTokenBVRR}">
                <c:set var="jsVarBVUserToken" value="${userTokenBVRR}" />
            </c:when>
            <c:when test="${not empty param.userToken}">
                <c:set var="jsVarBVUserToken" value="${param.userToken}" />
            </c:when>
            <c:otherwise>
                <c:set var="jsVarBVUserToken" value="" />
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty productIdBVRR}">
                <c:set var="jsVarBVProductId" value="${productIdBVRR}" />
            </c:when>
            <c:when test="${not empty param.productId}">
                <c:set var="jsVarBVProductId" value="${param.productId}" />
            </c:when>
            <c:when test="${not empty param.BVProductId}">
                <c:set var="jsVarBVProductId" value="${param.BVProductId}" />
            </c:when>
            <c:otherwise>
                <c:set var="jsVarBVProductId" value="" />
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${(bAsync && BazaarVoiceOnAsync != false) || BazaarVoiceOnAsync}">
                <script type="text/javascript">
                    BBB.config.bazaarvoice = {
                        userToken: '${jsVarBVUserToken}',
                        productId: '${jsVarBVProductId}',
                        campaignId: '${campaignId}'
                    };
                    bbbLazyLoader.js(
                        <c:choose>
                            <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                                '<bbbc:config key="js_BazaarVoice_us" configName="ThirdPartyURLs" />',
                            </c:when>
                            <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                '<bbbc:config key="js_BazaarVoice_baby" configName="ThirdPartyURLs" />',
                            </c:when>
                            <c:otherwise>
                                '<bbbc:config key="js_BazaarVoice_ca" configName="ThirdPartyURLs" />',
                            </c:otherwise>
                        </c:choose>
                        '${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/bazaarvoice.js'
                    );
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript">
                    BBB.config.bazaarvoice = {
                        userToken: '${jsVarBVUserToken}',
                        productId: '${jsVarBVProductId}'
                    };
                </script>
                <c:choose>
                    <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                        <script type="text/javascript" src='<bbbc:config key="js_BazaarVoice_us" configName="ThirdPartyURLs" />'></script>
                    </c:when>
                    <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                        <script type="text/javascript" src='<bbbc:config key="js_BazaarVoice_baby" configName="ThirdPartyURLs" />'></script>
                    </c:when>
                    <c:otherwise>
                        <script type="text/javascript" src='<bbbc:config key="js_BazaarVoice_ca" configName="ThirdPartyURLs" />'></script>
                    </c:otherwise>
                </c:choose>
                <script type="text/javascript" src='"${jsPath}/_assets/tbs_assets/js/src/legacy/extensions/bazaarvoice.js'></script>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:set var="client_ID">
        <bbbc:config key="${currentSiteId}" configName="BBBLiveClicker" />
    </c:set>

    <c:set var="lc_jquery_js_url">
        <bbbc:config key="lc_jquery_js_${currentSiteId}" configName="BBBLiveClicker" />
    </c:set>

    <c:if test="${fn:indexOf(pageWrapper, 'useLiveClicker') > -1 && bExternalJSCSS && bPlugins && LiveClickerOn}">
        <c:choose>
            <c:when test="${(bAsync && LiveClickerOnAsync != false) || LiveClickerOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_liveClicker" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_liveClicker" configName="ThirdPartyURLs" /></c:otherwise></c:choose>', function() {
                        if (typeof lc !== 'undefined') {
                            lc.settings = { 'account_id' : ${client_ID} };
                            bbbLazyLoader.js('<c:choose><c:when test="${pageSecured}"><bbbc:config key="banner_js_${currentSiteId}" configName="BBBLiveClicker" /></c:when><c:otherwise><bbbc:config key="banner_js_${currentSiteId}" configName="BBBLiveClicker" /></c:otherwise></c:choose>', function() {
                                if (typeof BBB.fn.initLCCreateBanner === 'function') {
                                    BBB.fn.initLCCreateBanner();
                                }
                            });
                        }
                    });
                    <c:if test="${fn:indexOf(pageWrapper, 'guideAdvice') > -1 || fn:indexOf(pageWrapper, 'videos') > -1}">
                        //TODO: make third party tag for this lib
                        bbbLazyLoader.js('${lc_jquery_js_url}', function() {
                            if (typeof BBB.fn.initLC !== 'undefined' && typeof BBB.fn.initLC === 'function') {
                                BBB.fn.initLC();
                            } else if (typeof BBB.fn.initLCSearch !== 'undefined' && typeof BBB.fn.initLCSearch === 'function') {
                                BBB.fn.initLCSearch();
                            }
                        });
                    </c:if>
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src='<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_liveClicker" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_liveClicker" configName="ThirdPartyURLs" /></c:otherwise></c:choose>'></script>
                <script type="text/javascript">
                    if (typeof lc !== 'undefined') {
                        lc.settings = { 'account_id' : ${client_ID} };
                    }
                </script>
                <script type="text/javascript" src='<c:choose><c:when test="${pageSecured}"><bbbc:config key="banner_js_${currentSiteId}" configName="BBBLiveClicker" /></c:when><c:otherwise><bbbc:config key="banner_js_${currentSiteId}" configName="BBBLiveClicker" /></c:otherwise></c:choose>'></script>
                <c:if test="${fn:indexOf(pageWrapper, 'guideAdvice') > -1 || fn:indexOf(pageWrapper, 'videos') > -1}">
                    <script type="text/javascript" src='<bbbc:config key="lc_jquery_js_${currentSiteId}" configName="BBBLiveClicker" />'></script>
                </c:if>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:if test="${fn:indexOf(pageWrapper, 'useCertonaJs') > -1 && bExternalJSCSS && bAnalytics && CertonaOn}">
        <c:choose>
            <c:when test="${(bAsync && CertonaOnAsync != false) || CertonaOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('${jsPath}<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_certona" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_certona" configName="ThirdPartyURLs" /></c:otherwise></c:choose>');
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_certona" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_certona" configName="ThirdPartyURLs" /></c:otherwise></c:choose>"></script>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:if test="${fn:indexOf(pageWrapper, 'useCertonaAjax') > -1 && bExternalJSCSS && bAnalytics && CertonaOn}">
        <script type="text/javascript">
            BBB.loadCertonaJS = function() {
                bbbLazyLoader.js('${jsPath}<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_certona" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_certona" configName="ThirdPartyURLs" /></c:otherwise></c:choose>');
            };
        </script>
    </c:if>

    <c:if test="${bExternalJSCSS && bAnalytics && GoogleAnalyticsOn}">

        <c:set var="googleSecuredJsURL"><bbbc:config key="google_secured_js_url" configName="ThirdPartyURLs" /></c:set>
        <c:set var="googleJsURL"><bbbc:config key="google_js_url" configName="ThirdPartyURLs" /></c:set>
        <c:choose>
            <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                <c:set var="GAAccountId"><bbbc:config key="googleAnalyticsId_US" configName="ThirdPartyURLs" /></c:set>
            </c:when>
            <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                <c:set var="GAAccountId"><bbbc:config key="googleAnalyticsId_Baby" configName="ThirdPartyURLs" /></c:set>
            </c:when>
            <c:otherwise>
                <c:set var="GAAccountId"><bbbc:config key="googleAnalyticsId_CA" configName="ThirdPartyURLs" /></c:set>
            </c:otherwise>
        </c:choose>

        <script type="text/javascript">
            var _gaq = _gaq || [];
            _gaq.push(['_setAccount', '${GAAccountId}']);
            _gaq.push(['_trackPageview']);
        </script>


        <c:choose>
            <c:when test="${pageSecured}">
                <c:set var="GAFile" scope="request" value="${googleSecuredJsURL}" />
            </c:when>
            <c:otherwise>
                <c:set var="GAFile" scope="request" value="${googleJsURL}" />
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${(bAsync && GoogleAnalyticsOnAsync != false) || GoogleAnalyticsOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('${GAFile}', function(){
                        if (typeof _gat !== 'undefined') {
                            function TLGetCookie(c_name) {
                                var ret = '', c_start, c_end;
                                if (document.cookie.length > 0) {
                                    c_start = document.cookie.indexOf(c_name + "=");
                                    if (c_start != -1) {
                                        c_start = c_start + c_name.length + 1;
                                        c_end = document.cookie.indexOf(";", c_start);
                                        if (c_end == -1) {
                                            c_end = document.cookie.length;
                                        }
                                        ret = unescape(document.cookie.substring(c_start, c_end));
                                    }
                                }
                                return ret;
                            }
                            var pageTracker = _gat._getTracker("UA-4040192-1");
                            pageTracker._initData();
                            pageTracker._setVar("TLTIID_" + TLGetCookie('TLTSID'));
                        }
                    });
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${GAFile}"></script>
                <script type="text/javascript">
                    function TLGetCookie(c_name) {
                        var ret = '', c_start, c_end;
                        if (document.cookie.length > 0) {
                            c_start = document.cookie.indexOf(c_name + "=");
                            if (c_start != -1) {
                                c_start = c_start + c_name.length + 1;
                                c_end = document.cookie.indexOf(";", c_start);
                                if (c_end == -1) {
                                    c_end = document.cookie.length;
                                }
                                ret = unescape(document.cookie.substring(c_start, c_end));
                            }
                        }
                        return ret;
                    }
                    var gaLoadTimerHandle,
                        gaLoadTimerCounter = 0,
                        gaPageTracker = function() {
                            if (typeof _gat !== 'undefined') {
                                var pageTracker = _gat._getTracker("UA-4040192-1");
                                pageTracker._initData();
                                pageTracker._setVar("TLTIID_" + TLGetCookie('TLTSID'));
                            }
                        },
                        gaLoadCheck = function() {
                            if (typeof _gat !== 'undefined') {
                                window.clearTimeout(gaLoadTimerHandle);
                                gaPageTracker();
                            } else {
                                if (++gaLoadTimerCounter <= 15) {
                                    gaLoadTimerHandle = window.setTimeout("gaLoadTimer()", 500);
                                } else {
                                    window.clearTimeout(gaLoadTimerHandle);
                                }
                            }
                        };

                    if (typeof _gat === 'undefined') {
                        gaLoadTimerHandle = window.setTimeout("gaLoadTimer()", 100);
                    } else {
                        gaPageTracker();
                    }
                </script>
            </c:otherwise>
        </c:choose>

    </c:if>

    <c:if test="${bExternalJSCSS && bAnalytics && OmnitureOn}">

        <c:choose>
            <c:when test="${minifiedJSFlag}">
                        
                <c:choose>
                    <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                        <c:set var="OmniFile1" value="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/omniture_us.min.js?v=${buildRevisionNumber}" scope="request" />
                    </c:when>
                    <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                        <c:set var="OmniFile1" value="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/omniture_baby.min.js?v=${buildRevisionNumber}" scope="request" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="OmniFile1" value="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/omniture_ca.min.js?v=${buildRevisionNumber}" scope="request" />
                    </c:otherwise>
                </c:choose>
                <%-- <c:set var="OnmiFile2" value="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/omniture_tracking.js?v=${buildRevisionNumber}" scope="request" /> --%>

                <c:choose>
                    <c:when test="${(bAsync && OmnitureOnAsync != false) || OmnitureOnAsync}">
                        <script type="text/javascript">
                            bbbLazyLoader.js('${OmniFile1}');
                        </script>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/omniture_us.min.js?v=${buildRevisionNumber}"></script>
                            </c:when>
                            <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/omniture_baby.min.js?v=${buildRevisionNumber}"></script>
                            </c:when>
                            <c:otherwise>
                                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/omniture_ca.min.js?v=${buildRevisionNumber}"></script>
                            </c:otherwise>
                        </c:choose>
                      <%--   <c:if test="${fn:indexOf(pageWrapper, 'productDetails') > -1}">
                            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/lc_omniture.js?v=${buildRevisionNumber}"></script>
                        </c:if> --%>
                    </c:otherwise>
                </c:choose>

            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                        <c:set var="OnmiFile1" value="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/bbb_s_code.js?v=${buildRevisionNumber}" scope="request" />
                    </c:when>
                    <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                        <c:set var="OnmiFile1" value="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/buybuybaby_s_code.js?v=${buildRevisionNumber}" scope="request" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="OnmiFile1" value="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/bbbca_s_code.js?v=${buildRevisionNumber}" scope="request" />
                    </c:otherwise>
                </c:choose>
                <c:set var="OnmiFile2" value="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/omniture_tracking.js?v=${buildRevisionNumber}" scope="request" />

                <c:choose>
                    <c:when test="${(bAsync && OmnitureOnAsync != false) || OmnitureOnAsync}">
                        <script type="text/javascript">
                            bbbLazyLoader.js('${OnmiFile1}', '${OnmiFile2}');
                        </script>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/bbb_s_code.js?v=${buildRevisionNumber}"></script>
                            </c:when>
                            <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/buybuybaby_s_code.js?v=${buildRevisionNumber}"></script>
                            </c:when>
                            <c:otherwise>
                                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/bbbca_s_code.js?v=${buildRevisionNumber}"></script>
                            </c:otherwise>
                        </c:choose>
                        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/omniture_tracking.js?v=${buildRevisionNumber}"></script>

                        <%-- <c:if test="${fn:indexOf(pageWrapper, 'productDetails') > -1}">
                            <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/omniture/lc_omniture.js?v=${buildRevisionNumber}"></script>
                        </c:if> --%>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>


    </c:if>

    <c:if test="${bExternalJSCSS && bAnalytics && RKGOn}">
        <c:set var="merchantId">
            <bbbc:config key="${currentSiteId}" configName="RKGKeys" />
        </c:set>
        <%-- must NOT be loaded async as it will break the entire site, because this lib uses document.write[deprecated] and it does not work on async loads. --%>
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/rkg/rkg_tracking.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript">
            try {
                rkg_track_sid('${merchantId}');
            } catch (e) { }
        </script>
    </c:if>
    
    <c:if test="${fn:indexOf(pageWrapper, 'printerFriendly') > -1 || (not empty param.printerFriendly && param.printerFriendly eq 'true')}">
        <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/print_page.css?v=${buildRevisionNumber}" media="all" />
    </c:if>
    
    <%-- Commenting out Foresee as part of 34473
    <c:if test="${bExternalJSCSS && bAnalytics && ForeseeOn}">
        <c:choose>
            <c:when test="${(bAsync && ForeseeOnAsync != false) || ForeseeOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('"${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/foresee/${currentSiteId}/foresee-alive.js?v=${buildRevisionNumber}', '"${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/foresee/${currentSiteId}/foresee-trigger.js?v=${buildRevisionNumber}');
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/foresee/${currentSiteId}/foresee-alive.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/thirdparty/foresee/${currentSiteId}/foresee-trigger.js?v=${buildRevisionNumber}"></script>
            </c:otherwise>
        </c:choose>
        <c:if test="${TeaLeafOn}"> --%>
            <%--Setting tealeaf cookie in foresee --%>
            <%--
                <script type="text/javascript">
                var tltSID = TLGetCookie("TLTSID");
                if (typeof FSR !== 'undefined') {
                    try { FSR.CPPS.set('TLTSID',tltSID); } catch(e) {}
                }
            </script>
        </c:if>
    </c:if>
    --%>
    <c:if test="${fn:indexOf(pageWrapper, 'useFB') > -1 && bExternalJSCSS && bPlugins && FBOn}">
        <c:set var="facebookSecuredJsURL"><bbbc:config key="facebook_secured_js_url" configName="ThirdPartyURLs" /></c:set>
        <c:set var="facebookJsURL"><bbbc:config key="facebook_js_url" configName="ThirdPartyURLs" /></c:set>
        <c:choose>
            <c:when test="${pageSecured}">
                <c:set var="FBFile" value="${facebookSecuredJsURL}" scope="request" />
            </c:when>
            <c:otherwise>
                <c:set var="FBFile" value="${facebookJsURL}" scope="request" />
            </c:otherwise>
        </c:choose>
        <div id="fb-root"></div>
        <script type="text/javascript">
            BBB.FBLoadedFlag = false;
            window.fbAsyncInit = function() {
                FB.init({
                    appId: '${fbConfigMap[currentSiteId]}',
                    status: true,
                    cookie: true,
                    xfbml: true,
                    oauth: true,
                    version: 'v2.0',
                    channelUrl: window.location.protocol + '//${pageContext.request.serverName}${contextPath}/channel.jsp' // IE recursive page loade hack
                });
                // IE recursive page loade hack
                try { FB.UIServer.setActiveNode = function(a,b){FB.UIServer._active[a.id]=b;}; } catch(e) {}
                <%--
                try { FB.UIServer.setLoadedNode = function(a,b){FB.UIServer._loadedNodes[a.id]=b;}; } catch(e) {}
                --%>
                BBB.FBLoadedFlag = true;
                if (typeof BBB.fn.initFB === 'function') {
                    BBB.fn.initFB();
                }
            };
        </script>
        <c:choose>
            <c:when test="${(bAsync && FBOnAsync != false) || FBOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('${FBFile}');
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${FBFile}"></script>
            </c:otherwise>
        </c:choose>
    </c:if>

    <script type="text/javascript">
        BBB.rootCategory = "menu${rootCategory}";
    </script>
    
    <dsp:getvalueof var="primeRegistryCompleted" bean="SessionBean.primeRegistryCompleted" />
    <dsp:getvalueof var="anonymousUser" bean="/atg/userprofiling/Profile.transient" />
<%-- <c:if test="${(securityStatus == 2 || securityStatus == 4) && not primeRegistryCompleted}"> --%>
	<input type="hidden" id="primeRegistryToBeDone" value="${not anonymousUser && not primeRegistryCompleted}" />

    <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param name="value" bean="/atg/userprofiling/Profile.transient" />
        <dsp:oparam name="false">
            <dsp:getvalueof var="profileID" bean="/atg/userprofiling/Profile.id"  />
            <script type="text/javascript">
                if(typeof s !=='undefined') {
                    s.prop23 = '${profileID}';
                    s.eVar38 ='${profileID}';
                    s.prop16= 'registered user';
                    s.eVar16= 'registered user';
                }
            </script>
        </dsp:oparam>
        <dsp:oparam name="true">
            <script type="text/javascript">
                if(typeof s !=='undefined') {
                    s.prop16= 'non registered user';
                    s.eVar16= 'non registered user';
                }
            </script>
            <dsp:droplet name="/com/bbb/account/droplet/BBBGetProfileCookieDroplet">
                <dsp:oparam name="output">
                    <dsp:getvalueof var="pId" param="profileId"/>
                    <c:choose>
                        <c:when test="${not empty pId}">
                            <script type="text/javascript">
                                if(typeof s !=='undefined') {
                                    s.prop23 = '${pId}';
                                    s.eVar38 ='${pId}';
                                    s.prop16= 'registered user';
                                    s.eVar16= 'registered user';
                                }
                            </script>
                        </c:when>
                        <c:otherwise>
                            <script type="text/javascript">
                                if(typeof s !=='undefined') {
                                    s.prop16= 'non registered user';
                                    s.eVar16= 'non registered user';
                                }
                            </script>
                        </c:otherwise>
                    </c:choose>
                </dsp:oparam>
                <dsp:oparam name="error">
                    <script type="text/javascript">
                        if(typeof s !=='undefined') {
                            s.prop16= 'non registered user';
                            s.eVar16= 'non registered user';
                        }
                    </script>
                </dsp:oparam>
            </dsp:droplet>
        </dsp:oparam>
    </dsp:droplet>
    <c:if test="${fn:indexOf(pageWrapper, 'bridalLanding') > -1}">
        <script type="text/javascript">
            var resx = new Object();
            resx.appid = "${appIdCertona}";
            resx.pageid = "${pageIdCertona}";
            resx.customerid = "${profileID}";
            /* resx.links = '${linksCertona}'+'${productList}';*/
            resx.links = ""; //passing these empty for registry landing page
            BBB.loadCertonaJS();
        </script>
    </c:if>
</dsp:page>
