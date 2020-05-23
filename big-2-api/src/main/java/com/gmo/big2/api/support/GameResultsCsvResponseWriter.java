package com.gmo.big2.api.support;

import com.gmo.playing.cards.GameResult;
import com.gmo.playing.cards.GameResults;
import com.gmo.playing.cards.Player;
import com.gmo.playing.cards.PlayerScore;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.Map.Entry;

/**
 * Writes {@link GameResults} in csv format to a provided {@link Writer}
 */
public final class GameResultsCsvResponseWriter {
    private static final String[] COLUMN_HEADERS = {"game_completed_epoch_ms", "player_name", "player_score"};

    /**
     * Writes {@link GameResults} in csv format to a provided {@link Writer} in the format:
     *
     * game_completed_epoch_ms, player_display_name, player_score
     *
     * Sorted by game_completed_epoch_ms, player_score desc
     *
     * @param writer {@link Writer} to send output stream
     * @param results {@link GameResults} to write
     */
    public static void writeResponse(final Writer writer, final GameResults results) throws IOException {
        final CSVWriter csvWriter = new CSVWriter(writer);

        csvWriter.writeNext(COLUMN_HEADERS);
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
}
