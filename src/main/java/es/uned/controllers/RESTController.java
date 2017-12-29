package es.uned.controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.components.ConfigParser;
import es.uned.components.TrakttvLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @RequestMapping(value = {"/sentiment-adapters", "/sentiment-adapters/{userID}"}, method = RequestMethod.GET)
    public ResponseEntity<ArrayNode> sentimentAdapters(@PathVariable Optional<Long> userID ) {
        Long uid = null;
        if (userID.isPresent())
            uid = userID.get();
        ArrayNode response = configParser.getAdapters("sentiment", uid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = {"/subjectivity-adapters", "/subjectivity-adapters/{userID}"}, method = RequestMethod.GET)
    public ResponseEntity<ArrayNode> subjectivityAdapters(@PathVariable Optional<Long> userID) {
        Long uid = null;
        if (userID.isPresent())
            uid = userID.get();
        ArrayNode response = configParser.getAdapters("subjectivity", uid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/imdb-lookup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObjectNode> imdbLookup(@RequestParam(value = "q", required = true) String title,
                                                 @RequestParam(value = "page", required = false) String page) {
        ObjectNode response = trakttvLookup.lookup(title, (page == null ? "1" : page));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
