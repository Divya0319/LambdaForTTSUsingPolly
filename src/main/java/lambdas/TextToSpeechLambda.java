package lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import models.Request;
import models.Response;

public class TextToSpeechLambda implements RequestHandler<Request, Response> {
    @Override
    public Response handleRequest(Request request, Context context) {
        return null;
    }
}
