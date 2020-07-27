package com.overops.azure.boards.view;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.overops.azure.boards.AzureBoardsException;
import com.overops.azure.boards.model.EventView;

import java.io.IOException;
import java.net.URL;

/**
 * Description Generator
 *
 * Generates HTML for the Description field using the Handlebars template engine
 */
public class DescriptionGenerator {

    /**
     * Generates the description HTML from the EventView model
     * @param event
     * @return an HTML string
     */
    public String generate(EventView event) {
        try {
            TemplateLoader loader = new ClassPathTemplateLoader(){
                @Override
                protected URL getResource(String location) {
                    return DescriptionGenerator.class.getResource(location);
                }
            };
            loader.setPrefix("/template");
            loader.setSuffix(".hbs");
            Handlebars handlebars = new Handlebars(loader);
            handlebars.registerHelper("or", ConditionalHelpers.or);
            handlebars.registerHelper("eq", ConditionalHelpers.eq);
            handlebars.prettyPrint(true);
            Template template = handlebars.compile("description");
            return template.apply(event);
        } catch (IOException e) {
            String message = "Unexpected exception generating description html.";
            throw new AzureBoardsException(message, e);
        }
    }

}
