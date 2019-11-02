package posd.proiect;

import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import com.google.auth.oauth2.ServiceAccountCredentials;

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
            gcs = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(apiKey.getInputStream()))
                    .build()
                    .getService();
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
        com.google.cloud.storage.Bucket currentBucket = null;
        try {
            currentBucket = gcs.get(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public Bucket createBucket(String bucketName) {
        com.google.cloud.storage.Bucket newBucket = null;
        try {
            newBucket = gcs.create(BucketInfo.newBuilder(bucketName)
                    .setStorageClass(StorageClass.STANDARD)
                    .setLocation("eu")
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (newBucket != null) {
            Bucket bucket = new Bucket();
            bucket.setName(newBucket.getName());
            bucket.setBlobs(new ArrayList<>());
            return bucket;
        }
        return null;
    }

    public void deleteBucket(String bucketName) {
        try {
            gcs.delete(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] downloadFile(String bucketName, String fileName) {
        com.google.cloud.storage.Blob currentBlob = null;
        try {
            currentBlob = gcs.get(BlobId.of(bucketName, fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentBlob != null) {
            return currentBlob.getContent();
        }
        return null;
    }

    public Blob uploadFile(String bucketName, String fileName, byte[] bytes) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        com.google.cloud.storage.Blob blob = null;
        try {
            blob = gcs.create(blobInfo, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (blob != null) {
            Blob b = new Blob();
            b.setId(blob.getGeneratedId());
            b.setName(blob.getName());
            b.setContentType(blob.getContentType());
            b.setHash(blob.getMd5ToHexString());
            b.setSize(blob.getSize());
            b.setCreationTime(blob.getCreateTime());
            return b;
        }
        return null;
    }
}
