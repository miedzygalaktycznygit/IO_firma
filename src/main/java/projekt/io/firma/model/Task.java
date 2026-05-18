package projekt.io.firma.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import projekt.io.firma.model.state.*;

@Entity
@Data
@Table(name = "tasks")
public class Task implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(name = "task_description", length = 2000)
    private String description;
    private String status;

    @ManyToOne
    @JoinColumn(name = "designer_id")
    private Employee designer;

    @ManyToOne
    @JoinColumn(name = "tailor_id")
    private Employee tailor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "produkt_id")
    private projekt.io.firma.model.builder.Produkt produkt;

    @Transient
    @EqualsAndHashCode.Exclude
    private TaskState state;

    @PostLoad
    public void init() {
        updateState();
    }

    public void updateState() {
        if ("NOWE".equals(status)) this.state = new NewState();
        else if ("W_REALIZACJI".equals(status)) this.state = new InProgressState();
        else if ("ZAKONCZONE".equals(status)) this.state = new CompletedState();
        else {
            this.status = "NOWE";
            this.state = new NewState();
        }
    }

    public void accept() {
        if (state == null) updateState();
        state.accept(this);
    }
    
    public void complete() {
        if (state == null) updateState();
        state.complete(this);
    }

    public void changeState(TaskState newState) {
        this.state = newState;
        this.status = newState.getStatusName();
    }

    @Override
    public Task clone() {
        try {
            Task clone = (Task) super.clone();
            clone.setId(null);
            clone.setTitle(this.title + " (Kopia)");
            clone.setStatus("NOWE");
            clone.setState(new NewState());
            
            if (this.produkt != null) {
                clone.setProdukt(this.produkt.clone());
            }
            
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}