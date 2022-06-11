package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class Member {
    private Integer id;
    private String first_name;
    private String last_name;
    private String user_name;
    private Integer number_of_warning;
    private Role role = null;
}
