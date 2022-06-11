package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class Category {
    private Integer id;
    private String title;
    private Integer id_group;
}
