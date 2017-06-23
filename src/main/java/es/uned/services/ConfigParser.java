package es.uned.services;

import es.uned.entities.SourceOptions;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
public class ConfigParser {

    @Inject
    private Environment environment;

    public List<SourceOptions> getSources() {
        List<SourceOptions> sourcesList = new ArrayList<>();
        String[] sources = environment.getProperty("sources").split(",");
        for (String source: sources) {
            String preffix = "source." + source.trim();
            SourceOptions sourceOptions = new SourceOptions();
            sourceOptions.setName(environment.getProperty(preffix + ".name"));
            sourceOptions.setAdapterClass(environment.getProperty(preffix + ".adapterClass"));
            sourceOptions.setLimitEnabled(environment.getProperty(preffix + ".limit").equals("true"));
            sourceOptions.setSinceDateEnabled(environment.getProperty(preffix + ".sinceDate").equals("true"));
            sourceOptions.setUntilDateEnabled(environment.getProperty(preffix + ".untilDate").equals("true"));
            sourceOptions.setLanguageEnabled(environment.getProperty(preffix + ".language").equals("true"));
            sourceOptions.setImdbIDEnabled(environment.getProperty(preffix + ".imdbID").equals("true"));
            sourcesList.add(sourceOptions);
        }
        return sourcesList;
    }

}
