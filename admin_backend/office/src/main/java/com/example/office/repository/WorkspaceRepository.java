package com.example.office.repository;

import com.example.office.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    
    @Query("SELECT w FROM Workspace w WHERE w.createdBy.id = ?1 OR w.id IN " +
           "(SELECT wm.workspace.id FROM WorkspaceMember wm WHERE wm.user.id = ?1)")
    List<Workspace> findByCreatedByIdOrMembersUserId(Long userId);
}