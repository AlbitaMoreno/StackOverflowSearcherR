/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stackoverflowsearcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
/**
 *
 * @author Alba, Antonio
 */
public class IndexSearch {
    private final IndexReader  rutaIndex = DirectoryReader.open(FSDirectory.open(Paths.get("../../index")));
    private Analyzer anTexto = new StandardAnalyzer();
    private Similarity sim = new ClassicSimilarity();
    private Query query;
    //==========================================================================
    //  Los índices tienen los siguientes campos:
    //      Questions:
    //          1. Title
    //          2. Body
    //          3. CreationDate
    //          4. Score
    //          5. Code
    //      Answers:
    //          1. IsAcceptedAnswer
    //          2. Body
    //          3. CreationDate
    //          4. Score
    //          5. Code
    IndexSearch(Query query) throws IOException {
        //LLAMADA CODIGO ALBERTO PARA TRATAR LA QUERY
        this.query = query;
    }
    
    public void indexSearch() throws IOException{
        IndexSearcher searcher = new IndexSearcher(this.rutaIndex);
        searcher.setSimilarity(sim);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        QueryParser parser = new QueryParser("Title_q", this.anTexto);
        TopDocs results = searcher.search(query,100);
        ScoreDoc [] hits = results.scoreDocs;

        for(int j=0; j < hits.length; j++){
            Document doc = searcher.doc(hits[j].doc);
            String body = doc.get("Title_q");
            Integer id = doc.getField("Score_q").numericValue().intValue();
        }
        rutaIndex.close();
    }
}
