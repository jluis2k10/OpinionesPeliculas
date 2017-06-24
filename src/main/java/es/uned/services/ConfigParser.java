package es.uned.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 *
 */
@Service
public class ConfigParser {

    @Inject
    private Environment environment;

    public ArrayNode getSources() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode results = mapper.createArrayNode();
        String[] sources = environment.getProperty("sources").split(",");

        for (String source: sources) {
            String prefix = "source." + source.trim();
            ObjectNode option = mapper.createObjectNode();
            option.put("name", environment.getProperty(prefix + ".name"));
            option.put("adapterClass", environment.getProperty(prefix + ".adapterClass"));
            option.put("limitEnabled", environment.getProperty(prefix + ".limit").equals("true"));
            option.put("sinceDateEnabled", environment.getProperty(prefix + ".sinceDate").equals("true"));
            option.put("untilDateEnabled", environment.getProperty(prefix + ".untilDate").equals("true"));
            option.put("languageEnabled", environment.getProperty(prefix + ".language").equals("true"));
            option.put("imdbIDEnabled", environment.getProperty(prefix + ".imdbID").equals("true"));
            results.add(option);
        }
        return results;
    }

}
