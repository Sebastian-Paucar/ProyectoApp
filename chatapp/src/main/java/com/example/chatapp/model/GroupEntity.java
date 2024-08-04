package com.example.chatapp.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_group;
    private String name_group;
    private String room_group;

    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id_group"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    )
    private Set<Users> members = new HashSet<>();

    // Getters and setters
    public Long getId_group() {
        return id_group;
    }

    public void setId_group(Long id_group) {
        this.id_group = id_group;
    }

    public String getName_group() {
        return name_group;
    }

    public void setName_group(String name_group) {
        this.name_group = name_group;
    }

    public String getRoom_group() {
        return room_group;
    }

    public void setRoom_group(String room_group) {
        this.room_group = room_group;
    }

    public Set<Users> getMembers() {
        return members;
    }

    public void setMembers(Set<Users> members) {
        this.members = members;
    }
}
