package top.shy.campusassistant.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装
 *
 * @author mqxu
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "数据列表")
    private List<T> list;

    public PageResult(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }
}
