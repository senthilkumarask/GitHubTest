<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/droplet/CommerceItemCheckDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableLTLShippingMethodsDroplet" />
<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
<dsp:getvalueof var="index" param="cisiIndex" />
<dsp:getvalueof var="ajaxParam" param="ajaxParam"/>
<dsp:getvalueof var="ltlItemFlag" param="ltlItemFlag" />
<%-- <input type="hidden" value="${ltlItemFlag}" name="ltlFlag"/> --%>
 <dsp:getvalueof var="selectedShipMethod" param="cisi.commerceItem.ltlShipMethod"></dsp:getvalueof>
 <dsp:getvalueof var="whiteGloveAssembly" param="cisi.commerceItem.whiteGloveAssembly"></dsp:getvalueof>
 <dsp:getvalueof var="actualShipMethod" param="cisi.commerceItem.registrantShipMethod"></dsp:getvalueof>
  <dsp:getvalueof var="regLTLId" param="cisi.commerceItem.registryId"/>
  <dsp:getvalueof var="CurrentRegistryId" param="CurrentRegistryId"/>
  <c:choose>
  	<c:when test="${ajaxParam}">
  		<c:set var="formIdAppend" value="formUpdateOrderSummary_"/>
  	</c:when>
  	<c:otherwise>
  		<c:set var="formIdAppend" value=""/>
  	</c:otherwise>
  </c:choose>
  
  <input type="hidden" value="${selectedShipMethod}" name="selectedShipMethod"/>
	<dsp:droplet name="GetApplicableLTLShippingMethodsDroplet">
		<dsp:param name="skuId" param="cisi.commerceItem.catalogRefId" />
		<dsp:param name="siteId" bean="/atg/commerce/ShoppingCart.current.siteId" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="shipMethodVOList" param="shipMethodVOList" />
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param value="${shipMethodVOList}" name="array" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="shipMethodId" param="element.shipMethodId" />
			<c:if test="${fn:contains(shipMethodId, actualShipMethod)}">
				<dsp:getvalueof var="actualShipMethodFlag" value="true"></dsp:getvalueof>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${not actualShipMethodFlag eq 'true'}">
		<dsp:getvalueof var="actualShipMethod" value=""></dsp:getvalueof>
	</c:if>
 <c:if test="${ whiteGloveAssembly eq 'true'}">
 	<dsp:getvalueof var="actualShipMethod" value="LWA"></dsp:getvalueof>
 </c:if>
 <dsp:getvalueof var="highestShipMethod" param="cisi.commerceItem.highestshipMethod"></dsp:getvalueof>
<dsp:getvalueof var="shippingGroupName" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName"/>

<dsp:droplet name="CommerceItemCheckDroplet">
			<dsp:param name="commerceItem" param="cisi.commerceItem" />
			<dsp:oparam name="true">
			<li>
				<input type="hidden" value="${ltlItemFlag}" name="ltlFlag"/>
				<c:if test="${not ajaxParam}">
				<strong><bbbl:label key="lbl_intl_shipto_topmenu" language="${pageContext.request.locale.language}"/></strong>
				</c:if>
			</li>
			<li class="noMarBot">        
			<div class="select">
				<dsp:getvalueof var="registryId" bean="BBBShippingGroupFormhandler.cisiItems[${index}].commerceItem.registryId" />
				<c:if test="${ltlItemFlag}">
					<dsp:getvalueof var="ltlRegistryFlag" value="${true}" />
				</c:if>
				<dsp:droplet name="/com/bbb/commerce/shipping/droplet/DisplayMultiShippingAddress">
		            <dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
					<dsp:param name="shippingGroupName" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName"/>
		            <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>            
		            <dsp:param name="addressContainer" bean="/com/bbb/commerce/checkout/MultiShippingAddContainer"/>
		            <dsp:param name="siteId" bean="/atg/commerce/ShoppingCart.current.siteId"/>
		            
		            <dsp:oparam name="output">	
					<dsp:getvalueof var="registryMap" bean="/atg/commerce/ShoppingCart.current.registryMap" />					
					<dsp:select bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName" id="${formIdAppend}shippingAddress_${index}" iclass="addressSelect addNewAddressDataSource" >
					<dsp:getvalueof var="shippingGroupName" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName"/>					

					<dsp:getvalueof var="defaultAddId" param="defaultAddId" />
					
					<c:set var="defaultAddId" scope="request">${defaultAddId }</c:set>
					
					
					<dsp:getvalueof var="latestAddID" param="latestAddID" />	 
					<dsp:getvalueof var="latestAddressMap" param="latestAddressMap" />
					<dsp:getvalueof var="addressMap" param="addressContainer.addressMap" />
					<c:set var="addressSelected" value="false"/>
					   <dsp:droplet name="/atg/dynamo/droplet/ForEach">
		  					<dsp:param name="array"  param="keys"/>
							<dsp:getvalueof var="addressKey" param="element" />
								<dsp:oparam name="outputStart">
								<option class="selectAddress" value="">
									<bbbl:label key="lbl_shipping_select_address" language="<c:out param='${language}'/>"/>
							    </option>						   	
							</dsp:oparam>
							<dsp:oparam name="output">
							<c:choose>
										<c:when test="${fn:contains(addressKey, 'registry')}">
											<dsp:getvalueof var="registryId" value="${fn:substring(addressKey, 8, fn:length(addressKey))}" />
											<dsp:getvalueof var="registryVO" value="${registryMap[registryId]}" />	
											<%-- <c:if test="${registryId ne regLTLId }">
												<c:set var="actualShipMethod" value="" />
											</c:if> --%>
										
											<c:if test="${registryVO ne null}">
												<c:choose>
													<c:when test="${(addressKey eq defaultAddId) or (addressKey eq shippingGroupName)}">
														<option class="multiAddressesOpt <c:if test="${CurrentRegistryId eq registryId}"> regAddr</c:if>" value="${addressKey}" selected="selected"  data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-ltlRegistryFlag="${ltlRegistryFlag}" 
														<c:choose><c:when test="${registryId eq regLTLId }">data-selectedLtlShipMethod="${actualShipMethod}"</c:when><c:otherwise>data-selectedLtlShipMethod=""</c:otherwise></c:choose> data-highsetShipMethod="${highestShipMethod}" ><c:out value='${addressMap[addressKey].registryInfo}'/>:${registryId} </option>
														<c:set var="addressSelected" value="true"/>
													</c:when>
													<c:otherwise>
														<option class="multiAddressesOpt <c:if test="${CurrentRegistryId eq registryId}">regAddr</c:if>" value="${addressKey}" data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-email="${addressMap[addressKey].email}" 
														data-companyName="${addressMap[addressKey].companyName}"
														data-address1="${addressMap[addressKey].address1}"
														data-address2="${addressMap[addressKey].address2}"
														data-city="${addressMap[addressKey].city}"
														data-state="${addressMap[addressKey].state}"
														data-zipCode="${addressMap[addressKey].postalCode}"
														data-phoneNumber="${addressMap[addressKey].phoneNumber}" data-ltlRegistryFlag="${ltlRegistryFlag}" 
														<c:choose><c:when test="${registryId eq regLTLId }">data-selectedLtlShipMethod="${actualShipMethod}"</c:when><c:otherwise>data-selectedLtlShipMethod=""</c:otherwise></c:choose> data-highsetShipMethod="${highestShipMethod}"><c:out value='${addressMap[addressKey].registryInfo}'/>:${registryId} </option>
													</c:otherwise>
												</c:choose>
											</c:if>
										</c:when>
										<c:otherwise>
											<c:set var="tempCompanyName" value="${addressMap[addressKey].companyName}" />
											<c:choose>
												<c:when test="${not empty tempCompanyName}">
													<c:set var="tempAddress" value="${addressMap[addressKey].firstName} ${addressMap[addressKey].lastName} - ${addressMap[addressKey].companyName} , " />
												</c:when>
												<c:otherwise>
													<c:set var="tempAddress" value="${addressMap[addressKey].firstName} ${addressMap[addressKey].lastName} - " />
												</c:otherwise>
											</c:choose>
											
											
											<c:set var="tempAddress2" value="${addressMap[addressKey].address2}" />
											<c:set var="tempAddress1" value="${addressMap[addressKey].address1}" />
											<c:if test="${not empty tempAddress1}">
												<c:choose>
													<c:when test="${not empty tempAddress2}">
														<c:choose>
															<c:when test="${ltlItemFlag}">
																<c:set var="address" value=" ${tempAddress} ${addressMap[addressKey].address1} , ${addressMap[addressKey].address2} , ${addressMap[addressKey].city}&#44; ${addressMap[addressKey].state} ${addressMap[addressKey].postalCode} , ${addressMap[addressKey].phoneNumber}" />
															</c:when>
															<c:otherwise>
														<c:set var="address" value="${tempAddress} ${addressMap[addressKey].address1} , ${addressMap[addressKey].address2} , ${addressMap[addressKey].city}&#44; ${addressMap[addressKey].state} ${addressMap[addressKey].postalCode}" />
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${ltlItemFlag}">
																<c:set var="address" value="${tempAddress} ${addressMap[addressKey].address1} , ${addressMap[addressKey].city}&#44; ${addressMap[addressKey].state} ${addressMap[addressKey].postalCode} , ${addressMap[addressKey].phoneNumber}" /> 
															</c:when>
															<c:otherwise>
														<c:set var="address" value="${tempAddress} ${addressMap[addressKey].address1} , ${addressMap[addressKey].city}&#44; ${addressMap[addressKey].state} ${addressMap[addressKey].postalCode}" />
													</c:otherwise>
												</c:choose>
													</c:otherwise>
												</c:choose>

													<c:choose>
														<c:when	test="${(addressKey eq defaultAddId) or (addressKey eq shippingGroupName)}">
														
															<c:choose>
																<c:when test="${ltlItemFlag}">
																<c:if test="${not empty addressMap[addressKey].email}">
																		<c:set var="addressSelected" value="true"/>
																</c:if>
																<option class="multiAddressesOpt" value="${addressKey}" <c:if test="${addressSelected}">selected="selected"</c:if>
																data-firstName="${addressMap[addressKey].firstName}"
																		data-middleName="${addressMap[addressKey].middleName}"
																		data-lastName="${addressMap[addressKey].lastName}"
																		data-companyName="${addressMap[addressKey].companyName}"
																		data-address1="${addressMap[addressKey].address1}"
																		data-address2="${addressMap[addressKey].address2}"
																		data-city="${addressMap[addressKey].city}"
																		data-state="${addressMap[addressKey].state}"
																		data-zipCode="${addressMap[addressKey].postalCode}"
																		data-email="${addressMap[addressKey].email}"
																		data-phoneNumber="${addressMap[addressKey].phoneNumber}"
																		data-alternatePhoneNumber="${addressMap[addressKey].alternatePhoneNumber}"
																		data-highsetShipMethod="${selectedShipMethod}"><c:out
																			value='${address}' />
																</option>
																</c:when>
																<c:otherwise>
																	<option class="multiAddressesOpt" value="${addressKey}" selected="selected"
																	data-firstName="${addressMap[addressKey].firstName}"
																		data-middleName="${addressMap[addressKey].middleName}"
																		data-lastName="${addressMap[addressKey].lastName}"
																		data-companyName="${addressMap[addressKey].companyName}"
																		data-address1="${addressMap[addressKey].address1}"
																		data-address2="${addressMap[addressKey].address2}"
																		data-city="${addressMap[addressKey].city}"
																		data-state="${addressMap[addressKey].state}"
																		data-zipCode="${addressMap[addressKey].postalCode}"
																		data-highsetShipMethod="${selectedShipMethod}"><c:out
																			value='${address}' />
																	</option>
																	<c:set var="addressSelected" value="true"/>
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${ltlItemFlag}">
																	<option class="multiAddressesOpt" value="${addressKey}"
																data-firstName="${addressMap[addressKey].firstName}"
																		data-middleName="${addressMap[addressKey].middleName}"
																		data-lastName="${addressMap[addressKey].lastName}"
																		data-companyName="${addressMap[addressKey].companyName}"
																		data-address1="${addressMap[addressKey].address1}"
																		data-address2="${addressMap[addressKey].address2}"
																		data-city="${addressMap[addressKey].city}"
																		data-state="${addressMap[addressKey].state}"
																		data-zipCode="${addressMap[addressKey].postalCode}"
																		data-email="${addressMap[addressKey].email}"
																		data-phoneNumber="${addressMap[addressKey].phoneNumber}"
																		data-alternatePhoneNumber="${addressMap[addressKey].alternatePhoneNumber}"
																		data-highsetShipMethod="${selectedShipMethod}"><c:out
																			value='${address}' />
																	</option>
																</c:when>
																<c:otherwise>
																	<option class="multiAddressesOpt" value="${addressKey}"
																	data-firstName="${addressMap[addressKey].firstName}"
																		data-middleName="${addressMap[addressKey].middleName}"
																		data-lastName="${addressMap[addressKey].lastName}"
																		data-companyName="${addressMap[addressKey].companyName}"
																		data-address1="${addressMap[addressKey].address1}"
																		data-address2="${addressMap[addressKey].address2}"
																		data-city="${addressMap[addressKey].city}"
																		data-state="${addressMap[addressKey].state}"
																		data-zipCode="${addressMap[addressKey].postalCode}"
																		data-highsetShipMethod="${selectedShipMethod}"><c:out
																			value='${address}' />
																	</option>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
												</c:if>
										</c:otherwise>
									</c:choose>		
									

									
							</dsp:oparam>
							<dsp:oparam name="empty"> 
									<option class="selectAddress" value="">
										<bbbl:label key="lbl_shipping_select_address" language="<c:out param='${language}'/>"/>
									</option>
  							</dsp:oparam>
						</dsp:droplet>
							<option value="" class="addNewAddressOpt"><bbbl:label key="lbl_shipping_add_new_address" language="${pageContext.request.locale.language}"/></option>
							<dsp:tagAttribute name="data-selected" value="${addressSelected}"/>
						</dsp:select>
						<input type="hidden" name="cisiIndex" value="${index}" class="addNewAddressIndexSource" />
		            </dsp:oparam>	            
		        </dsp:droplet>
			</div>
		</li>
		<li>
		<c:if test="${not ajaxParam}">
			<a href="#" class="upperCase marTop_5 addNewAddress">
				<strong>
					+ <bbbl:label key="lbl_shipping_add_new_address" language="<c:out param='${language}'/>"/>
				</strong></a>
		</c:if>
		</li>
	</dsp:oparam>
</dsp:droplet>		
		
</dsp:page>