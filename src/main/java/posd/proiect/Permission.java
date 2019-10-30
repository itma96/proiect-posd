package posd.proiect;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "permission")
@XmlEnum
public enum Permission {
    @XmlEnumValue(value = "view")
    VIEW,
    @XmlEnumValue(value = "create")
    CREATE,
    @XmlEnumValue(value = "upload")
    UPLOAD,
    @XmlEnumValue(value = "delete")
    DELETE
}
