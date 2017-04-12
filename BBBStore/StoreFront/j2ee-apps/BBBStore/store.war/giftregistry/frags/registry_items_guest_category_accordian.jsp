<dsp:page>
 <%--This JSP would load all the category C1 on page load.
--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GuestRegistryItemsDisplayDroplet" />
<dsp:droplet name="GuestRegistryItemsDisplayDroplet">
			<dsp:param name="eventTypeCode" param="eventTypeCode" />
				<dsp:oparam name="output">
						<dsp:getvalueof var="categoryBuckets" param="categoryBuckets"/>
				</dsp:oparam>
				</dsp:droplet>

<c:forEach items="${categoryBuckets}" var="categoryBucket" varStatus="index">
<div class="accordionReg${index.count} accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="In_${categoryBucket.key}">
<h2 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span><span class="accordionTitle block clearfix giftTitle"><a href="#" class="fl accordionLink">${fn:toLowerCase(categoryBucket.value)}</a></span></h2>
</div>
</c:forEach>

</dsp:page>