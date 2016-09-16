package back;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;

/** Simple command-line based search demo. */
public class Searcher {

  private Searcher() {}

  /** Simple command-line based search demo. */
  public static void search(String queryString,boolean stopword, boolean stemming) throws Exception {

	String index = null;
	Analyzer analyzer = null;
	if(!stopword && !stemming)
	{
		index = ".\\indexed";
		analyzer = new StandardAnalyzer(new CharArraySet(0, false));	
	}else if(stopword && !stemming)
	{
		index = ".\\indexedNoStpWrd";
		analyzer = new StandardAnalyzer();
	}else if(!stopword && stemming){
		index = ".\\indexedStemming";
		analyzer = new StandardAnalyzer(new CharArraySet(0, false));
		
	}else if(stopword && stemming)
	{
		index = ".\\indexedTreated";
	}
    String field = "contents";
    String queries = null;
    int repeat = 0;
    boolean raw = false;

    
    int hitsPerPage = 100;
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
    IndexSearcher searcher = new IndexSearcher(reader);
    

    BufferedReader in = null;
    QueryBuilder queryb = new QueryBuilder(analyzer);

    Query query = queryb.createPhraseQuery(field, queryString);
      	
    System.out.println("Searching for: " + query.toString(field));
    searcher.search(query, 100);
    doSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);    
    reader.close();
  }
  
  public static void doSearch(BufferedReader in, IndexSearcher searcher, Query query, 
                                     int hitsPerPage, boolean raw, boolean interactive) throws IOException {
 
    // Collect enough docs to show 5 pages
    TopDocs results = searcher.search(query, 5 * hitsPerPage);
    ScoreDoc[] hits = results.scoreDocs;
    
    int numTotalHits = results.totalHits;
    System.out.println(numTotalHits + " total matching documents");

    int start = 0;
    int end = Math.min(numTotalHits, hitsPerPage);
      
      for (int i = start; i < end; i++) {
        Document doc = searcher.doc(hits[i].doc);
       String path = doc.get("path");
        if (path != null) {
          System.out.println((i+1) + ". " + path);
          String title = doc.get("title");
          if (title != null) {
            System.out.println("   Title: " + doc.get("title"));
          }
        } else {
          System.out.println((i+1) + ". " + "No path for this document");
        }
                  
      }
    }
  }