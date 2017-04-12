<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="api"/>
<xsl:template match="/">
<html>

<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="json.js"></script>
<script type="text/javascript">

</script>


<body>

<h2>Description: Rest APIs</h2>
<form id="masterform" name="masterform" method="post" action="masterRender.jsp">

	<select onchange="document.location.href = 'testRestUI.jsp?var=' + this.value">
		<option>Select</option>
		<xsl:for-each select="rest-apis/rest-api">
			<option>
				<xsl:attribute name="value">
					<xsl:value-of select="api-def-xml-path"/>
				</xsl:attribute>
				<xsl:value-of select="api-name" />
			</option>
		</xsl:for-each>
	</select>
	<xsl:param name="sessionId"/>
	<input type="hidden" id="_dynSessConf" name="_dynSessConf">	
		<xsl:attribute name="value">
			<xsl:value-of select="$sessionId"/>
		</xsl:attribute>
	</input>
</form>


</body>
</html>
</xsl:template>
</xsl:stylesheet>