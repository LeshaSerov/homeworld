package domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MemberInGroup {
    Integer id_group;
    Integer id_member;
    Integer id_role;
}
