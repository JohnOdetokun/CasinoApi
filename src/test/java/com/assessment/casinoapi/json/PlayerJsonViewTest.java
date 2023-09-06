package com.assessment.casinoapi.json;

import com.assessment.casinoapi.model.Player;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class PlayerJsonViewTest {
    private ObjectMapper mapper;

    private Player player;

    @BeforeEach
    public void setup() {
        mapper = JsonMapper.builder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();
        player = new Player(1222453, "test", 500.55, 0L);
    }

    @Test
    public void BalanceViewTest() throws Exception {
        final String result = mapper
                .writerWithView(Player.Views.BalanceView.class)
                .writeValueAsString(player);

        assertThat(result, not(containsString(player.getUsername())));
        assertThat(result, containsString(String.valueOf(player.getPlayerId())));
        assertThat(result, containsString(String.valueOf(player.getBalance())));
    }
}
