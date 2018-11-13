/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stackoverflowsearcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

/**
 *
 * @author Alba, Alberto
 */
public class IndexSearcher {
    public String indexPath;
    Similarity sim = new ClassicSimilarity();
    Analyzer an = new StandardAnalyzer();
    
    public IndexSearcher(String indexPath){
        this.indexPath = indexPath;
    }
    
}
