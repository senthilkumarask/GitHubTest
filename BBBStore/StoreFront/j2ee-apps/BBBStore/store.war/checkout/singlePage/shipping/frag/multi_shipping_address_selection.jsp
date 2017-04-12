<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/droplet/CommerceItemCheckDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
<dsp:getvalueof var="index" param="cisiIndex" />
<dsp:getvalueof var="ltlItemFlag" param="ltlItemFlag" />
<%-- <input type="hidden" value="${ltlItemFlag}" name="ltlFlag"/> --%>

<dsp:droplet name="CommerceItemCheckDroplet">
			<dsp:param name="commerceItem" param="cisi.commerceItem" />
			<dsp:oparam name="true">
			<li>
				<input type="hidden" value="${ltlItemFlag}" name="ltlFlag"/>
				<strong><bbbl:label key="lbl_intl_shipto_topmenu" language="${pageContext.request.locale.language}"/></strong>
			</li>
			<li class="noMarBot">        
			<div class="select">
					<dsp:getvalueof var="registryId" bean="BBBShippingGroupFormhandler.cisiItems[${index}].commerceItem.registryId" />		
				<dsp:droplet name="/com/bbb/commerce/shipping/droplet/DisplayMultiShippingAddress">
		            <dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
					<dsp:param name="shippingGroupName" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName"/>
		            <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>            
		            <dsp:param name="addressContainer" bean="/com/bbb/commerce/checkout/MultiShippingAddContainer"/>
		            <dsp:param name="siteId" bean="/atg/commerce/ShoppingCart.current.siteId"/>
		            
		            <dsp:oparam name="output">	
					<dsp:getvalueof var="registryMap" bean="/atg/commerce/ShoppingCart.current.registryMap" />					
					<dsp:select bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName" id="shippingAddress_${index}" iclass="addressSelect addNewAddressDataSource" >
					<dsp:getvalueof var="shippingGroupName" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName"/>					

					<dsp:getvalueof var="defaultAddId" param="defaultAddId" />
					<dsp:getvalueof var="latestAddID" param="latestAddID" />	 
					<dsp:getvalueof var="latestAddressMap" param="latestAddressMap" />
					<dsp:getvalueof var="addressMap" param="addressContainer.addressMap" />
					   <dsp:droplet name="/atg/dynamo/droplet/ForEach">
		  					<dsp:param name="array"  param="keys"/>
							<dsp:getvalueof var="addressKey" param="element" />
		  					<dsp:oparam name="outputStart">
								<option value="">
									<bbbl:label key="lbl_spc_shipping_select_address" language="<c:out param='${language}'/>"/>
							    </option>						   	
							</dsp:oparam>
							<dsp:oparam name="output">
									<c:choose>
										<c:when test="${fn:contains(addressKey, 'registry')}">
											<dsp:getvalueof var="registryId" value="${fn:substring(addressKey, 8, fn:length(addressKey))}" />
											<dsp:getvalueof var="registryVO" value="${registryMap[registryId]}" />			  			
													<c:if test="${(registryVO ne null) && (not ltlItemFlag)}">
												<c:choose>
													<c:when test="${(addressKey eq defaultAddId) or (addressKey eq shippingGroupName)}">
																<option value="${addressKey}" selected="selected"  data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}"><c:out value='${addressMap[addressKey].registryInfo}'/>:${registryId}</option>
													</c:when>
													<c:otherwise>
																<option value="${addressKey}"  data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}"><c:out value='${addressMap[addressKey].registryInfo}'/>:${registryId}</option>
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
														<c:when test="${(addressKey eq defaultAddId) or (addressKey eq shippingGroupName)}">
															<c:choose>
																<c:when test="${ltlItemFlag}">
																	<option value="${addressKey}" selected="selected" data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}" data-email="${addressMap[addressKey].email}" data-phoneNumber="${addressMap[addressKey].phoneNumber}" data-alternatePhoneNumber="${addressMap[addressKey].alternatePhoneNumber}"><c:out value='${address}'/>
																	</option>
														</c:when>
														<c:otherwise>
																	<option value="${addressKey}" selected="selected" data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}"><c:out value='${address}'/>
																	</option>
														</c:otherwise>
												</c:choose>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${ltlItemFlag}">
																	<option value="${addressKey}"  data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}" data-email="${addressMap[addressKey].email}" data-phoneNumber="${addressMap[addressKey].phoneNumber}" data-alternatePhoneNumber="${addressMap[addressKey].alternatePhoneNumber}"><c:out value='${address}'/>
																	</option>
																</c:when>
																<c:otherwise>
																	<option value="${addressKey}"  data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}"><c:out value='${address}'/>
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
									<option value="">
										<bbbl:label key="lbl_spc_shipping_select_address" language="<c:out param='${language}'/>"/>
									</option>
  							</dsp:oparam>
						</dsp:droplet>
							<option value="" class="addNewAddressOpt"><bbbl:label key="lbl_spc_shipping_add_new_address" language="${pageContext.request.locale.language}"/></option>
						</dsp:select>
						<input type="hidden" name="cisiIndex" value="${index}" class="addNewAddressIndexSource" />
		            </dsp:oparam>	            
		        </dsp:droplet>
			</div>
		</li>
		<li>
			<a href="#" class="upperCase marTop_5 addNewAddress">
				<strong>
					+ <bbbl:label key="lbl_spc_shipping_add_new_address" language="<c:out param='${language}'/>"/>
				</strong></a>
		</li>
	</dsp:oparam>
</dsp:droplet>		
		
</dsp:page>