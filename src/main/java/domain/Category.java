package domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Category {
    Integer id;
    String name;
    Boolean public_all;
}
