package vn.com.lifesup.hackathon.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TestDTO {

    Long id;
    String code;
    String name;
    Double value;
    Date date;
}
