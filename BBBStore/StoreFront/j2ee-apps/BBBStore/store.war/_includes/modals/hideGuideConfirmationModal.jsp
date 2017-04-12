<dsp:page>
<dsp:importbean bean="/com/bbb/internationalshipping/droplet/InternationalShippingCheckoutDroplet" />
<dsp:importbean bean="/com/bbb/internationalshipping/formhandler/InternationalShipFormHandler" />
 <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="seeAllGuides"><bbbl:label key="lbl_aria_see_all_guides" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="hideGuide"><bbbl:label key="lbl_hide_guide" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="cancel"><bbbl:label key="lbl_cancel" language="${pageContext.request.locale.language}" /></c:set>
	<div id="hidden-content-guide" class="hidden">
		<div class="config-message-title"><bbbt:textArea key="txt_interactive_hide_model_heading" language="${pageContext.request.locale.language}" /></div>
		<p class="hide-message-details">
			<bbbt:textArea key="txt_interactive_hide_model_details" language="${pageContext.request.locale.language}" />
		</p>
		<a aria-label="${seeAllGuides}" href="/store/static/GuidesPage" class="see-all-links"> 
			<bbbl:label key="lbl_checklist_see_all_guides" language="${pageContext.request.locale.language}"/>
		</a>
		<button aria-label="${hideGuide}"  class="button-Med btnPrimary hideGuidebtn">
			<bbbl:label key="lbl_checklist_hide_guide_button" language="${pageContext.request.locale.language}"/>
		</button>
		 <button aria-label="${cancel}"  class="button-Med btnRegistrySecondary cancelGuidebtn">
			<bbbl:label key="lbl_checklist_hide_guide_cancel" language="${pageContext.request.locale.language}"/>
		</button>
	</div>
</dsp:page>