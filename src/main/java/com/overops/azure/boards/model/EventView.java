package com.overops.azure.boards.model;

import com.takipi.api.client.data.event.MainEventStats;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.event.EventUtil;
import com.takipi.udf.ContextArgs;
import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event View Object
 *
 * Needed since our EventResult object does not follow proper Java POJO standards
 */
@Data
public class EventView implements Serializable {

    private static final String DISPLAY_DATE_FORMAT = "EEE, dd/MM/yy HH:mm:ss";

    // Fields from EventResult
    private String id;
    private String summary;
    private String type;
    private String name;
    private String message;
    private String firstSeen;
    private String classGroup;
    private String callStackGroup;
    private LocationView errorLocation;
    private LocationView entryPoint;
    private LocationView errorOrigin;
    private List<LocationView> stackFrames;
    private String introducedBy;
    private String introducedByApplication;
    private String introducedByServer;
    private List<String> labels;
    private List<String> similarEventIds;
    private boolean isRethrow;
    private String jiraIssueUrl;
    private MainEventStats stats;

    // Additional Stuff
    private String arcLink;

    public EventView(ContextArgs args, EventResult eventResult){

        this.id = eventResult.id;
        this.summary = eventResult.summary;
        this.type = eventResult.type;
        this.name = eventResult.name;
        this.message = eventResult.message;
        this.firstSeen = DateTime.parse(eventResult.first_seen).toString(DISPLAY_DATE_FORMAT);
        this.classGroup = eventResult.class_group;
        this.callStackGroup = eventResult.call_stack_group;
        this.errorLocation = new LocationView(eventResult.error_location);
        this.entryPoint = new LocationView(eventResult.entry_point);
        this.errorOrigin = new LocationView(eventResult.error_origin);
        this.stackFrames = eventResult.stack_frames.stream().map(location -> new LocationView(location)).collect(Collectors.toList());
        this.introducedBy = eventResult.introduced_by;
        this.introducedByApplication = eventResult.introduced_by_application;
        this.introducedByServer = eventResult.introduced_by_server;
        this.labels = eventResult.labels;
        this.similarEventIds = eventResult.similar_event_ids;
        this.isRethrow = eventResult.is_rethrow;
        this.jiraIssueUrl = eventResult.jira_issue_url;
        this.stats = eventResult.stats;

        // This is needed because the snapshot needed to create the ARC screen link may not be available yet.
        this.arcLink = fetchArcLink(args, eventResult);
        for(int i=1; i <= 12; i++){
            if(this.arcLink == null){
                try {
                    System.out.println("Fetch Arc Link (Retry #"+i+")");
                    // Pause for the cause
                    Thread.sleep(10000);
                    this.arcLink = fetchArcLink(args, eventResult);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                break;
            }
        }
    }

    private String fetchArcLink(ContextArgs args, EventResult eventResult){
        return EventUtil.getEventRecentLinkDefault(args.apiClient(), args.serviceId, args.eventId,
                new DateTime(eventResult.first_seen), DateTime.now(),
                Arrays.asList(eventResult.introduced_by_application),
                Arrays.asList(eventResult.introduced_by_server),
                Arrays.asList(eventResult.introduced_by),
                EventUtil.DEFAULT_PERIOD);
    }
}
