package posd.proiect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "blob")
public class Blob {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "contentType")
    private String contentType;
    @XmlAttribute(name = "hash")
    private String hash;
    @XmlAttribute(name = "size")
    private Long size;
    @XmlAttribute(name = "creationTime")
    private Long creationTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }
}
