package com.gmo.big2.store.game;

import static com.gmo.big2.store.mysql.schema.jooq.tables.Game.GAME;
import static com.gmo.big2.store.mysql.schema.jooq.tables.GamePlayer.GAME_PLAYER;
import static com.gmo.big2.store.mysql.schema.jooq.tables.GamePlayerGroup.GAME_PLAYER_GROUP;
import static com.gmo.big2.store.mysql.schema.jooq.tables.PlayerGroup.PLAYER_GROUP;
import static com.gmo.big2.store.mysql.schema.jooq.tables.PlayerScore.PLAYER_SCORE;
import static com.google.common.base.Preconditions.checkState;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmo.big.two.Big2Game;
import com.gmo.playing.cards.Player;

/**
 * {@link CompletedGameStore} that stores games in MySQL
 */
public class MySqlCompletedGameStore implements CompletedGameStore {
    private DSLContext dslContext;
    private Clock clock;

    public MySqlCompletedGameStore(final DSLContext dslContext, final ObjectMapper objectMapper) {
        this.dslContext = Objects.requireNonNull(dslContext, "Null DSL Context");
        this.clock = Clock.systemUTC();
    }

    @Override
    public void saveFinalGameState(final Big2Game completedBig2Game) {
        checkState(completedBig2Game.isCompleted(), "This store can only save completed games");
        checkState(!dslContext.fetchExists(dslContext.select(GAME.GAME_UUID).from(GAME).where(GAME.GAME_UUID.eq(completedBig2Game.getId()))),
                "Game has already been saved");

        dslContext.transaction(configuration -> {
            final DSLContext transaction = DSL.using(configuration);
            // Insert serialized game into GAME table
            transaction
                    .insertInto(GAME)
                    .columns(GAME.GAME_UUID, GAME.WINNER_PLAYER_UUID, GAME.GAME_COMPLETED_EPOCH_MS)
                    .values(completedBig2Game.getId(),
                            completedBig2Game.getWinner().map(Player::getId).orElseThrow(() -> new RuntimeException("ain't no winner")),
                            clock.instant())
                    .execute();

            // Upsert user group and add mapping to this game
            createGroupMapping(transaction, completedBig2Game);

            // Insert individual scores and update player aggregate score
            completedBig2Game.getScores().forEach((player, score) -> {
                final UUID playerId = player.getId();
                transaction.insertInto(PLAYER_SCORE)
                        .columns(PLAYER_SCORE.PLAYER_UUID, PLAYER_SCORE.SCORE)
                        .values(playerId, 0)
                        .onConflictDoNothing()
                        .execute();
                transaction.update(PLAYER_SCORE)
                        .set(PLAYER_SCORE.SCORE, PLAYER_SCORE.SCORE.add(score))
                        .where(PLAYER_SCORE.PLAYER_UUID.eq(playerId))
                        .execute();
                transaction.insertInto(GAME_PLAYER)
                        .columns(GAME_PLAYER.GAME_UUID, GAME_PLAYER.PLAYER_UUID, GAME_PLAYER.SCORE)
                        .values(completedBig2Game.getId(), playerId, score)
                        .execute();
            });
        });
    }

    private void createGroupMapping(final DSLContext transaction, final Big2Game completedBig2Game) {
        final List<UUID> sortedPlayerIds = completedBig2Game.getPlayers()
                .stream()
                .map(Player::getId)
                .sorted()
                .collect(Collectors.toList());
        final UUID groupUuid;
        final Record1<UUID> fetch = transaction.select(PLAYER_GROUP.PLAYER_GROUP_UUID)
                .from(PLAYER_GROUP)
                .where(PLAYER_GROUP.PLAYER_UUID_1.eq(sortedPlayerIds.get(0)))
                .and(PLAYER_GROUP.PLAYER_UUID_2.eq(sortedPlayerIds.get(1)))
                .and(sortedPlayerIds.size() > 2
                        ? PLAYER_GROUP.PLAYER_UUID_3.eq(sortedPlayerIds.get(2))
                        : PLAYER_GROUP.PLAYER_UUID_3.isNull())
                .and(sortedPlayerIds.size() > 3
                        ? PLAYER_GROUP.PLAYER_UUID_4.eq(sortedPlayerIds.get(3))
                        : PLAYER_GROUP.PLAYER_UUID_4.isNull())
                .fetchOne();
        if (fetch == null) {
            groupUuid = UUID.randomUUID();
            transaction.insertInto(PLAYER_GROUP)
                    .columns(PLAYER_GROUP.PLAYER_GROUP_UUID, PLAYER_GROUP.PLAYER_UUID_1, PLAYER_GROUP.PLAYER_UUID_2, PLAYER_GROUP.PLAYER_UUID_3, PLAYER_GROUP.PLAYER_UUID_4)
                    .values(groupUuid,
                            sortedPlayerIds.get(0),
                            sortedPlayerIds.get(1),
                            sortedPlayerIds.size() > 2 ? sortedPlayerIds.get(2) : null,
                            sortedPlayerIds.size() > 3 ? sortedPlayerIds.get(3) : null)
                    .execute();
        } else {
            groupUuid = fetch.component1();
        }

        transaction.insertInto(GAME_PLAYER_GROUP)
                .columns(GAME_PLAYER_GROUP.GAME_UUID, GAME_PLAYER_GROUP.PLAYER_GROUP_UUID)
                .values(completedBig2Game.getId(), groupUuid)
                .execute();
    }
}
