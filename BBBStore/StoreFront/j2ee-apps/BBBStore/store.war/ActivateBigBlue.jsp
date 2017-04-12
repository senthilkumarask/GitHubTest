<dsp:page>
	<%@ page import="com.bbb.constants.BBBCoreConstants"%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>	
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean var="SessionBean" bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean var="ActivateBigBlueFormHandler" bean="/com/bbb/browse/ActivateBigBlueFormHandler" />
	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<bbb:pageContainer>
	<jsp:attribute name="section">browse</jsp:attribute>
	<jsp:attribute name="homepage">bigblue</jsp:attribute>
	<jsp:attribute name="pageWrapper"></jsp:attribute>
	<dsp:getvalueof var="successMessage" bean="/com/bbb/browse/ActivateBigBlueFormHandler.successMessage" />
	
		<%--<div id="cmsPageHead" class="grid_12 clearfix">
			<h1>Activate Your Online Offer</h1>
		</div> --%>
		<jsp:body>
		<table width="100%" style="margin-top: 8px;">
		<tbody>
		<div style="margin-left:150px;">
		<dsp:include page="/global/gadgets/errorMessage.jsp">
     	 <dsp:param name="formhandler" bean="ActivateBigBlueFormHandler"/>
    	</dsp:include>
			<p class="atg_store_pageDescription" ><h1><bbbl:label key="lbl_activate_your_online_offer" language="${pageContext.request.locale.language}" /></h1></p>
			 <%-- 	ErrorMessageForEach  starts --%>
    
<%-- 	ErrorMessageForEach Ends --%>
		<%--	<dsp:droplet name="Switch">
				<dsp:param name="value" bean="/com/bbb/browse/ActivateBigBlueFormHandler.successMessage"/>
				<dsp:oparam name="false">
				<p>
										<dsp:include page="/global/gadgets/errorMessage.jsp">
										<dsp:param name="formhandler" bean="ActivateBigBlueFormHandler"/>
										</dsp:include>
								</p>
				<%--<dsp:droplet name="IsEmpty">
		 		<dsp:param name="value" bean="SessionBean.couponError"/>
		 				<dsp:oparam name="false">
		 				<dsp:getvalueof var="displayMessage" bean="SessionBean.couponError" />
		 				<div>
						<b><font color="red">${displayMessage}</font></b>
						</div>
		 		</dsp:oparam>
		 		<dsp:oparam name="true"> --%> 
		 		<%--</dsp:oparam> 
		 		</dsp:droplet>	
				</dsp:oparam>
			</dsp:droplet> --%>
		<tr>
		<td>
		<div class="cmsPageContent" style="width: 350px; font-size: 11px; margin-left: 150px; margin-right: 10px;" >
			<dsp:form  id="frmActivateBigBlue" method="post" action="activateBigBlue.jsp">		
				<div>
					<b><bbbl:label key="lbl_To_Activate_offer" language="${pageContext.request.locale.language}" />, <bbbl:label key="lbl_enter_information_from_offer_into_form" language="${pageContext.request.locale.language}" /></b>
				</div>
				<div>
			<div class="input clearfix formRow marTop_10">
				<div class="label width_2 fl">
					<label for="txtOfferCd"><bbbl:label key="lbl_Offer_Number" language="${pageContext.request.locale.language}" /><span class="required">*</span></label>
				</div>
				<div class="text width_2 fl marRight_20">
					<dsp:input bean="ActivateBigBlueFormHandler.activateBigBlueVO.offerCd" name="offerCd" id="offerCd" type="text" size="20" />					
				</div>			
			</div> 
			
			 <div class="input clearfix formRow">
				<div class="label width_2 fl">
					<label for="txtEmailAddr"><bbbl:label key="lbl_spc_login_frag__email" language="${pageContext.request.locale.language}" /><span class="required">*</span></label>
				</div>
				<div class="text width_2 fl" style="width:200px;">
					<dsp:input bean="ActivateBigBlueFormHandler.activateBigBlueVO.emailAddr" id="newEmail" name="email" type="text" size="30"/>
				</div> 		
			</div> 
			<div class="input clearfix formRow">
            	<div class="label width_2 fl">
					<label for="basePhone1"><bbbl:label key="lbl_profile_primaryphone" language="${pageContext.request.locale.language}"/></label>
                    
				</div>
				<div class="text width_2 fl">
				<div class="grid_3 alpha phone multiplePhoneFldsWrap">								
                    <fieldset class="phoneFields"><legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/></legend>
                    <label for="basePhone1" class="offScreen" title="<bbbl:label key='lbl_phonefield_areacode' language='${pageContext.request.locale.language}'/>"><bbbl:label key="lbl_phonefield_areacode" language="${pageContext.request.locale.language}"/></label>
					<label for="basePhone2" class="offScreen" title="<bbbl:label key='lbl_phonefield_exchange' language='${pageContext.request.locale.language}'/>"><bbbl:label key="lbl_phonefield_exchange" language="${pageContext.request.locale.language}"/></label>
					<label for="basePhone3" class="offScreen" title="<bbbl:label key='lbl_phonefield_number' language='${pageContext.request.locale.language}'/>"><bbbl:label key="lbl_phonefield_number" language="${pageContext.request.locale.language}"/></label>
					<div class="text fl">
						<input id="basePhone1" type="text" name="basePhone1" class="phone phoneField" maxlength="3"/>
						<span class="dateSeperator">-</span>
					</div>
					<div class="text fl">
            			<input id="basePhone2" type="text" name="basePhone2" class="phone phoneField" maxlength="3" />
            			<span class="dateSeperator">-</span>
            		</div>
            		<div class="text fl">
            			<input id="basePhone3" type="text" name="basePhone3" class="phone phoneField" maxlength="4" />
            		</div>
	            	<div class="cb">
						<label class="PhoneErrLabel hidden"></label>
						<label for="basePhone1" class="error hidden" generated="true"></label>
						<label for="basePhone2" class="error hidden" generated="true"></label>
						<label for="basePhone3" class="error hidden" generated="true"></label>
					</div>
		            <dsp:input bean="ActivateBigBlueFormHandler.activateBigBlueVO.mobilePhone" type="hidden" iclass="fullPhoneNum" name="phone"/>
                    </fieldset>
            	</div>
            	</div>
			<%--	<div class="text width_2 fl">
					<dsp:input bean="ActivateBigBlueFormHandler.activateBigBlueVO.mobilePhone" name="MobilePhone" id="txtMobilePhone" type="text" size="20"/>
				</div>	 --%>		
			</div>			

			<div id="captchaDiv" class="clearfix noMarTop">
				<div class="input clearfix">
					<div class="">
						<label for="captchaAnswer">*<bbbl:label key="lbl_Please_type_words_you_see_in_image" language="${pageContext.request.locale.language}" /><span class="required">*</span></label>
					</div>
					<div class="text width_3 fl">
						<img width="300" height="100" src="<c:url value="/simpleCaptcha.png" />" />
						<dsp:input type="text" name="captchaAnswer" id="captchaAnswer" value="" iclass="marTop_5" bean="ActivateBigBlueFormHandler.captchaAnswer" autocomplete="off"/>
					</div>			
				</div>
			</div>
			<div class="formRow">
            <div class="optIn">		  	
                   <dsp:input type="checkbox" bean="ActivateBigBlueFormHandler.activateBigBlueVO.promoEmailFlag" name="emailOptIn" checked="false" iclass="fl"></dsp:input>
                  <label for="optIn" class="textDgray11"><bbbl:label key="lbl_mob_bigblue_emailOptin" language="${pageContext.request.locale.language}" /></label>
            </div>
			<div class="clear"></div>
          </div>
          <div class="formRow">
            <div class="optIn">		  	
                   <dsp:input type="checkbox" bean="ActivateBigBlueFormHandler.activateBigBlueVO.promoMobileFlag" name="PromoMobileFlag" checked="false" iclass="fl"></dsp:input>
                  <label for="optIn" class="textDgray11"><bbbl:label key="lbl_mob_bigblue_mobileOptin" language="${pageContext.request.locale.language}" /></label>
            </div>
			<div class="clear"></div>
          </div>
         <div class="button btnApply button_active button_active_orange clearfix">
					   <dsp:input id="btnSubmitActivateBigBlueRegRequest" type="submit" value="Submit" bean="/com/bbb/browse/ActivateBigBlueFormHandler.ActivateBigBlueRequest" />
					<!--<dsp:input bean="ActivateBigBlueFormHandler.successURL" type="hidden" value="/store/bigBlueSuccess.jsp"/>
					<dsp:input bean="ActivateBigBlueFormHandler.errorURL" type="hidden"  value="/store/activateBigBlue.jsp"/>-->
					</div><br></br>			
		</div>
					
			</dsp:form>
		</div>
		</td>
		<td>
		<div style="width: 450px; font-size: 11px; margin-left: 10px; margin-right: 20px;">
					<img src="/_assets/bbbeyond/images/bigblue.jpg" border="0"><br><br>
					<u><bbbl:label key="lbl_Email_Privacy" language="${pageContext.request.locale.language}" /></u><br>
					<bbbl:label key="lbl_protection_email_credit_card_information" language="${pageContext.request.locale.language}" />
					<br><br>
					<u><bbbl:label key="lbl_Email_Unsubscribe" language="${pageContext.request.locale.language}" /></u><br>
					<bbbl:label key="lbl_do_not_want_receive_email" language="${pageContext.request.locale.language}" />, <bbbl:label key="lbl_circulars_us_subsidiaries" language="${pageContext.request.locale.language}" />, <bbbl:label key="lbl_remove_from_email_list" language="${pageContext.request.locale.language}" /> <a href="https://app.bedbathandbeyond.com/prefs/unsub.cfm"><bbbl:label key="lbl_subscribe_email_unsubscribe_page" language="${pageContext.request.locale.language}" /></a>.
					<br><br>
					<u><bbbl:label key="lbl_direct_mail_Unsubscribe" language="${pageContext.request.locale.language}" /></u><br>
					<bbbl:label key="lbl_do_not_want_receive_email" language="${pageContext.request.locale.language}" />, <bbbl:label key="lbl_circulars_us_subsidiaries" language="${pageContext.request.locale.language}" />, <bbbl:label key="lbl_remove_from_email_list" language="${pageContext.request.locale.language}" /> <a href="https://app.bedbathandbeyond.com/prefs/dmunsub.cfm"><bbbl:label key="lbl_subscribe_email_unsubscribe_page" language="${pageContext.request.locale.language}" /></a>.			
				<div>
					<br><br>
					<div class="smallText">
                        <p><bbbl:label key="lbl_footer_privacypolicy" language="${pageContext.request.locale.language}" /></p>
                    </div>
					<br><br>
					<bbbl:label key="lbl_question_about_offer" language="${pageContext.request.locale.language}" />, <bbbl:label key="lbl_please_call_us_at" language="${pageContext.request.locale.language}" />,  <bbbl:label key="lbl_email_us_at" language="${pageContext.request.locale.language}" /><a href="mailto:customer.service@bedbath.com">customer.service@bedbath.com</a>.
					<br><br>
		</div>
		</div>
		</td>
		</div>
		</tr>
		</tbody>
		</table>
		</jsp:body>
		 <jsp:attribute name="footerContent">
		<script type="text/javascript">
if (typeof s !== 'undefined') {
	s.pageName='My Account > Offer activation'; // pageName
	s.channel='My Account';
	s.prop1='My Account';
	s.prop2='My Account';
	s.prop3='My Account';
	s.prop4='My Account';
	s.prop6='${pageContext.request.serverName}'; 
	s.eVar9='${pageContext.request.serverName}';	
	s.server='${pageContext.request.serverName}';
	var s_code = s.t();
	if (s_code)
		document.write(s_code);
}
</script>
		 
		 </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>