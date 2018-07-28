package es.uned.forms;

import org.apache.commons.collections.FactoryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * En las páginas de presentación del sitio o frontend de la aplicación, se
 * puede elegir la opción de ejecutar varios análisis al mismo tiempo, es decir
 * se generarán tantos formularios {@link es.uned.forms.AnalysisForm} como el
 * usuario requiera de forma dinámica.
 * <p>
 * Esta clase se utiliza para poder modelar este comportamiento en Spring. No
 * es más que un contenedor en forma de lista para varias instancias del objeto
 * {@link es.uned.forms.AnalysisForm}.
 * <p>
 * De este modo el controlador recoge un "formulario con formularios" creados
 * dinámicamente. Cada vez que el usuario decide añadir una nueva ejecución de
 * un análisis en el formulario, se crea una entrada nueva en la lista {@link #analysis}.
 * Si la lista tiene entradas vacías o los índices no son correlativos (puede suceder
 * cuando el usuario corrige/cambia las ejecuciones), se eliminan de ella dichas
 * entradas de forma automática al recuperar su iterable.
 * @see es.uned.forms.ShrinkableList
 */
public class AnalysisFormList {

    private boolean execute;

    private List analysis = ShrinkableList.decorate(new ArrayList(), FactoryUtils.instantiateFactory(AnalysisForm.class));

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public List getAnalysis() {
        return analysis;
    }

    public void setAnalysis(List analysis) {
        this.analysis = analysis;
    }
}
