package com.partyst.app.partystapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tasks", schema = "partyst_plastic")
public class Task {

    @Id
    @GeneratedValue
    @Column(name = "task_id")
    private Integer taskId;

    private String name;
    private String state;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignedUser;


}

