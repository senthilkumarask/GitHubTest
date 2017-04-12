<dsp:page>
  <%-- Email To A Friend email template 
      Parameters from TemplateEmailSender - 
      - locale - Current locale of the Shopper when she sent the email
      - productId - Repository Id of the Product being emailed
      - recipientEmail - Name of the Shopper who is receiving the Email
      - senderName - Name of the Shopper who is sending the Email
      - message - Optional Message to be delivered as part of the Email
  --%>

  <dsp:importbean bean="/atg/multisite/Site"/>
<div id="themeWrapper" class="by">  
  <fmt:message var="emailSubject" key="emailtemplates_emailAFriend.subject">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
  </fmt:message>
  <dsp:param name="displayProfileLink" value="false"/>
 
      <dsp:include page="gadgets/emailAFriendContents.jsp">
        <dsp:param name="locale" param="locale"/>
        <dsp:param name="productId" param="productId"/>
        <dsp:param name="recipientEmail" param="recipientEmail"/>
        <dsp:param name="senderName" param="senderName"/>
        <dsp:param name="message" param="message"/>
        <dsp:param name="container" value="/emailtemplates/emailAFriendContentsContainer.jsp"/>
      </dsp:include>
      
</div>  
</dsp:page>
