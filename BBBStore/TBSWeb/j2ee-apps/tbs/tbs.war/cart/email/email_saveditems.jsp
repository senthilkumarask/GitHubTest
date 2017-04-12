<dsp:page>
<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/bbb/commerce/cart/EmailSavedItemsFormHandler"/>

<c:set var="email" scope="page">
	<bbbl:label key="lbl_cart_email_email"
		language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="intro" scope="page">
	<bbbl:label key="lbl_saveditems_email_intro"
		language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="submit" scope="page">
	<bbbl:label key="lbl_cart_email_submit"
		language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="title" value="Email Saved Items" />

	<div class="row">
		<div class="small-12 columns no-padding">
			<div id="responseMessageWrapper"></div>
		</div>
	</div>
	<div class="row">
		<div class="small-12 columns no-padding">
			<h1>Email Saved Items</h1>
			<p class="p-secondary">${intro}</p>
		</div>
	</div>
	    <dsp:form id="emailCart" action="/tbs/cart/email/response_email_saveditems_json.jsp" method="post">
	     <div class="row">
			<div class="small-12 large-7 columns">
						<dsp:input type="text" bean="EmailSavedItemsFormHandler.recipientEmail" name="email" value="" >
						    <dsp:tagAttribute name="placeholder" value="Email (required)" />
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                        </dsp:input>
						<dsp:input type="hidden" value="${submit}" bean="EmailSavedItemsFormHandler.send"/>
						<small for="email" class="error hidden" id="errorEmail">Please enter a valid username.</small>
			</div>
        <div class="small-12 medium-12 large-5 columns">
            <div class="row">
                <div class="small-6 large-7 columns">
                    <dsp:input type="submit" name="" value="${submit}" bean="EmailSavedItemsFormHandler.send" iclass="button tiny service expand">
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
                </div>
                <div class="small-6 large-5 columns">
                    <input type="button" id="btnCancel" value="Cancel" class="button tiny download expand close-modal"  role="button" aria-pressed="false" aria-labelledby="btnCancel" />
                </div>
            </div>
        </div> 
      </div>
	</dsp:form>
</dsp:page>