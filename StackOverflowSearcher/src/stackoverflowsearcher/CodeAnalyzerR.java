
package stackoverflowsearcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LetterTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.xml.sax.SAXException;

public class CodeAnalyzerR {
    private String codigo;

    CodeAnalyzerR(String text) {
        this.codigo = text;    
    }

    public void index() throws IOException, FileNotFoundException, SAXException {
        Analyzer ana = CustomAnalyzer.builder(Paths.get("."))
            .withTokenizer(LetterTokenizerFactory.class)
            .addTokenFilter(LowerCaseFilterFactory.class)
            .addTokenFilter(StopFilterFactory.class, "ignoreCase", "false", "words", "stopwords.txt", "format", "wordset")
            .build();
    }    
}
