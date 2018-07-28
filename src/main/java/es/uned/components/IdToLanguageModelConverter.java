package es.uned.components;

import es.uned.entities.LanguageModel;
import es.uned.services.LanguageModelService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

/**
 * Convertir una String con el ID a un objeto LanguageModel.
 * @see es.uned.config.WebMvcConfig#addFormatters(FormatterRegistry)
 *
 * En formularios como {@link es.uned.forms.AnalysisForm} se utilizan objetos LanguageModel
 * cuando en las vistas se seleccionan mediante su ID. Con este convertidor, Spring se
 * encarga de transformar el String con el ID seleccionado en un formulario en el objeto
 * concreto que lo representa.
 */
@Component
public class IdToLanguageModelConverter implements Converter<String, LanguageModel> {

    private LanguageModelService languageModelService;

    public IdToLanguageModelConverter(LanguageModelService languageModelService) {
        this.languageModelService = languageModelService;
    }

    /**
     * Devuelve objeto LanguageModel a partir de un String con su identificador.
     * @param id Identificador del LanguageModel en la BBDD
     * @return LanguageModel
     */
    @Override
    public LanguageModel convert(String id) {
        try {
            Long languageModelID = Long.valueOf(id);
            return languageModelService.findOne(languageModelID);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

}
