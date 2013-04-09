package no.domeneparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class StorageManager {

	public static String STORAGE_BASE_PATH = "domeneparser";
	
	private static final String CRAWL_RESULT_PROP_FILE = "resultat.prop";
	
	private static final String ACTIVE_CRAWL_DIR = "aktiv";
	

	
	/**
	 *  storage/
	 * 		   organ/
	 * 				skatteetaten/
	 * 							details.prop    <- domene detailjer ++
	 * 							activecrawl/
	 * 								  details.prop
	 * 								  tmpdata/	
	 * 							crawls/
	 * 								  23.11.2012 16:24:22/
	 * 													 result.prop
	 * 													 /data
	 * 														  ...
	 * 				biltilsynet/
	 * 							details.prop
	 * 							crawls/
	 * 								   11.11.2012 16:12:12/
	 * 													  result.prop
	 * 								   11.09.2012 16:14:12/
	 * 													  result.prop
	 * 	
	 */
	
	
	private final String domene;
	
	private HashMap<String, ParseResult> parsedList = new HashMap<String, ParseResult>();
	private ArrayList<String> linkList = new ArrayList<String>();

	
	public StorageManager(String path, String domene) throws IOException {
		STORAGE_BASE_PATH = path + File.separator + STORAGE_BASE_PATH;
		this.domene = domene;
		loadData();
	}

	public HashMap<String, ParseResult> getParsedList() {
		return parsedList;
	}
	
	public ArrayList<String> getLinkList() {
		return linkList;
	}

	private void loadData() throws IOException {
		File dir = new File(STORAGE_BASE_PATH);
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}

		File domeneDir = new File(dir.getAbsoluteFile() + File.separator + domene);
		if(!domeneDir.exists()) {
			domeneDir.mkdirs();
		}
		
		loadDomene(domeneDir);
	}
		
	public void saveCrawl(Crawl crawl) throws IOException {

		File dir = new File(STORAGE_BASE_PATH);
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}

		File domeneDir = new File(dir.getAbsoluteFile() + File.separator + domene);
		if(!domeneDir.exists()) {
			domeneDir.mkdirs();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd'_'HH.mm.ss");
		Date lastCrawl = new Date(System.currentTimeMillis());
		String time = sdf.format(lastCrawl);
		
		File timeDir = new File(domeneDir.getAbsoluteFile() + File.separator + time);
		if(!timeDir.exists()) {
			timeDir.mkdirs();
		}
	
		CrawlData crawlData = crawl.getData();

		File links = new File(timeDir.getAbsolutePath() + File.separator + "links.txt");
		
		BufferedWriter output = new BufferedWriter(new FileWriter(links));
		
		ArrayList<String> linkList = crawlData.getLinks();
		for(String link : linkList) {
			output.write(link + "\n");
		}
		output.close();
		
		File parsed = new File(timeDir.getAbsolutePath() + File.separator + "parsed.txt");
		
		writeParseResults(parsed, crawlData);
		/*
		output = new BufferedWriter(new FileWriter(parsed));
		
		HashMap<String, ParseResult> parsedList = crawlData.getParsedURLS();
		for(String url : parsedList.keySet()) {
		//	output.write(url + "\t" + parsedList.get(url).getNumberOfWords() + "\t" + LanguageDefinitions.asString(parsedList.get(url).getLanguge()) + "\n");
			output.write(url + "\t" + parsedList.get(url).getNumberOfWords() + 
					"\tnn:" + parsedList.get(url).getWords(LanguageDefinitions.NYNORSK) + 
					", bm:" + parsedList.get(url).getWords(LanguageDefinitions.BOKMAAL) + 
					", eng:" + parsedList.get(url).getWords(LanguageDefinitions.ENGLISH) + 
					", ukjent:" + parsedList.get(url).getWords(LanguageDefinitions.UNKNWON) + 
					"\n");
			
		}
		output.close();
		*/
		CrawlResultSummary result = crawl.getResultSummary();
	
		File propFile = new File(timeDir.getAbsolutePath() + File.separator + CRAWL_RESULT_PROP_FILE);
    	try {
	 	
		Properties prop = getProperties(result);
		prop.setProperty("crawl_finished", time);
		prop.store(new FileOutputStream(propFile.getAbsolutePath()), null);
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }

    	/*
		Properties prop = new Properties();
	 	
    	try {
    		prop.setProperty("crawl_finished", time);
    		prop.setProperty("sider_parset", Integer.toString(result.numberOfUrls)); 
    		prop.setProperty("sider_nynorsk", Integer.toString(result.nynorskPages));
    		prop.setProperty("sider_bokmaal", Integer.toString(result.bokmaalPages));	
    		prop.setProperty("sider_engelsk", Integer.toString(result.englishPages));	
    		prop.setProperty("sider_uklassifisert", Integer.toString(result.unknownPages));	
    		prop.setProperty("nynorsk_andel", Double.toString(((double)result.nynorskPages / ((double)result.bokmaalPages + (double)result.nynorskPages))*100) + "%");
    		prop.store(new FileOutputStream(propFile.getAbsolutePath()), null);
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
        */
    	
    	File currentCrawlDir = new File(domeneDir.getAbsolutePath() + File.separator + ACTIVE_CRAWL_DIR);
    	System.out.println("Sletter: " + currentCrawlDir.getAbsolutePath());
    	deleteRecursive(currentCrawlDir);
	}

	private void writeParseResults(File parsed, CrawlData crawlData) throws IOException {
	
		BufferedWriter output = new BufferedWriter(new FileWriter(parsed));
	
		HashMap<String, ParseResult> parsedList = crawlData.getParsedURLS();
		for(String url : parsedList.keySet()) {
		output.write(url + "\t" + parsedList.get(url).getNumberOfWords() + 
				"\t" + parsedList.get(url).getWords(LanguageDefinitions.NYNORSK) + 
				"\t" + parsedList.get(url).getWords(LanguageDefinitions.BOKMAAL) + 
				"\t" + parsedList.get(url).getWords(LanguageDefinitions.ENGLISH) + 
				"\t" + parsedList.get(url).getWords(LanguageDefinitions.UNKNWON) + 
				"\n");
		
		}
		output.close();
	}
	
	private Properties getProperties(CrawlResultSummary result) {
		Properties prop = new Properties();
		prop.setProperty("sider_parset", Integer.toString(result.numberOfUrls)); 
		prop.setProperty("ord_nynorsk", Long.toString(result.nynorskWords));
		prop.setProperty("ord_bokmaal", Long.toString(result.bokmaalWords));	
		prop.setProperty("ord_engelsk", Long.toString(result.englishWords));	
		prop.setProperty("ord_uklassifisert", Long.toString(result.unknownWords));	
		prop.setProperty("nynorsk_andel", Double.toString(((double)result.nynorskWords / ((double)result.bokmaalWords + (double)result.nynorskWords))*100) + "%"); 
    	return prop;
	}
		
	private void deleteRecursive(File f) {
		if(f.isDirectory()) {
			if(f.listFiles().length==0){
				f.delete();
			} else {
				File[] list = f.listFiles();
				for(File cf : list) {
					deleteRecursive(cf);
				}
				f.delete();
			}			
		} else {
			f.delete();
		}
	}
	
	
	public void saveCrawlTmp(Crawl crawl) throws IOException {
		File dir = new File(STORAGE_BASE_PATH);
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}

		File domeneDir = new File(dir.getAbsoluteFile() + File.separator + domene);
		if(!domeneDir.exists()) {
			domeneDir.mkdirs();
		}
		
		File currentCrawlDir = new File(domeneDir.getAbsolutePath() + File.separator + ACTIVE_CRAWL_DIR);
		
		if(currentCrawlDir.exists()) {
			currentCrawlDir.delete();
		}
		
		currentCrawlDir.mkdir();
		
		CrawlData crawlData = crawl.getData();
		
		File links = new File(currentCrawlDir.getAbsolutePath() + File.separator + "links.txt");
		
		BufferedWriter output = new BufferedWriter(new FileWriter(links));
		
		ArrayList<String> linkList = crawlData.getLinks();
		for(String link : linkList) {
			output.write(link + "\n");
		}
		output.close();
		
		File parsed = new File(currentCrawlDir.getAbsolutePath() + File.separator + "parsed.txt");
		writeParseResults(parsed, crawlData);
		/*
		output = new BufferedWriter(new FileWriter(parsed));
		
		HashMap<String, ParseResult> parsedList = crawlData.getParsedURLS();
		for(String url : parsedList.keySet()) {
	//		output.write(url + "\t" + parsedList.get(url).getNumberOfWords() + "\t" + LanguageDefinitions.asString(parsedList.get(url).getLanguge()) + "\n");
			output.write(url + "\t" + parsedList.get(url).getNumberOfWords() + 
					"\tnn:" + parsedList.get(url).getWords(LanguageDefinitions.NYNORSK) + 
					", bm:" + parsedList.get(url).getWords(LanguageDefinitions.BOKMAAL) + 
					", eng:" + parsedList.get(url).getWords(LanguageDefinitions.ENGLISH) + 
					", ukjent:" + parsedList.get(url).getWords(LanguageDefinitions.UNKNWON) + 
					"\n");
	
		}
		output.close();
		*/
		
		File propFile = new File(currentCrawlDir.getAbsolutePath() + File.separator + CRAWL_RESULT_PROP_FILE);

		try {
			CrawlResultSummary result = crawl.getResultSummary();
			Properties prop = getProperties(result);
			prop.store(new FileOutputStream(propFile.getAbsolutePath()), null);
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
		
		/*
		Properties prop = new Properties();
	 		 	
    	try {
    		prop.setProperty("sider_parset", Integer.toString(result.numberOfUrls)); 
    		prop.setProperty("sider_nynorsk", Integer.toString(result.nynorskPages));
    		prop.setProperty("sider_bokmaal", Integer.toString(result.bokmaalPages));	
    		prop.setProperty("sider_engelsk", Integer.toString(result.englishPages));	
    		prop.setProperty("sider_uklassifisert", Integer.toString(result.unknownPages));	
    		prop.setProperty("nynorsk_andel", Double.toString(((double)result.nynorskPages / ((double)result.bokmaalPages + (double)result.nynorskPages))*100) + "%");
    		prop.store(new FileOutputStream(propFile.getAbsolutePath()), null);
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
        */
	}

	private void loadDomene(File domeneDir) throws IOException {
		linkList.clear();
		parsedList.clear();;

		File crawlDir = new File(domeneDir.getAbsolutePath() + File.separator + ACTIVE_CRAWL_DIR);			
		if(crawlDir.exists()) {
			
			File links = new File(crawlDir.getAbsolutePath() + File.separator + "links.txt");
			BufferedReader br = new BufferedReader(new FileReader(links));
			String line = br.readLine();
			while (line != null) {
				linkList.add(line);
				line = br.readLine();
			}
			br.close();
			
			File parsed = new File(crawlDir.getAbsolutePath() + File.separator + "parsed.txt");
			br = new BufferedReader(new FileReader(parsed));
			line = br.readLine();
			while (line != null) {
			
				String split[] = line.split("\t");
				if(split.length == 6) {
					String url = split[0];
					int numberOfWords = Integer.parseInt(split[1]);
					int numberOfNNWords = Integer.parseInt(split[2]);
					int numberOfBMWords = Integer.parseInt(split[3]);
					int numberOfENGWords = Integer.parseInt(split[4]);
					int numberOfUNKWords = Integer.parseInt(split[5]);
					
					
					parsedList.put(url, new ParseResult(url, numberOfWords,  numberOfNNWords, numberOfBMWords, numberOfENGWords, numberOfUNKWords));

					
					
					/*
	output.write(url + "\t" + parsedList.get(url).getNumberOfWords() + 
				"\t" + parsedList.get(url).getWords(LanguageDefinitions.NYNORSK) + 
				"\t" + parsedList.get(url).getWords(LanguageDefinitions.BOKMAAL) + 
				"\t" + parsedList.get(url).getWords(LanguageDefinitions.ENGLISH) + 
				"\t" + parsedList.get(url).getWords(LanguageDefinitions.UNKNWON) + 
				"\n");					 */
					
				}
				line = br.readLine();
			}
			br.close();
		}
	}
}
