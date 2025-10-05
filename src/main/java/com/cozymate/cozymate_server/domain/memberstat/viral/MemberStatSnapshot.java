package com.cozymate.cozymate_server.domain.memberstat.viral;


import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(
    name = "member_stat_snapshot",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_member_stat_snapshot_viral_code",
        columnNames = "viral_code"
    )
)
public class MemberStatSnapshot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "viral_code", length = 6, nullable = false, updatable = false, insertable = false)
    private String viralCode;

    @Embedded
    private Lifestyle lifestyle;

    @Builder(toBuilder = true)
    public MemberStatSnapshot(Lifestyle lifestyle) {
        this.lifestyle = lifestyle;
    }
}
