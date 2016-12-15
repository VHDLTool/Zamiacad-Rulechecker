package org.zamia.plugin.tool.vhdl.rules;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IntParam implements IHandbookParam
{
	public static enum Relation {LT, LET, E, GET, GT};
	
	public static final String INT_PARAM_TAG = "hb:IntParam";


	private static final String PARAM_ID_TAG = "hb:ParamID";
	private static final String RELATION_TAG = "hb:Relation";
	private static final String VALUE_TAG = "hb:Value";
	
	private String m_paramId;
	private Relation m_relation;
	private Integer m_value;
	
	/**
	 * Instantiates an IntParam from a given DOM element 
	 * @param element The DOM element representing the IntParam
	 */
	public IntParam(Element element)
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
							case RELATION_TAG:
								m_relation = Relation.valueOf(elem.getTextContent().toUpperCase());
								break;
							case VALUE_TAG:
								m_value = Integer.decode(elem.getTextContent());
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
			m_value = null;
			m_relation = null;
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
		return m_paramId != null && m_relation != null && m_value != null;
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
			if (m_value != null && m_relation != null)
			{
				switch (m_relation)
				{
				case LT:
					isValid = integer < m_value;
					break;
				case LET:
					isValid = integer <= m_value;
					break;
				case E:
					isValid = integer == m_value;
					break;
				case GET:
					isValid = integer >= m_value;
					break;
				case GT:
					isValid = integer > m_value;
					break;
				}
			}
		}
		
		return isValid;
	}

	/**
	 * Gets the relation operation
	 * @return The relation operation
	 */
	public Relation getRelation()
	{
		return m_relation;
	}
	
	/**
	 * Gets the value
	 * @return The value
	 */
	public Integer getValue()
	{
		return m_value;
	}
}
