package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LifestyleMatchRate {

    @EmbeddedId
    private LifestyleMatchRateId id;

    @Column(nullable = false)
    private Integer matchRate;

    public LifestyleMatchRate(Long memberA, Long memberB, Integer matchRate) {
        this.id = new LifestyleMatchRateId(memberA, memberB);
        this.matchRate = matchRate;
    }
    public void updateMatchRate(Integer matchRate){
        this.matchRate = matchRate;
    }

    @Embeddable
    @EqualsAndHashCode
    @Getter
    @Builder
    @NoArgsConstructor
    public static class LifestyleMatchRateId implements Serializable {

        @Column(nullable = false)
        private Long memberA;

        @Column(nullable = false)
        private Long memberB;

        public LifestyleMatchRateId(Long memberA, Long memberB) {
            if (memberA > memberB) {
                this.memberA = memberB;
                this.memberB = memberA;
                return;
            }
            this.memberA = memberA;
            this.memberB = memberB;
        }
    }
}
