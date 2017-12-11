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
import org.w3c.dom.*;
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

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Adaptadores disponibles para las fuentes de comentarios.
     * Genera objeto JSON desde el archivo de configuración /sourceAdapters.xml
     * @return Objeto ArrayNode que representa al archivo de configuración XML
     */
    public ArrayNode getSources() {
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
                    adapterNode.put("cleanTweet", element.getAttribute("cleanTweet").equals("true"));
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
     * Adaptadores disponibles.
     * Genera objeto JSON desde el archivo de configuración
     * @return Objeto ArrayNode que representa al archivo de configuración XML
     */
    public ArrayNode getAdapters(String adapterType) {
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
                    adapterNode.put("models_enabled", element.getAttribute("models").equals("true"));
                    adapterNode.put("model_creation", element.getAttribute("model_creation").equals("true"));
                    adapterNode.put("description", element.getElementsByTagName("description").item(0).getTextContent());
                    if (adapterNode.get("models_enabled").asBoolean())
                        adapterNode.set("models", this.getAdapterModels(element.getAttribute("class")));

                    this.constructAdapterParameters(adapterNode, element);
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
     * Construir los parámetros disponibles para el adaptador.
     * Existen parámetros para opciones que determinan el funcionamiento del clasificador a la hora de clasificar
     * un comentario, y parámetros para opciones que se utilizan cuando se pretende crear un modelo para el clasificador
     * @param adapterNode
     * @param element
     */
    private void constructAdapterParameters(ObjectNode adapterNode, Element element) {
        ArrayNode adapterParametersArray = mapper.createArrayNode();
        ArrayNode modelCreationParametersArray = mapper.createArrayNode();

        NodeList allParameters = element.getElementsByTagName("parameter");
        // Ahora hay tres posibles casos:
        //   1. Que sea un parámetro opcional del adaptador
        //   2. Que sea un parámetro para construir modelos del adaptador
        //   3. Que sea un parámetro dentro de una de las opciones de los parámetros utilizados
        //      para construir modelos del adaptador
        for (int i = 0; i < allParameters.getLength(); i++) {
            Element parameter = (Element) allParameters.item(i);
            if (parameter.getParentNode().getNodeName().equals("adapter")) {
                adapterParametersArray.add(constructParameter(parameter));
            } else if (parameter.getParentNode().getNodeName().equals("model_creation_parameters")) {
                modelCreationParametersArray.add(constructParameter(parameter));
            }
            // Ignoramos el caso 3. Ya se añade un parámetro de ese tipo mediante el método
            // constructParameterOptions() de forma recursiva.
        }
        if (adapterParametersArray.size() > 0)
            adapterNode.set("parameters", adapterParametersArray);
        if (modelCreationParametersArray.size() > 0)
            adapterNode.set("model_creation_parameters", modelCreationParametersArray);
    }

    /**
     * Construir el cuerpo de un parámetro
     * @param parameter
     * @return
     */
    private ObjectNode constructParameter (Element parameter) {
        ObjectNode parameterNode = mapper.createObjectNode();

        parameterNode.put("name", parameter.getAttribute("name"));
        parameterNode.put("type", parameter.getAttribute("type"));
        parameterNode.put("id", parameter.getAttribute("id"));
        parameterNode.put("default", parameter.getAttribute("default"));
        if (parameter.getElementsByTagName("description").getLength() > 0)
            parameterNode.put("description", parameter.getElementsByTagName("description").item(0).getTextContent());
        parameterNode.set("options", constructParameterOptions(parameter));

        return parameterNode;
    }

    /**
     * Construir las opciones disponibles para cada parámetro
     * @param parameter
     * @return
     */
    private ArrayNode constructParameterOptions(Element parameter) {
        ArrayNode optionsArray = mapper.createArrayNode();
        NodeList options = parameter.getChildNodes(); // Los hijos de "parameter" deben ser todos un elemento "option"

        for (int i = 0; i < options.getLength(); i++) {
            if (options.item(i).getNodeType() == Node.ELEMENT_NODE && options.item(i).hasAttributes()) {
                ObjectNode optionNode = mapper.createObjectNode();
                Element option = (Element) options.item(i);
                optionNode.put("name", option.getAttribute("name"));
                optionNode.put("value", option.getAttribute("value"));
                if (option.hasChildNodes()) { // Si la opción tiene hijos, construimos los parámetros de dicha opción
                    NodeList optionParameters = option.getElementsByTagName("parameter");
                    ArrayNode optionParametersArray = mapper.createArrayNode();
                    for (int j = 0; j < optionParameters.getLength(); j++) {
                        Element optionParameter = (Element) optionParameters.item(j);
                        optionParametersArray.add(constructParameter(optionParameter));
                    }
                    optionNode.set("parameters", optionParametersArray);
                }
                optionsArray.add(optionNode);
            }
        }

        return optionsArray;
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
            modelNode.put("trainable", model.isTrainable());
            modelNode.put("description", model.getDescription());
            results.add(modelNode);
        }
        return results;
    }
}
