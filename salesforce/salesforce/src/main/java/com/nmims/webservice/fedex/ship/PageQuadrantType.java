
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PageQuadrantType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PageQuadrantType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BOTTOM_LEFT"/>
 *     &lt;enumeration value="BOTTOM_RIGHT"/>
 *     &lt;enumeration value="TOP_LEFT"/>
 *     &lt;enumeration value="TOP_RIGHT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PageQuadrantType")
@XmlEnum
public enum PageQuadrantType {

    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    TOP_LEFT,
    TOP_RIGHT;

    public String value() {
        return name();
    }

    public static PageQuadrantType fromValue(String v) {
        return valueOf(v);
    }

}
