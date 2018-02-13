package es.uned.controllers;

import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.components.SearchWrapper;
import es.uned.entities.Account;
import es.uned.entities.Search;
import es.uned.services.AccountService;
import es.uned.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 *
 */
@Controller
@RequestMapping(value = "/searches")
@Scope("request")
public class SearchesController {

    @Autowired private SourceAdapterFactory sourceFactory;
    @Autowired private SubjectivityAdapterFactory subjectivityFactory;
    @Autowired private SentimentAdapterFactory sentimentFactory;

    @Autowired private SearchService searchService;
    @Autowired private AccountService accountService;
    @Autowired private SearchWrapper searchWrapper;

    @RequestMapping(value = "")
    public String mySearches() {
        return "my-searches";
    }

    @RequestMapping(value = "/{searchID}", method = RequestMethod.GET)
    public String mySearches(@PathVariable("searchID") Long id, Principal principal, Model model) {
        Account account = accountService.findByUserName(principal.getName());
        Search search = searchService.findOne(id);
        if (search == null || (search.getOwner() != account && !account.isAdmin()))
            return "redirect:/denied";
        model.addAttribute("search", search);
        return "results";
    }

    @RequestMapping(value = "/update/{searchID}", method = RequestMethod.GET)
    public String updateSearch(@PathVariable("searchID") Long id, Principal principal, Model model) {
        Account account = accountService.findByUserName(principal.getName());
        Search search = searchService.findOne(id);
        if (search == null || search.getOwner() != account)
            return "redirect:/denied";
        model.addAttribute("search", search);
        return "update-search";
    }

    // https://stackoverflow.com/questions/22281543/posting-a-complete-model-object-to-the-controller-when-only-few-attributes-are-u
    @RequestMapping(value = "/update/{searchID}", method = RequestMethod.POST)
    public String updateSearch(@ModelAttribute("search") Search search, Principal principal, Model model) {
        Search dbSearch = searchService.findOne(search.getId());
        Account account = accountService.findByUserName(principal.getName());
        if (dbSearch == null || dbSearch.getOwner() != account)
            return "redirect:/denied";

        // Actualizar búsqueda con nuevos parámetros
        dbSearch.setLimit(search.getLimit());
        dbSearch.setSinceDate(search.getSinceDate());
        dbSearch.setUntilDate(search.getUntilDate());

        // Recuperar nuevos comentarios
        SourceAdapter sourceAdapter = sourceFactory.get(dbSearch.getSourceClass());
        int newComments = sourceAdapter.updateSearch(dbSearch);

        // Analizar subjetividad
        if (dbSearch.isClassifySubjectivity()) {
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(dbSearch.getSubjectivityAdapter());
            subjectivityAdapter.analyze(dbSearch);
        }
        // Analizar sentimiento
        SentimentAdapter sentimentAdapter = sentimentFactory.get(dbSearch.getSentimentAdapter());
        sentimentAdapter.analyze(dbSearch);

        searchWrapper.setSearch(dbSearch);
        model.addAttribute("newComments", newComments);
        model.addAttribute("search", dbSearch);
        return "results";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<String> save(Principal principal) {
        if (principal == null)
            return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
        Account account = accountService.findByUserName(principal.getName());
        Search search = searchWrapper.getSearch();
        search.setOwner(account);
        searchService.save(search);
        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<String> delete(@RequestBody Long searchID, Principal principal) {
        Search search = searchService.findOne(searchID);
        if (principal == null || search == null)
            return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
        Account account = accountService.findByUserName(principal.getName());
        if (search.getOwner() == account || account.isAdmin()) {
            searchService.delete(search);
            return new ResponseEntity<>("Ok", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }

}
