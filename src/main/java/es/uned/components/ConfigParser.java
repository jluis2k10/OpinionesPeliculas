package es.uned.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.entities.AdapterModel;
import es.uned.services.AdapterModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Esta clase se utiliza para leer los archivos de configuración (XMLs) y generar un objeto
 * compatible con JSON (utilizando las librerías FasterXML/Jackson) que puede ser enviado
 * a la vista.
 */
@Component
public class ConfigParser {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private AdapterModelService adapterModelService;

    private static final String SOURCE_XML = "/sourceAdapters.xml";
    private static final String SENTIMENT_XML = "/sentimentAdapters.xml";
    private static final String SUBJECTIVITY_XML = "/subjectivityAdapters.xml";

    /**
     * Adaptadores disponibles para las fuentes de comentarios.
     * Genera objeto JSON desde el archivo de configuración /sourceAdapters.xml
     * @return Objeto ArrayNode que representa al archivo de configuración XML
     */
    public ArrayNode getSources() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode results = mapper.createArrayNode();
        Resource resource = resourceLoader.getResource("classpath:" + SOURCE_XML);

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
                    Element element = (Element) adapter;
                    ObjectNode adapterNode = mapper.createObjectNode();
                    adapterNode.put("name", element.getAttribute("name"));
                    adapterNode.put("adapterClass", element.getElementsByTagName("class").item(0).getTextContent());
                    adapterNode.put("limitEnabled", element.getAttribute("limit").equals("true"));
                    adapterNode.put("sinceDateEnabled", element.getAttribute("sinceDate").equals("true"));
                    adapterNode.put("untilDateEnabled", element.getAttribute("untilDate").equals("true"));
                    adapterNode.put("languageEnabled", element.getAttribute("language").equals("true"));
                    adapterNode.put("imdbIDEnabled", element.getAttribute("imdbID").equals("true"));
                    results.add(adapterNode);
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Adaptadores disponibles para el análisis de sentimiento.
     * Genera objeto JSON desde el archivo de configuración
     * @return Objeto ArrayNode que representa al archivo de configuración XML
     */
    public ArrayNode getSentimentAdapters(String adapterType) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode results = mapper.createArrayNode();
        Resource resource = null;
        switch (adapterType) {
            case "sentiment":
                resource = resourceLoader.getResource("classpath:" + SENTIMENT_XML);
                break;
            case "subjectivity":
                resource = resourceLoader.getResource("classpath:" + SUBJECTIVITY_XML);
                break;
        }
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
                    Element element = (Element) adapter;
                    ObjectNode adapterNode = mapper.createObjectNode();

                    adapterNode.put("ID", element.getAttribute("id"));
                    adapterNode.put("name", element.getAttribute("name"));
                    adapterNode.put("class", element.getAttribute("class"));
                    adapterNode.put("lang", element.getAttribute("lang"));
                    adapterNode.put("description", element.getElementsByTagName("description").item(0).getTextContent());

                    // Los parámetros opcionales son un array
                    ArrayNode adapterParameters = this.getAdapterParameters(element.getElementsByTagName("parameter"));
                    adapterNode.set("parameters", adapterParameters);

                    // Los modelos disponibles para el adaptador son un array
                    adapterNode.put("models_enabled", element.getAttribute("models").equals("true"));
                    if (adapterNode.get("models_enabled").asBoolean())
                        adapterNode.set("models", this.getAdapterModels(element.getAttribute("class")));

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

    /**
     * Modelos disponibles para aplicar a un adaptador (clasificador).
     * Se recuperan de la base de datos.
     * @param adapterClass Identificador del adaptador para el cual obtener los modelos disponibles.
     * @return Objeto ArrayNode que representa en formato JSON los modelos disponibles
     */
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

    /**
     * Genera un objeto JSON con los parámetros opcionales que tiene un adaptador
     * según el archivo de configuración correspondiente.
     * Se separa este método de {@link #getSentimentAdapters(String adapterType)} para
     * hacer más legible el código.
     * @param parameters Lista de Nodos parámetro DOM
     * @return Objeto ArrayNode que representa en formato JSON los parámetros disponibles
     */
    private ArrayNode getAdapterParameters(NodeList parameters) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode adapterParameters = mapper.createArrayNode();

        for (int i = 0; i < parameters.getLength(); i++) {
            ObjectNode paramNode = mapper.createObjectNode();
            Node parameterNode = parameters.item(i);
            Element parameter = (Element) parameterNode;

            paramNode.put("name", parameter.getAttribute("name"));
            paramNode.put("type", parameter.getAttribute("type"));
            paramNode.put("id", parameter.getAttribute("id"));
            paramNode.put("default", parameter.getAttribute("default"));
            paramNode.put("description", parameter.getElementsByTagName("description").item(0).getTextContent());

            ArrayNode parameterOptions = mapper.createArrayNode();
            NodeList options = parameter.getElementsByTagName("option");
            for (int j = 0; j < options.getLength(); j++) {
                ObjectNode optionObjNode = mapper.createObjectNode();
                Node optionNode = options.item(j);
                Element option = (Element) optionNode;
                optionObjNode.put("name", option.getAttribute("name"));
                optionObjNode.put("value", option.getAttribute("value"));
                parameterOptions.add(optionObjNode);
            }
            paramNode.set("options", parameterOptions);
            adapterParameters.add(paramNode);
        }
        return adapterParameters;
    }

}
