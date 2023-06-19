package vn.com.lifesup.hackathon.dto.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BaseDTO implements Serializable {
    private Long totalPage;
    private Long totalRow;
    private Integer page;
    private Integer pageSize;
    private String keyword;

//    @JsonIgnore
//    private ActionAuditDto.Builder logBuilder;
}
