package es.uned.components;

import es.uned.entities.LanguageModel;
import es.uned.services.LanguageModelService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convertir una String con el ID a un objeto LanguageModel
 */
@Component
public class IdToLanguageModelConverter implements Converter<String, LanguageModel> {

    private LanguageModelService languageModelService;

    public IdToLanguageModelConverter(LanguageModelService languageModelService) {
        this.languageModelService = languageModelService;
    }

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
