package domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class File {
    Integer id;
    String name;
    Integer id_group;
    Integer id_category;
    //Date data_create;
    String link;
}
