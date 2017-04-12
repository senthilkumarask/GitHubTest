<dsp:page>
<div class="grid_6 omega">
	<div class="findARegistryForm clearfix">
		<div class="findARegistryFormTitle">
			<h2><bbbt:textArea key="txt_create_registry_title" language ="${pageContext.request.locale.language}"/></h2>
			<div class="grid_2">
				<p><bbbt:textArea key="txt_create_registry_desc" language ="${pageContext.request.locale.language}"/></p>
			</div>
			<div class="clear"></div>
		</div>
		<div class="createRegFormPlacement">
			<p><bbbt:textArea key="txt_create_registry_select" language ="${pageContext.request.locale.language}"/></p>
				<div id="findARegistrySelectType" class="width_2">
		    
					<dsp:include page="/giftregistry/my_registry_type_select.jsp" ></dsp:include>
		    
				</div>
			<div class="clear"></div>
		</div>
	</div>
</div>
</dsp:page>