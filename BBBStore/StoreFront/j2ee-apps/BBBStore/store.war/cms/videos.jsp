<dsp:importbean bean="/com/bbb/cms/droplet/GuidesVideosDroplet" />
  <dsp:droplet name="GuidesVideosDroplet">
   	 
   	<dsp:oparam name="output">
   	<dsp:getvalueof var="finalVideoList" param="finalVideoList"/> 
   	 
   	
	   <iframe type="some_value_to_prevent_js_error_on_ie7" title="Live Clicker Videos" src="//www.liveclickerdocs.com/labs/custom-2.html?videos_id=${finalVideoList} " width='100%' height='100%'>
	   </iframe>
    </dsp:oparam>
</dsp:droplet>
