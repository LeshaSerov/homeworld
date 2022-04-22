package domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Member {
    Integer id;
    String first_name;
    String last_name;
    String user_name;
    Role role = null;

    public Boolean isEmptyRole(){
        return role == null;
    }
}
