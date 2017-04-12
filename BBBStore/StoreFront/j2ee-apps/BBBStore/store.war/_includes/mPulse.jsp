<dsp:page>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="currentSiteId" bean="Site.id" />
<dsp:getvalueof var="section" param="section"/>
<dsp:getvalueof var="BedBathUSSite" param="BedBathUSSite"/>
<dsp:getvalueof var="BuyBuyBabySite" param="BuyBuyBabySite"/>
<dsp:getvalueof var="BedBathCanadaSite" param="BedBathCanadaSite"/>
<dsp:getvalueof var="PageType" param="PageType"/>
<c:choose>
<c:when test="${(currentSiteId eq BedBathUSSite)}">
<c:set var="key" scope="request"><tpsw:switch tagName="mPulse_us_key"/></c:set>
<c:if test="${empty key}">
<c:set var="key" value="TT7XT-4RL2W-95HVG-RH9XQ-SRWPF"/>
</c:if>
</c:when>
<c:when test="${(currentSiteId eq BuyBuyBabySite)}">
<c:set var="key" scope="request"><tpsw:switch tagName="mPulse_baby_key"/></c:set>
<c:if test="${empty key}">
<c:set var="key" value="PATE6-7KXFR-8NPRC-JR59S-38RGP"/>
</c:if>
</c:when>
<c:when test="${(currentSiteId eq BedBathCanadaSite)}">
<c:set var="key" scope="request"><tpsw:switch tagName="mPulse_ca_key"/></c:set>
<c:if test="${empty key}">
<c:set var="key" value="PY69M-XZSZ7-FE95U-M2CVZ-2XHRD"/>
</c:if>
</c:when>
</c:choose>
<c:if test="${mPulseON}">
<c:set var="keywordBoostFlag"><bbbc:config key="KeywordBoostFlag" configName="FlagDrivenFunctions"/></c:set>
<c:set var="l2l3BoostFlag"><bbbc:config key="L2L3BoostFlag" configName="FlagDrivenFunctions"/></c:set> 
<c:set var="brandsBoostFlag"><bbbc:config key="BrandsBoostFlag" configName="FlagDrivenFunctions"/></c:set>
	<c:choose>
 		<c:when test="${sessionScope.VendorParam ne null}">
 			<c:set var="vendorName" value="${sessionScope.VendorParam}_SearchVendor_Sitespect"/>
 			<c:set var="mPulseTaggingVariable"><bbbc:config key="${vendorName}" configName="VendorKeys" /></c:set>
 		</c:when>
 		<c:when test="${sessionScope.boostCode ne null && sessionScope.boostCode ne '00'}">
 		
 			<c:choose>
 				<c:when test="${keywordBoostFlag || brandsBoostFlag || l2l3BoostFlag}">
 					<c:set var="mPulseTaggingVariable" value="En_custom"/>
 				</c:when>
 				<c:otherwise>
 					<c:set var="mPulseTaggingVariable" value="En"/>
 				</c:otherwise>
 			</c:choose>
 			
 		</c:when>
 		<c:otherwise>
 			<c:set var="mPulseTaggingVariable" value="En"/>
 		</c:otherwise>
 </c:choose>
<script type="text/javascript">
<%-- /* ###### BBBSL-3257 ###### */ --%>
<%-- /* NOTE: var SS_DC is declared outside of function scope deliberately, 
so that this variable can get added into global window scope */ --%>
var SS_DC,
	SS_EN;
(function(){
    var dcPrefix = '<dsp:valueof bean="/atg/dynamo/service/IdGenerator.dcPrefix"/>'.toUpperCase().replace(/\s{0,}/g,'');
    if (dcPrefix !== '') {
        SS_DC = (dcPrefix === 'DC1' ? 'AN' : (dcPrefix === 'DC2' ? 'SD' : dcPrefix));
    }
   		/* introduced for BBBI:3815 */
    	SS_EN =  '${mPulseTaggingVariable}'; 
}());

//
//Additional mPule Configuration
//
window.BOOMR_config = {
    //Enable Javascript Error Support
    Errors: {
       enabled: true,
       monitorTimeout: false
    },
    //Enable Big Beacon Third Party Analytics
    TPAnalytics: {
       enabled: true
    }
};

<c:if test="${section == 'singleCheckout'}">
	window.BOOMR_config = { instrument_xhr: true };
</c:if>

//
// Resource Timing - Set Count to 350
//
(function(w){
    if (!w ||
        !("performance" in w) ||
        !w.performance ||
        !w.performance.setResourceTimingBufferSize) {
        return;
    }

    w.performance.setResourceTimingBufferSize(350);
})(window);


//
//Clear Resource Timing Buffer
//
(function(w){
    if (!w ||
        !("performance" in w) ||
        !w.performance ||
        !w.performance.clearResourceTimings) {
        return;
    }

    document.addEventListener("onBoomerangBeacon", w.performance.clearResourceTimings.bind(w.performance));
})(window);


(function(){
//
//Critical for capturing page group - PLEASE CONFIRM THIS IS CORRECT
//
	SOASTA = {}; 
	link = document.location.pathname.split('/')[2];
	if (link === ""){
		SOASTA.pg = "Home";
	} else {
		SOASTA.pg = link;
	}
	//alert(SOASTA.pg);
	
	if(typeof BOOMR !== "undefined") {
	BOOMR.addVar("h.pg", SOASTA.pg);
	}	

	
  if (window.BOOMR && window.BOOMR.version) { return; }
  var dom,doc,where,iframe = document.createElement("iframe"),win = window;

  function boomerangSaveLoadTime(e) {
    win.BOOMR_onload=(e && e.timeStamp) || new Date().getTime();
	}
  if (win.addEventListener) {
    win.addEventListener("load", boomerangSaveLoadTime, false);
  } else if (win.attachEvent) {
    win.attachEvent("onload", boomerangSaveLoadTime);
  }

  iframe.src = "javascript:void(0)";
  iframe.title = ""; iframe.role = "presentation";
  (iframe.frameElement || iframe).style.cssText = "width:0;height:0;border:0;display:none;";
  where = document.getElementsByTagName("script")[0];
  where.parentNode.insertBefore(iframe, where);

  try {
    doc = iframe.contentWindow.document;
  } catch(e) {
    dom = document.domain;
    iframe.src="javascript:var d=document.open();d.domain='"+dom+"';void(0);";
    doc = iframe.contentWindow.document;
  }
  doc.open()._l = function() {
    var js = this.createElement("script");
    if (dom) { this.domain = dom; }
    js.id = "boomr-if-as";
    js.src = '//c.go-mpulse.net/boomerang/' + '${key}';
    BOOMR_lstart=new Date().getTime();
    this.body.appendChild(js);
  };
  doc.write('<body onload="document._l();">');
  doc.close();
})();
</script>
</c:if>

</dsp:page>
