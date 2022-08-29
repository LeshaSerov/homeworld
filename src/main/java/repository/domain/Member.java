package repository.domain;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
public class Member {
    private Long id;
    private String first_name;
    private String last_name;
    private String user_name;
    private Integer number_of_warning;
    Role role = null;

}
