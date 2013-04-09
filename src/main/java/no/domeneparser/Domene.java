package no.domeneparser;

import java.sql.Date;

public class Domene {
	
	private final String url;
	
	private String domainPart;
	
	private Date lastCrawl = null;
	
	public Date getLastCrawl() {
		return lastCrawl;
	}

	public Domene(String url) {
		this.url = cleanUrl(url);
		this.domainPart = extractDomainPart(url);
		
		System.out.println("Nytt domene : " + url + " => " + domainPart);
	}
	
	
	private String extractDomainPart(String url) {
		if(url.contains("http://")) {
			url = url.substring(7);
		}
		
		if(url.contains("/")) {
			url = url.substring(0, url.indexOf("/"));
		}
		
		String[] split = url.split("\\.");
		if(split == null || split.length < 2) {
		//	System.out.println("err: " + url);
		//	for(String part : split) {
		//		System.out.println("=>" + part);
		//	}
			return url;
		}
		
		String domainPart = split[split.length-2] + "." + split[split.length-1];
		return domainPart;
	}
	
	public boolean isPartOfDomain(String url) {
		return domainPart.equalsIgnoreCase(extractDomainPart(url));		
	}
	
	public String getDomainPart() {
		return domainPart;
	}

	private String cleanUrl(String url) {
		if(!url.startsWith("http://")) {
			url = "http://" + url;
		}
		if(url.endsWith("/")) {
			url = url.substring(0, url.length()-1);
		}
		return url;
	}
	
	public void setLastCrawl(Date lastCrawl) {
		this.lastCrawl = lastCrawl;
	}
	

	public String getUrl() {
		return url;
	}

	
}
