<!-- This page is using for rendering data -->
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/DisplayShippingRestrictionsDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:getvalueof var="skuId" param="skuId" />

<section class="shipRestrictionsApply" title="Shipping Restrictions Apply">

  <dsp:droplet name="DisplayShippingRestrictionsDroplet">
	  <dsp:param name="skuId" value="${skuId}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="skuShipRestrictionsVO" param="skuShipRestrictionsVO"/>
			<dsp:getvalueof var="nonShippableStates" param="skuShipRestrictionsVO.nonShippableStates"/>
			<c:if test="${not empty nonShippableStates}">
				<div class="shipRestrictText"><bbbl:label key="lbl_cartdetail_cantshipto" language="<c:out param='${language}'/>"/> ${nonShippableStates}</div>
				<c:if test="${not empty skuShipRestrictionsVO.zipCodesRestrictedForSkuMap}">
				<h4><bbbl:label key="lbl_other_restrictions" language="<c:out param='${language}'/>"/></h4>
				</c:if>
			</c:if>
			<div class="otherRestrictions">
			<div class="viewport">
				<div class="overview">
					<dsp:droplet name="ForEach">
					<dsp:param name="array" param="skuShipRestrictionsVO.zipCodesRestrictedForSkuMap"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="key" param="key"/>							
						<c:if test="${key ne null}">
							<c:set var="keyValue">${key}&nbsp;</c:set>
							<br/>
						
						</c:if>
						<div class="more-less zipRegion">
							<span class="zipRegionHead"> ${keyValue} </span>
							<dsp:getvalueof var="subelement" param="element" />
							<div class="zipRegionCodesBlock">
							<div class="more-block zipRegionCodes viewLessText"><bbbl:label key="lbl_cartdetail_cantshipto" language="<c:out param='${language}'/>"/> 
							<br/>
							<c:set var="elementWithSpace" value="${fn:replace(subelement, 
                                ',', ', ')}" /> ${elementWithSpace} </div>
							<a href="#" class="fr adjust hidden"><bbbl:label key="lbl_restrictions_view_more" language="<c:out param='${language}'/>"/></a>
							</div>
						</div>
					</dsp:oparam>
					</dsp:droplet>	
				</div>
			</div>
			</div>
		</dsp:oparam>
  </dsp:droplet>
</section>