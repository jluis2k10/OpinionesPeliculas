package es.uned.components;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Component
public class Tokenizer {

    @Autowired
    private ResourceLoader resourceLoader;

    private final static String EN_STOP_WORDS = "en_stopwords.txt";
    private final static String ES_STOP_WORDS = "es_stopwords.txt";
    private final static String URL_REGEX = "((www\\.[\\s]+)|(https?://[^\\s]+))";
    private final static String CONSECUTIVE_CHARS = "([a-z])\\1{2,}";
    private final static String STARTS_WITH_NUMBER = "^[1-9]\\s*(\\w+)";

    private String language;
    private boolean deleteStopWords = false;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isDeleteStopWords() {
        return deleteStopWords;
    }

    public void setDeleteStopWords(boolean deleteStopWords) {
        this.deleteStopWords = deleteStopWords;
    }

    /**
     * Credits: https://github.com/brendano/ark-tweet-nlp/blob/8bd4d8378e596e8127e4b700c89804720333ab8d/src/cmu/arktweetnlp/Twokenize.java
     * @param text
     * @return
     */
    public String tokenize(String text) {
        // Escape HTML
        text = text.replaceAll("&amp;", "&");
        text = StringEscapeUtils.unescapeHtml(text);

        // Convertir a minúsculas
        text = text.toLowerCase();

        // Eliminar stop-words
        if (isDeleteStopWords())
            text = deleteStopWords(text);

        // Eliminar enlaces si quedan, nombres de usuario, palabras que empiezan por un número
        // y caracteres consecutivos (más de 2, waaaay => way)
        text = text.replaceAll("@([^\\s]+)", "");
        text = text.replaceAll(URL_REGEX, "");
        text = text.replaceAll(CONSECUTIVE_CHARS, "$1");
        text = text.replaceAll(STARTS_WITH_NUMBER, "");

        // Edge punctuation
        // Queremos: 'foo' => ' foo ' pero conservando: don't => don't
        String edgePunctChars    = "'\"“”‘’«»{}\\(\\)\\[\\]\\*&"; //add \\p{So}? (symbols)
        String edgePunct    = "[" + edgePunctChars + "]";
        String notEdgePunct = "[a-zA-Z0-9]"; // content characters
        String offEdge = "(^|$|:|;|\\s|\\.|,)";  // colon here gets "(hello):" ==> "( hello ):"
        Pattern EdgePunctLeft  = Pattern.compile(offEdge + "("+edgePunct+"+)("+notEdgePunct+")");
        Pattern EdgePunctRight = Pattern.compile("("+notEdgePunct+")("+edgePunct+"+)" + offEdge);
        Matcher matcher = EdgePunctLeft.matcher(text);
        text = matcher.replaceAll("$1$2 $3");
        matcher = EdgePunctRight.matcher(text);
        text = matcher.replaceAll("$1 $2$3");

        // Tokenización del texto
        // Pasamos de "this is an 'example', i'm not interested." a "this is an ' example ' , i ' m not interested ."
        Pattern punctuation = Pattern.compile("(?:['\"“”‘’]+|[.?!¿¡,…]+|[:;]+)");
        matcher = punctuation.matcher(text);
        List<List<String>> bads = new ArrayList<>();
        List<Pair<Integer,Integer>> badSpans = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.start() != matcher.end()) {
                List<String> bad = new ArrayList<>(1);
                bad.add(text.substring(matcher.start(), matcher.end()));
                bads.add(bad);
                badSpans.add(new Pair<>(matcher.start(), matcher.end()));
            }
        }
        List<Integer> indices = new ArrayList<>(2+2*badSpans.size());
        indices.add(0);
        for (Pair<Integer,Integer> p: badSpans) {
            indices.add(p.first);
            indices.add(p.second);
        }
        indices.add(text.length());
        List<List<String>> splitGoods = new ArrayList<>(indices.size()/2);
        for (int i = 0; i < indices.size(); i+=2) {
            String goodStr = text.substring(indices.get(i), indices.get(i+1));
            List<String> splitStr = Arrays.asList(goodStr.trim().split(" "));
            splitGoods.add(splitStr);
        }
        List<String> zippedStr = new ArrayList<>();
        int i;
        for (i = 0; i < bads.size(); i++) {
            zippedStr = addAllnonempty(zippedStr, splitGoods.get(i));
            zippedStr = addAllnonempty(zippedStr, bads.get(i));
        }
        zippedStr = addAllnonempty(zippedStr, splitGoods.get(i));

        return StringUtils.join(zippedStr, " ");
    }

    private String deleteStopWords(String text) {
        String stopWordsRegEx = "";
        Resource stopWordsResource = null;
        if (language.equals("en"))
            stopWordsResource = resourceLoader.getResource("classpath:/" + EN_STOP_WORDS);
        else if (language.equals("es"))
            stopWordsResource = resourceLoader.getResource("classpath:/" + ES_STOP_WORDS);
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
                stopWordsRegEx = stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            if (bufferedReader != null) try { bufferedReader.close(); } catch (IOException logOrIgnore) {}
        }
        return text.replaceAll(stopWordsRegEx, "");
    }

    private static List<String> addAllnonempty(List<String> master, List<String> smaller){
        for (String s : smaller){
            String strim = s.trim();
            if (strim.length() > 0)
                master.add(strim);
        }
        return master;
    }

    private static class Pair<T1, T2> {
        public T1 first;
        public T2 second;
        public Pair(T1 x, T2 y) { first=x; second=y; }
    }

}
