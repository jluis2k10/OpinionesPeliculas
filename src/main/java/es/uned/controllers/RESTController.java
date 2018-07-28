package es.uned.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.components.ConfigParser;
import es.uned.components.TrakttvLookup;
import es.uned.entities.Account;
import es.uned.entities.Corpus;
import es.uned.services.AccountService;
import es.uned.services.CorpusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Controlador general para peticiones REST
 */
@RestController
@RequestMapping(value = "/api")
public class RESTController {

    @Autowired private ConfigParser configParser;
    @Autowired private TrakttvLookup trakttvLookup;
    @Autowired private CorpusService corpusService;
    @Autowired private AccountService accountService;

    /**
     * Devuelve en formato JSON la lista de las fuentes de comentarios disponibles con sus opciones
     * @param postData Datos POST de la petición
     * @return Respuesta HTTP a la petición más los datos en formato JSON
     */
    @RequestMapping(value = "/corpora-sources", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayNode> sources(@RequestBody Map postData) {
        String lang = (postData.get("lang") != null ? postData.get("lang").toString() : null);
        return ResponseEntity.status(HttpStatus.OK).body(configParser.getCorporaSources(lang));
    }

    /**
     * Devuelve una lista en formato JSON de los clasificadores disponibles para realizar un análisis
     * @param principal      Token de autenticación del usuario
     * @param classifierType Tipo de clasificador (Opinión o Polaridad)
     * @param postData       Datos POST de la petición
     * @return Respuesta HTTP a la petición más los datos en formato JSON
     */
    @RequestMapping(value = "/classifiers/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayNode> getClassifiers(Principal principal,
                                                        @PathVariable("type") String classifierType,
                                                        @RequestBody Map postData)
    {
        String lang = (postData.get("lang") != null ? postData.get("lang").toString() : null);
        boolean creation = "true".equals(postData.get("creation").toString());
        ArrayNode response = configParser.getClassifiers(classifierType, principal, lang, creation);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Devuelve una lista con títulos de películas y su correspondiente identificador IMDB
     * @param title Título completo o parcial de la película
     * @param page  Página de la consulta, se corresponde con la paginación de la API de Traktv
     * @return Respuesta HTTP a la petición, consiste en una lista con los títulos más su identificador IMDB
     */
    @RequestMapping(value = "/imdb-lookup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ObjectNode> imdbLookup(@RequestParam(value = "q", required = true) String title,
                                                 @RequestParam(value = "page", required = false) String page) {
        ObjectNode response = trakttvLookup.lookup(title, (page == null ? "1" : page));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Devuelve una lista en formato JSON de los corpus almacenados del usuario
     * @param principal Token de autenticación del usuario
     * @param postData  Datos POST de la petición
     * @return Respuesta HTTP a la petición con los datos JSON del corpora del usuario
     */
    @RequestMapping(value = "/user-corpora", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayNode> getUserCorpora(Principal principal, @RequestBody Map postData) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);

        boolean withComments = "true".equals(postData.get("withComments").toString());
        boolean withAnalyses = "true".equals(postData.get("withAnalyses").toString());
        boolean withRecords  = "true".equals(postData.get("withRecords").toString());

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode response = mapper.createArrayNode();
        Account account = accountService.findByUserName(principal.getName());
        List<Corpus> corpora = corpusService.findByOwner(account);
        corpora.forEach(corpus ->
            response.add(corpus.toJson(withComments, withAnalyses, withRecords))
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
