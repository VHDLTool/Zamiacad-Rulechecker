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
				<style:table-column-properties fo:break-before="auto" style:column-width="1.000cm"/>
			</style:style>
			<style:style style:name="co2" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="2.000cm"/>
			</style:style>
			<style:style style:name="co3" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="3.000cm"/>
			</style:style>
			<style:style style:name="co4" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="4.000cm"/>
			</style:style>
			<style:style style:name="co6" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="6.000cm"/>
			</style:style>
			<style:style style:name="co10" style:family="table-column">
				<style:table-column-properties fo:break-before="auto" style:column-width="10.000cm"/>
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
	<xsl:template name="MandatoryColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- RuleCheckerVersion -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- RuleName -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- ExecutionDate -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- File -->
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- line -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- Entity -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- Architecture -->
	</xsl:template>

	<!-- Columns configuration for CNE_01200 -->
	<xsl:template name="CNE_01200ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for CNE_02300 -->
	<xsl:template name="CNE_02300ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- Instance -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- ClockBefore -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- ClockAfter --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for CNE_02400 -->
	<xsl:template name="CNE_02400ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- Instance --> 
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- ResetBefore --> 
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- ResetAfter --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for CNE_04900 -->
	<xsl:template name="CNE_04900ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clock --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_00200 -->
	<xsl:template name="STD_00200ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clock --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_00300 -->
	<xsl:template name="STD_00300ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Reset --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_00400 -->
	<xsl:template name="STD_00400ColumnsConfiguration">
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_03600_R1 -->
	<xsl:template name="STD_03600R1ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Reset --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process --> 
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- SourceTag --> 
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- Level --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_03600_R2 -->
	<xsl:template name="STD_03600R2ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- SourceTag --> 
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- Level --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_03800 -->
	<xsl:template name="STD_03800ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Register --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clock --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_04500 -->
	<xsl:template name="STD_04500ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- SourceTag --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clock --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- SignalType --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>
		
	<!-- Columns configuration for STD_04600 -->
	<xsl:template name="STD_04600ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- SourceTag --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clock --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- SignalType --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_04700 -->
	<xsl:template name="STD_04700ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- SourceTag --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clock --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- SignalType --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_04800_R1 -->
	<xsl:template name="STD_04800R1ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clock --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process --> 
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- SourceTag --> 
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- Edge --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>
	
	<!-- Columns configuration for STD_04800_R2 -->
	<xsl:template name="STD_04800R2ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- SourceTag --> 
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- Edge --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>
	
	<!-- Columns configuration for STD_05000 -->
	<xsl:template name="STD_05000ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Sensitivity --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_05300 -->
	<xsl:template name="STD_05300ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Sensitivity --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_01800 -->
	<xsl:template name="STD_01800ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Library --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- Columns configuration for STD_03700 -->
	<xsl:template name="STD_03700ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Reset --> 
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- SignalType --> 
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarError -->
		<table:table-column table:style-name="co10" table:default-cell-style-name="Default"/> <!-- SonarRemediationMsg -->
	</xsl:template>

	<!-- ============================================================================== -->
	<!-- Headers -->
	<!-- ============================================================================== -->

	<!-- Headers for mandatory elements -->
	<xsl:template name="MandatoryHeaders">
		<table:table-cell office:value-type="string"><text:p>R.C Version</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Rule name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Execution date</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>File</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Line</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Entity</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Architecture</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for CNE_01200 -->
	<xsl:template name="CNE_01200Headers">
		<table:table-cell office:value-type="string"><text:p>Process</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for CNE_02300 -->
	<xsl:template name="CNE_02300Headers">
		<table:table-cell office:value-type="string"><text:p>Instance</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clock Before</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clock After</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for CNE_02400 -->
	<xsl:template name="CNE_02400Headers">
		<table:table-cell office:value-type="string"><text:p>Instance</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Reset Before</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Reset After</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>
	
	<!-- Headers for CNE_04900 -->
	<xsl:template name="CNE_04900Headers">
		<table:table-cell office:value-type="string"><text:p>Clock</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_00200 -->
	<xsl:template name="STD_00200Headers">
		<table:table-cell office:value-type="string"><text:p>Clock</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_00300 -->
	<xsl:template name="STD_00300Headers">
		<table:table-cell office:value-type="string"><text:p>Reset</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_00400 -->
	<xsl:template name="STD_00400Headers">
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>
	
	<!-- Headers for STD_03600_R1 -->
	<xsl:template name="STD_03600R1Headers">
		<table:table-cell office:value-type="string"><text:p>Reset</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Source Tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Level</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_03600_R2 -->
	<xsl:template name="STD_03600R2Headers">
		<table:table-cell office:value-type="string"><text:p>Source Tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Level</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_03800 -->
	<xsl:template name="STD_03800Headers">
		<table:table-cell office:value-type="string"><text:p>Register</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clock</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_04500 -->
	<xsl:template name="STD_04500Headers">
		<table:table-cell office:value-type="string"><text:p>Source Tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clock</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Signal Type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_04600 -->
	<xsl:template name="STD_04600Headers">
		<table:table-cell office:value-type="string"><text:p>Source Tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clock</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Signal Type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_04700 -->
	<xsl:template name="STD_04700Headers">
		<table:table-cell office:value-type="string"><text:p>Source Tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clock</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Signal Type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_04800_R1 -->
	<xsl:template name="STD_04800R1Headers">
		<table:table-cell office:value-type="string"><text:p>Clock</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Source Tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Edge</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_04800_R2 -->
	<xsl:template name="STD_04800R2Headers">
		<table:table-cell office:value-type="string"><text:p>Source Tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Edge</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>
	
	<!-- Headers for STD_05000 -->
	<xsl:template name="STD_05000Headers">
		<table:table-cell office:value-type="string"><text:p>Process</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sensitivity</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_05300 -->
	<xsl:template name="STD_05300Headers">
		<table:table-cell office:value-type="string"><text:p>Process</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sensitivity</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_01800 -->
	<xsl:template name="STD_01800Headers">
		<table:table-cell office:value-type="string"><text:p>Library</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for STD_03700 -->
	<xsl:template name="STD_03700Headers">
		<table:table-cell office:value-type="string"><text:p>Reset</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>SignalType</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Error</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sonar Remediation Message</text:p></table:table-cell>
	</xsl:template>

	<!-- ============================================================================== -->
	<!-- Elements -->
	<!-- ============================================================================== -->

	<!-- Mandatory elements -->
	<xsl:template name="MandatoryElements">
		<table:table-cell><text:p><xsl:value-of select="/rc:ReportRule/rc:RuleCheckerVersion"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="/rc:ReportRule/rc:RuleName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="/rc:ReportRule/rc:ExecutionDate"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:File"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:Line"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:Entity"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:Architecture"/></text:p></table:table-cell>
	</xsl:template>

	<!-- CNE_01200 elements -->
	<xsl:template name="CNE_01200Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_01200/rc:Process"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_01200/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_01200/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- CNE_02300 elements -->
	<xsl:template name="CNE_02300Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02300/rc:Instance"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02300/rc:ClockBefore"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02300/rc:ClockAfter"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02300/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02300/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- CNE_02400 elements -->
	<xsl:template name="CNE_02400Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02400/rc:Instance"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02400/rc:ResetBefore"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02400/rc:ResetAfter"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02400/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_02400/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- CNE_04900 elements -->
	<xsl:template name="CNE_04900Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_04900/rc:Clock"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_04900/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:CNE_04900/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- STD_00200 elements -->
	<xsl:template name="STD_00200Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_00200/rc:Clock"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_00200/rc:Process"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_00200/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_00200/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- STD_00300 elements -->
	<xsl:template name="STD_00300Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_00300/rc:Reset"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_00300/rc:Process"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_00300/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_00300/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- STD_00400 elements -->
	<xsl:template name="STD_00400Elements">
		<!-- No expected data. Nothing to do -->
	</xsl:template>

	<!-- STD_03600_R1 elements -->
	<xsl:template name="STD_03600R1Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R1/rc:Reset"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R1/rc:Process"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R1/rc:SourceTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R1/rc:Level"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R1/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R1/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- STD_03600_R2 elements -->
	<xsl:template name="STD_03600R2Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R2/rc:SourceTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R2/rc:Level"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R2/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03600_R2/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- STD_03800 elements -->
	<xsl:template name="STD_03800Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03800/rc:Register"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03800/rc:Clock"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03800/rc:Process"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03800/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03800/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- STD_04500 elements -->
	<xsl:template name="STD_04500Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04500/rc:SourceTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04500/rc:Clock"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04500/rc:SignalType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04500/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04500/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- STD_04600 elements -->
	<xsl:template name="STD_04600Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04600/rc:SourceTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04600/rc:Clock"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04600/rc:SignalType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04600/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04600/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- STD_04700 elements -->
	<xsl:template name="STD_04700Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04700/rc:SourceTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04700/rc:Clock"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04700/rc:SignalType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04700/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04700/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- STD_04800_R1 elements -->
	<xsl:template name="STD_04800R1Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R1/rc:Clock"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R1/rc:Process"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R1/rc:SourceTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R1/rc:Edge"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R1/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R1/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- STD_04800_R2 elements -->
	<xsl:template name="STD_04800R2Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R2/rc:SourceTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R2/rc:Edge"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R2/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_04800_R2/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- STD_05000 elements -->
	<xsl:template name="STD_05000Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_05000/rc:Process"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_05000/rc:Sensitivity"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_05000/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_05000/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>

	<!-- STD_05300 elements -->
	<xsl:template name="STD_05300Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_05300/rc:Process"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_05300/rc:Sensitivity"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_05300/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_05300/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- STD_01800 elements -->
	<xsl:template name="STD_01800Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_01800/rc:Library"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_01800/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_01800/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- STD_03700 elements -->
	<xsl:template name="STD_03700Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03700/rc:Reset"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03700/rc:SignalType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03700/rc:SonarQubeMsg/rc:SonarError"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:STD_03700/rc:SonarQubeMsg/rc:SonarRemediationMsg"/></text:p></table:table-cell>
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
						<xsl:call-template name="MandatoryColumnsConfiguration" />
						<xsl:choose>
							<!-- CNE rules -->
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:CNE_01200">
								<xsl:call-template name="CNE_01200ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:CNE_02300">
								<xsl:call-template name="CNE_02300ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:CNE_02400">
								<xsl:call-template name="CNE_02400ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:CNE_04900">
								<xsl:call-template name="CNE_04900ColumnsConfiguration" />
							</xsl:when>
							
							<!-- STD rules -->
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_00200">
								<xsl:call-template name="STD_00200ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_00300">
								<xsl:call-template name="STD_00300ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_00400">
								<xsl:call-template name="STD_00400ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_03600_R1">
								<xsl:call-template name="STD_03600R1ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_03600_R2">
								<xsl:call-template name="STD_03600R2ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_03800">
								<xsl:call-template name="STD_03800ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04500">
								<xsl:call-template name="STD_04500ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04600">
								<xsl:call-template name="STD_04600ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04700">
								<xsl:call-template name="STD_04700ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04800_R1">
								<xsl:call-template name="STD_04800R1ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04800_R2">
								<xsl:call-template name="STD_04800R2ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_05000">
								<xsl:call-template name="STD_05000ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_05300">
								<xsl:call-template name="STD_05300ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_01800">
								<xsl:call-template name="STD_01800ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_03700">
								<xsl:call-template name="STD_03700ColumnsConfiguration" />
							</xsl:when>
						</xsl:choose>
						
						<!-- ================= Headers ================= -->
						<table:table-header-rows>
							<table:table-row table:style-name="ro0">
								<xsl:call-template name="MandatoryHeaders" />
								
								<xsl:choose>
									<!-- CNE rules -->
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:CNE_01200">
										<xsl:call-template name="CNE_01200Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:CNE_02300">
										<xsl:call-template name="CNE_02300Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:CNE_02400">
										<xsl:call-template name="CNE_02400Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:CNE_04900">
										<xsl:call-template name="CNE_04900Headers" />
									</xsl:when>
									<!-- STD rules -->
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_00200">
										<xsl:call-template name="STD_00200Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_00300">
										<xsl:call-template name="STD_00300Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_00400">
										<xsl:call-template name="STD_00400Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_03600_R1">
										<xsl:call-template name="STD_03600R1Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_03600_R2">
										<xsl:call-template name="STD_03600R2Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_03800">
										<xsl:call-template name="STD_03800Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04500">
										<xsl:call-template name="STD_04500Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04600">
										<xsl:call-template name="STD_04600Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04700">
										<xsl:call-template name="STD_04700Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04800_R1">
										<xsl:call-template name="STD_04800R1Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_04800_R2">
										<xsl:call-template name="STD_04800R2Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_05000">
										<xsl:call-template name="STD_05000Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_05300">
										<xsl:call-template name="STD_05300Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_01800">
										<xsl:call-template name="STD_01800Headers" />
									</xsl:when>
									<xsl:when test="/rc:ReportRule/rc:RuleFailure/rc:STD_03700">
										<xsl:call-template name="STD_03700Headers" />
									</xsl:when>
								</xsl:choose>
							</table:table-row>
						</table:table-header-rows>

						<!-- ================= Body =========================== -->
						<xsl:for-each select="rc:ReportRule/rc:RuleFailure">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />

										<xsl:choose>
											<!-- CNE rules -->
											<xsl:when test="rc:CNE_01200">
												<xsl:call-template name="CNE_01200Elements" />
											</xsl:when>
											<xsl:when test="rc:CNE_02300">
												<xsl:call-template name="CNE_02300Elements" />
											</xsl:when>
											<xsl:when test="rc:CNE_02400">
												<xsl:call-template name="CNE_02400Elements" />
											</xsl:when>
											<xsl:when test="rc:CNE_04900">
												<xsl:call-template name="CNE_04900Elements" />
											</xsl:when>
											<!-- STD rules -->
											<xsl:when test="rc:STD_00200">
												<xsl:call-template name="STD_00200Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_00300">
												<xsl:call-template name="STD_00300Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_00400">
												<xsl:call-template name="STD_00400Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_03600_R1">
												<xsl:call-template name="STD_03600R1Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_03600_R2">
												<xsl:call-template name="STD_03600R2Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_03800">
												<xsl:call-template name="STD_03800Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04500">
												<xsl:call-template name="STD_04500Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04600">
												<xsl:call-template name="STD_04600Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04700">
												<xsl:call-template name="STD_04700Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04800_R1">
												<xsl:call-template name="STD_04800R1Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04800_R2">
												<xsl:call-template name="STD_04800R2Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_05000">
												<xsl:call-template name="STD_05000Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_05300">
												<xsl:call-template name="STD_05300Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_01800">
												<xsl:call-template name="STD_01800Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_03700">
												<xsl:call-template name="STD_03700Elements" />
											</xsl:when>
										</xsl:choose>

									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />

										<xsl:choose>
											<!-- CNE rules -->
											<xsl:when test="rc:CNE_01200">
												<xsl:call-template name="CNE_01200Elements" />
											</xsl:when>
											<xsl:when test="rc:CNE_02300">
												<xsl:call-template name="CNE_02300Elements" />
											</xsl:when>
											<xsl:when test="rc:CNE_02400">
												<xsl:call-template name="CNE_02400Elements" />
											</xsl:when>
											<xsl:when test="rc:CNE_04900">
												<xsl:call-template name="CNE_04900Elements" />
											</xsl:when>
											<!-- STD rules -->
											<xsl:when test="rc:STD_00200">
												<xsl:call-template name="STD_00200Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_00300">
												<xsl:call-template name="STD_00300Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_00400">
												<xsl:call-template name="STD_00400Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_03600_R1">
												<xsl:call-template name="STD_03600R1Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_03600_R2">
												<xsl:call-template name="STD_03600R2Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_03800">
												<xsl:call-template name="STD_03800Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04500">
												<xsl:call-template name="STD_04500Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04600">
												<xsl:call-template name="STD_04600Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04700">
												<xsl:call-template name="STD_04700Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04800_R1">
												<xsl:call-template name="STD_04800R1Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_04800_R2">
												<xsl:call-template name="STD_04800R2Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_05000">
												<xsl:call-template name="STD_05000Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_05300">
												<xsl:call-template name="STD_05300Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_01800">
												<xsl:call-template name="STD_01800Elements" />
											</xsl:when>
											<xsl:when test="rc:STD_03700">
												<xsl:call-template name="STD_03700Elements" />
											</xsl:when>
										</xsl:choose>

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