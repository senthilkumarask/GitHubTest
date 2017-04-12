<%--  

email_registry.jsp

This jsp appears as a popup window when user 
clicks on Email Registry link on Manage Registry.

--%>
<dsp:page>

  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler"/>

<dsp:getvalueof id="contextroot" idtype="java.lang.String"
                  bean="/OriginatingRequest.contextPath"/>
<%-- <dsp:form id="frm" method="post" action="${contextroot}/browse/gadgets/email_friend_confirm.jsp">--%>

<dsp:form id="frmEmailRegistry">

    <dsp:getvalueof var="registryId" param="registryId" />
    
    <c:if test="${not empty registryId}">
      <dsp:input bean="GiftRegistryFormHandler.value.registryURL" 
            value="${originatingRequest.contextPath}/bbregistry/manage_registry.jsp?${registryId}" type="hidden"/>
    </c:if>
    
	<%-- Subject
    <dsp:input bean="GiftRegistryFormHandler.subject" value="${defaultSubjectTxt}" type="hidden"/> --%>


    <c:url var="successUrlVar" value="../frags/registry_items_owner.jsp">
      <c:param name="registryId" value="${param.registryId}"/>
    </c:url>
    
    <c:url var="errorUrlVar" value="../frags/registry_items_owner.jsp">
      <c:param name="registryId" value="${param.registryId}"/>
      <c:param name="isEmailSuccess" value="false"/>
    </c:url>
    
    <dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" value="${successUrlVar}"/>
    <dsp:input bean="GiftRegistryFormHandler.errorURL" type="hidden" value="${errorUrlVar}"/>


    <p class="atg_store_pageDescription"></p>
  
	
	<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
	<dsp:oparam name="output">
		<dsp:valueof param="message"/>
	</dsp:oparam>
	</dsp:droplet>



<div title="Email Registry">
	<bbbt:textArea key="txt_reg_email_msg" language ="${pageContext.request.locale.language}"/>
	
	
    	<%--<input name="productId" type="hidden" value="${param.productId}"/>--%>
    
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.value.eventType" value="BRD" />
    
		<fieldset>
			<div class="input clearfix formRow">
				<div class="label width_2 fl">
					<label id="lblrecipientEmail" for="recipientEmail"><bbbl:label key="lbl_reg_frnd_email" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
				</div>
				<div class="text width_3 fl">
					<dsp:input type="text" bean="GiftRegistryFormHandler.value.recipientEmail" id="recipientEmail" name="recipientEmail" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblrecipientEmail errorrecipientEmail"/>
                    </dsp:input>
				</div>			
			</div>
			<div class="input clearfix formRow">
				<div class="label width_2 fl">
					<label id="lblsender" for="sender"><bbbl:label key="lbl_reg_frnd_yourname" language ="${pageContext.request.locale.language}"/><span class="required">*</span></label>
				</div>
				<div class="text width_3 fl">
					<dsp:input type="text" bean="GiftRegistryFormHandler.value.senderName" id="sender" name="sender" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblsender errorsender"/>
                    </dsp:input>
				</div>			
			</div>
			<div class="input clearfix formRow">
				<div class="label width_2 fl">
					<label for="comments"><bbbl:label key="lbl_reg_frnd_message" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
				</div>
				<div class="textarea width_4 fl">
					<dsp:textarea type="textarea" bean="GiftRegistryFormHandler.value.message" id="message" name="message"></dsp:textarea>
				</div>			
			</div>
			<div class="formRow clearfix">
				<div class="button fl marRight_10">
				
					<dsp:input type="Submit" bean="GiftRegistryFormHandler.emailRegistry" value="Send Email">
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
				</div>
				<div class="button fl">
					<input type="button" id="btnCancel" value="Cancel" class="close-any-dialog" role="button" aria-pressed="false" aria-labelledby="btnCancel" />
				</div>
			</div>
		</fieldset>
</div>		
  </dsp:form>

</dsp:page>










