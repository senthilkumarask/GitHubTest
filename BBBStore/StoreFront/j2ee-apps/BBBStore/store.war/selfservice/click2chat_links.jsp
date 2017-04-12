<%--
  Default welcome page
 --%> 

<dsp:page>
<bbb:pageContainer index="false" follow="false">

    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <br>
    <p>
    
    <bbbl:label key="lbl_welcome_click_to_chat_landing_page" language="${pageContext.request.locale.language}" />
 	   	
    	<BR/>
    	 <div id="chatModal">
	    	<div id="chatModalDialogs"></div>
    	 	<a href="click_to_chat.jsp" data-chatdata="_icf_1/3" title="Click To Chat" class="liveChat"><bbbl:label key="lbl_Click_to_launch_click_To_chat" language="${pageContext.request.locale.language}" /></a><br>
    	 </div>
    	 
    	 
</bbb:pageContainer>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/index.jsp#3 $$Change: 635969 $ --%>

