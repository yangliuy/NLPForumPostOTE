package edu.pku.yangliu.nlp.pdt.common;
/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    Stopwords.java
 *    Copyright (C) 2001 Eibe Frank
 */

//package weka.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class that can test whether a given string is a stop word.
 * Lowercases all words before the test. <p/>
 * The format for reading and writing is one word per line, lines starting
 * with '#' are interpreted as comments and therefore skipped. <p/>
 * The default stopwords are based on <a href="http://www.cs.cmu.edu/~mccallum/bow/rainbow/" target="_blank">Rainbow</a>. <p/>
 *
 * Accepts the following parameter: <p/>
 *
 * -i file <br/>
 * loads the stopwords from the given file <p/>
 *
 * -o file <br/>
 * saves the stopwords to the given file <p/>
 *
 * -p <br/>
 * outputs the current stopwords on stdout <p/>
 *
 * Any additional parameters are interpreted as words to test as stopwords.
 * 
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.4 $
 */
public class Stopwords {
  
  /** The hash set containing the list of stopwords */
  protected HashSet m_Words = null;

  /** The default stopwords object (stoplist based on Rainbow) */
  protected static Stopwords m_Stopwords;

  static {
    if (m_Stopwords == null) {
      m_Stopwords = new Stopwords();
    }
  }

  /**
   * initializes the stopwords (based on <a href="http://www.cs.cmu.edu/~mccallum/bow/rainbow/" target="_blank">Rainbow</a>).
   */
  public Stopwords() {//YL modified to make the stop words contain fewer words
    m_Words = new HashSet();

    //Stopwords list from Rainbow
    add("%");//yl add
    add("a");
    add("an");
    add("any");
    add("all");
    add("b");
    add("c");
    add("current");
    add("co");
    add("d");
    add("e");
    add("every");
    add("f");
    add("g");
    add("h");
    add("j");
    add("l");
    add("m");
    add("most");
    add("more");
    add("many");
    add("n");
    add("o");
    add("our");
    add("other");
    add("only");
    add("p");
    add("q");
    add("r");
    add("same");
    add("some");
    add("somebody");
    add("somehow");
    add("someone");
    add("something");
    add("sometime");
    add("sometimes");
    add("somewhat");
    add("somewhere");
    add("t");
    add("th");
    add("the");
    add("that");
    add("thats");
    add("this");
    add("these");
    add("their");
    add("those");
    add("the");
    add("u");
    add("v");
    add("w");
    add("x");
    add("y");
    add("z");
    add("w/");
    add("w/o");
    add("$");
    add("'s");
    add("'S");
    add("each");
    add("anyone");
    add("/");
    add("\\");
    add("''");
    add("\\*");
    add("RRB");
    add("LRB");
    add("few");
    add("first");
    add("actual");
    add("entire");
    add("else");
    add("exact");
    add("key");
    add("large");
    add("last");
    add("long");
    add("much");
    add("next");
    add("number");
    add("own");
    add("real");
    add("such");
    add("thing");
    add("total");
  }

  /**
   * removes all stopwords
   */
  public void clear() {
    m_Words.clear();
  }

  /**
   * adds the given word to the stopword list (is automatically converted to
   * lower case and trimmed)
   *
   * @param word the word to add
   */
  public void add(String word) {
    if (word.trim().length() > 0)
      m_Words.add(word.trim().toLowerCase());
  }

  /**
   * removes the word from the stopword list
   *
   * @param word the word to remove
   * @return true if the word was found in the list and then removed
   */
  public boolean remove(String word) {
    return m_Words.remove(word);
  }
  
  /**
   * Returns a sorted enumeration over all stored stopwords
   *
   * @return the enumeration over all stopwords
   */
  public Enumeration elements() {
    Iterator    iter;
    Vector      list;

    iter = m_Words.iterator();
    list = new Vector();

    while (iter.hasNext())
      list.add(iter.next());

    // sort list
    Collections.sort(list);

    return list.elements();
  }

  /**
   * Generates a new Stopwords object from the given file
   *
   * @param filename the file to read the stopwords from
   * @throws Exception if reading fails
   */
  public void read(String filename) throws Exception {
    read(new File(filename));
  }

  /**
   * Generates a new Stopwords object from the given file
   *
   * @param file the file to read the stopwords from
   * @throws Exception if reading fails
   */
  public void read(File file) throws Exception {
    read(new BufferedReader(new FileReader(file)));
  }

  /**
   * Generates a new Stopwords object from the reader. The reader is
   * closed automatically.
   *
   * @param reader the reader to get the stopwords from
   * @throws Exception if reading fails
   */
  public void read(BufferedReader reader) throws Exception {
    String      line;

    clear();
    
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      // comment?
      if (line.startsWith("#"))
        continue;
      add(line);
    }

    reader.close();
  }

  /**
   * Writes the current stopwords to the given file
   *
   * @param filename the file to write the stopwords to
   * @throws Exception if writing fails
   */
  public void write(String filename) throws Exception {
    write(new File(filename));
  }

  /**
   * Writes the current stopwords to the given file
   *
   * @param file the file to write the stopwords to
   * @throws Exception if writing fails
   */
  public void write(File file) throws Exception {
    write(new BufferedWriter(new FileWriter(file)));
  }

  /**
   * Writes the current stopwords to the given writer. The writer is closed
   * automatically.
   *
   * @param writer the writer to get the stopwords from
   * @throws Exception if writing fails
   */
  public void write(BufferedWriter writer) throws Exception {
    Enumeration   enm;

    // header
    writer.write("# generated " + new Date());
    writer.newLine();

    enm = elements();

    while (enm.hasMoreElements()) {
      writer.write(enm.nextElement().toString());
      writer.newLine();
    }

    writer.flush();
    writer.close();
  }

  /**
   * returns the current stopwords in a string
   *
   * @return the current stopwords
   */
  public String toString() {
    Enumeration   enm;
    StringBuffer  result;

    result = new StringBuffer();
    enm    = elements();
    while (enm.hasMoreElements()) {
      result.append(enm.nextElement().toString());
      if (enm.hasMoreElements())
        result.append(",");
    }

    return result.toString();
  }
  
  /** 
   * Returns true if the given string is a stop word.
   * 
   * @param word the word to test
   * @return true if the word is a stopword
   */
  public boolean is(String word) {
    return m_Words.contains(word.toLowerCase());
  }

/** 
   * Returns true if the given string is a stop word.
   * 
   * @param str the word to test
   * @return true if the word is a stopword
   */
  public static boolean isStopword(String str) {
    return m_Stopwords.is(str.toLowerCase());
  }

}
