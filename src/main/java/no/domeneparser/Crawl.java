package no.domeneparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawl extends Thread {

	public static final int STATE_NOT_STARTED = 1;
	public static final int STATE_COMPLETED = 2;
	public static final int STATE_IN_PROGESS = 3;
	
	private int crawlState = STATE_NOT_STARTED;
	
	private final ArrayList<String> links;
	private final HashMap<String, ParseResult> parsedURLS;

	private String currentURL;
	
	private Date lastSave = new Date(0);
	
	private CrawlResultSummary result;

	/*
	private int nn;
	private int nb;
	private int eng;	
	private int unk;
*/
	private final Domene domene;
	private final int sleepTime;
	
	public Crawl(Domene domene, double sleepTime, ArrayList<String> links, HashMap<String, ParseResult> parsedURLS) {
		this.domene = domene;
		this.links = links;
		this.parsedURLS = parsedURLS;
		this.sleepTime = (int)(sleepTime*1000);
		//System.out.println("sleep: " + this.sleepTime);
	//	initializeStats();
	}
	
	public int getCrawlState() {
		return crawlState;
	}
	
	/*
	private void initializeStats() {
		if(parsedURLS != null) {
			for(ParseResult res : parsedURLS.values()) {
				setClassification(res);
			}
		}
	}
	*/

	public boolean shouldResumeCrawl() {
		if(crawlState != STATE_COMPLETED && links.size() > 0) {
			return true;
		}
		return false;
	}
	
	public void run() {
		crawlState = STATE_IN_PROGESS;
		
		try {			
			addInitialLinks();
			performCrawl();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reportResult();
			crawlState = STATE_COMPLETED;
		}
	}

	public CrawlData getData() {
		CrawlData data = new CrawlData(links, parsedURLS);
		return data;
	}
	
	private CrawlResultSummary reportResult() {
		
		long nynorskOrd = getNumberOfWords(parsedURLS, LanguageDefinitions.NYNORSK);
		long bokmaalOrd = getNumberOfWords(parsedURLS, LanguageDefinitions.BOKMAAL);
		long englishOrd = getNumberOfWords(parsedURLS, LanguageDefinitions.ENGLISH);
		long unknownOrd = getNumberOfWords(parsedURLS, LanguageDefinitions.UNKNWON);
		
		/*
		int nynorskPages = getNumberPages(parsedURLS, LanguageDefinitions.NYNORSK);
		int bokmaalPages = getNumberPages(parsedURLS, LanguageDefinitions.BOKMAAL);
		int englishPages = getNumberPages(parsedURLS, LanguageDefinitions.ENGLISH);
		int unknownPages = getNumberPages(parsedURLS, LanguageDefinitions.UNKNWON);
		*/
		
		result = new CrawlResultSummary(parsedURLS.size(), unknownOrd, nynorskOrd, bokmaalOrd, englishOrd);
		return result;
	}

	private long getNumberOfWords(HashMap<String, ParseResult> parsedURLS, int langType) {
		long counter = 0;
		for(ParseResult result : parsedURLS.values()) {
			counter += result.getWords(langType);
		}
		return counter;
	}

	private void addInitialLinks() {
		if(links.size() == 0) {
			addLink(domene.getUrl());
		}
	}
	
	private void addLink(String url) {
	//	System.out.println("addLink: " + url);
		links.add(url);
	}

	private void performCrawl() {
//		int cutOff = 0;
		while(links.size() > 0/* && cutOff < 10*/) {
			currentURL = links.remove(0);
	
			parseURL(currentURL);
	//		cutOff++;
			
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
	}

	private void findLinks(Document doc, String url) {
		Elements elements = doc.select("a");

		Iterator<Element> it = elements.iterator();
		while(it.hasNext()) {
			Element elem = it.next();
			String link = elem.attr("href");
			
			if(link.startsWith("www")) {
				link = "http://" + link;
			}

			String absoluteLink = buildLink(link, url);
			
			if(verifyLink(absoluteLink)) {
				if(!absoluteLink.equals(currentURL) && !parsedURLS.containsKey(absoluteLink) && !links.contains(absoluteLink)) {
					addLink(absoluteLink);
				}
			} 				
		}

	}
	
	private void parseURL(String url) {
		try {
			Connection conn = Jsoup.connect(url);
			conn.timeout(10000);
			Document doc = conn.get();		
			
			String body = doc.body().text();
		
			analyzeBody(url, body);
			
			findLinks(doc, url);
			
		} catch (UnsupportedMimeTypeException e) {
			String type = e.getMimeType();
			System.out.println("type: " + type);
			if (type != null) {
				try {
					if (type.toLowerCase().contains("pdf")) {
						System.out.println("pdf");
						String body = DownloadPDF.getBody(url);
						if (body != null) {
							analyzeBody(url, body);
						}
					} else if (type.toLowerCase().contains("doc")) {
						DownloadDoc doc = new DownloadDoc(url);
						String body = doc.download();
						if (body != null) {
							analyzeBody(url, body);
						}
					} else {
						DomeneParser.display.setText("URL " + url + " feilet. " + type);
					}
				} catch (Exception x) {
					DomeneParser.display.setText("URL " + url + " feilet");
					// System.out.println("URL " + url + " feilet");
					x.printStackTrace();
				}
			} else {
				DomeneParser.display.setText("URL " + url + " feilet");
			}
		} catch (IOException e) {
			DomeneParser.display.setText("URL " + url + " feilet");
//			System.out.println("URL " + url + " feilet");
			e.printStackTrace();
		} catch (Exception e) {
			DomeneParser.display.setText("URL " + url + " feilet");
			//		System.out.println("URL " + url + " feilet");
			e.printStackTrace();
			
		}

	}
	

	private boolean verifyLink(String url) {
		if(url != null && url.length() > 0) {			
			String lower = url.toLowerCase();
			if(lower.endsWith(".pdf")) {
				return false;
			}
			if(lower.startsWith("mailto")) {
				return false;
			}
			if(lower.startsWith("mailto")) {
				return false;
			}
			if(lower.endsWith(".gif") || lower.endsWith(".jpg") || lower.endsWith(".png")) {
				return false;
			}
 			
			return domene.isPartOfDomain(url);
		} 
		return false;
	}

	
	private String buildLink(String link, String url) {
		if(link == null || link.length() == 0) {
			return "";
		}
		if(link.endsWith("/")) {
			link = link.substring(0, link.length()-1);
		}
		if(link.startsWith("http")) {
			return link;
		}
		if(link.startsWith("www")) {
			return "http://" + link;
		}
		if(link.startsWith("file") || url.startsWith("mailto")) {
			return link;
		}
		if(link.startsWith("/")) {
			return getDomainPart(url) + link;
		}
		return link;
	}

	private String getDomainPart(String url) {
		if(url.startsWith(domene.getUrl())) {
			return domene.getUrl();
		}
		return "";
	}

	private void analyzeBody(String url, String body) {
		
		ParseResult result = LanguageAnalyzer.analyzeBody(url, body);
		//setClassification(result);
		parsedURLS.put(url, result);	
	}
	
	/*
	private void setClassification(ParseResult result) {
		if(result.getLanguge() == LanguageDefinitions.BOKMAAL) {
			nb++;
		} else if(result.getLanguge() == LanguageDefinitions.NYNORSK) {
			nn++;
		} else if(result.getLanguge() == LanguageDefinitions.ENGLISH) {
			eng++;
		} else {
			unk++;
		}
	}
	*/

	public void setLastSave(Date date) {
		this.lastSave = date;
	}
	
	public Date getLastSave() {
		return lastSave;
	}

	public CrawlResultSummary getResultSummary() {
		return reportResult();
		//return result;
	}

	public boolean isDone() {
		return crawlState == STATE_COMPLETED;
	}

	/*
	public int getBokmaal() {
		return nb;
	}

	public int getNynorsk() {
		return nn;
	}
	*/
}
