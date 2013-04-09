package no.domeneparser;

public class ParseResult {
	
	private final String url;
	private final int wordCount;
//	private final int languageType;
	
	private final int nnTreff;
	private final int bmTreff;
	private final int engTreff;
	private final int unkTreff;
	
	public ParseResult(String url, int wordCount, int nnTreff, int bmTreff, int engTreff, int unkTreff) {
		this.url = url;
		this.wordCount = wordCount;
	
		this.nnTreff = nnTreff;
		this.bmTreff = bmTreff;
		this.engTreff = engTreff;
		this.unkTreff = unkTreff;
		//	this.languageType = languageType;
	}

	
	
	public int getNumberOfWords() {
		return wordCount;
	}
	
	public String getURL() {
		return url;
	}

	public int getWords(int langType) {
		if(langType == LanguageDefinitions.BOKMAAL) {
			return bmTreff;
			//return (int) (wordCount * bmProsent);			
		} else if(langType == LanguageDefinitions.ENGLISH) {
			return engTreff;
			//return (int) (wordCount * engProsent);			
		} else if(langType == LanguageDefinitions.NYNORSK) {
			return nnTreff;
		//	return (int) (wordCount * nnProsent);			
		} else if(langType == LanguageDefinitions.UNKNWON) {
			return unkTreff;
		//	return (int) (wordCount * unkProsent);			
		}
		return 0;
	}

	/*
	public int getLanguge() {
		return languageType;
	}
	*/

}
