<%@ page import="atg.servlet.ServletUtil" %>
<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>		
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>

	<dsp:getvalueof var="formExceptions" bean="BBBSPShippingGroupFormhandler.formExceptions"/>
	<dsp:getvalueof var="shippingGrpChanged" bean="BBBSPShippingGroupFormhandler.shippingGroupChanged"/>

	<%-- 
	<dsp:valueof bean="BBBSPShippingGroupFormhandler.formExceptions" />
	<dsp:getvalueof var="shippingGrpChanged" bean="BBBSPShippingGroupFormhandler.shippingGroupChanged" />

	<dsp:droplet name="ErrorMessageForEach">
		<dsp:param bean="BBBSPShippingGroupFormhandler.formExceptions" name="exceptions" />
		<dsp:oparam name="output">
			<p class="error"><dsp:valueof param="message" valueishtml="true"/></p>
		</dsp:oparam>
	</dsp:droplet>
	--%>

	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="BBBSPShippingGroupFormhandler.formExceptions"/>
		<dsp:oparam name="false">



			<json:object>
				<json:property name="error" value="${true}"/>
				

				<c:if test="${fn:contains(formExceptions, 'err_ship_zipcode_restricted_for_sku')}">
				   <json:property name="shippingRestriction" value="${true}"/>
				</c:if>

				<c:if test="${fn:contains(formExceptions, 'err_porchServiceNotAvailable')}">
				   <json:property name="porchServiceRestriction" value="${true}"/>
				</c:if>


				<%--
				    <json:array name="errorMessages">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="value"/>
							<dsp:oparam name="output">						
								<json:property><dsp:valueof param="element"/></json:property>						
							</dsp:oparam>				
						</dsp:droplet>
					</json:array>	
				 --%>

				<%--  --%>
				<dsp:droplet name="ErrorMessageForEach">					
					<dsp:param param="BBBSPShippingGroupFormhandler.formExceptions" name="exceptions" />
					

					<dsp:oparam name="outputStart">						
					</dsp:oparam>
					<dsp:oparam name="output">
						<json:array name="errorMessages">

							<dsp:getvalueof param="message" var="err_msg_key" />
							<c:set var="under_score" value="_" />
							<c:choose>
								<c:when test="${fn:contains(err_msg_key, under_score)}">
									<c:set var="err_msg" scope="page">
										<bbbe:error key="${err_msg_key}"
											language="${pageContext.request.locale.language}" />
									</c:set>
								</c:when>
							</c:choose>
							<c:choose>
								<c:when test="${empty err_msg}">
									<dsp:getvalueof var="ErrorMessage" param="message" />
									<c:if test="${errMsg ne ErrorMessage}">
										<json:property><dsp:valueof param="message" /></json:property>
									</c:if>
									<dsp:getvalueof var="errMsg" param="message" />	
								</c:when>
								<c:otherwise><json:property>${err_msg}</json:property></c:otherwise>
							</c:choose>
							<c:set var="err_msg" scope="page"></c:set>
						</json:array>		
					</dsp:oparam>
					<dsp:oparam name="outputEnd">
						
					</dsp:oparam>
				</dsp:droplet>
				




			</json:object>	
		</dsp:oparam>
		<dsp:oparam name="true">
			<json:object>
				<json:property name="success" value="true"/>
				<json:property name="displayDefaultedToStd" value="${shippingGrpChanged}"/>
			</json:object>
			<dsp:setvalue bean="BBBSPShippingGroupFormhandler.shippingGroupChanged" value="false"/>
			<dsp:getvalueof bean="BBBSPShippingGroupFormhandler.fromPaypalEdit" var="isFromPaypalEdit" />
			<c:if test="${isFromPaypalEdit}">
				<c:set var="frompaypal" value="true" scope="session"/>
		</c:if>
		</dsp:oparam>					
	</dsp:droplet>	
</dsp:page>