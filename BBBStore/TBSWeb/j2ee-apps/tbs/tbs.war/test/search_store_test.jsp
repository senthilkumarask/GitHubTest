<dsp:page>

<bbb:pageContainer>

	<dsp:droplet name="/com/bbb/tbs/test/SearchStoreTestDroplet">
	<dsp:param name="siteId" bean="/atg/multisite/Site.id"/>
    <dsp:param name="storeId" value="1079"/>
	<dsp:oparam name="output">

		<dsp:getvalueof var="lineItems" param="items"/>

		<c:forEach items="${lineItems}" var="lineItem" varStatus="status">
		    <br><br>
		    #<c:out value="${status.count}" />
 		    <br><br>
			longitude: <c:out value="${lineItem.longitude }"></c:out><br><br>

			latitude : <c:out value="${lineItem.latitude }"></c:out><br><br>

			storeType : <c:out value="${lineItem.storeType }"></c:out><br><br>

			hours : <c:out value="${lineItem.hours }"></c:out><br><br>

		</c:forEach>
	</dsp:oparam>
	</dsp:droplet>
</bbb:pageContainer>

</dsp:page>