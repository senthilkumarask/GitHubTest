<dsp:importbean bean="/com/bbb/cms/email/EmailAPageFormHandler"/>
<dsp:page>
	 <%-- If no error --%>
	 <c:if test="${empty param.error}">
		<div title="Envía por correo tu Mesa de Regalos a tus amigos">
		<p>
			<bbbl:label key='lbl_mxemailafriend_email_sent' language="${pageContext.request.locale.language}" /> 
			<dsp:valueof bean="EmailAPageFormHandler.recipientEmail"/> 
			<bbbl:label key='lbl_mxemailafriend_thanks' language="${pageContext.request.locale.language}" />
		</p>
			
			<div class="formRow clearfix">
				<div class="button fl">
					<input type="button" value="Ok" class="close-any-dialog" role="button" aria-pressed="false" />
				</div>
			</div>
		</div>
	</c:if>
	 <c:if test="${not empty param.error}">
		<bbbl:textArea key="txt_email_tofriend_err" language ="${pageContext.request.locale.language}"/>
	</c:if>
</dsp:page>

