package es.uned.controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.components.ConfigParser;
import es.uned.components.TrakttvLookup;
import es.uned.services.AccountService;
import es.uned.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping(value = "/api")
public class RESTController {

    @Autowired
    private ConfigParser configParser;

    @Autowired
    private TrakttvLookup trakttvLookup;

    @Autowired
    private SearchService searchService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/corpora-sources", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayNode> sources(@RequestBody Map postData) {
        String lang = (postData.get("lang") != null ? postData.get("lang").toString() : null);
        return ResponseEntity.status(HttpStatus.OK).body(configParser.getCorporaSources(lang));
    }

    @RequestMapping(value = "/classifiers/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayNode> opinionClassifiers(Principal principal,
                                                        @PathVariable("type") String classifierType,
                                                        @RequestBody Map postData)
    {
        String lang = (postData.get("lang") != null ? postData.get("lang").toString() : null);
        boolean creation = postData.get("creation").equals("true");
        ArrayNode response = configParser.getClassifiers(classifierType, principal, lang, creation);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /*@RequestMapping(value = "/sentiment-adapters", method = RequestMethod.GET)
    public ResponseEntity<ArrayNode> sentimentAdapters(Principal principal, @RequestParam("create_params") Optional<String> create_params) {
        boolean creation = false;
        if (create_params.isPresent() && create_params.get().equals("true"))
            creation = true;
        ArrayNode response = configParser.getAdapters("sentiment", principal, creation);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/subjectivity-adapters", method = RequestMethod.GET)
    public ResponseEntity<ArrayNode> subjectivityAdapters(Principal principal, @RequestParam("create_params") Optional<String> create_params) {
        boolean creation = false;
        if (create_params.isPresent() && create_params.get().equals("true"))
            creation = true;
        ArrayNode response = configParser.getAdapters("subjectivity", principal, creation);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }*/

    @RequestMapping(value = "/imdb-lookup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObjectNode> imdbLookup(@RequestParam(value = "q", required = true) String title,
                                                 @RequestParam(value = "page", required = false) String page) {
        ObjectNode response = trakttvLookup.lookup(title, (page == null ? "1" : page));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/searches" , method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObjectNode> searches(Principal principal) {
        ObjectNode response = searchService.JSONsearches(accountService.findByUserName(principal.getName()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
