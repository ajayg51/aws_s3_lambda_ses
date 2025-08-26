package  com.example.aws_test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class Lambda1 implements RequestHandler<S3Event, String> {
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private final String destinationBucket = "mcept-destination";

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            String sourceBucket = s3event.getRecords().get(0).getS3().getBucket().getName();
            String sourceKey = s3event.getRecords().get(0).getS3().getObject().getKey();

            ObjectMetadata meta = new ObjectMetadata();
            meta.addUserMetadata("author", "Ajay Gond");
            meta.addUserMetadata("title", "Insurance Policy Document");


            context.getLogger().log("Copying file from " + sourceBucket + "/" + sourceKey + " to " + destinationBucket);

            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                    sourceBucket, sourceKey, destinationBucket, sourceKey);

            copyObjRequest.setNewObjectMetadata(meta);
            copyObjRequest.setMetadataDirective("REPLACE");

            s3Client.copyObject(copyObjRequest);

            context.getLogger().log("Copy successful.");

            return "Copy success";

        } catch (Exception e) {
            context.getLogger().log("Error copying file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
