
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DangerousGoodsAccessibilityType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DangerousGoodsAccessibilityType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ACCESSIBLE"/>
 *     &lt;enumeration value="INACCESSIBLE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DangerousGoodsAccessibilityType")
@XmlEnum
public enum DangerousGoodsAccessibilityType {

    ACCESSIBLE,
    INACCESSIBLE;

    public String value() {
        return name();
    }

    public static DangerousGoodsAccessibilityType fromValue(String v) {
        return valueOf(v);
    }

}
