package com.example.office.repository;

import com.example.office.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByWorkspaceId(Long workspaceId);
    List<Meeting> findByRoomId(Long roomId);
}