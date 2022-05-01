package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Builder
@Getter
@Setter
public class Warning {
    Integer id;
    Integer id_group;
    Integer id_member;
    Integer id_cautioning;
    String cause;
    Date date;
    Integer deadline;
}
