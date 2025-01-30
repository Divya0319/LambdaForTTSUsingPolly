package lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import models.Request;
import models.Response;
import models.Status;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import utils.S3UrlGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

public class TextToSpeechLambda implements RequestHandler<Request, Response> {
    private static final String S3_BUCKET_NAME = "bucket-for-expenses-csv";
    @Override
    public Response handleRequest(Request request, Context context) {
        String inputText = request.getText();
        String voiceId = request.getVoiceId() != null ? request.getVoiceId() : "Joanna";

        String audioFileKey = generateSpeechAndUploadToS3(inputText, voiceId);

        S3UrlGenerator s3UrlGenerator = new S3UrlGenerator();
        URL fileUrl = s3UrlGenerator.generatePreSignedUrl(S3_BUCKET_NAME, audioFileKey, Region.AP_NORTHEAST_1);

        Response response = new Response();
        response.setStatus(Status.SUCCESS);
        response.setFileUrl(fileUrl.toString());
        return response;
    }

    private String generateSpeechAndUploadToS3(String text, String voiceId) {
        try(PollyClient pollyClient = PollyClient.create();
            S3Client s3Client = createS3Client()) {

            SynthesizeSpeechRequest synthesizeSpeechRequest = SynthesizeSpeechRequest.builder()
                    .text(text)
                    .voiceId(voiceId)
                    .outputFormat(OutputFormat.MP3)
                    .build();

            ResponseInputStream<SynthesizeSpeechResponse> responseInputStream = pollyClient.synthesizeSpeech(synthesizeSpeechRequest);

            byte[] audioBytes = responseInputStream.readAllBytes();

            String audioFileKey = "polly-audio/" + UUID.randomUUID() + ".mp3";

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(S3_BUCKET_NAME)
                    .key(audioFileKey)
                    .contentType("audio/mpeg")
                    .build();

            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(audioBytes));

            System.out.println("Audio file uploaded to S3 with ETag: " + putObjectResponse.eTag());

            return audioFileKey;

        } catch (IOException e) {
            throw new RuntimeException("IOException: " + e.getMessage());
        }
    }

    public S3Client createS3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
