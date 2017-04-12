<dsp:page>
	<dsp:getvalueof var="address" param="shipping.address" />
	<dsp:getvalueof var="shipping" param="shipping" />
	<dsp:getvalueof id="orderHeaderInfo" param="orderHeaderInfo" />
	<dsp:getvalueof id="cartDetailInfo" param="cartDetailInfo" />
	<div class="grid_2 alpha">
		<p>
		<strong><bbbl:label key="lbl_spc_preview_shippingaddress"
				language="<c:out param='${language}'/>" />
		</strong>
		</p>
	<c:choose>
				
			<c:when test="${empty address.firstNm}">
                <p>
					<strong>${cartDetailInfo.registrantName}
                    <c:if test="${cartDetailInfo.coRegistrantName ne null}">
                        &amp; ${cartDetailInfo.coRegistrantName}
                    </c:if>
                    </strong>
				</p>
			</c:when>
			<c:otherwise>
				<p class="marTop_5">${address.firstNm}&nbsp;${address.lastNm}</p>
					<p>${address.addr1}</p>
				<p>${address.city},
					${address.state}
					${address.zip}</p>					
			</c:otherwise>	
	</c:choose>		
	</div>	
	<div class="grid_3">
		<p>
			<strong><bbbl:label key="lbl_spc_preview_shippingmethod" language="<c:out param='${language}'/>"/> </strong>
		</p>
		<dl class="noMar marTop_5">
			<dt>${orderHeaderInfo.shipMethod}</dt>              	
              	<c:if test="${orderHeaderInfo.shipAmt gt 0.0}">
              		<dd><dsp:valueof value="${orderHeaderInfo.shipAmt}" converter="currency"/></dd>
              	</c:if>
			<c:choose>
				<c:when test="${cartDetailInfo.VDSInCart eq 'false'}">
					<dt><bbbl:label key="lbl_spc_preview_expecteddelivery" language="<c:out param='${language}'/>"/> </dt>
					<dd><dsp:valueof value="${shipping.deliveryDt}" /></dd>
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
		</dl>
		
	</div>
</dsp:page>