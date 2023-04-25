package com.assignment.dataprocessing;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.assignment.commonutils.Constants;
import com.assignment.models.AbsractAndAuthorInformation;


/**
 * 
 * files are begin written in the the main directory, target folder
 *
 */

public class AssignmentDataProcessing {
	
	/**
	 * in prodicstions these values should be in static centralized cache so when 
	 * we rebuild the cache it does not initialize the map again so the code 
	 * execution cost will be minimized
	 */
	public static HashMap<String, String> postThatAreTakenAlready=new HashMap<String, String>();
	
	/**
	 * putting sychronized so that if it takes long time to execute
	 * this method could not called again at the same time.
	 */
	public synchronized  void processData()
	{
		 /**
         * for now we are using console out. but while on
         * project or on productions we use log4j for logging.
         */
		System.out.println("-Starting to Process Post-");
		mainProcesing();
	}
	
	public static void mainProcesing() {
   
        try {
        	 
             Document document = Jsoup.connect(Constants.openAiUrl).get();
             Elements posts = document.select(".group-item");
           
            int count =0;
             for (Element post : posts) {
            	 count=count+1;
                 String date = post.select(".cols-container > div:first-child").text();
                 String title = post.select(".cols-container > div:nth-child(2) > a").text();
                 String linktoGetAbstractAndAuthors = post.select(".cols-container > div:nth-child(3) > a").attr("href");
               
                
                 
                 /**
                  * checking whether the post is taken already or not.
                  * For filters just like APIS filters it easy to filter
                  * and performance vise its better to have filters to get only updated data
                  * but here i tried to searched i could not fget anything so i tried with custom logic
                  */
                 if(!postThatAreTakenAlready.containsKey(title))
	              {
                	 /**
                      * for now we are using console out. but while on
                      * project we use log4j for logging.
                      */
                     System.out.println("Title: " + title);
                     System.out.println("Date: " + date);
                     System.out.println("link: " + linktoGetAbstractAndAuthors);
	                 AbsractAndAuthorInformation information =new AbsractAndAuthorInformation();
	                 if(linktoGetAbstractAndAuthors != null && ! linktoGetAbstractAndAuthors.isEmpty())
	                 {
	                	  information=AssignmentDataProcessing.gettingAuthorInformation(linktoGetAbstractAndAuthors);
	                 }
	                 else
	                 {
	                	  System.out.println("Emptry link so cannot get authors or abstract"); 
	                 }
	                 AssignmentDataProcessing assignmentProcesing=new AssignmentDataProcessing();
	                 assignmentProcesing.writingInformationToFile(title, date, information);
	             }
                 else
                 {
                	 System.out.println("All the posts already Taken , No new post is there: "+title);
                 }
             }
             
        } catch (IOException e) {
            e.printStackTrace();
        }
  
	}
	
	/**
	 * this method is taking inout as url (Read paper link from https://openai.com/research)
	 * and then getting authors names and abstract from this paramert (url)
	 * @param url
	 */
	public static AbsractAndAuthorInformation gettingAuthorInformation( String url)
	{
		HashMap<String, ArrayList<String>> mapOfAuthors=new HashMap<>();
		ArrayList<String> authorsLink=new ArrayList<String>();
		 
		 /**
         * for now we are using console out. but while on
         * project we use log4j for logging.
         */
	         System.out.println("-gettingAuthorInformation-method-url: "+url);
	        try {
	        	/**
	        	 * we see that some of the urls are with pdf, so we have just put the 
	        	 * check here. but ideally we can have other extends that ignore like .txt, .xml
	        	 * and we can put all the types in configruations.
	        	 */
	        
	          if(!url.contains("pdf"))
	          {
	             Document document = Jsoup.connect(url).get();
	             Element authorDiv = document.selectFirst(".authors");
	             Elements authorLinks = authorDiv.select("a");

	             ArrayList<String> authorsList=new ArrayList<>();
	             
	             for (Element authorLink : authorLinks) {
	                 String authorName = authorLink.text();
	                 String authorLinkUrl = authorLink.absUrl("href");
	                 System.out.println("Author Name: " + authorName);
	                 authorsList.add(authorName);
	                 System.out.println("Author Link: " + authorLinkUrl);
	                authorsLink=authorNameFromGooglr(authorName);
	                System.out.println("Author authorsLink Size: " + authorsLink.size());
	                mapOfAuthors.put(authorName, authorsLink);
	             }
	             
	             String abstractText = document.select("meta[name=citation_abstract]").attr("content");
	             AbsractAndAuthorInformation absractAndAuthorInformation=new AbsractAndAuthorInformation(abstractText, mapOfAuthors);
	         
	             return absractAndAuthorInformation;
	          } 
	             
	             /**
	              * As discussion cal i was trying to implement the first and last paragraph 
	              * but in email it was not mentioned so i skipped it
	              */
					/*
					 * if(abstractText.contains("<p")) { Elements pElems =
					 * Jsoup.parseBodyFragment(abstractText).select("p"); System.out.
					 * println("-------------------yes contains paragrapgh-----------------------------------------"
					 * ); System.out.println("-------------------url--------"+url); String lastPara
					 * = pElems.last().text(); } else { String firstParagraph =
					 * document.select("meta[name=citation_abstract]").attr("content");
					 * System.out.println("---------------we have only paragragh---");
					 * System.out.println("----firstParagraph--:"+firstParagraph); }
					 */
	       
	        } catch (IOException e) {
	            e.printStackTrace();
	        }  
	        return null;
	       
	}
	
	/**
	 * Writing files according to the instructions. Atlast the author names are written in one line
	 * After that for each author we have written 5 links in new line.
	 * We can write the files in any locations but for now the path is just inside the main project folder.
	 * 
	 * 5.1- File name should be title of the post.
	   5.2- Write Abstract as is from the post to file.
	   5.3- After Abstract, leave an empty line and write all author names.  After that write all of the links you got for each author.
	
	 * @param title
	 * @param date
	 * @param information
	 */
	public void writingInformationToFile(String title, String date,  AbsractAndAuthorInformation information)
	{
           try {
        	   
				/*
				 * Path source = Paths.get(this.getClass().getResource("/").getPath()); Path
				 * newFolder = Paths.get(source.toAbsolutePath() + "/files/");
				 * Files.createDirectories(newFolder);
				 */
        	   
		      System.out.println("writingInformationToFile---"+ title);
		        String thing = title +"\r\n"+date +"\r\n";
		        String authorsLins ="";
		       if(information !=null)
		       {
		    	   thing= thing+information.getAbrstract() +"\r\n";
		    	   for ( String key : information.getAuthors().keySet() ) {
		    		   thing=thing+key+",";
		    		   if(information.getAuthors().get(key) != null)
		    		   {
		    		    ArrayList<String> listInformation = information.getAuthors().get(key);
		    		    String authorsInsdie= listInformation.stream().collect(Collectors.joining(","));
		    		   
		    		    if(authorsLins !=null && authorsLins.length() >0) {
	    	              authorsLins=authorsLins+"\r\n"+authorsInsdie;
		    		    }
		    		    else
		    		    {
		    		    	  authorsLins=authorsLins+authorsInsdie;
		    		    }
		    		  
		    		   }
		    		   
		    		}
		       }
		       
		       System.out.println("authorsLins-- for Files----"+authorsLins);
		       if(authorsLins !=null && authorsLins.length() >0)
		       {
		       thing=thing+"\r\n"+authorsLins;
		       }
		       String titleTrimmed=title;
		       if(titleTrimmed.contains(":"))
		       {
		    	   titleTrimmed=title.replaceAll(":", "");
		       }
		       
		       System.out.println("atitleTrimmed--------"+titleTrimmed);
		        FileWriter myWriter = new FileWriter(titleTrimmed.trim()+".txt");
		        myWriter.write(thing);
		        myWriter.close();
		        System.out.println("Successfully wrote to the file.");
		        postThatAreTakenAlready.put(title, date);
		      } catch (IOException e) {
		        System.out.println("An error occurred.");
		        e.printStackTrace();
		      }
		    		
	}
	 public static ArrayList<String> authorNameFromGooglr(String authorName) throws IOException {
	        // Set the search query
	         authorName = authorName+Constants.suffixForAuthorNameforSearch;
             ArrayList<String> authorsLinks=new ArrayList<String>();
             
	        // Build the Google search URL
	        String url = Constants.googleAuthorsSearch + authorName.replace(" ", "+");
            System.out.println("url GOOGLE "+url);
	        // Fetch the search results page
	        Document doc = Jsoup.connect(url).get();

	        // Extract the search results
	        Elements results = doc.select("div.g");

	        // Print the first five search result links
	        int count = 0;
	        for (Element result : results) {
	            // Get the link element
	            Element link = result.select("a[href]").first();
	            if (link != null) {
	                // Get the link URL and title
	                String linkUrl = link.attr("href");
	                String linkTitle = link.text();

	                // Print the link URL and title
	                System.out.println("----linkUrl----" + " - " + linkUrl);
	                authorsLinks.add(linkUrl);

	                // Increment the count
	                count++;
	                if (count == 5) {
	                    break;
	                }
	            }
	        }
	        return authorsLinks;
	    }

}
