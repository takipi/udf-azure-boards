package com.overops.azure.boards.view;

import com.overops.azure.boards.model.EventView;
import com.overops.azure.boards.model.LocationView;
import com.takipi.api.client.data.event.Location;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class DescriptionGeneratorTest {

    @Test
    public void testDescription() throws Exception {
        DescriptionGenerator generator = new DescriptionGenerator();
        EventView event = new EventView();
        event.setId("myId");
        event.setSummary("mySummary");
        event.setType("myType");
        event.setName("myName");
        event.setMessage("myMessage");
        event.setFirstSeen("myFirstSeen");
        event.setCallStackGroup("myCallStackGroup");
        event.setClassGroup("myClassGroup");
        event.setErrorLocation(newLocationView(1));
        event.setEntryPoint(newLocationView(2));
        event.setErrorOrigin(newLocationView(3));
        event.setStackFrames(Arrays.asList(newLocationView(4), newLocationView(5)));
        event.setIntroducedBy("myDeployment");
        event.setIntroducedByApplication("myApplication");
        event.setIntroducedByServer("myServer");
        event.setLabels(Arrays.asList("label","label2"));
        event.setSimilarEventIds(Arrays.asList("similarEventIds1","similarEventIds2"));
        event.setRethrow(true);
        event.setJiraIssueUrl("myJiraIssueUrl");
        event.setStats(null);
        event.setServiceId("myServiceId");
        event.setServiceName("myServiceName");
        event.setArcLink("myServiceLink");

        String actual = generator.generate(event);
        InputStream is = this.getClass().getResourceAsStream("descriptionResult.html");
        String expected = IOUtils.toString(is, Charset.defaultCharset());
        assertThat(actual).isEqualToIgnoringWhitespace(expected);
    }

    private static LocationView newLocationView(int i) {
        Location location = new Location();
        location.prettified_name = "prettifiedName"+i;
        location.class_name = "className"+i;
        location.method_name = "methodName"+i;
        location.method_desc = "methodDesc"+i;
        location.code_last_modified = "codeLastModified"+i;
        location.original_line_number = i*10;
        location.method_position = i*20;
        location.in_filter = true;
        location.source_file_path = "sourceFilePath"+i;
        return new LocationView(location);
    }
}
