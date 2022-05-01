package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Chat {
    Integer id;
    String title;
    Boolean ping;
}
