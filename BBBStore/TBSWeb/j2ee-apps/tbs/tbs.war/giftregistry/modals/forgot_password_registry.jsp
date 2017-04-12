<dsp:page>
<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:setvalue bean="GiftRegistryFormHandler.forgetRegistryPassword" />
<dsp:getvalueof var="formExceptions" bean="GiftRegistryFormHandler.formExceptions"/>
<dsp:getvalueof bean="GiftRegistryFormHandler.importErrorMessage" var="errorMessage"/>
<c:choose>
   <c:when test="${not empty formExceptions}">
   {
    "error": "<p>${errorMessage}</p>"
   }
</c:when>
<c:otherwise>
{
"success": true,
"dialog": "<div id="forgotPasswordDialogLink" title=\"Forgot Password\"><p><bbbl:label key='lbl_forgot_pwd_success' language='${pageContext.request.locale.language}'/></p></div>"
}
</c:otherwise>
</c:choose>
</dsp:page>