package org.zamia.plugin.tool.vhdl.rules;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StringParam implements IHandbookParam
{

	public static enum Position {PREFIX, CONTAIN, EQUAL, SUFFIX};

	public static final String STRING_PARAM_TAG = "hb:StringParam";

	
	private static final String PARAM_ID_TAG = "hb:ParamID";
	private static final String POSITION_TAG = "hb:Position";
	private static final String VALUE_TAG = "hb:Value";
	
	private Position m_position;
	private String m_value;
	private String m_paramId;
	
	/**
	 * Instantiates an StringParam from a given DOM element 
	 * @param element The DOM element representing the StringParam
	 */
	public StringParam(Element element)
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
							case POSITION_TAG:
								m_position = Position.valueOf(elem.getTextContent().toUpperCase());
								break;
							case VALUE_TAG:
								m_value = elem.getTextContent().toUpperCase();
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
			m_position = null;
			m_value = null;
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
		return m_paramId!= null && m_position != null && m_value != null;
	}
	
	/**
	 * @inheritDoc
	 */
	public boolean isValid(Object object)
	{
		boolean isValid = false;
		
		if (object != null && object instanceof String)
		{
			String string = ((String) object).toUpperCase();
			if (m_value != null && m_position != null)
			{
				switch (m_position)
				{
				case PREFIX:
					isValid = string.startsWith(m_value);
					break;
				case CONTAIN:
					isValid = string.contains(m_value);
					break;
				case EQUAL:
					isValid = string.equals(m_value);
					break;
				case SUFFIX:
					isValid = string.endsWith(m_value);
					break;
				}
			}
		}
		
		return isValid;
	}

	/**
	 * Gets the position operation
	 * @return The position operation
	 */
	public Position getPosition()
	{
		return m_position;
	}
	
	/**
	 * Gets the value
	 * @return The value
	 */
	public String getValue()
	{
		return m_value;
	}
}
