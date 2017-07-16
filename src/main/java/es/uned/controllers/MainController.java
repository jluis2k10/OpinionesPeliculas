package es.uned.controllers;

import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Controller
public class MainController {

    @Autowired
    private SourceAdapterFactory sourceFactory;

    @Autowired
    private SubjectivityAdapterFactory subjectivityFactory;

    @Autowired
    private SentimentAdapterFactory sentimentFactory;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("searchForm", new SearchParams());
        return "home";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String home(Model model, @ModelAttribute("searchForm") SearchParams searchParams,
                       BindingResult searchFormErrors, HttpServletRequest request) {
        Map<String,String> optionalParameters = searchParams.getOptionalParameters(request.getParameterMap());

        SourceAdapter sourceAdapter = sourceFactory.get(searchParams.getSourceClass());
        HashMap<Integer,CommentWithSentiment> comments = sourceAdapter.getComments(searchParams);

        if (searchParams.isClassifySubjectivity()) {
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(searchParams.getSubjectivityAdapter());
            subjectivityAdapter.analyze(comments, searchParams, optionalParameters);
        }

        SentimentAdapter sentimentAdapter = sentimentFactory.get(searchParams.getSentimentAdapter());
        sentimentAdapter.analyze(comments, searchParams, optionalParameters);

        model.addAttribute("comments", comments);
        return "home";
    }

}
