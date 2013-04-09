package no.domeneparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import javax.swing.JFileChooser;

public class DomeneParser {

	private final Domene domene;
	private final double sleepTime;
	
	private final StorageManager storageManager;

	private final Crawl crawl;

	public static Display display = new Display();
	
	public DomeneParser(String path, String domene, double sleepTime) throws IOException {
		this.sleepTime = sleepTime;
		this.domene = new Domene(domene);
		
		this.storageManager = new StorageManager(path, this.domene.getDomainPart());
	
		display.setVisible(true);
		
		crawl = new Crawl(this.domene, this.sleepTime, storageManager.getLinkList(), storageManager.getParsedList());	
		crawl.start();

		CrawlSaver saver = new CrawlSaver();
		saver.start();
		
	}
	
	private class CrawlSaver extends Thread {
		public void run() {
			while(true) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
				}
		
				if(crawl.isDone()) {
					storeCrawlResults(crawl);					
					System.exit(0);
				} else {
					tmpStoreCrawlResults(crawl);
				//	report(crawl);
				}
			}
		}
	}

	public void storeCrawlResults(Crawl crawl) {
		try {
			storageManager.saveCrawl(crawl);
			crawl.setLastSave(new Date(System.currentTimeMillis()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}

	public void tmpStoreCrawlResults(Crawl crawl) {
		try {
			storageManager.saveCrawlTmp(crawl);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		crawl.setLastSave(new Date(System.currentTimeMillis()));		
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Velg hvor resultatet skal lagres");
		chooser.setApproveButtonText("Velg katalog");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int res = chooser.showSaveDialog(null);
		if(res != JFileChooser.APPROVE_OPTION) {
			System.exit(0);
		}
		
		File f = chooser.getSelectedFile();
			
		InputDialog dia = new InputDialog();
		dia.setVisible(true);
		
		if(dia.isCanceled()) {
			System.exit(0);
		}
		String domene = dia.getDomene();
		double sleepTime = dia.getSleepTime();
		
		/*
		String domene = "";
		double sleepTime = 1.0;
		if(args.length == 1) {
			domene = args[0];
		} else if(args.length == 2) {
			domene = args[0];
			sleepTime = Double.parseDouble(args[1]);
		}
		
		if(!domene.equals("")) {
			System.out.println("Starter parsing av domene "+ domene);
		} else {
			System.out.println("Bruk\n");
			System.out.println("java -jar domeneparser.jar domene intervall\n");
			System.out.println("Eks: java -jar domeneparser.jar sprakrad.no 0.5\n");
			System.exit(0);
		}
		*/
		
		try {
			new DomeneParser(f.getAbsolutePath(), domene, sleepTime);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
