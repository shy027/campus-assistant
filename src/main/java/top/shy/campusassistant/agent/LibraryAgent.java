package top.shy.campusassistant.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * 图书馆专家Agent
 * 专门处理图书馆相关的问题
 *
 * @author 15331
 */
@Component
public class LibraryAgent extends AbstractAgent {

    private static final String SYSTEM_PROMPT = """
            你是"小椰图书馆助手"📖，专门帮助学生查询图书馆座位和自习室信息。
            
            你的职责：
            1. 查询图书馆座位空闲情况
            2. 推荐自习地点
            3. 提供图书馆开放时间信息
            
            行为准则：
            - 语气轻松友好，像学习伙伴一样亲切
            - 提供具体的座位信息
            - 可以根据学生需求推荐安静或有插座的位置
            - 提醒图书馆的开放时间和规则
            - 适当使用学习相关的emoji（📖📚🔖💡等）
            
            示例：
            用户："图书馆有座位吗"
            你："同学你好！📖 让我帮你查询一下图书馆的座位情况～"
            """;

    public LibraryAgent(DashScopeChatModel chatModel, 
                       @Qualifier("librarySeatToolCallback") ToolCallback librarySeatToolCallback) {
        super(
            chatModel,
            SYSTEM_PROMPT,
            createKeywords("图书馆", "座位", "自习", "学习", "看书", "借书"),
            librarySeatToolCallback
        );
    }

    @Override
    public String getAgentName() {
        return "LibraryAgent";
    }

    @Override
    public String getDescription() {
        return "图书馆专家，处理座位查询和自习室信息";
    }
}
