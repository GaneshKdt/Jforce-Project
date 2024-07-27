
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChargeBasisLevelType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChargeBasisLevelType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CURRENT_PACKAGE"/>
 *     &lt;enumeration value="SUM_OF_PACKAGES"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ChargeBasisLevelType")
@XmlEnum
public enum ChargeBasisLevelType {

    CURRENT_PACKAGE,
    SUM_OF_PACKAGES;

    public String value() {
        return name();
    }

    public static ChargeBasisLevelType fromValue(String v) {
        return valueOf(v);
    }

}
