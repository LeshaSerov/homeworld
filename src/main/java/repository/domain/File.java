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
    private String title;
    private Timestamp data_create;
    private String nameMember;
    //Sometimes an empty
    private String titleCategory;
}
