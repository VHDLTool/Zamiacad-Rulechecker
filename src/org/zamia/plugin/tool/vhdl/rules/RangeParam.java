package org.zamia.plugin.tool.vhdl.rules;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RangeParam implements IHandbookParam{

	public static enum Range {LT_GT, LET_GT, LET_GET, LT_GET};

	public static final String RANGE_PARAM_TAG = "hb:RangeParam";
	
	private static final String PARAM_ID_TAG = "hb:ParamID";
	private static final String RANGE_TAG = "hb:Range";
	private static final String VALUE_MIN_TAG = "hb:ValueMin";
	private static final String VALUE_MAX_TAG = "hb:ValueMax";
	
	private String m_paramId;
	private Range m_range;
	private Integer m_valueMin;
	private Integer m_valueMax;
	
	/**
	 * Instantiates an RangeParam from a given DOM element 
	 * @param element The DOM element representing the RangeParam
	 */
	public RangeParam(Element element)
	{
		try 
		{
			NodeList nodes = element.getChildNodes();
			if (nodes != null)
			{
				for (int i = 0; i < nodes.getLength(); i++)
				{
        			Node current = nodes.item(i);
        			if (current.getNodeType() == Node.ELEMENT_NODE)
        			{
						Element elem = (Element) current;
						if (elem != null)
						{
							switch (elem.getTagName())
							{
							case PARAM_ID_TAG:
								m_paramId = elem.getTextContent();
								break;
							case RANGE_TAG:
								m_range = Range.valueOf(elem.getTextContent().toUpperCase());
								break;
							case VALUE_MIN_TAG:
								m_valueMin = Integer.decode(elem.getTextContent());
								break;
							case VALUE_MAX_TAG:
								m_valueMax = Integer.decode(elem.getTextContent());
								break;
							}
						}
        			}
				}
			}
		}
		catch (Exception e)
		{
			m_paramId = null;
			m_valueMin = null;
			m_valueMax = null;
			m_range = null;
		}
	}
	
	/**
	 * @inheritDoc
	 */
	public String getParamId()
	{
		return m_paramId;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isParamValid()
	{
		return m_paramId != null && m_range != null && m_valueMin != null && m_valueMax != null;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isValid(Object object)
	{
		boolean isValid = false;
		
		if (object != null && object instanceof Integer)
		{
			Integer integer = (Integer) object;
			if (m_valueMin != null && m_valueMax != null && m_range != null)
			{
				switch (m_range)
				{
				case LT_GT:
					isValid = integer > m_valueMin && integer < m_valueMax;
					break;
				case LET_GT:
					isValid = integer > m_valueMin && integer <= m_valueMax;
					break;
				case LET_GET:
					isValid = integer >= m_valueMin && integer <= m_valueMax;
					break;
				case LT_GET:
					isValid = integer >= m_valueMin && integer < m_valueMax;
					break;
				}
			}
		}
		
		return isValid;
	}
	
	/**
	 * Gets the range operation
	 * @return The range operation
	 */
	public Range getRange()
	{
		return m_range;
	}
	
	/**
	 * Gets the min value
	 * @return The min value
	 */
	public Integer getValueMin()
	{
		return m_valueMin;
	}
	
	/**
	 * Gets the max value
	 * @return The max value
	 */
	public Integer getValueMax()
	{
		return m_valueMax;
	}
}
