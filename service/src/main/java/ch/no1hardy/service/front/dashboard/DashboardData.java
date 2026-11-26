package ch.no1hardy.service.front.dashboard;

import ch.no1hardy.service.front.game.BetGameRes;
import ch.no1hardy.service.front.leaderboard.RankingRes;
import lombok.Builder;

import java.util.List;

@Builder
public record DashboardData(
        List<RankingRes> leaderboardPreview,
        UserSummary userSummary,
        Statistics stats,
        GlobalStatistics globalStats,
        List<BetGameRes> upcomingGames,
        List<BetGameRes> recentResults
) {
}
