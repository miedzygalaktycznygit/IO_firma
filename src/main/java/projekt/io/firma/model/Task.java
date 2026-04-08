package projekt.io.firma.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;

    @ManyToOne
    @JoinColumn(name = "designer_id")
    private Employee designer;

    @ManyToOne
    @JoinColumn(name = "tailor_id")
    private Employee tailor;

    private String decisionReason;
    private LocalDateTime decisionDate;
}