<%@ page import="com.bbb.commerce.giftregistry.vo.RegistryVO" %>

<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />	
	
	<dsp:getvalueof id="failure"  param="failure"/>
	<c:choose>
		<c:when test="${failure=='true'}">
		  <div>
			<p><bbbl:label key="lbl_reg_oops_some_error" language ="${pageContext.request.locale.language}"/></p>
			
			<div class="button">
				<input type="button" value='<bbbl:label key="lbl_reg_ok" language ="${pageContext.request.locale.language}"/>' class="close-any-dialog" role="button" aria-pressed="false" >
			</div>
		  </div>		
		</c:when>
       	<c:otherwise>
			  <div>
				<bbbt:textArea key="txt_reg_confirm_msg1" language ="${pageContext.request.locale.language}"/> <dsp:getvalueof id="regVO" bean="GiftRegSessionBean.registryVO" idtype="RegistryVO">
					<dsp:valueof value="${regVO.numRegAnnouncementCards}"/>
				</dsp:getvalueof> <bbbt:textArea key="txt_reg_confirm_msg2" language ="${pageContext.request.locale.language}"/>
				<div class="button">
					<input type="button" value='<bbbl:label key="lbl_reg_ok" language ="${pageContext.request.locale.language}"/>' class="close-any-dialog" role="button" aria-pressed="false" >
				</div>
			  </div>				
       	</c:otherwise>
	</c:choose> 
</dsp:page>
