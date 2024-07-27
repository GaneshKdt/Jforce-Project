
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LabelOrderType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LabelOrderType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SHIPPING_LABEL_FIRST"/>
 *     &lt;enumeration value="SHIPPING_LABEL_LAST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LabelOrderType")
@XmlEnum
public enum LabelOrderType {

    SHIPPING_LABEL_FIRST,
    SHIPPING_LABEL_LAST;

    public String value() {
        return name();
    }

    public static LabelOrderType fromValue(String v) {
        return valueOf(v);
    }

}
