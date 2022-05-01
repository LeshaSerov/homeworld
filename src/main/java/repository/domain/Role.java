package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Role {
    Integer id;
    Boolean right_to_view;
    Boolean right_ping;
    Boolean right_edit;
    Boolean right_admin;
}