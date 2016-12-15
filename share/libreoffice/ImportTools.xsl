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

	<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
	<!-- Common types -->
	<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
	<!-- Columns configuration for file_entity_architecture -->
	<xsl:template name="file_entity_architectureColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- File name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Entity name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Architecture name -->
	</xsl:template>

	<!-- Columns configuration for process_Signal -->
	<xsl:template name="process_SignalColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process name -->
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- Process loc -->
	</xsl:template>

	<!-- Columns configuration for clockSourceEdge -->
	<xsl:template name="clockSourceEdgeColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clk src -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Clk src edge -->
	</xsl:template>

	<!-- Columns configuration for resetSourceLevel -->
	<xsl:template name="resetSourceLevelColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Rst src -->
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- Rst src Lvl -->
	</xsl:template>

	<!-- Columns configuration for file -->
	<xsl:template name="fileColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- File name -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- file nb lines -->
	</xsl:template>

	<!-- Columns configuration for entity -->
	<xsl:template name="entityColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Entity name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Entity loc -->
	</xsl:template>

	<!-- Columns configuration for architecture -->
	<xsl:template name="architectureColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Architecture name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Architecture loc -->
	</xsl:template>

	<!-- Columns configuration for process -->
	<xsl:template name="processColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Process name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Process loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Process Nb Lines -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Process is synchronous -->
	</xsl:template>

	<!-- Columns configuration for clockSignal -->
	<xsl:template name="clockSignalColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Clk signal name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Clk signal loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Clk signal Edge -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Process has synchronous RST -->
	</xsl:template>

	<!-- Columns configuration for resetSignal -->
	<xsl:template name="resetSignalColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Rst signal name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Rst signal loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Rst signal Level -->
	</xsl:template>

	<!-- Columns configuration for library -->
	<xsl:template name="libraryColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Library name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Library loc -->
	</xsl:template>

	<!-- Columns configuration for registerClockDomain -->
	<xsl:template name="registerClockDomainColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register clk src tag -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Violation type -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register src name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register src loc -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Register clk src tag -->
	</xsl:template>

	<!-- Columns configuration for IO -->
	<xsl:template name="IOColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- IO tag -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- IO name -->
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- IO type -->
	</xsl:template>

	<!-- Columns configuration for signal -->
	<xsl:template name="signalColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- signal tag -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- signal name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- signal type -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- signal loc -->
	</xsl:template>

	<!-- Columns configuration for input -->
	<xsl:template name="inputColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Input tag -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Input name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Input loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Input type declaration -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Input type -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Input range -->
	</xsl:template>
	
	<!-- Columns configuration for registerId -->
	<xsl:template name="registerIdColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register tag -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register type declaration -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register type -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register range -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register source -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register src loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Register clk src -->
	</xsl:template>
		<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->	
	
	<!-- Columns configuration for mandatory elements -->
	<xsl:template name="MandatoryColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- RuleCheckerVersion -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- featureName -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- ExecutionDate -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_AR3_T1 -->
	<xsl:template name="REQ_FEAT_AR3_T1ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Source name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Source ID -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Sink name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Sink ID -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Sink file name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Sink Loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Sink Type -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- Stage Level -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Stop Condition -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_AR3_T2 -->
	<xsl:template name="REQ_FEAT_AR3_T2ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Sink name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Sink ID -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Source name -->
		<table:table-column table:style-name="co2" table:default-cell-style-name="Default"/> <!-- Src ID -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Src file name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Src Loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Src Type -->
		<table:table-column table:style-name="co3" table:default-cell-style-name="Default"/> <!-- Stage Level -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Stop Condition -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_AR6_T1 -->
	<xsl:template name="REQ_FEAT_AR6_T1ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="file_entity_architectureColumnsConfiguration" /> <!--file_entity_architecture -->
		<xsl:call-template name="process_SignalColumnsConfiguration" />  <!-- process_Signal -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clk signal name -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Clk signal loc -->
		<xsl:call-template name="clockSourceEdgeColumnsConfiguration" />  <!-- resetSourceLevel -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_AR6_T2 -->
	<xsl:template name="REQ_FEAT_AR6_T2ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- File name -->
		<table:table-column table:style-name="co6" table:default-cell-style-name="Default"/> <!-- Entity name -->
		<xsl:call-template name="clockSourceEdgeColumnsConfiguration" /> <!--file_entity_architecture -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_AR6_T3 -->
	<xsl:template name="REQ_FEAT_AR6_T3ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="clockSourceEdgeColumnsConfiguration" /> <!--file_entity_architecture -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_AR7_T1 -->
	<xsl:template name="REQ_FEAT_AR7_T1ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="file_entity_architectureColumnsConfiguration" /> <!--file_entity_architecture -->
		<xsl:call-template name="process_SignalColumnsConfiguration" />  <!-- process_Signal -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Reset signal name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Reset signal loc -->
		<xsl:call-template name="resetSourceLevelColumnsConfiguration" />  <!-- resetSourceLevel -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_AR7_T2 -->
	<xsl:template name="REQ_FEAT_AR7_T2ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- file name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Entity name -->
		<xsl:call-template name="resetSourceLevelColumnsConfiguration" />  <!-- resetSourceLevel -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_AR7_T3 -->
	<xsl:template name="REQ_FEAT_AR7_T3ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="resetSourceLevelColumnsConfiguration" />  <!-- resetSourceLevel -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_RST_PRJ -->
	<xsl:template name="REQ_FEAT_RST_PRJColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Rst src tag -->
		<xsl:call-template name="file_entity_architectureColumnsConfiguration" />  <!-- file_entity_architecture -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- rst src name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Rst src type -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Rst src loc -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Rst src declaration -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_CLK_PRJ -->
	<xsl:template name="REQ_FEAT_CLK_PRJColumnsConfiguration">
		<!-- Format the columns of the table -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Clk src tag -->
		<xsl:call-template name="file_entity_architectureColumnsConfiguration" />  <!-- file_entity_architecture -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Clk src name -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Clk src type -->
		<table:table-column table:style-name="co4" table:default-cell-style-name="Default"/> <!-- Clk src loc -->
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_FN15 -->
	<xsl:template name="REQ_FEAT_FN15ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
		<xsl:call-template name="entityColumnsConfiguration" />
		<xsl:call-template name="architectureColumnsConfiguration" />
		<xsl:call-template name="processColumnsConfiguration" />
		<xsl:call-template name="clockSignalColumnsConfiguration" />
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_FN18 -->
	<xsl:template name="REQ_FEAT_FN18ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
		<xsl:call-template name="entityColumnsConfiguration" />
		<xsl:call-template name="architectureColumnsConfiguration" />
		<xsl:call-template name="processColumnsConfiguration" />
		<xsl:call-template name="clockSignalColumnsConfiguration" />
		<xsl:call-template name="resetSignalColumnsConfiguration" />
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_FN19 -->
	<xsl:template name="REQ_FEAT_FN19ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
		<xsl:call-template name="libraryColumnsConfiguration" />
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_FN20 -->
	<xsl:template name="REQ_FEAT_FN20ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_FN22 -->
	<xsl:template name="REQ_FEAT_FN22ColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
		<xsl:call-template name="entityColumnsConfiguration" />
		<xsl:call-template name="architectureColumnsConfiguration" />
		<xsl:call-template name="processColumnsConfiguration" />
		<xsl:call-template name="registerClockDomainColumnsConfiguration" />
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_IO_PRJ -->
	<xsl:template name="REQ_FEAT_IO_PRJColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
		<xsl:call-template name="entityColumnsConfiguration" />
		<xsl:call-template name="IOColumnsConfiguration" />
	</xsl:template>
	
	<!-- Columns configuration for REQ_FEAT_OBJ_ID -->
	<xsl:template name="REQ_FEAT_OBJ_IDColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
		<xsl:call-template name="entityColumnsConfiguration" />
		<xsl:call-template name="signalColumnsConfiguration" />
	</xsl:template>
	
	<!-- Columns configuration for REQ_FEAT_CNT_PROC -->
	<xsl:template name="REQ_FEAT_CNT_PROCColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
		<xsl:call-template name="entityColumnsConfiguration" />
		<xsl:call-template name="architectureColumnsConfiguration" />
		<xsl:call-template name="processColumnsConfiguration" />
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_COMB_INPUT -->
	<xsl:template name="REQ_FEAT_COMB_INPUTColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
		<xsl:call-template name="entityColumnsConfiguration" />
		<xsl:call-template name="architectureColumnsConfiguration" />
		<xsl:call-template name="processColumnsConfiguration" />
		<xsl:call-template name="inputColumnsConfiguration" />
	</xsl:template>

	<!-- Columns configuration for REQ_FEAT_REG_PRJ -->
	<xsl:template name="REQ_FEAT_REG_PRJColumnsConfiguration">
		<!-- Format the columns of the table -->
		<xsl:call-template name="fileColumnsConfiguration" />
		<xsl:call-template name="entityColumnsConfiguration" />
		<xsl:call-template name="architectureColumnsConfiguration" />
		<xsl:call-template name="processColumnsConfiguration" />
		<xsl:call-template name="clockSignalColumnsConfiguration" />
		<xsl:call-template name="registerIdColumnsConfiguration" />
	</xsl:template>

	<!-- ============================================================================== -->
	<!-- Headers -->
	<!-- ============================================================================== -->

	<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
	<!-- Common types -->
	<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->

	<!-- Headers for file_entity_architecture -->
	<xsl:template name="file_entity_architectureHeaders">
		<table:table-cell office:value-type="string"><text:p>File name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Entity name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Architecture name</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for process_Signal -->
	<xsl:template name="process_SignalHeaders">
		<table:table-cell office:value-type="string"><text:p>Process name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process loc</text:p></table:table-cell>
	</xsl:template>
	
	<!-- Headers for clockSourceEdge -->
	<xsl:template name="clockSourceEdgeHeaders">
		<table:table-cell office:value-type="string"><text:p>clk src</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clk src edge</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for resetSourceLevel -->
	<xsl:template name="resetSourceLevelHeaders">
		<table:table-cell office:value-type="string"><text:p>Rst src</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Rst src lvl</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for file -->
	<xsl:template name="fileHeaders">
		<table:table-cell office:value-type="string"><text:p>File name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>File nb line</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for entity -->
	<xsl:template name="entityHeaders">
		<table:table-cell office:value-type="string"><text:p>Entity name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Entity loc</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for architecture -->
	<xsl:template name="architectureHeaders">
		<table:table-cell office:value-type="string"><text:p>Architecture name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Architecture loc</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for process -->
	<xsl:template name="processHeaders">
		<table:table-cell office:value-type="string"><text:p>Process name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process nb line</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process is sync</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for clockSignal -->
	<xsl:template name="clockSignalHeaders">
		<table:table-cell office:value-type="string"><text:p>Clk signal name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clk signal loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clk signal edge</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Process has sync RST</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for resetSignal -->
	<xsl:template name="resetSignalHeaders">
		<table:table-cell office:value-type="string"><text:p>Rst signal name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Rst signal loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Rst signal lvl</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for library -->
	<xsl:template name="libraryHeaders">
		<table:table-cell office:value-type="string"><text:p>Library name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Library loc</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for registerClockDomain -->
	<xsl:template name="registerClockDomainHeaders">
		<table:table-cell office:value-type="string"><text:p>Register name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register clk src tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Violation type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register src name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register src loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register clk src tag</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for IO -->
	<xsl:template name="IOHeaders">
		<table:table-cell office:value-type="string"><text:p>IO tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>IO name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>IO type</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for signal -->
	<xsl:template name="signalHeaders">
		<table:table-cell office:value-type="string"><text:p>Signal tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Signal name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Signal type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Signal loc</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for input -->
	<xsl:template name="inputHeaders">
		<table:table-cell office:value-type="string"><text:p>Input tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Input name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Input loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Input type decl</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Input type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Input range</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for registerId -->
	<xsl:template name="registerIdHeaders">
		<table:table-cell office:value-type="string"><text:p>Register tag</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Violation type decl</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register range</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register src</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register src loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Register clk src</text:p></table:table-cell>
	</xsl:template>
	<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->

	<!-- Headers for mandatory elements -->
	<xsl:template name="MandatoryHeaders">
		<table:table-cell office:value-type="string"><text:p>R.C Version</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Feature name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Execution date</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for REQ_FEAT_AR3_T1 -->
	<xsl:template name="REQ_FEAT_AR3_T1Headers">
		<table:table-cell office:value-type="string"><text:p>Src name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Src ID</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sink name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sink ID</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sink file name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sink loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sink type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Stage level</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Stop condition</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for REQ_FEAT_AR3_T2 -->
	<xsl:template name="REQ_FEAT_AR3_T2Headers">
		<table:table-cell office:value-type="string"><text:p>Sink name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Sink ID</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Src name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Src ID</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Src file name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Src loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Src type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Stage level</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Stop condition</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for REQ_FEAT_AR6_T1 -->
	<xsl:template name="REQ_FEAT_AR6_T1Headers">
		<xsl:call-template name="file_entity_architectureHeaders" />
		<xsl:call-template name="process_SignalHeaders" />
		<table:table-cell office:value-type="string"><text:p>Clk signal name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clk signal loc</text:p></table:table-cell>
		<xsl:call-template name="clockSourceEdgeHeaders" /> 
	</xsl:template>

	<!-- Headers for REQ_FEAT_AR6_T2 -->
	<xsl:template name="REQ_FEAT_AR6_T2Headers">
		<table:table-cell office:value-type="string"><text:p>File name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Entity name</text:p></table:table-cell>
		<xsl:call-template name="clockSourceEdgeHeaders" /> 
	</xsl:template>

	<!-- Headers for REQ_FEAT_AR6_T3 -->
	<xsl:template name="REQ_FEAT_AR6_T3Headers">
		<xsl:call-template name="clockSourceEdgeHeaders" /> 
	</xsl:template>

	<!-- Headers for REQ_FEAT_AR7_T1 -->
	<xsl:template name="REQ_FEAT_AR7_T1Headers">
		<xsl:call-template name="file_entity_architectureHeaders" />
		<xsl:call-template name="process_SignalHeaders" />
		<table:table-cell office:value-type="string"><text:p>Reset signal name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Reset signal loc</text:p></table:table-cell>
		<xsl:call-template name="resetSourceLevelHeaders" /> 
	</xsl:template>

	<!-- Headers for REQ_FEAT_AR7_T2 -->
	<xsl:template name="REQ_FEAT_AR7_T2Headers">
		<table:table-cell office:value-type="string"><text:p>File name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Entity name</text:p></table:table-cell>
		<xsl:call-template name="resetSourceLevelHeaders" /> 
	</xsl:template>

	<!-- Headers for REQ_FEAT_AR7_T3 -->
	<xsl:template name="REQ_FEAT_AR7_T3Headers">
		<xsl:call-template name="resetSourceLevelHeaders" /> 
	</xsl:template>

	<!-- Headers for REQ_FEAT_RST_PRJ -->
	<xsl:template name="REQ_FEAT_RST_PRJHeaders">
		<table:table-cell office:value-type="string"><text:p>Rst src tag</text:p></table:table-cell>
		<xsl:call-template name="file_entity_architectureHeaders" /> 
		<table:table-cell office:value-type="string"><text:p>Rst src name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Rst src type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Rst src loc</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Rst src declaration</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for REQ_FEAT_CLK_PRJ -->
	<xsl:template name="REQ_FEAT_CLK_PRJHeaders">
		<table:table-cell office:value-type="string"><text:p>Clk src tag</text:p></table:table-cell>
		<xsl:call-template name="file_entity_architectureHeaders" /> 
		<table:table-cell office:value-type="string"><text:p>Clk src name</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clk src type</text:p></table:table-cell>
		<table:table-cell office:value-type="string"><text:p>Clk src loc</text:p></table:table-cell>
	</xsl:template>

	<!-- Headers for REQ_FEAT_FN15 -->
	<xsl:template name="REQ_FEAT_FN15Headers">
		<xsl:call-template name="fileHeaders" />
		<xsl:call-template name="entityHeaders" />
		<xsl:call-template name="architectureHeaders" />
		<xsl:call-template name="processHeaders" />
		<xsl:call-template name="clockSignalHeaders" />
	</xsl:template>

	<!-- Headers for REQ_FEAT_FN18 -->
	<xsl:template name="REQ_FEAT_FN18Headers">
		<xsl:call-template name="fileHeaders" />
		<xsl:call-template name="entityHeaders" />
		<xsl:call-template name="architectureHeaders" />
		<xsl:call-template name="processHeaders" />
		<xsl:call-template name="clockSignalHeaders" />
		<xsl:call-template name="resetSignalHeaders" />
	</xsl:template>

	<!-- Headers for REQ_FEAT_FN19 -->
	<xsl:template name="REQ_FEAT_FN19Headers">
		<xsl:call-template name="fileHeaders" />
		<xsl:call-template name="libraryHeaders" />
	</xsl:template>

	<!-- Headers for REQ_FEAT_FN20 -->
	<xsl:template name="REQ_FEAT_FN20Headers">
		<xsl:call-template name="fileHeaders" />
	</xsl:template>

	<!-- Headers for REQ_FEAT_FN22 -->
	<xsl:template name="REQ_FEAT_FN22Headers">
		<xsl:call-template name="fileHeaders" />
		<xsl:call-template name="entityHeaders" />
		<xsl:call-template name="architectureHeaders" />
		<xsl:call-template name="processHeaders" />
		<xsl:call-template name="registerClockDomainHeaders" />
	</xsl:template>

	<!-- Headers for REQ_FEAT_IO_PRJ -->
	<xsl:template name="REQ_FEAT_IO_PRJHeaders">
		<xsl:call-template name="fileHeaders" />
		<xsl:call-template name="entityHeaders" />
		<xsl:call-template name="IOHeaders" />
	</xsl:template>

	<!-- Headers for REQ_FEAT_OBJ_ID -->
	<xsl:template name="REQ_FEAT_OBJ_IDHeaders">
		<xsl:call-template name="fileHeaders" />
		<xsl:call-template name="entityHeaders" />
		<xsl:call-template name="signalHeaders" />
	</xsl:template>

	<!-- Headers for REQ_FEAT_CNT_PROC -->
	<xsl:template name="REQ_FEAT_CNT_PROCHeaders">
		<xsl:call-template name="fileHeaders" />
		<xsl:call-template name="entityHeaders" />
		<xsl:call-template name="architectureHeaders" />
		<xsl:call-template name="processHeaders" />
	</xsl:template>

	<!-- Headers for REQ_FEAT_COMB_INPUT -->
	<xsl:template name="REQ_FEAT_COMB_INPUTHeaders">
		<xsl:call-template name="fileHeaders" />
		<xsl:call-template name="entityHeaders" />
		<xsl:call-template name="architectureHeaders" />
		<xsl:call-template name="processHeaders" />
		<xsl:call-template name="inputHeaders" />
	</xsl:template>

	<!-- Headers for REQ_FEAT_REG_PRJ -->
	<xsl:template name="REQ_FEAT_REG_PRJHeaders">
		<xsl:call-template name="fileHeaders" />
		<xsl:call-template name="entityHeaders" />
		<xsl:call-template name="architectureHeaders" />
		<xsl:call-template name="processHeaders" />
		<xsl:call-template name="clockSignalHeaders" />
		<xsl:call-template name="registerIdHeaders" />
	</xsl:template>

	<!-- ============================================================================== -->
	<!-- Elements -->
	<!-- ============================================================================== -->

	<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
	<!-- Common types -->
	<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
	
	<!-- file_entity_architecture elements -->
	<xsl:template name="file_entity_architectureElements">
		<table:table-cell><text:p><xsl:value-of select="rc:entity/rc:fileName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:entity/rc:entityName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:entity/rc:architectureName"/></text:p></table:table-cell>
	</xsl:template>

	<!-- process_Signal elements -->
	<xsl:template name="process_SignalElements">
		<table:table-cell><text:p><xsl:value-of select="rc:processSignal/rc:processName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:processSignal/rc:processLoc"/></text:p></table:table-cell>
	</xsl:template>

	<!-- clockSource elements -->
	<xsl:template name="clockSourceEdgeElements">
			<table:table-cell><text:p><xsl:value-of select="rc:clockSourceEdgeInfo/rc:clockSource"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:clockSourceEdgeInfo/rc:clockSourceEdge"/></text:p></table:table-cell>
	</xsl:template>


	<!-- resetSourceLevel elements -->
	<xsl:template name="resetSourceLevelElements">
		<table:table-cell><text:p><xsl:value-of select="rc:resetSourceLevelInfo/rc:resetSource"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:resetSourceLevelInfo/rc:resetSourceLevel"/></text:p></table:table-cell>
	</xsl:template>

	<!-- file elements -->
	<xsl:template name="fileElements">
		<table:table-cell><text:p><xsl:value-of select="rc:file/rc:fileName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:file/rc:fileNbLine"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- entity elements -->
	<xsl:template name="entityElements">
		<table:table-cell><text:p><xsl:value-of select="rc:entity/rc:entityName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:entity/rc:entityLoc"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- architecture elements -->
	<xsl:template name="architectureElements">
		<table:table-cell><text:p><xsl:value-of select="rc:architecture/rc:architectureName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:architecture/rc:architectureLoc"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- process elements -->
	<xsl:template name="processElements">
		<table:table-cell><text:p><xsl:value-of select="rc:process/rc:processName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:process/rc:processLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:process/rc:processNbLine"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:process/rc:processIsSynchronous"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- clockSignal elements -->
	<xsl:template name="clockSignalElements">
		<table:table-cell><text:p><xsl:value-of select="rc:clockSignal/rc:clockSignalName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:clockSignal/rc:clockSignalLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:clockSignal/rc:clockSignalEdge"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:clockSignal/rc:processHasAsynchronousReset"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- resetSignal elements -->
	<xsl:template name="resetSignalElements">
		<table:table-cell><text:p><xsl:value-of select="rc:resetSignal/rc:resetSignalName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:resetSignal/rc:resetSignalLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:resetSignal/rc:resetSignalLevel"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- library elements -->
	<xsl:template name="libraryElements">
		<table:table-cell><text:p><xsl:value-of select="rc:library/rc:libraryName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:library/rc:libraryLoc"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- registerClockDomain elements -->
	<xsl:template name="registerClockDomainElements">
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerClockSourceTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:violationType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerSourceName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerSourceLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerSourceClockSourceTag"/></text:p></table:table-cell>
	</xsl:template>

	<!-- IO elements -->
	<xsl:template name="IOElements">
		<table:table-cell><text:p><xsl:value-of select="rc:IO/rc:IOTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:IO/rc:IOName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:IO/rc:IOType"/></text:p></table:table-cell>
	</xsl:template>

	<!-- signal elements -->
	<xsl:template name="signalElements">
		<table:table-cell><text:p><xsl:value-of select="rc:signal/rc:signalTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:signal/rc:signalName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:signal/rc:signalType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:signal/rc:signalLoc"/></text:p></table:table-cell>
	</xsl:template>

	<!-- input elements -->
	<xsl:template name="inputElements">
		<table:table-cell><text:p><xsl:value-of select="rc:input/rc:inputTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:input/rc:inputName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:input/rc:inputLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:input/rc:inputTypeDeclaration"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:input/rc:inputType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:input/rc:inputRange"/></text:p></table:table-cell>
	</xsl:template>

	<!-- registerId elements -->
	<xsl:template name="registerIdElements">
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerTag"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerTypeDeclaration"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerRange"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerSource"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerSourceLOC"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:register/rc:registerClockSource"/></text:p></table:table-cell>
	</xsl:template>

	<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->

	<!-- Mandatory elements -->
	<xsl:template name="MandatoryElements">
		<table:table-cell><text:p><xsl:value-of select="/rc:Feature/rc:ruleCheckerVersion"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="/rc:Feature/rc:featureName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="/rc:Feature/rc:executionDate"/></text:p></table:table-cell>
	</xsl:template>

	<!-- REQ_FEAT_AR3_T1 elements -->
	<xsl:template name="REQ_FEAT_AR3_T1Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:sourceName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sourceID"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sinkName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sinkID"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sinkfileName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sinkLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sinkType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:stageLevel"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:stopCondition"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- REQ_FEAT_AR3_T2 elements -->
	<xsl:template name="REQ_FEAT_AR3_T2Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:sinkName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sinkID"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sourceName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sourceID"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sourcefileName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sourceLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:sourceType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:stageLevel"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:stopCondition"/></text:p></table:table-cell>
	</xsl:template>
	
	<!-- REQ_FEAT_AR6_T1 elements -->
	<xsl:template name="REQ_FEAT_AR6_T1Elements">
		<xsl:call-template name="file_entity_architectureElements" />
		<xsl:call-template name="process_SignalElements" />
		<table:table-cell><text:p><xsl:value-of select="rc:clockSignalName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:clockSignalLoc"/></text:p></table:table-cell>
		<xsl:call-template name="clockSourceEdgeElements" />
	</xsl:template>
	
	<!-- REQ_FEAT_AR6_T2 elements -->
	<xsl:template name="REQ_FEAT_AR6_T2Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:fileName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:entityName"/></text:p></table:table-cell>
		<xsl:call-template name="clockSourceEdgeElements" />
	</xsl:template>
	
	<!-- REQ_FEAT_AR6_T3 elements -->
	<xsl:template name="REQ_FEAT_AR6_T3Elements">
		<xsl:call-template name="clockSourceEdgeElements">
			<xsl:with-param name="begin" select="rc:REQ_FEAT_AR6_T3"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- REQ_FEAT_AR7_T1 elements -->
	<xsl:template name="REQ_FEAT_AR7_T1Elements">
		<xsl:call-template name="file_entity_architectureElements" />
		<xsl:call-template name="process_SignalElements" />
		<table:table-cell><text:p><xsl:value-of select="rc:resetSignalName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:resetSignalLoc"/></text:p></table:table-cell>
		<xsl:call-template name="resetSourceLevelElements" />
	</xsl:template>
	
	<!-- REQ_FEAT_AR7_T2 elements -->
	<xsl:template name="REQ_FEAT_AR7_T2Elements">
		<table:table-cell><text:p><xsl:value-of select="rc:fileName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:entityName"/></text:p></table:table-cell>
		<xsl:call-template name="resetSourceLevelElements" />
	</xsl:template>

	<!-- REQ_FEAT_AR7_T3 elements -->
	<xsl:template name="REQ_FEAT_AR7_T3Elements">
		<xsl:call-template name="resetSourceLevelElements" />
	</xsl:template>

	<!-- REQ_FEAT_RST_PRJ elements -->
	<xsl:template name="REQ_FEAT_RST_PRJElements">
		<table:table-cell><text:p><xsl:value-of select="rc:resetSourceTag"/></text:p></table:table-cell>
		<xsl:call-template name="file_entity_architectureElements" />
		<table:table-cell><text:p><xsl:value-of select="rc:resetSourceName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:resetSourceType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:resetSourceLoc"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:resetSourceDeclaration"/></text:p></table:table-cell>
	</xsl:template>

	<!-- REQ_FEAT_CLK_PRJ elements -->
	<xsl:template name="REQ_FEAT_CLK_PRJElements">
		<table:table-cell><text:p><xsl:value-of select="rc:clockSourceTag"/></text:p></table:table-cell>
		<xsl:call-template name="file_entity_architectureElements" />
		<table:table-cell><text:p><xsl:value-of select="rc:clockSourceName"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:clockSourceType"/></text:p></table:table-cell>
		<table:table-cell><text:p><xsl:value-of select="rc:clockSourceLoc"/></text:p></table:table-cell>
	</xsl:template>

	<!-- REQ_FEAT_FN15 elements -->
	<xsl:template name="REQ_FEAT_FN15Elements">
		<xsl:call-template name="fileElements" />
		<xsl:call-template name="entityElements" />
		<xsl:call-template name="architectureElements" />
		<xsl:call-template name="processElements" />
		<xsl:call-template name="clockSignalElements" />
	</xsl:template>

	<!-- REQ_FEAT_FN18 elements -->
	<xsl:template name="REQ_FEAT_FN18Elements">
		<xsl:call-template name="fileElements" />
		<xsl:call-template name="entityElements" />
		<xsl:call-template name="architectureElements" />
		<xsl:call-template name="processElements" />
		<xsl:call-template name="clockSignalElements" />
		<xsl:call-template name="resetSignalElements" />
	</xsl:template>

	<!-- REQ_FEAT_FN19 elements -->
	<xsl:template name="REQ_FEAT_FN19Elements">
		<xsl:call-template name="fileElements" />
		<xsl:call-template name="libraryElements" />
	</xsl:template>

	<!-- REQ_FEAT_FN20 elements -->
	<xsl:template name="REQ_FEAT_FN20Elements">
		<xsl:call-template name="fileElements" />
	</xsl:template>

	<!-- REQ_FEAT_FN22 elements -->
	<xsl:template name="REQ_FEAT_FN22Elements">
		<xsl:call-template name="fileElements" />
		<xsl:call-template name="entityElements" />
		<xsl:call-template name="architectureElements" />
		<xsl:call-template name="processElements" />
		<xsl:call-template name="registerClockDomainElements" />
	</xsl:template>

	<!-- REQ_FEAT_IO_PRJ elements -->
	<xsl:template name="REQ_FEAT_IO_PRJElements">
		<xsl:call-template name="fileElements" />
		<xsl:call-template name="entityElements" />
		<xsl:call-template name="IOElements" />
	</xsl:template>

	<!-- REQ_FEAT_OBJ_ID elements -->
	<xsl:template name="REQ_FEAT_OBJ_IDElements">
		<xsl:call-template name="fileElements" />
		<xsl:call-template name="entityElements" />
		<xsl:call-template name="signalElements" />
	</xsl:template>

	<!-- REQ_FEAT_CNT_PROC elements -->
	<xsl:template name="REQ_FEAT_CNT_PROCElements">
		<xsl:call-template name="fileElements" />
		<xsl:call-template name="entityElements" />
		<xsl:call-template name="architectureElements" />
		<xsl:call-template name="processElements" />
	</xsl:template>

	<!-- REQ_FEAT_COMB_INPUT elements -->
	<xsl:template name="REQ_FEAT_COMB_INPUTElements">
		<xsl:call-template name="fileElements" />
		<xsl:call-template name="entityElements" />
		<xsl:call-template name="architectureElements" />
		<xsl:call-template name="processElements" />
		<xsl:call-template name="inputElements" />
	</xsl:template>

	<!-- REQ_FEAT_REG_PRJ elements -->
	<xsl:template name="REQ_FEAT_REG_PRJElements">
		<xsl:call-template name="fileElements" />
		<xsl:call-template name="entityElements" />
		<xsl:call-template name="architectureElements" />
		<xsl:call-template name="processElements" />
		<xsl:call-template name="clockSignalElements" />
		<xsl:call-template name="registerIdElements" />
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
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR3_T1">
								<xsl:call-template name="REQ_FEAT_AR3_T1ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR3_T2">
								<xsl:call-template name="REQ_FEAT_AR3_T2ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR6_T1">
								<xsl:call-template name="REQ_FEAT_AR6_T1ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR6_T2">
								<xsl:call-template name="REQ_FEAT_AR6_T2ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR6_T3">
								<xsl:call-template name="REQ_FEAT_AR6_T3ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR7_T1">
								<xsl:call-template name="REQ_FEAT_AR7_T1ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR7_T2">
								<xsl:call-template name="REQ_FEAT_AR7_T2ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR7_T3">
								<xsl:call-template name="REQ_FEAT_AR7_T3ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_RST_PRJ">
								<xsl:call-template name="REQ_FEAT_RST_PRJColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_CLK_PRJ">
								<xsl:call-template name="REQ_FEAT_CLK_PRJColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN15">
								<xsl:call-template name="REQ_FEAT_FN15ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN18">
								<xsl:call-template name="REQ_FEAT_FN18ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN19">
								<xsl:call-template name="REQ_FEAT_FN19ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN20">
								<xsl:call-template name="REQ_FEAT_FN20ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN22">
								<xsl:call-template name="REQ_FEAT_FN22ColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_IO_PRJ">
								<xsl:call-template name="REQ_FEAT_IO_PRJColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_OBJ_ID">
								<xsl:call-template name="REQ_FEAT_OBJ_IDColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_CNT_PROC">
								<xsl:call-template name="REQ_FEAT_CNT_PROCColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_COMB_INPUT">
								<xsl:call-template name="REQ_FEAT_COMB_INPUTColumnsConfiguration" />
							</xsl:when>
							<xsl:when test="/rc:Feature/rc:REQ_FEAT_REG_PRJ">
								<xsl:call-template name="REQ_FEAT_REG_PRJColumnsConfiguration" />
							</xsl:when>
						</xsl:choose>
						
						<!-- ================= Headers ================= -->
						<table:table-header-rows>
							<table:table-row table:style-name="ro0">
								<xsl:call-template name="MandatoryHeaders" />
								
								<xsl:choose>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR3_T1">
										<xsl:call-template name="REQ_FEAT_AR3_T1Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR3_T2">
										<xsl:call-template name="REQ_FEAT_AR3_T2Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR6_T1">
										<xsl:call-template name="REQ_FEAT_AR6_T1Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR6_T2">
										<xsl:call-template name="REQ_FEAT_AR6_T2Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR6_T3">
										<xsl:call-template name="REQ_FEAT_AR6_T3Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR7_T1">
										<xsl:call-template name="REQ_FEAT_AR7_T1Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR7_T2">
										<xsl:call-template name="REQ_FEAT_AR7_T2Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_AR7_T3">
										<xsl:call-template name="REQ_FEAT_AR7_T3Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_RST_PRJ">
										<xsl:call-template name="REQ_FEAT_RST_PRJHeaders" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_CLK_PRJ">
										<xsl:call-template name="REQ_FEAT_CLK_PRJHeaders" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN15">
										<xsl:call-template name="REQ_FEAT_FN15Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN18">
										<xsl:call-template name="REQ_FEAT_FN18Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN19">
										<xsl:call-template name="REQ_FEAT_FN19Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN20">
										<xsl:call-template name="REQ_FEAT_FN20Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_FN22">
										<xsl:call-template name="REQ_FEAT_FN22Headers" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_IO_PRJ">
										<xsl:call-template name="REQ_FEAT_IO_PRJHeaders" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_OBJ_ID">
										<xsl:call-template name="REQ_FEAT_OBJ_IDHeaders" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_CNT_PROC">
										<xsl:call-template name="REQ_FEAT_CNT_PROCHeaders" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_COMB_INPUT">
										<xsl:call-template name="REQ_FEAT_COMB_INPUTHeaders" />
									</xsl:when>
									<xsl:when test="/rc:Feature/rc:REQ_FEAT_REG_PRJ">
										<xsl:call-template name="REQ_FEAT_REG_PRJHeaders" />
									</xsl:when>
								</xsl:choose>
							</table:table-row>
						</table:table-header-rows>

						<!-- ================= Body =========================== -->
						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_AR3_T1">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR3_T1Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR3_T1Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_AR3_T2">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR3_T2Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR3_T2Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_AR6_T1">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR6_T1Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR6_T1Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_AR6_T2">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR6_T2Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR6_T2Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_AR6_T3">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR6_T3Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR6_T3Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_AR7_T1">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR7_T1Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR7_T1Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_AR7_T2">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR7_T2Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR7_T2Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_AR7_T3">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR7_T3Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_AR7_T3Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_RST_PRJ">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_RST_PRJElements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_RST_PRJElements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_CLK_PRJ">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_CLK_PRJElements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_CLK_PRJElements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_FN15">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN15Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN15Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_FN18">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN18Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN18Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_FN19">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN19Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN19Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_FN20">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN20Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN20Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_FN22">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN22Elements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_FN22Elements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_IO_PRJ">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_IO_PRJElements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_IO_PRJElements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
						
						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_OBJ_ID">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_OBJ_IDElements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_OBJ_IDElements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_CNT_PROC">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_CNT_PROCElements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_CNT_PROCElements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_COMB_INPUT">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_COMB_INPUTElements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_COMB_INPUTElements" />
									</table:table-row>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
						
						<xsl:for-each select="rc:Feature/rc:REQ_FEAT_REG_PRJ">
							<xsl:choose>
								<xsl:when test="not((position() mod 2) = 0)">
									<table:table-row table:style-name="ro2">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_REG_PRJElements" />
									</table:table-row>
								</xsl:when>
								<!-- ===== Change color ===== -->
								<xsl:otherwise>									
									<table:table-row table:style-name="ro1">
										<xsl:call-template name="MandatoryElements" />
										<xsl:call-template name="REQ_FEAT_REG_PRJElements" />
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