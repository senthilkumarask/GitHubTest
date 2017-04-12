<%@ page language="java" %>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/com/bbb/email/droplet/BBBEmailFetchDroplet" />
	<dsp:droplet name="BBBEmailFetchDroplet">				
		<dsp:oparam name="output">			
					<dsp:valueof param="emailItem" valueishtml="true" />														
		</dsp:oparam>				
		<dsp:oparam name="error">
			<bbb:pageContainer>
				<div id="content" class="container_12 clearfix" role="main">
					<div class="grid_12 clearfix marTop_20">
						<h3><span class="error">
								<bbbl:label key='lbl_no_email_found' language="${pageContext.request.locale.language}" />
							</span>
						</h3>	
					</div>	
				</div>				
			</bbb:pageContainer>
		</dsp:oparam>
	</dsp:droplet>