<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
   xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
   xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
   xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
   xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" 
   xmlns:rc="RULECHECKER"
   exclude-result-prefixes="office table text rc">

	<!-- ============================================================================== -->
	<!-- Styles -->
	<!-- ============================================================================== -->
	<!-- Used styles -->
	<xsl:template name="setStyles">
		<office:automatic-styles>
			<!-- Column styles -->
			<style:style style:name="co1" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="3.000cm"/>
			</style:style>
			<style:style style:name="co2" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="6.000cm"/>
			</style:style>
			<style:style style:name="co3" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="12.000cm"/>
			</style:style>
			<style:style style:name="co4" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="6.000cm"/>
			</style:style>
			<style:style style:name="co5" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="3.000cm"/>
			</style:style>
			<style:style style:name="co6" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="3.000cm"/>
			</style:style>
			<style:style style:name="co7" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="40.000cm"/>
			</style:style>
			
			<!-- Row styles -->
			<style:style style:name="ro0" style:family="table-row">
				<style:table-row-properties fo:break-before="auto" style:row-height="0.500cm" fo:background-color="#cccccc"/>
			</style:style>
			<style:style style:name="ro1" style:family="table-row">
				<style:table-row-properties fo:break-before="auto" style:row-height="0.500cm" fo:background-color="#aaaaff"/>
			</style:style>
			<style:style style:name="ro2" style:family="table-row">
				<style:table-row-properties fo:break-before="auto" style:row-height="0.500cm" fo:background-color="#aaaacc"/>
			</style:style>
		</office:automatic-styles>
	</xsl:template>

	
	<!-- ============================================================================== -->
	<!-- Columns configuration -->
	<!-- ============================================================================== -->

	<!-- Columns configuration for mandatory elements -->
	<xsl:template name="ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co1" table:default-cell-style-name="Default"/> <!-- RuleCheckerVersion -->
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- ExecutionDate -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- Inputs -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- UID -->
		<table:table-column table:style-name="co5" table:default-cell-style-name="Default"/> <!-- Status -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Nb Failed -->
		<table:table-column table:style-name="co7" table:default-cell-style-name="Default"/> <!-- File -->
	</xsl:template>

	<!-- ============================================================================== -->
	<!-- Headers -->
	<!-- ============================================================================== -->

	<!-- Headers for mandatory elements -->
	<xsl:template name="Headers">
		<table:table-cell office:value-type="string"><text:p>R.C Version</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Execution date</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Inputs</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Rule name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Status</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Nb Failed</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>File</text:p></table:table-cell>
	</xsl:template>

	<!-- ============================================================================== -->
	<!-- Elements -->
	<!-- ============================================================================== -->

	<!-- Mandatory elements -->
	<xsl:template name="Elements1">
		<xsl:param name="elementNb" />
		<table:table-cell><text:p><xsl:value-of select="/rc:ruleReporting/rc:ruleCheckerVersion"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="/rc:ruleReporting/rc:executionDate"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="/rc:ruleReporting/rc:Inputs/rc:File[$elementNb]"/></text:p></table:table-cell>
	</xsl:template>
	
	<xsl:template name="Elements2">
		<table:table-cell><text:p><xsl:value-of select="/rc:ruleReporting/rc:ruleCheckerVersion"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="/rc:ruleReporting/rc:executionDate"/></text:p></table:table-cell>
		<table:table-cell><text:p></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="@UID"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:status"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:nbFailed"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:fileName"/></text:p></table:table-cell>
	</xsl:template>

	<!-- ============================================================================== -->
	<!-- Root entry -->
	<!-- ============================================================================== -->
	<xsl:template match="/">
		<office:document-content 
		  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" 
		  xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" 
		  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" 
		  xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" 
		  xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" 
		  xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" 
		  xmlns:xlink="http://www.w3.org/1999/xlink" 
		  xmlns:dc="http://purl.org/dc/elements/1.1/" 
		  xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" 
		  xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" 
		  xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" 
		  xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" 
		  xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" 
		  xmlns:math="http://www.w3.org/1998/Math/MathML" 
		  xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" 
		  xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" 
		  xmlns:ooo="http://openoffice.org/2004/office" 
		  xmlns:ooow="http://openoffice.org/2004/writer" 
		  xmlns:oooc="http://openoffice.org/2004/calc" 
		  xmlns:dom="http://www.w3.org/2001/xml-events" 
		  xmlns:xforms="http://www.w3.org/2002/xforms" 
		  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		  xmlns:rc="RULECHECKER"  
		  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" office:version="1.0">

			<xsl:call-template name="setStyles" />
			
			<office:body>
				<office:spreadsheet>
					<table:table>
						<!-- ================= Columns configuration ================= -->
						<xsl:call-template name="ColumnsConfiguration" />
						
						<!-- ================= Headers ================= -->
						<table:table-header-rows>
							<table:table-row table:style-name="ro0">
								<xsl:call-template name="Headers" />
							</table:table-row>
						</table:table-header-rows>

						<!-- ================= Body =========================== -->
						<xsl:for-each select="rc:ruleReporting/rc:Inputs/rc:File">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="Elements1" >
											<xsl:with-param name="elementNb" select="position()" />
										</xsl:call-template>
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="Elements1" >
											<xsl:with-param name="elementNb" select="position()" />
										</xsl:call-template>
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
								
						</xsl:for-each>
						
						<xsl:for-each select="rc:ruleReporting/rc:Rule">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="Elements2" />

									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="Elements2" />

									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
								
						</xsl:for-each>
					</table:table>
				</office:spreadsheet>
			</office:body>
			
		</office:document-content>
	</xsl:template>	
	
	

</xsl:stylesheet>