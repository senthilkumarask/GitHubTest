<dsp:page>
<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
<dsp:importbean bean="/com/bbb/browse/EmailAFriendFormHandler"/>

<dsp:setvalue bean="EmailAFriendFormHandler.productId" paramvalue="productId"/>
<dsp:setvalue bean="EmailAFriendFormHandler.senderEmail" paramvalue="yourEmail"/>
<dsp:setvalue bean="EmailAFriendFormHandler.recipientEmail" paramvalue="frndEmail"/>
<dsp:setvalue bean="EmailAFriendFormHandler.message" paramvalue="comments"/>
<dsp:setvalue bean="EmailAFriendFormHandler.send" paramvalue="SUBMIT"/>



<div title="Email a Friend">
	<p><bbbl:label key='lbl_emailafriend_email_sent' language="${pageContext.request.locale.language}" /> ${param.frndEmail}<bbbl:label key='lbl_emailafriend_thanks' language="${pageContext.request.locale.language}" /></p>
	<div class="formRow clearfix">
		<div class="button fl">
			<input type="button" value="Ok" class="close-any-dialog" role="button" aria-pressed="false" />
		</div>
	</div>
</div>
</dsp:page>