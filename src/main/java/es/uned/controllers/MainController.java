package es.uned.controllers;

import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.entities.Search;
import es.uned.services.MySearchService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MainController {

    @Autowired private SourceAdapterFactory sourceFactory;
    @Autowired private SubjectivityAdapterFactory subjectivityFactory;
    @Autowired private SentimentAdapterFactory sentimentFactory;
    @Autowired private MySearchService searchService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("searchForm", new Search());
        return "home";
    }

    @RequestMapping(value = "/results", method = RequestMethod.POST)
    public String home(Model model, @ModelAttribute("searchForm") Search search,
                       BindingResult searchFormErrors, HttpServletRequest request) {
        search.makeExtraParams(request.getParameterMap());;
        SourceAdapter sourceAdapter = sourceFactory.get(search.getSourceClass());
        sourceAdapter.doSearch(search);

        if (search.isClassifySubjectivity()) {
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(search.getSubjectivityAdapter());
            subjectivityAdapter.analyze(search);
        }

        SentimentAdapter sentimentAdapter = sentimentFactory.get(search.getSentimentAdapter());
        sentimentAdapter.analyze(search);
        searchService.save(search);
        model.addAttribute("comments", search.getComments());
        return "results";
    }

    @RequestMapping(value = "test")
    public String testClassifier() {
        //SentimentAdapter sentimentAdapter = sentimentFactory.get("es.uned.adapters.sentiment.LingPipe");

        //sentimentAdapter.createModel();
        return "home";
    }

}
