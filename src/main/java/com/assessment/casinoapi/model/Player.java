package com.assessment.casinoapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Version;

@Entity
@Table(name = "player", indexes = {
        @Index(name = "un_index", columnList = "username", unique = true)
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
public class Player {
    @Id
    @GeneratedValue
    @JsonView(Views.BalanceView.class)
    private Integer playerId;
    @JsonView(Views.HistoryView.class)
    @Size(min = 1, max = 50, message = "{validation.name.size.invalid}")
    private String username;
    @JsonView(Views.BalanceView.class)
    private Double balance;
    @Version
    @JsonIgnore
    private Long version;

    public static class Views {
        public interface BalanceView {
        }

        public interface HistoryView {
        }
    }
}
