package es.uned.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import java.io.File;

/**
 * Configuración para el contenedor de servlets.
 */
public class SpringWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { ApplicationContextConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    /* 10 MB */
    private final int maxUploadSizeInMb = 10 * 1024 * 1024;
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        // Directorio temporal donde se subirá el archivo
        File uploadDirectory = new File(System.getProperty("java.io.tmpdir"));
        // Registro de la configuración de un un objeto Multipart
        MultipartConfigElement multipartConfigElement =
                new MultipartConfigElement(uploadDirectory.getAbsolutePath(),
                        maxUploadSizeInMb, maxUploadSizeInMb * 2,
                        maxUploadSizeInMb / 2);
        registration.setMultipartConfig(multipartConfigElement);
    }
}