<%@ page contentType="text/json" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />

	<json:object>
		<c:if test="${not empty sessionScope.priceOverrideStatusVO}">

			<%-- success --%>
			<json:property name="success" value="${sessionScope.priceOverrideStatusVO.success}"/>

			<%-- errorMessages --%>
			<c:if test="${not empty sessionScope.priceOverrideStatusVO.errorMessages}">
				<json:array name="errorMessages">
					<c:forEach items="${sessionScope.priceOverrideStatusVO.errorMessages}" var="errorMessage">
						<json:object>
							<json:property name="fieldId" value="${errorMessage.fieldId}"/>
							<json:property name="message" value="${errorMessage.message}"/>
						</json:object>
					</c:forEach>
				</json:array>
			</c:if>

		</c:if>
	</json:object>

</dsp:page>
