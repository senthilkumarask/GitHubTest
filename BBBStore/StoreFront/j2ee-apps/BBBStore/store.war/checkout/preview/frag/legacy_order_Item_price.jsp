<dsp:page>	
	<dsp:getvalueof var="cartItemDetails" param="cartItemDetails"/>	
			<dsp:getvalueof	var="unitPrice" param="cartItemDetails.unitCost" />
			<dsp:getvalueof	var="totalPriceBeforeDiscount" param="cartItemDetails.extPrice" />
			<dsp:getvalueof	var="orderQty" param="cartItemDetails.orderQty" />
			<dsp:getvalueof var="discountAmount" param="cartItemDetails.discountAmt" />
			
			<li class="grid_1 textRight highlight">
				<strong><dsp:valueof value="${unitPrice}" converter="currency" /></strong>
			</li>
			<li class="grid_1 textCenter yourPrice">
			<ul class="productPriceContainer">
					<li class="highlight">
						<strong> 
						<c:choose>
							<c:when test="${discountAmount gt 0.0}">
								${orderQty} <bbbl:label key="lbl_cart_multiplier" language="${language}"/> 
								<dsp:valueof value="${unitPrice - discountAmount/orderQty}" converter="currency" />
							</c:when>
							<c:otherwise>
								${orderQty} <bbbl:label key="lbl_cart_multiplier" language="${language}"/>
								<dsp:valueof value="${unitPrice}" converter="currency" />
							</c:otherwise>
						</c:choose> 
						</strong>
					</li>
					<c:if test="${discountAmount gt 0.0}">
						<li class="smallText">(<bbbl:label key="lbl_preview_reg"
								language="<c:out param='${language}'/>" /> <dsp:valueof
								value="${unitPrice}" converter="currency" />)
						</li>
						<li class="smallText"><bbbl:label
								key="lbl_cartdetail_yousave"
								language="<c:out param='${language}'/>" /> <dsp:valueof
								value="${discountAmount/orderQty}" converter="currency" />
						</li>
					</c:if>
				</ul>
			</li>
				
			<c:set var="totalSavedPercentage" value="${(discountAmount / totalPriceBeforeDiscount)*100}" />
			<li class="grid_2 alpha omega textRight">
				<ul class="productPriceContainer">
					<li class="highlight"><strong><dsp:valueof
								value="${totalPriceBeforeDiscount - discountAmount}" converter="currency" />
					</strong>
					</li>
					<c:if test="${discountAmount gt 0.0}">
						<li class="smallText"><bbbl:label
								key="lbl_cartdetail_yousave"
								language="<c:out param='${language}'/>" /> <dsp:valueof
								value="${discountAmount}" converter="currency" /> (<dsp:valueof
								value="${totalSavedPercentage}" number="0.00"/> %)</li>
					</c:if>
				</ul>
			</li>		
</dsp:page>

