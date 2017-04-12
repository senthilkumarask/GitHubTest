
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBSkuPropDetailsDroplet" />
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:param name="order" bean="ShoppingCart.current"/>
<dsp:getvalueof param="order" var="order"/>
 <h4 class="attentionHead"><bbbl:textArea key="txt_prop65PageTitle" language="${pageContext.request.locale.language}" /></h4>
 <p class="noMarTop"><bbbl:textArea key="txt_prop65PageTitleMsg" language="${pageContext.request.locale.language}" /></p>
<dsp:droplet name="BBBSkuPropDetailsDroplet">
  <dsp:param name="order" value="${order}"/>
  <dsp:oparam name="output">
  <dsp:getvalueof var="skuMap" param="skuProdStatus" scope="request"/>
	  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
	   <dsp:param value="${skuMap}" name="array"/>
	     	<dsp:getvalueof var="skuId" param="key"/>
	     	<dsp:getvalueof var="skuDetailsMap" value="${skuMap[skuId]}" />
	   <dsp:oparam name="output">
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
	   <dsp:param value="${skuDetailsMap}" name="array" />
	   <dsp:getvalueof var="propFlag" param="key"/>
	   <dsp:getvalueof var="displayName" value="${skuDetailsMap[propFlag]}"/>
	   <dsp:oparam name="output">
	    <c:choose>
		    <c:when test="${propFlag eq 'prop65Other' }">
		        <div class="clearfix cb"><bbbl:textArea key="txt_prop65OtherFlag" language="${pageContext.request.locale.language}" /></div>
				<div class="clear"></div>
		    </c:when>
	    	<c:when test="${propFlag eq 'prop65Dinnerware'}">
		        <div class="clearfix cb"><bbbl:textArea key="txt_prop65DinnerwareFlag" language="${pageContext.request.locale.language}" /></div>
				<div class="clear"></div>
	    	</c:when>
	    	<c:when test="${propFlag eq 'prop65Crystal'}">
		      	<div class="clearfix cb"><bbbl:textArea key="txt_prop65CrystalFlag" language="${pageContext.request.locale.language}" /></div>
			  	<div class="clear"></div>
	    	</c:when>
	    	<c:when test="${propFlag eq 'prop65Lighting'}">
		    	<div class="clearfix cb"><bbbl:textArea key="txt_prop65LightingFlag" language="${pageContext.request.locale.language}" /></div>
			 	<div class="clear"></div>			
	    	</c:when>
	    </c:choose>
		</dsp:oparam>
	</dsp:droplet>
			<div class="width_2 fl alpha  padTop_10 bold"><bbbl:label key="lbl_prop65_itemNumber" language="${pageContext.request.locale.language}" /></div>	
			<div class="width_4 fl alpha padTop_10 bold"><bbbl:label key="lbl_prop65_itemDescription" language="${pageContext.request.locale.language}" /></div>	
			<div class="width_2 fl alpha padBottom_20">${skuId}</div>
			<div class="width_4 fl alpha padBottom_20">${displayName}</div>
			<div class="clear"></div>
	</dsp:oparam>
	</dsp:droplet>

 </dsp:oparam>
</dsp:droplet> 

