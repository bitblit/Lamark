package com.erigir.lamark;

/**
 * An enumeration of the outcomes of Lamark setting a custom property on a component.
 * 
 * Any component implementing IConfigurable will have to return one of these 
 * values describing what happened when Lamark tried to set a custom property.
 * <br />
 * @author cweiss
 * @since 11/07
 */
public enum EConfigResult
{
    /** Property set successfully **/
    OK 
    /** This component doesnt have this property **/
    ,NO_SUCH_PROPERTY 
    /** That value isnt value for this property **/
    ,INVALID_VALUE 
    /** Only Lamark itself should ever use this value **/
    ,MISSING_OR_NOT_CONFIGURABLE; 
}
