<dsp:page>
	<%-- <div class="grid_4" id="rightCol">
		<div class="spclSectionBoxGreen" id="summarySpcl">
			<bbbt:textArea key="txt_spc_preview_right_postagepaid" language="<c:out param='${language}'/>"/>
		</div>
		
		<div class="marTop_10 smallText">
			<bbbl:label key="lbl_spc_preview_customerservicemessage" language="<c:out param='${language}'/>"/>
		</div>
		
	</div> --%>
	
                     <div class="grid_4 start" id="orderSummary">
                    <dsp:include page="/checkout/singlePage/order_summary_frag.jsp">
                        <dsp:param name="displayTax" value="true"/>
                        <dsp:param name="displayShippingDisclaimer" value="true"/>
                    </dsp:include>
	</div>
</dsp:page>

