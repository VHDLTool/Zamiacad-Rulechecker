package org.zamia.plugin.tool.vhdl.rules;

/**
 * This interface defines a rule parameter as read from the handbook or from the rulechecker inner param file
 * @author mblondeau
 */
public interface IHandbookParam 
{
	/**
	 * Gets a value indicating that the given object is valid regarding this IHandbookParam
	 * @param object The object to validate
	 * @return True is the object is valid
	 */
	public boolean isValid(Object object);

	/**
	 * Gets a value indicating whether the IHandbookParam is valid and can be used to validate objects
	 * @return True if the IHanbookParam is valid
	 */
	public boolean isParamValid();
	
	/**
	 * Gets the id of the parameter
	 * @return The id of the parameter
	 */
	public String getParamId();
}
