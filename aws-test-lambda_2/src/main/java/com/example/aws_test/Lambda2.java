package  com.example.aws_test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

public class Lambda2 implements RequestHandler<S3Event, String> {

    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private final AmazonSimpleEmailService sesClient = AmazonSimpleEmailServiceClientBuilder.defaultClient();
    private final String toEmail = "ajayg51.nitp@gmail.com";
    private final String fromEmail = "ow.ajayg51@gmail.com"; 

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            String bucket = s3event.getRecords().get(0).getS3().getBucket().getName();
            String key = s3event.getRecords().get(0).getS3().getObject().getKey();

            context.getLogger().log("Reading metadata for " + bucket + "/" + key);

            ObjectMetadata metadata = s3Client.getObjectMetadata(bucket, key);

            StringBuilder emailBody = new StringBuilder();
            emailBody.append("Metadata for file: ").append(key).append("\n\n");

            metadata.getRawMetadata().forEach((k, v) -> {
                emailBody.append(k).append(": ").append(v).append("\n");
            });

            emailBody.append("\nUser Metadata:\n");
            metadata.getUserMetadata().forEach((k, v) -> {
                emailBody.append(k).append(": ").append(v).append("\n");
            });

            SendEmailRequest emailRequest = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(toEmail))
                    .withMessage(new Message()
                            .withSubject(new Content("S3 File Metadata: " + key))
                            .withBody(new Body().withText(new Content(emailBody.toString()))))
                    .withSource(fromEmail);

            sesClient.sendEmail(emailRequest);

            context.getLogger().log("Email sent to " + toEmail);

            return "Email sent";
        } catch (Exception e) {
            context.getLogger().log("Error in Lambda: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
