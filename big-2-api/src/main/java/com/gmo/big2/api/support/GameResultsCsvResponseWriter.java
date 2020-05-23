package com.gmo.big2.api.support;

import com.gmo.playing.cards.GameResult;
import com.gmo.playing.cards.GameResults;
import com.gmo.playing.cards.PlayerScore;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Writes {@link GameResults} in csv format to a provided {@link Writer}
 */
public final class GameResultsCsvResponseWriter {
    private static final String[] FLAT_COLUMN_HEADERS = {"game_completed_epoch_ms", "player_name", "player_score"};
    private static final String[] WIDE_COLUMN_HEADERS = {
            "game_completed_epoch_ms", "winner",
            "player1", "player1_score",
            "player2", "player2_score",
            "player3", "player3_score",
            "player4", "player4_score" };

    /**
     * Writes {@link GameResults} in csv format with one row per player per game, to a provided {@link Writer} in
     * the format:
     *
     * game_completed_epoch_ms, player_display_name, player_score
     *
     * Sorted by game_completed_epoch_ms, player_score desc
     *
     * @param writer {@link Writer} to send output stream
     * @param results {@link GameResults} to write
     */
    public static void writeFlattenedResponse(final Writer writer, final GameResults results) throws IOException {
        final CSVWriter csvWriter = new CSVWriter(writer);

        csvWriter.writeNext(FLAT_COLUMN_HEADERS);
        for (final GameResult result : results.getResults()) {
            result.getPlayerScores()
                    .stream()
                    .sorted(Comparator.comparingInt(PlayerScore::getScore).reversed())
                    .forEachOrdered(playerScore ->
                            csvWriter.writeNext(new String[]{
                                    String.valueOf(result.getGameCompletedInstant().toEpochMilli()),
                                    playerScore.getPlayer().getName(),
                                    String.valueOf(playerScore.getScore())
                            }));
        }

        csvWriter.flush();
    }

    /**
     * Writes {@link GameResults} in csv format with one row per game, to a provided {@link Writer} in the format:
     *
     * game_completed_epoch_ms, winner, player1, player1_score, player2, player2_score, player3, player3_score, player4, player4_score
     *
     * Sorted by game_completed_epoch_ms
     *
     * Players in each row are sorted alphabetically, such that games with the same set of players will
     * always be ordered predictably.
     *
     * @param writer {@link Writer} to send output stream
     * @param results {@link GameResults} to write
     */
    public static void writeWideResponse(final Writer writer, final GameResults results) throws IOException {
        final CSVWriter csvWriter = new CSVWriter(writer);

        csvWriter.writeNext(WIDE_COLUMN_HEADERS);
        for (final GameResult result : results.getResults()) {
            final List<String> values = new ArrayList<>();
            values.add(String.valueOf(result.getGameCompletedInstant().toEpochMilli()));
            values.add(result.getPlayerScores()
                    .stream()
                    .max(Comparator.comparingInt(PlayerScore::getScore))
                    .map(ps -> ps.getPlayer().getName())
                    .orElse(""));

            result.getPlayerScores().stream()
                    .sorted(Comparator.comparing(ps -> ps.getPlayer().getName()))
                    .forEachOrdered(playerScore ->  {
                        values.add(playerScore.getPlayer().getName());
                        values.add(String.valueOf(playerScore.getScore()));
                    });

            csvWriter.writeNext(values.toArray(new String[0]));
        }
        csvWriter.flush();
    }
}
