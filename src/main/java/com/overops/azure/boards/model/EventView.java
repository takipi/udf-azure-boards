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

    private static final String DISPLAY_DATE_FORMAT = "EEE, dd/MM/yy HH:mm:ss a";

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

        this.arcLink = EventUtil.getEventRecentLinkDefault(args.apiClient(), args.serviceId, args.eventId,
                new DateTime(eventResult.first_seen), DateTime.now(),
                Arrays.asList(eventResult.introduced_by_application),
                Arrays.asList(eventResult.introduced_by_server),
                Arrays.asList(eventResult.introduced_by),
                EventUtil.DEFAULT_PERIOD);
    }
}
