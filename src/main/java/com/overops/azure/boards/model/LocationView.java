package com.overops.azure.boards.model;

import com.takipi.api.client.data.event.Location;
import lombok.Data;

import java.io.Serializable;


/**
 * Location View Object
 *
 * Needed since our Location object does not follow proper Java POJO standards
 */
@Data
public class LocationView implements Serializable {

    private String prettifiedName;
    private String className;
    private String methodName;
    private String methodDesc;
    private String codeLastModified;
    private int originalLineNumber = -1;
    private int methodPosition = -1;
    private boolean inFilter;
    private String sourceFilePath;

    public LocationView(Location location){
        this.prettifiedName = location.prettified_name;
        this.className = location.class_name;
        this.methodName = location.method_name;
        this.methodDesc = location.method_desc;
        this.codeLastModified = location.code_last_modified;
        this.originalLineNumber = location.original_line_number;
        this.methodPosition = location.method_position;
        this.inFilter = location.in_filter;
        this.sourceFilePath = location.source_file_path;
    }

}
