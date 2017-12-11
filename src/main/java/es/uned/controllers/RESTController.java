package es.uned.controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.components.ConfigParser;
import es.uned.components.TrakttvLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/comments-source", method = RequestMethod.GET)
    public ResponseEntity<ArrayNode>  sources() {
        ArrayNode response = configParser.getSources();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/sentiment-adapters", method = RequestMethod.GET)
    public ResponseEntity<ArrayNode> sentimentAdapters() {
        ArrayNode response = configParser.getAdapters("sentiment");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/subjectivity-adapters", method = RequestMethod.GET)
    public ResponseEntity<ArrayNode> subjectivityAdapters() {
        ArrayNode response = configParser.getAdapters("subjectivity");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/imdb-lookup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObjectNode> imdbLookup(@RequestParam(value = "q", required = true) String title,
                                                 @RequestParam(value = "page", required = false) String page) {
        ObjectNode response = trakttvLookup.lookup(title, (page == null ? "1" : page));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
