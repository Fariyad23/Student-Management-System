package com.fariyad.an.St_management.repository;

import com.fariyad.an.St_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    // Yahan kuch bhi likhne ki zaroorat nahi hai.
    // Integer ka matlab hai aapki Student entity ki Primary Key (ID) ka type.
}