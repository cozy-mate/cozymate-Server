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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mss_seq_gen")
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

    @PrePersist
    void onPrePersist() {
        if (viralCode == null) {
            long mixed = mix(id); // 랜덤함을 보이기 위한 섞기.
            this.viralCode = toBase57Fixed(mixed, 6);
        }
    }

    private static final char[] ALPHABET = (
        "ABCDEFGHJKMNPQRSTUVWXYZ" +
            "abcdefghijkmnpqrstuvwxyz" +
            "123456789"
    ).toCharArray();
    private static final int RADIX = ALPHABET.length;

    private static String toBase57Fixed(long n, int width) {
        StringBuilder sb = new StringBuilder();
        do {
            long rem = Long.remainderUnsigned(n, RADIX);
            sb.append(ALPHABET[(int) rem]);
            n = Long.divideUnsigned(n, RADIX);
        } while (Long.compareUnsigned(n, 0L) > 0);
        while (sb.length() < width) {
            sb.append(ALPHABET[0]);
        }
        return sb.reverse().toString();
    }

    private static long mix(long x) {
        x ^= 0x5A17_2025CAFEL;
        x *= 0x9E3779B97F4A7C15L;
        x ^= (x >>> 33);
        x *= 0xC2B2AE3D27D4EB4FL;
        x ^= (x >>> 29);
        return x;
    }
}
