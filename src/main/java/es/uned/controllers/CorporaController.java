package es.uned.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.entities.Account;
import es.uned.entities.Corpus;
import es.uned.services.AccountService;
import es.uned.services.CorpusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

/**
 *
 */
@Controller
@RequestMapping(value = "/corpora")
public class CorporaController {

    @Autowired
    CorpusService corpusService;

    @Autowired
    AccountService accountService;

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String myCorpora() {
        return "corpora/view_corpora";
    }

    @RequestMapping(value = "/view/{corpusID}", method = RequestMethod.GET)
    public String viewCorpus(@PathVariable("corpusID") Long corpusID, Principal principal, Model model) {
        Corpus corpus = corpusService.findOne(corpusID);
        Account account = accountService.findByUserName(principal.getName());
        if (account == null || account != corpus.getOwner())
            return "redirect:/denied";
        model.addAttribute("corpus", corpus);
        return "corpora/view_corpus";
    }

    @Transactional
    @RequestMapping(value = "/get-corpus", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> getCorpus(Principal principal,
        @RequestParam("id") Long corpusID)
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        Corpus corpus = corpusService.findOne(corpusID);
        Account account = accountService.findByUserName(principal.getName());

        if (account == null || account != corpus.getOwner())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        response = corpus.toJson(true, false);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional
    @RequestMapping(value = "/get-analyses", method = RequestMethod.POST)
    public ResponseEntity<ArrayNode> getAnalyses(Principal principal, @RequestParam("id") Long corpusID) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode response = mapper.createArrayNode();
        Corpus corpus = corpusService.findOne(corpusID);
        Account account = accountService.findByUserName(principal.getName());

        if (account == null || account != corpus.getOwner())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        if (corpus.getAnalyses() != null) {
            corpus.getAnalyses().forEach(analysis ->
                    response.add(analysis.toJson(true))
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
