
<dsp:page>
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="pageURL" param="pageURL" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BVQAContentDroplet"/>
	<div id="BVQAContainer">
		
	<%-- Commenting out SmartSEO as part of 34473									
	<c:if test="${SmartSEOOn}">
		 	<dsp:droplet name="BVQAContentDroplet">
				<dsp:param name="productId" param="productId"/>
				<dsp:param name="pageURL" value="${pageURL}"/>
				<dsp:param name="content" value="QUESTIONS"/>
				<dsp:oparam name="output">
				
					<dsp:valueof param="bvContent" valueishtml="true"/>
				
				</dsp:oparam>
			</dsp:droplet> 
	</c:if>
	--%>
	<img width="20" height="20" alt="" src="/_assets/global/images/widgets/small_loader.gif" >
	</div>
</dsp:page>