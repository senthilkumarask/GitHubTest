<dsp:page>

  <%-- 
      This page displays the name of the sender as well as the message mailed by him/her 
      This page expects the following parameters  - 
      - recipientEmail - Name of the Shopper who is receiving the Email
      - senderName - Name of the Shopper who is sending the Email
      - product - Product to be featured in the greeting message
      - message - Optional Message to be delivered as part of the Email
  --%>

  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:getvalueof var="var_senderName" param="senderName"/>
  <dsp:getvalueof var="var_recipientEmail" param="recipientEmail"/>  
  <dsp:getvalueof var="var_message" param="message"/>
  <dsp:getvalueof var="var_productName" param="product.displayName"/>

  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
    Hi ${var_recipientEmail},
  </div>
  <br />
  
  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
   I thought you might like this ${var_productName} from <dsp:valueof bean="/atg/multisite/Site.name"/>.
     
  </div>
  <br />

  <c:if test="${not empty var_message}">
    <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
      <dsp:valueof value="${var_message}"/>
    </div>
    <br />
  </c:if>
  
  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
    <dsp:valueof value="${var_senderName}"/>
  </div>
  
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailAFriendMessage.jsp#2 $$Change: 635969 $--%>
