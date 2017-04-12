<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="section" param="section"/>
<dsp:getvalueof var="pageWrapper" param="pageWrapper"/>
<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
<c:if test="${minifiedCSSFlag == 'true'}"><c:set var="min" value=".min" /></c:if>

<%--
<!-- Prompt IE 6 users to install Chrome Frame. Remove this if you want to support IE 6.
    chromium.org/developers/how-tos/chrome-frame-getting-started -->
<!--[if lt IE 7 ]>
    <script src="//ajax.googleapis.com/ajax/libs/chrome-frame/1.0.3/CFInstall.min.js"></script>
    <script type="text/javascript">window.attachEvent('onload',function(){CFInstall.check({mode:'overlay'})})</script>
<![endif]-->
--%>

<c:set var="eximDesktopJsPath" scope="request"><bbbc:config key="Exim_desktop_js_path" configName="EXIMKeys" /></c:set>

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

<%--   
	<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
		<dsp:oparam name="output">
		<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM"/>
				</dsp:oparam>	
	</dsp:droplet>--%>

<dsp:getvalueof var="buildRevisionNumber" param="buildRevisionNumber"/>

<c:set var="googleAddressSwitch"><bbbc:config key="google_address_switch" configName="FlagDrivenFunctions" /></c:set>
<c:set var="googleLocationAPIURL"><bbbc:config key="google_api_url" configName="ThirdPartyURLs" /></c:set>
<c:set var="googleLocationAPIkey"><bbbc:config key="google_api_key" configName="ThirdPartyURLs" /></c:set>
<c:set var="useGoogleAPIkey"><bbbc:config key="google_api_key_flag" configName="ThirdPartyURLs" /></c:set>
<c:set var="useGoogleAPIProdkey"><bbbc:config key="google_api_key_prod_flag" configName="ThirdPartyURLs" /></c:set>
<c:if test="${fn:toLowerCase(useGoogleAPIkey) eq true}">
<c:choose>
	<c:when test="${fn:toLowerCase(useGoogleAPIProdkey) eq true}">
		<c:set var="googleLocationAPIURL">${googleLocationAPIURL}&key=${googleLocationAPIkey}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="googleLocationAPIURL">${googleLocationAPIURL}</c:set>
	</c:otherwise>
</c:choose>
</c:if>

<%--### load global js file(s) depending on the minification status ###--%>
<c:choose>
    <c:when test="${minifiedJSFlag}">
        <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_combined_bottom.js?v=${buildRevisionNumber}"></script>
    </c:when>
    <c:otherwise>
	 <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.validate.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/global.config.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery-ui-1.8.17.min.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.ui.selectmenu.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.carouFredSel-5.5.5-min.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.touchcarousel-1.1.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.form.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.zclip.min.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.placeholder.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.uniform.min.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.lazyload.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.copyEvents.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.tinyscrollbar.min.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.mousewheel.min.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.ui.multiAccordion.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/json.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/geo.js?v=${buildRevisionNumber}"></script>
       <%-- <c:if test="${fn:indexOf(pageWrapper, 'webPopOverJS') > -1}">  --%> 
	     <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.webui-popover.js?v=${buildRevisionNumber}"></script>
	     <%-- </c:if>  --%>        
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/bbb_fn.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/bbb_extend.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/jquery_fn_extend.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/js_native_extend.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/menu_flyout.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/modal_dialog.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/servicemethods.js?v=${buildRevisionNumber}"></script>
		<script type="text/javascript" src="${jsPath}/_assets/global/js/dynamic_header.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/change_store_bopus_dialog.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/form_helpers.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/bbb_geo.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/validation.util.bbb.js?v=${buildRevisionNumber}"></script>        
        <script type="text/javascript" src="${jsPath}/_assets/global/js/global.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/javadev.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/stacktrace.js?v=${buildRevisionNumber}"></script>
    </c:otherwise>
</c:choose>

<bbbt:textArea key='merchPLPJSLocadContainer' language='${language}'/>

<c:if test="${fn:toLowerCase(googleAddressSwitch) eq true}">
	<c:if test="${fn:indexOf(pageWrapper, 'useGoogleAddress') > -1}"> 
	<%-- BBBI-4518 : Places library – Lazy load on pages that are required- Mobile --%>			
	<%-- <script type="text/javascript" src="${googleLocationAPIURL}"></script> --%>
	<script type="text/javascript">
	
 		 /* ** bbbmLazyLoader ** */
 		(function(doc) {
 		    var bbbmwLazyLoader = "bbbmwLazyLoader",
 		        api = window[bbbmwLazyLoader] = (window[bbbmwLazyLoader] || function() {
 		            api.ready.apply(null, arguments);
 		        }),
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

 		            queue.push(function() {
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
 		        /*if (window.console) {
	                if (window.console[type]) {
	                    <c:if test="${minifiedJSFlag == 'true'}">
	                        window.console[type](msg);
	                    </c:if>
	                }
	            }*/
 		    };

 		    api.ready = function(key, fn) {
 		        if (isFunc(key)) {
 		            fn = key;
 		            key = "ALL";
 		        }

 		        if (typeof key != 'string' || !isFunc(fn)) {
 		            return api;
 		        }

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
 		            name = els[els.length - 1],
 		            i = name.indexOf("?");

 		        return i != -1 ? name.substring(0, i) : name;
 		    }

 		    function getScript(url) {
 		        var script;

 		        if (typeof url == 'object') {
 		            for (var key in url) {
 		                if (url[key]) {
 		                    script = {
 		                        name: key,
 		                        url: url[key]
 		                    };
 		                }
 		            }
 		        } else {
 		            script = {
 		                name: toLabel(url),
 		                url: url
 		            };
 		        }

 		        var existing = scripts[script.name];
 		        if (existing && existing.url === script.url) {
 		            return existing;
 		        }

 		        scripts[script.name] = script;

 		        return script;
 		    }

 		    function each(arr, fn) {
 		        if (!arr) {
 		            return;
 		        }

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
 		            scriptTag({
 		                src: script.url,
 		                type: 'cache'
 		            }, function() {
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
 		
		// prepare te pulgin	
 		if (Object.prototype.toString.call(bbbmwLazyLoader.fireReady) === '[object Function]') {
 			bbbmwLazyLoader.fireReady();
 		} 	
 		
        /* lazy load the script file using the utility */
		bbbmwLazyLoader.js('${googleLocationAPIURL}',function() {	
			  var elementObj = {},
              targetElement = '',
              formName = '';	
          if ( $('.useGoogleAddress').hasClass('createSimpleRegistry') || $('.useGoogleAddress').hasClass('updateFormPopup') ) { 
            // private function
              var _initAutoCompletePlaces = function (strTxtBoxId, elementObj) {                                       
                  formName = "registryFormPost";                    
                  BBB.fn.autocomplete_address_simplify(strTxtBoxId, elementObj, formName, false, validateFormObj); 
              } 

              // create registry page or update registry pop up
              var validateFormObj = {
                  street_number : 'txtAddressBookAddAddress1',
                  route : 'txtAddressBookAddAddress1',
                  premise : 'txtAddressBookAddAddress1',
                  sublocality_level_1 : 'txtAddressBookAddAddress1',
                  locality : 'txtAddressBookAddCity',
                  administrative_area_level_1 : 'selAddressBookAddState',
                  postal_code : 'txtAddressBookAddZip',
                  country :'txtAddressBookAddCountry'
              };
              // For Contact  Address
              if ($('#txtRegistryRegistrantContactAddress')[0]) {
                      elementObj = {
                          street_number : 'txtRegistryRegistrantContactAddress1',
                          route : 'txtRegistryRegistrantContactAddress1',
                          premise : 'txtRegistryRegistrantContactAddress1',
                          sublocality_level_1 : 'txtRegistryRegistrantContactAddress2',
                          locality : 'txtRegistryRegistrantContactCity',
                          administrative_area_level_1 : 'selRegistryRegistrantContactState',
                          postal_code : 'txtRegistryRegistrantContactZip',
                          country :'txtRegistryRegistrantContactCountryName'
                      };
                      _initAutoCompletePlaces("txtRegistryRegistrantContactAddress", elementObj);                               
                }  
              // For Shipping Address
              if ($('#txtRegistryCurrentShippingAddress')[0]) {
                      elementObj = {
                          street_number : 'txtRegistryCurrentShippingAddress1',
                          route : 'txtRegistryCurrentShippingAddress1',
                          premise : 'txtRegistryCurrentShippingAddress1',
                          sublocality_level_1 : 'txtRegistryCurrentShippingAddress2',
                          locality : 'txtRegistryCurrentShippingCity',
                          administrative_area_level_1 : 'selRegistryCurrentShippingState',
                          postal_code : 'txtRegistryCurrentShippingZip',
                          country :'txtRegistryCurrentShippingCountryName'
                      };
                      _initAutoCompletePlaces("txtRegistryCurrentShippingAddress", elementObj);
              }                       
              // For Future Shipping Address
              if ($('#txtSimpleRegFutureShippingGoogleSuggest')[0]) {                      
					elementObj = {
                          street_number : 'txtSimpleRegFutureShippingAddress1',
                          route : 'txtSimpleRegFutureShippingAddress1',
                          premise : 'txtSimpleRegFutureShippingAddress1',
                          sublocality_level_1 : 'txtSimpleRegFutureShippingAddress2',
                          locality : 'txtSimpleRegFutureShippingCity',
                          administrative_area_level_1 : 'txtSimpleRegFutureShippingState',
                          postal_code : 'txtSimpleRegFutureShippingZip',
                          country :'txtSimpleRegFutureShippingCountryName'
                      };  
                      _initAutoCompletePlaces("txtSimpleRegFutureShippingGoogleSuggest", elementObj);
              }                    
          } else if ( $('.useGoogleAddress').hasClass('addressBook') ) {   // adress book page             
              if ( $('#editAddressDialogForm')[0] && $('#txtAddressBookEditAddress1')[0] ) {
                  elementObj={
                      street_number:'txtAddressBookEditAddress1',
                      route:'txtAddressBookEditAddress1',
                      premise:'txtAddressBookEditAddress1',
                      sublocality_level_1:'txtAddressBookEditAddress2',
                      locality: 'txtAddressBookEditCity',
                      administrative_area_level_1: 'selAddressBookEditState',
                      postal_code:'txtAddressBookEditZip'
                  };
                  targetElement="txtAddressBookEditAddress1";
                  formName= "editAddressDialogForm";                    
                  BBB.fn.autocomplete_address(targetElement,elementObj,formName);
              }
              if ( $("#addAddressDialogForm")[0] && $('#txtAddressBookAddAddress1')[0] ) {                    
                  elementObj={
                      street_number:'txtAddressBookAddAddress1',
                      route:'txtAddressBookAddAddress1',
                      premise:'txtAddressBookAddAddress1',
                      sublocality_level_1:'txtAddressBookAddAddress2',
                      locality: 'txtAddressBookAddCity',
                      administrative_area_level_1: 'selAddressBookAddState',
                      postal_code:'txtAddressBookAddZip'
                  };
                  targetElement="txtAddressBookAddAddress1";
                  formName= "addAddressDialogForm";
                  BBB.fn.autocomplete_address(targetElement,elementObj,formName);
              }               
          } else if ( $('.useGoogleAddress').hasClass('singleShippingPage') || $('.useGoogleAddress').hasClass('multiShippingPage') ) {  // checkout page 
              if ( ( $('#formShippingSingleLocation')[0] || $('#addNewAddressFrm')[0] ) && $('#address1')[0] ) {  // shipping address
                  elementObj={
                      street_number: 'address1',
                      route: 'address1',
                      premise: 'address1',
                      sublocality_level_1: 'address2',
                      locality: 'cityName',
                      administrative_area_level_1: 'stateName',
                      postal_code: 'zip'
                  };
                  targetElement="address1";
                  formName = "formShippingSingleLocation";
                  if ( $('#addNewAddressDialogWrapper')[0] ) {	// multi shipping co page
                      formName = "addNewAddressFrm";
                  }
                  BBB.fn.autocomplete_address(targetElement,elementObj,formName);  
              }
              if ( $('#SpcBillingForm')[0] && $('#BillingAddress1')[0] ) {  // billing address
            	  // commenting out below two lines as part of BBB-975
                  // $('#BillingAddress1').on('focus', function() {
                  //    var _internationalBill = true; // private string
                      elementObj = {
                          street_number: 'BillingAddress1',
                          route: 'BillingAddress1',
                          premise: 'BillingAddress1',
                          sublocality_level_1: 'BillingAddress2',
                          locality: 'BillingCityName',
                          administrative_area_level_1: 'billStateName',
                          postal_code: 'BillingZip',
                          country: 'countryName'
                      };                       
                      formName = "SpcBillingForm";
                      targetElement="BillingAddress1";                        
                      $('#countryName').removeClass('error');
                      $('#errCountryInvalid').addClass('hidden');
                   	  // commenting out below line as part of BBB-975
                      // BBB.fn.autocomplete_address(targetElement, elementObj, formName, _internationalBill);
                   	  BBB.fn.autocomplete_address(targetElement, elementObj, formName);
                  //});                                        
              }
          }
			
		});
	</script>
	</c:if>
</c:if>
<c:if test="${fn:indexOf(pageWrapper, 'personalStorePage') > -1}">
    <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.mCustomScrollbar.min.js?v=${buildRevisionNumber}"></script>
</c:if>

<c:if test="${section == 'registry' || (section == 'browse' && fn:indexOf(pageWrapper, 'browseRegistryCollection') > -1)}">
    <script type="text/javascript">
        bbbLazyLoader.js('${jsPath}/_assets/global/js/registry<c:if test="${minifiedJSFlag}">.min</c:if>.js?v=${buildRevisionNumber}');
    </script>
</c:if>
<c:if test="${section !='singleCheckout'  && section !='checkout' &&  section!='createSimpleRegistry'}">    
    <c:if test="${fn:indexOf(pageWrapper, 'findAStorePage') < 0 && fn:indexOf(pageWrapper, 'homePage') < 0 && fn:indexOf(pageWrapper, 'brandListing')  < 0 && fn:indexOf(pageWrapper, 'login')  < 0}">
        <script type="text/javascript">
            bbbLazyLoader.js('${jsPath}/_assets/global/js/registryChecklist<c:if test="${minifiedJSFlag}">.min</c:if>.js?v=${buildRevisionNumber}');
        </script>      
    </c:if>    
</c:if>
<c:if test="${section !='singleCheckout' && section !='checkout'}">
  <c:choose>
    <c:when test="${minifiedJSFlag}">
      <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_trends_min.js?v=${buildRevisionNumber}"></script>
    </c:when>
    <c:otherwise>
      <script type="text/javascript" src="${jsPath}/_assets/global/js/trends.js?v=${buildRevisionNumber}"></script>
    </c:otherwise>
  </c:choose>
</c:if>

<c:if test="${fn:indexOf(pageWrapper, 'useCaptcha') > -1}">
    <%-- <script src='${recaptchaURL}'></script> --%>
    <script>
     /* lazy load the script file using the utility */
		bbbLazyLoader.js('//www.google.com/recaptcha/api.js',function() {
		});
	</script>
</c:if>

<script type="text/javascript">
	var _enableLog = false,
		collageSiteId = "";
</script>
<c:if test="${enableLog}">
<script type="text/javascript">
	var _enableLog = true;
</script>
</c:if>
<script type="text/javascript">
//code to console the value if parameter appends "enableLog=true" in URL
	_log = function(param) {
		if(_enableLog) {
			BBB.fn.log(param);
		}
	}
</script>


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
<%-- BBBI-3814|Add vendor JS and CSS file in type ahead (tech story) --%>
<%-- UNBXD AUTOSUGGEST start --%>
<c:if test="${not empty vendorParam && 'e1' ne vendorParam}">
	<c:set var="vNameBccKey">${vendorParam}<bbbl:label key="lbl_search_vendor_sitespect" language="${pageContext.request.locale.language}" /></c:set>
  	<c:set var="vName"><bbbc:config key="${vNameBccKey}" configName="VendorKeys"/></c:set>
  	<c:set var="vendorJsSrcBccKey">${vName}<bbbl:label key="lbl_js_src" language="${pageContext.request.locale.language}" /></c:set>
  	<script type="text/javascript" src='<bbbc:config key="${vendorJsSrcBccKey}" configName="VendorKeys"/>'></script>
</c:if>
<%-- UNBXD AUTOSUGGEST end --%>
<%-- Katori --%>
<%-- must load/check for this before any other js --%>
<c:if test="${section == 'browse' || section == 'compare' || section == 'search'}">
    <script type="text/javascript">
        BBB.browse = BBB.browse || {};
    </script>
    <c:if test="${fn:indexOf(pageWrapper, 'productDetails') > -1 || fn:indexOf(pageWrapper, 'compareProducts') > -1 || fn:indexOf(pageWrapper, 'personalStorePage') > -1 || fn:indexOf(pageWrapper, 'searchResults') > -1 || fn:indexOf(pageWrapper, 'subCategory') > -1}">
        <c:choose>
            <c:when test="${minifiedJSFlag}">
			<c:set var="lazyLoadScripts" scope="request"><bbbc:config key="JSScriptsLazyLoad" configName="FlagDrivenFunctions" language="${pageContext.request.locale.language}"/></c:set>
			<c:choose>
				<c:when test="${lazyLoadScripts}">
					<script>
						bbbLazyLoader.js('${jsPath}/_assets/global/js/bbb_browse_min.js?v=${buildRevisionNumber}');
					</script>
				</c:when>
				<c:otherwise>	
                <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_browse_min.js?v=${buildRevisionNumber}"></script>
				</c:otherwise>
            </c:choose>
            </c:when>
            <c:otherwise>
            	<script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/easyzoom.js?v=${buildRevisionNumber}"></script>
            	<script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/slick.min.js?v=${buildRevisionNumber}"></script>
        		<script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/pinchzoom.min.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript" src="${jsPath}/_assets/global/js/browse.js?v=${buildRevisionNumber}"></script>
            </c:otherwise>
        </c:choose>
    </c:if>
</c:if>


<%--### load js file for the current theme ###--%>


<%--### load js file for the current section ###--%>
<c:if test="${section == 'accounts'}">
    <script type="text/javascript">
        BBB.accountManagement = BBB.accountManagement || {};
    </script>
    <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_accounts_min.js?v=${buildRevisionNumber}"></script>
        	<%-- Adding JS files for coupon barcode --%>
		    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/coupon/bcmath-min.js?v=${buildRevisionNumber}"></script>
		    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/coupon/pdf417-min.js?v=${buildRevisionNumber}"></script>
		    <script type="text/javascript" src="${jsPath}/_assets/global/js/couponBarcode.js?v=${buildRevisionNumber}"></script>
		    <%-- End Adding JS files for coupon barcode--%>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/address_book.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/create_fb_connect.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/account.js?v=${buildRevisionNumber}"></script>
        	<%-- Adding JS files for coupon barcode --%>
		    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/coupon/bcmath-min.js?v=${buildRevisionNumber}"></script>
		    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/coupon/pdf417-min.js?v=${buildRevisionNumber}"></script>
		    <script type="text/javascript" src="${jsPath}/_assets/global/js/couponBarcode.js?v=${buildRevisionNumber}"></script>
		    <%-- End Adding JS files for coupon barcode--%>
        </c:otherwise>
    </c:choose>
    <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
        <c:choose>
            <c:when test="${(bAsync && QASOnAsync != false) || QASOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}', function(){
                        if (QAS_Variables) {
                            <c:choose>
                                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                    QAS_Variables.DEFAULT_DATA = "CAN";
                                </c:when>
                                <c:otherwise>
                                    QAS_Variables.DEFAULT_DATA = "USA";
                                </c:otherwise>
                            </c:choose>
                            BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                        }
                    });
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript">
                    if(QAS_Variables) {
                        <c:choose>
                            <c:when test="${currentSiteId eq BedBathCanadaSite}">
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
	<c:if test="${fn:indexOf(pageWrapper, 'updateFormPopup') > -1}">
        <script type="text/javascript" src="${jsPath}/_assets/global/js/updateRegistry.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.maskedinput.min.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/bootstrap-formhelpers-phone.min.js?v=${buildRevisionNumber}"></script>
    </c:if>
    <c:if test="${fn:indexOf(pageWrapper, 'giftView') > -1}">
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/easyzoom.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/slick.min.js?v=${buildRevisionNumber}"></script>
		<script type="text/javascript" src="${jsPath}/_assets/global/js/browse.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${jsPath}/_assets/global/js/registryQuickView.js?v=${buildRevisionNumber}"></script>
    </c:if>
	    <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
        <c:choose>
            <c:when test="${(bAsync && QASOnAsync != false) || QASOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}', function(){
                        if (QAS_Variables) {
                            <c:choose>
                                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                    QAS_Variables.DEFAULT_DATA = "CAN";
                                </c:when>
                                <c:otherwise>
                                    QAS_Variables.DEFAULT_DATA = "USA";
                                </c:otherwise>
                            </c:choose>
                            BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                        }
                    });
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript">
                    if(QAS_Variables) {
                        <c:choose>
                            <c:when test="${currentSiteId eq BedBathCanadaSite}">
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
<c:if test="${section == 'createRegistry'}">
    <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_createRegistry_min.js?v=${buildRevisionNumber}"></script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/rAccord_formValidator.js?v=${buildRevisionNumber}"></script>
        </c:otherwise>
    </c:choose>
    <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
        <c:choose>
            <c:when test="${(bAsync && QASOnAsync != false) || QASOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}', function(){
                        if (QAS_Variables) {
                            <c:choose>
                                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                    QAS_Variables.DEFAULT_DATA = "CAN";
                                </c:when>
                                <c:otherwise>
                                    QAS_Variables.DEFAULT_DATA = "USA";
                                </c:otherwise>
                            </c:choose>
                            BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                        }
                    });
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript">
                    if(QAS_Variables) {
                        <c:choose>
                            <c:when test="${currentSiteId eq BedBathCanadaSite}">
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
<c:if test="${section == 'createSimpleRegistry'}">
    <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/global/js/simpleRegistry.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.maskedinput.min.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/bootstrap-formhelpers-phone.min.js?v=${buildRevisionNumber}"></script>
           
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/simpleRegistry.js?v=${buildRevisionNumber}"></script>
             <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/jquery.maskedinput.min.js?v=${buildRevisionNumber}"></script>
             <script type="text/javascript" src="${jsPath}/_assets/global/js/plugins/bootstrap-formhelpers-phone.min.js?v=${buildRevisionNumber}"></script>
        </c:otherwise>
    </c:choose>
    <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
        <c:choose>
            <c:when test="${(bAsync && QASOnAsync != false) || QASOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}', function(){
                        if (QAS_Variables) {
                            <c:choose>
                                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                    QAS_Variables.DEFAULT_DATA = "CAN";
                                </c:when>
                                <c:otherwise>
                                    QAS_Variables.DEFAULT_DATA = "USA";
                                </c:otherwise>
                            </c:choose>
                            BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                        }
                    });
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript">
                    if(QAS_Variables) {
                        <c:choose>
                            <c:when test="${currentSiteId eq BedBathCanadaSite}">
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
<c:if test="${section == 'cart' || section == 'cartDetail'}">
    <script type="text/javascript">
        BBB.browse = BBB.cart || {};
    </script>
    <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_cart_min.js?v=${buildRevisionNumber}"></script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/cart.js?v=${buildRevisionNumber}"></script>
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test="${section == 'checkout'}">
    <script type="text/javascript">
        BBB.browse = BBB.cart || {};
    </script>
    <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/social_share.js?v=${buildRevisionNumber}"></script>
    <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_checkout_min.js?v=${buildRevisionNumber}"></script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/address_book.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/checkout.js?v=${buildRevisionNumber}"></script>
        </c:otherwise>
    </c:choose>
    <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
        <c:choose>
            <c:when test="${(bAsync && QASOnAsync != false) || QASOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}', function(){
                        if (QAS_Variables) {
                            <c:choose>
                                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                    QAS_Variables.DEFAULT_DATA = "CAN";
                                </c:when>
                                <c:otherwise>
                                    QAS_Variables.DEFAULT_DATA = "USA";
                                </c:otherwise>
                            </c:choose>
                            BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                        }
                    });
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript">
                    if(QAS_Variables) {
                        <c:choose>
                            <c:when test="${currentSiteId eq BedBathCanadaSite}">
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
<c:if test="${section == 'singleCheckout'}">
    <script type="text/javascript">
        BBB.browse = BBB.cart || {};
    </script>
    <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/social_share.js?v=${buildRevisionNumber}"></script>
    <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_singlecheckout_min.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/create_fb_connect.js?v=${buildRevisionNumber}"></script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/address_book.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/singleCheckout.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/create_fb_connect.js?v=${buildRevisionNumber}"></script>
        </c:otherwise>
    </c:choose>
    <c:if test="${bExternalJSCSS && bPlugins && QASOn}">
        <c:choose>
            <c:when test="${(bAsync && QASOnAsync != false) || QASOnAsync}">
                <script type="text/javascript">
                    bbbLazyLoader.js('${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}', function(){
                        if (QAS_Variables) {
                            <c:choose>
                                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                    QAS_Variables.DEFAULT_DATA = "CAN";
                                </c:when>
                                <c:otherwise>
                                    QAS_Variables.DEFAULT_DATA = "USA";
                                </c:otherwise>
                            </c:choose>
                            BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};
                        }
                    });
                </script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript" src="${jsPath}/_assets/global/js/qas.js?v=${buildRevisionNumber}"></script>
                <script type="text/javascript">
                    if(QAS_Variables) {
                        <c:choose>
                            <c:when test="${currentSiteId eq BedBathCanadaSite}">
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
<c:if test="${section == 'search'}">
    <script type="text/javascript">
        BBB.search = BBB.search || {};
    </script>
    <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_search_min.js?v=${buildRevisionNumber}"></script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/search.js?v=${buildRevisionNumber}"></script>
        </c:otherwise>
    </c:choose>
</c:if>

<%--### load js file(s) based on applied pageWrapper(s) ###--%>

<%--### LOCAL FILES ###--%>
<c:if test="${fn:indexOf(pageWrapper, 'category') > -1}">
    <script type="text/javascript">
        BBB.browse = BBB.browse || {};
        BBB.browse.category = true;
    </script>
</c:if>
<c:if test="${fn:indexOf(pageWrapper, 'requestBridalBook') > -1 || fn:indexOf(pageWrapper, 'manageRegistry') > -1 || fn:indexOf(pageWrapper, 'registryConfirm') > -1}">
    <script type="text/javascript" src="${jsPath}/_assets/bbregistry/js/registry<c:if test='${minifiedJSFlag}'>.min</c:if>.js?v=${buildRevisionNumber}"></script>
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
<c:if test="${fn:indexOf(pageWrapper, 'subCategory') > -1}">
    <script type="text/javascript">
        BBB.browse = BBB.browse || {};
        BBB.browse.subCategory = true;
        BBB.search = BBB.search || {};
    </script>
    <c:choose>
        <c:when test="${minifiedJSFlag}">
            <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_search_min.js?v=${buildRevisionNumber}"></script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/search.js?v=${buildRevisionNumber}"></script>
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test="${fn:indexOf(pageWrapper, 'login') > -1}">
    <script type="text/javascript">
        BBB.accountManagement.login = true;
    </script>
</c:if>




<%--### THIRD PARTY FILES ###--%>
<c:if test="${fn:indexOf(pageWrapper, 'useScene7') > -1 && bExternalJSCSS && bPlugins && SceneSevenOn}">
	<%-- <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/scene7${min}.css?v=${buildRevisionNumber}" property="stylesheet"/> --%>
</c:if>
<c:if test="${fn:indexOf(pageWrapper, 'useScene7') > -1 && bExternalJSCSS && bPlugins && SceneSevenOn}">
	<%-- Don't need this with scene7 replacement
	<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/global/css/scene7${min}.css?v=${buildRevisionNumber}" property="stylesheet"/>
    <c:set var="s7sdk_utils_js_url"><bbbc:config key="s7sdk_utils_js_url" configName="ThirdPartyURLs" /></c:set>
    <script type="text/javascript">
        (function ($, window, document, undefined) {
            BBB.s7sdkUtilLoader = function(callback) {
                bbbLazyLoader.js("${jsPath}/_assets/global/js/extensions/scene7${min}.js?v=${buildRevisionNumber}", "${REQURLPROTOCOL}:${s7sdk_utils_js_url}", function(){
                    BBB.s7sdkUtilInit(callback);
                });
            };
            BBB.s7sdkUtilInit = function(callback) {
                if (typeof s7sdk !== 'undefined') {
                    s7sdk.Util.lib.include('s7sdk.image.FlyoutZoomView');
                    s7sdk.Util.lib.include('s7sdk.image.FlyoutView');
                    s7sdk.Util.lib.include('s7sdk.set.MediaSet');
                    s7sdk.Util.lib.include('s7sdk.set.Swatches');
                    s7sdk.Util.lib.include('s7sdk.common.Container');
                    s7sdk.Util.lib.include('s7sdk.common.Thumb');

                    // blocked s7 sdk from loading any style sheet as it was causing js errors
                    s7sdk.Util.css.addDefaultCSS = function() {};
                    s7sdk.Util.css.add = function() {};
                    s7sdk.Util.css.include = function() {};

                    // initialize s7 sdk
                    s7sdk.Util.init();

                    (function isAllJSLoaded() {
                        if (!s7sdk.Util.lib.isAllJSLoaded()) {
                            setTimeout(isAllJSLoaded, 100);
                        } else {
                            if (typeof callback === 'function') {
                                BBB.bbbS7.fLoad({
                                    bInit: true,
                                    bTriggerInit: true,
                                    fCallback: callback
                                });
                            } else if (typeof BBB.bbbS7 !== 'undefined' && typeof BBB.bbbS7.fInit === 'function') {
                                BBB.bbbS7.fLoad({ bInit: true });
                            }
                        }
                    }());
                }
            };
        }(jQuery, this, this.document));
    </script>
	--%>


    <c:if test="${fn:indexOf(pageWrapper, 'productDetails') > -1}">
    	<%-- Don't need this with scene7 replacement
        <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/scene7${min}.js?v=${buildRevisionNumber}"></script>
        <script type="text/javascript" src="${REQURLPROTOCOL}:${s7sdk_utils_js_url}"></script>
        <script type="text/javascript">
            (function ($, window, document, undefined) {
                BBB.s7sdkUtilInit();
            }(jQuery, this, this.document));
        </script>
        --%>
        
    </c:if>
</c:if>
<dsp:droplet name="/atg/dynamo/droplet/ForEach">
	<dsp:param name="array" bean="/atg/userprofiling/Profile.userSiteItems"/>
    <dsp:param name="elementName" value="sites"/>
    <dsp:oparam name="output">
    	<dsp:getvalueof id="key" param="key"/>
		<c:if test="${currentSiteId eq key}">
			<dsp:getvalueof var="favouriteStoreId" param="sites.favouriteStoreId"/>
		</c:if>
	</dsp:oparam>
</dsp:droplet>

<c:set var="radius_default_us"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set>
<c:set var="radius_default_baby"><bbbc:config key="radius_default_baby" configName="MapQuestStoreType" /></c:set>
<c:choose>
	<c:when test="${currentSiteId eq BedBathUSSite}">
		<c:set var="radius_default_selected">${radius_default_us}</c:set>
	</c:when>
	<c:when test="${currentSiteId eq BuyBuyBabySite}">
		<c:set var="radius_default_selected">${radius_default_baby}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="radius_default_selected">${radius_default_us}</c:set>
	</c:otherwise>
</c:choose>
<c:if test="${bExternalJSCSS && bPlugins && LoadMQALibOn}">

    <script type="text/javascript">
        function mapQuestStoreLocatorInitialize() {
            if($("#pageWrapper").hasClass("college")) {
                 var options = {
                    radius:'${not empty radius_default_selected ? radius_default_selected : 25}'
                    ,favoriteStoreID : '${favouriteStoreId}'
                    ,conceptID: '${currentSiteId}'
                    ,enableFavoriteStores : ${not empty enableFavoriteStores ? enableFavoriteStores : false}
                    ,debug: false
                    ,useAlerts: true
                    , usePrintPage:true
                    , useScrollShadows: true
                };
                BBB.BBBStoreLocator.init(options);
                BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden
            }
            else if($("#pageWrapper").hasClass("findAStorePage")) {
                var options = {
                    radius:'${not empty radius_default_selected ? radius_default_selected : 25}'
                    ,favoriteStoreID : '${favouriteStoreId}'
                    ,conceptID: '${currentSiteId}'
                    ,enableFavoriteStores : ${not empty enableFavoriteStores ? enableFavoriteStores : false}
                    ,debug: false
                    ,useAlerts: true
                    , usePrintPage:true
                    , useScrollShadows: true
                };

                BBB.BBBStoreLocator.init(options);
                BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden
                 if($("#pageWrapper").hasClass("findAStorePage")) {
                    $('#FindStoreBTN').removeClass('disabled').addClass('button_active').find('input').removeAttr('disabled');
                 }
            } else if ($("#pageWrapper").hasClass("productDetails") || $("#pageWrapper").hasClass("subCategory") || $("#pageWrapper").hasClass("searchResults")) {
                   var options = {
                        radius:'${radius_default_selected}',
                        //,favoriteStoreID : '${favouriteStoreId}'
                        conceptID: '${currentSiteId}',
                        enableFavoriteStores : false,
                        debug: false,
                        usePrintPage: true
                    };
                        BBB.BBBStoreLocator.init(options);
                        //BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden
                    var GetDirectionsV2Dialog = $("#GetDirectionsV2Wrapper").removeClass('hidden').dialog({
                        autoOpen: false,
                        modal: true,
                        resizable: false,
                        draggable: false,
                        width: 856,
                        show: {effect: 'fade', duration: 250},
                        hide: {effect: 'fade', duration: 250},
                        //buttons: btnsObj,
                        //dialogClass: settings.dialogClass,
                        open: function () {
                            if( $('#changeStoreDialogWrapper' ).dialog( "isOpen" ) === true)
                            {
                                //$(this).data('changeStoreWasOpen',true);
                                $('.changeStoreDialog.ui-dialog').fadeOut(250); //.addClass('hidden');
                                //$('#changeStoreDialogWrapper').dialog('close');
                            }
                        },
                        close: function () {
                            if($('#changeStoreDialogWrapper' ).dialog( "isOpen" ))
                            {
                                $('.changeStoreDialog.ui-dialog').fadeIn(250);//.removeClass('hidden');
                                //$(this).data('changeStoreWasOpen',false);
                            }
                            //_this.closeDialog(this);
                            //$('#changeStoreDialogWrapper').dialog('open');
                        }
                    });
            } else if ($("#pageWrapper").hasClass("estorePage")) {
                    var options = {
                    radius:25,
                    //favoriteStoreID : '1102',
                    conceptID: 'BedBathUS',
                    enableFavoriteStores: false,
                    debug:false,
                    //origin: '90210',
                    //runSearchOnLoad: true
                    //imgBasePath: '/images/icons/',
                    usePrintPage:true
                    //printPage: 'map_print.html'
                    };
                    BBB.BBBStoreLocator.init(options);
                    BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden
            } else if ($("#pageWrapper").hasClass("noStoreLocatorCss")) {
                var origin, destination, options;
                    origin = decodeURIComponent($.urlParam('origin'));
                    destination = decodeURIComponent($.urlParam('destination'));

                    options = {
                            conceptID: '${currentSiteId}',
                            debug:false
                    };

                    BBB.BBBStoreLocator.init(options);
                    BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden
                    BBB.BBBStoreLocator.loadMapDirections(origin, destination,{});

                    //only load static map and open the print dialog after the map directions have finished loading
                $('body').on('storeLocator.directionsLoaded', function() {
                                $('#storeLocatorMapResults').hide();
                                $('#storeLocatorStaticMapResults').attr('src', BBB.BBBStoreLocator.getStaticMapUrl);
                                window.print();
                });
            } else if($("#pageWrapper").hasClass("useStoreLocator")) {
                var options = {
                        radius: '${not empty radius_default_selected ? radius_default_selected : 25}',
                        //favoriteStoreID : '1230',
                        conceptID: '${currentSiteId}',
                        enableFavoriteStores : ${not empty enableFavoriteStores ? enableFavoriteStores : false},
                        debug: false,
                        usePrintPage: true
                    };

                BBB.BBBStoreLocator.init(options);
                //BBB.BBBStoreLocator.initMap(); //loads the maps from MQ, but keeps them hidden

                var GetDirectionsV2Dialog = $("#GetDirectionsV2Wrapper").removeClass('hidden').dialog({
                    autoOpen: false,
                    modal: true,
                    resizable: false,
                    draggable: false,
                    width: 856,
                    show: {effect: 'fade', duration: 250},
                    hide: {effect: 'fade', duration: 250},
                    //buttons: btnsObj,
                    //dialogClass: settings.dialogClass,
                    open: function () {
                    	if( $('#changeStoreDialogWrapper' ).dialog( "isOpen" ) === true)
                    	{
                    		//$(this).data('changeStoreWasOpen',true);
                    		$('.changeStoreDialog.ui-dialog').fadeOut(250); //.addClass('hidden');
                    		//$('#changeStoreDialogWrapper').dialog('close');
                    	}
                    },
                    close: function () {
                    	if($('#changeStoreDialogWrapper' ).dialog( "isOpen" ))
                    	{
                    		$('.changeStoreDialog.ui-dialog').fadeIn(250);//.removeClass('hidden');
                    		//$(this).data('changeStoreWasOpen',false);
                    	}
                        //_this.closeDialog(this);
                    	//$('#changeStoreDialogWrapper').dialog('open');
                    }
                });
            }
			BBB.fn.initMQ = function() {
            if (typeof MQA !== 'undefined') {
                try {
                    if ($('#directionsDialogWrapper')[0]) {
                        if (typeof BBB.fn.storeLocator !== 'undefined') {
                            BBB.fn.storeLocator.initMapquestStoreFinder();
                        }
                    }
                } catch (e) {}
            } else {
                $('.viewMapModal, .viewDirectionsNew, .viewMap').hide();
            }
        };
			BBB.fn.initMQ();
        }
        /*bbbLazyLoader.js('<bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" />', function() {
            bbbLazyLoader.js('${jsPath}/_assets/global/js/extensions/store_locator_v2${min}.js?v=${buildRevisionNumber}', function() {
                if($('html').hasClass('bedBathCA')) {
                    bbbLazyLoader.js('${jsPath}/_assets/global/js/extensions/store_locator.js?v=${buildRevisionNumber}', function() {
                        mapQuestStoreLocatorInitialize();
                    });
                } else{
                    if ($('html').hasClass('ie')) {
						setTimeout( function(){
							mapQuestStoreLocatorInitialize();
						},6000);
                    } else {
                        mapQuestStoreLocatorInitialize();
                }
                }
            });
        });*/
        BBB.fn.loadStoreLocatorLibrary = function () {
            var mapQuestUSUrl = '${jsPath}/_assets/global/js/extensions/store_locator_v2${min}.js?v=${buildRevisionNumber}',
                mapQuestCAUrl = '${jsPath}/_assets/global/js/extensions/store_locator.js?v=${buildRevisionNumber}',
                mapQuestThirdPartyUrl = '<bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" />';

            bbbLazyLoader.js(mapQuestThirdPartyUrl, function() {
                bbbLazyLoader.js(mapQuestUSUrl, function() {
                    if($('html').hasClass('bedBathCA')) {
                        bbbLazyLoader.js(mapQuestCAUrl, function() {
                            mapQuestStoreLocatorInitialize();
                        });
                    } else{
                        if ($('html').hasClass('ie')) {
                            setTimeout( function(){
                                mapQuestStoreLocatorInitialize();
                            },6000);
                        } else {
                            mapQuestStoreLocatorInitialize();
                    	}
                    }
                });
            });
        };

        var loadStoreLocatorFlag = false;
        if($("#pageWrapper").hasClass("college")
            || $("#pageWrapper").hasClass("findAStorePage")
            || $("#pageWrapper").hasClass("productDetails")
            || $("#pageWrapper").hasClass("subCategory")
            || $("#pageWrapper").hasClass("searchResults")
            || $("#pageWrapper").hasClass("estorePage")
            || $("#pageWrapper").hasClass("noStoreLocatorCss")
            || $("#pageWrapper").hasClass("useStoreLocator")) {
            loadStoreLocatorFlag = true;
        }

        if (loadStoreLocatorFlag) {
        	if (window.attachEvent) {
    	        window.attachEvent('onload', BBB.fn.loadStoreLocatorLibrary);
    	    }
    	    else {
    	        window.addEventListener('load', BBB.fn.loadStoreLocatorLibrary, false);
    	    }
        }
    </script>
</c:if>
<%--
<c:if test="${fn:indexOf(pageWrapper, 'useMapQuest') > -1 && bExternalJSCSS && bPlugins && LoadMQALibOn}">
    <c:choose>
        <c:when test="${(bAsync && MapQuestOnAsync != false) || MapQuestOnAsync}">
            <script type="text/javascript">
                <c:choose>
                    <c:when test="${minifiedJSFlag}">
                        <c:choose>
                            <c:when test="${fn:indexOf(pageWrapper, 'productDetails') > -1}">
                                bbbLazyLoader.js(
                                    '${jsPath}/_assets/global/js/extensions/initiate_map_quest_view_map_dialog.js?v=${buildRevisionNumber}',
                                    '${jsPath}/_assets/global/js/extensions/get_map_quest_directions.js?v=${buildRevisionNumber}'
                                );
                            </c:when>
                            <c:otherwise>
                                bbbLazyLoader.js('<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:otherwise></c:choose>',
                                    '${jsPath}/_assets/global/js/bbb_mapQuest_min.js?v=${buildRevisionNumber}',
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
                                    '${jsPath}/_assets/global/js/extensions/initiate_map_quest_view_map_dialog.js?v=${buildRevisionNumber}',
                                    '${jsPath}/_assets/global/js/extensions/get_map_quest_directions.js?v=${buildRevisionNumber}'
                                );
                            </c:when>
                            <c:otherwise>
                                bbbLazyLoader.js('<c:choose><c:when test="${pageSecured}"><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:when><c:otherwise><bbbc:config key="js_mapQuest" configName="ThirdPartyURLs" /></c:otherwise></c:choose>',
                                    '${jsPath}/_assets/global/js/extensions/initiate_map_quest_view_map_dialog.js?v=${buildRevisionNumber}',
                                    '${jsPath}/_assets/global/js/extensions/get_map_quest_directions.js?v=${buildRevisionNumber}',
                                    '${jsPath}/_assets/global/js/extensions/store_locator.js?v=${buildRevisionNumber}',
                                    '${jsPath}/_assets/global/js/extensions/load_map_quest_map.js?v=${buildRevisionNumber}',
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
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/bbb_mapQuest_min.js?v=${buildRevisionNumber}"></script>
                </c:when>
                <c:otherwise>
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/store_locator.js?v=${buildRevisionNumber}"></script>
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/initiate_map_quest_view_map_dialog.js?v=${buildRevisionNumber}"></script>
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/get_map_quest_directions.js?v=${buildRevisionNumber}"></script>
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/extensions/load_map_quest_map.js?v=${buildRevisionNumber}"></script>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</c:if>
--%>
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
		<%--Using Pie Campaign ${usePieCampaignId}--%>
		<dsp:getvalueof var="usePieCampaignId" value="false" scope="session"/>
		<dsp:getvalueof var="campaignId" param="pieCampaignId" />
    </c:when>
    <c:otherwise>
		<%--Using page Campaign ${usePieCampaignId}--%>
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

<%--Compaing Id value is ${campaignId}--%>

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
                        <c:when test="${currentSiteId eq BedBathUSSite}">
                            '<bbbc:config key="js_BazaarVoice_us" configName="ThirdPartyURLs" />',
                        </c:when>
                        <c:when test="${currentSiteId eq BuyBuyBabySite}">
                            '<bbbc:config key="js_BazaarVoice_baby" configName="ThirdPartyURLs" />',
                        </c:when>
                        <c:otherwise>
                            '<bbbc:config key="js_BazaarVoice_ca" configName="ThirdPartyURLs" />',
                        </c:otherwise>
                    </c:choose>
                    '${jsPath}/_assets/global/js/extensions/bazaarvoice${min}.js'
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
                <c:when test="${currentSiteId eq BedBathUSSite}">
                    <script type="text/javascript" src='<bbbc:config key="js_BazaarVoice_us" configName="ThirdPartyURLs" />'></script>
                </c:when>
                <c:when test="${currentSiteId eq BuyBuyBabySite}">
                    <script type="text/javascript" src='<bbbc:config key="js_BazaarVoice_baby" configName="ThirdPartyURLs" />'></script>
                </c:when>
                <c:otherwise>
                    <script type="text/javascript" src='<bbbc:config key="js_BazaarVoice_ca" configName="ThirdPartyURLs" />'></script>
                </c:otherwise>
            </c:choose>
            <script type="text/javascript" src='${jsPath}/_assets/global/js/extensions/bazaarvoice${min}.js'></script>
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
            	function loadBannerJS() {
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
            	}
                
                if (window.attachEvent) {
        	        window.attachEvent('onload', loadBannerJS);
        	    }
        	    else {
        	        window.addEventListener('load', loadBannerJS, false);
        	    }
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
<%--TeaLeaf Method --%>
<%-- TeaLeaf Method --%>
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
</script>

<c:if test="${bExternalJSCSS && bAnalytics && GoogleAnalyticsOn}">
    <c:set var="googleSecuredJsURL"><bbbc:config key="google_secured_js_url" configName="ThirdPartyURLs" /></c:set>
    <c:set var="googleJsURL"><bbbc:config key="google_js_url" configName="ThirdPartyURLs" /></c:set>
    <c:choose>
        <c:when test="${currentSiteId eq BedBathUSSite}">
            <c:set var="GAAccountId"><bbbc:config key="googleAnalyticsId_US" configName="ThirdPartyURLs" /></c:set>
        </c:when>
        <c:when test="${currentSiteId eq BuyBuyBabySite}">
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
        <%-- WEB-279 --%>
        <%-- // need this on order confirmation page only --%>
        <c:if test="${fn:indexOf(pageWrapper, 'chekoutConfirm') > -1}">
            <%-- // client did not want to capture any price/tax/etc related info ... thus setting them all to "0" --%>
            if (typeof BBB !== "undefined" && typeof BBB.gaTransData !== "undefined" && typeof BBB.gaTransData.items !== "undefined" && typeof BBB.gaTransData.transaction !== "undefined") {
                _gaq.push(['_addTrans',
                   BBB.gaTransData.transaction[0],      // transaction ID - required
                   BBB.currentSiteName,                 // affiliation or store name
                   BBB.gaTransData.transaction[1],      // total - required
                   BBB.gaTransData.transaction[2],      // tax
                   BBB.gaTransData.transaction[3],      // shipping
                   "",                                  // city
                   "",                                  // state or province
                   BBB.config.country.shortCode         // country
                ]);
                for (var i = 0, j = BBB.gaTransData.items.length; i < j; i ++) {
                    _gaq.push(['_addItem',
                       BBB.gaTransData.transaction[0],      // transaction ID - necessary to associate item with transaction
                       BBB.gaTransData.items[i][0],         // SKU/code - required
                       BBB.gaTransData.items[i][1],         // product name
                       "",                                  // category or variation
                       "0",                                 // unit price - required
                       BBB.gaTransData.items[i][2]          // quantity - required
                    ]);
                }
                _gaq.push(['_trackTrans']);
            }
        </c:if>
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
        <c:when test="${currentSiteId eq BedBathUSSite}">
            <c:set var="OnmiFile1" value="${jsPath}/_assets/global/js/thirdparty/omniture/bbb_s_code${min}.js?v=${buildRevisionNumber}" scope="request" />
        </c:when>
        <c:when test="${currentSiteId eq BuyBuyBabySite}">
            <c:set var="OnmiFile1" value="${jsPath}/_assets/global/js/thirdparty/omniture/buybuybaby_s_code.js?v=${buildRevisionNumber}" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="OnmiFile1" value="${jsPath}/_assets/global/js/thirdparty/omniture/bbbca_s_code.js?v=${buildRevisionNumber}" scope="request" />
        </c:otherwise>
    </c:choose>
    <c:set var="OnmiFile2" value="${jsPath}/_assets/global/js/thirdparty/omniture/omniture_tracking${min}.js?v=${buildRevisionNumber}" scope="request" />

    <script type="text/javascript">
        var BBB = BBB || {};
        BBB.omnitureObj = {
            s_values_backup: {},
            backup_s: function (sKeys) {
                if (!BBB.omnitureObj.has_s()) { return; }
                if (typeof sKeys === "string" && sKeys.trim() !== "") {
                    $.each(sKeys.split(","), function(index, key, value) {
                        if (typeof s[key] !== "undefined") {
                            value = s[key];
                            if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
                                BBB.omnitureObj.s_values_backup[key] = value;
                            }
                        }
                    });
                } else {
                    $.each(s, function(key, value) {
                        if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
                            BBB.omnitureObj.s_values_backup[key] = value;
                        }
                    });
                }
            },
            delete_s: function (sKeys) {
                if (!BBB.omnitureObj.has_s()) { return; }
                if (typeof sKeys === "string" && sKeys.trim() !== "") {
                    $.each(sKeys.split(","), function(index, key, value) {
                        if (typeof s[key] !== "undefined") {
                            value = s[key];
                            if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
                                delete s[key];
                            }
                        }
                    });
                } else {
                    $.each(s, function(key, value) {
                        if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
                            delete s[key];
                        }
                    });
                }
            },
            clear_s: function (sKeys) {
                if (!BBB.omnitureObj.has_s()) { return; }
                if (typeof sKeys === "string" && sKeys.trim() !== "") {
                    $.each(sKeys.split(","), function(index, key, value) {
                        if (typeof s[key] !== "undefined") {
                            value = s[key];
                            if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
                                s[key] = "";
                            }
                        }
                    });
                } else {
                    $.each(s, function(key, value) {
                        if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
                            s[key] = "";
                        }
                    });
                }
            },
            restore_s: function (sKeys) {
                if (!BBB.omnitureObj.has_s()) { return; }
                if (typeof sKeys === "string" && sKeys.trim() !== "") {
                    $.each(sKeys.split(","), function(index, key, value) {
                        if (typeof BBB.omnitureObj.s_values_backup[key] !== "undefined") {
                            s[key] = BBB.omnitureObj.s_values_backup[key];
                        }
                    });
                } else {
                    $.each(BBB.omnitureObj.s_values_backup, function(key, value) {
                        s[key] = value;
                    });
                }
            },
            has_s: function () {
                return (typeof s !== "undefined");
            }
        };
        /*****************************/

        function fixOmniSpacing() {
            var clean = function(arg) { return ((typeof arg === 'string' && arg.trim() !== '')? arg.replace(/(&amp;|&#38;|&#038;)/g,'&').replace(/[\s]{0,}(>|&gt;)[\s]{0,}/g,'>').replace(/(&#39;|&#34;|&#039;|&#034;|&quot;)/g,'').replace(/&nbsp;/g,' ').replace(/\s+/g,' ') : ''); };
            if (typeof s !== "undefined") {
                if (typeof s.pageName === 'string') { s.pageName = clean(s.pageName); }
                if (typeof s.prop2 === 'string') { s.prop2 = clean(s.prop2); }
                if (typeof s.prop3 === 'string') { s.prop3 = clean(s.prop3); }
                if (typeof s.prop4 === 'string') { s.prop4 = clean(s.prop4); }
                if (typeof s.prop5 === 'string') { s.prop5 = clean(s.prop5); }
                if (typeof s.eVar5 === 'string') { s.eVar5 = clean(s.eVar5); }
                if (typeof s.eVar6 === 'string') { s.eVar6 = clean(s.eVar6); }
            }
        }
    </script>

    <c:choose>
        <c:when test="${(bAsync && OmnitureOnAsync != false) || OmnitureOnAsync}">
            <script type="text/javascript">
                bbbLazyLoader.js('${OnmiFile1}', '${OnmiFile2}');
            </script>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${currentSiteId eq BedBathUSSite}">
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/omniture/bbb_s_code${min}.js?v=${buildRevisionNumber}"></script>
                </c:when>
                <c:when test="${currentSiteId eq BuyBuyBabySite}">
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/omniture/buybuybaby_s_code.js?v=${buildRevisionNumber}"></script>
                </c:when>
                <c:otherwise>
                    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/omniture/bbbca_s_code.js?v=${buildRevisionNumber}"></script>
                </c:otherwise>
            </c:choose>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/omniture/omniture_tracking${min}.js?v=${buildRevisionNumber}"></script>
            <c:if test="${fn:indexOf(pageWrapper, 'productDetails') > -1}">
                <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/omniture/lc_omniture.js?v=${buildRevisionNumber}"></script>
            </c:if>
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test="${bExternalJSCSS && bAnalytics && RKGOn}">
    <c:set var="merchantId">
        <bbbc:config key="${currentSiteId}" configName="RKGKeys" />
    </c:set>
    <%-- must NOT be loaded async as it will break the entire site, because this lib uses document.write[deprecated] and it does not work on async loads. --%>
    <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/rkg/rkg_tracking.js?v=${buildRevisionNumber}"></script>
    <script type="text/javascript">
        try {
            rkg_track_sid('${merchantId}');
        } catch (e) { }
    </script>
</c:if>
<c:if test="${bExternalJSCSS && bAnalytics && ForeseeOn}">
    <c:choose>
        <c:when test="${(bAsync && ForeseeOnAsync != false) || ForeseeOnAsync}">
            <script type="text/javascript">
                bbbLazyLoader.js('${jsPath}/_assets/global/js/thirdparty/foresee/${currentSiteId}/foresee-alive.js?v=${buildRevisionNumber}', '${jsPath}/_assets/global/js/thirdparty/foresee/${currentSiteId}/foresee-trigger${min}.js?v=${buildRevisionNumber}');
            </script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/foresee/${currentSiteId}/foresee-alive.js?v=${buildRevisionNumber}"></script>
            <script type="text/javascript" src="${jsPath}/_assets/global/js/thirdparty/foresee/${currentSiteId}/foresee-trigger${min}.js?v=${buildRevisionNumber}"></script>
        </c:otherwise>
    </c:choose>
    <c:if test="${TeaLeafOn}">
        <%--Setting tealeaf cookie in foresee --%>
        <script type="text/javascript">
            var tltSID = TLGetCookie("TLTSID");
            if (typeof FSR !== 'undefined') {
                try { FSR.CPPS.set('TLTSID',tltSID); } catch(e) {}
            }
        </script>
    </c:if>
</c:if>
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
        /*window.fbWWWNotAvailable = function() {
            if (console && console.log) { console.log('fbWWWNotAvailable... unable to access www.facebook.com'); }
        };
        window.fbAsyncInit = function() {
            $('body').append('<img style="display:none;visibility:hidden;" height="1" width="1" alt="" border="0" src="https://www.facebook.com/tr/?_='+(''+Math.random()).substring(2)+'" onload="(function(x){$(x).remove();window.fbWWWAvailable();})(this);" onerror="(function(x){$(x).remove();window.fbWWWNotAvailable();})(this);" onabort="(function(x){$(x).remove();window.fbWWWNotAvailable();})(this);" />');
        };
        window.fbWWWAvailable = function() {*/
        window.fbAsyncInit = function() {
            FB.init({
                appId: '${fbConfigMap[currentSiteId]}',
                status: true,
                cookie: true,
                xfbml: true,
                oauth: true,
				version: 'v2.6',
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
<dsp:getvalueof var="profileID" bean="/atg/userprofiling/Profile.id" />
<dsp:getvalueof var="profileTransient" bean="/atg/userprofiling/Profile.transient"/>
<input type="hidden" id="profileId" value="${profileID}" />
<c:choose>
<c:when test="${profileTransient}">
	<input type="hidden" id="profileType" value="non registered user" />
</c:when>
<c:otherwise>
	<dsp:getvalueof var ="securityStatus" bean="/atg/userprofiling/Profile.securityStatus"/>
	<c:choose>
		<c:when test="${securityStatus eq '2' }">
			<input type="hidden" id="profileType" value="recognized user" />
		</c:when>
		<c:otherwise>
			<input type="hidden" id="profileType" value="registered user" />
		</c:otherwise>
	</c:choose>
</c:otherwise>
</c:choose>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" bean="/atg/userprofiling/Profile.transient" />
    <dsp:oparam name="false">
    <script type="text/javascript">
		var recognizedUserFlag = "";
		if($('#recognizedUserFlag').val() != null)
		{
			recognizedUserFlag = $('#recognizedUserFlag').val();

		}
        if(typeof s !=='undefined') {
            s.prop23 = '${profileID}';
            s.eVar38 ='${profileID}';
			if(recognizedUserFlag){
				s.prop16= 'recognized user';
				s.eVar16= 'recognized user';
			} else{
            s.prop16= 'registered user';
            s.eVar16= 'registered user';
        }
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

<dsp:getvalueof var="couponCount" bean="SessionBean.couponCount" />

<input type="hidden" id="address2ForQAS" value=""/>
<script type="text/javascript">
    BBB.userCouponCount = '${couponCount}';
</script>

<c:set var="helixWebChatFlag"><bbbc:config key="helixWebChatFlag" configName="FlagDrivenFunctions" /></c:set>
<c:set var="chat_helixStaticJs"><bbbc:config key="chat_helixStaticJs" configName="ThirdPartyURLs" /></c:set>
<c:set var="chat_helixCustHelp"><bbbc:config key="chat_helixCustHelp" configName="ThirdPartyURLs" /></c:set>
<c:set var="chat_helixLauncherJS"><bbbc:config key="chat_helixLauncherJS" configName="ThirdPartyURLs" /></c:set>
<c:set var="chat_helixCustHelp_vsoptsJS"><bbbc:config key="chat_helixCustHelp_vsoptsJS" configName="ThirdPartyURLs" /></c:set>
<c:set var="chat_helixEEID"><bbbc:config key="helix_ee_id" configName="ThirdPartyURLs" /></c:set>

<script>
	(function() {
    var isHelixChatOn = '${helixWebChatFlag}' === 'true' || document.body.className.indexOf('helixChatOn') > -1;
    if (isHelixChatOn) {
      bbbLazyLoader.js('${chat_helixStaticJs}', function() {
        ATGSvcs.setEEID('${chat_helixEEID}');
        bbbLazyLoader.js('${chat_helixCustHelp}', '${chat_helixCustHelp_vsoptsJS}', function() {
          bbbLazyLoader.js('${chat_helixLauncherJS}', function() {
            console.log('Helix Chat loaded.');      
          });
        });
      });
    }
  }());
</script>