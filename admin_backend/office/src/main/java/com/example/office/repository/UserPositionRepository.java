package com.example.office.repository;

import com.example.office.model.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {
    List<UserPosition> findByRoomId(Long roomId);
    Optional<UserPosition> findByUserIdAndRoomId(Long userId, Long roomId);
}