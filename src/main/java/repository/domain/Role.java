package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class Role {
    private Integer id;
    private String title;
    private Boolean right_to_view;
    private Boolean right_ping;
    private Boolean right_edit;
    private Boolean right_admin;
}