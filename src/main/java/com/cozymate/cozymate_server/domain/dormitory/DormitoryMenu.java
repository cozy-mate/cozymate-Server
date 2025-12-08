package com.cozymate.cozymate_server.domain.dormitory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "dormitory_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Immutable
public class DormitoryMenu {

    @Id
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime createdAt;

    private String monBreakfast;
    private String monLunch;
    private String monDinner;
    private String tueBreakfast;
    private String tueLunch;
    private String tueDinner;
    private String wedBreakfast;
    private String wedLunch;
    private String wedDinner;
    private String thuBreakfast;
    private String thuLunch;
    private String thuDinner;
    private String friBreakfast;
    private String friLunch;
    private String friDinner;
    private String satBreakfast;
    private String satLunch;
    private String satDinner;
    private String sunBreakfast;
    private String sunLunch;
    private String sunDinner;
}