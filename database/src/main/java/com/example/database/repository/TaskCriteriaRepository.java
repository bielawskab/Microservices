package com.example.database.repository;

import com.example.database.model.Task;
import com.example.database.model.TaskSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Repository
public class TaskCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public TaskCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Task> findAllWithFilters(TaskSearchCriteria taskSearchCriteria, Pageable pageable) {
        CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
        Root<Task> taskRoot = criteriaQuery.from(Task.class);
        List<Predicate> predicates = new ArrayList<>();

        if(Objects.nonNull(taskSearchCriteria.getDescription())){
            predicates.add(
                    criteriaBuilder.like(taskRoot.get("description"), "%" + taskSearchCriteria.getDescription() + "%")
            );
        }
        if(Objects.nonNull(taskSearchCriteria.isDone())) {
            predicates.add(
                    criteriaBuilder.equal(taskRoot.get("done"), taskSearchCriteria.isDone())
            );
        }
        if(Objects.nonNull(taskSearchCriteria.getDeadlineFrom())) {
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(taskRoot.<Instant>get("deadline"), taskSearchCriteria.getDeadlineFrom())
            );
        }
        if(Objects.nonNull(taskSearchCriteria.getDeadlineTo())) {
            predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(taskRoot.<Instant>get("deadline"), taskSearchCriteria.getDeadlineTo())
            );
        }
        if(Objects.nonNull(taskSearchCriteria.getDeadlineFrom()) && Objects.nonNull(taskSearchCriteria.getDeadlineTo())) {
            predicates.add(
                    criteriaBuilder.between(taskRoot.<Instant>get("deadline"), taskSearchCriteria.getDeadlineFrom(), taskSearchCriteria.getDeadlineTo())
            );
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        criteriaQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), taskRoot, criteriaBuilder));
        
        List<Task> result = entityManager.createQuery(criteriaQuery).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();
        
        
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Task> taskRootCount = countQuery.from(Task.class);
        countQuery.select(criteriaBuilder.count(taskRootCount)).where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        
        
        Long count = entityManager.createQuery(countQuery).getSingleResult();
        TypedQuery<Task> typedQuery = entityManager.createQuery(criteriaQuery);

        return new PageImpl<>(result, pageable, count);
    }
}

