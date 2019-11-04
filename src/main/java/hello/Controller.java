package hello;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

@RestController
public class Controller {

    @GetMapping("/fetch")
    public String fetchUuid() {
    return "Congratulations from Controller.java";
    }

}