package es.uned.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.entities.Account;
import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.Search;
import es.uned.forms.AnalysisForm;
import es.uned.forms.AnalysisFormList;
import es.uned.forms.SourceForm;
import es.uned.forms.validators.SourceFormValidator;
import es.uned.services.AccountService;
import es.uned.services.AdapterModelService;
import es.uned.services.CorpusService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
@Controller
@SessionAttributes({"corpus"})
public class MainController {

    @Autowired private SourceAdapterFactory sourceFactory;
    @Autowired private SubjectivityAdapterFactory subjectivityFactory;
    @Autowired private SentimentAdapterFactory sentimentFactory;

    @Autowired private AdapterModelService adapterModelService;
    @Autowired private AccountService accountService;
    @Autowired private CorpusService corpusService;

    @Autowired private SourceFormValidator sourceFormValidator;

    @ModelAttribute("corpus")
    public Corpus setUpCorpus() {
        return new Corpus();
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        Corpus myCorpus = corpusService.findOne(1L);
        Analysis analysis = (Analysis) CollectionUtils.get(myCorpus.getAnalyses(), 0);
        myCorpus.removeAnalysis(analysis);
        corpusService.save(myCorpus);
        //corpusService.delete(myCorpus);
        return "";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        Corpus corpus = new Corpus();
        model.addAttribute("corpus", corpus);
        model.addAttribute("sourceForm", new SourceForm());
        return "home";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String home(@ModelAttribute("sourceForm") SourceForm sourceForm,
                       BindingResult bindingResult, Model model,
                       @RequestParam(value = "action", required = false) String action,
                       @ModelAttribute("corpus") Corpus corpus)
    {
        if (action == null)
            return "home";
        sourceFormValidator.validate(sourceForm, bindingResult);
        if (bindingResult.hasErrors())
            return "home";

        if (action.equals("get-comments")) {
            SourceAdapter sourceAdapter = sourceFactory.get(sourceForm.getSourceAdapter());
            sourceAdapter.setOptions(sourceForm);
            int updatedComments;
            if (corpus.getComments() != null) {
                updatedComments = sourceAdapter.updateCorpus(sourceForm, corpus);
            } else {
                sourceAdapter.generateCorpus(corpus);
                updatedComments = corpus.getComments().size();
            }

            Map<String, String> flashMsg = new HashMap<String, String>(){{
                put("primary", "Añadidos <strong>" + updatedComments + "</strong> mensajes nuevos al Corpus.");
            }};
            model.addAttribute("flashMessage", flashMsg);
            return "home";
        }
        else if (action.equals("save-corpus")) {
            return "results";
        }

        return "redirect:/denied";
    }

    @RequestMapping(value = "/opinion-analysis", method = RequestMethod.POST)
    public String opinionAnalysis(@ModelAttribute("opinionForm") AnalysisFormList analysisFormList,
                                  @RequestParam(value = "action", required = false) String action,
                                  HttpServletRequest request, @ModelAttribute("corpus") Corpus corpus,
                                  Model model)
    {
        if (action == null) {
            model.addAttribute("opinionForm", new AnalysisFormList());
            return "opinion_analysis";
        }
        else if (action.equals("back")) {
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/";
        }
        else if (action.equals("analyse")) {
            Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
            while (it.hasNext()) {
                AnalysisForm analysisForm = it.next();
                SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(analysisForm.getAdapterClass());
                subjectivityAdapter.analyze(corpus, new Analysis(analysisForm));
            }
            return "opinion_analysis";
        }
        else if (action.equals("next")) {
            if (analysisFormList.isExecute()) {
                Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
                while (it.hasNext()) {
                    AnalysisForm analysisForm = it.next();
                    SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(analysisForm.getAdapterClass());
                    subjectivityAdapter.analyze(corpus, new Analysis(analysisForm));
                }
            }
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/polarity-analysis";
        }
        return "redirect:/denied";
    }

    @RequestMapping(value = "/polarity-analysis", method = RequestMethod.POST)
    public String polarityAnalysis(@ModelAttribute("polarityForm") AnalysisFormList analysisFormList,
                                   @RequestParam(value = "action", required = false) String action,
                                   @ModelAttribute("corpus") Corpus corpus, HttpServletRequest request,
                                   Model model) {
        if (action == null) {
            model.addAttribute("polarityForm", new AnalysisFormList());
            return "polarity_analysis";
        }
        else if (action.equals("back")) {
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/opinion-analysis";
        }
        else if (action.equals("analyse")) {
            Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
            while (it.hasNext()) {
                AnalysisForm analysisForm = it.next();
                SentimentAdapter sentimentAdapter= sentimentFactory.get(analysisForm.getAdapterClass());
                sentimentAdapter.analyze(corpus, new Analysis(analysisForm));
            }
            return "polarity_analysis";
        }
        else if (action.equals("next")) {
            if (analysisFormList.isExecute()) {
                Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
                while (it.hasNext()) {
                    AnalysisForm analysisForm = it.next();
                    SentimentAdapter sentimentAdapter= sentimentFactory.get(analysisForm.getAdapterClass());
                    sentimentAdapter.analyze(corpus, new Analysis(analysisForm));
                }
            }
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/analysis-results";
        }
        return "redirect:/denied";
    }

    @RequestMapping(value = "/analysis-results", method = RequestMethod.POST)
    public String analysisResults(@ModelAttribute("corpus") Corpus corpus,
                                  @RequestParam(value = "action", required = false) String action,
                                  HttpServletRequest request, Model model)
    {
        if (action == null)
            return "analysis_results";
        else if (action.equals("back")) {
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/polarity-analysis";
        }
        return "analysis_results";
    }

    /*@RequestMapping(value = "/get-analyses", method = RequestMethod.POST)
    public ResponseEntity<ArrayNode> getAnalyses(@ModelAttribute("corpus") Corpus corpus) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode response = mapper.createArrayNode();
        if (corpus.getAnalyses() != null) {
            corpus.getAnalyses().forEach(analysis ->
                response.add(analysis.toJson(true))
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/get-corpus-comment-hashes", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> getCorpusCommentHashes(@ModelAttribute("corpus") Corpus corpus) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ArrayNode hashesArray = mapper.createArrayNode();
        LinkedList<Comment> commentsList = new LinkedList<>(corpus.getComments());
        Collections.sort(commentsList);
        commentsList.forEach(comment ->
            hashesArray.add(comment.getHash())
        );
        response.set("hashes", hashesArray);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }*/

    @RequestMapping(value = "/save-corpus", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> saveCorpus(
            SessionStatus sessionStatus,
            @ModelAttribute("corpus") Corpus corpus, Principal principal,
            @RequestParam(value = "corpus-title", required = false) String title,
            @RequestParam(value = "corpus-description", required = false) String description,
            @RequestParam(value = "corpus-public", required = false) boolean isPublic)
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        Account account = accountService.findByUserName(principal.getName());

        if (title.trim().length() < 5) {
            response.put("status", "error");
            response.put("message", "El título del Corpus debe ser de al menos 5 caracteres alfanuméricos.");
        }
        else {
            corpus.setName(title.trim());
            corpus.setDescription(description);
            corpus.setPublic(isPublic);
            corpus.setOwner(account);
            corpusService.save(corpus);

            /* "Hack". Limpiamos la sesión y volvemos a meter en ella el corpus
            recién guardado. De este modo hibernate no lo marca como "detached" y
            podemos seguir actualizando el corpus y guardar los cambios posteriormente. */
            //sessionStatus.setComplete();

            response.put("status", "success");
            response.put("message", "Corpus guardado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/results", method = RequestMethod.POST)
    public String results(Model model, @ModelAttribute("searchForm") Search search,
                       BindingResult searchFormErrors, HttpServletRequest request) {
        search.makeExtraParams(request.getParameterMap());
        // TODO: hay que comprobar antes que el formulario contiene un id o un id válido
        search.setSentimentModel(adapterModelService.findOne(search.getSentimentModel().getId()));
        if (search.isClassifySubjectivity())
            search.setSubjectivityModel(adapterModelService.findOne(search.getSubjectivityModel().getId()));
        SourceAdapter sourceAdapter = sourceFactory.get(search.getSourceClass());
        // (quitado tras cambio en interface sourceAdapter) sourceAdapter.doSearch(search);

        if (search.isClassifySubjectivity()) {
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(search.getSubjectivityAdapter());
            //subjectivityAdapter.analyze(search);
        }

        SentimentAdapter sentimentAdapter = sentimentFactory.get(search.getSentimentAdapter());
        //sentimentAdapter.analyze(search);

        //searchWrapper.setSearch(search);

        model.addAttribute("search", search);
        return "results";
    }
}
