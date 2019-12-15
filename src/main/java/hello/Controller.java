package hello;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import java.util.*;
import java.io.*;
import com.google.gson.*;
import org.springframework.http.*;

@RestController
public class Controller {

    private static JsonParser prsr = new JsonParser();
    final private static String[] RequiredEnv = {"FETCH_URL", "REGISTER_URL", "MOBILE_USERNAME", "MOBILE_PASSWORD"};
    //String fetchUrl = "https://openmrs-cng-staging.homefry.tk/openmrs/module/idgen/generateIdentifier.form?source=1&username=admin&password=Admin123";
    //String registerUrl= "https://openmrs-cng-staging.homefry.tk/openmrs/ws/rest/v1/patient";
    private String fetchUrl;
    private String registerUrl;
    static RestTemplate restTemplate = new RestTemplate();

    public Controller() {
        Map<String, String> systemEnv = System.getenv();

        // Check that we have what we need & fail fast
        for(String requiredVar : RequiredEnv) {
            if(!systemEnv.containsKey(requiredVar)){
                throw new NullPointerException("Could not find required environment variable: " + requiredVar);
            }
        }

        // Build URLs from env
        fetchUrl = System.getenv("FETCH_URL") + "?source=1&username=" +
                System.getenv("MOBILE_USERNAME") + "&password=" + System.getenv("MOBILE_PASSWORD");

        registerUrl = System.getenv("REGISTER_URL");
    }
    
    @GetMapping("/fetch")
    public String fetchUuid() {
    String result = restTemplate.getForObject(fetchUrl, String.class);
    return result;    
    }

    public static Boolean matchPhoneNumbers(JsonArray arr, String newNum)
    {
        for (JsonElement resultElement : arr) {
            JsonArray attributes = resultElement.getAsJsonObject().getAsJsonObject("person")
                              .getAsJsonArray("attributes");
            if(attributes.size()>0){                  
            String phonenum=attributes.get(0).getAsJsonObject()
                              .get("display").getAsString();
            String s=phonenum.substring(19);
        
            if(s.equals(newNum))
            return false;

            System.out.println(s);
        }   
        }
        return true;  
    }

    public static JsonArray getDuplicates(String name)
    {
    String fetchUrl2 = "https://openmrs-cng-staging.homefry.tk/openmrs/ws/rest/v1/patient?v=default&q="+name;
    HttpHeaders headers=getHeaders();
    HttpEntity<String> request = new HttpEntity<String>(headers);
    ResponseEntity<String> response = restTemplate.exchange(fetchUrl2, HttpMethod.GET, request, String.class);
    String str = response.getBody();  
    System.out.println(str);
    JsonElement jsEl = prsr.parse(str);
    System.out.println("Phone Numbers");
    JsonObject obj = jsEl.getAsJsonObject();
    JsonArray resultArray = obj.getAsJsonArray("results");
    return resultArray;
    }
    
    public static HttpHeaders getHeaders()
    {
    String plainCreds = "admin:Admin123";
    String plainString 
            = Base64.getEncoder() 
                  .encodeToString(plainCreds.getBytes());
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + plainString);
    return headers;   
    }

    @PostMapping("/register")
    public ResponseEntity < String > register(@RequestBody String body) {
        JsonElement jsEl = prsr.parse(body);
        JsonObject person = jsEl.getAsJsonObject().getAsJsonObject("person");
        JsonArray names = person.getAsJsonArray("names");
        JsonArray attributes = person.getAsJsonArray("attributes");
        String firstName = names.get(0).getAsJsonObject().get("givenName").getAsString();
        String phoneNum = attributes.get(0).getAsJsonObject().get("value").getAsString();
        System.out.println(body);
        System.out.println("FIRST_NAME = "+firstName);
        System.out.println("PHONE_NUM = "+phoneNum);
        JsonArray arr = getDuplicates(firstName);
        Boolean check= matchPhoneNumbers(arr,phoneNum);
         
        if(!check){ 
            System.out.println("Duplicate Registration");        
            return new ResponseEntity < String > ("Duplicate Registration", HttpStatus.CONFLICT);
        }
        HttpHeaders headers=getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> request = 
        new HttpEntity<String>(body, headers);
     
        ResponseEntity<String> response = restTemplate.
        postForEntity(registerUrl, request, String.class);
        //HttpEntity<String> request = new HttpEntity<String>(headers);
        //ResponseEntity<String> response = restTemplate.exchange(registerUrl, HttpMethod.POST, request, String.class,body);
        return response;
    }
}