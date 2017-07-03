package es.uned.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.entities.AdapterModel;
import es.uned.services.AdapterModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 *
 */
@Service
public class ConfigParser {

    @Inject
    private Environment environment;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private AdapterModelService adapterModelService;

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

    public ArrayNode getSentimentAdapters() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode results = mapper.createArrayNode();
        Resource resource = resourceLoader.getResource("classpath:/sentimentAdapters.xml");
        try {
            File xmlFile = resource.getFile();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Document xmlDoc = docBuilder.parse(xmlFile);
            xmlDoc.getDocumentElement().normalize();
            NodeList adapters = xmlDoc.getElementsByTagName("adapter");
            for (int i = 0; i < adapters.getLength(); i++) {
                Node adapter = adapters.item(i);
                if (adapter.getNodeType() == Node.ELEMENT_NODE) {
                    Element eAdapter = (Element) adapter;
                    ObjectNode adapterNode = mapper.createObjectNode();

                    String adapterName = eAdapter.getAttribute("name");
                    adapterNode.put("name", adapterName);

                    String adapterClass = eAdapter.getAttribute("class");
                    adapterNode.put("class", adapterClass);

                    String adapterLanguages = eAdapter.getAttribute("lang");
                    adapterNode.put("lang", adapterLanguages);

                    String adapterDescription = eAdapter.getElementsByTagName("description")
                                                        .item(0)
                                                        .getTextContent();
                    adapterNode.put("description", adapterDescription);

                    ArrayNode adaperParameters = mapper.createArrayNode();
                    NodeList parameters = eAdapter.getElementsByTagName("parameter");
                    for (int j = 0; j < parameters.getLength(); j++) {
                        ObjectNode paramNode = mapper.createObjectNode();
                        Node parameterNode = parameters.item(j);
                        Element parameter = (Element) parameterNode;
                        String parameterName = parameter.getAttribute("name");
                        paramNode.put("name", parameterName);
                        String parameterType = parameter.getAttribute("type");
                        paramNode.put("type", parameterType);
                        String parameterID = parameter.getAttribute("id");
                        paramNode.put("id", parameterID);
                        String parameterDefault = parameter.getAttribute("default");
                        paramNode.put("default", parameterDefault);
                        String parameterDescription = parameter.getElementsByTagName("description")
                                                               .item(0)
                                                               .getTextContent();
                        paramNode.put("description", parameterDescription);

                        ArrayNode parameterOptions = mapper.createArrayNode();
                        NodeList options = parameter.getElementsByTagName("option");
                        for (int k = 0; k < options.getLength(); k++) {
                            ObjectNode optionObjNode = mapper.createObjectNode();
                            Node optionNode = options.item(k);
                            Element option = (Element) optionNode;
                            String optionName = option.getAttribute("name");
                            optionObjNode.put("name", optionName);
                            String optionValue = option.getAttribute("value");
                            optionObjNode.put("value", optionValue);
                            parameterOptions.add(optionObjNode);
                        }
                        paramNode.set("options", parameterOptions);

                        adaperParameters.add(paramNode);
                    }
                    adapterNode.set("parameters", adaperParameters);

                    String modelsEnabled = eAdapter.getAttribute("models");
                    adapterNode.put("models_enabled", modelsEnabled.equals("true")? true : false);

                    if (modelsEnabled.equals("true")) {
                        ArrayNode adapterModels = this.getAdapterModels(adapterClass);
                        adapterNode.set("models", adapterModels);
                    }

                    results.add(adapterNode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        
        return results;
    }

    private ArrayNode getAdapterModels(String adapterClass) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode results = mapper.createArrayNode();
        Set<AdapterModel> models = adapterModelService.findByAdapterClass(adapterClass);
        for(AdapterModel model: models) {
            ObjectNode modelNode = mapper.createObjectNode();
            modelNode.put("name", model.getName());
            modelNode.put("location", model.getLocation());
            modelNode.put("lang", model.getLanguage());
            modelNode.put("description", model.getDescription());
            results.add(modelNode);
        }
        return results;
    }

}
