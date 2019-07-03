package com.reproduction.nullpointer;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.ResponseEnvelope;
import com.amazon.ask.util.JacksonSerializer;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;


public class Function {


    private final JacksonSerializer mSerializer = new JacksonSerializer();
    public static Logger logger = null;

    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        this.logger = context.getLogger();
        context.getLogger().setLevel(Level.ALL);
        final String requestString = request.getBody().orElse("");

        // The obtained request string is assumed to be formatted like a request envelope object. Therefore we now try to
        // interpret the content as such one and let it be handled inside our skill object.
        final RequestEnvelope requestEnvelope = getRequestEnvelopeFromString(requestString, context);
        if(requestEnvelope == null) {
            final String errorMessage= "The request body could not be deserialized into a RequestEnvelope object.";
            context.getLogger().info(errorMessage);
            return request.createResponseBuilder(HttpStatus.OK).body(errorMessage).build();
        }

        final MySkill mySkill = new MySkill();
        final ResponseEnvelope responseEnvelope = mySkill.handle(requestEnvelope);
        if (responseEnvelope == null) {
            return request.createResponseBuilder(HttpStatus.NOT_IMPLEMENTED)
                    .body("No intend found that is able to handle the request: " + (requestEnvelope != null ? requestEnvelope.getRequest().getType() : "No envelope provided")).build();
        }
        HttpResponseMessage response = request.createResponseBuilder(HttpStatus.OK).body(getStringFromResponseEnvelope(responseEnvelope, context)).build();
        return response;
    }



    private RequestEnvelope getRequestEnvelopeFromString(String requestString, ExecutionContext context) {
        RequestEnvelope requestEnvelope = null;
        try {
            requestEnvelope = mSerializer.deserialize(requestString, RequestEnvelope.class);
        } catch (AskSdkException e) {
            context.getLogger().severe("ERROR: Could not deserialize request string into a RequestEnvelope. The request string was: " + requestString);
        }
        return requestEnvelope;
    }


    private String getStringFromResponseEnvelope(ResponseEnvelope responseEnvelope, ExecutionContext context) {
        String responseString = "";
        try {
            responseString = mSerializer.serialize(responseEnvelope);
        } catch (AskSdkException e) {
            context.getLogger().severe("ERROR: Could not serialize response envelope. Error is: " + e.getMessage());
        }
        return responseString;
    }
}
