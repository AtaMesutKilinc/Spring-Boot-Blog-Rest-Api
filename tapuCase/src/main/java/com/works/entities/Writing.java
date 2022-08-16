package com.works.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Writing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String title;

    private String text;







    @ManyToOne
    @JoinColumn(name="author_id",referencedColumnName = "id")
    private Author author;

}
