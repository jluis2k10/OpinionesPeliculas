package es.uned.components;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Component
public class TwitterTokenizer {

    @Autowired
    private ResourceLoader resourceLoader;

    private final static String URL_REGEX = "((www\\.[\\s]+)|(https?://[^\\s]+))";
    private final static String CONSECUTIVE_CHARS = "([a-z])\\1{2,}";
    private final static String STARTS_WITH_NUMBER = "^[1-9]\\s*(\\w+)";

    private String searchTerm = "";
    private String stopWords;

    public String cleanUp(String tweet, String searchTerm) {
        this.searchTerm = searchTerm;
        return this.cleanUp(tweet);
    }

    public String  cleanUp(String tweet) {

        // Escape HTML
        tweet = tweet.replaceAll("&amp;", "&");
        tweet = StringEscapeUtils.unescapeHtml(tweet);

        // Convertir a minúsculas
        tweet = tweet.toLowerCase();

        // Eliminar término de búsqueda (para no incluirlo en las palabras a analizar)
        if (!searchTerm.isEmpty()) {
            tweet = tweet.replaceAll("\\b(" + searchTerm.toLowerCase() + ")\\b\\s?", "#SEARCHTERM# ");
        }

        // Eliminar enlaces si quedan, nombres de usuario, palabras que empiezan por un número
        // y caracteres consecutivos (más de 2, waaaay => way)
        tweet = tweet.replaceAll("@([^\\s]+)", "");
        tweet = tweet.replaceAll(URL_REGEX, "");
        tweet = tweet.replaceAll(CONSECUTIVE_CHARS, "$1");
        tweet = tweet.replaceAll(STARTS_WITH_NUMBER, "");

        // Edge punctuation
        // Credits: https://github.com/brendano/ark-tweet-nlp/blob/8bd4d8378e596e8127e4b700c89804720333ab8d/src/cmu/arktweetnlp/Twokenize.java
        // Queremos: 'foo' => ' foo ' pero conservando: don't => don't
        String edgePunctChars    = "'\"“”‘’«»{}\\(\\)\\[\\]\\*&"; //add \\p{So}? (symbols)
        String edgePunct    = "[" + edgePunctChars + "]";
        String notEdgePunct = "[a-zA-Z0-9]"; // content characters
        String offEdge = "(^|$|:|;|\\s|\\.|,)";  // colon here gets "(hello):" ==> "( hello ):"
        Pattern EdgePunctLeft  = Pattern.compile(offEdge + "("+edgePunct+"+)("+notEdgePunct+")");
        Pattern EdgePunctRight = Pattern.compile("("+notEdgePunct+")("+edgePunct+"+)" + offEdge);
        Matcher matcher = EdgePunctLeft.matcher(tweet);
        tweet = matcher.replaceAll("$1$2 $3");
        matcher = EdgePunctRight.matcher(tweet);
        tweet = matcher.replaceAll("$1 $2$3");

        // Eliminar stop words
        Resource stopWordsResource = resourceLoader.getResource("classpath:/en_stopwords.txt");
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = stopWordsResource.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            if (bufferedReader != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\\b(");
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("|");
                }
                stringBuilder.setLength(stringBuilder.length() - 1); // Eliminar último "|"
                stringBuilder.append(")\\b\\s?");
                stopWords = stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            if (bufferedReader != null) try { bufferedReader.close(); } catch (IOException logOrIgnore) {}
        }
        tweet = tweet.replaceAll(stopWords, "");

        return tweet;

    }

}
