package no.domeneparser;

public class CrawlResultSummary {

	public final int numberOfUrls;

	public final long unknownWords;
	public final long nynorskWords;
	public final long bokmaalWords;
	public final long englishWords;
	public CrawlResultSummary(int numberOfUrls, long unknownWords, long nynorskWords, long bokmaalWords, long englishWords) {
		this.numberOfUrls = numberOfUrls;
		this.unknownWords = unknownWords;
		this.nynorskWords = nynorskWords;
		this.bokmaalWords = bokmaalWords;
		this.englishWords = englishWords;
	}

	
	/*
	public final int unknownPages;
	public final int nynorskPages;
	public final int bokmaalPages;
	public final int englishPages;
	public CrawlResultSummary(int numberOfUrls, int unknownPages, int nynorskPages, int bokmaalPages, int englishPages) {
		this.numberOfUrls = numberOfUrls;
		this.unknownPages = unknownPages;
		this.nynorskPages = nynorskPages;
		this.bokmaalPages = bokmaalPages;
		this.englishPages = englishPages;
	}
	*/


}
