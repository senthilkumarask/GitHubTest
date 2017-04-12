<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="api"/>
<xsl:template match="/">


<link rel="stylesheet" type="text/css" href="css/12colgrid.css" />
<link rel="stylesheet" type="text/css" href="css/jquery-ui-1.8.16.custom.css"/>
<link rel="stylesheet" type="text/css" href="css/preview.css"/>


<h1>API: <xsl:value-of select="form/@name"/></h1>
<p>Description: <xsl:value-of select="form/description"/></p>
<form id="theform" name="theform" method="post">
	<xsl:param name="actionHttpHost"/>
	<xsl:attribute name="action">
		<xsl:value-of select="$actionHttpHost" /><xsl:value-of select="form/action" />
    </xsl:attribute>
	
	
    <xsl:for-each select="form/params/param">
    
	    <div class="input grid_3 suffix_9 alpha clearfix marTop_10">
	      <div class="label">
			<label for="reqBodyName">
				<xsl:value-of select="@name"/>      	
		      	<xsl:if test="@required = 'true'">
		      		<span class="required">*</span>
		      	</xsl:if>
	      	</label>
		  </div>
		  
		 <xsl:choose>
					<xsl:when test="@type = 'text'">
							<div class="text">
						      <input class="required reqBody">
								  <xsl:attribute name="id">
							        <xsl:value-of select="@id" />
							      </xsl:attribute>
							      <xsl:attribute name="type">
							        <xsl:value-of select="@type" />
							      </xsl:attribute>
							      <xsl:attribute name="name">
							        <xsl:value-of select="@id" />
							      </xsl:attribute>
							      <xsl:attribute name="value">
							        <xsl:value-of select="." />
							      </xsl:attribute>
							       <xsl:attribute name="class">
								       <xsl:value-of select="@class" />
								    </xsl:attribute>
							      
							      <xsl:choose>
									<xsl:when test="@disabled = 'disabled'">
										<xsl:attribute name="disabled">disabled</xsl:attribute>
									</xsl:when>
									<xsl:otherwise/>
								  </xsl:choose>
								  
							      <xsl:choose>
									<xsl:when test="@required = 'true'">
										<xsl:attribute name="required">true</xsl:attribute>
									</xsl:when>
									<xsl:otherwise/>
								  </xsl:choose>				
						     </input>
					      </div>	
					</xsl:when>
					<xsl:when test="@type = 'select'">
						  <div class="select">
						  <select class="selector reqHead">
								 <xsl:attribute name="id">
							        <xsl:value-of select="@id" />
							      </xsl:attribute>
							      <xsl:attribute name="type">
							        <xsl:value-of select="@type" />
							      </xsl:attribute>
							      <xsl:attribute name="name">
							        <xsl:value-of select="@id" />
							      </xsl:attribute>
								   <xsl:attribute name="class">
								       <xsl:value-of select="@class" />
								    </xsl:attribute>
									<xsl:choose>
									<xsl:when test="@required = 'true'">
										<xsl:attribute name="required">true</xsl:attribute>
									</xsl:when>
									<xsl:otherwise/>
								  </xsl:choose>	
							  <xsl:call-template name="tokenize">
								<xsl:with-param name="text"><xsl:value-of select="." /></xsl:with-param>
							  </xsl:call-template>
						</select>
						</div>
					</xsl:when>
					<xsl:otherwise/>
					
		 </xsl:choose>
	      
	    </div>
    </xsl:for-each>

  
	<xsl:param name="sessionId"/>
	<input type="hidden" name="_dynSessConf" class="reqBody">
		<xsl:attribute name="value">
			<xsl:value-of select="$sessionId" />
		</xsl:attribute>
	</input>
	
	<div class="grid_7 alpha clearfix btnSubmit">
		<div class="button button_active">
			<input type="submit" value="Submit" />
		</div>
		
	</div>
	<div id="loading" class="grid_3 hidden">loading.....</div>
</form>

</xsl:template>


<xsl:template name="tokenize">
	<xsl:param name="text" />
        <xsl:param name="separator" select="','"/>
        <xsl:choose>
            <xsl:when test="not(contains($text, $separator))">
                 <!-- Call Create Option -->
				 <xsl:call-template name="creatOption">
						<xsl:with-param name="text"><xsl:value-of select="$text"/></xsl:with-param>
				</xsl:call-template>
			</xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="tokenize">
                    <xsl:with-param name="text" select="substring-before($text, $separator)"/>
                </xsl:call-template>
                <xsl:call-template name="tokenize">
                    <xsl:with-param name="text" select="substring-after($text, $separator)"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
 </xsl:template>


 <xsl:template name="creatOption">
	<xsl:param name="text" />
	<xsl:param name="valSeparator" select="':'"/>
        <xsl:choose>
            <xsl:when test="not(contains($text, $valSeparator))">
                 <!-- Call Create Option -->
				 <option>
				    <xsl:attribute name="value"><xsl:value-of select="normalize-space($text)"/></xsl:attribute>
                    <xsl:value-of select="normalize-space($text)"/>
                </option>
			</xsl:when>
            <xsl:otherwise>
                <option>
				    <xsl:attribute name="value"><xsl:value-of select="normalize-space(substring-after($text, $valSeparator))"/></xsl:attribute>
                    <xsl:value-of select="normalize-space(substring-before($text, $valSeparator))"/>
                </option>
            </xsl:otherwise>
        </xsl:choose>
 </xsl:template>


</xsl:stylesheet>