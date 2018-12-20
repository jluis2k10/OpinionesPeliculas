package es.uned.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.entities.Account;
import es.uned.entities.LanguageModel;
import es.uned.services.AccountService;
import es.uned.services.LanguageModelService;
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
import java.util.Arrays;
import java.util.Set;

/**
 * Esta clase se utiliza para leer los archivos de configuración (XMLs) y generar un objeto
 * compatible con JSON (utilizando las librerías FasterXML/Jackson) que puede ser enviado
 * a la vista.
 */
@Component
public class ConfigParser {

    @Autowired private ResourceLoader resourceLoader;
    @Autowired private LanguageModelService languageModelService;
    @Autowired private AccountService accountService;

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Archivo XML con información sobre los adaptadores para fuentes de comentarios.
     */
    private static final String SOURCE_XML = "/sourceAdapters.xml";

    /**
     * Archivo XML con información sobre los adaptadores para análisis de polaridad.
     */
    private static final String POLARITY_XML = "/sentimentAdapters.xml";

    /**
     * Archivo XML con información sobre los adaptadores para análisis de opinión.
     */
    private static final String OPINION_XML = "/subjectivityAdapters.xml";

    /**
     * Archivo XML con información sobre los adaptadores para análisis de dominio.
     */
    private static final String DOMAIN_XML = "/domainAdapters.xml";

    /**
     * Devuelve objeto JSON con el listado de los adaptadores disponibles para recuperar comentarios
     * de diferentes fuentes y sus opciones.
     * @param selectedLang Idioma seleccionado
     * @return Objeto JSON
     */
    public ArrayNode getCorporaSources(String selectedLang) {
        ArrayNode results = mapper.createArrayNode();
        NodeList adapters = this.readXML(SOURCE_XML);
        for (int i = 0; i < adapters.getLength(); i++) {
            Node adapter = adapters.item(i);
            if (adapter.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element e = (Element) adapter;
            ObjectNode adapterNode = mapper.createObjectNode();
            adapterNode.put("name", e.getAttribute("name"));
            adapterNode.put("searchTermEnabled", e.getAttribute("searchTermEnabled").equals("true"));
            adapterNode.put("adapterClass", e.getElementsByTagName("class").item(0).getTextContent());
            adapterNode.put("limit", e.getAttribute("limit").equals("true"));
            adapterNode.put("sinceDate", e.getAttribute("sinceDate").equals("true"));
            adapterNode.put("untilDate", e.getAttribute("untilDate").equals("true"));
            adapterNode.put("chooseLanguage", e.getAttribute("languages").equals("true"));
            adapterNode.set("languages", this.constructLanguagesList(e.getElementsByTagName("languages").item(0).getChildNodes()));
            adapterNode.put("imdbIDEnabled", e.getAttribute("imdbID").equals("true"));
            adapterNode.put("fileUpload", e.getAttribute("fileUpload").equals("true"));
            adapterNode.put("textDataset", e.getAttribute("textDataset").equals("true"));
            adapterNode.set("options", this.getAdapterParameters(e, false));
            if ((selectedLang != null && this.hasSelectedLanguage(e, selectedLang)) || selectedLang == null)
                results.add(adapterNode);
        }
        return results;
    }

    /**
     * Devuelve un objeto JSON con el listado de los adaptadores disponibles para realizar análisis sobre un
     * corpus.
     * Si el adaptador cuenta con modelos de lenguaje, también son incluídos en el objeto JSON teniendo
     * en cuenta su dueño y si son públicos o no.
     * También incluye los parámetros necesarios para añadir nuevos modelos de lenguaje si así se solicita.
     * @param classifierType Tipo de clasificadores que recuperar (opinión o polaridad)
     * @param principal      Token de autenticación del usuario
     * @param lang           Idioma del corpus. No todos los adaptadores trabajan en todos los idiomas
     * @param creation       Indica si se deben incluir los parámetros necesarios para la creación
     *                       de nuevos modelos de lenguaje
     * @return Objeto JSON
     */
    public ArrayNode getClassifiers(String classifierType, Principal principal, String lang, boolean creation) {
        ArrayNode results = mapper.createArrayNode();
        NodeList adapters = null;
        Account account = (principal == null ? null : accountService.findByUserName(principal.getName()));

        if (classifierType.equals("opinion"))
            adapters = readXML(OPINION_XML);
        else if (classifierType.equals("polarity"))
            adapters = readXML(POLARITY_XML);
        else if (classifierType.equals("domain"))
            adapters = readXML(DOMAIN_XML);

        for (int i = 0; i < adapters.getLength(); i++) {
            Node adapter = adapters.item(i);
            if (adapter.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element e = (Element) adapter;

            String[] langs = e.getAttribute("lang").split(",");
            if (!Arrays.stream(langs).anyMatch(lang::equals)) // Clasificador no es aplicable al idioma indicado, ignorar
                continue;

            ObjectNode adapterNode = mapper.createObjectNode();
            adapterNode.put("name", e.getAttribute("name"));
            adapterNode.put("type", classifierType);
            adapterNode.put("class", e.getAttribute("class"));
            adapterNode.put("lang", lang);
            adapterNode.put("models_enabled", e.getAttribute("models").equals("true"));
            if (adapterNode.get("models_enabled").asBoolean())
                adapterNode.set("models", this.getAdapterModels(e.getAttribute("class"), lang, account));
            adapterNode.put("model_creation", e.getAttribute("model_creation").equals("true"));
            adapterNode.put("description", e.getElementsByTagName("description").item(0).getTextContent());
            if (!creation)
                adapterNode.set("parameters", this.getAdapterParameters(e, creation));
            else
                adapterNode.set("model_creation_params", this.getAdapterParameters(e, creation));

            if (creation && adapterNode.get("models_enabled").asBoolean() && adapterNode.get("model_creation").asBoolean())
                results.add(adapterNode);
            else if (!creation && !adapterNode.get("models_enabled").asBoolean())
                results.add(adapterNode);
            else if (!creation && adapterNode.get("models_enabled").asBoolean() && adapterNode.get("models").size() > 0)
                results.add(adapterNode);
        }

        return results;
    }

    /**
     * Lee un archivo XML y devuelve su estructura como una lista de nodos JSON
     * @param xml Archivo XML a parsear
     * @return Lista de nodos JSON
     */
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

    /**
     * En el archivo XML de configuración con las fuentes de comentarios disponibles,
     * se indica los idiomas soportados de la siguiente forma:
     * <languages>
     *     <lang desc="Idioma">id</lang>
     *     <lang ...
     * </languages>
     * Este método procesa una lista de todos los nodos hijos del nodo <languages> y devuelve
     * un ObjectNode en el que cada subelemento es un nodo con el par (Idioma, id)
     * @param languages Lista de nodos hijos de <languages>
     * @return ObjectNode con los pares (Idioma, id)
     */
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
     * Este método comprueba si el adaptador de una fuente de comentarios soporta el
     * lenguaje indicado.
     * @param e            Elemento obtenido parseando un archivo XML de configuración
     * @param selectedLang Lenguaje seleccionado
     * @return true si el elemento soporta el lenguaje seleccionado, false en caso contrario
     */
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

    /**
     * Parsea los parámetros opcionales de cada nodo padre en los archivos de configuración para los
     * adaptadores disponibles.
     * Estos parámetros no tienen una estructura predefinida y varían de un adaptador a otro, en
     * función de las opciones con las que se pueden configurar.
     * Cuelgan de los nodos <adapter> como elementos <parameter> en los archivos XML de configuración.
     * @param adapter         Elemento "raíz" (<adapter>) de un archivo XML.
     * @param creation_params Indica si además se deben incluir o no los elementos correspondientes
     *                        a los parámetros para la creación de modelos de lenguaje.
     * @return Lista de nodos JSON con los parámetros parseados
     */
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
     * @param parameter Parámetro a procesar
     * @return ObjectNode resultado de haber parseado el parámetro
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
     * Construir las opciones disponibles para cada parámetro.
     * Cada opción de un parámetro se indica mediante la etiqueta <option></option> en el
     * archivo de configuración XML.
     * @param parameter Parámetro a procesar
     * @return Lista de Nodos JSON con las opciones parseadas
     */
    private ArrayNode constructParameterOptions(Element parameter) {
        ArrayNode optionsArray = mapper.createArrayNode();
        NodeList options = parameter.getChildNodes(); // Los hijos de <parameter> deben ser todos un elemento <option>

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
     * Devuelve los modelos de lenguaje disponibles para el adaptador teniendo en cuenta su
     * dueño, su lenguaje y si son públicos o no.
     * @param adapterClass Clase del adaptador (hace de identificador en la BBDD)
     * @param lang         Idioma que debe soportar el modelo de lenguaje
     * @param account      Token de autenticación del usuario
     * @return Lista de nodos JSON con los modelos de lenguaje disponibles
     */
    private ArrayNode getAdapterModels(String adapterClass, String lang, Account account) {
        ArrayNode results = mapper.createArrayNode();
        Set<LanguageModel> models = languageModelService.findByAdapterClassAndLang(adapterClass, lang, account);
        models.forEach(languageModel -> {
            ObjectNode modelNode = mapper.createObjectNode();
            modelNode.put("id", languageModel.getId());
            modelNode.put("name", languageModel.getName());
            modelNode.put("location", languageModel.getLocation());
            modelNode.put("lang", languageModel.getLanguage());
            modelNode.put("trainable", languageModel.isTrainable());
            modelNode.put("is_public", languageModel.isPublic());
            modelNode.put("description", languageModel.getDescription());
            results.add(modelNode);
        });
        return results;
    }
}
