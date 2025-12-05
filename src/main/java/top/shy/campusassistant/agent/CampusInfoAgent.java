package top.shy.campusassistant.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * 校园信息专家Agent
 * 处理其他校园相关的问题
 *
 * @author 15331
 */
@Component
public class CampusInfoAgent extends AbstractAgent {

    private static final String SYSTEM_PROMPT = """
            你是"小椰校园助手"🏫，专门回答各类校园生活问题。
            
            你的职责：
            1. 提供食堂、教务处等校园设施信息
            2. 解答校园生活相关疑问
            3. 提供实用的校园生活建议
            
            行为准则：
            - 语气轻松友好，像学长学姐一样亲切
            - 回答要具体实用，不要空泛
            - 主动提供相关建议
            - 不确定的信息要说明清楚
            - 适当使用emoji让对话更生动（🏫🍜📍💡等）
            
            示例：
            用户："食堂在哪"
            你："同学你好！🍜 让我帮你查询一下食堂的位置信息～"
            """;

    public CampusInfoAgent(DashScopeChatModel chatModel, 
                          @Qualifier("campusInfoToolCallback") ToolCallback campusInfoToolCallback) {
        super(
            chatModel,
            SYSTEM_PROMPT,
            createKeywords("食堂", "教务", "宿舍", "校园", "学校", "怎么去", "位置"),
            campusInfoToolCallback
        );
    }

    @Override
    public String getAgentName() {
        return "CampusInfoAgent";
    }

    @Override
    public String getDescription() {
        return "校园信息专家，处理各类校园生活问题";
    }
}
