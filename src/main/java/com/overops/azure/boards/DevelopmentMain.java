package com.overops.azure.boards;

import com.google.gson.Gson;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.client.request.event.EventsRequest;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.result.event.EventsResult;
import com.takipi.api.client.util.view.ViewUtil;
import com.takipi.api.core.url.UrlClient;
import com.takipi.common.util.CollectionUtil;
import com.takipi.udf.ContextArgs;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

/**
 * Used to help developers develop the UDF
 *
 * Replace contextArgs & rawInput with valid values and simply run the `main` method.
 */
public class DevelopmentMain {

    public static void main(String[] args) throws Exception {

        ContextArgs contextArgs = new ContextArgs();
        contextArgs.apiHost = "http://localhost:8080";
        contextArgs.apiKey = "_____________________";
        contextArgs.serviceId = "S1";

        StringBuilder rawInput = new StringBuilder();
        rawInput.append("authUser=chris.caspanello@overops.com").append("\n");
        rawInput.append("authToken=_____________________").append("\n");
        rawInput.append("organization=ccaspanello").append("\n");
        rawInput.append("project=sandbox2");

        SummarizedView view = ViewUtil.getServiceViewByName(contextArgs.apiClient(), contextArgs.serviceId,
                "All Events");
        contextArgs.viewId = view.id;

        // get an event that has occurred in the last 5 minutes
        DateTime to = DateTime.now();
        DateTime from = to.minusDays(5);

        // date parameter must be properly formatted
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZoneUTC();

        // get all events within the date range
        EventsRequest eventsRequest = EventsRequest.newBuilder().setServiceId(contextArgs.serviceId)
                .setViewId(contextArgs.viewId).setFrom(from.toString(fmt)).setTo(to.toString(fmt)).build();

        // create a new API Client
        ApiClient apiClient = contextArgs.apiClient();

        // execute API GET request
        UrlClient.Response<EventsResult> eventsResponse = apiClient.get(eventsRequest);

        // check for a bad API response
        if (eventsResponse.isBadResponse())
            throw new IllegalStateException("Failed getting events.");

        // retrieve event data from the result
        EventsResult eventsResult = eventsResponse.data;

        // exit if there are no events - increase date range if this occurs
        if (CollectionUtil.safeIsEmpty(eventsResult.events)) {
            System.out.println("NO EVENTS");
            return;
        }

        // retrieve a list of events from the result
        List<EventResult> events = eventsResult.events;

        // get the first event
        contextArgs.eventId = events.get(0).id;


        // convert context args to a JSON string
        String rawContextArgs = new Gson().toJson(contextArgs);

        // execute the UDF
        AzureBoardsFunction.execute(rawContextArgs, rawInput.toString());
    }


}
