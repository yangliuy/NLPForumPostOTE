package edu.pku.yangliu.nlp.pdt.parser;

import java.util.List;

import opennlp.tools.tokenize.WhitespaceTokenizer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

/**Phrase sentences based on stanford parser
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class StanfordParser {
	private static StanfordParser instance = null ;
	private static LexicalizedParser lp;
	
	//Singleton pattern
	public static StanfordParser getInstance(){
		if(StanfordParser.instance == null){
			LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz","-retainTmpSubcategories");
			StanfordParser.instance = new StanfordParser(lp);
		}
		return StanfordParser.instance;
	}
	
	public StanfordParser(LexicalizedParser lp){
		StanfordParser.lp = lp;
	}
	 /**Parse sentences in a file
	 * @param SentFilename The input file
	 * @return  void
	 */
	  public void DPFromFile(String SentFilename) {
		    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		    
		    for (List<HasWord> sentence : new DocumentPreprocessor(SentFilename)) {
		      Tree parse = lp.apply(sentence);
		     // parse.pennPrint();
		     // System.out.println();
		      
		      GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		      List<TypedDependency> tdl = (List<TypedDependency>)gs.typedDependenciesCollapsedTree();
		      //System.out.println(tdl);
		      //System.out.println();
		    }
	  }

	 /**Parse sentences from a String
	 * @param sent The input sentence
	 * @return  List<TypedDependency> The list for type dependency
	 */
	  public List<TypedDependency> DPFromString(String sent) {
		   String [] tokens = WhitespaceTokenizer.INSTANCE// Use the tokenizer in OpenNLP
			.tokenize(sent);
		   //System.out.print("OpenNLP tokens: ");
		  /* for(String token : tokens){
			   System.out.print(token + "  ");
		   }*/
		   //System.out.println();
			List<CoreLabel> rawWords = Sentence.toCoreLabelList(tokens);
			Tree parse = lp.apply(rawWords);
		    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		    //Choose the type of dependenciesCollapseTree
		    //so that dependencies which do not 
		    //preserve the tree structure are omitted
		   return (List<TypedDependency>) gs.typedDependenciesCollapsedTree(); 
	  }
		   
}
