package es.uned.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.adapters.ClassifierType;
import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.entities.Account;
import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.Record;
import es.uned.forms.AnalysisForm;
import es.uned.forms.AnalysisFormList;
import es.uned.forms.EditCorpusForm;
import es.uned.forms.SourceForm;
import es.uned.forms.validators.SourceFormValidator;
import es.uned.services.AccountService;
import es.uned.services.AnalysisService;
import es.uned.services.CorpusService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
@Controller
@SessionAttributes({"corpus"})
@RequestMapping(value = "/corpora")
public class CorporaController {

    @Autowired
    private CorpusService corpusService;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SentimentAdapterFactory sentimentFactory;

    @Autowired
    private SubjectivityAdapterFactory subjectivityFactory;

    @Autowired
    private SourceAdapterFactory sourceFactory;

    @Autowired
    private SourceFormValidator formValidator;

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

        response = corpus.toJson(true, false, false);
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

    @RequestMapping(value = "/edit/{corpusID}", method = RequestMethod.GET)
    public String editCorpus(@PathVariable(name = "corpusID") Long corpusID, Principal principal, Model model) {
        Corpus corpus = corpusService.findOne(corpusID);
        Account account = accountService.findByUserName(principal.getName());

        if (account == null || account != corpus.getOwner())
            return "redirect:/denied";

        EditCorpusForm corpusForm = new EditCorpusForm();
        corpusForm.setId(corpusID);
        corpusForm.setName(corpus.getName());
        corpusForm.setDescription(corpus.getDescription());
        model.addAttribute("editCorpusForm", corpusForm);
        return "corpora/edit_corpus";
    }

    @RequestMapping(value = "/edit/{corpusID}", method = RequestMethod.POST)
    public String editCorpus(@ModelAttribute("editCorpusForm") EditCorpusForm corpusForm, Principal principal) {
        Corpus corpus = corpusService.findOne(corpusForm.getId());
        Account account = accountService.findByUserName(principal.getName());

        if (account == null || account != corpus.getOwner())
            return "redirect:/denied";

        corpus.setName(corpusForm.getName());
        corpus.setDescription(corpusForm.getDescription());
        corpusService.quickSave(corpus);
        return "redirect:/corpora";
    }

    @RequestMapping(value = "/switchPublic", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> switchPublic(@RequestBody Long corpusID, Principal principal) {
        Corpus corpus = corpusService.findOne(corpusID);
        Account account = accountService.findByUserName(principal.getName());

        if (account == null || account != corpus.getOwner())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("forbidden");

        corpus.setPublic(!corpus.getIsPublic());
        corpusService.quickSave(corpus);
        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }

    @RequestMapping(value = "/delete-corpus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> deleteCorpus(@RequestBody Long corpusID, Principal principal) {
        Corpus corpus = corpusService.findOne(corpusID);
        Account account = accountService.findByUserName(principal.getName());

        if (corpus == null || account == null || account != corpus.getOwner())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("forbidden");

        corpusService.delete(corpus);

        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @RequestMapping(value = "/add-comments/{corpusID}", method = RequestMethod.GET)
    public String addComments(@PathVariable(name = "corpusID") Long corpusID, Principal principal, Model model) {
        Corpus corpus = corpusService.findOneFetchAll(corpusID);
        Account account = accountService.findByUserName(principal.getName());
        if (corpus == null || corpus.getOwner() != account)
            return "redirect:/denied";
        model.addAttribute("corpus", corpus);
        model.addAttribute("sourceForm", new SourceForm());
        return "corpora/add_comments";
    }

    @RequestMapping(value = "/add-comments/{corpusID}", method = RequestMethod.POST)
    public String addComments(@ModelAttribute("corpus") Corpus corpus, @ModelAttribute("sourceForm") SourceForm sourceForm,
                              BindingResult bindingResult, Model model, Principal principal,
                              @RequestParam(value = "action", required = false) String action) {
        Account account = accountService.findByUserName(principal.getName());
        if (!corpus.getOwner().equals(account))
            return "redirect:/denied";

        formValidator.validate(sourceForm, bindingResult);
        if (bindingResult.hasErrors())
            return "corpora/add_comments";

        if ("get-comments".equals(action)) {
            SourceAdapter sourceAdapter = sourceFactory.get(sourceForm.getSourceAdapter());
            sourceAdapter.setOptions(sourceForm);
            int newComments = sourceAdapter.updateCorpus(sourceForm, corpus);
            Map<String, String> flashMsg = new HashMap<String, String>(){{
                put("primary", "Añadidos <strong>" + newComments + "</strong> comentarios nuevos al Corpus.");
            }};
            model.addAttribute("flashMessage", flashMsg);
        }
        return "corpora/add_comments";
    }

    @RequestMapping(value = "/add-opinion-analysis/{corpusID}", method = RequestMethod.GET)
    public String addOpinionAnalysis(@PathVariable(name = "corpusID") Long corpusID, Principal principal, Model model) {
        Corpus corpus = corpusService.findOneFetchAll(corpusID);
        Account account = accountService.findByUserName(principal.getName());
        if (corpus == null || corpus.getOwner() != account)
            return "redirect:/denied";
        model.addAttribute("corpus", corpus);
        model.addAttribute("opinionForm", new AnalysisFormList());
        return "corpora/add_opinion_analysis";
    }

    @RequestMapping(value = "/add-opinion-analysis/{corpusID}", method = RequestMethod.POST)
    public String addOpinionAnalysis(@ModelAttribute("corpus") Corpus corpus, Model model, Principal principal,
                                     @ModelAttribute("opinionForm") AnalysisFormList analysisFormList,
                                     @RequestParam(value = "action", required = false) String action) {
        Account account = accountService.findByUserName(principal.getName());
        if (!corpus.getOwner().equals(account))
            return "redirect:/denied";

        if ("analyse".equals(action)) {
            Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
            while (it.hasNext()) {
                AnalysisForm analysisForm = it.next();
                SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(analysisForm.getAdapterClass());
                subjectivityAdapter.analyze(corpus, new Analysis(analysisForm));
            }
            Map<String, String> flashMsg = new HashMap<String, String>(){{
                put("primary", "Ejecutado/s <strong>" + analysisFormList.getAnalysis().size() + "</strong> análisis de opinión sobre el corpus.");
            }};
            model.addAttribute("flashMessage", flashMsg);
        }
        return "corpora/add_opinion_analysis";
    }

    @RequestMapping(value = "/add-polarity-analysis/{corpusID}", method = RequestMethod.GET)
    public String addPolarityAnalysis(@PathVariable(name = "corpusID") Long corpusID, Principal principal, Model model) {
        Corpus corpus = corpusService.findOneFetchAll(corpusID);
        Account account = accountService.findByUserName(principal.getName());
        if (corpus == null || corpus.getOwner() != account)
            return "redirect:/denied";
        model.addAttribute("corpus", corpus);
        model.addAttribute("polarityForm", new AnalysisFormList());
        return "corpora/add_polarity_analysis";
    }

    @RequestMapping(value = "/add-polarity-analysis/{corpusID}", method = RequestMethod.POST)
    public String addPolarityAnalysis(@ModelAttribute("corpus") Corpus corpus, Model model, Principal principal,
                                      @ModelAttribute("polarityForm") AnalysisFormList analysisFormList,
                                      @RequestParam(value = "action", required = false) String action) {
        Account account = accountService.findByUserName(principal.getName());
        if (!corpus.getOwner().equals(account))
            return "redirect:/denied";

        if ("analyse".equals(action)) {
            Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
            while (it.hasNext()) {
                AnalysisForm analysisForm = it.next();
                SentimentAdapter sentimentAdapter = sentimentFactory.get(analysisForm.getAdapterClass());
                sentimentAdapter.analyze(corpus, new Analysis(analysisForm));
            }
            Map<String, String> flashMsg = new HashMap<>() {{
                put("primary", "Ejecutado/s <strong>" + analysisFormList.getAnalysis().size() + "</strong> análisis de polaridad sobre el corpus.");
            }};
            model.addAttribute("flashMessage", flashMsg);
        }
        return "corpora/add_polarity_analysis";
    }

    @RequestMapping(value = "/delete-analysis", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> deleteAnalysis(@RequestBody Long analysisID, Principal principal) {
        Analysis analysis = analysisService.findOne(analysisID);
        Account account = accountService.findByUserName(principal.getName());

        if (account == null || account != analysis.getCorpus().getOwner())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("forbidden");

        analysisService.delete(analysis);
        analysis.getCorpus().refreshScores();
        corpusService.save(analysis.getCorpus());
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @RequestMapping(value = "/rerun-analysis", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> rerunAnalysis(@RequestBody Long analysisID, Principal principal) {
        Analysis analysis = analysisService.findOne(analysisID);
        Account account = accountService.findByUserName(principal.getName());

        if (account == null || account != analysis.getCorpus().getOwner())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("forbidden");

        //analysisService.clearRecords(analysis);

        if (analysis.getAnalysisType() == ClassifierType.POLARITY) {
            SentimentAdapter sentimentAdapter = sentimentFactory.get(analysis.getAdapterClass());
            sentimentAdapter.analyze(analysis.getCorpus(), analysis);
        }
        else if (analysis.getAnalysisType() == ClassifierType.OPINION) {
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(analysis.getAdapterClass());
            subjectivityAdapter.analyze(analysis.getCorpus(), analysis);
        }

        analysis.getCorpus().refreshScores();
        corpusService.save(analysis.getCorpus());

        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

}
