package es.uned.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.adapters.DomainAdapterFactory;
import es.uned.adapters.SentimentAdapterFactory;
import es.uned.adapters.SourceAdapterFactory;
import es.uned.adapters.SubjectivityAdapterFactory;
import es.uned.adapters.domain.DomainAdapter;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.entities.Account;
import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.forms.AnalysisForm;
import es.uned.forms.AnalysisFormList;
import es.uned.forms.SourceForm;
import es.uned.forms.validators.SourceFormValidator;
import es.uned.services.AccountService;
import es.uned.services.CorpusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Controlador "raíz" para el proceso de crear un nuevo corpus:
 *   1. Recuperar comentarios de diversas fuentes
 *   2. Ejecutar análisis de opinión
 *   3. Ejecutar análisis de polaridad
 *   4. Mostrar resultados
 *
 * Permite avanzar/retroceder entre pasos y guardar los resultados.
 * Guardamos un Objeto Corpus en la sesión para facilitar el proceso de avanzar/retroceder entre fases
 * añadiendo nuevos elementos al Corpus a voluntad.
 */
@Controller
@SessionAttributes({"corpus"})
public class MainController {

    @Autowired private SourceAdapterFactory sourceFactory;
    @Autowired private SubjectivityAdapterFactory subjectivityFactory;
    @Autowired private SentimentAdapterFactory sentimentFactory;
    @Autowired private DomainAdapterFactory domainFactory;
    @Autowired private AccountService accountService;
    @Autowired private CorpusService corpusService;
    @Autowired private SourceFormValidator sourceFormValidator;

    /**
     * Devuelve nuevo objeto Corpus en caso de que Spring no lo encuentre en la sesión actual
     * @return nuevo Corpus
     */
    @ModelAttribute("corpus")
    public Corpus setUpCorpus() {
        return new Corpus();
    }

    /**
     * Página de la portada. Presenta un formulario para recuperar comentarios de las diferentes
     * fuentes disponibles.
     * @param model Contenedor de datos para la vista
     * @return Página JSP
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        Corpus corpus = new Corpus();
        model.addAttribute("corpus", corpus);
        model.addAttribute("sourceForm", new SourceForm());
        return "home";
    }

    /**
     * 1ª fase: recuperar comentarios.
     * Recoge el formulario POST de recuperar comentarios para añadirlos al corpus.
     * @param sourceForm    Formulario con las opciones seleccionadas para una fuente de comentarios
     * @param bindingResult Contenedor de errores en el formulario sourceForm
     * @param model         Contenedor de datos para la vista
     * @param action        Parámetro "action" de la URL
     * @param corpus        Entidad Corpus sobre la que se está trabajando
     * @return Página JSP
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String home(@ModelAttribute("sourceForm") SourceForm sourceForm,
                       BindingResult bindingResult, Model model,
                       @RequestParam(value = "action", required = false) String action,
                       @ModelAttribute("corpus") Corpus corpus)
    {
        // Acceso desde la fase de ejecutar análisis de opinión
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

            Map<String, String> flashMsg = new HashMap<>(){{
                put("primary", "Añadidos <strong>" + updatedComments + "</strong> comentarios nuevos al Corpus.");
            }};
            model.addAttribute("flashMessage", flashMsg);
            return "home";
        }

        return "redirect:/denied";
    }

    /**
     * 2ª fase: ejecutar análisis de dominio.
     * Presenta el formulario mediante el cual se eligen las opciones para ejecutar un nuevo o
     * nuevos análisis de dominio o recoge el POST de dicho formulario.
     * @param analysisFormList  Lista de formulario/s con información sobre los análisis a ejecutar
     * @param action            Parámetro "action" de la URL
     * @param request           Información sobre la petición HTTP a este controlador
     * @param corpus            Entidad Corpus sobre la que se está trabajando
     * @param model             Contenedor de datos para la vista
     * @return Página JSP o redirección a otra fase
     */
    @RequestMapping(value = "/domain-analysis", method = RequestMethod.POST)
    public String domainAnalysis(@ModelAttribute("domainForm") AnalysisFormList analysisFormList,
                                 @RequestParam(value = "action", required = false) String action,
                                 HttpServletRequest request, @ModelAttribute("corpus") Corpus corpus,
                                 Model model)
    {
        // Acceso desde la fase de recuperar comentarios o desde la fase de ejecutar análisis de opinión
        if (action == null) {
            model.addAttribute("domainForm", new AnalysisFormList());
            return "domain_analysis";
        }
        // Se ha clickado el botón de retroceder, volvemos a la portada
        else if (action.equals("back")) {
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/";
        }
        if (analysisFormList.isExecute()) {
            AnalysisForm analysisForm = analysisFormList.getFirst();
            DomainAdapter domainAdapter = domainFactory.get(analysisForm.getAdapterClass());
            domainAdapter.analyze(corpus, new Analysis(analysisForm));
        }
        // Ejecutar análisis seleccionados sin avanzar a la fase de análisis de polaridad
        if (action.equals("analyse")) {
            return "domain_analysis";
        }
        // Ejecutar análisis seleccionados y avanzar a la fase de análisis de polaridad
        else if (action.equals("next")) {
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/opinion-analysis";
        }
        return "redirect:/denied";
    }

    /**
     * 3ª fase: ejecutar análisis de opinión.
     * Presenta el formulario mediante el cual se eligen las opciones para ejecutar un nuevo o
     * nuevos análisis de opinión o recoge el POST de dicho formulario.
     * @param analysisFormList Lista de formulario/s con información sobre los análisis a ejecutar
     * @param action           Parámetro "action" de la URL
     * @param request          Información sobre la petición HTTP a este controlador
     * @param corpus           Entidad Corpus sobre la que se está trabajando
     * @param model            Contenedor de datos para la vista
     * @return Página JSP o redirección a otra fase
     */
    @RequestMapping(value = "/opinion-analysis", method = RequestMethod.POST)
    public String opinionAnalysis(@ModelAttribute("opinionForm") AnalysisFormList analysisFormList,
                                  @RequestParam(value = "action", required = false) String action,
                                  HttpServletRequest request, @ModelAttribute("corpus") Corpus corpus,
                                  Model model)
    {
        // Acceso desde la fase de recuperar comentarios o desde la fase de ejecutar análisis de dominio o desde
        // la fase de ejecutar análisis de polaridad
        if (action == null) {
            model.addAttribute("opinionForm", new AnalysisFormList());
            return "opinion_analysis";
        }
        // Se ha clickado en el botón de retroceder, volvemos a la fase de análisis de dominio
        else if (action.equals("back")) {
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/domain-analysis";
        }
        // Ejecutar análisis seleccionados sin avanzar a la fase de análisis de polaridad
        else if (action.equals("analyse")) {
            Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
            while (it.hasNext()) {
                AnalysisForm analysisForm = it.next();
                SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(analysisForm.getAdapterClass());
                subjectivityAdapter.analyze(corpus, new Analysis(analysisForm));
            }
            return "opinion_analysis";
        }
        // Ejecutar análisis seleccionados y avanzar a la fase de análisis de polaridad
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

    /**
     * 4ª fase: ejecutar análisis de polaridad.
     * Presenta el formulario mediante el cual se eligen las opciones para ejecutar un nuevo o
     * nuevos análisis de polaridad o recoge el POST de dicho formulario.
     * @param analysisFormList Lista de formulario/s con información sobre los análisis a ejecutar
     * @param action           Parámetro "action" de la URL
     * @param corpus           Entidad Corpus sobre la que se está trabajando
     * @param request          Información sobre la petición HTTP a este controlador
     * @param model            Contenedor de datos para la vista
     * @return Página JSP o redirección a otra fase
     */
    @RequestMapping(value = "/polarity-analysis", method = RequestMethod.POST)
    public String polarityAnalysis(@ModelAttribute("polarityForm") AnalysisFormList analysisFormList,
                                   @RequestParam(value = "action", required = false) String action,
                                   @ModelAttribute("corpus") Corpus corpus, HttpServletRequest request,
                                   Model model) {
        // Acceso desde la fase de ejecutar análisis de opinión
        if (action == null) {
            model.addAttribute("polarityForm", new AnalysisFormList());
            return "polarity_analysis";
        }
        // Se ha clickado en el botón de retroceder, volvemos a la fase de análisis de opinión
        else if (action.equals("back")) {
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/opinion-analysis";
        }
        // Ejecutar análisis seleccionados y permanecer en esta fase
        else if (action.equals("analyse")) {
            Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
            while (it.hasNext()) {
                AnalysisForm analysisForm = it.next();
                SentimentAdapter sentimentAdapter= sentimentFactory.get(analysisForm.getAdapterClass());
                sentimentAdapter.analyze(corpus, new Analysis(analysisForm));
            }
            return "polarity_analysis";
        }
        // Ejecutar análisis seleccionados y mostrar comentarios
        else if (action.equals("next")) {
            if (analysisFormList.isExecute()) {
                Iterator<AnalysisForm> it = analysisFormList.getAnalysis().iterator();
                while (it.hasNext()) {
                    AnalysisForm analysisForm = it.next();
                    SentimentAdapter sentimentAdapter = sentimentFactory.get(analysisForm.getAdapterClass());
                    sentimentAdapter.analyze(corpus, new Analysis(analysisForm));
                }
            }
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/analysis-results";
        }
        return "redirect:/denied";
    }

    /**
     * Mostrar Corpus generado durante las fases anteriores
     * @param corpus  Entidad Corpus sobre la que se está trabajando
     * @param action  Parámetro "action" de la URL
     * @param request Información sobre la petición HTTP a este controlador
     * @return Página JSP o redirección a otra fase
     */
    @RequestMapping(value = "/analysis-results", method = RequestMethod.POST)
    public String analysisResults(@ModelAttribute("corpus") Corpus corpus,
                                  @RequestParam(value = "action", required = false) String action,
                                  HttpServletRequest request)
    {
        // Se accede desde la fase de ejecutar análisis de polaridad
        if (action == null)
            return "analysis_results";
        // Se ha clickado el botón de retroceder, volvemos a la fase de ejecutar análisis de polaridad
        else if (action.equals("back")) {
            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return "redirect:/polarity-analysis";
        }
        return "analysis_results";
    }


    /**
     * Petición de guardar un nuevo Corpus. Se recoge la información desde un formulario presentado
     * en una ventana modal y del propio corpus generado durante las fases anteriores almacenado en
     * la sesión HTTP.
     * @param corpus      Entidad Corpus sobre la que se está trabajando
     * @param principal   Token de autenticación del usuario
     * @param title       Título del Corpus
     * @param description Descripción del Corpus
     * @param isPublic    Parámetro isPublic del Corpus
     * @return respuesta HTTP a la petición con información sobre el resultado de la operación de guardado
     */
    @RequestMapping(value = "/save-corpus", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> saveCorpus(
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
            response.put("status", "success");
            response.put("message", "Corpus guardado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
