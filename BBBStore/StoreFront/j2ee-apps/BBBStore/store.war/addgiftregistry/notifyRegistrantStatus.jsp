<dsp:page>
	<dsp:importbean bean="com/bbb/commerce/giftregistry/droplet/NotifyRegValidateDroplet" />
	
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="regEventDate" param="regEventDate" />
	<dsp:getvalueof var="skuId" param="skuId" />
	<dsp:getvalueof var="disableAddToCart" param="disableAddToCart" />
	<dsp:getvalueof var="hideAddToCart" param="hideAddToCart" />
	<c:set var="submitAddToRegistryBtn">
		<bbbl:label key='lbl_add_to_registry'
			language="${pageContext.request.locale.language}"></bbbl:label>
	</c:set>
	<c:set var="submitAddTOCartBtn">
		<bbbl:label key='lblAddToCart' language="${pageContext.request.locale.language}" />
	</c:set>
	<json:object name="notifyRegResponse">
	<dsp:droplet name="NotifyRegValidateDroplet">
		<dsp:param name="registryId" value="${registryId}"/>
		<dsp:param name="eventDate" value="${regEventDate}"/>
		<dsp:param name="skuId" value="${skuId}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="displayMessageType" param="displayMessageType" scope="request"/>			
			<c:choose>					
				<c:when test="${not empty displayMessageType}">
				<json:property name="notifyData" escapeXml="false">
					<c:set var="displayCartFlag" value=""/>
					<c:choose>
						<c:when test="${hideAddToCart}">
							<c:set var="displayCartFlag" value="hidden"/>
						</c:when>
						<c:otherwise>
							<c:if test="${disableAddToCart}">
								<c:set var="displayCartFlag" value="disabled"/>
							</c:if>
						</c:otherwise>
					</c:choose>					
					
					<section id="notifyRegistrantInner" class="notify-registrant-modal-inner" data-title="<bbbl:label key="notifyRegistrantModalTitle${displayMessageType}" language ="${pageContext.request.locale.language}"/>">
					    <div class="notify-message">
					        <bbbt:textArea key="notifyRegistrantModalMessage${displayMessageType}" language ="${pageContext.request.locale.language}"/>
					    </div>
					    <div class="call-to-action clearfix">  
					        <div class= "alpha omega">
					            <div class="fl notify-add-to-registry">
					                <input type="button" role="button"  class="button-Med btn-notify-add"
					                 value="${submitAddToRegistryBtn}" data-notify-reg="false" />
					            </div>
					            <div class="fl notify-add-to-cart ${displayCartFlag} ">
					               <input type="button" role="button" class="button-Med btn-notify-add"
					                value="${submitAddTOCartBtn}" <c:if test="${disableAddToCart}"> disabled="disabled" </c:if> />               
					            </div>        
					        </div>
					    </div>
					</section>
				</json:property>
				</c:when>
			   <c:otherwise>
			   		<json:property name="notifyData">false</json:property>			   		
			   </c:otherwise>
			</c:choose>			
		</dsp:oparam>	
	</dsp:droplet>
	</json:object>	
</dsp:page>