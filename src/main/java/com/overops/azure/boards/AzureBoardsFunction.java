package com.overops.azure.boards;

import com.google.gson.GsonBuilder;
import com.overops.azure.boards.model.AzureBoardsInput;
import com.overops.azure.boards.model.AzureItem;
import com.overops.azure.boards.model.EventView;
import com.overops.azure.boards.view.DescriptionGenerator;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.request.event.EventRequest;
import com.takipi.api.client.result.event.EventResult;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.takipi.udf.ContextArgs;

import java.net.URL;
import java.util.Arrays;

/**
 * Azure Boards Function
 * <p>
 * Main UDF entry point called by the OverOps server
 */
public class AzureBoardsFunction {

    /**
     * Utilized by OverOps UI to validate configuration
     *
     * @param rawInput
     * @return
     */
    public static String validateInput(String rawInput) {
        return getLabelInput(rawInput).toString();
    }

    /**
     * Main entry point that gets executed by the UDF framework
     *
     * @param rawContextArgs
     * @param rawInput
     */
    public static void execute(String rawContextArgs, String rawInput) {
        try {
            ContextArgs args = (new Gson()).fromJson(rawContextArgs, ContextArgs.class);
            AzureBoardsInput input = getLabelInput(rawInput);

            // Fetch Event
            ApiClient apiClient = args.apiClient();
            EventRequest eventsRequest = EventRequest.newBuilder()
                    .setServiceId(args.serviceId)
                    .setEventId(args.eventId)
                    .setIncludeStacktrace(true)
                    .build();
            EventResult eventResult = apiClient.get(eventsRequest).data;

            if(eventResult == null){
                throw new AzureBoardsException("Error: Event query came back empty.");
            }

            // Create Azure Items
            EventView event = new EventView(args, eventResult);

            String name = "[OverOps] New " + event.getName() + " in " + event.getIntroducedByApplication() + " Application version " + event.getIntroducedBy();

            String description = new DescriptionGenerator().generate(event);
            System.out.println(description);

            AzureItem title = new AzureItem("add", "/fields/System.Title", "OverOps", name);
            AzureItem desc = new AzureItem("add", "/fields/System.Description", null, description);
            AzureItem tag = new AzureItem("add", "/fields/System.Tags", null, "OverOps");

            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            String bodyContent = gson.toJson(Arrays.asList(title, desc, tag));

            // Authenticate
            String credential = Credentials.basic(input.authUser, input.authToken);

            // Create HTTP Client
            OkHttpClient client = new OkHttpClient.Builder().build();
            MediaType mediaType = MediaType.parse("application/json-patch+json");
            RequestBody body = RequestBody.create(mediaType, bodyContent);

            // Send Request
            URL url = new URL("https://dev.azure.com/" + input.organization + "/" + input.project + "/_apis/wit/workitems/$task?api-version=5.0");
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json-patch+json")
                    .header("Authorization", credential)
                    .build();
            client.newCall(request).execute();

        } catch (Exception e) {
            System.out.println("Exception: ");
            System.out.println("**************************************************");
            System.out.println("Message: " + e.getMessage());
            System.out.println("StackTrace:");
            e.printStackTrace(System.out);
            System.out.println("**************************************************");
            throw new RuntimeException("Unexpected error running Azure Boards Function", e);
        }
    }

    private static AzureBoardsInput getLabelInput(String rawInput) {

        if (Strings.isNullOrEmpty(rawInput))
            throw new IllegalArgumentException("Input is empty");

        AzureBoardsInput input;

        try {
            input = AzureBoardsInput.of(rawInput);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        if (StringUtils.isEmpty(input.authUser))
            throw new IllegalArgumentException("Authentication user can't be empty");

        if (StringUtils.isEmpty(input.authToken))
            throw new IllegalArgumentException("Authentication token can't be empty");

        if (StringUtils.isEmpty(input.organization))
            throw new IllegalArgumentException("Organization can't be empty");

        if (StringUtils.isEmpty(input.project))
            throw new IllegalArgumentException("Project can't be empty");

        return input;
    }

}
