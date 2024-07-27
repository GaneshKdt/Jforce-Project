
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CodReturnReferenceIndicatorType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CodReturnReferenceIndicatorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INVOICE"/>
 *     &lt;enumeration value="PO"/>
 *     &lt;enumeration value="REFERENCE"/>
 *     &lt;enumeration value="TRACKING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CodReturnReferenceIndicatorType")
@XmlEnum
public enum CodReturnReferenceIndicatorType {

    INVOICE,
    PO,
    REFERENCE,
    TRACKING;

    public String value() {
        return name();
    }

    public static CodReturnReferenceIndicatorType fromValue(String v) {
        return valueOf(v);
    }

}
