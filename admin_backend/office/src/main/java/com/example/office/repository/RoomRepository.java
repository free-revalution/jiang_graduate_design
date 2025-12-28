package com.example.office.repository;

import com.example.office.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByWorkspaceId(Long workspaceId);
    List<Room> findByWorkspaceIdOrderByNameAsc(Long workspaceId);
}