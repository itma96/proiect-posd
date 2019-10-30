package posd.proiect;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "role")
@XmlEnum
public enum Role {
    @XmlEnumValue("admin")
    ADMIN,
    @XmlEnumValue("readwrite")
    READWRITE,
    @XmlEnumValue("readonly")
    READONLY
}
