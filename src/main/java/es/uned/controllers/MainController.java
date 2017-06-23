package es.uned.controllers;

import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.entities.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;

/**
 *
 */
@Controller
public class MainController {

    @Autowired
    private SourceAdapterFactory sourceFactory;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("searchForm", new SearchParams());
        return "home";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String home(Model model, @ModelAttribute("searchForm") SearchParams searchParams,
                       BindingResult searchFormErrors) {
        SourceAdapter sourceAdapter = sourceFactory.get(searchParams.getSourceClass());
        HashMap<Integer,String> comments = sourceAdapter.getComments(searchParams);
        model.addAttribute("comments", comments);
        return "home";
    }

}
