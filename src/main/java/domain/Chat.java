package domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Chat {
    Integer id;
    String name;
    String address;
    String link;
    Boolean can_ping;
}
