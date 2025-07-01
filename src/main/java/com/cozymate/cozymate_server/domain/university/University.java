package com.cozymate.cozymate_server.domain.university;

import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class University extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> mailPatterns;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> departments;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> dormitoryNames;

    public void update(List<String> mailPatterns, List<String> departments, List<String> dormitoryNames) {
        this.mailPatterns = mailPatterns;
        this.departments = departments;
        this.dormitoryNames = dormitoryNames;
    }
}
