<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<div title="Forgot Password" id="forgotPasswordDialog">
		<div class="formRow grid_6 clearfix">
			<p><bbbl:label key="lbl_mail_success" language="${pageContext.request.locale.language}" /></p>
		</div>
		
			<div class="formRow grid_6 clearfix">
				
				<div class="input clearfix">
			    	<div class="label">		
			    		    		
			    		<%-- Temp Code Waiting for Ajax Code--%>
			    			
			    			<c:out value="${emailError}"/>
			    			<br>
			    			<c:if test="${!(empty emailError)}">
			    				<c:set var="emailError"/>
			    			</c:if>
			    		<%-- Temp Code Waiting for Ajax--%>
			    		
			        	<label for="newEmail">
			            	<bbbl:label key="lbl_cart_email_email" language="${pageContext.request.locale.language}" /> <span class="required">*</span>            
		        		</label>
		        		
		    		</div>
				    <div class="text">		        
				        <div class="">				        	
						 									           
				        </div>		        
				    </div>
		    		<div class="error"></div>
		    	</div>
				
			</div>
		
	</div>
</dsp:page>