<dsp:page>
	<dsp:importbean bean="/com/bbb/account/WalletFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="contextPath"bean="/OriginatingRequest.contextPath" />
	<c:set var="section" value="accounts" scope="request" />
	<c:set var="pageWrapper" value="coupons myAccount" scope="request" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	


	<bbb:pageContainer section="accounts">

	<div id="content" class="container_12 clearfix">
		<div class="grid_12">

			<dsp:form  action="#" method="post" iclass="flatform">

				<div class="input_wrap">
					<label class="popUpLbl" for="email"><bbbl:label key="lbl_chat_email" language="${pageContext.request.locale.language}"/></label>
					<input id="email" name="email" class="fl grid_3 alpha omega" type="text" />
				</div>
				
				<div class="clearfix"></div>

				<div class="input_wrap">
					<label class="popUpLbl" for="email"><bbbl:label key="lbl_chat_email" language="${pageContext.request.locale.language}"/></label>
					<input id="email" name="email" class="fl grid_3 alpha omega" type="text" />
				</div>

				<div class="input_wrap">
					<label class="popUpLbl" for="email"><bbbl:label key="lbl_chat_email" language="${pageContext.request.locale.language}"/></label>
					<input id="email" name="email" class="fl grid_3 alpha omega" type="text" />
				</div>

				<dsp:input 	tabindex="-1" 
								iclass="button-Large btnPrimary" 
								bean="WalletFormHandler.createWallet" 
								type="submit" 
								value="Submit">
					<dsp:tagAttribute name="aria-pressed" value="false"/>
				</dsp:input>

			</dsp:form>

		</div>
	</div>
	
	</bbb:pageContainer>
</dsp:page>