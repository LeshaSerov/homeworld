package repository.domain;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
public class Project {
    private Long id;
    private String title;
    private Long id_chat;
}
