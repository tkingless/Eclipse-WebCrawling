package com.tkk.webCrawling.webCrawler;
import java.io.IOException;
import java.lang.String;
import java.text.ParseException;
import java.util.*;

import com.tkk.webCrawling.CSVmanager;
import com.tkk.webCrawling.Crawlee;
import com.tkk.webCrawling.FileManager;

import org.apache.commons.collections4.*;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.csv.*;

public abstract class BaseCrawler extends Thread {

	public enum CrawlingStates {
		STATE_PARSE_IN_CONFIG,
		STATE_PROCESS_URL,
		STATE_ANALYSE_CONTENT,
		STATE_SEARCH_CRIT_FILTER,
		STATE_POSTPROCESS
	}

	/*
	 * Binding of Crawler object (programming logical concept) with a key Since
	 * there is no reason for a strange web key to tell program run
	 */
	protected enum CrawlerKeyBinding {
		TutorGroup, ECTutor, L4Tutor
	}

	String CONFIG_FILE = "config.csv";
	protected String CRIT_SUBJECT_KEY = "WC_SEARCH_CRIT";
	protected String CRIT_LOCATION_KEY = "WC_SEARCH_OUT_CRIT";
	protected String CRIT_PRICE_KEY = "WC_SEARCH_COND_PRICE_ABOVE";
	protected String[] config_header_mapping = { "WEB_KEY", "TYPE", "VALUE" };

	protected BaseCrawler.CrawlingStates mState;
	protected List<Crawlee> crawlees = new ArrayList<Crawlee>();

	/*
	 * Need it be protected?
	 */
	protected MultiMap<String, String> config = new MultiValueMap<String, String>();
    protected String threadName = "default";
    protected Thread thread;
    
	protected BaseCrawler (CrawlerKeyBinding id) {
		System.out.println("[BaseCrawler] constructed called and parse in config");
		ParseInResultAction(id);
		if(thread == null){
			thread = new Thread(this, threadName);
			System.out.println("[thread] thread, " + threadName + " created.");
		}
	}
	
	public void StartRun() {
		if(thread != null){
			thread.start(); //jump to the run function to see what to do
		}else{
			System.err.println("thread not initialized");
		}
	}
	
	protected void ParseInResultAction(CrawlerKeyBinding id) {
		mState = CrawlingStates.STATE_PARSE_IN_CONFIG;
		
		String Key = id.toString();
		
		System.out.println("[ParseInResultAction] Key is: "+ Key);
		
		//no need to protect csvHdr, as it is just local variable
		try {
			FileManager csvHdr = new CSVmanager(CONFIG_FILE);
			List<CSVRecord> csvRecords = ((CSVmanager) csvHdr).CreateParseInRecord(config_header_mapping);

			for (int i = 1; i < csvRecords.size(); i++) {
				CSVRecord record = csvRecords.get(i);

				String webKey = record.get(config_header_mapping[0]);
				String key = record.get(config_header_mapping[1]);
				String val = record.get(config_header_mapping[2]);

				if ( Key.equals(webKey)){
					System.out.println("[Apache] apache commons csv here, The WebKey: " + webKey + ", TYPE: " + key
							+ " and the VALUE: " + val);

					config.put(key, val);
				}
			}
			csvHdr.Close();
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	public List<Crawlee> getCrawlees () {
		return crawlees;
	}

	//TODO: can prevent the throws here?
	protected void ProcessUrlsAction() {
		mState = CrawlingStates.STATE_PROCESS_URL;
	}

	public void AnalyzeContentAction(Crawlee crwl) {
		mState = CrawlingStates.STATE_ANALYSE_CONTENT;
	}

	public void FilterByCritAction() {
		mState = CrawlingStates.STATE_SEARCH_CRIT_FILTER;
	}

	public void PostProcessAction() {
		mState = CrawlingStates.STATE_POSTPROCESS;
	}

}
