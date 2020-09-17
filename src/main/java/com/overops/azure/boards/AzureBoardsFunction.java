package com.overops.azure.boards;

import com.google.gson.GsonBuilder;
import com.overops.azure.boards.model.AzureBoardsInput;
import com.overops.azure.boards.model.AzureItem;
import com.overops.azure.boards.model.AzureWorkItemResponse;
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
import org.joda.time.DateTimeZone;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

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

            if (eventResult == null) {
                throw new AzureBoardsException("Error: Event query came back empty.");
            }

            // Create Azure Items
            EventView event = new EventView(args, input, eventResult);

            String name = "[OverOps] New " + event.getName() + " in " + event.getIntroducedByApplication() + " release " + event.getIntroducedBy();
            String description = new DescriptionGenerator().generate(event);
            if (input.debug) {
                System.out.println(description);
            }

            List<AzureItem> azureItems = new ArrayList<>();

            azureItems.add(new AzureItem("add", "/fields/System.Title", "OverOps", name));
            azureItems.add(new AzureItem("add", "/fields/System.Description", null, description));
            azureItems.add(new AzureItem("add", "/fields/System.Tags", null, "OverOps"));
            // Only provide workItemType if defined; if not present allow Azure to default values
            if (!StringUtils.isEmpty(input.workItemType)) {
                azureItems.add(new AzureItem("add", "/fields/System.WorkItemType", null, input.workItemType));
            }
            // Only provide priority if defined; if not present allow Azure to default values
            if (input.priority > 0) {
                azureItems.add(new AzureItem("add", "/fields/Microsoft.VSTS.Common.Priority", null, input.priority));
            }
            // Populate custom field / field overrides
            for(String key : input.otherFields.keySet()){
                azureItems.add(new AzureItem("add", "/fields/"+key, null, input.otherFields.get(key)));
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            String bodyContent = gson.toJson(azureItems);

            // Authenticate
            String credential = Credentials.basic(input.authUser, input.authToken);

            // Create HTTP Client
            OkHttpClient client = new OkHttpClient.Builder().build();
            MediaType mediaType = MediaType.parse("application/json-patch+json");
            RequestBody body = RequestBody.create(mediaType, bodyContent);

            // Send Request
            String rootUrl = input.azureUrl + "/" + input.organization + "/" + input.project;
            URL url = new URL(rootUrl + "/_apis/wit/workitems/$task?api-version=5.0");
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json-patch+json")
                    .header("Authorization", credential)
                    .build();
            Response result = client.newCall(request).execute();
            if (result.code() != 200) {
                System.out.println("Error creating Azure Task:");
                System.out.println(result.body().string());
            } else {
                AzureWorkItemResponse response = gson.fromJson(result.body().string(), AzureWorkItemResponse.class);
                System.out.println("Work Item Created: " + rootUrl + "/_workitems/edit/" + response.getId() + "/");
            }

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

    public static AzureBoardsInput getLabelInput(String rawInput) {

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

        if (!StringUtils.isEmpty(input.dateFormat)) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(input.dateFormat);
                simpleDateFormat.format(new Date());
            } catch (Exception e) {
                throw new IllegalArgumentException("Date format invalid; please refer back to documentation.");
            }
        }

        if (!StringUtils.isEmpty(input.timeZone)) {
            DateTimeZone.forID(input.timeZone);
        }
        
        return input;
    }

}
