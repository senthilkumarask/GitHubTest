<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<div title="Forgot Password" id="forgotPasswordDialog">
		<div class="formRow grid_6 clearfix">
			<p>Mail Send Successfull</p>
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
			            	Email <span class="required">*</span>            
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