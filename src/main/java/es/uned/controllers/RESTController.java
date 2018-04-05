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
import es.uned.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
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
    CorpusService corpusService;

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

    @RequestMapping(value = "/imdb-lookup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ObjectNode> imdbLookup(@RequestParam(value = "q", required = true) String title,
                                                 @RequestParam(value = "page", required = false) String page) {
        ObjectNode response = trakttvLookup.lookup(title, (page == null ? "1" : page));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/user-corpora", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayNode> getUserCorpora(Principal principal) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode response = mapper.createArrayNode();
        Account account = accountService.findByUserName(principal.getName());
        List<Corpus> corpora = corpusService.findByOwner(account);
        corpora.forEach(corpus -> {
            ObjectNode corpusNode = corpus.toJson(false, false);
            ArrayNode analysesArray = mapper.createArrayNode();
            corpus.getAnalyses().forEach(analysis ->
                    analysesArray.add(analysis.toJson(false))
            );
            corpusNode.set("analyses", analysesArray);
            response.add(corpusNode);
        });
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /*@RequestMapping(value = "/searches" , method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObjectNode> searches(Principal principal) {
        ObjectNode response = searchService.JSONsearches(accountService.findByUserName(principal.getName()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }*/
}
