<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	
	<dsp:getvalueof var="shippingGroup" param='shippingGroup'/>
	<c:set var="cmo" value="false" />
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
		<dsp:param name="elementName" value="commerceItemRelationship" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="cmo" param="commerceItemRelationship.commerceItem.CMO" />
		</dsp:oparam>
	</dsp:droplet>
	
	<div class="cartItemsTotalWrapper marTop_20" id="cartItemsTotalWrapper_<dsp:valueof param='shippingGroup.id'/>">
		<ul class="clearfix noMar address">
			<li class="columns large-6"><bbbl:label key="lbl_preview_bopus_message" language="<c:out param='${language}'/>"/></li>
			<li class="columns large-6">
				<dl class="right-align">
					<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
                					<dsp:param name="priceObject" param="shippingGroup" />
               						<dsp:oparam name="output">
							<dt class="large-8 columns">
								<h3 class="totalItems"><dsp:valueof param="priceInfoVO.itemCount"/>&nbsp;<bbbl:label key="lbl_cartdetail_items" language="<c:out param='${language}'/>"/></h3>
							</dt>
							<dd class="medium-4 columns"><dsp:valueof param="priceInfoVO.totalAmount" converter="currency"/></dd>
							<dt class="large-8 columns">
								<h3><bbbl:label key="lbl_preview_bopus_total_amount" language="<c:out param='${language}'/>"/></h3>
							</dt>
							<dd class="medium-4 columns"><dsp:valueof param="priceInfoVO.totalAmount" converter="currency"/></dd>
						</dsp:oparam>
					</dsp:droplet>
				</dl>
			</li>
		</ul>
		<c:if test="${not cmo}">
			<div class="columns small-offset-6"><bbbl:label key="lbl_preview_bopus_amount_due" language="<c:out param='${language}'/>"/></div>
	            <div class="alert alert-alert noMar"> 
	                <h2><bbbl:label key="lbl_items_for_store_pickup_title" language="<c:out param='${language}'/>"/></h2>
	                <p><bbbt:textArea key="txt_items_for_store_pickup_preview" language="<c:out param='${language}'/>"/></p>
	            </div>
			</div>
		</c:if>
</dsp:page>