package com.assessment.casinoapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@Builder
@Table(name = "transaction", indexes = {
        @Index(name = "pi_index", columnList = "playerId")
})
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Transaction {
    @Id
    @GeneratedValue
    @JsonView({Views.UpdateResponseView.class, Views.HistoryView.class})
    private Long transactionId;
    private Integer playerId;
    @JsonView({Views.HistoryView.class, Views.UpdateRequestView.class})
    @Min(0)
    private Double amount;
    @JsonView(Views.UpdateResponseView.class)
    @JsonProperty("balance")
    private Double runningBalance;
    @JsonView({Views.HistoryView.class, Views.UpdateRequestView.class})
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @CreationTimestamp
    private Instant transactionDateTime;


    public static class Views {
        public interface UpdateRequestView {
        }

        public interface UpdateResponseView {
        }

        public interface HistoryView {
        }
    }
}
