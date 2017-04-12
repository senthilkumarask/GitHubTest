<dsp:page>

  <%-- 
      This page displays the name of the sender as well as the message mailed by him/her 
      This page expects the following parameters  - 
      - recipientEmail - Name of the Shopper who is receiving the Email
      - senderName - Name of the Shopper who is sending the Email
      - registryURL - Registry URL to be featured in the greeting message
      - message - Optional Message to be delivered as part of the Email
  --%>

 <dsp:importbean bean="/atg/multisite/Site"/>
 <dsp:getvalueof var="var_senderName" param="senderName"/>
 <dsp:getvalueof var="var_recipientEmail" param="recipientEmail"/>
 <dsp:getvalueof var="var_registryURL" param="registryURL"/>
 <dsp:getvalueof var="var_eventType" param="eventType"/>  
 <dsp:getvalueof var="var_message" param="message"/>
  
  

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-2" />
		<title>Email BBB Gift Registry</title>
	</head>
	<body>  
	  
	   <c:if test="${ var_eventType eq 'BA1' }">
		  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
		 	   Dear Friends of ${var_senderName},
		  </div>
		
		  <br />
		  
		  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
			   <p>
			    <dsp:valueof value="${var_senderName}"/> is expecting to welcome a new member into their family and are thrilled they have chosen buybuy BABY for their Baby Registry.
			    <dsp:valueof value="${var_senderName}"/> has provided us with your email address and requested this e-mail be sent to you. 
			    You may access <dsp:valueof value="${var_senderName}"/> 's Baby Registry by clicking on the link 
			    below.
			   </p>
			   
			   ${var_registryURL} on <dsp:valueof bean="/atg/multisite/Site.name"/>.
		     
		  </div>
		  <br />
		
		  <c:if test="${not empty var_message}">
		    <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
		      <p>
		      Here is his message : <br/>
		      <dsp:valueof value="${var_message}"/>
		      </p>
		      <br/>
		      <p>
		     	 It will be our pleasure to provide complimentary gift packaging in-store or for a nominal fee online, as well as personalized message from you
		      	on all gifts you purchase from <dsp:valueof value="${var_senderName}"/>'s registry.
		      	If you have any questions, please visit one of our store locations, call 1-877-3-BUY-BABY, or e-mail us at customer.service@buybuybaby.com.</p>
		    </div>
		    <br />
		  </c:if>
		  
		  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
		  		All the best, <br/>
				The buybuy BABY Registry Staff <br/>
		  </div>
		   
	 </c:if>
	 
	  <c:if test="${ var_eventType eq 'BRD'}">
			  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
			 	   Dear Friends of ${var_recipientEmail},
			  </div>
			
			  <br />
			  
			  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
				  <p>
				    <dsp:valueof value="${var_senderName}"/> is expecting to get big Day and is thrilled he has chosen BedBathandBeyond for his Wedding Registry.
				    <dsp:valueof value="${var_senderName}"/> has provided us with your email address and requested this e-mail be sent to you. You may access <dsp:valueof value="${var_senderName}"/>'s Wedding Registry by clicking on the link 
				    below.
				  </p>
				   
				   ${var_registryURL} on <dsp:valueof bean="/atg/multisite/Site.name"/>.
			     
			  </div>
			  <br />
			
			  <c:if test="${not empty var_message}">
			    <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
			       <p>
		      		Here is his message : <br/>
		      		<dsp:valueof value="${var_message}"/>
		      	  </p>
		      	  
			      <br/>
			      <p>
			     	It will be our pleasure to provide complimentary gift packaging in-store or for a nominal fee online, as well as personalized message from you
			      	on all gifts you purchase from <dsp:valueof value="${var_senderName}"/> and 's registry.
			      	If you have any questions, please visit one of our store locations, call 1-877-3-BUY-BABY, or e-mail us at customer.service@buybuybaby.com.</p>
			    </div>
			    <br />
			  </c:if>
			  
			  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
			  		All the best, <br/>
			
					The buybuy BABY Registry Staff <br/>
			  </div>
			   
			   <dsp:valueof value="${var_senderName}"/>
			      
			   <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
			
					<p>
						This special announcement was sent to you at the request of the registrant listed above. We respect your right to privacy, 
						and want to assure you that without your permission, your e-mail address will not be used for any reason.
				 		If you would like to receive periodic e-mail offers from buybuy BABY simply reply to this e-mail and type  "SUBSCRIBE" in the subject line.
				 	</p>  
			  	</div>
			  	
		</c:if>	 
			  	
	<div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
	
		<p>
			This special announcement was sent to you at the request of the registrant listed above. We respect your right to privacy, 
			and want to assure you that without your permission, your e-mail address will not be used for any reason.
			If you would like to receive periodic e-mail offers from buybuy BABY simply reply to this e-mail and type  "SUBSCRIBE" in the subject line.
		</p>
		  
	</div>			  	
  	</body>
  </html>
    
</dsp:page>

