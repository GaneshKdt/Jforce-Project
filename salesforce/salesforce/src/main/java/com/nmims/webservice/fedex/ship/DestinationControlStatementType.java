
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DestinationControlStatementType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DestinationControlStatementType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DEPARTMENT_OF_COMMERCE"/>
 *     &lt;enumeration value="DEPARTMENT_OF_STATE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DestinationControlStatementType")
@XmlEnum
public enum DestinationControlStatementType {

    DEPARTMENT_OF_COMMERCE,
    DEPARTMENT_OF_STATE;

    public String value() {
        return name();
    }

    public static DestinationControlStatementType fromValue(String v) {
        return valueOf(v);
    }

}
