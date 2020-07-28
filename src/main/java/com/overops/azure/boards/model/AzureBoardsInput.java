package com.overops.azure.boards.model;

import com.takipi.udf.input.Input;

/**
 * Azure Boards Input Definition
 *
 * Required by UDF Implementation
 */
public class AzureBoardsInput extends Input {

    // user defined parameters
    public String authUser;
    public String authToken;
    public String organization;
    public String project;

    // parse and populate variables
    private AzureBoardsInput(String raw) {
        super(raw);
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
