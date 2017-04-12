<dsp:page>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="currentSiteId" bean="Site.id" />
<dsp:getvalueof var="PageType" param="PageType"/>
<c:choose>
<c:when test="${(currentSiteId eq TBS_BedBathUSSite)}">
<c:set var="key" scope="request"><tpsw:switch tagName="mPulse_us_key"/></c:set>
<c:if test="${empty key}">
<c:set var="key" value="GEHZK-6CKBH-GUKJH-EGFHW-UUKG9"/> <%-- Updated keys from John C Carton dated July 14 2015 --%>
</c:if>
</c:when>
<c:when test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
<c:set var="key" scope="request"><tpsw:switch tagName="mPulse_baby_key"/></c:set>
<c:if test="${empty key}">
<c:set var="key" value="GEHZK-6CKBH-GUKJH-EGFHW-UUKG9"/>
</c:if>
</c:when>
<c:when test="${(currentSiteId eq TBS_BedBathCanadaSite)}">
<c:set var="key" scope="request"><tpsw:switch tagName="mPulse_ca_key"/></c:set>
<c:if test="${empty key}">
<c:set var="key" value="GEHZK-6CKBH-GUKJH-EGFHW-UUKG9"/>
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
 	<c:when test="${sessionScope.boostCode ne null  && sessionScope.boostCode ne '00'}">
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
var SS_EN;
(function(){
		/* introduced for BBBI:3815 */
    	SS_EN =  '${mPulseTaggingVariable}'; 
	
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

// PLEASE EDIT API KEY!!!!

  var dom,doc,where,iframe = document.createElement('iframe');
  iframe.src = "javascript:false";
  iframe.title = 'tbs';
  (iframe.frameElement || iframe).style.cssText = "width: 0; height: 0; border: 0";
  where = document.getElementsByTagName('script')[0];
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
    if(dom) this.domain = dom;
    js.id = "boomr-if-as";
    js.src = '//c.go-mpulse.net/boomerang/' +
    '${key}';
    BOOMR_lstart=new Date().getTime();
    this.body.appendChild(js);
  };
  doc.write('<body onload="document._l();">');
  doc.close();
  })();
</script>
</c:if>

</dsp:page>