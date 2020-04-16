import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * @author pratapgurung date: March 05,2020
 * 
 */
public class SearchEngine {

	// to store stoplist
	private static HashMap<Integer, String> stopListMap = new HashMap<>();

	// to store each word
	private static HashMap<String, words> listTerm = new HashMap<String, words>();

	// store list of 200 documents
	private static HashMap<String, String> corpus = new HashMap<>();

	// file containing list of stop words
	private static String stopList = "stoplist.txt";
	private static String results = "results.txt";
	private static String corpusDir = "./corpus";
	private static String invertedIndex = "InvertedIndex2.txt";
	private static String query = "query.txt";
	private static Boolean stemSwitch = true;


	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// pass the path to the file as a parameter

		// get command line args
		// cmdLineParser(args);

		// initialize the stop list
		getStopList(stopList);

		// read each html file from corpus list and
		// for each html file, scrape the text and initialiaze listerm
		getCorpusList();

		// write to the file
		FileWriter writer = new FileWriter(invertedIndex, true);
		InvertedIndexToFile(writer);
		writer.close();
		// search Term and write its inverted index and frequency to a file
		searchTerm(query, results);

	}
	
	

	private static void InvertedIndexToFile(FileWriter writer) {
		// TODO Auto-generated method stub
		
		listTerm.entrySet().forEach(entry -> {
			try {
				String Key = String.format("%-30s", entry.getKey());
				writer.write(Key);
				words w = entry.getValue();

				w.getTermFreqInDoc().entrySet().forEach(doc -> {
					try {
						// writer.write( doc.getKey() + ",Freq: " + doc.getValue() + "\t\t");

						String text = String.format("%-7s%-12s", doc.getKey(), ",Freq: " + doc.getValue());
						writer.write(text);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				writer.write("\n\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		
	}



	private static void cmdLineParser(String[] args) {
		// TODO Auto-generated method stub
		int i = 0;

		while (i < args.length) {
			String argument = args[i];
			if (argument.equals("-CorpusDir")) {
				if (i < args.length) {

					int j = i;
					j++;
					if (args[j].equals("-InvertedIndex") || args[j].equals("-StopList") || args[j].equals("-Queries")
							|| args[j].equals("-Results")) {

						System.err.println("-output requires a CorpusDir");
						System.exit(0);
					}

					else {
						corpusDir = args[++i];
						System.out.println("CorpusDir = " + corpusDir);
					}

				}

			} else if (argument.equals("-InvertedIndex")) {
				if (i < args.length) {
					int j = i;
					j++;
					if (args[j].equals("-CorpusDir") || args[j].equals("-StopList") || args[j].equals("-Queries")
							|| args[j].equals("-Results")) {

						System.err.println("-Program requires a InvertedIndex FileName");
						// System.exit(0);
					}

					else {
						invertedIndex = args[++i];
						System.out.println("InvertedIndex= " + invertedIndex);
					}

				}

			} else if (argument.equals("-StopList")) {
				if (i < args.length) {

					int j = i;
					j++;
					if (args[j].equals("-CorpusDir") || args[j].equals("-InvertedIndex") || args[j].equals("-Queries")
							|| args[j].equals("-Results")) {

						System.err.println("-Program requires a StopList FileName");
						// System.exit(0);
					}

					else {
						stopList = args[++i];
						System.out.println("StopList=" + stopList);
					}

				}

			} else if (argument.equals("-Queries")) {
				if (i < args.length) {
					int j = i;
					j++;
					if (args[j].equals("-CorpusDir") || args[j].equals("-InvertedIndex") || args[j].equals("-StopList")
							|| args[j].equals("-Results")) {

						System.err.println("-Program requires a Queries FileName");
						// System.exit(0);
					}

					else {
						query = args[++i];
						System.out.println("Queries =" + query);
					}

				}

			} else if (argument.equals("-Results")) {
				if (i < args.length) {
					int j = i;
					j++;
					if (args[j].equals("-CorpusDir") || args[j].equals("-InvertedIndex") || args[j].equals("-StopList")
							|| args[j].equals("-Queries")) {

						System.err.println("-Program requires a Results FileName");
						// System.exit(0);
					}

					else {
						results = args[++i];
						System.out.println(results);
					}
				}

			}
			i++;
		}
	}

	/*
	 * parameters : Two String type query-> queryFile name from where query will be
	 * read results -> results will be written for given query
	 * 
	 */
	private static void searchTerm(String queryFile, String resultsFile) {
		// TODO Auto-generated method stub
		try {
			FileReader reader = new FileReader(queryFile);
			FileWriter writer = new FileWriter(resultsFile, true);
			BufferedReader in = new BufferedReader(reader);
			String str;

			while ((str = in.readLine()) != null) {
				str = str.strip();
				if (!str.isEmpty()) {
					String term = str.substring(str.indexOf('<') + 1, str.indexOf('>'));

					// search for term in inverted index and returns it's inverted index
					if (str.startsWith("Q") || str.startsWith("q")) {

						if (listTerm.containsKey(term)) {
							words w = listTerm.get(term);
							writer.write("Query<" + term + ">" + "=> ");
							w.getTermFreqInDoc().entrySet().forEach(entry -> {

								try {
									writer.write(entry.getKey() + " -> ");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							});
							writer.write("\n");
						} else {
							writer.write("Query<" + term + ">" + "=> No Match Found" + "\n");
						}
					}

					// search for term in inverted index and returns it's inverted index with
					// frequency
					if (str.startsWith("F") || str.startsWith("F")) {
						if (listTerm.containsKey(term)) {
							words w = listTerm.get(term);
							writer.write("Query<" + term + ">" + "=> ");
							w.getTermFreqInDoc().entrySet().forEach(doc -> {
								try {
									String text = String.format("%-7s%-12s", doc.getKey(), ",Freq: " + doc.getValue());
									writer.write(text);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							});
							writer.write("\n");
						} else {
							writer.write("Query<" + term + ">" + "=> No Match Found" + "\n");
						}
					}
				}

			}

			in.close();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// end of searchTerm

	private static void getCorpusList() {
		ArrayList<String> list = new ArrayList<String>();
		// TODO Auto-generated method stub
		try (Stream<Path> paths = Files.walk(Paths.get(corpusDir))) {
			paths

			.filter(Files::isRegularFile).forEach(Files -> {

				list.add(Files.toString());

			});
			// .forEach(System.out::println);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getCorpus(list); // call getCorpus function to read all the files
	}// end of geCorpusList

	/*
	 * this method will pass text from fileList and creates list of String that
	 * represents the file to read to create the corpus
	 */

	private static void getCorpus(ArrayList<String> filesList) {

		int i = 1;

		/*
		 * read each line from corpus doc pass that line as string representing fileName
		 * and call creatTermList function
		 */
		for (String str : filesList) {
			str = str.strip();
			String docId = "Doc" + i;
			corpus.put(docId, str);
			// read document
			createTermList(str, docId);
			i++;
		}

	}// end of getCorpus

	/*
	 * this method will take string as arguments that represents the fileName of
	 * file to be read and create list of Stop word using HashMaps
	 * 
	 */
	private static void getStopList(String doc) {
		File file = new File(doc);
		Scanner sc;
		try {
			sc = new Scanner(file);
			int i = 1;
			while (sc.hasNextLine()) {
				stopListMap.put(i, sc.nextLine().toUpperCase());
				// STOP_LIST.add(sc.nextLine());
				// System.out.println(sc.nextLine());
				i++;
			}
			// System.out.println(STOP_LIST.size());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// end of getStopList

	/*
	 * this method will recieve string which represents the filename to read and its
	 * docId and add the all the terms from the file to TermList
	 */
	private static void createTermList(String fileName, String docId) {
		StringBuilder contentBuilder = new StringBuilder();

		try {

			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String str;
			while ((str = in.readLine()) != null) {

				// call cleanUpString method to clean the string from tags, special characters,
				// numbers etc
				str = cleanUpString(str, in);
				if (!str.isEmpty()) {
					contentBuilder.append(str + " ");
				}

			}
			String strArray[] = contentBuilder.toString().split(" ");



			// put elements of String array in term hashmaps
			for (int i = 0; i < strArray.length; i++) {
				String key = strArray[i];


				Stemmer s = new Stemmer();// to stem the word
				String keyAfterStem = s.stem(key); //new key 
				
				//if stemming is off
				if (!keyAfterStem.isEmpty() && stemSwitch) {

					if (!stopListMap.containsValue(keyAfterStem.toUpperCase()) && !listTerm.containsKey(keyAfterStem)) {
						/*
						 * if key is not present in listTerm then its new term hence add it to listerm
						 * and pass the doc id
						 */
						if (!keyAfterStem.contains("http") && keyAfterStem.length() > 1 && keyAfterStem.length() <= 20) {
							// I set the length to 20 because I got some gibberish term from
							// combination of words so to remove those term. I fix the length to my desire
							//if stemming is off
							//words w = new words();
							//w.setWordDesc(key);
							//w.setTermFreqInDoc(docId);
							//listTerm.put(key, w);

							//if stem is on
							words w = new words();
							w.setWordDesc(keyAfterStem);
							w.setTermFreqInDoc(docId);
							listTerm.put(keyAfterStem, w);
						}

					} else if (listTerm.containsKey(keyAfterStem)) {
						// get the word find if the key is present in this doc
						// if present the increase the frequency
						//if stem is off
						//words w = listTerm.get(key);
						//w.setTermFreqInDoc(docId);


						//if stem is on
						words w = listTerm.get(keyAfterStem);
						w.setTermFreqInDoc(docId);

					}
				}
				//if stemming is off
				else if (!key.isEmpty() && !stemSwitch) {

					if (!stopListMap.containsValue(key.toUpperCase()) && !listTerm.containsKey(key)) {
						/*
						 * if key is not present in listTerm then its new term hence add it to listerm
						 * and pass the doc id
						 */
						if (!key.contains("http") && key.length() > 1 && key.length() <= 20) {
							// I set the length to 20 because I got some gibberish term from
							// combination of words so to remove those term. I fix the length to my desire


							words w = new words();
							w.setWordDesc(key);
							w.setTermFreqInDoc(docId);
							listTerm.put(key, w);

						}

					} else if (listTerm.containsKey(key)) {
						// get the word find if the key is present in this doc
						// if present the increase the frequency
						//if stem is off
						words w = listTerm.get(key);
						w.setTermFreqInDoc(docId);

					}
				}

			}

			in.close();

		} catch (IOException e) {
		}
	}// end of createTermList

	/*
	 * the method will take argument sting and bufferedreader and strip html tags,
	 * numbers, special characters and whitespaces from string and return the
	 * resulting string back to its caller
	 */
	private static String cleanUpString(String str, BufferedReader in) throws IOException {
		// TODO Auto-generated method stub

		// removing html tags
		if (str.startsWith("<") && str.endsWith(">")) {
			str = "";
		}

		// removing single line script code
		if (str.contains("<script") && str.contains("/script>")) {
			str = str.replaceAll("<script.*<\\/script>", "");
			// removing multi line script code
			if (str.contains("<script") && !str.contains("</script>")) {
				while (!str.contains("</script>")) {
					str = in.readLine();
				}
				str = "";
			}
		}
		// removing multi line script code
		else if (str.contains("<script") && !str.contains("</script>")) {
			while (!str.contains("</script>")) {
				str = in.readLine();
			}
			str = "";
		}

		// removing single line style code
		if (str.contains("<style") && str.contains("</style>")) {
			str = "";
		}

		// removing multi-line style code
		else if (str.contains("<style") && !str.contains("</style>")) {
			while (!str.contains("/style>")) {
				str = in.readLine();
			}
			str = "";
		}

		// removing single line comments
		if (str.contains("<!") && str.contains("-->")) {
			str = "";
		}

		// removing multi line comments
		else if (str.contains("<!--") && !str.contains("-->")) {
			while (!str.contains("-->")) {
				str = in.readLine();
			}
			str = "";
		}

		// removing multi line tags
		else if (str.contains("<") && !str.contains(">")) {
			while (!str.contains(">")) {
				str = in.readLine();
			}
			str = "";
		}

		// final touch of regex to clean it up the string before adding to content
		// builder
		// str = str.replaceAll("<[^>]*>|<[^>]*", " "); //removing html tags
		str = str.replaceAll("[^A-Za-z0-9\\s]*", "");// removing special characters
		str = str.replaceAll("[\\d\\t]*", "");// removing whitespaces and number
		str = str.replaceAll("src|href|style|alt", "");
		str = str.replaceAll("&[a-z]*\\d*;|&#\\d*;|&[a-z]*\\d*", " "); // removing &nbsp and similar
		str = str.strip();

		return str;
	}

}// end of cleanUpString

/*
 * word class: this class will represent the each distinct word/term found in
 * corpus each words object will have wordDesc as String and HashMap to store
 * DocumentIds for document in which it was found and frequency in that document
 * 
 * setter function: setWordDesc => to set word desc setTermFreqIndoc => write or
 * updates the the frequency for passed docId getter function: getWordDesc =>
 * returns word desc getTermFreqIndoc => returns the HashMap representing docId
 * -> Freq relationship
 *
 */
