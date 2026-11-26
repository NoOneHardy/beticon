package ch.no1hardy.service.mapper;

import ch.no1hardy.service.front.admin.PointsChangeRes;
import ch.no1hardy.service.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PointsChangeMapperTest {
    @Autowired
    private PointsChangeMapper pointsChangeMapper;

    @Test
    void shouldMapUserAndOldPointsToPointsChange() {
        User user = new User();
        user.setId("user-1");
        user.setUsername("alice");
        user.setPoints(12);

        PointsChangeRes change = pointsChangeMapper.toPointsChange(user, 7);

        assertEquals("user-1", change.userId());
        assertEquals("alice", change.username());
        assertEquals(7, change.oldPoints());
        assertEquals(12, change.newPoints());
    }
}
