<%@ taglib uri="/WEB-INF/tld/BBBLblTxt.tld" prefix="bbb"%>
<div id="updateAddressWarningDialog" title="Address Update Warning">
    <p> <bbbl:label key="lbl_billingaddresschange_note1" language="${pageContext.request.locale.language}" /> </p>
</div>
<div id="removeAddressWarningDialog" title="Removed Address?">
    <p><bbbl:label key="lbl_spc_billingaddresschange_note2" language="${pageContext.request.locale.language}" /></p>
</div>
<div class="width_4">
<div class="button marRight_10">
    <input type="submit" value="Ok" id="submitokBtn" role="button" aria-pressed="false" />
</div>
<div class="button fr">
    <input type="button" value="Cancel" class="close-any-dialog" role="button" aria-pressed="false" />
</div>
</div>