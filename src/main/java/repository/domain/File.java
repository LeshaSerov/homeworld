package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@ToString
public class File {
    private Integer id;
    private Integer id_group;
    private Integer id_category;
    private String title;
    private Timestamp data_create;
}
