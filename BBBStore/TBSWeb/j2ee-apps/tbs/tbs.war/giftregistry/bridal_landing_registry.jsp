<dsp:page>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/> 
    
    <dsp:getvalueof var="registrySummaryVO" param="registrySummaryVO"/>
    <dsp:getvalueof var="userStatus" param="userStatus"/>

  <div class="small-12 columns">
			        <dsp:droplet name="Switch">
			            <dsp:param name="value" param="userStatus"/>
				                 <dsp:oparam name="1">
									    <dsp:include page="bridal_landing_non_logged_registry.jsp"/>
								</dsp:oparam>
								
								<dsp:oparam name="2">
									<dsp:include page="bridal_landing_logged_no_registry.jsp"/>
								</dsp:oparam>
								
								<dsp:oparam name="4">
									<dsp:include page="bridal_landing_logged_mul_registry.jsp">
										<dsp:param name="registrySummaryVO" value="${registrySummaryVO}" />
										<dsp:param name="multiReg" value="true"/>
									</dsp:include>
								</dsp:oparam>
								
								<dsp:oparam name="3">
									<dsp:include page="bridal_landing_logged_mul_registry.jsp">
										<dsp:param name="multiReg" value="false"/>
									</dsp:include>
								</dsp:oparam>
								
				     	</dsp:droplet>

     </div>
</dsp:page>