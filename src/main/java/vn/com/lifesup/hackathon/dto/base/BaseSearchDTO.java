package vn.com.lifesup.hackathon.dto.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
public class BaseSearchDTO {
    @ApiModelProperty(notes = "Trang", example = "0")
    private Integer page;
    @ApiModelProperty(notes = "Số bản ghi 1 trang", example = "10")
    private Integer pageSize;
    @ApiModelProperty(notes = "Từ khóa tìm kiếm nhiều trường", example = "")
    private String keyword;
}
