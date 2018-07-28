package es.uned.forms;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import java.util.Iterator;
import java.util.List;

/**
 * LazyList que se "encoje" cuando instanciamos su Iterator para recorrerla, eliminando
 * entradas vacías.
 * <p>
 * De este modo podemos tener formularios dinámicos sin preocuparnos de los índices al
 * añadir/eliminar campos en el formulario.
 */
public class ShrinkableList extends LazyList {

    protected ShrinkableList(List list, Factory factory) {
        super(list, factory);
    }

    public static List decorate(List list, Factory factory) {
        return new ShrinkableList(list, factory);
    }

    /**
     * Eliminar entradas vacías de la lista.
     */
    public void shrink() {
        for (Iterator it = getList().iterator(); it.hasNext();)
            if (it.next() == null)
                it.remove();
    }

    /**
     * Antes de devolver el iterable, "encogemos" la lista elimininda las entradas vacías.
     * @return objeto iterable de la lista
     */
    @Override
    public Iterator iterator() {
        shrink();
        return super.iterator();
    }

}
