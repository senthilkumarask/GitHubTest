<dsp:page>
<dsp:getvalueof var="regName" param="registryName" />
<div class="addedToRegistryModalText">
<span class="icon-checkmark"></span>
<bbbl:label key='lbl_reg_from_modal_txt1' language="${pageContext.request.locale.language}" /> ${fn:escapeXml(regName)} <bbbl:label key='lbl_reg_from_modal_txt2' language="${pageContext.request.locale.language}" />
</div>

</dsp:page>