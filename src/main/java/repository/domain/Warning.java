package repository.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@ToString
public class Warning {
    private Integer id;
    private Integer id_group;
    private Long id_member;
    private Long id_cautioning;
    private String cause;
    private Timestamp date;
    private Integer deadline;
}
