package es.uned.components;

import es.uned.entities.SearchParams;
import es.uned.entities.TrainParams;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class Train2SearchConverter {

    public SearchParams convert(TrainParams trainParams) {
        SearchParams searchParams = new SearchParams();

        searchParams.setSearchTerm(trainParams.getSearchTerm());
        searchParams.setSourceClass(trainParams.getSourceClass());
        searchParams.setLimit(trainParams.getLimit());
        searchParams.setSinceDate(trainParams.getSinceDate());
        searchParams.setUntilDate(trainParams.getUntilDate());
        searchParams.setLang(trainParams.getLang());

        return searchParams;
    }

}
