package com.reproduction.nullpointer;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.services.ServiceException;
import com.amazon.ask.request.RequestHelper;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;


public class LaunchHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {

        // TRY TO ACCESS THE TIMEZONE
        String timezone = "EMPTY";
        try {
            RequestHelper helper = RequestHelper.forHandlerInput(handlerInput);
            timezone = handlerInput.getServiceClientFactory().getUpsService().getSystemTimeZone(helper.getDeviceId());
        } catch(ServiceException e) {
            // No permission....
            Function.logger.warning("WARNING: Service Exception during getSystemTimeZone() -> " + e.getMessage());
        }

        return handlerInput.getResponseBuilder()
                .withSpeech("Skill has been started. The timezone is: " + timezone)
                .build();
    }
}
