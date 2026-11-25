package ch.no1hardy.service.model.game;

import ch.no1hardy.service.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BetRepository extends ListCrudRepository<Bet, String> {
    /**
     * Sum of the stored Points over all Bets of a user; Bets without Points count as 0.
     */
    @Query("SELECT COALESCE(SUM(b.points), 0) FROM Bet b WHERE b.user = :user")
    int sumPointsByUser(@Param("user") User user);
}
