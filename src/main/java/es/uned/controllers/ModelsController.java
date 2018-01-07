package es.uned.controllers;

import es.uned.adapters.AdapterType;
import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.entities.Account;
import es.uned.entities.AdapterModels;
import es.uned.entities.Search;
import es.uned.entities.TrainParams;
import es.uned.services.AccountService;
import es.uned.services.AdapterModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Autowired private AccountService accountService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String myModels(Model model, Principal principal) {
        if (principal != null) {
            Account account = accountService.findByUserName(principal.getName());
            Set<AdapterModels> userSentimentModels = adapterModelService.findUserModels(account, AdapterType.SENTIMENT);
            Set<AdapterModels> userSubjectivityModels = adapterModelService.findUserModels(account, AdapterType.SUBJECTIVITY);
            model.addAttribute("sentimentModels", userSentimentModels);
            model.addAttribute("subjectivityModels", userSubjectivityModels);
            if (account.isAdmin()) {
                Set<AdapterModels> allSentimentModels = adapterModelService.findFromOthers(account, AdapterType.SENTIMENT);
                Set<AdapterModels> allSubjectivityModels = adapterModelService.findFromOthers(account, AdapterType.SUBJECTIVITY);
                model.addAttribute("allSentimentModels", allSentimentModels);
                model.addAttribute("allSubjectivityModels", allSubjectivityModels);
            }
        }
        return "my-models";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("modelForm", new AdapterModels());
        return "create_model";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model, @ModelAttribute("modelForm") AdapterModels aModel,
                         BindingResult modelFormErrors, HttpServletRequest servletRequest,
                         Principal principal) {
        Map<String,String> modelParameters = aModel.getModelParameters(servletRequest.getParameterMap());
        List<String> positivesSubjectives = aModel.getPositivesSubjectives();
        List<String> negativesSubjectives = aModel.getNegativesObjectives();
        if (principal != null) {
            Account account = accountService.findByUserName(principal.getName());
            aModel.setOwner(account);
        }
        if (modelParameters.get("classifierType").equals("polarity")) {
            aModel.setAdapterType(AdapterType.SENTIMENT);
            SentimentAdapter adapter = sentimentFactory.get(aModel.getAdapterClass());
            adapter.createModel(aModel.getLocation(), modelParameters, positivesSubjectives, negativesSubjectives);
        } else {
            aModel.setAdapterType(AdapterType.SUBJECTIVITY);
            SubjectivityAdapter adapter = subjectivityFactory.get(aModel.getAdapterClass());
            adapter.createModel(aModel.getLocation(), modelParameters, positivesSubjectives, negativesSubjectives);
        }
        adapterModelService.save(aModel);
        return "create_model";
    }

    @RequestMapping(value = "train/{id}", method = RequestMethod.GET)
    public String train(Model model, Principal principal, @PathVariable("id") Long modelID) {
        AdapterModels adapterModel = adapterModelService.findOne(modelID);
        Account account = accountService.findByUserName(principal.getName());
        if (!adapterModel.isTrainable() || (!account.isAdmin() && !adapterModel.getOwner().equals(account))) {
            return "redirect:/denied";
        }
        TrainParams trainParams = new TrainParams(adapterModel);
        model.addAttribute("trainForm", trainParams);
        return "train";
    }

    @RequestMapping(value = "train/{id}", method = RequestMethod.POST)
    public String train(Model model, @PathVariable("id") Long modelId,
                        @ModelAttribute("trainForm") TrainParams trainForm, BindingResult trainFormErrors) {
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
            Search search = new Search(trainForm);
            sourceAdapter.doSearch(search);
            model.addAttribute("trainForm", trainForm);
            model.addAttribute("comments", search.getComments());
            return "train_comments";
        }

        if (trainForm.getAdapterType() == AdapterType.SENTIMENT) {
            SentimentAdapter sentimentAdapter = sentimentFactory.get(trainForm.getAdapterClass());
            sentimentAdapter.trainModel(trainForm.getModelLocation(), pos_subj, neg_obj);
        } else {
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(trainForm.getAdapterClass());
            subjectivityAdapter.trainModel(trainForm.getModelLocation(), pos_subj, neg_obj);
        }

        return "my-models";
    }

}
