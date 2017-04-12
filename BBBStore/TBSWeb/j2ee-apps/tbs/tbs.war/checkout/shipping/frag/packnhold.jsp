<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>

<dsp:getvalueof var="formExceptionFlag" param="formExceptionFlag" />
<dsp:getvalueof var="siteId" bean="Site.id" />


	<c:if test="${not cmo}">
		<dsp:droplet name="BBBBeddingKitsAddrDroplet">
		  <dsp:param name="order" bean="ShoppingCart.current"/>
		  <dsp:param name="shippingGroup" param="order.shippingGroups"/>
		  <dsp:param name="" bean="ShoppingCart.current"/>
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
		        <c:set var="beddingKit" value="false"/>
		      </dsp:oparam>
		</dsp:droplet>
	</c:if>
    <dsp:droplet name="BBBPackNHoldDroplet">
        <dsp:param name="order" bean="ShoppingCart.current"/>
        <dsp:oparam name="output">
	        <label class="inline-rc checkbox packNHold_shipDate" for="shippingOption1pack">
	         	<dsp:droplet name="Switch">
	                <dsp:param name="value" param="isPackHold"/>
					<c:choose>
						<c:when test="${formExceptionFlag eq 'true'}">
							<dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="checkbox" name="shippingOption1pack" id="shippingOption1pack"/><span></span>
						</c:when>
						<c:otherwise>
							<dsp:oparam name="true">
								<c:choose>
							  		<c:when test="${beddingKit eq true}"><dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="checkbox" onfocus="blur();" name="shippingOption1pack" id="shippingOption1pack" checked="true" value="true"/><span></span></c:when>
							  		<c:when test="${weblinkOrder eq true}"><dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="checkbox" onfocus="blur();" name="shippingOption1pack" id="shippingOption1pack" checked="true" value="true"/><span></span></c:when>
									<c:otherwise><dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="checkbox" name="shippingOption1pack" id="shippingOption1pack" checked="false"/><span></span></c:otherwise>
								</c:choose>
							</dsp:oparam>
							<dsp:oparam name="false">
							 <dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="checkbox" name="shippingOption1pack" id="shippingOption1pack" checked="false"/><span></span>
						  </dsp:oparam>
						</c:otherwise>
					</c:choose>
	            </dsp:droplet>
	            <strong><bbbl:label key="lbl_shipping_pack_hold" language="${pageContext.request.locale.language}" /></strong> <bbbl:label key="lbl_shipping_pack_hold_msg" language="${pageContext.request.locale.language}" />
	         </label>
	         <div class="clear"></div>
	         <div class="subForm hidden clearfix small-12">
	             <div class="input shippingDate clearfix columns">
	                 <c:choose>
	                     <c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
	                         <c:set var="shippinDateIDName" value="shippingDateCA" />
	                     </c:when>
	                     <c:otherwise>
	                         <c:set var="shippinDateIDName" value="shippingDate" />
	                     </c:otherwise>
	                 </c:choose>
	                 <div class="label">
	                     <label id="lbl${shippinDateIDName}" for="${shippinDateIDName}">
	                         <bbbl:label key="lbl_shipping_pack_hold_date" language="${pageContext.request.locale.language}"/>
	                     </label>
	                 </div>
	                 <div class="text">
	                     <div class="grid_2 alpha clearfix">
							<input type="hidden" class="shippingStartDate" value = "${beddingShipAddrVO.shippingStartDate}" name="shippingStartDate" />
							<input type="hidden" class="shippingEndDate" value = "${beddingShipAddrVO.shippingEndDate}" name="shippingEndDate" />					   
							<c:choose>
								<c:when test="${formExceptionFlag eq 'true'}">
									<dsp:input bean="BBBShippingGroupFormhandler.packNHoldDate" type="text" name="${shippinDateIDName}" id="${shippinDateIDName}">
	                               		<dsp:tagAttribute name="aria-required" value="true"/>
	                               		<dsp:tagAttribute name="aria-labelledby" value="lbl${shippinDateIDName} error${shippinDateIDName}"/>
	                          		</dsp:input>
								</c:when>
								<c:otherwise>									
									<c:choose>
										<c:when test="${beddingKit eq true}"> 
						  					<dsp:input bean="BBBShippingGroupFormhandler.packNHoldDate" type="text"
				                               name="${shippinDateIDName}" value="${beddingShipAddrVO.shippingEndDate}" id="${shippinDateIDName}" disabled="true">
				                               <dsp:tagAttribute name="aria-required" value="true"/>
				                           		<dsp:tagAttribute name="aria-labelledby" value="lbl${shippinDateIDName} error${shippinDateIDName}"/>
		                           	 		</dsp:input>
											<dsp:input bean="BBBShippingGroupFormhandler.packNHold" type="hidden" name="shippingOption1pack" id="shippingOption1packhidden" value="true"/>
						  					<dsp:input bean="BBBShippingGroupFormhandler.packNHoldDate" type="hidden" name="${shippinDateIDName}" value="${beddingShipAddrVO.shippingEndDate}" id="${shippinDateIDName}" />
										</c:when>
									    <c:when test="${weblinkOrder eq true}"> 
									  	    <dsp:input bean="BBBShippingGroupFormhandler.packNHoldDate" type="text" iclass="weblinkOrder" name="${shippinDateIDName}" value="${beddingShipAddrVO.shippingEndDate}" id="${shippinDateIDName}" />
									    </c:when>
										<c:otherwise>
											<dsp:input bean="BBBShippingGroupFormhandler.packNHoldDate" paramvalue="packAndHoldDate" type="text" name="${shippinDateIDName}" id="${shippinDateIDName}" >
		                                       	<dsp:tagAttribute name="aria-required" value="true"/>
		                           				<dsp:tagAttribute name="aria-labelledby" value="lbl${shippinDateIDName} error${shippinDateIDName}"/>
		                                   	</dsp:input>
										</c:otherwise>
									</c:choose>										  
								</c:otherwise>
							</c:choose>
						</div>
	         			<div class="grid_1 omega">
							<c:if test="${beddingKit eq true}"> 
	 							<c:set var="cal" value="hidden"/>                                    
							</c:if>
							<div class="calendar"  id="shippingDateButton"></div>
	          			</div>
	          			<div class="cb">
	              			<label for="${shippinDateIDName}" class="error" generated="true"></label>
	          			</div>
	        		</div>
	      		</div>
	    	</div>
        </dsp:oparam>
   </dsp:droplet>
</dsp:page>
