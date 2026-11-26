package ch.no1hardy.service.mapper;

import ch.no1hardy.service.front.group.JokerQuota;
import ch.no1hardy.service.model.group.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class GroupMapperTest {
    @Autowired
    private GroupMapper groupMapper;

    @MockitoSpyBean
    public UserHelper userHelper;

    @BeforeEach
    void beforeEach() {
        Mockito.doReturn(10).when(userHelper).getRemainingDoubleJokers();
        Mockito.doReturn(6).when(userHelper).getRemainingTripleJokers();
        Mockito.doReturn(10).when(userHelper).getDoubleJokerLimit();
        Mockito.doReturn(6).when(userHelper).getTripleJokerLimit();
    }

    @Test
    @DisplayName("toDto(Group) - should map remaining double jokers")
    void toDto01() {
        JokerQuota quota = groupMapper.toDto(new Group()).jokerQuota();
        assertThat(quota.remainingDoubleJokers()).isEqualTo(10);
        Mockito.verify(userHelper, Mockito.times(1)).getRemainingDoubleJokers();
    }

    @Test
    @DisplayName("toDto(Group) - should map remaining triple jokers")
    void toDto02() {
        JokerQuota quota = groupMapper.toDto(new Group()).jokerQuota();
        assertThat(quota.remainingTripleJokers()).isEqualTo(6);
        Mockito.verify(userHelper, Mockito.times(1)).getRemainingTripleJokers();
    }

    @Test
    @DisplayName("toDto(Group) - should map double joker limit")
    void toDto03() {
        JokerQuota quota = groupMapper.toDto(new Group()).jokerQuota();
        assertThat(quota.doubleJokerLimit()).isEqualTo(10);
        Mockito.verify(userHelper, Mockito.times(1)).getDoubleJokerLimit();
    }

    @Test
    @DisplayName("toDto(Group) - should map triple joker limit")
    void toDto04() {
        JokerQuota quota = groupMapper.toDto(new Group()).jokerQuota();
        assertThat(quota.tripleJokerLimit()).isEqualTo(6);
        Mockito.verify(userHelper, Mockito.times(1)).getTripleJokerLimit();
    }
}
