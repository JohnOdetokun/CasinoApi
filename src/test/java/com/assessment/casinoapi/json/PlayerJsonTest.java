package com.assessment.casinoapi.json;

import com.assessment.casinoapi.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class PlayerJsonTest {
    @Autowired
    private JacksonTester<Player> json;

    private Player player;

    @BeforeEach
    public void setup() {
        player = new Player(1222453, "test", 500.55, 0L);
    }

    @Test
    public void playerBalanceSerializeTest() throws Exception {
        assertThat(json.write(player)).isStrictlyEqualToJson("player_example.json");
    }

    @Test
    public void playerBalanceDeserializeTest() throws Exception {
        final String expected = """
                {
                  "playerId": 1222453,
                  "username": "test",
                  "balance": 500.55
                }
                """;
        assertThat(json.parseObject(expected).getPlayerId()).isEqualTo(1222453);
        assertThat(json.parseObject(expected).getUsername()).isEqualTo("test");
        assertThat(json.parseObject(expected).getBalance()).isEqualTo(500.55);
    }
}
