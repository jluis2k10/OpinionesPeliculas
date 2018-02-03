package es.uned.components;

import es.uned.entities.Search;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Scope("session")
/**
 * "Almacén" de un objeto Search para hacerlo accesible entre controladores.
 *
 * Cuando se realiza una nueva búsqueda, este wrapper se actualiza almacenándola para que
 * si luego se quiere guardar, podamos recuperarla de aquí. El alcance (Scope) de este bean
 * es "session" para que persista durante la sesión del usuario.
 */
public class SearchWrapper {

    private Search search;

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }
}
