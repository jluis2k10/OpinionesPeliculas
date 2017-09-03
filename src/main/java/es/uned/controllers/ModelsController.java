package es.uned.controllers;

import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.entities.AdapterModel;
import es.uned.services.AdapterModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Controller
@RequestMapping(value = "/models")
public class ModelsController {

    @Autowired private SubjectivityAdapterFactory subjectivityFactory;
    @Autowired private SentimentAdapterFactory sentimentFactory;
    @Autowired private AdapterModelService adapterModelService;

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("modelForm", new AdapterModel());
        return "create_model";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model, @ModelAttribute("modelForm") AdapterModel adapterModel,
                         BindingResult modelFormErrors, HttpServletRequest servletRequest) {
        Map<String,String> modelParameters = adapterModel.getModelParameters(servletRequest.getParameterMap());
        List<String> positivesSubjectives = adapterModel.getPositivesSubjectives();
        List<String> negativesSubjectives = adapterModel.getNegativesObjectives();
        if (modelParameters.get("classifierType").equals("polarity")) {
            SentimentAdapter sentimentAdapter = sentimentFactory.get(adapterModel.getAdapterClass());
            sentimentAdapter.createModel(adapterModel.getLocation(), modelParameters, positivesSubjectives, negativesSubjectives);
        } else {
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(adapterModel.getAdapterClass());
            subjectivityAdapter.createModel(adapterModel.getLocation(), modelParameters, positivesSubjectives, negativesSubjectives);
        }
        adapterModelService.save(adapterModel);
        return "create_model";
    }

}
