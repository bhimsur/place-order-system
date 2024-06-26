package io.bhimsur.posservice.repository;

import io.bhimsur.posservice.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<T extends Audit, ID extends Serializable>
		extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}