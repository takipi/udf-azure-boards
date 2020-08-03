package com.overops.azure.boards;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Properties;

public class AzureBoardsFunctionTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testInvalidRawInput(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Input is empty");
        AzureBoardsFunction.getLabelInput("");
    }

    @Test
    public void testInvalidAuthUser() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Authentication user can't be empty");

        Properties properties = new Properties();
        properties.put("authUser", "");

        AzureBoardsFunction.getLabelInput(propertiesToString(properties));
    }

    @Test
    public void testInvalidAuthToken(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Authentication token can't be empty");

        Properties properties = new Properties();
        properties.put("authUser", "authuser");
        properties.put("authToken", "");

        AzureBoardsFunction.getLabelInput(propertiesToString(properties));
    }

    @Test
    public void testInvalidOrganization(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Organization can't be empty");

        Properties properties = new Properties();
        properties.put("authUser", "authuser");
        properties.put("authToken", "authToken");
        properties.put("organization", "");

        AzureBoardsFunction.getLabelInput(propertiesToString(properties));
    }

    @Test
    public void testInvalidProject(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Project can't be empty");

        Properties properties = new Properties();
        properties.put("authUser", "authuser");
        properties.put("authToken", "authToken");
        properties.put("organization", "organization");
        properties.put("project", "");

        AzureBoardsFunction.getLabelInput(propertiesToString(properties));
    }

    @Test
    public void testInvalidDateFormat(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("Date format invalid; please refer back to documentation.");

        Properties properties = new Properties();
        properties.put("authUser", "authuser");
        properties.put("authToken", "authToken");
        properties.put("organization", "organization");
        properties.put("project", "project");
        properties.put("dateFormat", "someBadValue");

        AzureBoardsFunction.getLabelInput(propertiesToString(properties));
    }

    @Test
    public void testInvalidTimeZone(){
        Properties properties = new Properties();
        properties.put("authUser", "authuser");
        properties.put("authToken", "authToken");
        properties.put("organization", "organization");
        properties.put("project", "project");
        properties.put("timeZone", "someBadValue");

        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The datetime zone id 'someBadValue' is not recognised");

        AzureBoardsFunction.getLabelInput(propertiesToString(properties));
    }


    private String propertiesToString(Properties properties){
        try{
            StringWriter writer = new StringWriter();
            properties.store(writer,"");
            return writer.toString();
        }catch(Exception e){
            throw new RuntimeException("Unable to convert properties to string", e);
        }

    }

}
