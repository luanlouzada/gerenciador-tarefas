package br.com.gerenciador.infrastructure.websocket;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin(origins = "https://gerenciador-frontend-five.vercel.app/", allowCredentials = "true")
@RequestMapping("/ws")
public class WebSocketInfoController {

    @GetMapping("/notifications/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSockJSInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("websocket", true);
        info.put("cookie_needed", false);
        info.put("origins", new String[]{"*:*"});
        info.put("entropy", System.currentTimeMillis());

        return new ResponseEntity<>(info, HttpStatus.OK);
    }
}