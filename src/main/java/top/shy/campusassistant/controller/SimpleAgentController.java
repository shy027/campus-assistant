package top.shy.campusassistant.controller;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.web.bind.annotation.*;

/**
 * @author 15331
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SimpleAgentController {

//    private final ReactAgent reactAgent;
//
//    @GetMapping("/chat")
//    public String chat(@RequestParam(value = "question", defaultValue = "图书馆在哪里") String question) {
//        AssistantMessage response;
//        try {
//            response = reactAgent.call(question);
//        } catch (GraphRunnerException e) {
//            throw new RuntimeException(e);
//        }
//        return response.getText();
//    }
}
