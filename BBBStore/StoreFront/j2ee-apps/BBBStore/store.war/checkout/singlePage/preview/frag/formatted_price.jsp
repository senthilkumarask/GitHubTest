<dsp:page>
	<dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
	<dsp:getvalueof var="totalSavedPercentage" param="priceInfoVO.totalSavedPercentage"/>
	<li class="grid_2 alpha omega textRight">
		<ul class="productPriceContainer">
			<li class="highlight"><strong><dsp:valueof param="commerceItemRelationship.amountByAverage" converter="currency"/></strong></li>
			<c:if test="${totalSavedAmount gt 0.0}">
				<li class="smallText">
					<bbbl:label key="lbl_spc_cartdetail_yousave" language="<c:out param='${language}'/>"/>
				 	<dsp:valueof value="${totalSavedAmount}" converter="currency"/>
				 	(<dsp:valueof value="${totalSavedPercentage}" number="0.00"/> %)
				 </li>
			</c:if>
		</ul>
	</li>
</dsp:page>

