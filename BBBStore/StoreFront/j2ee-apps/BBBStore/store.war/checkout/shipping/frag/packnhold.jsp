<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet"/>
<dsp:getvalueof var="formExceptionFlag" param="formExceptionFlag" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="siteId" bean="Site.id" />

<dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet">
	 	  <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
	 	  <dsp:param name="shippingGroup" param="order.shippingGroups"/>
	 	   <dsp:param name="isPackHold" value="${true}"/> 
	 	   <dsp:oparam name="beddingKit">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="beddingKit" value="true"/>
           </dsp:oparam>
		   <dsp:oparam name="weblinkOrder">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="weblinkOrder" value="true"/>
           </dsp:oparam>
           <dsp:oparam name="notBeddingKit">
                <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="beddingKit" value="false"/>
           </dsp:oparam>
</dsp:droplet>

        <dsp:droplet name="BBBPackNHoldDroplet">
            <dsp:param name="order" bean="ShoppingCart.current"/>
            <dsp:oparam name="output">
			<dsp:getvalueof param="hasSingleCollegeItem" var="hasSingleCollegeItem"/>
			<dsp:getvalueof param="hasAllPackNHoldItems" var="hasAllPackNHoldItems"/>
			<dsp:getvalueof var="packAndHoldDate" param="packAndHoldDate" /> 
			<dsp:getvalueof var="isPackHold" param="isPackHold" />
			<c:if test="${hasSingleCollegeItem}">
                <div class="checkboxItem input clearfix">	
				<c:choose>
					<c:when test="${hasSingleCollegeItem && !hasAllPackNHoldItems}">
						 <div class="checkbox disabled">
							<div class="checker" id="uniform-shippingOption2">
								<span><input id="shippingOption2" disabled name="shippingOption2" value="true" type="checkbox" /></span>
							</div>
							<input name="_D:shippingOption2" value=" " type="hidden"/>
						</div>
						<div class="label">
							<label for="shippingOption1pack" class="disabled half-opacity" ><strong><bbbl:label key="lbl_spc_shipping_pack_hold" language="${pageContext.request.locale.language}" /></strong> <bbbl:label key="lbl_shipping_pack_hold_unavailable_msg" language="${pageContext.request.locale.language}" /> </label>
							<span><bbbl:label key="learn_more_pack_hold" language="${pageContext.request.locale.language}" /></span>	
						</div>						
					</c:when>
					<c:otherwise>
					<div class="checkbox">
						<dsp:droplet name="Switch">
                    	<dsp:param name="value" param="isPackHold"/>
							<c:choose>
									<c:when test="${formExceptionFlag eq 'true'}">
										<dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="checkbox" name="shippingOption1pack" id="shippingOption1pack"/>
									</c:when>
									<c:otherwise>
										<dsp:oparam name="true">
										  		<dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="checkbox" name="shippingOption1pack" id="shippingOption1pack" checked="false"/>
										</dsp:oparam>
										<dsp:oparam name="false">
										 <dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="checkbox" name="shippingOption1pack" id="shippingOption1pack" checked="false"/>
									  </dsp:oparam>
									</c:otherwise>
								</c:choose>                    
						</dsp:droplet>                                                       
                    </div>
                    <div class="label">
                        <label for="shippingOption1pack"><strong><bbbl:label key="lbl_shipping_pack_hold" language="${pageContext.request.locale.language}" /></strong> <bbbl:label key="lbl_shipping_pack_hold_msg" language="${pageContext.request.locale.language}" /></label>
                    </div>
								
                    <div class="clear"></div>
                    <div class="subForm hidden clearfix">
                        <div class="input shippingDate clearfix">
                            <c:choose>
                                <c:when test="${currentSiteId eq 'BedBathCanada'}">
                                    <c:set var="shippinDateIDName" value="shippingDateCA" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="shippinDateIDName" value="shippingDate" />
                                </c:otherwise>
                            </c:choose>
                            <div class="label">
                                <label id="lbl${shippinDateIDName}" for="${shippinDateIDName}">
                                    <bbbl:label key="lbl_shipping_pack_hold_date" language="${pageContext.request.locale.language}" />
                                </label>
                            </div>
                            <div class="text">
                                <div class="grid_2 alpha clearfix">
								  
										<input type="hidden" class="shippingStartDate" value = "${beddingShipAddrVO.shippingStartDate}" name="shippingStartDate" />
										<input type="hidden" class="shippingEndDate" value = "${beddingShipAddrVO.shippingEndDate}" name="shippingEndDate" />
								<c:choose>
									<c:when test="${not empty packAndHoldDate}">
										<c:choose>
											<c:when test="${currentSiteId eq 'BedBathCanada'}">
												 <fmt:formatDate value="${packAndHoldDate}" var="PreFilledDate" pattern="dd/MM/yyyy"/>
											</c:when>
											<c:otherwise>
												 <fmt:formatDate value="${packAndHoldDate}" var="PreFilledDate" pattern="MM/dd/yyyy"/>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
											<c:set var="PreFilledDate" value="${beddingShipAddrVO.shippingStartDate}"/>
									</c:otherwise>
								</c:choose>						   
								  <c:choose>
									<c:when test="${formExceptionFlag eq 'true'}">
										<dsp:input bean="BBBShippingGroupFormhandler.packNHoldDate" type="text"
                                        name="${shippinDateIDName}" id="${shippinDateIDName}" value="${PreFilledDate}">
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbl${shippinDateIDName} error${shippinDateIDName}"/>
                                    </dsp:input>
									</c:when>
									<c:otherwise>									
										<c:choose>
										  <c:when test="${beddingKit eq true || weblinkOrder eq true}">
										  	  <dsp:input bean="BBBShippingGroupFormhandler.packNHoldDate" type="text"
		                                        name="${shippinDateIDName}" id="${shippinDateIDName}"  value="${PreFilledDate}">
		                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        		<dsp:tagAttribute name="aria-labelledby" value="lbl${shippinDateIDName} error${shippinDateIDName}"/>
                                        	 </dsp:input>
											<dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="hidden" name="shippingOption1pack" id="shippingOption1packhidden" value=""/>
										  	  <dsp:input bean="BBBShippingGroupFormhandler.packNHoldDate" type="hidden"
		                                        name="${shippinDateIDName}" id="${shippinDateIDName}"  value="${PreFilledDate}" />
										  </c:when>
										  <c:otherwise>
											  <dsp:input bean="BBBShippingGroupFormhandler.packNHoldDate" type="text"
		                                        name="${shippinDateIDName}" id="${shippinDateIDName}"  value="${PreFilledDate}">
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                        			<dsp:tagAttribute name="aria-labelledby" value="lbl${shippinDateIDName} error${shippinDateIDName}"/>
                                                </dsp:input>
										  </c:otherwise>
										 </c:choose>										  
									</c:otherwise>
								</c:choose>
								</div>
                                <div class="grid_1 omega">
								  <%-- <c:if test="${beddingKit eq true}"> 
								    <c:set var="cal" value="hidden"/>                                    
								  </c:if> --%>
								  <div class="calendar"  id="shippingDateButton"></div>
                                </div>
                                <div class="cb">
                                    <label for="${shippinDateIDName}" class="error" generated="true"></label>
                                </div>
                            </div>
                        </div>
                        
                    </div>
					</c:otherwise>
				</c:choose>	
                </div>
                </c:if>
            </dsp:oparam>
       </dsp:droplet>
</dsp:page>
