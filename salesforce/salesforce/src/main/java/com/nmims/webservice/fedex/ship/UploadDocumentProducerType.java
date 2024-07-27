
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UploadDocumentProducerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UploadDocumentProducerType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CUSTOMER"/>
 *     &lt;enumeration value="FEDEX_CLS"/>
 *     &lt;enumeration value="FEDEX_GSMW"/>
 *     &lt;enumeration value="FEDEX_GTM"/>
 *     &lt;enumeration value="OTHER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UploadDocumentProducerType")
@XmlEnum
public enum UploadDocumentProducerType {

    CUSTOMER,
    FEDEX_CLS,
    FEDEX_GSMW,
    FEDEX_GTM,
    OTHER;

    public String value() {
        return name();
    }

    public static UploadDocumentProducerType fromValue(String v) {
        return valueOf(v);
    }

}
