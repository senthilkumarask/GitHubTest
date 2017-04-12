<dsp:page>
<div class="addToCartModal">
<dsp:getvalueof var="commCount" param="count"></dsp:getvalueof>
                                <dsp:include src="${contextPath}/browse/addtocart_modal.jsp">
                                	<dsp:param name="quantity" value="${commCount}"/>
                                </dsp:include>
                                </div>
 </dsp:page>