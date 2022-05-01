package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Builder
@Getter
@Setter
public class File {
    Integer id;
    Integer id_group;
    Integer id_category;
    String title;
    Date data_create;
}
