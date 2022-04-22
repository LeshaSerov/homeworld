package domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MemberInChat {
    Integer id_chat;
    Integer id_member;
}
