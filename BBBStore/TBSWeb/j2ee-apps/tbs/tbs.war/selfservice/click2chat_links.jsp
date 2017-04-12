<%--
  Default welcome page
 --%> 

<dsp:page>
<bbb:pageContainer index="false" follow="false">

    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <br>
    <p>
    Welcome to click to chat landing page!
 	   	
    	<BR/>
    	 <div id="chatModal">
	    	<div id="chatModalDialogs"></div>
    	 	<a href="click_to_chat.jsp" data="_icf_1/3" title="Click To Chat" class="liveChat">Click here to launch click To chat</a><br>
    	 </div>
    	 
    	 
</bbb:pageContainer>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/index.jsp#3 $$Change: 635969 $ --%>

