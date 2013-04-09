package no.domeneparser;

import java.text.DecimalFormat;

public class LanguageAnalyzer {

	static DecimalFormat twoDForm = new DecimalFormat("#.##");
      
	
	public static String[] nynorskOrd = {"ein", "korleis", "ikkje", "eg", "eit", "ho", "frå", "blei", "berre", "no", "noko", "vere", "nokon", "ho", "sjølv", "seier", "vore", "gjekk", "meir", "kva", "fekk", "kvar", "heile", "saman", "fleire", "mykje"};	
	public static String[] bokmaalOrd = {"en", "hvordan", "ikke", "jeg", "et", "hun", "fra", "vart", "bare", "nå", "noe", "være", "noen", "hun", "selv", "sier", "vært", "gikk", "mer", "hva", "fikk", "hele", "sammen", "flere", "mye"};
	public static String[] engelskOrd = {"one", "they", "not", "she", "from", "only", "some", "now", "self", "said", "walk", "walked", "more", "where", "together", "whole"};
	
	public static ParseResult analyzeBody(String url, String body) {
		String[] split = body.split(" ");
		
		int nynorsk = occurenceCount(split, nynorskOrd);
		int bokmaal = occurenceCount(split, bokmaalOrd);
		int english = occurenceCount(split, engelskOrd);


		/*
		int langType = LanguageDefinitions.UNKNWON;

		if(nynorsk > 0 && bokmaal == 0 && english == 0) {
			langType = LanguageDefinitions.NYNORSK;
		} else if(nynorsk == 0 && bokmaal > 0 && english == 0) {
			langType = LanguageDefinitions.BOKMAAL;
		} else if(nynorsk == 0 && bokmaal == 0 && english > 0) {
			langType = LanguageDefinitions.ENGLISH;
		} else {
			if(nynorsk > bokmaal && nynorsk > english) {
				langType = LanguageDefinitions.NYNORSK;				
			} else if(bokmaal > nynorsk && bokmaal > english) {
				langType = LanguageDefinitions.BOKMAAL;				
			} else if(english > nynorsk && english > bokmaal) {
				langType = LanguageDefinitions.ENGLISH;				
			}
		}
		*/

//		System.out.println(url + " - " + nynorsk + "/" + bokmaal + "/" + english + " => " + LanguageDefinitions.asString(langType));
		
		double nnProsent = 0.0;
		double bmProsent = 0.0;
		double engProsent = 0.0;
		double unkProsent = 0.0;
		
		int totOrdTreff = nynorsk + bokmaal + english;
		if(totOrdTreff > 0) {
			nnProsent = (double)nynorsk / (double)totOrdTreff;
			bmProsent = (double)bokmaal / (double)totOrdTreff;
			engProsent = (double)english / (double)totOrdTreff;
		}

	//	DomeneParser.display.setText(url + " - " + nnProsent + "/" + bmProsent + "/" + engProsent + " => " + LanguageDefinitions.asString(langType));
		
		
		DomeneParser.display.setText(url + " - nn: " +  String.format("%.2f", nnProsent*100.0) + "% bm:" + String.format("%.2f", bmProsent*100.0) +"%" /* + "/eng:" + roundTwoDecimals(engProsent)*/);
		
		
		
		return new ParseResult(url, split.length, (int)(nnProsent*split.length), (int)(bmProsent*split.length), (int)(engProsent*split.length), (int)(unkProsent*split.length));
		//return new ParseResult(url, split.length, langType);
	}
	
	
	static double roundTwoDecimals(double d) {
        return Double.valueOf(twoDForm.format(d));
	}

	
	private static int occurenceCount(String[] words, String[] lookFor) {
		if(words == null || words.length == 0 || lookFor == null || lookFor.length == 0) {
			return 0;
		} 

		int counter = 0;
		for(int i = 0; i < words.length; i++) {
			for(int j = 0; j < lookFor.length; j++) {
				if(words[i].equalsIgnoreCase(lookFor[j])) {
					counter++;
				}
			}
		}
		return counter;
	}
}
