package com.example.chatapp.repositopry;

import com.example.chatapp.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
}