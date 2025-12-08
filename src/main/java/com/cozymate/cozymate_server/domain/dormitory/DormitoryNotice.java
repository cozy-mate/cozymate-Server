package com.cozymate.cozymate_server.domain.dormitory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "dormitory_notice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Immutable
public class DormitoryNotice {

    @Id
    private Long id;

    private String title;

    private String url;

    @Column(name = "is_important")
    private boolean isImportant;

    private LocalDate createdAt;
}