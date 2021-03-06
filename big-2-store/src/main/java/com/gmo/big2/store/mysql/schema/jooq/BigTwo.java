/*
 * This file is generated by jOOQ.
*/
package com.gmo.big2.store.mysql.schema.jooq;


import com.gmo.big2.store.mysql.schema.jooq.tables.Game;
import com.gmo.big2.store.mysql.schema.jooq.tables.GamePlayer;
import com.gmo.big2.store.mysql.schema.jooq.tables.GamePlayerGroup;
import com.gmo.big2.store.mysql.schema.jooq.tables.Player;
import com.gmo.big2.store.mysql.schema.jooq.tables.PlayerGroup;
import com.gmo.big2.store.mysql.schema.jooq.tables.PlayerScore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BigTwo extends SchemaImpl {

    private static final long serialVersionUID = 136798334;

    /**
     * The reference instance of <code>big_two</code>
     */
    public static final BigTwo BIG_TWO = new BigTwo();

    /**
     * The table <code>big_two.game</code>.
     */
    public final Game GAME = com.gmo.big2.store.mysql.schema.jooq.tables.Game.GAME;

    /**
     * The table <code>big_two.game_player</code>.
     */
    public final GamePlayer GAME_PLAYER = com.gmo.big2.store.mysql.schema.jooq.tables.GamePlayer.GAME_PLAYER;

    /**
     * The table <code>big_two.game_player_group</code>.
     */
    public final GamePlayerGroup GAME_PLAYER_GROUP = com.gmo.big2.store.mysql.schema.jooq.tables.GamePlayerGroup.GAME_PLAYER_GROUP;

    /**
     * The table <code>big_two.player</code>.
     */
    public final Player PLAYER = com.gmo.big2.store.mysql.schema.jooq.tables.Player.PLAYER;

    /**
     * The table <code>big_two.player_group</code>.
     */
    public final PlayerGroup PLAYER_GROUP = com.gmo.big2.store.mysql.schema.jooq.tables.PlayerGroup.PLAYER_GROUP;

    /**
     * The table <code>big_two.player_score</code>.
     */
    public final PlayerScore PLAYER_SCORE = com.gmo.big2.store.mysql.schema.jooq.tables.PlayerScore.PLAYER_SCORE;

    /**
     * No further instances allowed
     */
    private BigTwo() {
        super("big_two", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Game.GAME,
            GamePlayer.GAME_PLAYER,
            GamePlayerGroup.GAME_PLAYER_GROUP,
            Player.PLAYER,
            PlayerGroup.PLAYER_GROUP,
            PlayerScore.PLAYER_SCORE);
    }
}
