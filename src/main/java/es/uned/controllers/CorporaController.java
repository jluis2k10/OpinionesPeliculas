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
import es.uned.forms.AnalysisForm;
import es.uned.forms.AnalysisFormList;
import es.uned.forms.EditCorpusForm;
import es.uned.forms.SourceForm;
import es.uned.forms.validators.SourceFormValidator;
import es.uned.services.AccountService;
import es.uned.services.AnalysisService;
import es.uned.services.CorpusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Controlador para mostrar y manejar los corpus generados
 */
@Controller
@SessionAttributes({"corpus"})
@RequestMapping(value = "/corpora")
public class CorporaController {

    @Autowired private CorpusService corpusService;
    @Autowired private AnalysisService analysisService;
    @Autowired private AccountService accountService;
    @Autowired private SentimentAdapterFactory sentimentFactory;
    @Autowired private SubjectivityAdapterFactory subjectivityFactory;
    @Autowired private SourceAdapterFactory sourceFactory;
    @Autowired private SourceFormValidator formValidator;

    /**
     * Muestra todos los corpus generados por el usuario
     * @return Página JSP
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String myCorpora() {
        return "corpora/view_corpora";
    }

    /**
     * Muestra información de un corpus concreto
     * @param corpusID  ID del corpus a mostrar
     * @param principal Token de autenticación del usuario
     * @param model     Contenedor de datos para la vista
     * @return Página JSP
     */
    @RequestMapping(value = "/view/{corpusID}", method = RequestMethod.GET)
    public String viewCorpus(@PathVariable("corpusID") Long corpusID, Principal principal, Model model) {
        Corpus corpus = corpusService.findOne(corpusID);
        Account account = accountService.findByUserName(principal.getName());
        if (account == null || account != corpus.getOwner())
            return "redirect:/denied";
        model.addAttribute("corpus", corpus);
        return "corpora/view_corpus";
    }

    /**
     * Presenta formulario para editar un corpus
     * @param corpusID  ID del corpus a editar
     * @param principal Token de autenticación del usuario
     * @param model     Contenedor de datos para la vista
     * @return Página JSP
     */
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

    /**
     * Recoge el POST del formulario de editar corpus
     * @param corpusForm Datos introducidos en el formulario
     * @param principal  Token de autenticación del usuario
     * @return Página JSP
     */
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

    /**
     * Presenta el formulario para añadir más comentarios al corpus
     * @param corpusID  ID del corpus sobre el que se añadirán nuevos comentarios
     * @param principal Token de autenticación del usuario
     * @param model     Contenedor de datos para la vista
     * @return Página JSP
     */
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

    /**
     * Recoge el formulario POST de añadir nuevos comentarios a un corpus
     * @param corpus        Corpus al cual se le están añadiendo nuevos comentarios
     * @param sourceForm    Datos introducidos en el formulario
     * @param bindingResult Contenedor de errores en el formulario sourceForm
     * @param model         Contenedor de datos para la vista
     * @param principal     Token de autenticación del usuario
     * @return Página JSP o redirección
     */
    @RequestMapping(value = "/add-comments/{corpusID}", method = RequestMethod.POST)
    public String addComments(@ModelAttribute("corpus") Corpus corpus, @ModelAttribute("sourceForm") SourceForm sourceForm,
                              BindingResult bindingResult, Model model, Principal principal) {
        Account account = accountService.findByUserName(principal.getName());
        if (!corpus.getOwner().equals(account))
            return "redirect:/denied";

        formValidator.validate(sourceForm, bindingResult);
        if (bindingResult.hasErrors())
            return "corpora/add_comments";

        SourceAdapter sourceAdapter = sourceFactory.get(sourceForm.getSourceAdapter());
        sourceAdapter.setOptions(sourceForm);
        int newComments = sourceAdapter.updateCorpus(sourceForm, corpus);
        Map<String, String> flashMsg = new HashMap<>(){{
            put("primary", "Añadidos <strong>" + newComments + "</strong> comentarios nuevos al Corpus.");
        }};
        model.addAttribute("flashMessage", flashMsg);
        return "corpora/add_comments";
    }

    /**
     * Formulario para ejecutar nuevos análisis de opinión sobre el corpus
     * @param corpusID  ID del corpus
     * @param principal Token de autenticación del usuario
     * @param model     Contenedor de datos para la vista
     * @return Página JSP
     */
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

    /**
     * Recoge el formulario POST de ejecutar nuevos análisis de opinión
     * @param corpus           Corpus sobre el que se deben ejecutar los análisis
     * @param model            Contenedor de datos para la vista
     * @param principal        Token de autenticación del usuario
     * @param analysisFormList Lista de formulario/s con información sobre los análisis a ejecutar
     * @return Página JSP
     */
    @RequestMapping(value = "/add-opinion-analysis/{corpusID}", method = RequestMethod.POST)
    public String addOpinionAnalysis(@ModelAttribute("corpus") Corpus corpus, Model model, Principal principal,
                                     @ModelAttribute("opinionForm") AnalysisFormList analysisFormList) {
        Account account = accountService.findByUserName(principal.getName());
        if (!corpus.getOwner().equals(account))
            return "redirect:/denied";


        Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
        while (it.hasNext()) {
            AnalysisForm analysisForm = it.next();
            SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(analysisForm.getAdapterClass());
            subjectivityAdapter.analyze(corpus, new Analysis(analysisForm));
        }
        Map<String, String> flashMsg = new HashMap<>(){{
            put("primary", "Ejecutado/s <strong>" + analysisFormList.getAnalysis().size() + "</strong> análisis de opinión sobre el corpus.");
        }};
        model.addAttribute("flashMessage", flashMsg);
        return "corpora/add_opinion_analysis";
    }

    /**
     * Formulario para ejecutar nuevos análisis de polaridad sobre el corpus
     * @param corpusID  ID del corpus
     * @param principal Token de autenticación del usuario
     * @param model     Contenedor de datos para la vista
     * @return Página JSP
     */
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

    /**
     * Recoge el formulario POST de ejecutar nuevos análisis de polaridad
     * @param corpus           Corpus sobre el que se deben ejecutar los análisis
     * @param model            Contenedor de datos para la vista
     * @param principal        Token de autenticación del usuario
     * @param analysisFormList Lista de formulario/s con datos sobre los análisis a ejecutar
     * @return Página JSP
     */
    @RequestMapping(value = "/add-polarity-analysis/{corpusID}", method = RequestMethod.POST)
    public String addPolarityAnalysis(@ModelAttribute("corpus") Corpus corpus, Model model, Principal principal,
                                      @ModelAttribute("polarityForm") AnalysisFormList analysisFormList) {
        Account account = accountService.findByUserName(principal.getName());
        if (!corpus.getOwner().equals(account))
            return "redirect:/denied";

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
        return "corpora/add_polarity_analysis";
    }

    /**
     * Devuelve una entidad Corpus en formato JSON
     * @param principal Token de autenticación del usuario
     * @param corpusID  ID del corpus a devolver en formato JSON
     * @return Respuesta HTTP con el string del objeto JSON
     */
    @Transactional
    @RequestMapping(value = "/get-corpus", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> getCorpus(Principal principal, @RequestParam("id") Long corpusID) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        Corpus corpus = corpusService.findOne(corpusID);
        Account account = accountService.findByUserName(principal.getName());

        if (account == null || account != corpus.getOwner())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        response = corpus.toJson(true, false, false);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Devuelve un listado en formato JSON de todas las entidades Analysis ejecutadas sobre un corpus
     * determinado
     * @param principal Token de autenticación del usuario
     * @param corpusID  ID del corpus
     * @return Respuesta HTTP con el string del objeto JSON
     */
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

    /**
     * Cambia la propiedad "isPublic" de un corpus determinado
     * @param corpusID  ID del corpus
     * @param principal Token de autenticación del usuario
     * @return Respuesta HTTP a la petición
     */
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

    /**
     * Atiende petición de eliminar un corpus
     * @param corpusID  ID del corpus
     * @param principal Token de autenticación del usuario
     * @return Respuesta HTTP a la petición
     */
    @RequestMapping(value = "/delete-corpus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> deleteCorpus(@RequestBody Long corpusID, Principal principal) {
        Corpus corpus = corpusService.findOne(corpusID);
        Account account = accountService.findByUserName(principal.getName());

        if (corpus == null || account == null || account != corpus.getOwner())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("forbidden");

        corpusService.delete(corpus);
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    /**
     * Atiende a la petición de eliminar un análisis ejecutado sobre un corpus
     * @param analysisID ID del análisis
     * @param principal  Token de autenticación del usuario
     * @return Respuesta HTTP a la petición
     */
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

    /**
     * Atiende a la petición de re-ejecutar un análisis ya ejecutado sobre un corpus y guarda
     * el resultado
     * @param analysisID ID del análisis
     * @param principal  Token de autenticación del usuario
     * @return Respuesta HTTP a la petición
     */
    @RequestMapping(value = "/rerun-analysis", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> rerunAnalysis(@RequestBody Long analysisID, Principal principal) {
        Analysis analysis = analysisService.findOne(analysisID);
        Account account = accountService.findByUserName(principal.getName());

        if (account == null || account != analysis.getCorpus().getOwner())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("forbidden");

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
