<dsp:page>
	<div class="cartItemsTotalWrapper marTop_20">
		<ul class="clearfix noMar">
			<li class="grid_5 "><bbbl:label key="lbl_preview_bopus_message" language="<c:out param='${language}'/>"/></li>
			<li class="grid_5 alpha omega">
				<dl class="clearfix">
					<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
                					<dsp:param name="priceObject" param="shippingGroup" />
               						<dsp:oparam name="output">
							<dt>
								<p class="noMar"><span class="totalItems"><dsp:valueof param="priceInfoVO.itemCount"/></span> <bbbl:label key="lbl_cartdetail_items" language="<c:out param='${language}'/>"/></p>
								<p class="smallText noMar"></p>
							</dt>
							<dd class="fr"><dsp:valueof param="priceInfoVO.totalAmount" converter="defaultCurrency"/></dd>
							<dt>
								<p class="noMar"><bbbl:label key="lbl_preview_bopus_total_amount" language="<c:out param='${language}'/>"/></p>
								
							</dt>
							<dd class="fr"><dsp:valueof param="priceInfoVO.totalAmount" converter="defaultCurrency"/></dd>
						</dsp:oparam>
					</dsp:droplet>
				</dl>
			</li>
		</ul>
		<div class="fr marRight_10 marTop_5"><bbbl:label key="lbl_preview_bopus_amount_due" language="<c:out param='${language}'/>"/></div>
		<div class="clear"></div>
        <div class="clearfix padTop_10">
            <div class="alert alert-alert noMar"> 
                <h6><bbbl:label key="lbl_items_for_store_pickup_title" language="<c:out param='${language}'/>"/></h6>
                <p><bbbt:textArea key="txt_items_for_store_pickup_preview" language="<c:out param='${language}'/>"/></p>
            </div>
            <div class="clear"></div>
        </div>
		</div>
</dsp:page>