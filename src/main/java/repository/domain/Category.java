package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Category {
    Integer id;
    String title;
    Integer id_group;
}
