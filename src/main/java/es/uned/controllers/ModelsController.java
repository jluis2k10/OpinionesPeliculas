package es.uned.controllers;

import es.uned.adapters.*;
import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.adapters.sources.SourceAdapter;
import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.entities.Account;
import es.uned.entities.Corpus;
import es.uned.entities.LanguageModel;
import es.uned.forms.CreateLanguageModelForm;
import es.uned.forms.TrainModelForm;
import es.uned.services.AccountService;
import es.uned.services.AnalysisService;
import es.uned.services.LanguageModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador para manejar los modelos de lenguaje.
 */
@Controller
@RequestMapping(value = "/models")
public class ModelsController {

    @Autowired private SourceAdapterFactory sourceFactory;
    @Autowired private SubjectivityAdapterFactory subjectivityFactory;
    @Autowired private SentimentAdapterFactory sentimentFactory;
    @Autowired private DomainAdapterFactory domainFactory;
    @Autowired private LanguageModelService languageModelService;
    @Autowired private AnalysisService analysisService;
    @Autowired private AccountService accountService;

    /**
     * Muestra el listado con los modelos de lenguaje disponibles
     * @param model     Contenedor de datos para la vista
     * @param principal Token de autenticación del usuario
     * @return Página JSP
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String myModels(Model model, Principal principal) {
        if (principal != null) {
            Account account = accountService.findByUserName(principal.getName());
            Set<LanguageModel> userPolarityModels = languageModelService.findUserModels(account, ClassifierType.POLARITY);
            Set<LanguageModel> userOpinionModels = languageModelService.findUserModels(account, ClassifierType.OPINION);
            Set<LanguageModel> userDomainModels = languageModelService.findUserModels(account, ClassifierType.DOMAIN);
            model.addAttribute("polarityModels", userPolarityModels);
            model.addAttribute("opinionModels", userOpinionModels);
            model.addAttribute("domainModels", userDomainModels);
            if (account.isAdmin()) {
                Set<LanguageModel> allPolarityModels = languageModelService.findFromOthers(account, ClassifierType.POLARITY);
                Set<LanguageModel> allOpinionModels = languageModelService.findFromOthers(account, ClassifierType.OPINION);
                Set<LanguageModel> allDomainModels = languageModelService.findFromOthers(account, ClassifierType.DOMAIN);
                model.addAttribute("allPolarityModels", allPolarityModels);
                model.addAttribute("allOpinionModels", allOpinionModels);
                model.addAttribute("allDomainModels", allDomainModels);
            }
        }
        return "models/my_models";
    }

    /**
     * Presenta el formulario para crear un nuevo modelo de lenguaje
     * @param model Contenedor con datos para la vista
     * @return Página JSP
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("modelForm", new CreateLanguageModelForm());
        return "models/create_model";
    }

    /**
     * Recoge el formulario POST de crear un nuevo modelo de lenguaje.
     * El formulario es dinámico, así que no podemos acoplarlo directamente a una clase y debemos
     * recoger todos los parámetros POST enviados para procesarlos convenientemente.
     * @param createModelForm Formulario con las opciones "estándar" seleccionadas
     * @param servletRequest  Datos POST introducidos en la parte dinámica del formulario
     * @param principal       Token de autenticación del usuario
     * @return Página JSP
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@ModelAttribute("modelForm") CreateLanguageModelForm createModelForm,
                         HttpServletRequest servletRequest, Principal principal) {
        Map<String,String> modelParameters = createModelForm.getModelParameters(servletRequest.getParameterMap());

        Account account = accountService.findByUserName(principal.getName());
        LanguageModel languageModel = createModelForm.generateLanguageModel();
        languageModel.setOwner(account);

        if (createModelForm.getClassifierType() == ClassifierType.POLARITY) {
            SentimentAdapter adapter = sentimentFactory.get(languageModel.getAdapterClass());
            adapter.createModel(languageModel.getLocation(), modelParameters, createModelForm.buildDatasets());
        }
        else {
            SubjectivityAdapter adapter = subjectivityFactory.get(languageModel.getAdapterClass());
            adapter.createModel(languageModel.getLocation(), modelParameters, createModelForm.buildDatasets());
        }

        languageModelService.save(languageModel);
        return "models/create_model";
    }

    /**
     * Presenta el formulario que se utiliza para introducir los parámetros necesarios para entrenar
     * un modelo de lenguaje determinado.
     * @param model     Contenedor de datos para la vista
     * @param principal Token de autenticación del usuario
     * @param modelID   ID del modelo de lenguaje a entrenar
     * @return Página JSP
     */
    @RequestMapping(value = "train/{id}", method = RequestMethod.GET)
    public String train(Model model, Principal principal, @PathVariable("id") Long modelID) {
        LanguageModel languageModel = languageModelService.findOne(modelID);
        Account account = accountService.findByUserName(principal.getName());
        if (!languageModel.isTrainable() || (!account.isAdmin() && !languageModel.getOwner().equals(account))) {
            return "redirect:/denied";
        }
        TrainModelForm trainForm = new TrainModelForm(languageModel);
        model.addAttribute("trainForm", trainForm);
        return "models/train_model";
    }

    /**
     * Recoge el formulario POST de entrenar un modelo de lenguaje.
     * Si la fuente de comentarios para entrenar el modelo de lenguaje no es directamente un dataset en
     * forma de archivo o de texto, primero se recuperarán los comentarios de dicha fuente y luego se
     * presentará la página con la interfaz necesaria para que el usuario decida la clasificación de cada
     * uno de los comentarios recuperados.
     * @param model           Contenedor con datos para la vista
     * @param modelId         ID del modelo de lenguaje
     * @param trainForm       Formulario con las opciones seleccionadas
     * @param trainFormErrors Contenedor de errores en el formulario
     * @return Página JSP
     */
    @RequestMapping(value = "train/{id}", method = RequestMethod.POST)
    public String train(Model model, @PathVariable("id") Long modelId,
                        @ModelAttribute("trainForm") TrainModelForm trainForm, BindingResult trainFormErrors) {
        if (trainForm.getSourceClass().equals("es.uned.adapters.sources.Dataset")
                || trainForm.getSourceClass().equals("TextDataset")) {
            Map<Enum, List<String>> datasets = trainForm.buildDatasets();
            if (trainForm.getClassifierType() == ClassifierType.POLARITY) {
                SentimentAdapter sentimentAdapter = sentimentFactory.get(trainForm.getAdapterClass());
                sentimentAdapter.trainModel(trainForm.getModelLocation(), datasets);
            } else {
                SubjectivityAdapter subjectivityAdapter = subjectivityFactory.get(trainForm.getAdapterClass());
                subjectivityAdapter.trainModel(trainForm.getModelLocation(), datasets);
            }
        }
        // La fuente no es un dataset, recuperar comentarios y presentar la página para clasificar manualmente
        // estos comentarios
        else {
            SourceAdapter sourceAdapter = sourceFactory.get(trainForm.getSourceClass());
            sourceAdapter.setOptions(trainForm);
            Corpus corpus = new Corpus();
            sourceAdapter.generateCorpus(corpus);
            model.addAttribute("trainForm", trainForm);
            model.addAttribute("corpus", corpus);
            return "models/train_from_comments";
        }

        return "models/my_models";
    }

    /**
     * Atiende a la petición de hacer público/privado un modelo de lenguaje
     * @param modelID   ID del modelo de lenguaje
     * @param principal Token de autenticación del usuario
     * @return Respuesta HTTP a la petición
     */
    @RequestMapping(value = "/switchModelOpen", method = RequestMethod.POST)
    public ResponseEntity<String> switchModelOpen(@RequestBody Long modelID, Principal principal) {
        LanguageModel model = languageModelService.findOne(modelID);
        if (principal == null || model == null)
            return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
        Account account = accountService.findByUserName(principal.getName());
        if (model.getOwner() == account || account.isAdmin()) {
            model.setPublic(!model.isPublic());
            languageModelService.save(model);
            return new ResponseEntity<>("Ok", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }

    /**
     * Atiende a la petición de eliminar un modelo de lenguaje
     * @param modelID   ID del modelo de lenguaje
     * @param principal Toekn de autenticación del usuario
     * @return Respuesta HTTP a la petición
     */
    @RequestMapping(value = "/deleteModel", method = RequestMethod.POST)
    public ResponseEntity<String> deleteModel(@RequestBody Long modelID, Principal principal) {
        LanguageModel model = languageModelService.findOne(modelID);
        if (principal == null || model == null)
            return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
        Account account = accountService.findByUserName(principal.getName());
        if (model.getOwner() == account || account.isAdmin()) {
            String adapterPath = null;
            if (model.getClassifierType() == ClassifierType.POLARITY)
                adapterPath = sentimentFactory.get(model.getAdapterClass()).get_adapter_path();
            else if (model.getClassifierType() == ClassifierType.OPINION)
                adapterPath = subjectivityFactory.get(model.getAdapterClass()).get_adapter_path();
            else if (model.getClassifierType() == ClassifierType.DOMAIN)
                adapterPath = domainFactory.get(model.getAdapterClass()).get_adapter_path();
            if (languageModelService.delete(adapterPath, model))
                return new ResponseEntity<>("Ok", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }

    /**
     * Devuelve el número de análisis en los que se utiliza el modelo de lenguaje indicado
     * @param modelID   ID del modelo de lenguaje
     * @param principal Token de autenticación del usuario
     * @return Respuesta HTTP a la petición con información sobre el número de análisis
     */
    @RequestMapping(value = "/countAnalysis", method = RequestMethod.POST)
    public ResponseEntity<String> countAnalysis(@RequestBody Long modelID, Principal principal) {
        LanguageModel model = languageModelService.findOne(modelID);
        if (principal == null || model == null)
            return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
        Account account = accountService.findByUserName(principal.getName());
        if (model.getOwner() == account || account.isAdmin()) {
            int totalAnalysis = analysisService.countByLanguageModel(model);
            return new ResponseEntity<>(String.valueOf(totalAnalysis), HttpStatus.OK);
        }
        return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
}
