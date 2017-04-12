<dsp:page>
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

<dsp:getvalueof id="contextroot" idtype="java.lang.String"
                  bean="/OriginatingRequest.contextPath"/>
	<div>
		<div id="responseMessageWrapper"></div>
		<div id ="confirmMessage" title="Email a Friend" class="hidden">
				<p><bbbl:label key="lbl_email_cart_confirm_message" language="${pageContext.request.locale.language}" /></p>
				<!-- <div class="formRow clearfix">
					<div class="button fl">
						<input type="button" value="Ok" class="close-any-dialog" role="button" aria-pressed="false" />
					</div>
				</div> -->
			</div>
		
		<div id="cartEmailContent">
		<p>${intro}</p>
	    <dsp:form id="emailCart" action="/store/cart/email/response_email_cart_json.jsp" method="post">
	       
			<div class="input clearfix">
				<div class="label">
					<label lbl="lblfrndEmail" for="email"><bbbl:label key="lbl_cart_toEmail" language="${pageContext.request.locale.language}" /></label>
				</div>
				<div class="text">
					<div class="width_3 alpha">
						<dsp:input type="email" bean="EmailCartFormHandler.recipientEmail" name="email" value="" iclass="required" >
						<dsp:tagAttribute name="aria-required" value="true"/>
                           <dsp:tagAttribute name="aria-labelledby" value="lblfrndEmail errorfrndEmail"/>
                        </dsp:input>
					</div>
				</div>			
			</div>
			<div class="input clearfix">
				<div class="label">
					<label lbl="lblemailfrom" for="yourEmail"><bbbl:label key="lbl_cart_emailFrom" language="${pageContext.request.locale.language}" /></label>
				</div>
				<div class="text">
					<div class="width_3 alpha">
						<dsp:input type="email" bean="EmailCartFormHandler.senderEmail" name="yourEmail" value="" iclass="required">
						<dsp:tagAttribute name="aria-required" value="true"/>
                           <dsp:tagAttribute name="aria-labelledby" value="lblemail errorfrndEmail"/>
                        </dsp:input>
					</div>
				</div>			
			</div>
			<div class="input clearfix">
				<div class="label">
					<label lbl="lblmessage" for="message"><bbbl:label key="lbl_cartEmail_message" language="${pageContext.request.locale.language}" /></label>
				</div>
				<div class="text">
					<div class="width_3 alpha">
						<dsp:textarea type="textarea" bean="EmailCartFormHandler.message" name="message" id="message" maxlength="1500" >
                        </dsp:textarea>
					</div>
				</div>			
			</div>
        <div class="marTop_20 buttonpane clearfix">
            <div class="ui-dialog-buttonset">
                <div class="button button_active fl marRight_10">
                    <dsp:input type="submit" name="" value="${submit}" bean="EmailCartFormHandler.send">
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
                </div>
                <div class="button fl">
                    <input type="button" id="btnCancel" value="Cancel" class="close-any-dialog" role="button" aria-labelledby="btnCancel" />
                </div>
            </div>
        </div> 
	    </dsp:form>
	</div>
	</div>
</dsp:page>