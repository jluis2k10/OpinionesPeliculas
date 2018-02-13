package es.uned.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.entities.Account;
import es.uned.entities.AdapterModels;
import es.uned.services.AccountService;
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
import java.security.Principal;
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
    @Autowired
    private AccountService accountService;

    private static final String SOURCE_XML = "/sourceAdapters.xml";
    private static final String SENTIMENT_XML = "/sentimentAdapters.xml";
    private static final String SUBJECTIVITY_XML = "/subjectivityAdapters.xml";

    private ObjectMapper mapper = new ObjectMapper();

    public ArrayNode getAllSources(String selectedLang, String adapterClass) {
        ArrayNode results = mapper.createArrayNode();
        NodeList adapters = this.readXML(SOURCE_XML);
        for (int i = 0; i < adapters.getLength(); i++) {
            Node adapter = adapters.item(i);
            if (adapter.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element e = (Element) adapter;
            if (adapterClass != null && !adapterClass.equals(e.getElementsByTagName("class").item(0).getTextContent()))
                continue;
            ObjectNode adapterNode = mapper.createObjectNode();
            adapterNode.put("name", e.getAttribute("name"));
            adapterNode.put("adapterClass", e.getElementsByTagName("class").item(0).getTextContent());
            adapterNode.put("limitEnabled", e.getAttribute("limit").equals("true"));
            adapterNode.put("sinceDateEnabled", e.getAttribute("sinceDate").equals("true"));
            adapterNode.put("untilDateEnabled", e.getAttribute("untilDate").equals("true"));
            adapterNode.put("chooseLanguage", e.getAttribute("languages").equals("true"));
            adapterNode.set("languages", this.constructLanguagesList(e.getElementsByTagName("languages").item(0).getChildNodes()));
            adapterNode.put("imdbIDEnabled", e.getAttribute("imdbID").equals("true"));
            adapterNode.put("cleanTweet", e.getAttribute("cleanTweet").equals("true"));
            adapterNode.put("updateable", e.getAttribute("updateable").equals("true"));
            adapterNode.set("extra_parameters", this.getAdapterParameters(e, false));
            if (selectedLang != null && this.hasSelectedLanguage(e, selectedLang))
                results.add(adapterNode);
            else if (selectedLang == null)
                results.add(adapterNode);

        }
        return results;
    }

    private NodeList readXML(String xml) {
        Resource resource = resourceLoader.getResource("classpath:" + xml);
        NodeList adapters = null;
        try {
            File xmlFile = resource.getFile();
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmlDoc = docBuilder.parse(xmlFile);
            xmlDoc.getDocumentElement().normalize();
            adapters = xmlDoc.getElementsByTagName("adapter");
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return adapters;
    }

    private boolean hasSelectedLanguage(Element e, String selectedLang) {
        NodeList languages = e.getElementsByTagName("languages").item(0).getChildNodes();
        for (int i = 0; i < languages.getLength(); i++) {
            Node lang = languages.item(i);
            if (lang.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element element = (Element) lang;
            if (element.getTextContent().equals(selectedLang))
                return true;
        }
        return false;
    }

    private ObjectNode constructLanguagesList(NodeList languages) {
        ObjectNode languagesON = mapper.createObjectNode();
        for (int i = 0; i < languages.getLength(); i++) {
            Node langNode = languages.item(i);
            if (langNode.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) langNode;
                languagesON.put(e.getAttribute("desc"), e.getTextContent());
            }
        }
        return languagesON;
    }

    /**
     * Adaptadores disponibles.
     * Genera objeto JSON desde el archivo de configuración
     * @return Objeto ArrayNode que representa al archivo de configuración XML
     */
    public ArrayNode getAdapters(String adapterType, Principal principal, boolean creation_params) {
        ArrayNode results = mapper.createArrayNode();
        NodeList adapters = null;
        Account account = null;

        if (principal != null)
            account = accountService.findByUserName(principal.getName());

        switch (adapterType) {
            case "sentiment":
                adapters = readXML(SENTIMENT_XML);
                break;
            case "subjectivity":
                adapters = readXML(SUBJECTIVITY_XML);
                break;
        }

        for (int i = 0; i < adapters.getLength(); i++) {
            Node adapter = adapters.item(i);
            if (adapter.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element element = (Element) adapter;
            ObjectNode adapterNode = mapper.createObjectNode();

            adapterNode.put("ID", element.getAttribute("id"));
            adapterNode.put("name", element.getAttribute("name"));
            adapterNode.put("class", element.getAttribute("class"));
            adapterNode.put("lang", element.getAttribute("lang"));
            adapterNode.put("models_enabled", element.getAttribute("models").equals("true"));
            adapterNode.set("models", this.getAdapterModels(element.getAttribute("class"), account));
            adapterNode.put("model_creation", element.getAttribute("model_creation").equals("true"));
            adapterNode.put("description", element.getElementsByTagName("description").item(0).getTextContent());
            if (!creation_params)
                adapterNode.set("parameters", this.getAdapterParameters(element, creation_params));
            else
                adapterNode.set("model_creation_params", this.getAdapterParameters(element, creation_params));

            if (creation_params && adapterNode.get("models_enabled").asBoolean() && adapterNode.get("model_creation").asBoolean())
                results.add(adapterNode);
            else if (!creation_params && !adapterNode.get("models_enabled").asBoolean())
                results.add(adapterNode);
            else if (!creation_params && adapterNode.get("models_enabled").asBoolean() && adapterNode.get("models").size() > 0)
                results.add(adapterNode);
        }
        
        return results;
    }

    private ArrayNode getAdapterParameters(Element adapter, boolean creation_params) {
        ArrayNode results = mapper.createArrayNode();
        NodeList allParameters = adapter.getElementsByTagName("parameter");
        for (int i = 0; i < allParameters.getLength(); i++) {
            Node parameter = allParameters.item(i);
            if (parameter.getNodeType() != Node.ELEMENT_NODE
                    || (!parameter.getParentNode().getNodeName().equals("adapter") && !creation_params)
                    || (!parameter.getParentNode().getNodeName().equals("model_creation_parameters") && creation_params))
                continue;
            results.add(constructParameter((Element) parameter));
        }
        return results;
    }

    /**
     * Construir el cuerpo de un parámetro
     * @param parameter
     * @return
     */
    private ObjectNode constructParameter(Element parameter) {
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
    private ArrayNode getAdapterModels(String adapterClass, Account account) {
        ArrayNode results = mapper.createArrayNode();
        Set<AdapterModels> models = adapterModelService.findByAdapterClass(adapterClass, account);
        for(AdapterModels model: models) {
            ObjectNode modelNode = mapper.createObjectNode();
            modelNode.put("id", model.getId());
            modelNode.put("name", model.getName());
            modelNode.put("location", model.getLocation());
            modelNode.put("lang", model.getLanguage());
            modelNode.put("trainable", model.isTrainable());
            modelNode.put("owner_id", model.getOwner().getId());
            modelNode.put("is_open", model.isOpen());
            modelNode.put("description", model.getDescription());
            results.add(modelNode);
        }
        return results;
    }
}
