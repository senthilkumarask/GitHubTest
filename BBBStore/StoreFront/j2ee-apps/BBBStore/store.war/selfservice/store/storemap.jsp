<dsp:page>
<div id="storeMapDialog">
<div > <img src="${imagePath}/_assets/global/images/logo/logo_by.png" width="191" height="33" alt="bed bath and beyond"></div>
	<dsp:form iclass="clearfix" id="storeMapForm">

		<div class="formRow grid_6 clearfix">
			<dsp:valueof param="address"/><br/>
			<dsp:valueof param="city"/>,
			<dsp:valueof param="state"/><br/>
			<dsp:valueof param="country"/><br/><br/>
			
			<input type="text" id="zoomId" hidden="false" value="<dsp:valueof param='zoom'/>" aria-required="false" />
			<div id="mapImageDiv">
	 			<img src="${imagePath}<dsp:valueof param='key'/>&center=<dsp:valueof param='lat'/>,<dsp:valueof param='lng'/>&zoom=<dsp:valueof param='zoom'/>&size=<dsp:valueof param='size'/>&pois=S,<dsp:valueof param='lat'/>,<dsp:valueof param='lng'/>,-20,-20"/>
			 
			 </div>
			<div class="formRow grid_6 clearfix">
			    <div id="MapImage"></div>
				<div class="grid_3 alpha">
					<div class="button">
						<input type="button" id="btnZoomIn" value="Zoom In" onclick="javascript:zoomIn('<dsp:valueof param="key"/>','<dsp:valueof param="lat"/>','<dsp:valueof param="lng"/>','<dsp:valueof param="zoom"/>','<dsp:valueof param="size"/>')" role="button" aria-pressed="false" aria-labelledby="btnZoomIn" />
					</div>
				</div>
				<div class="grid_3 alpha">
					<div class="button">
						<input type="button" value="Zoom Out" id="btnZoomOut" onclick="javascript:zoomOut('<dsp:valueof param="key"/>','<dsp:valueof param="lat"/>','<dsp:valueof param="lng"/>','<dsp:valueof param="zoom"/>','<dsp:valueof param="size"/>')" role="button" aria-pressed="false" aria-labelledby="btnZoomOut" />
					</div>
				</div>
					<BR/>
					<BR/>
				<div class="grid_2 alpha">
					<div class="button">
						<input type="button" value="Close" onclick="javascript:closeDialog();" role="button" aria-pressed="false" />
					</div>
				</div>		
			</div>
		</div>
	</dsp:form>
</div>

</dsp:page>


<script type="text/javascript">


function closeDialog(){
	jQuery('#mapModalDialogs').dialog('close');
	
}
function zoomIn(key,lat,lng,zoom,size){
	
	if($("#zoomId").val() <16)
	{
		$("#btnZoomOut").removeAttr("disabled");
		$("#btnZoomIn").removeAttr("disabled");
		$("#zoomId").val(Number($("#zoomId").val()) + 1);
		var varZoom = $("#zoomId").val();
		$("#mapImageDiv img").attr("src","/store/_assets/global/images/ajax-loader-large.gif");
		
		var src=key+"&center="+lat+","+lng+"&zoom="+varZoom+"&size="+size+"&pois=S,"+lat+","+lng+",-20,-20";
		
		$("#mapImageDiv img").attr("src",src);
		
		if($("#zoomId").val() ==16)
		{
			$("#btnZoomIn").attr("disabled","disabled");
		}
	}
}
function zoomOut(key,lat,lng,zoom,size){
	
	if($("#zoomId").val() >1)
	{
		$("#btnZoomOut").removeAttr("disabled");
		$("#btnZoomIn").removeAttr("disabled");
		$("#zoomId").val(Number($("#zoomId").val()) - 1);
		var varZoom = $("#zoomId").val();
		$("#mapImageDiv img").attr("src","/store/_assets/global/images/ajax-loader-large.gif");
		
		var src=key+"&center="+lat+","+lng+"&zoom="+varZoom+"&size="+size+"&pois=S,"+lat+","+lng+",-20,-20";
		
		$("#mapImageDiv img").attr("src",src);
		
		if($("#zoomId").val() ==1)
		{
			$("#btnZoomOut").attr("disabled","disabled");
		}
	}
}
</script>
