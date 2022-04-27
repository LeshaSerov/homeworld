package domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

public class Warning {
    Integer id_member;
    Integer id_group;
    Integer id_cautioning;
    String cause;
    Date date;
    Integer deadline;
}
