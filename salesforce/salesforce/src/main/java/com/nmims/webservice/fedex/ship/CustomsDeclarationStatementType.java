
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomsDeclarationStatementType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CustomsDeclarationStatementType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NAFTA_LOW_VALUE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CustomsDeclarationStatementType")
@XmlEnum
public enum CustomsDeclarationStatementType {

    NAFTA_LOW_VALUE;

    public String value() {
        return name();
    }

    public static CustomsDeclarationStatementType fromValue(String v) {
        return valueOf(v);
    }

}
