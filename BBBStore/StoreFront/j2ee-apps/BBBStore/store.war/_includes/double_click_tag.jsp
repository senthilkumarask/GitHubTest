<dsp:page>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="requestParam" param="doubleClickParam"/>
<dsp:getvalueof id="ord" param="ord"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<c:if test="${DoubleClickOn}">
 			   <c:if test="${currentSiteId eq 'BedBathUS'}">
		       <c:set var="doubleClickUrl"><bbbc:config key="doubleClickUrlBedBathUS" configName="ThirdPartyURLs" /></c:set>
		       <c:if test="${empty doubleClickUrl}">
		       <c:set var="doubleClickUrl">//3948870.fls.doubleclick.net</c:set>
		       </c:if>
		       </c:if>
		       <c:if test="${currentSiteId eq 'BuyBuyBaby'}">
		       <c:set var="doubleClickUrl"><bbbc:config key="doubleClickUrlBaby" configName="ThirdPartyURLs" /></c:set>
		       <c:if test="${empty doubleClickUrl}">
		       <c:set var="doubleClickUrl">//3951097.fls.doubleclick.net</c:set>
		       </c:if>
		       </c:if>
		       <c:if test="${currentSiteId eq 'BedBathCanada'}">
		       <c:set var="doubleClickUrl"><bbbc:config key="doubleClickUrlBedBathCA" configName="ThirdPartyURLs" /></c:set>
		       <c:if test="${empty doubleClickUrl}">
		       <c:set var="doubleClickUrl">//3948870.fls.doubleclick.net</c:set>
		       </c:if>
			   </c:if>

	<%--
	Start of DoubleClick Floodlight Tag: Please do not remove
	Activity name of this tag: buybuy Baby Strollers
	URL of the webpage where the tag is expected to be placed: http://www.buybuybaby.com/nodePage.asp?RN=7030&
	This tag must be placed between the <body> and </body> tags, as close as possible to the opening tag.
	Creation Date: 03/12/2012
	--%>
    <c:set var="lbl_doubleclick_iframe_title"><bbbl:label key="lbl_doubleclick_iframe_title" language="${pageContext.request.locale.language}" /></c:set>
	<c:choose>
		<c:when test="${ord eq null}">
			<script type="text/javascript">
				var axel = Math.random() + "";
				var a = axel * 10000000000000;
				document.write('<iframe type="some_value_to_prevent_js_error_on_ie7" src="${doubleClickUrl}/activityi;${requestParam};ord=' + a + '?" title="${lbl_doubleclick_iframe_title}" width="1" height="1" class="hidden"></iframe>');
			</script>
			<noscript>
				<iframe type="some_value_to_prevent_js_error_on_ie7" src="${doubleClickUrl}/activityi;${requestParam};ord=1?"  width="1" height="1" title="${lbl_doubleclick_iframe_title}" class="hidden"></iframe>
			</noscript>
		</c:when>
		<c:otherwise>
				<iframe type="some_value_to_prevent_js_error_on_ie7" src="${doubleClickUrl}/activityi;${requestParam}" width="1" height="1" title="${lbl_doubleclick_iframe_title}" class="hidden"></iframe>
		</c:otherwise>
	</c:choose>
	<%-- End of DoubleClick Floodlight Tag: Please do not remove --%>
</c:if>
</dsp:page>