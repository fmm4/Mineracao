package back;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashSet;

public class Indexer {
  
  private Indexer() {}
  
  public static void main(String[] args) {
	 
	String defaultIndex = ".\\indexed";
	String stemmedIndex = ".\\indexedStemmed";
	String stopwordIndex = ".\\indexedNoStpWrd";
	String treatedIndex = ".\\indexedTreated";
    String docsPath = ".\\artigos";
    boolean create = true;

    final Path docDir = Paths.get(docsPath);
    if (!Files.isReadable(docDir)) {
      System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
    
    
    //Custom Analyze
    
    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + defaultIndex + "'...");
      
      Directory dir = FSDirectory.open(Paths.get(defaultIndex));
      Analyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

      if (create) {
        iwc.setOpenMode(OpenMode.CREATE);
     } else {
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      }

      IndexWriter writer = new IndexWriter(dir, iwc);
      indexDocs(writer, docDir);
      
      writer.close();

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
  }

  static void indexDocs(final IndexWriter writer, Path path) throws IOException {
    if (Files.isDirectory(path)) {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          try {
            indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
          } catch (IOException ignore) {
     
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } else {
      indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
    }
  }

  static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
    try (InputStream stream = Files.newInputStream(file)) {
      Document doc = new Document();

      System.out.println("Reading file:"+file.getFileName());
      Field pathField = new StringField("path", file.toString(), Field.Store.YES);
      doc.add(pathField);
      
      doc.add(new LegacyLongField("modified", lastModified, Field.Store.NO));
      
      doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
      
      if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
        System.out.println("adding " + file);
        writer.addDocument(doc);
      } else {
        System.out.println("updating " + file);
        writer.updateDocument(new Term("path", file.toString()), doc);
      }
    }
  }
}