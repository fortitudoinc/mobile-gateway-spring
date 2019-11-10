package hello;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

@RestController
public class Controller {

    @GetMapping("/fetch")
    public String fetchUuid() {
    final String uri = "https://openmrs-cng-staging.homefry.tk/openmrs/module/idgen/generateIdentifier.form?source=1&username=admin&password=Admin123"; 
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(uri, String.class);
    return result;
    }

}