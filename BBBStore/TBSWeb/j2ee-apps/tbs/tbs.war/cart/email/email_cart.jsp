<dsp:page>

	<%-- Imports --%>
	<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/bbb/commerce/cart/EmailCartFormHandler"/>

	<c:set var="email" scope="page">
		<bbbl:label key="lbl_cart_email_email"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="intro" scope="page">
		<bbbl:label key="lbl_cart_email_intro"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="submit" scope="page">
		<bbbl:label key="lbl_cart_email_submit"
			language="${pageContext.request.locale.language}" />
	</c:set>

	<div class="row">
		<div class="small-12 columns no-padding">
			<div id="responseMessageWrapper"></div>
		</div>
	</div>
	<div class="row">
		<div class="small-12 columns no-padding">
			<h1>Email Cart</h1>
			<p class="p-secondary">${intro}</p>
		</div>
	</div>

	<dsp:form id="emailCart" action="/tbs/cart/email/response_email_cart_json.jsp" method="post">
		<div class="row">
			<div class="small-12 large-7 columns">
                <dsp:input type="email" bean="EmailCartFormHandler.recipientEmail" id="emailIt" name="email" value="">
                    <dsp:tagAttribute name="placeholder" value="Email (required)" />
                    <dsp:tagAttribute name="aria-required" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                </dsp:input>
				<dsp:input type="hidden" value="${submit}" bean="EmailCartFormHandler.send"/>
                <small for="email" class="error hidden" id="errorEmail">Please enter a valid username.</small>
			</div>
			<div class="small-12 medium-12 large-5 columns">
				<div class="row">
					<div class="small-6 large-7 columns">
						<dsp:input type="submit" name="" value="${submit}" bean="EmailCartFormHandler.send" iclass="button tiny service expand">
							<dsp:tagAttribute name="aria-pressed" value="false"/>
							<dsp:tagAttribute name="role" value="button"/>
						</dsp:input>
					</div>
					<div class="small-6 large-5 columns">
						<a href="#" class="button tiny download expand close-modal" aria-pressed="false" aria-labelledby="btnCancel">Cancel</a>
					</div>
				</div>
			</div>
		</div>
	</dsp:form>
</dsp:page>
