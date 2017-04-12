<%@page contentType="application/json"%>
 <%--This JSP would load in cases of Dynamic Checklist Data on manual check. Story : BBBI-1071
--%>
<dsp:importbean bean="/com/bbb/commerce/checklist/droplet/CheckListDroplet" />
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:page>
<dsp:getvalueof bean="SessionBean" var="bean"/>
	<dsp:droplet name="CheckListDroplet">
	<dsp:param name="registryId" param="registryId" />
	<dsp:param name="registryType" param="registryType" />
	<dsp:param name="cat1Id" param="cat1Id" />
	<dsp:param name="cat2Id" param="cat2Id" />
	<dsp:param name="cat3Id" param="cat3Id" />
	<dsp:param name="totalC3QuantityAdded" param="totalC3QuantityAdded" />
	<dsp:param name="totalC3QuanitySuggested" param="totalC3QuanitySuggested" />
	<dsp:param name="averagePercentage" param="averagePercentage" />
	<dsp:param name="noOfC1" param="noOfC1" />
	<dsp:param name="manualC3Check" value="true" />
	<dsp:param name="c3AddedQuantity" param="c3AddedQuantity" />
	<dsp:param name="isChecklistSelected" param="isChecklistSelected" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="checkListProgressVO" param="checkListProgressVO" />
			<json:object escapeXml="false">
				<json:property name="categoryIDForC1" value="${checkListProgressVO.categoryIDForC1}"/>		
				<json:property name="categoryIDForC2" value="${checkListProgressVO.categoryIDForC2}"/>		
				<json:property name="categoryIDForC3" value="${checkListProgressVO.categoryIDForC3}"/>		
				<json:property name="categoryProgress" value="${checkListProgressVO.categoryProgress}"/>		
				<json:property name="overAllProgress" value="${checkListProgressVO.overAllProgress}"/>		
				<json:property name="labelMessage" value="${checkListProgressVO.labelMessage}"/>		
				<json:property name="updatedAddedQuantity" value="${checkListProgressVO.updatedAddedQuantity}"/>		
				<json:property name="c1ImageUrl" value="${checkListProgressVO.c1ImageUrl}&wid=38"/>	
				<json:property name="error" value="${checkListProgressVO.error}"/>	
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
