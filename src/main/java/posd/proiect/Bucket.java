package posd.proiect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "bucket")
public class Bucket {
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "blobs")
    private List<Blob> blobs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Blob> getBlobs() {
        return blobs;
    }

    public void setBlobs(List<Blob> blobs) {
        this.blobs = new ArrayList<>(blobs);
    }
}
