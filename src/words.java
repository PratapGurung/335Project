import java.util.HashMap;


/*
 * word class: this class will represent the each distinct word/term found in corpus 
 * 			   each words object will have wordDesc as String and 
 * 			   HashMap to store DocumentIds for document in which it was found 
 * 				and frequency in that document
 * 
 * 				setter function: setWordDesc => to set word desc
 * 								 setTermFreqIndoc => write or updates the the frequency 
 * 												     for passed docId	
 * 				getter function: getWordDesc => returns  word desc
 * 								 getTermFreqIndoc => returns the HashMap representing docId -> Freq relationship	
 *
 */
class words {
	private String wordDesc = "";
	private HashMap<String,Integer> termFreqInDoc;
	words(){
		termFreqInDoc = new HashMap<String,Integer>();
	}
	words(String s){
		wordDesc = s;
	}
	public void setWordDesc(String s) {
		wordDesc = s;
	}
	public String getWordDesc() {
		return wordDesc;
	}
	public void setTermFreqInDoc(String docId) {	
		/*
		 * if docId is present for the term increase the freq
		 * else put new docId and set freq to 1
		 */
		if(termFreqInDoc.containsKey(docId)) {
			termFreqInDoc.replace(docId, termFreqInDoc.get(docId)+1);
		}
		else {
			termFreqInDoc.put(docId,1);
		}
	}

	public HashMap<String,Integer>  getTermFreqInDoc() {
		return termFreqInDoc;
	}
}
