package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.todo.Todo;

@SuppressWarnings("NonAsciiCharacters")
public class RoomLogFixture {

    public RoomLog 룸로그_1(Room room) { // todo와 mateId가 null인 경우
        return RoomLog.builder()
            .room(room)
            .content("{코지메이트 우심방}의 역사적인 하루가 시작됐어요!")
            .todo(null)
            .mateId(null)
            .build();
    }

    public RoomLog 룸로그_2(Room room) { // todo와 mateId가 null인 경우
        return RoomLog.builder()
            .room(room)
            .content("11월의 Best, Worst 코지메이트를 선정해주세요!")
            .todo(null)
            .mateId(null)
            .build();
    }

    public RoomLog 룸로그_3(Room room, Mate mate) { // todo가 null인 경우
        return RoomLog.builder()
            .room(room)
            .content("12월 29일은 우기님의 생일이에요! 모두 축하해주세요!")
            .todo(null)
            .mateId(mate.getId())
            .build();
    }

    public RoomLog 룸로그_4(Room room, Mate mate, Todo todo) {
        return RoomLog.builder()
            .room(room)
            .content("{리원}님이 [개발하기]을/를 완료하여, cozy room을 만드는데 기여했어요!")
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }

    public RoomLog 룸로그_5(Room room, Mate mate, Todo todo) {
        return RoomLog.builder()
            .room(room)
            .content("{리원}님이 [내일투두]을/를 완료했어요!")
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }

    public RoomLog 룸로그_6(Room room, Mate mate, Todo todo) {
        return RoomLog.builder()
            .room(room)
            .content("{눈꽃}님이 [말즈 밥주기]을/를 완료하여, 최고의 cozy mate가 되었어요!")
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }

    public RoomLog 룸로그_7(Room room, Mate mate, Todo todo) {
        return RoomLog.builder()
            .room(room)
            .content("{델로}님이 [팀원들을 칭찬해주기]을/를 까먹은 거 같아요 ㅠㅠ")
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }

    public RoomLog 룸로그_8(Room room) { // todo와 mateId가 null인 경우 + content가 비어있는 경우
        return RoomLog.builder()
            .room(room)
            .content("")
            .todo(null)
            .mateId(null)
            .build();
    }

    public RoomLog 내용이_null인_룸로그(Room room) { // todo와 mateId, content까지 null인 경우
        return RoomLog.builder()
            .room(room)
            .content(null)
            .todo(null)
            .mateId(null)
            .build();
    }

    public RoomLog 다_없는_룸로그() { // 이럴일은 없긴 함
        return RoomLog.builder()
            .room(null)
            .content(null)
            .todo(null)
            .mateId(null)
            .build();
    }
}
