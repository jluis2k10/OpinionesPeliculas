package es.uned.controllers;

import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.components.SearchWrapper;
import es.uned.entities.Search;
import es.uned.services.AdapterModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Controller
@Scope("request")
public class MainController {

    @Autowired private SourceAdapterFactory sourceFactory;
    @Autowired private SubjectivityAdapterFactory subjectivityFactory;
    @Autowired private SentimentAdapterFactory sentimentFactory;
    @Autowired private SearchWrapper searchWrapper;

    @Autowired private AdapterModelService adapterModelService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("searchForm", new Search());
        return "home";
    }

    @RequestMapping(value = "/results", method = RequestMethod.POST)
    public String home(Model model, @ModelAttribute("searchForm") Search search,
                       BindingResult searchFormErrors, HttpServletRequest request) {
        search.makeExtraParams(request.getParameterMap());
        // TODO: hay que comprobar antes que el formulario contiene un id o un id válido
        search.setSentimentModel(adapterModelService.findOne(search.getSentimentModel().getId()));
        if (search.isClassifySubjectivity())
            search.setSubjectivityModel(adapterModelService.findOne(search.getSubjectivityModel().getId()));
        SourceAdapter sourceAdapter = sourceFactory.get(search.getSourceClass());
        sourceAdapter.doSearch(search);

        if (search.isClassifySubjectivity()) {
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(search.getSubjectivityAdapter());
            subjectivityAdapter.analyze(search);
        }

        SentimentAdapter sentimentAdapter = sentimentFactory.get(search.getSentimentAdapter());
        sentimentAdapter.analyze(search);

        searchWrapper.setSearch(search);

        model.addAttribute("search", search);
        return "results";
    }
}
