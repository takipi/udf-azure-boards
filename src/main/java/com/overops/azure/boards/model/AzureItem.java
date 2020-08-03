package com.overops.azure.boards.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Azure Item
 *
 * Object representation of WorkItem entries for the Azure DevOps API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AzureItem {

    private String op;
    private String path;
    private String from;
    private Object value;

}
