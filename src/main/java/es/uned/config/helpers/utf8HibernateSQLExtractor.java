package es.uned.config.helpers;

import org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor;

import javax.sql.DataSource;
import java.io.IOError;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Por defecto Hibernate lee los archivos utilizando la codificación definida
 * en el sistema, independientemente de la propia codificación que tenga el archivo.
 * Con esta clase se consigue que los archivos SQL se "reconviertan" a UTF-8.
 * @see es.uned.config.DBConfig#entityManagerFactory(DataSource)
 */
public class utf8HibernateSQLExtractor extends MultipleLinesSqlCommandExtractor {

    private final String SOURCE_CHARSET = "UTF-8";

    /**
     * Convierte/codifica cadenas de texto a formato/codificación UTF-8
     * @param reader Stream de entrada con el texto a convertir
     * @return Texto codificado
     */
    @Override
    public String[] extractCommands(final Reader reader) {
        String[] lines = super.extractCommands(reader);

        Charset charset = Charset.defaultCharset();
        if (!charset.equals(Charset.forName(SOURCE_CHARSET))) {
            for (int i = 0; i < lines.length; i++) {
                try {
                    lines[i] = new String(lines[i].getBytes(), SOURCE_CHARSET);
                } catch (UnsupportedEncodingException e) {
                    throw new IOError(e);
                }
            }
        }
        return lines;
    }
}
