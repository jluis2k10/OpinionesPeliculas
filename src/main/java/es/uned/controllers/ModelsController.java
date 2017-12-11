package es.uned.controllers;

import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.components.Train2SearchConverter;
import es.uned.entities.AdapterModel;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.TrainParams;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Controller
@RequestMapping(value = "/models")
public class ModelsController {

    @Autowired private SourceAdapterFactory sourceFactory;
    @Autowired private SubjectivityAdapterFactory subjectivityFactory;
    @Autowired private SentimentAdapterFactory sentimentFactory;
    @Autowired private AdapterModelService adapterModelService;
    @Autowired private Train2SearchConverter train2SearchConverter;

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

    @RequestMapping(value = "train", method = RequestMethod.GET)
    public String train(Model model) {
        model.addAttribute("trainForm", new TrainParams());
        return "train";
    }

    @RequestMapping(value = "train", method = RequestMethod.POST)
    public String train(Model model, @ModelAttribute("trainForm") TrainParams trainForm, BindingResult trainFormErrors) {
        List<String> pos_subj;
        List<String> neg_obj;

        if (trainForm.getSourceClass().equals("FileDataset")) {
            pos_subj = trainForm.sentenceList(trainForm.getPsFile());
            neg_obj = trainForm.sentenceList(trainForm.getNoFile());
        } else if(trainForm.getSourceClass().equals("TextDataset")) {
            pos_subj = trainForm.sentenceList(trainForm.getPsText());
            neg_obj = trainForm.sentenceList(trainForm.getNoText());
        } else {
            SourceAdapter sourceAdapter = sourceFactory.get(trainForm.getSourceClass());
            HashMap<Integer,CommentWithSentiment> comments = sourceAdapter.getComments(train2SearchConverter.convert(trainForm));
            model.addAttribute("trainForm", trainForm);
            model.addAttribute("comments", comments);
            return "train_comments";
        }

        if (trainForm.getAnalysisType().equals("polarity")) {
            SentimentAdapter sentimentAdapter = sentimentFactory.get(trainForm.getAdapterClass());
            sentimentAdapter.trainModel(trainForm.getModelLocation(), pos_subj, neg_obj);
        } else {
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(trainForm.getAdapterClass());
            subjectivityAdapter.trainModel(trainForm.getModelLocation(), pos_subj, neg_obj);
        }

        return "train";
    }

}
