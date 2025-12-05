package top.shy.campusassistant.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

/**
 * @author 15331
 */
@Component
public class CampusInfoTool implements BiFunction<String, ToolContext, String> {

    @Override
    public String apply(
            @ToolParam(description = "学生的问题，例如：图书馆开放时间、食堂位置等") String query,
            ToolContext toolContext) {

        // 模拟校园知识库
        if (query.contains("图书馆")) {
            return "图书馆在教学楼A座旁边，开放时间是8:00-22:00，周六日正常开放。";
        } else if (query.contains("食堂")) {
            return "学校有三个食堂：第一食堂在宿舍区（6:30-21:30），第二食堂在体育馆旁（7:00-21:00），第三食堂是清真食堂（7:00-20:30）。";
        } else if (query.contains("教务处")) {
            return "教务处位于行政楼3楼，电话：010-12345678。";
        } else {
            return "抱歉，暂时无法回答这个问题。你可以咨询学校官方公众号或拨打校园热线010-12345678。";
        }
    }
}
