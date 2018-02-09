package es.uned.controllers;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

/**
 *
 */
@Controller
@RequestMapping(value = "/searches")
@Scope("request")
public class SearchesController {

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
        if (search.getOwner() != account)
            return "redirect:/denied";
        return "update-search";
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
