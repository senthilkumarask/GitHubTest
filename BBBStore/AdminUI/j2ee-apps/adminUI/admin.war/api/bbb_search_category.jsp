<%@ taglib uri="dspTaglib" prefix="dsp"%>
<dsp:page>
	<dsp:importbean bean="/com/admin/droplet/BBBCategoryDroplet" />

	<dsp:droplet name="BBBCategoryDroplet">
		<dsp:param name="category_id" param="search_term" />
		<dsp:oparam name="output">
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>