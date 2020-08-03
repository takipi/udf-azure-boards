package com.overops.azure.boards.model;

import com.takipi.udf.input.Input;
import org.apache.commons.lang3.StringUtils;

/**
 * Azure Boards Input Definition
 *
 * Required by UDF Implementation
 */
public class AzureBoardsInput extends Input {

    // user defined parameters
    public String azureUrl;
    public String authUser;
    public String authToken;
    public String organization;
    public String project;
    public String workItemType;
    public int priority;
    public String dateFormat;
    public String timeZone;
    public boolean debug;

    // parse and populate variables
    private AzureBoardsInput(String raw) {
        super(raw);

        // Default if Empty
        if(StringUtils.isEmpty(azureUrl)){
            azureUrl = "https://dev.azure.com";
        }
        if(StringUtils.isEmpty(dateFormat)){
            dateFormat = "EEE, M/dd/yyyy HH:mm:ss Z";
        }
        if(StringUtils.isEmpty(timeZone)){
            timeZone = "UTC";
        }
    }

    // override toString() to set anomaly function label
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Some Value");

        return builder.toString();
    }

    public static AzureBoardsInput of(String raw) {
        return new AzureBoardsInput(raw);
    }

}
