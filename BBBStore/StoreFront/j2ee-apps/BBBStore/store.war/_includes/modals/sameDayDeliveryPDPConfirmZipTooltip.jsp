<dsp:page>
	<dsp:getvalueof var="sddCurrentZip" bean="/com/bbb/profile/session/SessionBean.currentZipcodeVO.zipCode" />
	<c:if test="${empty sddCurrentZip}">
		<c:set var="sddCurrentZip" value="" />
	</c:if>
	<div id="confirmZipCode" class="clearfix">
		<form id="confirmZipToolTip">
			<div class="input textBox">
				<%-- <input id="sddZip" title="SDD Zip" placeholder="" maxlength="5" name="sddZip" value="" type="text" class="escapeHTMLTag"> --%>
				<div class="sddAjaxLoader hidden">
					<img width="20" height="20" src="/_assets/global/images/widgets/small_loader.gif" alt="loading data">
				</div>
				
				<label for="sddZip" id="lblSddZip" class="txtOffScreen"><bbbl:label key="lbl_enter_zip_code" language="${pageContext.request.locale.language}" /></label>
				<input type="text" id="sddZip" maxlength="10" name="zipUS" value="${sddCurrentZip}" />
			</div>	
			<div class="confirmZipBtnWrap">
				<button id="confirmBtn" class="btnPrimary btn-block" aria-label="<bbbl:label key='lbl_sdd_confirm_zip_code_verbose' language='${pageContext.request.locale.language}' />"><bbbl:label key="lbl_sdd_confirm" language="${pageContext.request.locale.language}" /></button>
				<a class="first-modal-link" href="javascript:;"></a>
			</div>
		</form>
	</div>
</dsp:page>