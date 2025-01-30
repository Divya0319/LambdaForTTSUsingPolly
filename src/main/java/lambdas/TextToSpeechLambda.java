package lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import models.Request;
import models.Response;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechResponse;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;

public class TextToSpeechLambda implements RequestHandler<Request, Response> {
    @Override
    public Response handleRequest(Request request, Context context) {
        String inputText = request.getText();
        String voiceId = request.getVoiceId() != null ? request.getVoiceId() : "Joanna";
        return null;
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

            InputStream audioStream = responseInputStream;

        }
    }

    public S3Client createS3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
