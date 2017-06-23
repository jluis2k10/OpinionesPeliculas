package es.uned.config;

import es.uned.adapters.SourceAdapterFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Configuración del contexto de la aplicación Spring.
 */
@Configuration
@PropertySources({
        @PropertySource(value = "classpath:application.properties"),
        @PropertySource(value = "classpath:commentsSource.properties")})
@ComponentScan(value = "es.uned.*")
public class ApplicationContextConfig {

    @Bean(name = "viewResolver")
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    /*
     Registramos las interfaces de las fábricas como beans que "localizan servicios".
     Es un modo de implementar el patrón factory aprovechando las ventajas de la inyección
     de dependencias.
     */
    @Bean
    public ServiceLocatorFactoryBean svcLocSourceAdapterFactory() {
        ServiceLocatorFactoryBean svcLoc = new ServiceLocatorFactoryBean();
        svcLoc.setServiceLocatorInterface(SourceAdapterFactory.class);
        return svcLoc;
    }

}