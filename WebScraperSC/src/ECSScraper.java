/**
 *
 * @author Joe
 */
import java.io.*;
import java.net.URL;

public class ECSScraper {
    
    static String username;
    static String urlPrefix = "http://www.ecs.soton.ac.uk/people/"; 
    static URL targetURL;
    static String[] properties = {"name","jobTitle","telephone","email"};
    static String[] values = new String[4];
    
    public static void main(String[] args) throws Exception{
        getUsername();
        
        //Produce URL from username and prefix
        targetURL = new URL(urlPrefix+username);
        
        scrapeData(targetURL);
        
        outputResults();
    }
    
    private static void getUsername() throws Exception {
        
        System.setProperty("jsse.enableSNIExtension", "false");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("Enter username...");    
        username = br.readLine();
    }
    
    private static void scrapeData(URL url) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        
        //Search through HTML for relevant properties, storing the text
        String inputLine;
        
        //keeps track of which properties are not found for reporting
        boolean[] foundProperty = {false,false,false,false};
        
        while ((inputLine = in.readLine()) != null) {
            
            //Find each property in the input

            for (int i = 0; i < properties.length; i++) {

                //For the first two properties quotations marks are used. For the last two, apostrophes are used. Branch controls which punctuation is search for
                if (i < 2) {
                    String phrase = "property=\"" + properties[i] + "\"";
                    if (inputLine.contains(phrase)) {   
                        foundProperty[i] = true;

                        retrieveData(i,phrase, inputLine);
                    }
                } else {
                    String phrase = "property='" + properties[i] + "'";
                    if (inputLine.contains(phrase)) {   
                        foundProperty[i] = true;

                        retrieveData(i,phrase, inputLine);
                    }
                }
                
                
            }
        }
        
        for (int i = 0; i < 4; i++) {
            if (!foundProperty[i]) {
                System.out.println("Could not find " + properties[i] + " property \n");
            }
        }
        
    }
    
    private static void outputResults() {
        System.out.println("The username '" + username + "' has returned the following details...\n");
        
        System.out.println("Name: " + values[0]);
        System.out.println("Job Title: " + values[1]);
        System.out.println("Telephone: " + values[2]);
        System.out.println("E-mail: " + values[3]);
    }
    
    private static void retrieveData(int propertyNumber, String phrase, String input) {
        
        //Find the position of the phrase in the line
        int phraseIndex = input.indexOf(phrase);
        //Substring from the end of the phrase(+1 to skip the > character) to the next < character
        String value = input.substring(phraseIndex+phrase.length()+1, input.indexOf("<", phraseIndex));
        
        values[propertyNumber] = value;
    }
    
}
