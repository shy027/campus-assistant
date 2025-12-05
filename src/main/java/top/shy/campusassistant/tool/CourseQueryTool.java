package top.shy.campusassistant.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
@Component
public class CourseQueryTool implements BiFunction<String, ToolContext, List<CourseQueryTool.Course>> {

    @Override
    public List<Course> apply(
            @ToolParam(description = "学生学号") String studentId,
            ToolContext toolContext) {
        //经测试，前端给了学号后，传到这里变成了 {"studentId":"20220001"}，所以就简单处理了一下
        if (studentId.contains("20220001")) {
            return Arrays.asList(
                    new Course("Spring Boot", "08:00-09:40", "教四319"),
                    new Course("Spring AI", "13:30-15:10", "教四419"));
        }
        if (studentId.contains("20220002")) {
            return Arrays.asList(
                    new Course("Spring Boot", "08:00-09:40", "教四319"),
                    new Course("Spring AI", "13:30-15:10", "教四419"));
        }
        return List.of();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Course {
        private String name;
        private String time;
        private String location;
    }
}
