package top.shy.campusassistant.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.BiFunction;
/**
 * @author 15331
 */
@Component
public class LibrarySeatTool implements BiFunction<String, ToolContext, String> {

    @Override
    public String apply(
            @ToolParam(description = "图书馆区域，如：A区、B区、C区") String area,
            ToolContext toolContext) {

        Random random = new Random();
        int totalSeats = 200;
        // 20-170 之间的随机数
        int occupied = random.nextInt(150) + 20;
        int available = totalSeats - occupied;

        return String.format("{ \"area\": \"%s\", \"totalSeats\": %d, \"occupied\": %d, \"available\": %d, \"availabilityRate\": \"%.1f%%\" }",
                area, totalSeats, occupied, available, (available * 100.0 / totalSeats));
    }
}
