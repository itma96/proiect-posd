package posd.proiect;

import com.google.cloud.storage.BlobId;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleStorageService {

    private Storage gcs;

    public GoogleStorageService() {
        Resource apiKey = null;
        try {
            apiKey = new ClassPathResource("my_key.json");
            gcs = StorageOptions.newBuilder().setCredentials(ServiceAccountCredentials.fromStream(apiKey.getInputStream())).build().getService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String test() {
        return gcs.toString();
    }

    public List<Bucket> getAllBuckets() {
        List<Bucket> bucketList = new ArrayList<>();
        for (com.google.cloud.storage.Bucket currentBucket : gcs.list().iterateAll()) {
            Bucket bucket = new Bucket();
            bucket.setName(currentBucket.getName());
            List<Blob> blobList = new ArrayList<>();
            for (com.google.cloud.storage.Blob currentBlob : currentBucket.list().iterateAll()) {
                Blob blob = new Blob();
                blob.setId(currentBlob.getGeneratedId());
                blob.setName(currentBlob.getName());
                blob.setContentType(currentBlob.getContentType());
                blob.setHash(currentBlob.getMd5ToHexString());
                blob.setSize(currentBlob.getSize());
                blob.setCreationTime(currentBlob.getCreateTime());
                blobList.add(blob);
            }
            bucket.setBlobs(blobList);
            bucketList.add(bucket);
        }
        return bucketList;
    }

    public Bucket getBucket(String bucketName) {
        com.google.cloud.storage.Bucket currentBucket = gcs.get(bucketName);
        if (currentBucket != null) {
            Bucket bucket = new Bucket();
            bucket.setName(currentBucket.getName());
            List<Blob> blobList = new ArrayList<>();
            for (com.google.cloud.storage.Blob currentBlob : currentBucket.list().iterateAll()) {
                Blob blob = new Blob();
                blob.setId(currentBlob.getGeneratedId());
                blob.setName(currentBlob.getName());
                blob.setContentType(currentBlob.getContentType());
                blob.setHash(currentBlob.getMd5ToHexString());
                blob.setSize(currentBlob.getSize());
                blob.setCreationTime(currentBlob.getCreateTime());
                blobList.add(blob);
            }
            bucket.setBlobs(blobList);
            return bucket;
        }
        return null;
    }

    public byte[] getFile(String bucketName, String fileName) {
        com.google.cloud.storage.Blob currentBlob = gcs.get(BlobId.of(bucketName, fileName));
        if (currentBlob != null) {
            return currentBlob.getContent();
        }
        return null;
    }
}
